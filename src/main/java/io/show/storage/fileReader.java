package io.show.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class fileReader {
    public static void main(String[] args) {
        fileReader f = new fileReader();
        fileReader.read();

    }


    public static List read() {
        String fileContent = "";
        List<String> list = new Vector<>();
        try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {

            String line;


            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            if (list.isEmpty()) {
                System.out.println("liste_ist_leer");
            } else {
                System.out.println("liste_ist_gefüllt");

            }


        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);

        }

        return list;

    }


}
