package io.show.game.world;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class World {
    private int WIDTH;
    private int HEIGHT;
    private Random m_Random = new Random();
    public final long heightSeed = m_Random.nextLong();
    public final long orelikelynessSeed = m_Random.nextLong();
    public final long orehightSeed1 = m_Random.nextLong();
    public final long orehightSeed2 = m_Random.nextLong();
    public final long whichOreSeed = m_Random.nextLong();
    public List<Chunk> Map = new Vector<>();

    enum Mapsize {LARGE, MEDIUM}

    public World(Mapsize mapsize) {
        switch (mapsize) {
            case MEDIUM -> {
                WIDTH = Constants.MAP_MEDIUM_WIDTH;
                HEIGHT = Constants.MAP_MEDIUM_HEIGHT;
            }
            case LARGE -> {
                WIDTH = Constants.MAP_LARGE_WIDTH;
                HEIGHT = Constants.MAP_LARGE_HEIGHT;
            }
        }
        for (int i = 0; i < WIDTH / 16; i++) {
            Map.add(new Chunk(HEIGHT, i * 16, this));
        }
    }
}
