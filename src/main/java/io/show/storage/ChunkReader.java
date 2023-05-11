package io.show.storage;

import io.show.game.world.Chunk;
import io.show.game.world.World;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class ChunkReader {

    public Chunk read() throws IOException {
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
