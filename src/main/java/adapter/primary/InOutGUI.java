package adapter.primary;
import actions.ReUnDo.Round;
import entities.GUI;
import entities.Table;
import ports.inbound.MessageInput;
import ports.outbound.MessageOutput;

public class InOutGUI implements MessageOutput,MessageInput{
    private GUI gui;

    private Round model;

    public InOutGUI(GUI gui){
        this.gui=gui;
        this.model = new Round();
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

    /**
     * Updates the model with the new representation of the table
     * */
    @Override
    public void tablePicture(Table table) {
        this.model.setTableStatus(table);
        gui.updateGUI(this.model);
    }

    @Override
    public String inputAnything() {
        return "";
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
    public int inputINTPlayerInitialization(String question) {
        gui.inputNumber(question);
        int players =gui.getInputNumber(question,2,4);
        return players;
    }

    @Override
    public String inputName(String question) {
        gui.getProfileName(question);
        String name=gui.returnProfile(question);
        return name;
    }

    @Override
    public String inputLevel() {
        gui.buildAI();
        String[]ki=gui.AIsettings();
        String ready=ki[0]+","+ki[1];
        return ready;
    }

    @Override
    public String inputPasswort(String question) {
        gui.getProfileName(question);
        String passwort=gui.returnProfile(question);
        return passwort;
    }

    @Override
    public int inputINT() {
        return 0;
    }

    @Override
    public String inputCoord() {
        return null;
    }

    @Override
    public String yesNo(String text) {
        gui.yesOrNo(text);
        boolean yesNo=gui.returningYesOrNO(text);
        if (yesNo){
            return "y";
        }
        return "n";
    }


}
