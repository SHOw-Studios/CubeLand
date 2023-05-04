package io.show.game.world;

import vendor.opensimplex2.OpenSimplex2;

import java.util.Arrays;
import java.util.Random;

public class Generator {
    double xOff = 0;
    Random random = new Random();
    long seed = random.nextLong();

    // Map size in Tiles

    private enum MapSize{medium,large}

    public static void main(String[] args) {
        Generator generator = new Generator();
        print2D(generator.getGroundHight(MapSize.medium));
    }

    public static void print2D(int mat[][])
    {
        // Loop through all rows
        for (int i = 0; i < mat.length; i++)

            // Loop through all elements of current row
            for (int j = 0; j < mat[i].length; j++)
                System.out.print(mat[i][j] + " ");
    }

    /**
     * Returns an int[][] Array with the values on witch PerlinNoise is set to 1,
     * to get a value that saperates air and Ground
    */
    public int[][] getGroundHight(MapSize mapSize){
        int[][] MapArray = new int[0][0];
        switch (mapSize) {
            case medium -> {
                MapArray = new int[World.MAP_MEDIUM_WIDTH][World.MAP_MEDIUM_HIGHT];
                for (int i = 0; i < World.MAP_MEDIUM_WIDTH; i++) {
                    float noise = OpenSimplex2.noise2(seed, xOff, 0)*(World.MAP_MEDIUM_HIGHT-150)+50;
                    //-150 to get min 50 Ground and min 100 to build above
                    //+50 to get min 50 Ground
                    int roundedNoise = (int) Math.round(noise);
                    MapArray[i][roundedNoise]=1;
                }
            }
            case large -> {
                MapArray = new int[World.MAP_LARGE_WIDTH][World.MAP_LARGE_HIGHT];
                for (int i = 0; i < World.MAP_LARGE_WIDTH; i++) {
                    float noise = OpenSimplex2.noise2(seed, xOff, 0);
                    int roundedNoise = (int) Math.round(noise);
                    MapArray[i][roundedNoise]=1;
                }
            }
        }
        if(Arrays.deepEquals(MapArray, new int[0][0])){
            System.err.println("Medium or Large didnt initialize array correctly");
        }
        return MapArray;
    }
    float noiseValue = OpenSimplex2.noise2(seed, xOff, 0.0);

}
