package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

/**
 * @author Ilian O.
 */
public class Generator {
    private double m_xOff;
    private int m_HEIGHT;
    private int m_WIDTH;
    private int m_DEPTH;
    private float m_scale = 0.1f;
    private long m_HeightSeed;
    private int m_StartPos;
    private long m_OrelikelynessSeed;
    private int[] m_graphicArray;

    /**
     * Generates a Generator depending on the chunk and world it should generate a mapslice for.
     * The inputChunk is there to get the Chunkspecific values. InputWorld has the same task.
     */
    public Generator(World inputWorld, Chunk inputChunk, int[] graphicArray) {
        m_HEIGHT = inputChunk.getHeight();
        m_WIDTH = inputChunk.getWidth();
        m_DEPTH = inputChunk.getDepth();
        m_xOff = inputChunk.getStartPosition();
        m_HeightSeed = inputWorld.getHeightSeed();
        m_StartPos = inputChunk.getStartPosition();
        m_graphicArray = graphicArray;
        m_OrelikelynessSeed = inputWorld.getOreLikelinessSeed();
    }

    /**
     * Returns an int[][][] Array representing the generated map
     */
    public int[][][] generate() {
        int[][][] Map;
        Map = initMap();
        return Map;
    }

    /**
     * Returns an int[][] Array with values!=0 if PerlinNoise is higher than a threshold,
     * to add Blocks to the World
     */
    public int[][][] initMap() {
        int[][][] MapArray;
        MapArray = new int[m_DEPTH][m_HEIGHT][m_WIDTH];
        for (int i = 0; i < m_DEPTH; i++) {
            for (int j = 0; j < m_HEIGHT; j++) {
                for (int k = 0; k < m_WIDTH; k++) {
                    float noise = m_HEIGHT / 2 + OpenSimplex2.noise3_ImproveXY(m_HeightSeed, j * m_scale, (m_xOff * 16 + k) * m_scale, i * m_scale) * 20.0f;
                    float orenoise = OpenSimplex2.noise3_ImproveXY(m_OrelikelynessSeed, j * m_scale, (m_xOff * 16 + k) * m_scale, i * m_scale);
                    float d = noise - j;
                    if (d <= 2 && d > 0) MapArray[i][j][k] = m_graphicArray[4];
                    else if (j < noise && orenoise < -0.6) MapArray[i][j][k] = m_graphicArray[11];
                    else if (j < noise && orenoise < -0.5) MapArray[i][j][k] = m_graphicArray[12];
                    else if (j < noise && orenoise > 0.6) MapArray[i][j][k] = m_graphicArray[11];
                    else if (j < noise && orenoise > 0.5) MapArray[i][j][k] = m_graphicArray[13];
                    else if (j < noise) MapArray[i][j][k] = m_graphicArray[14];
                    else {
                        d = m_HEIGHT / 2 - j;
                        if (d <= 1 && d > 0) MapArray[i][j][k] = m_graphicArray[3];
                        else if (d > 0) MapArray[i][j][k] = m_graphicArray[2];
                        else MapArray[i][j][k] = m_graphicArray[15];
                    }
                }
            }
        }
        return MapArray;
    }

}
