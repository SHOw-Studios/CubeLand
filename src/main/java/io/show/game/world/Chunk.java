package io.show.game.world;

public class Chunk {

    private static final int WIDTH = 16;
    private int HEIGHT;
    private static final int LAYERS = 2;
    private int m_StartPosition;
    private World m_ParentWorld;
    private int[][][] m_Blocks;

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getM_StartPosition() {
        return m_StartPosition;
    }

    public World getM_ParentWorld() {
        return m_ParentWorld;
    }

    public int[][][] getM_Blocks() {
        return m_Blocks;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public Chunk(int height, int startPosition, World parentWorld) {
        HEIGHT = height;
        m_Blocks = new int[WIDTH][HEIGHT][LAYERS];
        m_StartPosition = startPosition;
        m_ParentWorld = parentWorld;
        Generator generator = new Generator(parentWorld, this);
        m_Blocks = generator.generate();
    }

}
