package io.show.storage;

import java.io.*;
import java.util.List;
import java.util.Vector;

import io.show.game.world.World;

public class WorldReader {

    private File m_File;

    public WorldReader(File file) {
        m_File = file;
    }

    public World read() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(m_File));

        final int WIDTH = 16;
        final int HEIGHT = 256;
        byte[] currentChunkBlocks = new byte[WIDTH * HEIGHT];
        List<Integer> currentChunkEntities = new Vector<>();

        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) continue;


        }

        return null;
    }
}
