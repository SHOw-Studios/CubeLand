package io.show.graphics.internal.scene;

import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.VertexArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL15.*;

public class QuadMesh extends Mesh {

    public QuadMesh() {
        final float[] vertices = new float[]{-1.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f};
        ByteBuffer buffer = ByteBuffer.allocateDirect(Float.BYTES * vertices.length).order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(vertices);
        m_VertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        final int[] indices = new int[]{0, 1, 2, 2, 3, 0};
        buffer = ByteBuffer.allocateDirect(Integer.BYTES * indices.length).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(indices);
        m_IndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        final VertexArray.Layout layout = new VertexArray.Layout().pushFloat(3).pushFloat(2);
        m_VertexArray = new VertexArray().bind().bindBuffer(m_VertexBuffer, layout).unbind();
    }
}
