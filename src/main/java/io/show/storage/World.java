package io.show.storage;

import java.util.HashMap;
import java.util.Map;

public class World {

    private String m_Name;
    private long m_Seed;
    private Map<Integer, int[]> m_Chunks = new HashMap();

    public World(String name, long seed) {
        m_Name = name;
        m_Seed = seed;
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

    public void addChunk(int x, int[] chunk) {
        m_Chunks.put(x, chunk);
    }

}
