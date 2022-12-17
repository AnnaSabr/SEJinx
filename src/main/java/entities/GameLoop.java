package entities;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.sql.Date;

import actions.ReUnDo.Round;
import actions.ReUnDo.Course;
import actions.Zuege.Action;
import actions.Zuege.Moves;
import actions.Zuege.MoveHistory;
import actions.speichern.Storage;
import adapter.primary.InOutGUI;
import adapter.primary.InputConsole;
import adapter.secondary.OutputConsole;
import adapter.secondary.TextfileAdapter;
import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import actions.ReUnDo.cards.LuckCard;
import persistence.DBConnector;
import persistence.PlayerHistory;
import ports.inbound.MessageInput;
import ports.outbound.MessageOutput;


/**
 * Main class handling all the relevant game logic
 * Includes the main game loop, controlling game flow
 */
public class GameLoop {

    static int currentRound = 1;
    Player currentPlayer;

    //time between msgs and actions
    protected int sleepTime = 200;
    protected boolean manualNextMsg = true;

    Player[] players;
    Table table;
    Course course;
    MoveHistory moves;

    ArrayList<String> highscores;
    boolean rff;
    int cP;
    int kiCount;
    Storage storageObject;
    ArrayList<String> profiles=new ArrayList<>();
    ArrayList<String> availableProfiles=new ArrayList<>();
    boolean db;
    DBConnector connector=DBConnector.getInstance();
    TextfileAdapter textfileAdapter=new TextfileAdapter();
    private MessageOutput outCon;
    private MessageInput inCon;
    boolean showGui;
    GUI gui;
    private InOutGUI inOut;

    /**
     * Constructor of the game loop, used to configure the game
     *
     * @param rff should the stack be loaded from file
     * @param manualNextMsg does the player control the game himself
     * @param sleepTime time between messages
     * @param dataFromDB should game be loaded from database
     * @param showGui should a gui be used or the console
     * */
    public GameLoop(boolean rff, boolean manualNextMsg, int sleepTime, boolean dataFromDB, boolean showGui) {
        this.rff=rff;
        this.table = new Table(rff);
        this.highscores = new ArrayList<>();
        this.getHighscore();
        this.course = new Course();
        this.cP = 0;
        this.kiCount = 0;
        this.manualNextMsg = manualNextMsg;
        this.sleepTime = sleepTime;
        this.currentRound = 1;
        this.moves = new MoveHistory();
        this.storageObject = new Storage();
        this.db=dataFromDB;
        this.showGui= showGui;

        if(showGui){
            gui=new GUI();
            this.inOut= new InOutGUI(gui);
            this.outCon = this.inOut;
            this.inCon = this.inOut;
        } else{
            this.outCon= new OutputConsole();
            this.inCon= new InputConsole();
        }

    }

