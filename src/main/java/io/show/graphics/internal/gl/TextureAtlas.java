package io.show.graphics.internal.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Felix Schreiber
 */
public class TextureAtlas implements AutoCloseable {

    private final Texture m_Texture;
    private final int m_TileW;
    private final int m_TileH;

    public TextureAtlas(int tileW, int tileH, int tilesX, int tilesY, int clear) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(tileW * tilesX * tileH * tilesY * 4).order(ByteOrder.nativeOrder());
        while (buffer.remaining() > 0) {
            buffer.putInt(clear);
        }
        buffer.position(0);

        m_Texture = new Texture().bind().setData(tileW * tilesX, tileH * tilesY, buffer).unbind();
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

    public TextureAtlas setTile(int tileX, int tileY, ByteBuffer data) {
        m_Texture.setData(tileX * m_TileW, tileY * m_TileH, m_TileW, m_TileH, data);
        return this;
    }

    public int getWidth() {
        return m_Texture.getWidth();
    }

    public int getHeight() {
        return m_Texture.getHeight();
    }

    public int getTileW() {
        return m_TileW;
    }

    public int getTileH() {
        return m_TileH;
    }

    public int getTilesX() {
        return getWidth() / getTileW();
    }

    public Texture getTexture() {
        return m_Texture;
    }

    @Override
    public void close() {
        m_Texture.close();
    }
}
