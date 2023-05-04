package io.show.game.world;

public class Block {

    public enum BlockType {

        // BLOCK TYPES
        BLOCK_AIR

    }

    private BlockType m_Type;
    private boolean m_Solid;
    private float m_Opacity;

    public Block(BlockType type, boolean solid, float opacity) {
        m_Type = type;
        m_Solid = solid;
        m_Opacity = opacity;
    }

    public Block() {
        this(BlockType.BLOCK_AIR, false, 0.0f);
    }

    public BlockType getType() {
        return m_Type;
    }

    public boolean isSolid() {
        return m_Solid;
    }

    public float getOpacity() {
        return m_Opacity;
    }

}
