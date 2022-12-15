package adapter.secondary;

import entities.Table;
import ports.outbound.MessageOutput;

/**
 * Primary Adapter handling output on console
 * */
public class OutputConsole implements MessageOutput {

    /**
     * Displays a simple message on console
     * @param text text to be displayed
     * */
    @Override
    public void simpleMessage(String text) {
        System.out.println(text);
    }

    /**
     * Displays the field on the console
     * @param table the current state of the table
     * */
    @Override
    public void tablePicture(Table table) {
        System.out.println("\n"+table.toString());
    }

    /**
     * Displays moves of an ai on the console
     * @param name name of the AI
     * @param text the message to be displayed
     * */
    @Override
    public void logKiPlayer(String name,String text) {
        System.out.println(name+": "+text);
    }

    /**
     * Displays a message from the game on the console
     * @param text text to be displayed
     * */
    @Override
    public void jinxMessage(String text) {
        System.out.println("[JINX]\n"+text);
    }

    /**
     * Displays an info message
     * @param text text to be displayed
     * */
    @Override
    public void loggerMessage(String text) {
        System.out.println("INFO:\n"+text);
    }

    /**
     * Displays an error message on the console
     * @param text text to be displayed
     * */
    @Override
    public void errorSelfMessage(String text) {
        System.out.println(text);
    }

    /**
     * Displays an exception message on the console
     * @param text text to be displayed
     * */
    @Override
    public void exceptionMessage(String text) {
        System.out.println(text);
    }

}
