package com.aspectgaming.common.video;

import java.nio.ByteBuffer;

/**
 * @author ligang.yao
 */
class VideoFrame {
    static final int IDLE = 0;
    static final int READING = 1;
    static final int READ_OK = 2;
    static final int UPLOADING = 3;

    private long serial = -1;
    private int handle = 0;
    private ByteBuffer data;
    private int state = IDLE;

    public int getHandle() {
        return handle;
    }

    public void setHandle(int val) {
        handle = val;
    }

    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer val) {
        data = val;
    }

    public long getSerial() {
        return serial;
    }

    public void setSerial(long val) {
        serial = val;
    }

    public int getState() {
        return state;
    }

    public void setState(int val) {
        state = val;
    }
}
