package io.show.graphics.internal;

import io.show.graphics.Graphics;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallback;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_DONT_CARE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.opengl.GL43.glDebugMessageControl;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT_SYNCHRONOUS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Debug {

    public static void enableDebug() {
        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {

            String _source = switch (source) {
                case GL43.GL_DEBUG_SOURCE_API -> "API";
                case GL43.GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW SYSTEM";
                case GL43.GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER COMPILER";
                case GL43.GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD PARTY";
                case GL43.GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION";
                case GL43.GL_DEBUG_SOURCE_OTHER -> "OTHER";
                default -> "UNKNOWN";
            };

            String _type = switch (type) {
                case GL43.GL_DEBUG_TYPE_ERROR -> "ERROR";
                case GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED BEHAVIOR";
                case GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED BEHAVIOR";
                case GL43.GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY";
                case GL43.GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE";
                case GL43.GL_DEBUG_TYPE_OTHER -> "OTHER";
                case GL43.GL_DEBUG_TYPE_MARKER -> "MARKER";
                default -> "UNKNOWN";
            };

            String _severity = switch (severity) {
                case GL43.GL_DEBUG_SEVERITY_HIGH -> "HIGH";
                case GL43.GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM";
                case GL43.GL_DEBUG_SEVERITY_LOW -> "LOW";
                case GL43.GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION";
                default -> "UNKNOWN";
            };

            System.out.printf("0x%s: %s of %s severity, raised from %s: %s\n", Integer.toHexString(id).toUpperCase(), _type, _severity, _source, GLDebugMessageCallback.getMessage(length, message));
            Graphics.getInstance().getDebugInfoWindow().logf("0x%s: %s of %s severity, raised from %s: %s\n", Integer.toHexString(id).toUpperCase(), _type, _severity, _source, GLDebugMessageCallback.getMessage(length, message));

            if (type == GL43.GL_DEBUG_TYPE_ERROR || severity == GL43.GL_DEBUG_SEVERITY_HIGH) {
                System.err.println("TJesus don't like it");
                Graphics.getInstance().getDebugInfoWindow().logf("TJesus don't like it");
            }

        }, NULL);
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);

        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer) null, false);
    }

    public static void disableDebug() {
        glDisable(GL_DEBUG_OUTPUT);
        glDisable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(null, NULL);
    }
}
