package adapter.secondary;

import ports.outbound.Files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Secondary Adapter handling input from files
 * Uses OutputAdapter to display information
 */
public class CSVFileAdapter implements Files {
    private OutputConsole outCon = new OutputConsole();

    /**
     * Reads a csv file for a config
     *
     * @return arrayList of Strings containing information for card stacks
     */
    @Override
    public ArrayList<String> getFileInput(String filename) {

        ArrayList<String> input = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String line = br.readLine();

            while (line != null) {
                input.add(line);
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            outCon.errorSelfMessage(filename + "was not found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input;
    }

    /**
     * Unused as nothing is saved in csv format
     */
    @Override
    public void saveToFile(String filename, ArrayList<String> contents) {
        //nothing is saved to csvFile
    }
}
