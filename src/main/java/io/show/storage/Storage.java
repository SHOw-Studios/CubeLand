package io.show.storage;

import org.json.JSONObject;
import org.json.JSONWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Vector;

/**
 * @author Samuel Hierl
 */
public class Storage {

    public static JSONObject readJson(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            builder.append(line).append('\n');

        return new JSONObject(builder.toString());
    }

    public static BufferedImage readImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    public static List<String> readList(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        List<String> list = new Vector<>();
        String line;
        while ((line = reader.readLine()) != null)
            list.add(line);

        return list;
    }

    private static final String SAVES_PATH = "saves";

    public static boolean writeWorld(World world) throws IOException {
        File dir = new File(SAVES_PATH, world.getName());
        if (!dir.exists())
            if (!dir.mkdirs()) return false;

        File worldFile = new File(dir, "world.json");
        StringBuilder builder = new StringBuilder();
        JSONWriter writer = new JSONWriter(builder);
        writer.object()
                .key("name")
                .value(world.getName())
                .endObject();
        writer.object()
                .key("seed")
                .value(world.getSeed())
                .endObject();

        FileWriter fileWriter = new FileWriter(worldFile);
        fileWriter.write(builder.toString());
        fileWriter.flush();
        fileWriter.close();

        return true;
    }

    public static boolean writeChunk(World world, int x) throws IOException {
        File dir = new File(SAVES_PATH, world.getName());
        if (!dir.exists()) return false;

        int[] chunk = world.getChunk(x);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chunk.length; i++) {
            builder.append(chunk[i]).append('\n');
        }
        File worldFile = new File(dir, "chunk_" + x + ".txt");
        FileWriter fileWriter = new FileWriter(worldFile);
        fileWriter.write(builder.toString());
        fileWriter.flush();
        fileWriter.close();
        return true;
    }


    pu
}
