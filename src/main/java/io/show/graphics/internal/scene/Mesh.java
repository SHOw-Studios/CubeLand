package io.show.graphics.internal.scene;

import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.VertexArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL15.*;

public class Mesh implements AutoCloseable {

    protected GLBuffer m_VertexBuffer;
    protected GLBuffer m_IndexBuffer;
    protected VertexArray m_VertexArray;

    public Mesh() {
        m_VertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW);
        m_IndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW);
        m_VertexArray = new VertexArray();
    }

    public Mesh(float[] vertices, int[] indices, VertexArray.Layout layout) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(Float.BYTES * vertices.length).order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(vertices);
        m_VertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        buffer = ByteBuffer.allocateDirect(Integer.BYTES * indices.length).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(indices);
        m_IndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        m_VertexArray = new VertexArray().bind().bindBuffer(m_VertexBuffer, layout).unbind();
    }

    public GLBuffer getVertexBuffer() {
        return m_VertexBuffer;
    }

    public GLBuffer getIndexBuffer() {
        return m_IndexBuffer;
    }

    public VertexArray getVertexArray() {
        return m_VertexArray;
    }

    public Mesh setVertices(float[] vertices) {
        if (m_VertexBuffer == null) m_VertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW);

        ByteBuffer buffer = ByteBuffer.allocateDirect(Float.BYTES * vertices.length).order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(vertices);

        m_VertexBuffer.bind().setData(buffer).unbind();

        return this;
    }

    public Mesh setVertices(ByteBuffer buffer) {
        if (m_VertexBuffer == null) m_VertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW);

        m_VertexBuffer.bind().setData(buffer).unbind();

        return this;
    }

    public Mesh setIndices(int[] indices) {
        if (m_IndexBuffer == null)
            m_IndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW);

        ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES * indices.length).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(indices);
        m_IndexBuffer.bind().setData(buffer).unbind();

        return this;
    }

    public Mesh setIndices(ByteBuffer buffer) {
        if (m_IndexBuffer == null)
            m_IndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW);

        m_IndexBuffer.bind().setData(buffer).unbind();

        return this;
    }

    public Mesh setVertexLayout(VertexArray.Layout layout) {
        if (m_VertexBuffer == null) return this;
        if (m_VertexArray == null) m_VertexArray = new VertexArray();

        m_VertexArray.bind().bindBuffer(m_VertexBuffer, layout).unbind();

        return this;
    }

    @Override
    public void close() {
        if (m_VertexBuffer != null) m_VertexBuffer.close();
        if (m_IndexBuffer != null) m_IndexBuffer.close();
        if (m_VertexArray != null) m_VertexArray.close();
    }
}
