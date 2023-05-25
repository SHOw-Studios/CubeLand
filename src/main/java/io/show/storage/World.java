package io.show.storage;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class World {

    private String m_Name;
    private long m_Seed;
    private List<int[]> m_Chunks = new Vector<>();

    public World(String name, long seed, int[]... chunks) {
        m_Name = name;
        m_Seed = seed;
        m_Chunks.addAll(Arrays.asList(chunks));
    }

    public String getName() {
        return m_Name;
    }

    public long getSeed() {
        return m_Seed;
    }

    public int getChunkCount() {
        return m_Chunks.size();
    }

    public int[] getChunk(int x) {
        return m_Chunks.get(x);
    }

}
