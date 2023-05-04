package io.show.game.world;

public class Chunk {

    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    private static final int LAYERS = 2;

    private Block[] m_Blocks;

    public Chunk() {
        m_Blocks = new Block[LAYERS * HEIGHT * WIDTH];
    }

    public Block getBlock(int x, int y, int l) {
        return m_Blocks[x + y * WIDTH + l * WIDTH * HEIGHT];
    }

    public Block getBlock(int idx) {
        return m_Blocks[idx];
    }

    public Block setBlock(int x, int y, int l, Block block) {
        return m_Blocks[x + y * WIDTH + l * WIDTH * HEIGHT] = block;
    }

    public Block setBlock(int idx, Block block) {
        return m_Blocks[idx] = block;
    }

}
