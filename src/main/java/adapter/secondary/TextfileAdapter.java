package adapter.secondary;

import ports.outbound.Files;

import java.io.*;
import java.util.ArrayList;

public class TextfileAdapter implements Files {

    private OutputConsole outCon = new OutputConsole();

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
            outCon.simpleMessage(filename + "was not found.");
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input;
    }

    @Override
    public void saveToFile(String filename, ArrayList<String> contents) {
        try {
            PrintWriter pw = new PrintWriter(filename);

            for (String entry : contents) {
                pw.println(entry);
                pw.flush();
            }
        } catch (FileNotFoundException e) {
            outCon.simpleMessage("Data could not be saved. File not found.");
        }
    }
}
