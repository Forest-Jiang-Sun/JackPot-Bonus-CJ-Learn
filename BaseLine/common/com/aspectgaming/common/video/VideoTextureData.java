package com.aspectgaming.common.video;

import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author ligang.yao
 */
public class VideoTextureData implements TextureData {

    private final Video video;
    private final boolean isPBOEnabled;
    private final int width;
    private final int height;
    private final int textureDataSize;

    private int target;

    public VideoTextureData(Video video, boolean usePbo) {
        this.video = video;
        isPBOEnabled = usePbo;

        // significantly improved the texture uploading speed in some machines after change to power of two
        width = MathUtils.nextPowerOfTwo(video.width);
        height = MathUtils.nextPowerOfTwo(video.height);

        textureDataSize = width * height * 4;
    }

    @Override
    public boolean isPrepared() {
        return true;
    }

    @Override
    public void prepare() {}

    @Override
    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not support consumePixmap");
    }

    @Override
    public void consumeCustomData(int target) {
        this.target = target;

        // free memory instantly instead of waiting for GC if using ByteBuffer.allocateDirect();
        ByteBuffer data = VideoCodec.malloc_frame(textureDataSize);

        // according to OpenGL official documents, optimal internal format is GL_RGBA8
        // even the video has no alpha channel, it is still better to use RGBA
        glTexImage2D(target, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        VideoCodec.free_frame(data);

        if (isPBOEnabled) {
            initPbo(video.frameRead);
            initPbo(video.frameCopy);

            glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);
        } else {
            video.frameRead.setData(VideoCodec.malloc_frame(textureDataSize));
            video.frameCopy.setData(VideoCodec.malloc_frame(textureDataSize));
        }
    }

    @Override
    public boolean disposePixmap() {
        if (isPBOEnabled) {
            if (video.frameRead.getHandle() != 0) glDeleteBuffersARB(video.frameRead.getHandle());
            if (video.frameCopy.getHandle() != 0) glDeleteBuffersARB(video.frameCopy.getHandle());
        } else {
            if (video.frameRead.getData() != null) VideoCodec.free_frame(video.frameRead.getData());
            if (video.frameCopy.getData() != null) VideoCodec.free_frame(video.frameCopy.getData());
        }
        return true;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Format getFormat() {
        return Format.RGBA8888;
    }

    @Override
    public boolean useMipMaps() {
        return false;
    }

    @Override
    public boolean isManaged() {
        return false;
    }

    @Override
    public TextureDataType getType() {
        return TextureDataType.Custom;
    }

    void startRead() {
        VideoFrame frame = video.frameRead;
        if (frame.getState() == VideoFrame.IDLE) {
            frame.setState(VideoFrame.READING);
            if (isPBOEnabled) {
                glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, frame.getHandle());
                // glBufferDataARB(GL_PIXEL_UNPACK_BUFFER_ARB, frameSize, GL_STREAM_DRAW_ARB);
                frame.setData(glMapBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, GL_WRITE_ONLY_ARB, frame.getData()));
                glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);
            }
            VideoThread.getInstance().startRead(video);
        }
    }

    void updateTexture() {
        VideoFrame frame = video.frameCopy;

        if (frame.getState() == VideoFrame.READ_OK) {
            frame.setState(VideoFrame.UPLOADING);
            long timeStart = System.nanoTime();

            if (isPBOEnabled) {
                glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, frame.getHandle());
                glUnmapBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB);
                glTexSubImage2D(target, 0, 0, 0, video.width, video.height, GL_RGBA, GL_UNSIGNED_BYTE, 0);
                glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);
            } else {
                Gdx.gl.glTexSubImage2D(target, 0, 0, 0, video.width, video.height, GL_RGBA, GL_UNSIGNED_BYTE, frame.getData());
            }

            video.recordFrameCopyTime(System.nanoTime() - timeStart);
            frame.setState(VideoFrame.IDLE);
        }
    }

    private int createPBOHandle() {
        IntBuffer buffer = BufferUtils.newIntBuffer(1);
        glGenBuffersARB(buffer);
        return buffer.get(0);
    }

    private void initPbo(VideoFrame frame) {
        int handle = createPBOHandle();

        glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, handle);
        glBufferDataARB(GL_PIXEL_UNPACK_BUFFER_ARB, textureDataSize, GL_STREAM_DRAW_ARB);
        glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);

        frame.setHandle(handle);
    }
}
