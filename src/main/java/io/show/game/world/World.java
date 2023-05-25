package io.show.game.world;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.*;

public class World {
    private int WIDTH;
    private int HEIGHT;
    private int chunk0 = 2;
    private Random m_Random = new Random();
    public final long heightSeed = m_Random.nextLong();
    public final long orelikelynessSeed = m_Random.nextLong();
    public final long treeHeightSeed = m_Random.nextLong();
    public final long treeLikelinessSeed = m_Random.nextLong();
    public final long noodleSeed = m_Random.nextLong();
    public final long cheeseSeed = m_Random.nextLong();
    public List<Chunk> Map = new Vector<>();

    public enum Mapsize {LARGE, MEDIUM, SMALL}

    public World(Mapsize mapsize, int[] graphicArr) {
        switch (mapsize) {
            case MEDIUM -> {
                WIDTH = Constants.MAP_MEDIUM_WIDTH;
                HEIGHT = Constants.MAP_MEDIUM_HEIGHT;
            }
            case LARGE -> {
                WIDTH = Constants.MAP_LARGE_WIDTH;
                HEIGHT = Constants.MAP_LARGE_HEIGHT;
            }
            case SMALL -> {
                WIDTH = Constants.MAP_SMALL_WIDTH;
                HEIGHT = Constants.MAP_SMALL_HEIGHT;
            }
        }
        for (int i = 0; i < WIDTH / 16; i++) {
            Map.add(new Chunk(HEIGHT, i * 16, this, graphicArr));
        }
    }

    public Chunk getChunkAtPos(int pos) {
        return Map.get(pos + chunk0);
    }

//    public static void main(String[] args) {
//        int[][][] arr1 = {{{1, 2}, {3, 4}, {5, 6}, {7, 8}}, {{9, 10}, {11, 12}}};
//        int[][][] arr2 = {{{13, 14}, {15, 16}, {17, 18}, {19, 20}}, {{21, 212}, {22, 23}}};
//        int[][][] arr3 = {{{24, 25}, {26, 27}}, {{28, 29}, {30, 31}}};
//        int[][][] arr4 = {{{32, 33}, {34, 35}}, {{36, 37}, {38, 39}}};
//        int[][][] arr5 = {{{40, 41}, {42, 43}}, {{44, 45}, {46, 47}}};
//        int[][][] result = append_5_3DArrays(arr1, arr2, arr3, arr4, arr5);
//
//        // Print the appended array
//        for (int[][] arr : result) {
//            for (int[] row : arr) {
//                System.out.println(Arrays.toString(row));
//            }
//            System.out.println();
//        }
//    }

    public static int[][][] append_5_3DArrays(int[][][] arr1, int[][][] arr2, int[][][] arr3, int[][][] arr4, int[][][] arr5) {
        int depth = arr1.length;
        int chunkWidth = arr1[0][0].length;
        int chunkHeight = arr1[1].length;
        int totalWidth = 5 * chunkWidth;

        int[][][] result = new int[depth][chunkHeight][totalWidth];

        int i = 0;
        for (int k = 0; k < depth; k++) {
            for (int h = 0; h < chunkHeight; h++) {
                for (int j = 0; j < chunkWidth; j++) {
                    result[k][h][j + i * chunkWidth] = arr1[k][h][j];
                }
            }
        }
        i++;
        for (int k = 0; k < depth; k++) {
            for (int h = 0; h < chunkHeight; h++) {
                for (int j = 0; j < chunkWidth; j++) {
                    result[k][h][j + i * chunkWidth] = arr2[k][h][j];
                }
            }
        }
        i++;
        for (int k = 0; k < depth; k++) {
            for (int h = 0; h < chunkHeight; h++) {
                for (int j = 0; j < chunkWidth; j++) {
                    result[k][h][j + i * chunkWidth] = arr3[k][h][j];
                }
            }
        }
        i++;
        for (int k = 0; k < depth; k++) {
            for (int h = 0; h < chunkHeight; h++) {
                for (int j = 0; j < chunkWidth; j++) {
                    result[k][h][j + i * chunkWidth] = arr4[k][h][j];
                }
            }
        }
        i++;
        for (int k = 0; k < depth; k++) {
            for (int h = 0; h < chunkHeight; h++) {
                for (int j = 0; j < chunkWidth; j++) {
                    result[k][h][j + i * chunkWidth] = arr5[k][h][j];
                }
            }
        }
        i++;


        return result;
    }

}
