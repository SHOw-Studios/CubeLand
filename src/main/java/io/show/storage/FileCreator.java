package io.show.storage;

import java.io.File;

public class FileCreator {

    public static void main(String args[]) {

        try {

            // Get the file
            File f = new File("C:\\Users\\hierl\\OneDrive\\Attachments\\Documents\\GitHub\\CubeLand\\src\\main\\java\\io\\show\\storage\\test.txt");

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


