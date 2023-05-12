package io.show.graphics.internal.gl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public Shader(String path) throws CompileStatusException, LinkStatusException, ValidateStatusException, IOException {
        m_Uniforms = new HashMap<>();
        compile(parseShaderFile(path));
    }

    public Shader bind() {
        glUseProgram(m_Handle);
        return this;
    }

    public Shader unbind() {
        glUseProgram(0);
        return this;
    }

    public Shader compile(List<ShaderData> shaderDataList) throws CompileStatusException, LinkStatusException, ValidateStatusException {
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

        return this;
    }

    public int getLocation(String id) {
        if (!m_Uniforms.containsKey(id)) m_Uniforms.put(id, glGetUniformLocation(m_Handle, id));
        return m_Uniforms.get(id);
    }

    public Shader setUniformFloat(String id, float... v) {
        switch (v.length) {
            case 1:
                glUniform1fv(getLocation(id), v);
                break;
            case 2:
                glUniform2fv(getLocation(id), v);
                break;
            case 3:
                glUniform3fv(getLocation(id), v);
                break;
            case 4:
                glUniform4fv(getLocation(id), v);
                break;

            default:
                throw new RuntimeException("undefined for array of length " + v.length);
        }

        return this;
    }

    public Shader setUniformInt(String id, int... v) {
        switch (v.length) {
            case 1:
                glUniform1iv(getLocation(id), v);
                break;
            case 2:
                glUniform2iv(getLocation(id), v);
                break;
            case 3:
                glUniform3iv(getLocation(id), v);
                break;
            case 4:
                glUniform4iv(getLocation(id), v);

            default:
                throw new RuntimeException("undefined for array of length " + v.length);
        }

        return this;
    }

    public Shader setUniformFloatMat4(String id, float[] v) {
        glUniformMatrix4fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat4x3(String id, float[] v) {
        glUniformMatrix4x3fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat4x2(String id, float[] v) {
        glUniformMatrix4x2fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat3(String id, float[] v) {
        glUniformMatrix3fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat3x4(String id, float[] v) {
        glUniformMatrix3x4fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat3x2(String id, float[] v) {
        glUniformMatrix3x2fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat2(String id, float[] v) {
        glUniformMatrix2fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat2x4(String id, float[] v) {
        glUniformMatrix2x4fv(getLocation(id), false, v);
        return this;
    }

    public Shader setUniformFloatMat2x3(String id, float[] v) {
        glUniformMatrix2x3fv(getLocation(id), false, v);
        return this;
    }

    public record ShaderData(int type, String source, String identifier) {
    }

    public static List<ShaderData> parseShaderFile(String path) throws IOException {
        List<ShaderData> shaderDataList = new Vector<>();

        BufferedReader reader = new BufferedReader(new FileReader(path));

        StringBuilder src = null;
        String identifier = null;
        int type = -1;

        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            i++;
            String[] args = line.split(" ");
            if (args[0].equals("#")) {
                if (!args[1].equals("shader")) continue;
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

                continue;
            }

            if (src == null) continue;

            src.append(line).append("\n");
        }

        if (src != null) shaderDataList.add(new ShaderData(type, src.toString(), identifier));

        reader.close();

        return shaderDataList;
    }
}
