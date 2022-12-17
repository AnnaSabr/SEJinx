package ports.outbound;

import java.util.ArrayList;

/**
 * Port for output to files
 * */
public interface Files {

    /**
     * reads a file
     *
     * @param filename
     */
    ArrayList<String> getFileInput(String filename);

    /**
     * saves contents in a file
     *
     * @param filename
     */
    void saveToFile(String filename, ArrayList<String> contents);
}
