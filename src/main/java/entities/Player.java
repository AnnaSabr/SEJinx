package entities;

import actions.Zuege.Action;
import actions.Zuege.Zuege;
import actions.Zuege.ZugHistorie;
import adapter.primary.InputConsole;
import adapter.secondary.OutputConsole;
import adapter.secondary.TextfileAdapter;
import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import actions.ReUnDo.cards.CardType;
import actions.ReUnDo.cards.LuckCard;
import persistence.DBConnector;
import persistence.PlayerHistory;
import java.util.*;

/**
 * Class representing a player
 * */
public class Player implements Cloneable{

    private InputConsole inCon;
    protected String name;

    protected ArrayList<Card> cards;
    protected ArrayList<LuckCard> luckCards;
    protected int score;
    //time between msgs and actions

    protected int sleepTime = 200;
    protected boolean manualNextMsg = true;
    ArrayList<String> history=new ArrayList<>();
    public int getDiceCount() {
        return diceCount;
    }

    public void setDiceCount(int diceCount) {
        this.diceCount = diceCount;
    }

    public int getRolls() {
        return rolls;
    }

    public void setRolls(int rolls) {
        this.rolls = rolls;
    }

    protected int diceCount = 0;

    //needs to be reset after each round

    protected int rolls = 0;
    protected ArrayList<LuckCard> usedCards = new ArrayList<LuckCard>();
    protected boolean active = true;

    //used to roll the dice

    Random rand = new Random();
    private OutputConsole outCon;

    /**
    * Overloaded Constructor to support sleeptimers
    * */
    public Player(String name, int sleepTime, boolean manualNextMsg, boolean database){
        this.inCon= new InputConsole();
        this.name = name;
        this.sleepTime = sleepTime;
        this.manualNextMsg = manualNextMsg;
        this.cards = new ArrayList<Card>();
        this.luckCards = new ArrayList<LuckCard>();
        if(database){
            if(!(this instanceof EasyKI) && !(this instanceof MediumAI) && !(this instanceof AIPLayer3)){
                //this.loadHistoryFromDB();
            }
        }else{
            this.loadHistoryFromFile();
        }
        this.outCon=new OutputConsole();
    }



    public void setName(String name) {
        this.name = name;
    }

    /**
     * Cloned eine ArrayListe
     *
     * @param alt Arraylist die Kopiert werden soll
     * @return geclonte Liste
     */
    public static ArrayList<Card> copyC(ArrayList<Card> alt) {
        if (alt == null) {
            return null;
        }
        ArrayList<Card> neu = (ArrayList<Card>) alt.clone();
        return neu;
    }

    /**
     * Cloned eine ArrayListe mit LuckyCards
     *
     * @param alt ArrayListe mit LuckyCards
     * @return geclonte Liste
     */
    public static ArrayList<LuckCard> copyL(ArrayList<LuckCard> alt) {
        if (alt == null) {
            return null;
        }
        ArrayList<LuckCard> neu = (ArrayList<LuckCard>) alt.clone();

        return neu;
    }

    /**
     * Aktualisiert die Karten auf der Spieler Hand
     *
     * @param handkarten neue Liste der Spielerkarten im besitz
     */
    public void setCards(ArrayList<Card> handkarten) {
        this.cards = copyC(handkarten);
    }

    /**
     * Aktualisiert die LuckyKarten des Spielers
     *
     * @param luckyKarten neue Liste vorhandener LuckyKarten
     */
    public void setLuckCards(ArrayList<LuckCard> luckyKarten) {
        this.luckCards = copyL(luckyKarten);
    }

    /**
     * returns current hand of player
     */
    public ArrayList<Card> getCards() {
        return this.cards;
    }

    /**
     * returns current luckcards of player
     */
    public ArrayList<LuckCard> getLuckCards() {
        return this.luckCards;
    }

    /**
     * returns players name
     */
    public String getName() {
        return this.name;
    }


    /**
     * Adds a card to the players hand
     */
    public void addCard(Card card) {
        this.cards.add(card);
    }

    /**
     * Adds a card to the players luck cards
     */
    public void addLuckCard(LuckCard luckCard) {
        this.luckCards.add(luckCard);
    }

    public void removeLuckCard(LuckCard luckCard) {
        this.luckCards.remove(luckCard);
    }

    /**
     * Removes a card from the players hand
     *
     * @param pos position of card in players hand, counting from 0!
     * @return null if no card was found otherwise card in position pos
     */
    public Card removeCard(int pos) {
        try {
            Card card = this.cards.get(pos);
            this.cards.remove(pos);
            return card;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("[ERROR] No Card found!");
            return null;
        }
    }

