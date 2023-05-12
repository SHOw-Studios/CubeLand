package io.show.graphics.internal.scene;

import io.show.graphics.internal.gl.Shader;
import io.show.graphics.internal.gl.Texture;

import java.util.List;
import java.util.Vector;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

public class Material {

    private final Shader m_Shader;
    private final List<Texture> m_Textures;

    public Material(Shader shader, List<Texture> textures) {
        m_Shader = shader;
        m_Textures = textures;
    }

    public Material(Shader shader) {
        this(shader, new Vector<>());
    }

    public Material addTexture(Texture texture) {
        m_Textures.add(texture);
        return this;
    }

    public Material bind() {
        m_Shader.bind();
        int i = GL_TEXTURE0;
        for (Texture texture : m_Textures)
            texture.bind(i++);
        return this;
    }

    public Material unbind() {
        m_Shader.unbind();
        int i = GL_TEXTURE0;
        for (Texture texture : m_Textures)
            texture.unbind(i++);
        return this;
    }

    public Shader getShader() {
        return m_Shader;
    }

    public List<Texture> getTextures() {
        return m_Textures;
    }
}
