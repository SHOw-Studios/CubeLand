package io.show.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Storage {

    public class FileCreator {
        public static void main(String args[]) {

            try {

                // Get the file
                File f = new File("D:\\example.txt");

                // Create new file
                // Check if it does not exist
                if (f.createNewFile())
                    System.out.println("File created");
                else
                    System.out.println("File already exists");
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }


}