    /**
     * Overloaded removeCard function to remove a card by reference
     *
     * @param card card to be removed
     * @return true if card was remove, false if card was not found
     */
    public boolean removeCard(Card card) {
        try {
            this.cards.remove(card);
            return true;
        } catch (Exception e) {
            System.out.println("[ERROR] No Card found!");
            return false;
        }
    }

    /**
     * Calculates the current score of the player
     *
     * @return current score as int
     */
    public int getScore() {
        int score = this.score;

        for (Card c : this.cards) {
            score += c.getValue();
        }

        return score;
    }

    /**
     * Function to get the current score of the player
     * */
    public void setScore(int score){
        this.score = score;
    }

    /**
     * Function to let the player choose a card
     * Sets isActive according to the players action
     * Player chose a card --> isActive = false
     * Player wasnt able to choose a card --> isActive = false
     * Player didnt choose a card --> isActive = true
     *
     * @param table the current playing field
     * @return true if card was chosen, false if no card was chosen
     */
    public boolean chooseCard(Table table) {
        //check if player is able to choose a card
        if (this.diceCount <= 0) {
            log("Roll the dice first!");
            return false;
        }

        //check if the player has an option to choose from
        if (checkEndRound(table)) {
            log(this.name + ", there is no card you could choose!");
            Card halter = new Card(CardColor.RED, 420);
            Action action6 = new Action(Zuege.SKIPPED, halter, this);
            ZugHistorie.actionHinzufuegen(action6);
            //set the player as inactive to end his turn
            this.active = false;

            return false;
        }

        log("Which card would you like to take? Current diceCount: " + diceCount);
        log("Enter the cards position as y,x");

        int[] inputCoord = this.getPlayerInputCoord();

        try {
            //subtract one to get back to array counting
            int[] coords = {inputCoord[0] - 1, inputCoord[1] - 1};

            //get a card from the field
            Card chosenOne = table.getCard(coords[0], coords[1]);


            if (chosenOne == null) {
                log("There is no card at that position!");
                return false;
            } else if (chosenOne.getValue() != diceCount) {
                log("You can only choose a card equal to the value of your diceCount!");
                table.addCard(coords[0], coords[1], chosenOne);

                return false;
            }
            //add card to players hand
            addCard(chosenOne);
            Action action1 = new Action(Zuege.GOTCARDFROMTABLE, chosenOne, this);
            ZugHistorie.actionHinzufuegen(action1);
            //set the player as inactive, since this action ended his turn
            this.active = false;

            //signal that player has chosen a card successfully
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log("Choose a valid combination!");
            return false;
        }
    }

    /**
     * Function to check if the turn of a player has to end because he cant choose a card
     *
     * @param table the current instance of the table
     * @return true if player has no option, false if he does
     */
    protected boolean checkEndRound(Table table) {
        Card[][] field = table.getField();
        for (Card[] row : field) {
            for (Card c : row) {
                if (c != null && c.getValue() == this.diceCount) {
                    //there is a card the player can choose
                    return false;
                }
            }
        }
        //there is no card the player can choose --> end his turn
        return true;
    }

    /**
     * Lets the player draw a luckCard from the table
     *
     * @param table the current instance of the table
     * @return returns true if the player has chosen a luckCard, false if he didnt
     */
    public boolean drawLuckCard(Table table, Player[] players) {
        //check if player has cards on his hand to exchange for a luck card
        if (this.cards.size() == 0) {
            log(this.name + ", you dont have any cards to exchange for a luck card!");
            return false;
        }

        log(name + ", you can choose to draw a luck card!");
        Card selected = selectCard(players);

        if (selected == null) {
            log(this.name + ", you didnt choose a card");
            return false;
        } else {
            //remove the selected card from the players hand
            Action action2 = new Action(Zuege.DROPPEDCARD, selected, this);
            ZugHistorie.actionHinzufuegen(action2);
            this.cards.remove(selected);
            //add a luckCard to the players hand
            LuckCard drawn = table.drawLuckCard();
            //check if table has luckcards left
            if (drawn != null) {
                this.luckCards.add(drawn);
                Action action3 = new Action(Zuege.GOTLUCKYCARD, drawn, this);
                ZugHistorie.actionHinzufuegen(action3);

                return true;
            } else {
                log("The luck card stack has no more cards you can draw!");
                return false;
            }
        }
    }

    /**
     * Lets the player select a card from his hand
     * Doesnt remove the card from players hand!
     *
     * @return selected Card or null if no card was selected
     */
    public Card selectCard(Player[] players) {

        log(this.name + ", choose a card");
        log("Enter 0 to not choose a card");
        //List all cards available to choose
        for (int i = 1; i < cards.size() + 1; i++) {
            log(cards.get(i - 1) + " - " + i);
        }

        while (true) {
            int input = this.playerInputNumberInRange(1, this.cards.size());

            if (input == 0) {
                log("You have not chosen a card");
                return null;
            } else {
                return cards.get(input - 1);
            }
        }
    }

