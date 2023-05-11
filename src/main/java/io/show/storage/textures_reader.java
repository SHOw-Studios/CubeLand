package io.show.storage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class textures_reader {
    public static void main(String[] args) {
        String fileContent = "";
        try (BufferedReader br = new BufferedReader(new FileReader("res/textures/textures.json"))) {

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }


            fileContent = sb.toString();


        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        JSONObject jsonObj = new JSONObject(fileContent);


        ;

    }


}
