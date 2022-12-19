package adapter.secondary;

import ports.outbound.Files;

import java.io.*;
import java.util.ArrayList;

/**
 * Secondary adapter to get and store information from/in a file
 */
public class TextfileAdapter implements Files {

    private OutputConsole outCon = new OutputConsole();

    /**
     * Function to get input from a file
     *
     * @param filename path of the file to be read
     * @return ArrayList\<String\> of the contents
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
     * Function to save information to a file
     *
     * @param filename location where information should be saved
     * @param contents contents to be stored in the file
     */
    @Override
    public void saveToFile(String filename, ArrayList<String> contents) {
        try {
            PrintWriter pw = new PrintWriter(filename);

            for (String entry : contents) {
                pw.println(entry);
                pw.flush();
            }
        } catch (FileNotFoundException e) {
            outCon.errorSelfMessage("Data could not be saved. File not found.");
        }
    }
}
