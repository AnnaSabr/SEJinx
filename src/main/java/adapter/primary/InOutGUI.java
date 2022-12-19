package adapter.primary;
import actions.ReUnDo.Round;
import actions.ReUnDo.cards.Card;
import entities.GUI;
import entities.Player;
import entities.Table;
import ports.inbound.MessageInput;
import ports.outbound.MessageOutput;

import java.util.ArrayList;
import java.util.Scanner;

public class InOutGUI implements MessageOutput,MessageInput{
    private GUI gui;

    private Round model;

    public InOutGUI(GUI gui){
        this.gui=gui;
        this.model = new Round();
    }


    @Override
    public void simpleMessage(String text) {
        gui.showAdvice(text, "JINX");
    }

    @Override
    public void helpMessage(String text) {
        gui.showAdvice(text, "ADVISOR");
    }

    @Override
    public void logKiPlayer(String name, String text) {
        gui.showAdvice(name+text, "[KI]" + name );
    }

    @Override
    public void exceptionMessage(String text) {
        simpleMessage(text);
    }

    @Override
    public void errorSelfMessage(String text) {
        simpleMessage(text);
    }

    @Override
    public void loggerMessage(String text) {
        simpleMessage(text);
    }

    @Override
    public void jinxMessage(String text) {
        gui.updateGUI(this.model, new String[]{text});
    }

    /**
     * Updates the model with the new representation of the table
     * */
    @Override
    public void tablePicture(Table table, Round current) {
        current.setTableStatus(table);
        this.model = current;
        gui.updateGUI(this.model, new String[]{"Make your next move!"});
    }

    @Override
    public String inputAnything() {
        gui.showAdvice("Next Move","MANUAL");
        return "";
    }

    @Override
    public int inputINTTime() {
        return gui.getInputNumber("How long should the time between messages be? [ms]");
    }

    @Override
    public String letterInput(String msg) {
        return gui.actionChosen(model, new String[]{msg});
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
    public int inputINT(int min, int max) {
        return gui.getInputNumber("Input a Number", min, max);
    }

    @Override
    public int luckCardInput(){
        return gui.getChosenLuckcard();
    }

    @Override
    public int inputINTDrawLuckCard(Player p) {
        model.setActive(p);
        gui.updateGUI(model,new String[]{"Select a card to drop for a luck card!"});
        return gui.pickCardFromHandToDrop();
    }

    @Override
    public int inputMaxCard(ArrayList<Card> maxCards) {
        model.getActive().setCards(maxCards);
        gui.updateGUI(model, new String[]{"Drop your highest card!"});
        return gui.pickCardFromHandToDrop();
    }

    @Override
    public String inputCoord(String question) {
        return gui.tableGui.chosenCardCoord;
    }

    /**
     * To input a coordinate
     * @return coordinate as string (x,y)
     * */
    public String inputMultipleCoords(String question){

        String coords = "";

        Round before = new Round();

        before.setTableStatus(model.getTableStatus());
        before.setActive(model.getActive());
        before.setHighscores(model.getHighscores());
        before.setBehind(model.getBehind());
        before.setBefore(model.getBefore());

        while(true) {
            String action = gui.actionChosen(before, new String[]{"Select multiple cards you wish to draw!"});
            String tempCoords = "";
            if ("C".equals(action)) {
                coords = coords + gui.getChosenCardCoord() + ";";
                tempCoords = gui.getChosenCardCoord();
                int x = Integer.parseInt(tempCoords.split(",")[0]) - 1;
                int y = Integer.parseInt(tempCoords.split(",")[1]) - 1;
                before.getTableStatus().getField()[x][y] = null;
            } else {
                if (coords.equals("")) {
                    gui.updateGUI(model,new String[]{"Bla"});
                    return "0";
                }else{
                    gui.updateGUI(model,new String[]{"Bla"});
                    return coords.substring(0, coords.length() - 1);
                }
            }
        }
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

    /**
     * Function to display config feedback on console
     * */
    @Override
    public void configJinxMessage(String text){
        gui.showAdvice(text,"JINX");
    }

    @Override
    public String menueInput(String text) {
        return gui.actionChosen(model, new String[]{text});
    }

    /**
     * Function to display simple text
     * !Not used in GUI!
     * */
    @Override
    public void simpleText(String text) {
        System.out.println(text);
    }

    /**
     * Function to display a message with a manual delay
     * @param text should be like [ENTER] - Next move
     * */
    public void manualMessage(String text){
        gui.showAdvice("Next Move","MANUAL");
    }

}
