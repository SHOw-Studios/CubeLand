package io.show.graphics.internal.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;

public class GLBuffer implements AutoCloseable {

    private final int m_Handle;
    private int m_Target;
    private int m_Usage;

    public GLBuffer() {
        m_Handle = glGenBuffers();
    }

    public GLBuffer bind() {
        glBindBuffer(m_Target, m_Handle);
        return this;
    }

    public GLBuffer bind(int i) {
        glBindBufferBase(m_Target, i, m_Handle);
        return this;
    }

    public GLBuffer unbind() {
        glBindBuffer(m_Target, 0);
        return this;
    }

    public GLBuffer unbind(int i) {
        glBindBufferBase(m_Target, i, 0);
        return this;
    }

    public GLBuffer setTarget(int target) {
        m_Target = target;
        return this;
    }

    public GLBuffer setUsage(int usage) {
        m_Usage = usage;
        return this;
    }

    public GLBuffer setSize(int size) {
        glBufferData(m_Target, size, m_Usage);
        return this;
    }

    public GLBuffer setData(ByteBuffer buffer) {
        glBufferData(m_Target, buffer, m_Usage);
        return this;
    }

    public int getSize() {
        return glGetBufferParameteri(m_Target, GL_BUFFER_SIZE);
    }

    @Override
    public void close() {
        glDeleteBuffers(m_Handle);
    }
}
