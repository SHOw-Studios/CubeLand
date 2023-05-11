package io.show.graphics.internal;

import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.Shader;
import io.show.graphics.internal.gl.VertexArray;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {

    public static void render(VertexArray vertexArray, GLBuffer indexBuffer, Shader shader) {
        shader.bind();
        vertexArray.bind();
        indexBuffer.bind();

        glDrawElements(GL_TRIANGLES, indexBuffer.getSize() / Integer.BYTES, GL_UNSIGNED_INT, NULL);

        indexBuffer.unbind();
        vertexArray.unbind();
        shader.unbind();
    }

}
