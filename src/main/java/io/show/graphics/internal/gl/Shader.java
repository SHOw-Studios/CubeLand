package io.show.graphics.internal.gl;

import io.show.storage.StringListReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

public class Shader {

    public static class CompileStatusException extends Exception {
        public CompileStatusException(String message) {
            super(message);
        }
    }

    public static class LinkStatusException extends Exception {
        public LinkStatusException(String message) {
            super(message);
        }
    }

    public static class ValidateStatusException extends Exception {
        public ValidateStatusException(String message) {
            super(message);
        }
    }

    private int m_Handle;
    private Map<String, Integer> m_Uniforms;

    public Shader(String path) throws CompileStatusException, LinkStatusException, ValidateStatusException {
        m_Uniforms = new HashMap<>();
        compile(parseShaderFile(path));
    }

    public void bind() {
        glUseProgram(m_Handle);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void compile(List<ShaderData> shaderDataList) throws CompileStatusException, LinkStatusException, ValidateStatusException {
        if (m_Handle != 0) glDeleteProgram(m_Handle);
        m_Handle = glCreateProgram();

        for (ShaderData shaderData : shaderDataList) {
            final int id = glCreateShader(shaderData.type);
            glShaderSource(id, shaderData.source);

            glCompileShader(id);
            int status = glGetShaderi(id, GL_COMPILE_STATUS);
            if (status != GL_TRUE)
                throw new CompileStatusException("Compiling error in shader '" + shaderData.identifier + "': \n" + glGetShaderInfoLog(id));

            glAttachShader(m_Handle, id);
            glDeleteShader(id);
        }

        glLinkProgram(m_Handle);
        final int linked = glGetProgrami(m_Handle, GL_LINK_STATUS);
        if (linked != GL_TRUE) throw new LinkStatusException(glGetProgramInfoLog(m_Handle));

        glValidateProgram(m_Handle);
        final int validated = glGetProgrami(m_Handle, GL_VALIDATE_STATUS);
        if (validated != GL_TRUE) throw new ValidateStatusException(glGetProgramInfoLog(m_Handle));
    }

    public int getLocation(String id) {
        if (!m_Uniforms.containsKey(id)) m_Uniforms.put(id, glGetUniformLocation(m_Handle, id));
        return m_Uniforms.get(id);
    }

    public void setUniformFloat(String id, float... v) {
        switch (v.length) {
            case 1:
                glUniform1fv(getLocation(id), v);
            case 2:
                glUniform2fv(getLocation(id), v);
            case 3:
                glUniform3fv(getLocation(id), v);
            case 4:
                glUniform4fv(getLocation(id), v);

            case 0:
            default:
                throw new RuntimeException("undefined for array of length " + v.length);
        }
    }

    public void setUniformInt(String id, int... v) {
        switch (v.length) {
            case 1:
                glUniform1iv(getLocation(id), v);
            case 2:
                glUniform2iv(getLocation(id), v);
            case 3:
                glUniform3iv(getLocation(id), v);
            case 4:
                glUniform4iv(getLocation(id), v);

            case 0:
            default:
                throw new RuntimeException("undefined for array of length " + v.length);
        }
    }

    public void setUniformFloatMat4(String id, float[] v) {
        glUniformMatrix4fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat4x3(String id, float[] v) {
        glUniformMatrix4x3fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat4x2(String id, float[] v) {
        glUniformMatrix4x2fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat3(String id, float[] v) {
        glUniformMatrix3fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat3x4(String id, float[] v) {
        glUniformMatrix3x4fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat3x2(String id, float[] v) {
        glUniformMatrix3x2fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat2(String id, float[] v) {
        glUniformMatrix2fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat2x4(String id, float[] v) {
        glUniformMatrix2x4fv(getLocation(id), false, v);
    }

    public void setUniformFloatMat2x3(String id, float[] v) {
        glUniformMatrix2x3fv(getLocation(id), false, v);
    }

    public record ShaderData(int type, String source, String identifier) {
    }

    public static List<ShaderData> parseShaderFile(String path) {
        List<ShaderData> shaderDataList = new Vector<>();

        List<String> source = StringListReader.read(path);

        StringBuilder src = null;
        String identifier = null;
        int type = -1;

        for (int i = 0; i < source.size(); i++) {
            String line = source.get(i);
            String[] args = line.split(" ");
            if (args[0] == "#") {
                if (args[1] != "shader") continue;
                if (src != null) shaderDataList.add(new ShaderData(type, src.toString(), identifier));

                type = switch (args[2]) {
                    case "vertex" -> GL_VERTEX_SHADER;
                    case "fragment" -> GL_FRAGMENT_SHADER;
                    case "compute" -> GL_COMPUTE_SHADER;
                    default ->
                            throw new RuntimeException("Undefined shader type: '" + args[2] + "' at line " + i + " of file path '" + path + "'");
                };
                identifier = args[3];
                src = new StringBuilder();
            }

            if (src == null) continue;

            src.append(line).append("\n");
        }

        if (src != null) shaderDataList.add(new ShaderData(type, src.toString(), identifier));

        return shaderDataList;
    }
}