    /**
     *
     * @return die hoechste Karte auf der Hand
     */
    public boolean selectHighCard() {

        //check if the player is able to drop a card
        if (this.cards.size() == 0) {
            log(name + ", has no cards to drop after this round!");
            return false;
        }

        //look for highest card the player has!
        ArrayList<Card> maxCards = new ArrayList<>();
        int currentHigh = 0;
        Card[] hand = this.cards.toArray(new Card[0]);

        for (Card c : hand) {
            if (c.getValue() > currentHigh) {
                currentHigh = c.getValue();
            }
        }

        for (Card c : hand) {
            if (c.getValue() == currentHigh) {
                maxCards.add(c);
            }
        }

        log(this.name + ", you finished the round! Choose a card to drop!");
        //List all cards available to choose
        for (int i = 0; i < maxCards.size(); i++) {
            log(maxCards.get(i) + " - " + i);
        }

        int removing = this.playerInputNumberInRange(0, maxCards.size() - 1);
        while (true) {
            try {
                Card halter = maxCards.get(removing);
                this.cards.remove(maxCards.get(removing));
                Action action4 = new Action(Zuege.DROPPEDCARD, halter, this);
                ZugHistorie.actionHinzufuegen(action4);
                return true;
            } catch (Exception e) {
                log("Please choose a card!");
            }
            removing = this.playerInputNumberInRange(0, maxCards.size() - 1);
        }
    }


    /**
     * Lets the player select a luckCard from his hand
     * Doesnt remove the card from players hand!
     *
     * @return selected Card or null if no card was selected
     */
    public LuckCard selectLuckCard(Table table) {

        //check if player has luck cards
        if (this.luckCards.size() == 0) {
            log(name + ", you dont have any luck cards!");
            return null;
        }
        log(this.name + ", choose a card you wish to play");
        log("Enter 0 to not choose a card");
        //List all cards available to choose
        for (int i = 1; i < luckCards.size() + 1; i++) {
            log(luckCards.get(i - 1) + " - " + i);
        }

        while (true) {

            int input = this.playerInputNumberInRange(1, this.luckCards.size());

            if (input == 0) {
                log("You have not chosen a card to play!");
                return null;
            } else {
                return luckCards.get(input - 1);
            }
        }
    }

    /**
     * Lets the player choose an action he wants to perform
     *
     * @return returns the chosen action!
     */
    public String chooseAction(Table table) {

        String[] actions = {"R", "L", "C", "M", "N", "T", "H","S","Z","X","A","P"};

        while (true) {
            log("Your turn " + this.name + "! Eye count - " + this.diceCount);
            log(this.toString());

            //let player choose the action
            log("""
                    Choose your action!
                    R - Roll the Dice
                    L - Play a luck card
                    C - Choose a card - this might end the round!
                    M - Re or Undo
                    N - Verlauf anzeigen
                    H - Show all previous scores
                    S - save the Game
                    Z - to show  moves
                    X - load other game
                    A - Give advise
                    P - Show player's history
                    """);
            String action = inCon.inputConsole();
            //check if value is acceptable
            if (Arrays.asList(actions).contains(action)) {
                return action;
            } else {
                log("Please choose a valid option!");
            }
        }
    }

    /**
     * Lets the player roll the dice
     * Automaticly sets the players diceCount
     *
     * @return the number the player rolled, current DiceCount if nothing changed
     */
    public int roll() {
        if (this.rolls >= 2) {
            log("You can??t roll again! Your current eye count is " + diceCount);
            return this.diceCount;
        }

        //Player rolls the dice
        this.diceCount = rand.nextInt(6) + 1;
        //log action of player for rollbacks

        this.rolls++;

        log("You rolled a " + this.diceCount + "!");
        return this.diceCount;
    }


    /**
     * Function to perfrom a ONETOTHREE or FOURTOSIX luckCard action
     * !Removes the Card from the players hand!
     *
     * @param lC  the card the player chose
     * @param min the min value the player can choose
     * @param max the max value the player can choose
     * @return true if player selected a valid value and his diceCount was changed, false if he didnt
     */
    public boolean mintomax(LuckCard lC, int min, int max) {

        //check if the card has already been used
        if (usedCards.contains(lC)) {
            log("You have already played that card!");
            return false;
        }

        log("Which number do you wish to replace your eye count with? [" + min + "," + max + "] Enter 0 to abort!");

        int input = this.playerInputNumberInRange(min, max);

        if (input == 0) {
            return false;
        } else {
            //set diceCount to the input
            this.diceCount = input;

            //remove the card from the players hand --> single use
            removeLuckCard(lC);

            log(this.name + ", your new eye count is: " + this.diceCount);
            return true;
        }
    }


