package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

import java.util.Random;

public class Generator {
    private double xOff;
    private Random random = new Random();
    private int HEIGHT;
    private int WIDTH;
    private int DEPTH;
    private float scale = 0.1f;
    private long m_HeightSeed;
    private int m_StartPos;
    private long m_OrelikelynessSeed;
    private int[] graphicArray;

    public Generator(World inputWorld, Chunk inputChunk, int[] graphicArray) {
        HEIGHT = inputChunk.getHeight();
        WIDTH = inputChunk.getWidth();
        DEPTH = inputChunk.getDepth();
        xOff = inputChunk.getStartPosition();
        m_HeightSeed = inputWorld.m_HeightSeed;
        m_StartPos = inputChunk.getStartPosition();
        this.graphicArray = graphicArray;
        m_OrelikelynessSeed = inputWorld.m_OreLikelinessSeed;
        ;
    }

//    public static void print2D(int[][] mat) {
//        // Loop through all rows
//        // Loop through all elements of current row
//        for (int[] ints : mat)
//            for (int anInt : ints) System.out.print(anInt + " ");
//    }

    public int[][][] generate() {
        int[][][] Map;
        Map = initMap();
        return Map;
    }

    /**
     * Returns an int[][] Array with the values on witch PerlinNoise is set to 1,
     * to get a value that saperates air and Ground
     */
    public int[][][] initMap() {
        int[][][] MapArray;
        MapArray = new int[DEPTH][HEIGHT][WIDTH];
        for (int i = 0; i < DEPTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                for (int k = 0; k < WIDTH; k++) {
                    float noise = HEIGHT / 2 + OpenSimplex2.noise3_ImproveXY(m_HeightSeed, j * scale, (xOff * 16 + k) * scale, i * scale) * 20.0f;
                    float orenoise = OpenSimplex2.noise3_ImproveXY(m_OrelikelynessSeed, j * scale, (xOff * 16 + k) * scale, i * scale);
                    //-150 to get min 50 Ground and min 100 to build above
                    //+50 to get min 50 Ground
                    float d = noise - j;
                    int roundedNoise = Math.round(noise);
                    if (d <= 2 && d > 0) MapArray[i][j][k] = graphicArray[4];
                    else if (j < noise && orenoise < -0.6) MapArray[i][j][k] = graphicArray[11];
                    else if (j < noise && orenoise < -0.5) MapArray[i][j][k] = graphicArray[12];
                    else if (j < noise && orenoise > 0.6) MapArray[i][j][k] = graphicArray[11];
                    else if (j < noise && orenoise > 0.5) MapArray[i][j][k] = graphicArray[13];
                    else if (j < noise) MapArray[i][j][k] = graphicArray[14];
                    else {
                        d = HEIGHT / 2 - j;
                        if (d <= 1 && d > 0) MapArray[i][j][k] = graphicArray[3];
                        else if (d > 0) MapArray[i][j][k] = graphicArray[2];
                        else MapArray[i][j][k] = graphicArray[15];
                    }
                }
            }
        }
        return MapArray;
    }

//    public int[][][] setWater(int[][][] Map) {
//        for (int i = 0; i < Map[0].length; i++) {
//            for (int j = Map[1].length / 2; j < Map[1].length; j++) {
//                if (Map[j][i][0] != 1) Map[j][i][0] = 2;
//                else break;
//            }
//        }
//        return Map;
//    }

}
