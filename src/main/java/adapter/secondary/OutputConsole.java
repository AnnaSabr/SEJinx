package adapter.secondary;

import entities.Table;
import ports.outbound.MessageOutput;

public class OutputConsole implements MessageOutput {

    @Override
    public void simpleMessage(String text) {
        System.out.println(text);
    }


    @Override
    public void tablePicture(Table table) {
        System.out.println("\n"+table.toString());
    }

    @Override
    public void logKiPlayer(String name,String text) {
        System.out.println(name+": "+text);
    }
    @Override
    public void jinxMessage(String text) {
        System.out.println("[JINX]\n"+text);
    }
    @Override
    public void loggerMessage(String text) {
        System.out.println("INFO:\n"+text);
    }
    @Override
    public void errorSelfMessage(String text) {
        System.out.println(text);
    }
    @Override
    public void exceptionMessage(String text) {
        System.out.println(text);
    }
}
