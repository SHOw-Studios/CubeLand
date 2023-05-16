package io.show.graphics.internal.gl;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

/**
 * @author Felix Schreiber
 */
public class Texture implements AutoCloseable {

    private final int m_Handle;

    public Texture() {
        int[] handle = new int[1];
        glGenTextures(handle);
        m_Handle = handle[0];

        bind();
        setDefaultParameters();
        unbind();
    }

    public int getHandle() {
        return m_Handle;
    }

    public Texture bind() {
        glBindTexture(GL_TEXTURE_2D, m_Handle);
        return this;
    }

    public Texture bind(int i) {
        glActiveTexture(i);
        glBindTexture(GL_TEXTURE_2D, m_Handle);
        return this;
    }

    public Texture unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }

    public Texture unbind(int i) {
        glActiveTexture(i);
        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }

    public Texture setDefaultParameters() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        return this;
    }

    public Texture setData(int width, int height, @Nullable ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        return this;
    }

    public Texture setData(int x, int y, int width, int height, ByteBuffer data) {
        glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);
        return this;
    }

    @Override
    public void close() {
        glDeleteTextures(m_Handle);
    }
}
