package io.show.storage;

import java.util.HashMap;
import java.util.Map;

public class World {

    private String m_Name;
    private int WIDTH;
    private int HEIGHT;

    private Map<Integer, int[]> m_Chunks = new HashMap();
    public long heightSeed;
    public long orelikelynessSeed;
    public long treeHeightSeed;
    public long treeLikelinessSeed;
    public long noodleSeed;
    public long cheeseSeed;

    public World(String name, long heightSeed, long orelikelynessSeed, long treeHeightSeed, long treeLikelinessSeed, long noodleSeed, long cheeseSeed) {
        m_Name = name;
        this.heightSeed = heightSeed;
        this.orelikelynessSeed = orelikelynessSeed;
        this.treeHeightSeed = treeHeightSeed;
        this.treeLikelinessSeed = treeLikelinessSeed;
        this.noodleSeed = noodleSeed;
        this.cheeseSeed = cheeseSeed;


    }

    public String getName() {
        return m_Name;
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
