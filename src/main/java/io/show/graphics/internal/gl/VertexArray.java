package io.show.graphics.internal.gl;

import java.util.List;
import java.util.Vector;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VertexArray {

    public static class Layout {

        public record Parameter(int count, int type, boolean normalized) {
        }

        public static int sizeof(int type) {
            return switch (type) {
                case GL_FLOAT -> Float.BYTES;
                case GL_UNSIGNED_INT, GL_INT -> Integer.BYTES;
                case GL_UNSIGNED_BYTE, GL_BYTE -> Byte.BYTES;
                case GL_UNSIGNED_SHORT, GL_SHORT -> Short.BYTES;

                default -> 0;
            };
        }

        public List<Parameter> parameterList = new Vector<>();
        public int stride = 0;

        public Layout push(int count, int type, boolean normalized) {
            parameterList.add(new Parameter(count, type, normalized));
            stride += sizeof(type) * count;
            return this;
        }

        public Layout pushFloat(int count) {
            return push(count, GL_FLOAT, false);
        }
    }

    private final int m_Handle;

    public VertexArray() {
        m_Handle = glGenVertexArrays();
    }

    public VertexArray bind() {
        glBindVertexArray(m_Handle);
        return this;
    }

    public VertexArray unbind() {
        glBindVertexArray(0);
        return this;
    }

    public VertexArray bindBuffer(GLBuffer buffer, Layout layout) {
        buffer.bind();

        int pointer = 0;
        for (int i = 0; i < layout.parameterList.size(); i++) {
            Layout.Parameter parameter = layout.parameterList.get(i);

            int size = Layout.sizeof(parameter.type) * parameter.count;
            glVertexAttribPointer(i, size, parameter.type, parameter.normalized, layout.stride, pointer);

            pointer += size;
        }
        buffer.unbind();

        return this;
    }
}
