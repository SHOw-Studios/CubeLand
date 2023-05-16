package io.show.graphics.internal.gl;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Felix Schreiber
 */
public class TextureAtlas implements AutoCloseable {

    private final Texture m_Texture;
    private final int m_TileW;
    private final int m_TileH;

    public TextureAtlas(int tileW, int tileH, int tilesX, int tilesY) {
        m_Texture = new Texture().bind().setDefaultParameters().setData(tileW * tilesX, tileH * tilesY, null).unbind();
        m_TileW = tileW;
        m_TileH = tileH;
    }

    public TextureAtlas bind() {
        m_Texture.bind();
        return this;
    }

    public TextureAtlas bind(int i) {
        m_Texture.bind(i);
        return this;
    }

    public TextureAtlas unbind() {
        m_Texture.unbind();
        return this;
    }

    public TextureAtlas unbind(int i) {
        m_Texture.unbind(i);
        return this;
    }

    public TextureAtlas setTile(int tileX, int tileY, @Nullable ByteBuffer data) {
        m_Texture.setData(tileX * m_TileW, tileY * m_TileH, m_TileW, m_TileH, data == null ? ByteBuffer.allocateDirect(m_TileW * m_TileH * 4).order(ByteOrder.nativeOrder()) : data);
        return this;
    }

    public Texture getTexture() {
        return m_Texture;
    }

    @Override
    public void close() {
        m_Texture.close();
    }
}
