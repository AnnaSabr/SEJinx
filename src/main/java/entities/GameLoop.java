package entities;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.sql.Date;

import actions.ReUnDo.Runde;
import actions.ReUnDo.Verlauf;
import actions.Zuege.Action;
import actions.Zuege.Zuege;
import actions.Zuege.ZugHistorie;
import actions.speichern.Speicher;
import adapter.primary.InputConsole;
import adapter.secondary.OutputConsole;
import adapter.secondary.TextfileAdapter;
import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import actions.ReUnDo.cards.LuckCard;
import persistence.DBConnector;
import persistence.PlayerHistory;


/**
 * Class handling game logic
 * TODO Change the way y and x are used to determine a place on the field
 */
public class GameLoop {

    static int currentRound = 1;
    Player currentPlayer;

    //time between msgs and actions
    protected int sleepTime = 200;
    protected boolean manualNextMsg = true;

    Player[] players;
    Table table;
    Verlauf verlauf;
    ZugHistorie zuege;

    ArrayList<String> highscores;
    boolean rff;
    int cP;
    int anzahlKI;
    Speicher speicherObjekt;
    ArrayList<String> profiles=new ArrayList<>();
    ArrayList<String> availableProfiles=new ArrayList<>();
    boolean db;
    DBConnector connector=DBConnector.getInstance();
    TextfileAdapter textfileAdapter=new TextfileAdapter();
    private OutputConsole outCon;
    private InputConsole inCon;

    public GameLoop(boolean rff, boolean manualNextMsg, int sleepTime, boolean dataFromDB) {
        this.rff=rff;
        this.table = new Table(rff);
        this.highscores = new ArrayList<>();
        this.getHighscore();
        this.verlauf = new Verlauf();
        this.cP = 0;
        this.anzahlKI = 0;
        this.manualNextMsg = manualNextMsg;
        this.sleepTime = sleepTime;
        this.currentRound = 1;
        this.zuege = new ZugHistorie();
        this.speicherObjekt = new Speicher();
        this.db=dataFromDB;
        this.outCon= new OutputConsole();
        this.inCon=new InputConsole();
    }

    /**
     * Call this function to run the game
     */
    public void run() {
        //init all required fields for the first time
        init();

        currentPlayer = players[0];

        //start the game loop
        this.showHighscore();
        this.saveHighscores();
        impLoop();
        //loop();
    }

    /**
     * Function to initialize everything needed
     */
    private void init() {
        if(!db){
            this.getProfilesFromFile();
        }
        outCon.simpleMessage("Welcome to JINX! How many players do you wish to play with? (2-4 Players)");
        int playerCount;
        while (true) {
            try {
                playerCount = inCon.inputConsoleINT();

                if (playerCount < 2 || playerCount > 4) {
                    outCon.simpleMessage("This game is designed for 2-4 Players! Choose again!");
                } else {
                    // set size of players to user specified value
                    this.players = new Player[playerCount];

                    outCon.simpleMessage("Please tell us if you like do modifier any player into KI: y/n");
                    String kiInvolvieren =inCon.inputConsole();
                    if (kiInvolvieren.equals("y")) {
                        initKI();
                    } else {
                        outCon.simpleMessage("No KI's involved in this game.");
                    }
                    break;
                }
            } catch (Exception e) {
                log("Enter a valid number!");
            }
        }
        if (playerCount != anzahlKI) {
            // init all players
            initPlayers();
        }
    }