    public int playerInputNumberInRange(int min, int max) {
        String line = inCon.inputConsole();
        try {
            int newDiceCount = Integer.parseInt(line);
            if (newDiceCount <= max && newDiceCount >= min) {
                return newDiceCount;
            } else if (newDiceCount == 0) {
                return newDiceCount;
            } else {
                log("Enter a valid number!");
                return this.playerInputNumberInRange(min, max);
            }
        } catch (NumberFormatException e) {
            log("Please enter a number!");
            return playerInputNumberInRange(min, max);
        }
    }

    /**
     * Function to let the player perform an extra throw
     *
     * @param lC the card used by the player
     */
    public boolean extraThrow(LuckCard lC) {
        //check if the player still has rolls left
        if (rolls < 2) {
            log(this.name + ", you still have extra rolls!");
            return false;
        }

        //check if the player already played that card
        if (usedCards.contains(lC)) {
            log(name + ", you have already played that card!");
            return false;
        }

        //decrease roll count so that the player can roll again
        this.rolls--;
        //roll a new diceCount for the player
        roll();
        //add the card to the usedCards so the player cant play it again
        usedCards.add(lC);

        return true;
    }

    /**
     * Function to let the player subtract one of his diceCount
     *
     * @param lC the card used by the player
     */
    public boolean minusOne(LuckCard lC) {

        //check if the player rolled the dice already
        if (this.rolls <= 0) {
            log(name + ", roll the dice first!");
            return false;
        }

        //check if the subtraction makes sense
        if (this.diceCount <= 1) {
            log(name + ", this action wouldnt make much sense!");
            return false;
        }

        //check if the player already played that card
        if (usedCards.contains(lC)) {
            log(name + ", you have already played that card!");
            return false;
        }

        //reduce the players diceCount
        this.diceCount--;
        //add card to usedCards, so it cant be played twice
        usedCards.add(lC);

        log(name + ", your new eye count is: " + this.diceCount);
        return true;
    }

    /**
     * Function to let the player add one to his diceCount
     *
     * @param lC the card used by the player
     */
    public boolean plusOne(LuckCard lC) {

        //check if the player rolled the dice already
        if (this.rolls <= 0) {
            log(name + ", roll the dice first!");
            return false;
        }

        //check if the addition makes sense
        if (this.diceCount >= 6) {
            log(name + ", this action wouldnt make much sense!");
            return false;
        }

        //check if the player already played that card
        if (usedCards.contains(lC)) {
            log(name + ", you have already played that card!");
            return false;
        }

        //increase the players diceCount
        this.diceCount++;
        //add card to usedCards, so it cant be played twice
        usedCards.add(lC);

        log(name + ", your new eye count is: " + this.diceCount);
        return true;
    }

    /**
     * Function to let the player choose cards based on his eye count
     *
     * @param lC    card used by the player
     * @param table the current instance of the field
     */
    public boolean cardSum(LuckCard lC, Table table) {

        //check if the player has rolled the dice before
        if (rolls <= 0) {
            log(name + ", roll the dice first!");
            return false;
        }

        //check if the player already played that card
        if (usedCards.contains(lC)) {
            log(name + ", you have already played that card!");
            return false;
        }

        //tell player what to do
        log("You need to match your diceCount: " + diceCount);
        log("Enter all cards you want to select like this: y,x;y,x;y,x;...;y,x");
        log("Enter 0 if u dont want to choose any cards!");

        String input = this.getPlayerInputMultipleCoordinates(table);

        if (input.equals("0")) {
            log(name + ", you stopped the card selection!");
            return false;
        }

        int[][] coords = parseCoordinateInput(input);
        ArrayList<Card> selectedCards = new ArrayList<>();

        int sum = 0;
        if (coords != null) {
            for (int[] coord : coords) {
                Card card = table.getCard(coord[0], coord[1]);
                if (card != null) {
                    selectedCards.add(card);
                    sum += card.getValue();
                }
            }
            if (sum != diceCount) {
                log(name + ", this combination equals " + sum + ", however you need to match " + this.diceCount);
                // sum doesn't match, put all cards back on the table
                int z = 0;
                for (Card c : selectedCards) {
                    table.addCard(coords[z][0], coords[z][1], c);
                    z++;
                }
                return false;
            } else {
                // the sum matches the diceCount --> add all selected Cards to players hand!
                this.cards.addAll(selectedCards);

                //set player as inactive, since his turn is over
                this.active = false;

                //add the card to usedCards so the player cant play it again
                this.usedCards.add(lC);

                return true;
            }
        } else {
            log(name + ", you entered a wrong format!");
            return false;
        }
    }

