package io.show.storage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JSONReader {
    public static void main(String[] args) {
        JSONReader d = new JSONReader();
        d.m_File = new File("res/textures/textures.json");
        d.read();

    }

    private File m_File;

    public JSONObject read() {
        String fileContent = "";
        try (BufferedReader br = new BufferedReader(new FileReader(m_File))) {

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

        return jsonObj;


    }


}