    /**
     * New and improved main loop
     */
    private void impLoop() {


        //current player counter
        cP = 0;
        //determines if a round is over
        boolean roundOver;

        // Run the loop for 3 Rounds maximum
        while (currentRound <= 3) {
            roundOver = false;
            //Round is only over if a player cant choose a card anymore
            while (!roundOver) {
                // set the next player
                currentPlayer = players[cP];
                currentPlayer.setActive();
                currentPlayer.clearUsedCards();
                currentPlayer.resetRolls();

                // Let the player perform certain actions until he is done
                while (currentPlayer.isActive()) {
                    //display the current round
                    log("Round: " + currentRound);
                    //display the field
                    log("\n" + this.table.toString());

                    // Let the player choose an action
                    String action = currentPlayer.chooseAction(this.table);

                    switch (action) {
                        //let the player roll the dice
                        case "R" -> currentPlayer.roll();
                        case "L" -> {
                            //let the player choose a luckCard to play
                            LuckCard chosenOne = currentPlayer.selectLuckCard(this.table);

                            //check if player selected a card
                            if (chosenOne == null) {
                                break;
                            }
                            Action act= new Action(Zuege.USEDLUCKYCARD,chosenOne,currentPlayer);
                            ZugHistorie.actionHinzufuegen(act);
                            //switch over all possible card types
                            switch (chosenOne.getCardType()) {
                                // let the player change his diceCount to a set value

                                case ONETOTHREE:
                                    currentPlayer.mintomax(chosenOne, 1, 3);
                                    // let the player change his diceCount to a set value
                                case FOURTOSIX:
                                    currentPlayer.mintomax(chosenOne, 4, 6);
                                    // give the player an extra throw
                                case EXTRATHROW:
                                    currentPlayer.extraThrow(chosenOne);
                                    // reduce the diceCount of the player by one
                                case MINUSONE:
                                    currentPlayer.minusOne(chosenOne);
                                    // increase the diceCount of the player by one
                                case PLUSONE:
                                    currentPlayer.plusOne(chosenOne);
                                    // let the player choose a collection of cards based on his dice count
                                case CARDSUM:
                                    currentPlayer.cardSum(chosenOne, this.table);
                            }
                        }
                        case "C" -> {
                            //let the player choose a card from the field
                            boolean chosen = currentPlayer.chooseCard(this.table);

                            //was the player not able to choose a card? --> end the round!
                            if (!chosen && !currentPlayer.isActive()) {
                                roundOver = true;
                            }
                        }
                        case "N" -> verlauf.verlaufAnzeigen();
                        case "M" -> {
                            Runde veraendert = verlauf.jump();
                            if (!veraendert.equals(verlauf.getTail())) {
                                manipulieren(veraendert);
                                Card platzhalter = new Card(CardColor.RED, 420);
                                Action action7 = new Action(Zuege.MANIPULATION, platzhalter, currentPlayer);
                            }
                        }
                        case "A" -> currentPlayer.getHelp(this.table);
                        case "Z" -> showActions();
                        case "S" -> {
                                try {
                                    speicherObjekt.setVerlaufAction(ZugHistorie.zumSpeichern());
                                    speicherObjekt.setVerlaufRunden(verlauf.zumSpeichern());
                                    DBConnector dbConnector = DBConnector.getInstance();

                                    boolean saved = dbConnector.createSpeicher(speicherObjekt);

                                    if(saved){
                                        log("Your game was saved, you can load it any time!");
                                    }else{
                                        log("Saving failed - Sorry!");
                                    }
                                }catch (Exception e){
                                    log("Saving failed - You will need to make a move first!");
                                }
                        }
                        case "X" ->{

                            DBConnector dbConnector = DBConnector.getInstance();

                            Integer[] speicherObjekte = dbConnector.getSpeicherList();

                            //no speicherObjekte present
                            if(speicherObjekte == null){
                                log("You have no saved games!");
                                break;
                            }

                            log("You can choose one of the following save states! Choose 0 to stop the selection");
                            //present player with selection of save states
                            for(int i = 0; i < speicherObjekte.length; i++){
                                log("Speicherstand " + speicherObjekte[i] + " - " + (i + 1));
                            }

                            //let player choose a save state
                            int input = currentPlayer.getPlayerInputINT(0,speicherObjekte.length);

                            if(input == 0){
                                break;
                            }

                            speicherObjekt = dbConnector.getSpeicher(speicherObjekte[input - 1]);

                            laden(speicherObjekt);
                        }
                        case "P" -> {
                            currentPlayer.showHistory();
                        }
                    }
                }
                //make sure current player always loops, only when round is active
                if (!roundOver) {
                    verlaufAktualisieren(currentPlayer);
                    cP = (cP + 1) % (players.length);
                }
            }

            //clean up after round!

            log("Round Over! All your cards will be checked and possibly removed now");
            //check which cards the players have to drop
            for (Player p : players) {
                checkPlayerHand(p);
            }


            //let the player who ended the round drop his highest card!
            currentPlayer.selectHighCard();

            //let each player exchange a card for a luck card
            for (Player p : players) {
                p.drawLuckCard(this.table, players);
            }

            //deal new cards
            this.table.resetField();
            verlaufAktualisieren(currentPlayer);

            //increase the round count
            currentRound++;
        }

        //all 3 rounds ended, calculate score here
        this.addCurrentHighscores();
        this.saveHighscores();
        if(!db){
            this.savingHistoryToFile();
        }else{
            Player[] enemies=new Player[this.players.length-1];
            String[] enemyNames=new String[enemies.length];
            for(Player p:this.players){
                String myName=p.getName();
                if(p instanceof EasyKI){
                    p.setName("AILevel1");
                } else if (p instanceof MediumAI) {
                    p.setName("AILevel2");
                } else if (p instanceof AIPLayer3) {
                    p.setName("AILevel3");
                }
                int b=0;
                //temporarily change enemy AI's name for database
                for(Player enemy:this.players){
                    if(!p.equals(enemy)){
                        enemyNames[b]=enemy.name;
                        if(enemy instanceof EasyKI){
                            enemy.name="AILevel1";
                        } else if (enemy instanceof MediumAI) {
                            enemy.name="AILevel2";
                        } else if (enemy instanceof AIPLayer3) {
                            enemy.name="AILevel3";
                        }
                        enemies[b]=enemy;
                        b++;
                    }
                }
                PlayerHistory playerHistory=new PlayerHistory(p,p.usedCards.size(),this.getDate(),enemies);
                this.connector.createHistory(playerHistory);
                for(int a=0; a<enemies.length;a++){
                    enemies[a].name=enemyNames[a];
                }
                p.name=myName;
            }
            for(Player p:this.players){
                log("Histories of "+p.name);
                p.showHistory();
            }
        }
        log("Game Over!");
    }

