package io.show.graphics.internal.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengles.GLES30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TextureAtlas {

    private final int m_Handle;

    private final int m_TileW;
    private final int m_TileH;
    private final int m_TilesX;
    private final int m_TilesY;

    public TextureAtlas(int tileW, int tileH, int tilesX, int tilesY) {
        int[] handle = new int[1];
        glGenTextures(handle);
        m_Handle = handle[0];

        bind();
        setDefaultParameters();
        setData(tilesX * tileW, tilesY * tileH, null);
        unbind();

        m_TileW = tileW;
        m_TileH = tileH;

        m_TilesX = tilesX;
        m_TilesY = tilesY;
    }

    public int getHandle() {
        return m_Handle;
    }

    public TextureAtlas bind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, m_Handle);
        return this;
    }

    public TextureAtlas unbind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        return this;
    }

    public TextureAtlas setDefaultParameters() {
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_BORDER);
        return this;
    }

    public TextureAtlas setData(int width, int height, ByteBuffer data) {
        if (data == null)
            glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA32F, m_TileW, m_TileH, m_TilesX * m_TilesY, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        else
            glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA32F, m_TileW, m_TileH, m_TilesX * m_TilesY, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        return this;
    }

    public TextureAtlas setTile(int x, int y, ByteBuffer data) {
        glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, x + y * m_TilesX, m_TileW, m_TileH, 1, GL_RGBA, GL_UNSIGNED_BYTE, data);
        return this;
    }
}
