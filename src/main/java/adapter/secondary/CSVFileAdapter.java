package adapter.secondary;

import ports.outbound.Files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVFileAdapter implements Files {
    private OutputConsole outCon=new OutputConsole();

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
            outCon.errorSelfMessage(filename + "was not found.");
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