    public void showActions() {
        outCon.simpleMessage("\n\nBisher gespielte Zuege:");
        Action start = ZugHistorie.getHead().getDahinter();
        while (!start.equals(ZugHistorie.getTail())) {
            if (start.getKarte()!=null){
                if (start.getKarte().getValue()==420){
                    outCon.simpleMessage("Spieler: " + start.getAktiverSpieler().getName() + ",   Zug: " + start.getZug()+"\n");
                }
                else{
                    outCon.simpleMessage("Spieler: " + start.getAktiverSpieler().getName() + ",   Zug: " + start.getZug() + ",   Karte: " + start.getKarte()+"\n");
                }

            }
            else{
                outCon.simpleMessage("Spieler: " + start.getAktiverSpieler().getName() + ",   Zug: " + start.getZug() + ",   Karte: " + start.getGlueckskarte()+"\n");
            }
            start = start.getDahinter();
        }

        outCon.simpleMessage("\n\n");
    }

    /**
     * fuegt die letzte Spielrunde in den Spielverlauf ein
     *
     * @param aktiv Spieler der den letzten Zug gemacht hat
     */
    public void verlaufAktualisieren(Player aktiv) {
        Table aktuellerTisch = new Table(rff);
        aktuellerTisch.setField(table.getField());
        aktuellerTisch.setCardStack(table.getCardStack());
        aktuellerTisch.setLuckStack(table.getLuckStack());

        ArrayList<Player> aktuelleSpielerStaende= new ArrayList<>();
        Player ak= new Player(aktiv.getName(),sleepTime,manualNextMsg,db);
        ak.setCards(aktiv.getCards());
        ak.setLuckCards(aktiv.getLuckCards());
        for (int i=cP; i<players.length; i++){
            Player dummy= new Player(players[i].getName(),sleepTime,manualNextMsg,db);
            dummy.setCards(players[i].getCards());
            dummy.setLuckCards(players[i].getLuckCards());
            aktuelleSpielerStaende.add(dummy);
        }
        for (int i=0; i<cP; i++){
            Player dummy= new Player(players[i].getName(),sleepTime,manualNextMsg,db);
            dummy.setCards(players[i].getCards());
            dummy.setLuckCards(players[i].getLuckCards());
            aktuelleSpielerStaende.add(dummy);
        }

        Runde neu = new Runde(aktuelleSpielerStaende, aktuellerTisch);
        verlauf.rundeHinzufuegen(neu);
    }

