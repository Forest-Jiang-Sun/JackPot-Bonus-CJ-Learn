/*
 * video_codec.c
 *
 *  Created on: Jul 18, 2013
 *      Author: ligang.yao
 */

#include "video_codec.h"

#ifdef __cplusplus
extern "C" {
#endif

static const char* const clazzName = "com/aspectgaming/common/video/VideoCodec";
static JNINativeMethod gMethods[] = {
        { "open", "(Lcom/aspectgaming/common/video/Video;)I", (void*) video_open },
        { "close", "(J)I", (void*) video_close },
        { "reset", "(J)I", (void*) video_reset },
        { "get_duration", "(Ljava/lang/String;)J", (void*) video_get_duration },

        { "malloc_frame", "(I)Ljava/nio/ByteBuffer;", (void*) video_malloc },
        { "free_frame", "(Ljava/nio/ByteBuffer;)I", (void*) video_free },
        { "seek_frame", "(JJ)J", (void*) video_seek_frame },
        { "read_next_frame", "(JLjava/nio/ByteBuffer;)J", (void*) video_read_next_frame },
        { "read_frame", "(JJLjava/nio/ByteBuffer;)J", (void*) video_read_frame },

};

typedef struct MyAVPacketList {
    AVPacket pkt;
    struct MyAVPacketList *next;
} MyAVPacketList;

typedef struct PacketQueue {
    MyAVPacketList *first_pkt, *last_pkt, *current_pkt;
    int nb_packets;
    int size;
} PacketQueue;

typedef struct AVContext {
    jint error;
    int32_t w, h;
    int32_t video_stream;
    int32_t alpha_stream;
    double frame_rate;
    int64_t total_frames;
    char filename[256];
    int64_t serial;

    AVFormatContext *format;
    AVCodecContext *video_codec;
    AVCodecContext *alpha_codec;
    struct SwsContext *sws;
    AVFrame *video_frame;
    AVFrame *alpha_frame;
    AVFrame *picture;

    jboolean seamless_loop;
    jboolean caching;
    PacketQueue queue;

    uint8_t eof;
} AVContext;

/*****************************************************************************
 * packets operations
 *****************************************************************************/

static void packet_queue_init(PacketQueue *q) {
    memset(q, 0, sizeof(PacketQueue));
}

static void packet_queue_free(PacketQueue *q) {
    MyAVPacketList *pkt1 = q->first_pkt;
    while (pkt1) {
        MyAVPacketList *pkt2 = pkt1->next;
        av_free_packet(&pkt1->pkt);
        av_free(pkt1);
        pkt1 = pkt2;
    }
    memset(q, 0, sizeof(PacketQueue));
}

static int packet_queue_put(PacketQueue *q, AVPacket *pkt) {
    MyAVPacketList *pkt1;

    // make av_malloc()ed copy of packet data to avoid automatically data release
    if (av_dup_packet(pkt) < 0)
        return -1;

    pkt1 = av_malloc(sizeof(MyAVPacketList));
    if (!pkt1)
        return -1;

    pkt1->pkt = *pkt;
    pkt1->next = NULL;

    if (!q->last_pkt) {
        q->first_pkt = pkt1;
    } else {
        q->last_pkt->next = pkt1;
    }

    q->last_pkt = pkt1;
    q->nb_packets++;
    q->size += pkt1->pkt.size + sizeof(*pkt1);
    return 0;
}

/**
 * return 0 if OK, < 0 on empty or end of file
 */
static int packet_queue_next(PacketQueue *q, AVPacket *pkt) {
    MyAVPacketList *pkt1;

    if (!q->first_pkt) {
        return -1;
    }

    if (!q->current_pkt) {
        pkt1 = q->first_pkt;
    } else {
        pkt1 = q->current_pkt->next;
    }

    q->current_pkt = pkt1;

    if (!pkt1) {
        return -1;
    }

    *pkt = pkt1->pkt;

    return 0;
}

static void packet_queue_restart_iterate(PacketQueue *q) {
    q->current_pkt = NULL;
}

/*****************************************************************************
 * video operations
 *****************************************************************************/

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    jclass clazz;

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    clazz = (*env)->FindClass(env, clazzName);
    if (clazz == NULL) {
        return JNI_ERR;
    }

    if ((*env)->RegisterNatives(env, clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK) {
        return JNI_ERR;
    }

    av_register_all();

    return JNI_VERSION_1_6;
}

void detect_streams(AVContext* ctx) {
    ctx->video_stream = -1;
    ctx->alpha_stream = -1;
    int i = 0;
    for (i = 0; i < ctx->format->nb_streams; i++) {
        AVStream* stream = ctx->format->streams[i];
        if (stream->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            if (ctx->video_stream == -1) {
                // first video stream is rgb video
                ctx->video_stream = i;
                ctx->video_codec = stream->codec;
                ctx->frame_rate = av_q2d(stream->r_frame_rate);
                ctx->total_frames = stream->nb_frames;
                ctx->w = ctx->video_codec->width;
                ctx->h = ctx->video_codec->height;
            } else {
                // second video stream is alpha video
                ctx->alpha_stream = i;
                ctx->alpha_codec = stream->codec;
                break;
            }
        }
    }
}

void read_all_video_packages(AVContext *ctx) {
    AVPacket pkt1;
    AVPacket *pkt = &pkt1;

    packet_queue_init(&ctx->queue);

    while (1) {
        av_init_packet(pkt);
        pkt->data = NULL;
        pkt->size = 0;

        if (av_read_frame(ctx->format, pkt) < 0) { // end of file
            return;
        }
        packet_queue_put(&ctx->queue, pkt);
    }
}

AVContext* open(const char *fileName, jboolean caching) {
    // allocate memory and fill with zero
    AVContext *ctx = (AVContext*) calloc(sizeof(AVContext), 1);
    AVCodec *pCodec = NULL;

    av_strlcpy(ctx->filename, fileName, sizeof(ctx->filename));

    ctx->caching = caching;

    // allocate source video frame
    ctx->video_frame = av_frame_alloc();
    if (ctx->video_frame == NULL) {
        ctx->error = ERR_NO_MEMORY;
        return ctx;
    }

    ctx->alpha_frame = av_frame_alloc();
    if (ctx->alpha_frame == NULL) {
        ctx->error = ERR_NO_MEMORY;
        return ctx;
    }

    // allocate rgb video frame
    ctx->picture = av_frame_alloc();
    if (ctx->picture == NULL) {
        ctx->error = ERR_NO_MEMORY;
        return ctx;
    }

    // Open file
    if (avformat_open_input(&ctx->format, ctx->filename, NULL, NULL) != 0) {
        ctx->error = ERR_OPEN_FILE;
        return ctx;
    }

    // Some formats do not have a header or do not store enough information,
    // avformat_find_stream_info() will read and decode a few frames to find missing information.
    // ctx->format->max_analyze_duration = 50000; // analyze less time to speed up avformat_find_stream_info
    if (avformat_find_stream_info(ctx->format, NULL) < 0) {
        ctx->error = ERR_NO_STREAM_INFORMATION;
        return ctx;
    }

    // Dump information about file
    // av_dump_format(ctx->format, 0, ctx->filename, 0);

    // Find the first video stream
    detect_streams(ctx);

    if (ctx->video_stream == -1) {
        ctx->error = ERR_NO_VIDEO_STREAM;
        return ctx;
    }

    // Find the decoder for the video stream
    pCodec = avcodec_find_decoder(ctx->video_codec->codec_id);
    if (pCodec == NULL) {
        ctx->error = ERR_UNSUPPORTED_CODEC;
        return ctx;
    }
    // Open codec
    AVDictionary *optionsDict = NULL;
    if (avcodec_open2(ctx->video_codec, pCodec, &optionsDict) < 0) {
        ctx->error = ERR_OPEN_CODEC;
        return ctx;
    }

    if (ctx->alpha_stream != -1) {
        // Find the decoder for the alpha stream
        pCodec = avcodec_find_decoder(ctx->alpha_codec->codec_id);
        if (pCodec == NULL) {
            ctx->error = ERR_UNSUPPORTED_CODEC;
            return ctx;
        }
        // Open alpha codec
        optionsDict = NULL;
        if (avcodec_open2(ctx->alpha_codec, pCodec, &optionsDict) < 0) {
            ctx->error = ERR_OPEN_CODEC;
            return ctx;
        }
    }

    // create color space converter
    ctx->sws = sws_getCachedContext(ctx->sws, ctx->w, ctx->h, ctx->video_codec->pix_fmt, ctx->w, ctx->h, AV_PIX_FMT_RGBA, SWS_FAST_BILINEAR, NULL, NULL, NULL);

    if (ctx->sws == NULL) {
        ctx->error = ERR_UNSUPPORTED_PIXEL_FORMAT;
        return ctx;
    }

    if (ctx->caching) {
        read_all_video_packages(ctx);
    }

    return ctx;
}

int close(AVContext* ctx) {
    if (ctx) {
        if (ctx->caching) {
            packet_queue_free(&ctx->queue);
        }
        if (ctx->sws) {
            sws_freeContext(ctx->sws);
            ctx->sws = NULL;
        }
        if (ctx->alpha_codec) {
            avcodec_close(ctx->alpha_codec); // close codec
            ctx->alpha_codec = NULL;
        }
        if (ctx->video_codec) {
            avcodec_close(ctx->video_codec); // close codec
            ctx->video_codec = NULL;
        }
        if (ctx->format) {
            avformat_close_input(&ctx->format); // close video file
        }
        if (ctx->picture) {
            av_frame_free(&ctx->picture); // Free rgb frame
        }
        if (ctx->alpha_frame) {
            av_frame_free(&ctx->alpha_frame); // Free alpha frame
        }
        if (ctx->video_frame) {
            av_frame_free(&ctx->video_frame); // Free video frame
        }

        free(ctx);
    }
    return 0;
}

void convert_color_space(AVContext *ctx, uint8_t *data) {
    // speed up color format conversion if using preferred format
	if (ctx->video_codec->pix_fmt == AV_PIX_FMT_GBRP) {
		int line;
		uint8_t *des = data;
		uint8_t *g = ctx->video_frame->data[0];
		uint8_t *b = ctx->video_frame->data[1];
		uint8_t *r = ctx->video_frame->data[2];
		int linesize = ctx->video_frame->linesize[0];

		if (ctx->alpha_stream != -1) {
			uint8_t *a = ctx->alpha_frame->data[0];
			int linesize_a = ctx->alpha_frame->linesize[0];

			for (line = 0; line < ctx->h; line++) {
				uint8_t *rsrc = r;
				uint8_t *gsrc = g;
				uint8_t *bsrc = b;
				uint8_t *asrc = a;
				int width = ctx->w;

				while (width--) {
					*des++ = *rsrc++;
					*des++ = *gsrc++;
					*des++ = *bsrc++;
					*des++ = *asrc++;
				}

				r += linesize;
				g += linesize;
				b += linesize;
				a += linesize_a;
			}
			av_frame_unref(ctx->alpha_frame);
		} else {
			for (line = 0; line < ctx->h; line++) {
				uint8_t *rsrc = r;
				uint8_t *gsrc = g;
				uint8_t *bsrc = b;
				int width = ctx->w;

				while (width--) {
					*des++ = *rsrc++;
					*des++ = *gsrc++;
					*des++ = *bsrc++;
					*des++ = 0xff;
				}

				r += linesize;
				g += linesize;
				b += linesize;
			}
		}
    } else {
        // assign buffer to image planes
        avpicture_fill((AVPicture *) ctx->picture, data, AV_PIX_FMT_RGBA, ctx->w, ctx->h);

        // convert pixel format to RGB
        sws_scale(ctx->sws, (uint8_t const * const *) ctx->video_frame->data, ctx->video_frame->linesize, 0, ctx->h, ctx->picture->data,
                ctx->picture->linesize);
        av_frame_unref(ctx->picture); // TODO: need to check whether this will cause issue
    }
    av_frame_unref(ctx->video_frame);
}

jlong read_next_frame(AVContext *ctx) {
    int got_rgb = 0;
    int got_alpha = 0;
    AVPacket pkt;

    if (ctx->alpha_stream == -1) {
        got_alpha = 1;
    }

    while (!got_rgb || !got_alpha) {
        if (!ctx->eof) {
            int ret;

            av_init_packet(&pkt);

            if (ctx->caching) {
                ret = packet_queue_next(&ctx->queue, &pkt);
            } else {
                ret = av_read_frame(ctx->format, &pkt);
            }

            if (ret >= 0) {
                if (!got_rgb && pkt.stream_index == ctx->video_stream) {
                    if (avcodec_decode_video2(ctx->video_codec, ctx->video_frame, &got_rgb, &pkt) < 0) {
                        return -9L;
                    }
                    if (got_rgb) {
                        ctx->serial = ctx->serial % ctx->total_frames + 1;
                    }
                } else if (!got_alpha && pkt.stream_index == ctx->alpha_stream) {
                    if (avcodec_decode_video2(ctx->alpha_codec, ctx->alpha_frame, &got_alpha, &pkt) < 0) {
                        return -10L;
                    }
                }

                if (!ctx->caching) {
                	av_free_packet(&pkt);
                }
            } else {
                if (ctx->caching) {
                    packet_queue_restart_iterate(&ctx->queue);
                } else {
                    if (avformat_seek_file(ctx->format, -1, 0, 1, 1, AVSEEK_FLAG_FRAME) < 0) {
                        return ERR_INTERNAL;
                    }
                }
                if (!ctx->seamless_loop) {
                    ctx->eof = 1;
                }
            }
        } else {
            if (!got_rgb) {
                av_init_packet(&pkt);
                pkt.data = NULL;
                pkt.size = 0;
                if (avcodec_decode_video2(ctx->video_codec, ctx->video_frame, &got_rgb, &pkt) < 0) {
                    return -11L;
                }
                if (got_rgb) {
                    ctx->serial = ctx->serial % ctx->total_frames + 1;
                }
            }

            if (!got_alpha) {
                av_init_packet(&pkt);
                pkt.data = NULL;
                pkt.size = 0;
                if (avcodec_decode_video2(ctx->alpha_codec, ctx->alpha_frame, &got_alpha, &pkt) < 0) {
                    return -12L;
                }
            }

            if (ctx->serial >= ctx->total_frames) {
                ctx->eof = 0;

                avcodec_flush_buffers(ctx->video_codec);
                if (ctx->alpha_stream != -1) {
                    avcodec_flush_buffers(ctx->alpha_codec);
                }
                // END_OF_FILE;
            }
        }
    }

    return ctx->serial;
}

jlong seek(AVContext *ctx, jlong frame) {
    if (frame < 1 || frame > ctx->total_frames) {
        return ERR_FRAME_INDEX_OUT_OF_RANGE;
    }

    frame = frame - 1;
    ctx->serial %= ctx->total_frames;

    if (frame == ctx->serial) return frame;

    if (frame < ctx->serial) {
        jlong num_frames_next = frame + ctx->total_frames - ctx->serial;
        if (!ctx->seamless_loop || (num_frames_next > 5 && frame < 10) || (num_frames_next > frame)) {
            if (ctx->caching) {
                packet_queue_restart_iterate(&ctx->queue);
            } else {
                if (avformat_seek_file(ctx->format, -1, 0, 1, 1, AVSEEK_FLAG_FRAME) < 0) {
                    return ERR_INTERNAL;
                }
            }

            ctx->eof = 0;
            ctx->serial = 0;

            avcodec_flush_buffers(ctx->video_codec);
            if (ctx->alpha_stream != -1) {
                avcodec_flush_buffers(ctx->alpha_codec);
            }
        }
    }

    while (ctx->serial != frame) {
        read_next_frame(ctx);
        ctx->serial %= ctx->total_frames;
    }

    return frame;
//    int ret = avformat_seek_file(ctx->format, -1, frame, frame, frame, AVSEEK_FLAG_FRAME);
//    int ret =av_seek_frame(ctx->format, -1, seekTime, AVSEEK_FLAG_BACKWARD | AVSEEK_FLAG_ANY);
}

long getDuration(const char *fileName) {
    long duration = 0;
    AVFormatContext *format = NULL;

    // Open file
    if (avformat_open_input(&format, fileName, NULL, NULL) == 0) {
        // Retrieve stream information
        if (avformat_find_stream_info(format, NULL) >= 0) {
            duration = format->duration * 1000 / AV_TIME_BASE;
        }
    }

    if (format) {
        avformat_close_input(&format); // close file
    }
    return duration;
}

jint JNICALL video_open(JNIEnv *env, jclass claz, jobject video) {
    jclass cls;
    jfieldID fid;
    jstring path;
    jboolean caching;
    jboolean seamless_loop;
    const char* filename;

    cls = (*env)->GetObjectClass(env, video);

    fid = (*env)->GetFieldID(env, cls, "path", "Ljava/lang/String;");
    path = (*env)->GetObjectField(env, video, fid);

    fid = (*env)->GetFieldID(env, cls, "isCaching", "Z");
    caching = (*env)->GetBooleanField(env, video, fid);

    fid = (*env)->GetFieldID(env, cls, "isSeamlessLoop", "Z");
    seamless_loop = (*env)->GetBooleanField(env, video, fid);

    filename = (*env)->GetStringUTFChars(env, path, 0);
    AVContext* ctx = open(filename, caching);
    (*env)->ReleaseStringUTFChars(env, path, filename);

    jint error = ctx->error;
    if (error == 0) {
        ctx->seamless_loop = seamless_loop;

        fid = (*env)->GetFieldID(env, cls, "handle", "J");
        (*env)->SetLongField(env, video, fid, (jlong) ctx);

        fid = (*env)->GetFieldID(env, cls, "width", "I");
        (*env)->SetIntField(env, video, fid, ctx->w);

        fid = (*env)->GetFieldID(env, cls, "height", "I");
        (*env)->SetIntField(env, video, fid, ctx->h);

        fid = (*env)->GetFieldID(env, cls, "duration", "J");
        (*env)->SetLongField(env, video, fid, (ctx->format->duration * 1000 / AV_TIME_BASE));

        fid = (*env)->GetFieldID(env, cls, "framesPerSecond", "D");
        (*env)->SetDoubleField(env, video, fid, ctx->frame_rate);

        fid = (*env)->GetFieldID(env, cls, "numberOfFrames", "J");
        (*env)->SetLongField(env, video, fid, ctx->total_frames);

        fid = (*env)->GetFieldID(env, cls, "cacheSize", "J");
        (*env)->SetLongField(env, video, fid, ctx->queue.size);
    } else {
        close(ctx);
    }

    return error;
}

jobject JNICALL video_malloc(JNIEnv *env, jobject obj, jint size) {
    void *data = av_malloc(size);
    return (*env)->NewDirectByteBuffer(env, (void*) data, size);
}

jint JNICALL video_free(JNIEnv *env, jobject obj, jobject buffer) {
    av_free((*env)->GetDirectBufferAddress(env, buffer));
    return RET_OK;
}

jlong JNICALL video_seek_frame(JNIEnv *env, jobject obj, jlong handle, jlong frame) {
    AVContext *ctx = (AVContext *) handle;
    return seek(ctx, frame);
}

jlong JNICALL video_read_next_frame(JNIEnv *env, jobject obj, jlong context, jobject buffer) {
    AVContext *ctx = (AVContext *) context;
    uint8_t *data = (uint8_t *) (*env)->GetDirectBufferAddress(env, buffer);
    jlong frame = read_next_frame(ctx);
    if (frame >= 0) {
        convert_color_space(ctx, data);
    }
    return frame;
}

jlong JNICALL video_read_frame(JNIEnv *env, jobject obj, jlong context, jlong frame_idx, jobject buffer) {
    AVContext *ctx = (AVContext *) context;
    jlong ret = seek(ctx, frame_idx);
    if (ret < 0) return ret;

    uint8_t *data = (uint8_t *) (*env)->GetDirectBufferAddress(env, buffer);
    jlong frame = read_next_frame(ctx);
    if (frame >= 0) {
        convert_color_space(ctx, data);
    }
    return frame;
}

jint JNICALL video_reset(JNIEnv *env, jobject obj, jlong handle) {
    return RET_OK;
}

jint JNICALL video_close(JNIEnv *env, jobject obj, jlong handle) {
    AVContext *ctx = (AVContext *) handle;
    return close(ctx);
}

jlong JNICALL video_get_duration(JNIEnv *env, jobject obj, jstring path) {
    const char* filename = (*env)->GetStringUTFChars(env, path, 0);
    jlong duration = getDuration(filename);
    (*env)->ReleaseStringUTFChars(env, path, filename);
    return duration;
}

#ifdef __cplusplus
}
#endif


