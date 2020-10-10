package com.aspectgaming.common.video;

import java.nio.ByteBuffer;

import com.aspectgaming.common.loader.LibraryLoader;

/**
 * @author ligang.yao
 */
public class VideoCodec {

    static final int FORMAT_RGB = 0;
    static final int FORMAT_RGBA = 1;

    static final int OK = 0;

    static final int ERR_OPEN_FILE = -2;
    static final int ERR_NO_STREAM_INFORMATION = -3;
    static final int ERR_NO_VIDEO_STREAM = -4;
    static final int ERR_UNSUPPORTED_CODEC = -5;
    static final int ERR_OPEN_CODEC = -6;
    static final int ERR_NO_MEMORY = -7;
    static final int ERR_UNSUPPORTED_PIXEL_FORMAT = -8;
    static final int ERR_DECODE_FRAME = -9;
    static final int ERR_FRAME_INDEX_OUT_OF_RANGE = -13;
    static final int ERR_INTERNAL = -100;

    static native int open(Video video);
    static native int close(long handle);
    static native int reset(long handle);
    public static native long get_duration(String file); // get video or sound duration in ms

    static native ByteBuffer malloc_frame(int size);
    static native int free_frame(ByteBuffer buffer);
    public static native long seek_frame(long handle, long frameIndex);
    static native long read_next_frame(long handle, ByteBuffer frame);
    static native long read_frame(long handle, long frameIndex, ByteBuffer frame);

    static {
        LibraryLoader.load("avutil-52");
        LibraryLoader.load("swscale-2");
        LibraryLoader.load("avcodec-55");
        LibraryLoader.load("avformat-55");

        LibraryLoader.load("VideoCodec");
    }
}
