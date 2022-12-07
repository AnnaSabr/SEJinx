package entities;

import cards.Card;
import cards.CardColor;
import persistence.DBConnector;
import persistence.PlayerHistory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EasyKI extends Player{

    private OutputConsole outCon;
    /**
     * class to define die first Level KI easy
     * @param name name of KI
     */
    public EasyKI (String name, int sleepTime, boolean manualNextMsg, boolean database){
        super(name,sleepTime,manualNextMsg,database);
        if(database){
            String DBName="AILevel1";
            String password="aipassword1";
            if(!DBConnector.getInstance().checkPlayer("AILevel1")){
                DBConnector.getInstance().createPlayer(DBName,password);
            }
            this.loadHistoryFromDB();
        }
        this.outCon=new OutputConsole();
    }

    public void loadHistoryFromDB(){
        DBConnector connector=DBConnector.getInstance();
        PlayerHistory[] playerHistories = connector.getPlayerHistory("AILevel1");
        if(playerHistories!=null) {
            for (PlayerHistory ph : playerHistories) {
                String historyString = this.name + "," + ph.getPlayer().getScore() + "," + ph.getLuckCardCount() + "," + ph.getDate()+",";
                for (Player p : ph.getEnemys()) {
                    historyString = historyString + ph.getPlayer().name + ":" + ph.getPlayer().getScore() + "/";
                }
                this.history.add(historyString);
            }
        }
    }

    @Override
    public String chooseAction(Table table){
        outCon.jinxMessage("Your turn " + this.name + "! Eye count - " + this.diceCount);
        outCon.jinxMessage(this.toString());

        //roll if not rolled yet
        if(this.rolls == 0){
            outCon.jinxMessage(this.name + "[AI], i didnt roll the dice yet!");
            return "R";
        }
        //check if there is a card that can be picked, if not and rolls are available --> roll
        if(cardAvailable(table) == null && this.rolls < 2){
            outCon.jinxMessage(this.name + "[AI], there is no card i want...I´ll roll again!");
            return "R";
        }

        outCon.jinxMessage(this.name + "[AI], i will choose a card now!");
        //nothing left to do, possibly end round
        return "C";

    }

    /**
     *  checks if a card with the same number as dicecount is on the table
     * @param table table and cards from this round
     * @return the card or null if not
     */
    private Card cardAvailable(Table table){
        Card[][] karten= table.getField();
        for (int a=0; a<karten.length; a++){
            for (int b=0; b<karten[0].length; b++){
                Card c= table.checkCard(a,b);
                if (c!=null){
                    if (c.getValue()==this.diceCount){
                        outCon.simpleMessage(c.getValue()+"");
                        return c;
                    }
                }
            }
        }
        outCon.simpleMessage("Chooses the first Card wich was compatible.");
        return null;
    }

    @Override
    public boolean chooseCard(Table table){
        //check if the AI has to end its turn because it has no options to pick a card
        if(checkEndRound(table)){
           outCon.jinxMessage(this.name + "[AI], there is no card you could choose!");
            //set AI inactive to end its turn;
            this.active = false;
            return false;
        }
        //find the best possible card to pick from the field
        Card chosenOne = cardAvailable(table);

        //add card to AIs hand and remove it from field
        addCard(table.getCard(chosenOne));
        //set the AI as inactive, since this action ended its turn
        this.active = false;
        //signal that the AI has chosen a card successfully
        return true;
    }

    @Override
    public boolean selectHighCard(){
        Card away= new Card(CardColor.BLUE,0);
        if (this.cards.size()!=0){
            for (Card c:this.cards){
                if (c.getValue()>=away.getValue()){
                    away=c;
                }
            }
            this.cards.remove(away);
        }
        return false;
    }

    @Override
    public boolean drawLuckCard(Table table, Player[] players){
        outCon.jinxMessage(this.name + "[AI], i would never waste points for a luck card!");
        return false;
    }

    /**
     * adds previous histories of this player to arraylist history
     */
    public void loadHistoryFromFile(){

        TextfileAdapter textfileAdapter=new TextfileAdapter();
        ArrayList<String> historiesFromFile=textfileAdapter.getFileInput("src/main/java/entities/playerHistories.txt");

        for(String entry:historiesFromFile){
            String[] a=entry.split(",");
            if(a[0].equals("AILevel1")){
                this.history.add(entry);
            }
        }
    }
}
