package io.show.game.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class World {
    private int m_Width;


    private int m_Height;
    private final Random m_Random = new Random();
    public final long m_HeightSeed = m_Random.nextLong();
    public final long m_OreLikelinessSeed = m_Random.nextLong();
    public final long m_TreeHeightSeed = m_Random.nextLong();
    public final long m_TreeLikelinessSeed = m_Random.nextLong();
    public final long m_NoodleSeed = m_Random.nextLong();
    public final long m_CheeseSeed = m_Random.nextLong();
    private final Map<Integer, Chunk> m_Chunks = new HashMap<>();
    private final int[] m_BlockTypes;

    public enum MapSize {LARGE, MEDIUM, SMALL}

    public World(MapSize mapsize, int[] blockTypes) {
        switch (mapsize) {
            case MEDIUM -> {
                m_Width = Constants.MAP_MEDIUM_WIDTH;
                m_Height = Constants.MAP_MEDIUM_HEIGHT;
            }
            case LARGE -> {
                m_Width = Constants.MAP_LARGE_WIDTH;
                m_Height = Constants.MAP_LARGE_HEIGHT;
            }
            case SMALL -> {
                m_Width = Constants.MAP_SMALL_WIDTH;
                m_Height = Constants.MAP_SMALL_HEIGHT;
            }
        }

        m_BlockTypes = blockTypes;

        for (int i = 0; i < m_Width; i += Chunk.getWidth())
            m_Chunks.put(i / Chunk.getWidth(), new Chunk(i, this, blockTypes));

    }

    public Chunk getChunkAtPos(int pos) {
        return m_Chunks.get(pos / Chunk.getWidth());
    }

    public int getChunkIndexAtPos(int pos) {
        return pos / Chunk.getWidth();
    }

    public Chunk getChunk(int index) {
        return m_Chunks.get(index);
    }

    public int getHeight() {
        return m_Height;
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

    /*
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
*/

    /**
     * make a 3d array containing the given chunks inclusively
     *
     * @param startChunk first chunk inclusive
     * @param endChunk   last chunk inclusive
     * @return a 3d world array
     */
    public int[][][] makeWorldArray(int startChunk, int endChunk) {
        int span = endChunk - startChunk + 1;

        int[][][] world = new int[Chunk.getDepth()][m_Height][Chunk.getWidth() * span];

        for (int c = 0; c < span; c++) {
            Chunk chunk = m_Chunks.get(c + startChunk);

            if (chunk == null) {
                m_Chunks.put(c + startChunk, new Chunk(m_Height, c + startChunk, this, m_BlockTypes));
                chunk = m_Chunks.get(c + startChunk);
            }

            int[][][] blocks = chunk.getBlocks();

            for (int k = 0; k < Chunk.getDepth(); k++)
                for (int j = 0; j < chunk.getHeight(); j++)
                    System.arraycopy(blocks[k][j], 0, world[k][j], c * Chunk.getWidth(), Chunk.getWidth());
        }

        return world;
    }

}
