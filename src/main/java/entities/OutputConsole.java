package entities;

public class OutputConsole implements MessageOutput{

    @Override
    public void simpleMessage(String text) {
        System.out.println(text);
    }

    @Override
    public void jinxMessage(String text) {
        System.out.println("[JINX]\n"+text);
    }

    @Override
    public void loggerMessage(String text) {
        System.out.println("INFO:\n"+text);
    }
}