    /**
     * Function to parse a string of coordinates into a 2D integer array
     *
     * @param input array like y,x;y,x;...;y,x
     * @return int[][] of coord-pairs, null if error
     */
    private int[][] parseCoordinateInput(String input) {
        try {
            //split string into coordinate segments
            String[] coordPair = input.split(";");
            //init int[][] with correct dimensions
            int[][] ret = new int[coordPair.length][2];
            int i = 0;
            for (String s : coordPair) {
                //split coordinate segements into coordinates
                String[] coordSTR = s.split(",");
                //store coordinate as pairs, account for array counting
                ret[i][0] = Integer.parseInt(coordSTR[0]) - 1;
                ret[i][1] = Integer.parseInt(coordSTR[1]) - 1;
                i++;
            }
            return ret;
        } catch (Exception e) {
            //return null if something went wrong
            return null;
        }
    }

    /**
     * player chooses an option from menu
     *
     * @return
     */
    public String getPlayerInputMenu() {
        String line = inCon.inputConsole();
        if ((!line.equals("C")) && (!line.equals("L")) && (!line.equals("R") && !line.equals("M") && (!line.equals("N")) && (!line.equals("T")) && (!line.equals("H")))) {
            log("Try again!");
            return this.getPlayerInputMenu();
        }
        this.playerlog(line);
        return line;

    }

    /**
     * player can enter coordinates of card on table
     *
     * @return
     */
    public int[] getPlayerInputCoord() {
        String line = inCon.inputConsole();
        String[] coordsSTR = line.split(",");
        try {
            if (Integer.valueOf(coordsSTR[0]) > 4 || Integer.valueOf(coordsSTR[0]) < 1) {
                log("Enter valid coordinates");
                return this.getPlayerInputCoord();
            } else if (Integer.valueOf(coordsSTR[1]) > 4 || Integer.valueOf(coordsSTR[1]) < 1) {
                log("Enter valid coordinates");
                return this.getPlayerInputCoord();
            }
        } catch (NumberFormatException e) {
            log("Enter coordinates in format y,x!");
            return this.getPlayerInputCoord();
        }
        String coord = coordsSTR[0] + "," + coordsSTR[1];
        this.playerlog(coord);
        int[] coordInt = new int[2];
        coordInt[0] = Integer.parseInt(coordsSTR[0]);
        coordInt[1] = Integer.parseInt(coordsSTR[1]);
        return coordInt;

    }


    /**
     * player chooses yes or no
     *
     * @return
     */
    public String getPlayerInputYesNo() {
        String line = inCon.inputConsole();
        if ((!line.equals("y")) && (!line.equals("n"))) {
            log("Enter y or n!");
            return this.getPlayerInputYesNo();
        }
        return line;

    }

    /**
     * player can enter multiple coordinates
     *
     * @return
     */
    public String getPlayerInputMultipleCoordinates(Table table) {
        String line = inCon.inputConsole();
        if ((!line.equals("0"))) {
            String[] coord = line.split(";");
            for (String c : coord) {
                try {
                    if (!(Integer.parseInt(String.valueOf(c.charAt(0))) <= 4 && Integer.parseInt(String.valueOf(c.charAt(0))) > 0 && String.valueOf(c.charAt(1)) == "," && Integer.parseInt(String.valueOf(c.charAt(2))) <= 4) && Integer.parseInt(String.valueOf(c.charAt(2))) > 0) {
                        log("Enter valid coordinates or type 0");
                        return this.getPlayerInputMultipleCoordinates(table);
                    }
                } catch (NumberFormatException e) {
                    log("Please enter coordinates in a valid format or type 0");
                    return this.getPlayerInputMultipleCoordinates(table);
                }
            }
        }
        this.playerlog(line);
        return line;

    }


    /**
     * Function to get input of player as INT
     */
    public int getPlayerInputINT(int min, int max) {
        while (true) {
            try {
                int ret = inCon.inputConsoleINT();
                if (ret > max || ret < min) {
                    log("Choose a number in the specified range!" + "[" + min + "," + max + "]");
                } else {
                    return ret;
                }
            } catch (Exception e) {
                log("Enter a valid Number!");
                //read line out of stream to clear it
                inCon.inputConsole();
            }
        }
    }

