/*
 * video_codec.h
 *
 *  Created on: Jul 31, 2013
 *      Author: ligang.yao
 */

#ifndef VIDEO_CODEC_H_
#define VIDEO_CODEC_H_

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libavutil/avstring.h>
#include <stdio.h>
#include <jni.h>

#define FORMAT_RGB (0L)
#define FORMAT_RGBA (1L)

#define RET_OK (0L)
#define ERR_OPEN_FILE (-2L)
#define ERR_NO_STREAM_INFORMATION (-3L)
#define ERR_NO_VIDEO_STREAM (-4L)
#define ERR_UNSUPPORTED_CODEC (-5L)
#define ERR_OPEN_CODEC (-6L)
#define ERR_NO_MEMORY (-7L)
#define ERR_UNSUPPORTED_PIXEL_FORMAT (-8L)
#define ERR_DECODE_FRAME (-9L)
#define ERR_FRAME_INDEX_OUT_OF_RANGE (-13L)
#define ERR_INTERNAL (-100L)

jint JNICALL video_open(JNIEnv *env, jobject obj, jobject video);
jint JNICALL video_close(JNIEnv *env, jobject obj, jlong handle);
jint JNICALL video_reset(JNIEnv *env, jobject obj, jlong handle);
jlong JNICALL video_get_duration(JNIEnv *env, jobject obj, jstring path);

jobject JNICALL video_malloc(JNIEnv *env, jobject obj, jint size);
jint JNICALL video_free(JNIEnv *env, jobject obj, jobject buffer);
jlong JNICALL video_seek_frame(JNIEnv *env, jobject obj, jlong handle, jlong frame_idx);
jlong JNICALL video_read_next_frame(JNIEnv *env, jobject obj, jlong handle, jobject buffer);
jlong JNICALL video_read_frame(JNIEnv *env, jobject obj, jlong handle, jlong frame_idx, jobject buffer);

#endif /* VIDEO_CODEC_H_ */