    /**
     * Ueberschreibt alle Informationen des aktuellen Spiels, mit denen des Spielstandes, welcher fortgesetzt werden soll
     * @param altesSpiel Spielstand von dem aus weiter gespielt werden soll
     */
    public void laden(Speicher altesSpiel) {
        this.verlauf = speicherObjekt.zumLadenVerlauf();

        int spielerCount= speicherObjekt.zumLadenRunden().getSpielerAnzahl();
        this.players= new Player[spielerCount];
        for (int i=0; i<spielerCount; i++){
            this.players[i]=speicherObjekt.zumLadenRunden().getSpieler().get(i+1);
        }

        manipulieren(speicherObjekt.zumLadenRunden());

        ZugHistorie.leeren();
        speicherObjekt.zugHistorieUeberschreiben();
    }

    /**
     * aktualisiert die aktive Runde auf eine ausgewaehlte andere Runde
     *
     * @param neuerStand Runde, von der weiter gespielt werden soll
     */
    public void manipulieren(Runde neuerStand) {
        this.table.setField(neuerStand.getTischStand().getField());
        this.table.setCardStack(neuerStand.getTischStand().getCardStack());
        this.table.setLuckStack(neuerStand.getTischStand().getLuckStack());

        int laenge = players.length;
        this.players = new Player[laenge];
        int z = 0;
        for (Player p : neuerStand.getSpieler()) {
            this.players[z] = p;
            z++;
        }
        Runde vorher = verlauf.getTail().getDavor();
        vorher.setDahinter(neuerStand);
        verlauf.getTail().setDavor(neuerStand);
        neuerStand.setDavor(vorher);
        neuerStand.setDahinter(verlauf.getTail());
        log("status updated");

    }


    /**
     * Function to remove cards from player hand if round ends
     */
    private void checkPlayerHand(Player p) {
        Card[] hand = p.getCards().toArray(new Card[0]);
        Card[][] field = this.table.getField();

        for (Card cP : hand) {
            for (Card[] row : field) {
                for (Card cF : row) {
                    if (cF != null && cP.getColor() == cF.getColor()) {
                        //Player has a card with the same color on his hand --> remove it!
                        p.removeCard(cP);
                    }
                }
            }
        }
    }


    /**
     * Function to reset the game
     */
    private void resetGame() {
        //create new table
        this.table = new Table(false);
        //delete all players
        this.players = null;

        //restart game;
        init();
    }