    /**
     * Function to easily log a msg on the console
     */
    void log(String msg) {
        if (manualNextMsg) {
            outCon.simpleMessage("[JINX] " + msg + " [ENTER] to continue!");
            inCon.inputConsole();
        } else {
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                outCon.simpleMessage("Sleep exception!");
            }
            outCon.jinxMessage(msg);
        }
    }

    /**
     * Function to see if player is still active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Function to set the player in active status
     */
    public void setActive() {
        this.active = true;
    }

    /**
     * Function to clear the used cards
     */
    public void clearUsedCards() {
        this.usedCards.clear();
    }

    /**
     * Function to reset the rolls and the dice count
     */
    public void resetRolls() {
        this.rolls = 0;
        this.diceCount = 0;
    }

    /* shows player input on console
     *
     * @param msg
     */
    private void playerlog(String msg) {
        outCon.simpleMessage("["+this.getName() + "] chose "+msg);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("");

        if (this.cards.size() == 0 && this.luckCards.size() == 0) {
            return this.name + "\n[]\n[]";
        }

        ret.append(this.name);
        ret.append("\n");
        ret.append("[");
        for (int i = 0; i < this.cards.size(); i++) {
            ret.append(cards.get(i).toString());
            if (i < this.cards.size() - 1) {
                ret.append(",");
            }
        }
        ret.append("]\n");
        ret.append(" [");
        for (int i = 0; i < this.luckCards.size(); i++) {
            ret.append(luckCards.get(i).toString());
            if (i < this.luckCards.size() - 1) {
                ret.append(",");
            }
        }
        ret.append("]\n");

        return ret.toString();
    }

    /**
     * prints advice for player on console
     *
     * @param msg
     */
    private void adviceLog(String msg) {
        outCon.simpleMessage("[Advisor]" + msg);
    }

    /**
     * gives advice to the player
     */
    public void getHelp(Table table) {
        if (this.diceCount == 0) {
            this.adviceLog("First you need to roll the dice!");
            return;
        }
        String[] cardcoords = this.findValidCards(table);
        if (cardcoords[0] == null) {
            this.adviceLog("You can't take any cards.");
            boolean rollAgain = false;
            for (LuckCard lc : this.getLuckCards()) {
                if (lc.getCardType().equals(CardType.EXTRATHROW) && !this.usedCards.contains(lc)) {
                    rollAgain = true;
                }
            }
            if (this.rolls < 2) {
                this.adviceLog("You should roll again!");
                return;
            } else if (rollAgain) {
                this.adviceLog("You can use your Extrathrow-Luckcard to roll again!");
                return;
            } else {
                this.adviceLog("You can't take any cards and you can't roll again. It looks like you have to end this round.");
                return;
            }
        } else if (cardcoords[1] == null) {
            this.adviceLog("There is only one card you can take, so you should take it.");
            return;
        } else {
            this.adviceLog("These are the coordinates you can chose:");
            for (String coord : cardcoords) {
                if (coord != null) {
                    this.adviceLog(coord);
                    String[] cardcoord = coord.split(",");
                    Card c = table.getField()[Integer.parseInt(cardcoord[0]) - 1][Integer.parseInt(cardcoord[1]) - 1];
                    if (c.getValue() == this.diceCount) {
                        this.adviceLog("You can just take this card.");
                    } else {
                        this.adviceLog("If you'd like to take this card you need to use a Luckcard.");
                        if (c.getValue() > this.diceCount) {
                            this.adviceLog("You need to increase your dice count.");
                        } else if (c.getValue() < this.diceCount) {
                            this.adviceLog("You need to decrease your dice count.");
                        }
                    }
                }
            }
            CardColor[] desiredColour = new CardColor[8];
            //if player has no cards
            if (this.cards.size() == 0) {
                int[] coloursOnTable = this.findAmountByColour(table);
                int amount = 100;
                int c = 0;
                //gets index of smallest amount of cards of a colour
                for (int b = 0; b < 8; b++) {
                    for (int a = 0; a < coloursOnTable.length; a++) {
                        if (coloursOnTable[a] < amount || amount == 0) {
                            amount = coloursOnTable[a];
                            c = a;
                        }
                    }
                    coloursOnTable[c] = 100;
                    CardColor adding = CardColor.GREEN;
                    switch (c) {
                        case 0:
                            adding = CardColor.RED;
                            break;
                        case 1:
                            adding = CardColor.GREEN;
                            break;
                        case 2:
                            adding = CardColor.BLUE;
                            break;
                        case 3:
                            adding = CardColor.YELLOW;
                            break;
                        case 4:
                            adding = CardColor.PURPLE;
                            break;
                        case 5:
                            adding = CardColor.ORANGE;
                            break;
                        case 6:
                            adding = CardColor.GREY;
                            break;
                        case 7:
                            adding = CardColor.WHITE;
                            break;
                    }
                    desiredColour[b] = adding;
                    amount = 100;
                }
            }
            //if player has cards
            else {
                int[] valueByColour = new int[8];
                for (Card c : this.cards) {
                    switch (c.getColor()) {
                        case RED -> valueByColour[0] = valueByColour[0] + c.getValue();
                        case GREEN -> valueByColour[1] = valueByColour[1] + c.getValue();
                        case BLUE -> valueByColour[2] = valueByColour[2] + c.getValue();
                        case YELLOW -> valueByColour[3] = valueByColour[3] + c.getValue();
                        case PURPLE -> valueByColour[4] = valueByColour[4] + c.getValue();
                        case ORANGE -> valueByColour[5] = valueByColour[5] + c.getValue();
                        case GREY -> valueByColour[6] = valueByColour[6] + c.getValue();
                        case WHITE -> valueByColour[7] = valueByColour[7] + c.getValue();
                    }
                }
                int val = -1;
                int c = 0;
                //order card colours by value in hand
                for (int a = 0; a < 8; a++) {
                    for (int b = 0; b < 8; b++) {
                        if (valueByColour[b] > val) {
                            val = valueByColour[b];
                            c = b;
                        }
                    }
                    valueByColour[c] = -2;
                    val = -1;
                    CardColor cardColor = CardColor.GREEN;
                    switch (c) {
                        case 0:
                            cardColor = CardColor.RED;
                            break;
                        case 1:
                            cardColor = CardColor.GREEN;
                            break;
                        case 2:
                            cardColor = CardColor.BLUE;
                            break;
                        case 3:
                            cardColor = CardColor.YELLOW;
                            break;
                        case 4:
                            cardColor = CardColor.PURPLE;
                            break;
                        case 5:
                            cardColor = CardColor.ORANGE;
                            break;
                        case 6:
                            cardColor = CardColor.GREY;
                            break;
                        case 7:
                            cardColor = CardColor.WHITE;
                            break;
                    }
                    desiredColour[a] = cardColor;
                }
            }
            for (CardColor cardColor : desiredColour) {
                for (String co : cardcoords) {
                    if (co != null) {
                        String[] line = co.split(",");
                        Card c = table.getField()[Integer.parseInt(line[0]) - 1][Integer.parseInt(line[1]) - 1];
                        if (c != null) {
                            if (c.getColor().equals(cardColor)) {
                                if (this.cards.size() == 0) {
                                    this.adviceLog("I recommend taking " + co + ".");
                                    this.adviceLog("Based on the cards on the table this is the card you are most likely to keep.");
                                    return;
                                } else {
                                    boolean cardInHand = false;
                                    for (Card card : this.cards) {
                                        if (card.getColor().equals(cardColor)) {
                                            cardInHand = true;
                                        }
                                    }
                                    if (cardInHand) {
                                        this.adviceLog("I recommend taking " + co + ".");
                                        this.adviceLog("Based on the cards you already have, this is the best card you can take in order to keep a high score.");
                                        return;
                                    }
                                    CardColor[] prefferedColour = new CardColor[8];
                                    int[] coloursOnTable = this.findAmountByColour(table);
                                    int amount = 100;
                                    int d = 0;
                                    //gets index of smallest amount of cards of a colour
                                    for (int b = 0; b < 8; b++) {
                                        for (int a = 0; a < coloursOnTable.length; a++) {
                                            if (coloursOnTable[a] < amount) {
                                                amount = coloursOnTable[a];
                                                d = a;
                                            }
                                        }
                                        coloursOnTable[d] = 200;
                                        CardColor adding = CardColor.GREEN;
                                        switch (d) {
                                            case 0:
                                                adding = CardColor.RED;
                                                break;
                                            case 1:
                                                adding = CardColor.GREEN;
                                                break;
                                            case 2:
                                                adding = CardColor.BLUE;
                                                break;
                                            case 3:
                                                adding = CardColor.YELLOW;
                                                break;
                                            case 4:
                                                adding = CardColor.PURPLE;
                                                break;
                                            case 5:
                                                adding = CardColor.ORANGE;
                                                break;
                                            case 6:
                                                adding = CardColor.GREY;
                                                break;
                                            case 7:
                                                adding = CardColor.WHITE;
                                                break;
                                        }
                                        prefferedColour[b] = adding;
                                        amount = 100;
                                    }
                                    for (CardColor cardColor1 : prefferedColour) {
                                        for (String coCard : cardcoords) {
                                            if (coCard == null) {
                                                break;
                                            }
                                            String[] line2 = coCard.split(",");
                                            Card c2 = table.getField()[Integer.parseInt(line2[0]) - 1][Integer.parseInt(line2[1]) - 1];
                                            if (c2 != null) {
                                                if (c2.getColor().equals(cardColor1)) {
                                                    this.adviceLog("I recommend taking " + coCard + ".");
                                                    this.adviceLog("You can't take a card to protect your score, so you should take this one because there are few cards of this color on the table.");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * gets the amount of cards on table by colour
     * in order red, green, blue, yellow, purple, orange, grey, white
     *
     * @param table
     * @return
     */
    public int[] findAmountByColour(Table table) {
        int[] cardByColour = new int[8];
        for (Card[] card : table.getField()) {
            for (Card c : card) {
                if (c != null) {
                    switch (c.getColor()) {
                        case RED -> cardByColour[0]++;
                        case GREEN -> cardByColour[1]++;
                        case BLUE -> cardByColour[2]++;
                        case YELLOW -> cardByColour[3]++;
                        case PURPLE -> cardByColour[4]++;
                        case ORANGE -> cardByColour[5]++;
                        case GREY -> cardByColour[6]++;
                        case WHITE -> cardByColour[7]++;
                    }
                }
            }
        }
        return cardByColour;
    }

    /**
     * finds all cards the AI could take
     *
     * @param table
     * @return
     */
    private String[] findValidCards(Table table) {
        boolean oneThree = false;
        boolean fourSix = false;
        for (LuckCard luckCard : this.getLuckCards()) {
            if (luckCard.getCardType().equals(CardType.ONETOTHREE)) {
                oneThree = true;
            } else if (luckCard.getCardType().equals(CardType.FOURTOSIX)) {
                fourSix = true;
            }
        }
        String[] cards = new String[16];
        int index = 0;
        int plus = 0;
        int minus = 0;
        for (LuckCard luckCard : this.getLuckCards()) {
            //compareTo returns 0 if they are equal
            if (luckCard.getCardType().compareTo(CardType.PLUSONE) == 0 && !this.usedCards.contains(luckCard)) {
                plus++;
            } else if (luckCard.getCardType().compareTo(CardType.MINUSONE) == 0 && !this.usedCards.contains(luckCard)) {
                minus++;
            }
        }
        //row, y-coordinate
        int ycoord = 1;
        for (Card[] card : table.getField()) {
            //x-coordinate
            int xcoord = 1;
            for (Card c : card) {
                if (c != null) {
                    if (this.diceCount == c.getValue()) {
                        cards[index] = ycoord + "," + xcoord;
                        index++;
                    } else if (oneThree && c.getValue() < 4) {
                        cards[index] = ycoord + "," + xcoord;
                        index++;
                    } else if (fourSix && c.getValue() > 3) {
                        cards[index] = ycoord + "," + xcoord;
                        index++;
                    } else {
                        int a = plus;
                        int b = minus;
                        //all cards that can be taken with plusone
                        while (a != 0) {
                            if (this.diceCount + a == c.getValue()) {
                                cards[index] = ycoord + "," + xcoord;
                                index++;
                            }
                            a--;
                        }
                        //all cards that can be taken with minusone
                        while (b != 0) {
                            if (this.diceCount - b == c.getValue()) {
                                cards[index] = ycoord + "," + xcoord;
                                index++;
                            }
                            b--;
                        }
                    }
                }
                xcoord++;
            }
            ycoord++;
        }
        return cards;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isManualNextMsg() {
        return manualNextMsg;
    }

    public void setManualNextMsg(boolean manualNextMsg) {
        this.manualNextMsg = manualNextMsg;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * shows player's history
     */
    public void showHistory(){
        if(this.history.size()==0){
            this.log("You do not have a history yet.");
            return;
        }
        int i=0;
        for(String line:this.history){
            i++;
            String[] a=line.split(",");
            String[] opponents=a[4].split("/");
            this.log(i+". Played by:"+name+" Score: "+a[1]+"\nused Luckcards: "+a[2]+"\nplayed on: "+a[3]+"\nPlayed against:");
            int c=0;
            for(String lines:opponents){
                c++;
                String[] b=lines.split(":");
                System.out.println(c+": "+b[0]+" scored "+b[1]);
            }
        }
        return;
    }

    /**
     * adds previous histories of this player to arraylist history
     */
    public void loadHistoryFromFile(){

        TextfileAdapter textfileAdapter=new TextfileAdapter();
        ArrayList<String> historiesFromFile=textfileAdapter.getFileInput("src/main/java/entities/playerHistories.txt");

        for(String entry:historiesFromFile){
            String[] a=entry.split(",");
            if(a[0].equals(this.name)){
                this.history.add(entry);
            }
        }

    }

    /**
     * loads player's histories from database
     */
    public void loadHistoryFromDB(){
        DBConnector connector=DBConnector.getInstance();
        PlayerHistory[] playerHistories = connector.getPlayerHistory(this.name);
        if(playerHistories!=null) {

            for (PlayerHistory ph : playerHistories) {
                String historyString = this.name + "," + ph.getPlayer().getScore() + "," + ph.getLuckCardCount() + "," + ph.getDate()+",";
                for (Player p : ph.getEnemys()) {
                    historyString = historyString + p.name + ":" + p.getScore() + "/";
                }
                this.history.add(historyString);
            }
        }
    }

}