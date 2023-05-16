package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

import java.util.Random;

public class Generator {
    private double xOff;
    private Random random = new Random();
    private int HEIGHT;
    private int WIDTH;
    private long m_HeightSeed;
    private int m_StartPos;
    private long m_OrelikelynessSeed;
    private long m_OrehightSeed1;
    private long m_OrehightSeed2;
    private long m_WhichOreSeed;
    private Orelikelikelyness ores = new Orelikelikelyness(m_OrehightSeed1, m_OrehightSeed2,
            m_OrelikelynessSeed, m_WhichOreSeed);

    public Generator(World inputWorld, Chunk inputChunk) {
        HEIGHT = inputChunk.getHEIGHT();
        WIDTH = inputChunk.getWIDTH();
        xOff = inputChunk.getM_StartPosition();
        m_HeightSeed = inputWorld.heightSeed;
        m_StartPos = inputChunk.getM_StartPosition();
    }

//    public static void print2D(int[][] mat) {
//        // Loop through all rows
//        // Loop through all elements of current row
//        for (int[] ints : mat)
//            for (int anInt : ints) System.out.print(anInt + " ");
//    }

    public int[][][] generate() {
        int[][][] Map;
        Map = initMap(m_StartPos);
        Map = setWater(Map);
        Map = ores.setOres(Map);
        return Map;
    }

    /**
     * Returns an int[][] Array with the values on witch PerlinNoise is set to 1,
     * to get a value that saperates air and Ground
     */
    public int[][][] initMap(int startPos) {
        int[][][] MapArray;
        MapArray = new int[WIDTH][HEIGHT][2];
        for (int i = startPos; i < WIDTH - 1; i++) {
            float noise = OpenSimplex2.noise2(m_HeightSeed, xOff, 0) * (HEIGHT - 150) + 50;
            //-150 to get min 50 Ground and min 100 to build above
            //+50 to get min 50 Ground
            int roundedNoise = Math.round(noise);
            for (int j = roundedNoise; j < HEIGHT - 1; j++) {
                MapArray[i][j][0] = 1;
            }
            System.out.print("ok");
        }
        return MapArray;
    }

    public int[][][] setWater(int[][][] Map) {
        for (int i = 0; i < Map[0].length; i++) {
            for (int j = Map[1].length / 2; j < Map[1].length; j++) {
                if (Map[j][i][0] != 1) Map[j][i][0] = 2;
                else break;
            }
        }
        return Map;
    }

}
