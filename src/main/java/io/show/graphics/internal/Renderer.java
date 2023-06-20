package io.show.graphics.internal;

import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.VertexArray;
import io.show.graphics.internal.scene.Material;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Felix Schreiber
 */
public class Renderer {

    public static void render(VertexArray vertexArray, GLBuffer indexBuffer, Material material) {
        material.bind();
        vertexArray.bind();
        indexBuffer.bind();

        int count = indexBuffer.getSize() / Integer.BYTES;
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, NULL);

        indexBuffer.unbind();
        vertexArray.unbind();
        material.unbind();
    }

}
