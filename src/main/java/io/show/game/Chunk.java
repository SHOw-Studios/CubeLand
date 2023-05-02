package io.show.game;

public class Chunk {

    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    private static final int LAYERS = 2;

    private Block[][][] m_Blocks;

    public Chunk() {
        m_Blocks = new Block[LAYERS][HEIGHT][WIDTH];
    }

    public Block getBlock(int x, int y, int l) {
        return m_Blocks[l][y][x];
    }

    public Block getBlock(int idx) {
        int[] coord = indexToCoord(idx);
        return getBlock(coord[0], coord[1], coord[2]);
    }

    public Block setBlock(int x, int y, int l, Block block) {
        return m_Blocks[l][y][x] = block;
    }

    public Block setBlock(int idx, Block block) {
        int[] coord = indexToCoord(idx);
        return setBlock(coord[0], coord[1], coord[2], block);
    }

    private int coordToIndex(int x, int y, int l) {
        return x + y * WIDTH + l * WIDTH * HEIGHT;
    }

    private int[] indexToCoord(int idx) {
        int l = idx % LAYERS;
        idx /= LAYERS;
        int y = idx % HEIGHT;
        idx /= HEIGHT;
        int x = idx;
        return new int[]{x, y, l};
    }

}
