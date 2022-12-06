package entities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVFileAdapter implements Files{

    @Override
    public ArrayList<String> getFileInput(String filename) {

        ArrayList<String> input=new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String line = br.readLine();

            while (line != null) {
                input.add(line);
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            System.out.println(filename + "was not found.");
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input;
    }

    @Override
    public void saveToFile(String filename, ArrayList<String> contents) {
        //nothing is saved to csvFile
    }
}