    /**
     * Call this function to run the game
     * Handles creation of players and starts the loop (Never returns)
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
                playerCount = inCon.inputINTPlayerInitialization();

                if (playerCount < 2 || playerCount > 4) {
                    outCon.simpleMessage("This game is designed for 2-4 Players! Choose again!");
                } else {
                    // set size of players to user specified value
                    this.players = new Player[playerCount];

                    outCon.simpleMessage("Please tell us if you like do modifier any player into KI: y/n");
                    String kiInvolved =inCon.letterInput();
                    if (kiInvolved.equals("y")) {
                        initKI();
                    } else {
                        outCon.simpleMessage("No KI's involved in this game.");
                    }
                    break;
                }
            } catch (Exception e) {
                outCon.errorSelfMessage("Enter a valid number!");
            }
        }
        if (playerCount != kiCount) {
            // init all players
            initPlayers();
        }
    }


    /**
     * New and improved main loop
     */
    private void impLoop() {


        //this.gui.runGUI();

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
                    outCon.tablePicture(this.table);

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
                            Action act= new Action(Moves.USEDLUCKYCARD,chosenOne,currentPlayer);
                            MoveHistory.addNewAction(act);
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
                        case "N" -> course.showHistory();
                        case "M" -> {
                            Round changed = course.jump();
                            if (!changed.equals(course.getTail())) {
                                manipulate(changed);
                                Card placeholder = new Card(CardColor.RED, 420);
                            }
                        }
                        case "A" -> currentPlayer.getHelp(this.table);
                        case "Z" -> showActions();
                        case "S" -> {
                                try {
                                    storageObject.setActionHistory(MoveHistory.toSave());
                                    storageObject.setRoundHistory(course.toSave());
                                    DBConnector dbConnector = DBConnector.getInstance();

                                    boolean saved = dbConnector.createSpeicher(storageObject);

                                    if(saved){
                                        log("Your game was saved, you can load it any time!");
                                    }else{
                                        outCon.errorSelfMessage("Saving failed - Sorry!");
                                    }
                                }catch (Exception e){
                                    outCon.errorSelfMessage("Saving failed - You will need to make a move first!");
                                }
                        }
                        case "X" ->{

                            DBConnector dbConnector = DBConnector.getInstance();

                            Integer[] saveObject = dbConnector.getSpeicherList();

                            //no saveObject present
                            if(saveObject == null){
                                log("You have no saved games!");
                                break;
                            }

                            log("You can choose one of the following save states! Choose 0 to stop the selection");
                            //present player with selection of save states
                            for(int i = 0; i < saveObject.length; i++){
                                log("Speicherstand " + saveObject[i] + " - " + (i + 1));
                            }

                            //let player choose a save state
                            int input = currentPlayer.getPlayerInputINT(0,saveObject.length);

                            if(input == 0){
                                break;
                            }

                            storageObject = dbConnector.getSpeicher(saveObject[input - 1]);

                            load(storageObject);
                        }
                        case "P" -> {
                            currentPlayer.showHistory();
                        }
                    }
                }
                //make sure current player always loops, only when round is active
                if (!roundOver) {
                    historyUpdate(currentPlayer);
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
            historyUpdate(currentPlayer);

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

    /**
     * Function to display all past actions of this game
     * */
    public void showActions() {
        outCon.simpleMessage("\n\nBisher gespielte Zuege:");
        Action begin = MoveHistory.getHead().getBehind();
        while (!begin.equals(MoveHistory.getTail())) {
            if (begin.getCard()!=null){
                if (begin.getCard().getValue()==420){
                    outCon.simpleMessage("Spieler: " + begin.getActivePlayer().getName() + ",   Zug: " + begin.getMove()+"\n");
                }
                else{
                    outCon.simpleMessage("Spieler: " + begin.getActivePlayer().getName() + ",   Zug: " + begin.getMove() + ",   Karte: " + begin.getCard()+"\n");
                }

            }
            else{
                outCon.simpleMessage("Spieler: " + begin.getActivePlayer().getName() + ",   Zug: " + begin.getMove() + ",   Karte: " + begin.getLuckCard()+"\n");
            }
            begin = begin.getBehind();
        }

        outCon.simpleMessage("\n\n");
    }

    /**
     * Add the last round to the gameHistory
     *
     * @param active last active player
     */
    public void historyUpdate(Player active) {
        Table tableStatus = new Table(rff);
        tableStatus.setField(table.getField());
        tableStatus.setCardStack(table.getCardStack());
        tableStatus.setLuckStack(table.getLuckStack());

        ArrayList<Player> allPlayerStatus= new ArrayList<>();
        Player ak= new Player(active.getName(),sleepTime,manualNextMsg,db);
        ak.setCards(active.getCards());
        ak.setLuckCards(active.getLuckCards());
        for (int i=cP; i<players.length; i++){
            Player dummy= new Player(players[i].getName(),sleepTime,manualNextMsg,db);
            dummy.setCards(players[i].getCards());
            dummy.setLuckCards(players[i].getLuckCards());
            allPlayerStatus.add(dummy);
        }
        for (int i=0; i<cP; i++){
            Player dummy= new Player(players[i].getName(),sleepTime,manualNextMsg,db);
            dummy.setCards(players[i].getCards());
            dummy.setLuckCards(players[i].getLuckCards());
            allPlayerStatus.add(dummy);
        }

        Round newOne = new Round(allPlayerStatus, tableStatus);
        newOne.setActive(active);
        course.addRound(newOne);
    }

    /**
     * Overwrites all important information of the current game with a new game
     * @param oldGame Game that should be loaded
     */
    public void load(Storage oldGame) {
        this.course = storageObject.HistoryToLoad();

        int playerCount= storageObject.getLastRound().getPlayerCount();
        this.players= new Player[playerCount];
        for (int i=0; i<playerCount; i++){
            this.players[i]= storageObject.getLastRound().getAllPlayers().get(i+1);
        }

        manipulate(storageObject.getLastRound());

        MoveHistory.empty();
        storageObject.overwriteActions();
    }

    /**
     * Function to manipulate the current game state
     * Used for undo and redo mechanics
     *
     * @param newStatus Runde, von der weiter gespielt werden soll
     */
    public void manipulate(Round newStatus) {
        this.table.setField(newStatus.getTableStatus().getField());
        this.table.setCardStack(newStatus.getTableStatus().getCardStack());
        this.table.setLuckStack(newStatus.getTableStatus().getLuckStack());

        int lengthPlayers = players.length;
        this.players = new Player[lengthPlayers];
        int z = 0;
        for (Player p : newStatus.getAllPlayers()) {
            this.players[z] = p;
            z++;
        }
        Round before = course.getTail().getBefore();
        before.setBehind(newStatus);
        course.getTail().setBefore(newStatus);
        newStatus.setBefore(before);
        newStatus.setBehind(course.getTail());
        log("status updated");

    }


    /**
     * Function to remove cards from player hand if round ends
     * @param p player to be checked for duplicates in his hand
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
     * Function to easily log a msg on the console
     * @param msg message to be logged
     */
    private void log(String msg) {
        if (manualNextMsg) {
            outCon.jinxMessage(msg + " [ENTER] to continue!");
            inCon.inputAnything();
        } else {
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                outCon.errorSelfMessage("Sleep exception!");
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
        for (int i = kiCount; i < players.length; i++) {
            String name=this.chooseProfile();
            Player p=new Player(name,sleepTime,manualNextMsg,db);
            p.registerInput(inCon);
            p.registerOutput(outCon);
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
                //wenn der alte wert kleiner ist als der score, score muss also darüber, bei gleichen werten kommt der neue nach unten
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
            int kiCounter = inCon.inputINTPlayerInitialization();
            try {
                if (kiCounter > 0 && kiCounter <= players) {
                    for (int a = 0; a < kiCounter; a++) {
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
     * Creates an individual AI
     *
     * @return the created AI
     */
    public Player buildingKI() {
        Player k;
        String name = "";
        String level = "";
        while (true) {
            outCon.simpleMessage("Please enter a Name for your KI:");
            name = inCon.inputName();
            if (!name.equals("")) {
                outCon.simpleMessage("Please choose a level for your KI:  " +
                        "easy / medium / hard");
                level=inCon.inputLevel();
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
        this.kiCount++;
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
        if(inCon.letterInput().equals("y")){
            this.log("Which profile do you want?");
            String name=inCon.inputName().replaceAll(" ","");
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
                    String enteredPassword=inCon.inputPasswort();
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
                name=inCon.inputName().replaceAll(" ","");
                if(this.connector.checkPlayer(name)){
                    this.log("This profile already exists.");
                    return this.chooseProfile();
                }else{
                    this.log("Now enter the new password.");
                    String password=inCon.inputPasswort();
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
                    name = inCon.inputName().replaceAll(" ", "");
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
                String pw=inCon.inputPasswort();
                this.profiles.add(name+","+this.calculatePassword(pw));
                this.availableProfiles.add(name+","+this.calculatePassword(pw));
                return name;
            }
        }
    }

    /**
     * enter password for chosen profile
     * @param profileName name of chosen profile
     * @return true if there is a profile and the player has access
     */
    public boolean accessProfileFromFile(String profileName){
        for (String line : this.profiles) {
            String[] name = line.split(",");
            if (name[0].equals(profileName)) {
                this.log("Please enter the password!");
                String pw = inCon.inputPasswort();
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
     * @param password password to be turned into a hash
     * @return password as hash
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
     * @param expected value for that password
     * @param entered useres values
     * @return true if password is valid, false if not
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
     * @return the history of given player as string
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
     * @return the date the history was created
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
