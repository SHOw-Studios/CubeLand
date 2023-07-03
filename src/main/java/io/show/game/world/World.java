package io.show.game.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Ilian O.
 */
public class World {
    private int m_Width;


    private int m_Height;
    private final Random m_Random = new Random();
    public final long m_HeightSeed;
    public final long m_OreLikelinessSeed;
    public final long m_TreeHeightSeed;
    public final long m_TreeLikelinessSeed;
    //noodle and Cheese Seed are there for cave creation in the future
    public final long m_NoodleSeed;
    public final long m_CheeseSeed;
    private final Map<Integer, Chunk> m_Chunks = new HashMap<>();
    private final int[] m_BlockTypes;

    public enum MapSize {LARGE, MEDIUM, SMALL}

    public World(MapSize mapsize, int[] blockTypes) {
        m_HeightSeed = m_Random.nextLong();
        m_OreLikelinessSeed = m_Random.nextLong();
        m_TreeHeightSeed = m_Random.nextLong();
        m_TreeLikelinessSeed = m_Random.nextLong();
        m_NoodleSeed = m_Random.nextLong();
        m_CheeseSeed = m_Random.nextLong();

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

    public World(io.show.storage.World StoredWorld, int[] blockTypes) {
        m_HeightSeed = StoredWorld.getHeightSeed();
        m_OreLikelinessSeed = StoredWorld.getOreLikelinessSeed();
        m_TreeHeightSeed = StoredWorld.getTreeHeightSeed();
        m_TreeLikelinessSeed = StoredWorld.getTreeLikelinessSeed();
        m_NoodleSeed = StoredWorld.getNoodleSeed();
        m_CheeseSeed = StoredWorld.getCheeseSeed();
        m_BlockTypes = blockTypes;
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

    //A relict of the past. Maybe a war crime, but it worked
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
                m_Chunks.put(c + startChunk, new Chunk(c + startChunk, this, m_BlockTypes));
                chunk = m_Chunks.get(c + startChunk);
            }

            int[][][] blocks = chunk.getBlocks();

            for (int k = 0; k < Chunk.getDepth(); k++)
                for (int j = 0; j < chunk.getHeight(); j++)
                    System.arraycopy(blocks[k][j], 0, world[k][j], c * Chunk.getWidth(), Chunk.getWidth());
        }

        return world;
    }

    /**
     * add A chunk to the Hashmap
     *
     * @param index
     */
    public void addChunk(int index) {
        m_Chunks.put(index, new Chunk(index, this, m_BlockTypes));
    }

}
