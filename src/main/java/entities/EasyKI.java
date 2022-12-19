package entities;

import actions.Zuege.Action;
import actions.Zuege.MoveHistory;
import actions.Zuege.Moves;
import adapter.secondary.OutputConsole;
import adapter.secondary.TextfileAdapter;
import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import persistence.DBConnector;
import persistence.PlayerHistory;
import java.util.ArrayList;
/**
 * Class to define the first Level KI easy
 */
public class EasyKI extends Player{

    private OutputConsole outCon;

    /**
     * Constructor for a new Easy AI
     * @param name name for AI
     * @param sleepTime time between messages
     * @param manualNextMsg if the player should be able to control the flow
     * @param database if the AI should be loaded from database
     * */
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


    /**
     * Function to load an AI from the database
     * */
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

    /**
     * Function to let the AI chose its next move
     * @param table not used by the easy AI
     * @return next action as string
     * */
    @Override
    public String chooseAction(Table table){
        log("Your turn " + this.name + "! Eye count - " + this.diceCount);
        log(this.toString());

        //roll if not rolled yet
        if(this.rolls == 0){
            outCon.logKiPlayer(this.getName(), "[AI], i didnt roll the dice yet!");
            return "R";
        }
        //check if there is a card that can be picked, if not and rolls are available --> roll
        if(cardAvailable(table) == null && this.rolls < 2){
            outCon.logKiPlayer(this.getName(),"[AI], there is no card i want...I´ll roll again!");
            return "R";
        }

        outCon.logKiPlayer(this.getName(),"[AI], i will choose a card now!");
        //nothing left to do, possibly end round
        return "C";

    }

    /**
     *  checks if a card with the same number as dicecount is on the table
     * @param table table and cards from this round
     * @return the card or null if not
     */
    private Card cardAvailable(Table table){
        Card[][] cards= table.getField();
        for (int a=0; a<cards.length; a++){
            for (int b=0; b<cards[0].length; b++){
                Card c= table.checkCard(a,b);
                if (c!=null){
                    if (c.getValue()==this.diceCount){
                        outCon.logKiPlayer(this.getName(),c.getValue()+"");
                        return c;
                    }
                }
            }
        }
        outCon.logKiPlayer(this.getName(),"Chooses the first Card wich was compatible.");
        return null;
    }

    /**
     * Function to let the AI choose a card from the table
     * Easy AI chooses the frist best card it can find
     * @param table current state of the game
     * @return true if card was found, false if not
     * */
    @Override
    public boolean chooseCard(Table table){
        //check if the AI has to end its turn because it has no options to pick a card
        if(checkEndRound(table)){
           log(this.name + "[AI], there is no card you could choose!");
            Card placeholder = new Card(CardColor.RED, 420);
            Action action6 = new Action(Moves.SKIPPED, placeholder, this);
            MoveHistory.addNewAction(action6);
            //set AI inactive to end its turn;
            this.active = false;
            return false;
        }
        //find the best possible card to pick from the field
        Card chosenOne = cardAvailable(table);

        //add card to AIs hand and remove it from field
        addCard(table.getCard(chosenOne));
        Action action1 = new Action(Moves.GOTCARDFROMTABLE, chosenOne, this);
        MoveHistory.addNewAction(action1);
        //set the AI as inactive, since this action ended its turn
        this.active = false;
        //signal that the AI has chosen a card successfully
        return true;
    }

    /**
     * Function to let the Ai choose its highest card if round ends
     * @return always false
     * */
    @Override
    public boolean selectHighCard(){
        Card away= new Card(CardColor.BLUE,0);
        if (this.cards.size()!=0){
            for (Card c:this.cards){
                if (c.getValue()>=away.getValue()){
                    away=c;
                }
            }
            Action action4 = new Action(Moves.DROPPEDCARD, away, this);
            MoveHistory.addNewAction(action4);
            this.cards.remove(away);
        }
        return false;
    }

    /**
     * Funtion to let the Easy AI draw a luck card
     * Easy Ai never chooses to draw a luck card
     * @param table current table
     * @param players opponents of AI
     * @return always false
     * */
    @Override
    public boolean drawLuckCard(Table table, Player[] players){
        outCon.logKiPlayer(this.getName(),"[AI], i would never waste points for a luck card!");
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
