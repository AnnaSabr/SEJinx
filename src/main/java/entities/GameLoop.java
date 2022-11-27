package entities;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
//import java.util.Date;
import java.sql.Date;
import java.util.Scanner;

import actions.ReUnDo.Runde;
import actions.ReUnDo.Verlauf;
import cards.Card;
import cards.LuckCard;
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

    ArrayList<String> highscores;
    boolean rff;
    int cP;
    int anzahlKI;
    ArrayList<String> profiles=new ArrayList<>();
    ArrayList<String> availableProfiles=new ArrayList<>();
    boolean db;
    DBConnector connector=DBConnector.getInstance();

    public GameLoop(boolean rff, boolean manualNextMsg, int sleepTime, boolean dataFromDB) {
        this.rff=rff;
        this.table = new Table(rff);
        this.highscores=new ArrayList<>();
        this.getHighscore();
        this.verlauf = new Verlauf();
        this.cP=0;
        this.anzahlKI=0;
        this.manualNextMsg = manualNextMsg;
        this.sleepTime = sleepTime;
        this.currentRound=1;
        this.db=dataFromDB;
    }

    /**
     * Call this function to run the game
     */
    public void run() {
        //init all required fields for the first time
        init();
        //start the game loop
        this.showHighscore();
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
        System.out.println("Welcome to JINX! How many players do you wish to play with? (2-4 Players)");
        int playerCount;
        while (true) {
            //try {
                Scanner s = new Scanner(System.in);
                playerCount = s.nextInt();

                if (playerCount < 2 || playerCount > 4) {
                    System.out.println("This game is designed for 2-4 Players! Choose again!");
                } else {
                    // set size of players to user specified value
                    this.players = new Player[playerCount];

                    System.out.println("Please tell us if you like do modifier any player into KI: y/n");
                    String kiInvolvieren=s.next();
                    if (kiInvolvieren.equals("y")){
                        initKI();
                    }
                    else{
                        System.out.println("No KI's involved in this game.");
                    }
                    break;
                }
            //}catch (Exception e){
               // log("Enter a valid number!");
            //}
        }
        if (playerCount!=anzahlKI){
            // init all players
            initPlayers();
        }
    }


    /**
     * New and improved main loop
     * */
    private void impLoop(){

        //current player counter
         cP = 0;
        //determines if a round is over
        boolean roundOver;

        // Run the loop for 3 Rounds maximum
        while(currentRound <= 3){
            roundOver = false;
            //Round is only over if a player cant choose a card anymore
            while(!roundOver){
                // set the next player
                currentPlayer = players[cP];
                currentPlayer.setActive();
                currentPlayer.clearUsedCards();
                currentPlayer.resetRolls();

                // Let the player perform certain actions until he is done
                while(currentPlayer.isActive()){

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

                            //switch over all possible card types
                            switch (chosenOne.getCardType()) {
                                // let the player change his diceCount to a set value
                                case ONETOTHREE -> currentPlayer.mintomax(chosenOne, 1, 3);
                                // let the player change his diceCount to a set value
                                case FOURTOSIX -> currentPlayer.mintomax(chosenOne, 4, 6);
                                // give the player an extra throw
                                case EXTRATHROW -> currentPlayer.extraThrow(chosenOne);
                                // reduce the diceCount of the player by one
                                case MINUSONE -> currentPlayer.minusOne(chosenOne);
                                // increase the diceCount of the player by one
                                case PLUSONE -> currentPlayer.plusOne(chosenOne);
                                // let the player choose a collection of cards based on his dice count
                                case CARDSUM -> currentPlayer.cardSum(chosenOne, this.table);
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
                        case "N" -> {
                            verlauf.verlaufAnzeigen();
                        }
                        case "M" ->{
                            Runde veraendert=verlauf.jump();
                            if (!veraendert.equals(verlauf.getTail())){
                                manipulieren(veraendert);
                            }
                        }
                        case "A" -> {
                            currentPlayer.getHelp(this.table);
                        }
                        case "P" -> {
                            currentPlayer.showHistory();
                        }
                    }
                }
                //make sure current player always loops, only when round is active
                if(!roundOver) {
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
            for (Player p: players){
                p.drawLuckCard(this.table,players);
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
                    p.name="AILevel1";
                } else if (p instanceof MediumAI) {
                    p.name="AILevel2";
                } else if (p instanceof AIPLayer3) {
                    p.name="AILevel3";
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
        }
        log("Game Over!");
    }


    /**
     * fuegt die letzte Spielrunde in den Spielverlauf ein
     * @param aktiv Spieler der den letzten Zug gemacht hat
     */
    public void verlaufAktualisieren(Player aktiv){
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
     * aktualisiert die aktive Runde auf eine ausgewaehlte andere Runde
     * @param neuerStand Runde, von der weiter gespielt werden soll
     */
    public void manipulieren(Runde neuerStand){
        this.table.setField(neuerStand.getTischStand().getField());
        this.table.setCardStack(neuerStand.getTischStand().getCardStack());
        this.table.setLuckStack(neuerStand.getTischStand().getLuckStack());

        int laenge= players.length;
        this.players=new Player[laenge];
        int z=0;
        for (Player p: neuerStand.getSpieler()){
            this.players[z]=p;
            z++;
        }
        Runde vorher=verlauf.getTail().getDavor();
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
        if(manualNextMsg){
            System.out.println("[JINX] " + msg + " [ENTER] to continue!");
            Scanner s = new Scanner(System.in);
            s.nextLine();
        }else {
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                System.out.println("Sleep exception!");
            }
            System.out.println("[JINX] " + msg);
        }
    }


    //TODO: Fail save, only let player enter valid names
    /**
     * Function to initialize all players by name
     */
    private void initPlayers() {

        Scanner s = new Scanner(System.in);

        //create as many players as needed
        for (int i = anzahlKI; i < players.length; i++) {
            String name=this.chooseProfile();
            players[i] = new Player(name,sleepTime,manualNextMsg,db);
        }
        //set the first player
        currentPlayer = players[0];

    }

    /**
     * loads data from Highscore file
     */
    private void getHighscore() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/java/entities/highscore.txt"));

            String line = br.readLine();

            while (line != null) {
                this.highscores.add(line);
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            PrintWriter pw = new PrintWriter("src/main/java/entities/highscore.txt");

            for (String entry : this.highscores) {
                System.out.println(entry);
                pw.println(entry);
                pw.flush();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
                    added=true;
                    newHighscore.add(nameAndScore[0] + " " + nameAndScore[1]);
                }else if(!added && this.highscores.size()==lines){
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
    public void initKI(){
        int players=this.players.length;
        ArrayList<Player> ki = new ArrayList<>();
        while(true){
            System.out.println("This game will have "+players+ " players. Choose between 0-"+players+".\n" +
                    "How many do you want to substitute with KI's?");
            Scanner s = new Scanner(System.in);
            String kiAnzahl = s.next();
            try{
                int anzahl = Integer.parseInt(kiAnzahl);
                if (anzahl>0&&anzahl<=players){
                    for (int a=0; a<anzahl; a++){
                        ki.add(buildingKI());
                    }
                    int b=0;
                    for (Player p: ki){
                        this.players[b]=p;
                        b++;
                    }
                    break;
                }
                else{
                    System.out.println("Incorrect input.");
                }
            }
            catch (NumberFormatException n){
                System.out.println("Not an acceptable answer. You will play without any KI.");
            }
        }
    }

    /**
     * erstellt individuelle KI
     * @return einzelne KI
     */
    public Player buildingKI(){
        Player k;
        String name="";
        String level="";
        Scanner s = new Scanner(System.in);
        while (true){
            System.out.println("Please enter a Name for your KI:");
            name=s.next();
            if (!name.equals("")){
                System.out.println("Please choose a level for your KI:  "+
                        "easy / medium / hard");
                level=s.next();
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
                }
                else{
                    System.out.println("Not an option. Try again.");
                }
            }
            else{
                System.out.println("Wrong input.");
            }
        }
        System.out.println("You created: KI: "+ k.getName()+ "  Level: "+ k.getClass().getSimpleName());
        this.anzahlKI++;
        return k;
    }

    /**
     *  loads existing profiles from textfile
     */
    public void getProfilesFromFile(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/main/java/entities/userProfiles.txt"));

            String line = br.readLine();
            while (line != null && !line.equals("histories")) {
                this.profiles.add(line);
                line = br.readLine();
            }

        }catch (IOException e){
            System.out.println("Something is wrong with the file");
        }

    }

    /**
     * check if the password is valid
     */
    public boolean matchPasswordToProfile(String name, String entry){
        for(String oneLine : this.profiles){
            String[] line=oneLine.split(",");
            if(line[0]==name){
                if(this.validatePassword(line[1],entry)){
                    System.out.println("Correct!");
                    return true;
                }else{
                    System.out.println("Wrong password!");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * chooses profile for player from db
     * @return name of profile
     */
    public String chooseProfile(){
        Scanner s=new Scanner(System.in);
        this.log("Would you like to choose a profile? y/n");
        if(s.nextLine().equals("y")){
            this.log("Which profile do you want?");
            String name=s.nextLine().replaceAll(" ","");
            if(db){
                //TODO ban names of AI profiles, create AIprofiles, create access in initKI
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
                    String enteredPassword=s.nextLine();
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
                }
            }
        }else{
            //new profile to db
            if(db){
                String name="";
                this.log("Please enter a name for the profile.");
                name=s.nextLine().replaceAll(" ","");
                if(this.connector.checkPlayer(name)){
                    this.log("This profile already exists.");
                    return this.chooseProfile();
                }else{
                    this.log("Now enter the new password.");
                    String password=s.nextLine();
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
                    name = s.nextLine().replaceAll(" ", "");
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
                String pw=s.nextLine();
                this.profiles.add(name+","+this.calculatePassword(pw));
                this.availableProfiles.add(name+","+this.calculatePassword(pw));
                return name;
            }
        }
        return null;
    }

    /**
     * enter password for chosen profile
     * @param profileName name of chosen profile
     * @return
     */
    public boolean accessProfileFromFile(String profileName){
        Scanner s=new Scanner(System.in);
        for (String line : this.profiles) {
            String[] name = line.split(",");
            if (name[0].equals(profileName)) {
                this.log("Please enter the password!");
                String pw = s.nextLine();
                boolean access = this.matchPasswordToProfile(name[0], pw);
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
        for (String line : this.availableProfiles) {
            String[] name = line.split(",");
            if (name[0].equals(newName)) {
                for (Player p : this.players) {
                    if (p.equals(null)) {
                        break;
                    } else if (p.name.equals(newName)) {
                        this.log("This profile is taken! Please choose a different profile!");
                        return false;
                    }
                    found = true;
                }
            }
        }
        if (!found) {
            log("Profile not found!");
            return false;
        }
        return true;
    }

    /**
     * choose a profile for the player from text-file
     *
     * @return name of the chosen profile
     *///TODO delete when replaced
    public String chooseProfileFromFile(){
        Scanner s=new Scanner(System.in);
        this.log("Would you like to choose a profile? y/n");
        if(s.nextLine().equals("y")){
            this.log("Which profile do you want?");
            boolean found=false;
            String newName=s.nextLine();
            for(String line:this.availableProfiles){
                String[] name=line.split(",");
                if(name[0].equals(newName)){
                    for(Player p:this.players){
                        if(p.equals(null)){
                            break;
                        } else if (p.name.equals(newName)) {
                            this.log("This profile is taken! Please choose a different profile!");
                            return this.chooseProfileFromFile();
                        }
                        found=true;
                    }
                }
            }
            if(!found){
                log("Profile not found!");
                return this.chooseProfileFromFile();
            }
            for(String line:this.profiles){
                String[] name=line.split(",");
                if(name[0]==newName){
                    this.log("Please enter the password!");
                    String pw=s.nextLine();
                    boolean access=this.matchPasswordToProfile(name[0],pw);
                    if(access){
                        this.availableProfiles.add(line);
                        return name[0];
                    }
                }
            }
        }else{
            String name="";
            boolean a=true;
            while(a){
                a=false;
                this.log("Please choose a name for your new profile!");
                name = s.nextLine().replaceAll(" ", "");
                for(String line:this.profiles){
                    String[] strings=line.split(",");
                    if(strings[0]==name){
                        this.log("This already exists! Please choose a new name!");
                        a=true;
                        break;
                    }
                }
            }
            this.log("Please choose a password!");
            String pw=s.nextLine();
            this.profiles.add(name+","+this.calculatePassword(pw));
            this.availableProfiles.add(name+","+this.calculatePassword(pw));
            for(String p:profiles){
                log(p);
            }
            return name;
        }
        return null;
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
        history=history+","+player.getScore()+","+this.getDate()+","+player.usedCards.size()+",";
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
        ArrayList<String> prevHistory = new ArrayList<>();
        String[] histories=new String[this.players.length];
        //getting all current player histories
        for(int a=0; a<this.players.length;a++){
            histories[a]=this.createHistory(players[a]);
            //iterate player's histories to show them in order
            boolean added=false;
            for(int b=0;b<players[a].history.size();b++){
                String[] lHistory=players[a].history.get(b).split(",");
                if(Integer.parseInt(lHistory[1])<players[a].getScore()){
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
        ArrayList<String> content= new ArrayList<>();

        //get all previous histories from textfile
        try {
            BufferedReader br = new BufferedReader(new FileReader("main/java/entities/userProfiles.txt"));

            String line = br.readLine();

            while (!line.equals("histories")) {
                content.add(line);
                line = br.readLine();
            }
            content.add(line);
            line = br.readLine();
            while (line != null) {
                prevHistory.add(line);
                line = br.readLine();
            }
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
        }catch(IOException e) {
            throw new RuntimeException(e);
        }try {
                PrintWriter pw = new PrintWriter("main/java/entities/userProfiles.txt");


            for(String a:content){
                pw.println(a);
                pw.flush();
            }
            for(String a:prevHistory){
                pw.println(a);
                pw.flush();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
