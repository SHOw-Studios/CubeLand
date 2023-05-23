package io.show.storage;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Vector;

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
    public static boolean writeWorld(String world){
        FileWriter writer = new FileWriter("world.txt");
        StringReader reader = new StringReader(world);
        reader.


    }
}