    /**
     * Function to easily log a msg on the console
     */
    private void log(String msg) {
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


    //TODO: Fail save, only let player enter valid names

    /**
     * Function to initialize all players by name
     */
    private void initPlayers() {

        //create as many players as needed
        for (int i = anzahlKI; i < players.length; i++) {
            String name=this.chooseProfile();
            Player p=new Player(name,sleepTime,manualNextMsg,db);
            if(db){
                p.loadHistoryFromDB();
            }
            players[i] = p;
        }
        //set the first player
        currentPlayer = players[0];

    }

    /**
     * loads data from Highscore file
     */
    private void getHighscore() {
        this.highscores=this.textfileAdapter.getFileInput("src/main/java/entities/highscore.txt");
    }


    /**
     * shows all high scores
     */
    private void showHighscore() {
        log("High scores:");
        int ranking = 0;
        for (String score : this.highscores) {
            ranking++;
            log(ranking + ". " + score);
        }
    }

    /**
     * saves high scores in textfile
     */
    private void saveHighscores() {
        this.textfileAdapter.saveToFile("src/main/java/entities/highscore.txt",this.highscores);
    }

    /**
     * adds Highscore of the current game, sorted by score
     */
    private void addCurrentHighscores() {
        for (Player player : this.players) {
            ArrayList<String> newHighscore = new ArrayList<>();
            boolean added = false;
            int lines = 0;
            for (String line : this.highscores) {
                lines++;
                String[] nameAndScore = line.split(" ");
                //wenn der alte wert kleiner ist als der score, score muss also dar??ber, bei gleichen werten kommt der neue nach unten
                if (Integer.parseInt(nameAndScore[1]) < player.getScore() && !added) {
                    newHighscore.add(player.getName() + " " + player.getScore());
                    added = true;
                    newHighscore.add(nameAndScore[0] + " " + nameAndScore[1]);
                } else if (!added && this.highscores.size() == lines) {
                    newHighscore.add(nameAndScore[0] + " " + nameAndScore[1]);
                    newHighscore.add(player.getName() + " " + player.getScore());
                } else {
                    newHighscore.add(nameAndScore[0] + " " + nameAndScore[1]);
                }
            }
            if(this.highscores.size()==0){
                newHighscore.add(player.getName() + " " + player.getScore());
            }
            this.highscores = newHighscore;
        }
    }

    /**
     * Ki menue, Auswahl der Anzahl der KIs im Spiel und deren Initialisierung
     */
    public void initKI() {
        int players = this.players.length;
        ArrayList<Player> ki = new ArrayList<>();
        while (true) {
            outCon.simpleMessage("This game will have " + players + " players. Choose between 0-" + players + ".\n" +
                    "How many do you want to substitute with KI's?");
            String kiAnzahl = inCon.inputConsole();
            try {
                int anzahl = Integer.parseInt(kiAnzahl);
                if (anzahl > 0 && anzahl <= players) {
                    for (int a = 0; a < anzahl; a++) {
                        ki.add(buildingKI());
                    }
                    int b = 0;
                    for (Player p : ki) {
                        this.players[b] = p;
                        b++;
                    }
                    break;
                } else {
                    outCon.simpleMessage("Incorrect input.");
                }
            } catch (NumberFormatException n) {
                outCon.simpleMessage("Not an acceptable answer. You will play without any KI.");
            }
        }
    }

    /**
     * erstellt individuelle KI
     *
     * @return einzelne KI
     */
    public Player buildingKI() {
        Player k;
        String name = "";
        String level = "";
        while (true) {
            outCon.simpleMessage("Please enter a Name for your KI:");
            name = inCon.inputConsole();
            if (!name.equals("")) {
                outCon.simpleMessage("Please choose a level for your KI:  " +
                        "easy / medium / hard");
                level=inCon.inputConsole();
                if (level.equals("easy")){
                    k = new EasyKI(name,sleepTime,manualNextMsg,db);
                    break;
                }
                else if (level.equals("medium")){
                    k = new MediumAI(name,sleepTime,manualNextMsg,db);
                    break;
                }
                else if (level.equals("hard")){
                    k=new AIPLayer3(name,sleepTime,manualNextMsg,db);
                    break;
                } else {
                    outCon.simpleMessage("Not an option. Try again.");
                }
            } else {
                outCon.simpleMessage("Wrong input.");
            }
        }
        outCon.simpleMessage("You created: KI: " + k.getName() + "  Level: " + k.getClass().getSimpleName());
        this.anzahlKI++;
        return k;
    }

    /**
     *  loads existing profiles from textfile
     */
    public void getProfilesFromFile(){
        this.profiles=this.textfileAdapter.getFileInput("src/main/java/entities/userProfiles.txt");
    }

    /**
     * check if the password is valid
     */
    public boolean matchPasswordToProfile(String name, String entry){
        for(String oneLine : this.profiles){
            String[] line=oneLine.split(",");
            if(line[0].equals(name)){
                if(this.validatePassword(line[1],entry)){
                    outCon.simpleMessage("Correct!");
                    return true;
                }else{
                    outCon.simpleMessage("Wrong password!");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * chooses profile for player from db or file
     * @return name of profile
     */
    public String chooseProfile(){
        this.log("Would you like to choose a profile? y/n");
        if(inCon.inputConsole().equals("y")){
            this.log("Which profile do you want?");
            String name=inCon.inputConsole().replaceAll(" ","");
            if(name.equals("AILevel1")||name.equals("AILevel2")||name.equals("AILevel3")){
                this.log("This is the AI's profile! You may not use it.");
                return chooseProfile();
            }
            if(db){
                //if player exists
                if(this.connector.checkPlayer(name)){
                    //check if profile is taken
                    for(Player player:this.players){
                        if(player==null){
                            break;
                        }else{
                            if(player.name.equals(name)){
                                this.log("This profile is taken already");
                                return this.chooseProfile();
                            }
                        }
                    }
                    //if profile was not taken yet, but is already available
                    for(String entry:this.availableProfiles){
                        if(entry.equals(name)){
                            this.log("No log-in required.");
                            return name;
                        }
                    }
                    this.log("Enter your password!");
                    String enteredPassword=inCon.inputConsole();
                    if(this.connector.playerLogin(name,enteredPassword)){
                        this.log("Correct!");
                        this.availableProfiles.add(name);
                        return name;
                    }else{
                        this.log("Your password is wrong.");
                        return chooseProfile();
                    }
                }else{
                    this.log("Profile not found.");
                    return this.chooseProfile();
                }
            }else{
                //log in to profile from textfile
                if(this.findProfileFromFile(name)){
                    if(this.accessProfileFromFile(name)){
                        return name;
                    }
                    return this.chooseProfile();
                }else{
                    return this.chooseProfile();
                }
            }
        }else{
            //new profile to db
            if(db){
                String name="";
                this.log("Please enter a name for the profile.");
                name=inCon.inputConsole().replaceAll(" ","");
                if(this.connector.checkPlayer(name)){
                    this.log("This profile already exists.");
                    return this.chooseProfile();
                }else{
                    this.log("Now enter the new password.");
                    String password=inCon.inputConsole();
                    if(this.connector.createPlayer(name,password)){
                        return name;
                    }else{
                        this.log("Profile could not be created.");
                        return this.chooseProfile();
                    }
                }
            }else{
                //new profile to textfile
                String name="";
                boolean a=true;
                while(a){
                    a=false;
                    this.log("Please choose a name for your new profile!");
                    name = inCon.inputConsole().replaceAll(" ", "");
                    for(String line:this.profiles){
                        String[] strings=line.split(",");
                        if(strings[0].equals(name)){
                            this.log("This already exists! Please choose a new name!");
                            a=true;
                            break;
                        }
                    }
                }
                this.log("Please choose a password!");
                String pw=inCon.inputConsole();
                this.profiles.add(name+","+this.calculatePassword(pw));
                this.availableProfiles.add(name+","+this.calculatePassword(pw));
                return name;
            }
        }
    }

    /**
     * enter password for chosen profile
     * @param profileName name of chosen profile
     * @return
     */
    public boolean accessProfileFromFile(String profileName){
        for (String line : this.profiles) {
            String[] name = line.split(",");
            if (name[0].equals(profileName)) {
                this.log("Please enter the password!");
                String pw = inCon.inputConsole();
                boolean access = this.matchPasswordToProfile(profileName, pw);
                if (access) {
                    this.availableProfiles.add(line);
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * finds profile the player can use from textfile
     * @param newName name of profile
     * @return whether profile is available
     */
    public boolean findProfileFromFile(String newName){
        boolean found = false;
        for (String line : this.profiles) {
            String[] name = line.split(",");
            if (name[0].equals(newName)) {
                for (Player p : this.players) {
                    if (p == null) {
                        break;
                    } else if (p.name.equals(newName)) {
                        this.log("This profile is taken! Please choose a different profile!");
                        return false;
                    }
                }
                found = true;
            }
        }
        if (!found) {
            log("Profile not found!");
            return false;
        }
        return true;
    }



    /**
     * calculates the password that will be saved in textfile
     */
    public int calculatePassword(String password){
        int val=0;
        for(int a=0; a<password.length();a++){
            char c=password.charAt(a);
            val=val+c;
        }
        return val%5000;
    }

    /**
     * calculates if entered password matches password in textfile
     * @return
     */
    public boolean validatePassword(String expected,String entered){
        int enteredVal=this.calculatePassword(entered);
        String enter=String.valueOf(enteredVal);
        if(enter.equals(expected)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * creates history for current round
     *
     * @param player player the history is created for
     */
    public String createHistory(Player player){
        String history="";
        if(player instanceof EasyKI){
            history="AILevel1";
        }else if(player instanceof MediumAI){
            history="AILevel2";
        }else if(player instanceof AIPLayer3){
            history="AILevel3";
        }else{
            history=player.name;
        }
        //profilename,score,amount of used luckcards, date, opponents and their scores
        history=history+","+player.getScore()+","+player.usedCards.size()+","+this.getDate()+",";
        for(int a=0; a<this.players.length;a++){
            String aiLevel="";
            if(players[a] instanceof EasyKI){
                aiLevel=" (AILevel1)";
            }else if(players[a] instanceof MediumAI){
                aiLevel=" (AILevel2)";
            }else if(players[a] instanceof AIPLayer3){
                aiLevel=" (AILevel3)";
            }
            if(this.players[a]!=player){
                history=history+players[a].name+aiLevel+": "+players[a].getScore()+" /";
            }
        }
        return history;
    }

    /**
     * get date for player history
     */
    private Date getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-[m]m-[d]d");
        //java.util.Date currentTime = new java.util.Date();
        //String date=formatter.format(currentTime);
        Date date=new Date(Instant.now().toEpochMilli());
        formatter.format(date);
        return date;
    }

    /**
     *  saves all new and previous histories to textfile
     */
    public void savingHistoryToFile(){
        //saves new histories and shows them
        String[] histories=new String[this.players.length];
        //getting all current player histories
        for(int a=0; a<this.players.length;a++){
            histories[a]=this.createHistory(players[a]);
            //iterate player's histories to show them in order
            boolean added=false;
            for(int b=0;b<players[a].history.size();b++){
                String[] playersPrevHistory=players[a].history.get(b).split(",");
                if(Integer.parseInt(playersPrevHistory[1])<players[a].getScore()){
                    //add history of current round, in order by score
                    players[a].history.add(b,histories[a]);
                    added=true;
                    break;
                }
            }
            if(!added){
                players[a].history.add(histories[a]);
            }
            players[a].showHistory();
        }
        //get profiles and histories
        ArrayList<String> content= this.textfileAdapter.getFileInput("src/main/java/entities/userProfiles.txt");
        ArrayList<String> prevHistory=this.textfileAdapter.getFileInput("src/main/java/entities/playerHistories.txt");

        //add all histories of this round
        for (String h : histories) {
            boolean profileFound = false;
            for (int a = 0; a < prevHistory.size(); a++) {
                //history of this round
                String[] s = h.split(",");
                //Arraylist
                String[] b = prevHistory.get(a).split(",");
                //compare profile names
                if (b[0].equals(s[0])) {
                    //if old score is lower than new score
                    if (Integer.parseInt(b[1]) < Integer.parseInt(s[1])) {
                        prevHistory.add(a, h);
                        break;
                        //if profile was found but history has not been added yet
                    } else {
                        profileFound = true;
                    }
                    //if profile was found, but value has not been added
                } else if (profileFound) {
                    prevHistory.add(a, h);
                    break;
                }
            }
            //if no spot in Arraylist was found, add history to the end
            if (!prevHistory.contains(h)) {
                prevHistory.add(h);
            }
        }
        for(String prof:this.profiles){
            boolean saved=false;
            String[] a=prof.split(",");
            for(String oldData:content){
                if(a[0].equals(oldData.split(",")[0])){
                    saved=true;
                }
            }
            //profile needs to be added to file
            if(!saved){
                content.add(content.size()-1,prof);
            }
        }
        textfileAdapter.saveToFile("src/main/java/entities/userProfiles.txt",content);
        textfileAdapter.saveToFile("src/main/java/entities/playerHistories.txt",prevHistory);

    }

}
