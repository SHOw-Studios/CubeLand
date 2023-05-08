package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

import java.util.Arrays;
import java.util.Random;

public class Generator {
    double xOff = 0;
    Random random = new Random();
    long seed = random.nextLong();
    private int HEIGHT;
    private int WIDTH;
    private Orelikelikelyness ores = new Orelikelikelyness();

    Generator(int height, int width) {
        HEIGHT = height;
        WIDTH = width;
    }

    public static void main(String[] args) {
        Generator generator = new Generator(Constants.MAP_MEDIUM_HEIGHT, Constants.MAP_MEDIUM_WIDTH);
        print2D(generator.generate());
    }

    public static void print2D(int[][] mat) {
        // Loop through all rows
        // Loop through all elements of current row
        for (int[] ints : mat)
            for (int anInt : ints) System.out.print(anInt + " ");
    }

    public int[][] generate() {
        int[][] Map;
        Map = initMap();
        Map = setWater(Map);
        Map = ores.setOres(Map);
        // TODO: 08.05.2023 make Generator for Trees
        // TODO: 08.05.2023 Hardcode Trees (perlinnoise for leaf count)
        return Map;
    }

    /**
     * Returns an int[][] Array with the values on witch PerlinNoise is set to 1,
     * to get a value that saperates air and Ground
     */
    public int[][] initMap() {
        int[][] MapArray = new int[0][0];
        MapArray = new int[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH - 1; i++) {
            float noise = OpenSimplex2.noise2(seed, xOff, 0) * (HEIGHT - 150) + 50;
            //-150 to get min 50 Ground and min 100 to build above
            //+50 to get min 50 Ground
            int roundedNoise = Math.round(noise);
            for (int j = roundedNoise; j < HEIGHT - 1; j++) {
                MapArray[i][j] = 1;
            }
            System.out.print("ok");
        }
        if (Arrays.deepEquals(MapArray, new int[0][0])) {
            System.err.println("Medium or Large didn't initialize array correctly");
        }
        return MapArray;
    }

    public int[][] setWater(int[][] Map) {
        for (int i = 0; i < Map[0].length; i++) {
            for (int j = Map[1].length / 2; j < Map[1].length; j++) {
                if (Map[j][i] != 1) Map[j][i] = 2;
            }
        }
        return Map;
    }

    public long getSeed() {
        return seed;
    }

    public long newSeed() {
        seed = random.nextLong();
        return seed;
    }

}
