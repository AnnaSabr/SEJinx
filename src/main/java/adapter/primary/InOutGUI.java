package adapter.primary;
import entities.GUI;
import entities.Table;
import ports.inbound.MessageInput;
import ports.outbound.MessageOutput;

public class InOutGUI implements MessageOutput,MessageInput{
    private GUI gui;

    public InOutGUI(GUI gui){
        this.gui=gui;
    }


    @Override
    public void simpleMessage(String text) {

    }

    @Override
    public void logKiPlayer(String name, String text) {

    }

    @Override
    public void exceptionMessage(String text) {

    }

    @Override
    public void errorSelfMessage(String text) {

    }

    @Override
    public void loggerMessage(String text) {

    }

    @Override
    public void jinxMessage(String text) {

    }

    @Override
    public void tablePicture(Table table) {

    }

    @Override
    public String inputAnything() {
        return null;
    }

    @Override
    public int inputINTTime() {
        return 0;
    }

    @Override
    public String letterInput() {
        return null;
    }

    @Override
    public int inputINTPlayerInitialization() {
        return 0;
    }

    @Override
    public String inputName() {
        return null;
    }

    @Override
    public String inputLevel() {
        return null;
    }

    @Override
    public String inputPasswort() {
        return null;
    }

    @Override
    public int inputINT() {
        return 0;
    }

    @Override
    public String inputCoord() {
        return null;
    }
}
