package io.show.graphics.internal;

import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.VertexArray;
import io.show.graphics.internal.scene.Material;
import org.lwjgl.opengl.GLDebugMessageCallback;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {

    public static void debug() {
        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {

            String _source = switch (source) {
                case GL_DEBUG_SOURCE_API -> "API";
                case GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW SYSTEM";
                case GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER COMPILER";
                case GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD PARTY";
                case GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION";
                case GL_DEBUG_SOURCE_OTHER -> "OTHER";
                default -> "UNKNOWN";
            };

            String _type = switch (type) {
                case GL_DEBUG_TYPE_ERROR -> "ERROR";
                case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED BEHAVIOR";
                case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED BEHAVIOR";
                case GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY";
                case GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE";
                case GL_DEBUG_TYPE_OTHER -> "OTHER";
                case GL_DEBUG_TYPE_MARKER -> "MARKER";
                default -> "UNKNOWN";
            };

            String _severity = switch (severity) {
                case GL_DEBUG_SEVERITY_HIGH -> "HIGH";
                case GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM";
                case GL_DEBUG_SEVERITY_LOW -> "LOW";
                case GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION";
                default -> "UNKNOWN";
            };

            System.out.printf("0x%s: %s of %s severity, raised from %s: %s\n", Integer.toHexString(id).toUpperCase(), _type, _severity, _source, GLDebugMessageCallback.getMessage(length, message));

            if (type == GL_DEBUG_TYPE_ERROR || severity == GL_DEBUG_SEVERITY_HIGH) {
                System.out.println("continue");
            }

        }, NULL);
    }

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
