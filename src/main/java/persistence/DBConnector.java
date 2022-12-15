package persistence;
import actions.ReUnDo.Round;
import actions.Zuege.Action;
import actions.Zuege.Moves;
import actions.speichern.Storage;
import adapter.secondary.OutputConsole;
import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.LuckCard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entities.*;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Stack;

public class DBConnector {

    private static final DBConnector singleton = new DBConnector();
    private Connection con;
    private OutputConsole outCon;
    private DBConnector(){
        String url = "jdbc:mysql://localhost:3306/jinx?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String user = "root";
        String pass = "passwort";
        outCon= new OutputConsole();

        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            String text=e.getMessage();
            outCon.exceptionMessage(text);
        }
    }



    public static DBConnector getInstance(){
        return singleton;
    }

    /**
     * Function to check if a player exists
     * @param name name of the player
     * @return boolean true if player exists / false if not
     * */
    public boolean checkPlayer(String name){
        try{
            Statement stm = con.createStatement();
            String request = "SELECT * FROM Player WHERE name='" + name + "'";
            ResultSet rs = stm.executeQuery(request);

            //check if an entry was found
            if(!rs.isBeforeFirst()){
                return false;
            }

        }catch (Exception e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return false;
        }

        //return true if an entry was found
        return true;
    }

    /**
     * Function to create a player
     * @param name name of the player
     * @param password password used by the player
     * @return boolean true if player was created / false if not
     * */
    public boolean createPlayer(String name, String password){
        try{
            int passwordHash = password.hashCode();

            String request = "INSERT INTO Player (name, password) VALUES (?,?)";
            PreparedStatement stm = con.prepareStatement(request);

            stm.setString(1, name);
            stm.setInt(2, passwordHash);

            //push changes to db
            int affectedRows = stm.executeUpdate();

            //check if something was created
            if (affectedRows > 0){
                return true;
            }

        } catch (SQLException e){
            String text="Something went wrong while trying to save the player";
            outCon.errorSelfMessage(text);
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return false;
        }

        //something must have failed
        return false;
    }

    /**
     * Function to "login" a player
     * @param name name of the player
     * @param password used by the player
     * @return true if combination of player and password was found / false if not
     * */
    public boolean playerLogin(String name, String password){

        try{

            int passwordHash = password.hashCode();

            Statement stm = con.createStatement();
            String request = "SELECT * FROM Player WHERE name='" + name + "'" + "AND password = " + passwordHash;
            ResultSet rs = stm.executeQuery(request);

            //check if an entry was found
            if(!rs.isBeforeFirst()){
                return false;
            }
        }catch (Exception e){
            String text="Something went wrong while checking the player login";
            outCon.errorSelfMessage(text);
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return false;
        }

        //return true if an entry was found
        return true;
    }

    /**
     * Function to store the game history of a player
     * !Currently only stores enemys information if enemy has profile in DB!
     * @param history history object storing the important game information
     * */
    public boolean createHistory(PlayerHistory history){
        Player player = history.getPlayer();
        Player[] enemys = history.getEnemys();

        try{
            //check if all information is given and player even exists in DB
            if(!history.isComplete() && !checkPlayer(player.getName())){
                return false;
            }

            //handle player information
            String request = "INSERT INTO PlayerHistory (player, saved, score, numLuckCards) VALUES (?,?,?,?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //set information in statement
            stm.setString(1, player.getName());
            stm.setDate(2, history.getDate());
            stm.setInt(3,player.getScore());
            stm.setInt(4, history.getLuckCardCount());

            //push changes to db
            int affectedRows = stm.executeUpdate();

            //check if something was created
            if (affectedRows == 0){
                return false;
            }

            //handle enemy information
            ResultSet generatedKeys = stm.getGeneratedKeys();

            int id = -1;
            if(generatedKeys.next()){
                id = generatedKeys.getInt(1);
            }

            if(id == -1){
                //TODO handle case, delete history if no id was returned
                return false;
            }


            String request_enemy = "INSERT INTO PlayerHistoryEnemy (ph_id, enemy, scorce) VALUES (?,?,?)";
            PreparedStatement stm_enemy = con.prepareStatement(request_enemy, Statement.RETURN_GENERATED_KEYS);
            for(Player e : enemys) {
                if(checkPlayer(e.getName())){
                    stm_enemy.setInt(1, id);
                    stm_enemy.setString(2, e.getName());
                    stm_enemy.setInt(3, e.getScore());
                    //push changes to db
                    stm_enemy.executeUpdate();
                }
            }

            //everything stored in DB
            return true;
        } catch (SQLException e){
            String text="Something went wrong while trying to save the playerhistory";
            outCon.errorSelfMessage(text);
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return false;
        }
    }

    /**
     * Gets all histories of the player
     * @param name name of the player
     * @return null if player is not found, no history is found or an error occurred.
     * Returns PlayerHistory object if all information was found (enemies might be null)
     * */
    public PlayerHistory[] getPlayerHistory(String name){

        ArrayList<PlayerHistory> playerHistories = new ArrayList<>();

        //check if player exists
        if(!checkPlayer(name)){
            return null;
        }

        try{

            String request = "SELECT * FROM PlayerHistory WHERE player='" + name + "'";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(request);

            //check if an entry was found
            if(!rs.isBeforeFirst()){
                return null;
            }

            //get all histories of player
            while(rs.next()){
                Player p = new Player(rs.getString("player"),0,false,true);
                p.setScore(rs.getInt("score"));

                //create new player history
                PlayerHistory ph = new PlayerHistory(p,rs.getInt("numLuckCards"),rs.getDate("saved"),null);


                //get information of enemies if there is some
                String request_enemy = "SELECT * FROM PlayerHistoryEnemy WHERE ph_id=" + rs.getInt("id");
                Statement stm_enemy = con.createStatement();
                ResultSet rs_enemy = stm_enemy.executeQuery(request_enemy);

                //check if an entry was found
                if(rs_enemy.isBeforeFirst()){
                    ArrayList<Player> enemies = new ArrayList<>();
                    //add all found enemies
                    while (rs_enemy.next()){
                        Player enemie = new Player(rs_enemy.getString("enemy"),0,false,true);
                        //set their score accordingly
                        enemie.setScore(rs_enemy.getInt("scorce"));
                        enemies.add(enemie);
                    }
                    //add enemies to playerhistory
                    ph.setEnemys(enemies.toArray(new Player[0]));
                }
                //add to all histories
                playerHistories.add(ph);
            }

            //sort player histories by score
            playerHistories.sort((o1, o2) -> {
                if(o1.getPlayer().getScore() < o2.getPlayer().getScore()){
                    return 1;
                }else if(o1.getPlayer().getScore() == o2.getPlayer().getScore()){
                    return 0;
                }
                return -1;
            });

            return playerHistories.toArray(new PlayerHistory[0]);
        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return null;
        }
    }

    /**
     * Function to create a 'Speicherstand'
     * */
    public boolean createSpeicher(Storage storage){

        //TODO CREATE ROUND 0 in DB-SCRIPT TO MAKE SURE IT EXISTS
        //create the speicher reference
        int speicher_id = handleSpeicher(Date.valueOf("2002-12-10")); //TODO CHANGE TO REAL DATE

        if(speicher_id == 0){
            return false;
        }

        //create entry for every round in the speicher object
        for(Round r: storage.getRoundHistory()){

            //create the runde itself first
            int runden_id = handleRunde(speicher_id);

            //make sure runde was created successfully
            if(runden_id == 0){
                return false;
            }

            //add tisch for runde
            int tisch_id = handleTisch(r.getTableStatus(), runden_id);

            //make sure tisch was created
            if(tisch_id == 0){
                return false;
            }

            //add all players for this round
            for(Player p: r.getAllPlayers()){

                //create each player
                int spieler_id = handleSpieler(p, runden_id);

                //check if a player was created
                if(spieler_id == 0){
                    return false;
                }
            }
        }

        //create entry for every action in the speicher object
        for(Action a : storage.getActionHistory()){
            //handle the action separately
            int action_id = handleAction(a, speicher_id);

            //check if action was created successfully
            if(action_id == 0){
                return false;
            }
        }

        //speicher was created!
        return true;
    }

    /**
     * Function that creates a new spieler for a round
     * @param spieler spieler to be created
     * @param runde id of round
     * @return 0 if nothing was created, id of spieler otherwise
     * */
    private int handleSpieler(Player spieler, int runde){

        try{
            //handle player information
            String request = "INSERT INTO spieler (name, cards, luckCards, score, sleeptime, " +
                    "manualNextMsg, diceCount, rolls, active, ai, r_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //parse stacks into JSON
            Gson gson = new Gson();
            String cards = gson.toJson(spieler.getCards());
            String luckCards = gson.toJson(spieler.getLuckCards());

            //set information
            stm.setString(1, spieler.getName());
            stm.setString(2, cards);
            stm.setString(3, luckCards);
            stm.setInt(4, spieler.getScore());
            stm.setInt(5, spieler.getSleepTime());
            stm.setBoolean(6, spieler.isManualNextMsg());
            stm.setInt(7, spieler.getDiceCount());
            stm.setInt(8, spieler.getRolls());
            stm.setBoolean(9, spieler.isActive());

            //check if the player is actually an AI, otherwise set null
            if(spieler instanceof EasyKI){
                stm.setString(10, "EasyKI");
            }else if(spieler instanceof MediumAI){
                stm.setString(10, "MediumKI");
            }else if(spieler instanceof AIPLayer3){
                stm.setString(10, "AIPLayer3");
            }else{
                stm.setNull(10,Types.VARCHAR);
            }

            if(runde == 0) {
                stm.setNull(11, Types.INTEGER);
            }else{
                stm.setInt(11, runde);
            }

            //push changes to db
            int affectedRows = stm.executeUpdate();

            //check if something was created
            if (affectedRows == 0){
                return 0;
            }

            //handle generated id
            ResultSet generatedKeys = stm.getGeneratedKeys();

            int id = 0;
            if(generatedKeys.next()){
                id = generatedKeys.getInt(1);
            }

            return id;


        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return 0;
        }
    }

    /**
     * Function that creates a new tisch
     * @param tisch tisch that needs to be persisted
     * @param runde id of the runde it belongs to
     * @return 0 if nothing was created, id of tisch otherwise
     * */
    private int handleTisch(Table tisch, int runde){

        try{
            //handle tisch information
            String request = "INSERT INTO tisch (cardStack, luckCardStack, field, r_id) VALUES (?,?,?,?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //parse stacks into json
            Gson gson = new Gson();
            String cardStack = gson.toJson(tisch.getCardStack());
            String luckCardStack = gson.toJson(tisch.getLuckStack());
            String field = gson.toJson(tisch.getField());

            //set information in statement
            stm.setString(1, cardStack);
            stm.setString(2, luckCardStack);
            stm.setString(3, field);
            stm.setInt(4, runde);

            //push changes to db
            int affectedRows = stm.executeUpdate();

            //check if something was created
            if (affectedRows == 0){
                return 0;
            }

            //handle generated id
            ResultSet generatedKeys = stm.getGeneratedKeys();

            int id = 0;
            if(generatedKeys.next()){
                id = generatedKeys.getInt(1);
            }

            return id;

        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return 0;
        }catch (Exception e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return 0;
        }

    }

    /**
     * Function that creates a new runde
     * @param speicher speicher the runde should be connected to
     * @return 0 if something could not be created, id of runde if created
     * */
    private int handleRunde(int speicher){
        try{

            String request =  "INSERT INTO runde (s_id) VALUES (?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);


            //Set information in statement
            stm.setInt(1, speicher);

            //push to DB
            int affectedRows = stm.executeUpdate();

            //check if something was created
            if(affectedRows == 0){
                return 0;
            }

            //get id of created speicher
            ResultSet generatedKeys = stm.getGeneratedKeys();

            int id = 0;
            if(generatedKeys.next()){
                id = generatedKeys.getInt(1);
            }

            //return id if found
            return id;

        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return 0;
        }
    }

    /**
     * Overloaded function that creates an Action and links it to a Speicher
     * @param action action to be created
     * @param speicher speicher_id to be linked to this action
     * @return 0 if nothing was created, id of action otherwise
     * */
    private int handleAction(Action action, int speicher){

        try{
            //create player for this action, runde = 0 default
            int player_id = handleSpieler(action.getActivePlayer(), 0);

            //check if player created
            if(player_id == 0){
                return 0;
            }

            //prepare request
            String request = "INSERT INTO action (zug, luckCard, card, p_id, s_id) VALUES (?,?,?,?,?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //parse cards into JSON
            Gson gson = new Gson();
            String luckCard = gson.toJson(action.getLuckCard());
            String card = gson.toJson(action.getCard());

            //set information
            stm.setString(1, action.getMove().toString());
            stm.setString(2, luckCard);
            stm.setString(3, card);
            stm.setInt(4, player_id);
            stm.setInt(5, speicher);

            //push to DB
            int affectedRows = stm.executeUpdate();

            //check if something was created
            if(affectedRows == 0){
                return 0;
            }

            //get id of created speicher
            ResultSet generatedKeys = stm.getGeneratedKeys();

            int id = 0;
            if(generatedKeys.next()){
                id = generatedKeys.getInt(1);
            }

            //return id if found
            return id;
        }catch (Exception e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return 0;
        }
    }

    /**
     * Function that creates a new Speicher
     * @param date day of creation
     * @return 0 if something went wrong, id of created Speicher otherwise
     * */
    private int handleSpeicher(Date date){
        try{
            String request = "INSERT INTO speicher (created) VALUES (?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //set information in statement
            stm.setDate(1, date);

            //push changes to db
            int affectedRows = stm.executeUpdate();

            //check if something was created
            if(affectedRows == 0){
                return 0;
            }

            //get id of created speicher
            ResultSet generatedKeys = stm.getGeneratedKeys();

            int id = 0;
            if(generatedKeys.next()){
                id = generatedKeys.getInt(1);
            }

            //return id if found
            return id;

        } catch (SQLException e){
            String text="Something went wrong while trying to save the Speicherstand";
            outCon.errorSelfMessage(text);
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return 0;
        }
    }

    /**
     * Function to load a speicher object from Storage
     * @param speicher_id the id of the speicher object
     * @return a fully constructed speicher object
     * */
    public Storage getSpeicher(int speicher_id){
        //Speicher object to be returned
        Storage newStorage = new Storage();
        try{
            //only load something if the Speicher exists in DB
            Statement stm = con.createStatement();
            String request_speicher = "SELECT * FROM speicher WHERE id=" + speicher_id;
            ResultSet rs = stm.executeQuery(request_speicher);

            //check if an entry was found
            if(!rs.isBeforeFirst()){
                return null;
            }

            //get all rounds of this speicher
            String request_rounds = "SELECT * FROM runde WHERE s_id=" + speicher_id;
            rs = stm.executeQuery(request_rounds);

            //check if an entry was found
            if(!rs.isBeforeFirst()){
                return null;
            }

            ArrayList<Round> runden = new ArrayList<>();

            //create all existing rounds
            while(rs.next()){
                //runde to be added when filled
                Round round;

                //look for table
                Table table = loadTable(rs.getInt("id"));

                //check if a table was found
                if(table == null){
                    return null;
                }

                //look for player
                ArrayList<Player> players = loadPlayers(rs.getInt("id"));

                //something failed
                if(players == null){
                    return null;
                }

                //create new runde with loaded information
                runden.add(new Round(players, table));
            }

            //load the action of this speicher
            ArrayList<Action> actions = loadActions(speicher_id);

            if(actions == null){
                return null;
            }

            //setup new speicher object
            newStorage.setRoundHistory(runden);
            newStorage.setActionHistory(actions);
            //return result
            return newStorage;
        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return null;
        }
    }

    /**
     * Function that returns a list of all available speicher-objects
     * @return array of ids, null if something failed/nothing was found
     * */
    public Integer[] getSpeicherList(){
        try{
            //prepare statement
            Statement stm = con.createStatement();
            String request = "SELECT id FROM speicher";
            ResultSet rs = stm.executeQuery(request);

            //check if something was found
            if(!rs.isBeforeFirst()){
                return null;
            }
            ArrayList<Integer> speicher = new ArrayList<>();
            while(rs.next()){
                speicher.add(rs.getInt("id"));
            }
            return speicher.toArray(new Integer[]{});
        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return null;
        }
    }
    /**
     * Function to load actions of a speicher
     * @param s_id id of the speicher referring to
     * @return ArrayList of actions, null if failed
     * */
    private ArrayList<Action> loadActions(int s_id){
        try{
            //get all actions performed during this speicher
            Statement stm = con.createStatement();
            String request_actions = "SELECT * FROM action WHERE s_id=" + s_id;
            ResultSet rs = stm.executeQuery(request_actions);

            //check if an entry was found
            if(!rs.isBeforeFirst()){
                return null;
            }

            ArrayList<Action> actions = new ArrayList<>();
            Gson gson = new Gson();

            while(rs.next()){
                Action action = null;

                String zug_s = rs.getString("zug");
                Moves zug = null;
                switch (zug_s){
                    case "SKIPPED" -> zug = Moves.SKIPPED;
                    case "GOTCARDFROMTABLE" -> zug = Moves.GOTCARDFROMTABLE;
                    case "USEDLUCKYCARD" -> zug = Moves.USEDLUCKYCARD;
                    case "DROPPEDCARD" -> zug = Moves.DROPPEDCARD;
                    case "GOTLUCKYCARD" -> zug = Moves.GOTLUCKYCARD;
                    case "MANIPULATION" -> zug = Moves.MANIPULATION;
                }

                LuckCard luckCard = gson.fromJson(rs.getString("luckCard"),LuckCard.class);
                Card card = gson.fromJson(rs.getString("card"), Card.class);

                //get the player who performed this action
                Player player = loadPlayer(rs.getInt("p_id"));

                if(luckCard == null && card != null){
                    action = new Action(zug, card, player);
                }else if(card == null && luckCard !=null){
                    action = new Action(zug, luckCard, player);
                }

                actions.add(action);
            }
            return actions;
        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return null;
        }
    }

    /**
     * Function to load a single player for an action
     * @param p_id player id
     * @return Player object, null if not found
     * */
    private Player loadPlayer(int p_id){
        try {
            Statement stm = con.createStatement();
            String request = "SELECT * FROM spieler WHERE id=" + p_id;
            ResultSet rs = stm.executeQuery(request);

            //check if something was found
            if (!rs.isBeforeFirst()) {
                return null;
            }

            //Gson configs
            Gson gson = new Gson();
            Type cardToken = new TypeToken<ArrayList<Card>>(){}.getType();
            Type luckToken = new TypeToken<ArrayList<LuckCard>>(){}.getType();

            Player p = null;
            while(rs.next()){
                String name = rs.getString("name");
                ArrayList<Card> cards = gson.fromJson(rs.getString("actions/ReUnDo/cards"),cardToken);
                ArrayList<LuckCard> luckCards = gson.fromJson(rs.getString("luckCards"), luckToken);
                int score = rs.getInt("score");
                int sleeptime = rs.getInt("sleeptime");
                boolean manualNextMsg = rs.getBoolean("manualNextMsg");
                int diceCount = rs.getInt("diceCount");
                int rolls = rs.getInt("rolls");
                boolean active = rs.getBoolean("active");
                String ai = rs.getString("ai");

                //create the type of player as saved
                if(ai != null){
                    p = switch (ai) {
                        case "EasyKI" -> new EasyKI(name, sleeptime, manualNextMsg,true);
                        case "MediumKI" -> new MediumAI(name, sleeptime, manualNextMsg,true);
                        case "AIPLayer3" -> new AIPLayer3(name, sleeptime, manualNextMsg,true);
                        default -> new Player(name, sleeptime, manualNextMsg,true);
                    };
                }else{
                    p = new Player(name, sleeptime, manualNextMsg,true);
                }

                //set all attributes
                p.setCards(cards);
                p.setLuckCards(luckCards);
                p.setScore(score);
                p.setDiceCount(diceCount);
                p.setRolls(rolls);
                if(active){
                    p.setActive();
                }
            }
            //return created player
            return p;
        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return null;
        }

    }
    /**
     * Function to load all players of a round
     * @param r_id id of the round the players are referring to
     * @return null if failed, ArrayList of Players otherwise
     * */
    private ArrayList<Player> loadPlayers(int r_id){
            try{
                Statement stm = con.createStatement();
                String request = "SELECT * FROM spieler WHERE r_id=" + r_id;
                ResultSet rs = stm.executeQuery(request);

                //check if something was found
                if(!rs.isBeforeFirst()){
                    return null;
                }

                //Gson configs
                Gson gson = new Gson();
                Type cardToken = new TypeToken<ArrayList<Card>>(){}.getType();
                Type luckToken = new TypeToken<ArrayList<LuckCard>>(){}.getType();

                ArrayList<Player> players = new ArrayList<>();
                while(rs.next()){
                    Player p;
                    String name = rs.getString("name");
                    ArrayList<Card> cards = gson.fromJson(rs.getString("actions/ReUnDo/cards"),cardToken);
                    ArrayList<LuckCard> luckCards = gson.fromJson(rs.getString("luckCards"), luckToken);
                    int score = rs.getInt("score");
                    int sleeptime = rs.getInt("sleeptime");
                    boolean manualNextMsg = rs.getBoolean("manualNextMsg");
                    int diceCount = rs.getInt("diceCount");
                    int rolls = rs.getInt("rolls");
                    boolean active = rs.getBoolean("active");
                    String ai = rs.getString("ai");

                    //create the type of player as saved
                    if(ai != null){
                        p = switch (ai) {
                            case "EasyKI" -> new EasyKI(name, sleeptime, manualNextMsg,true);
                            case "MediumKI" -> new MediumAI(name, sleeptime, manualNextMsg,true);
                            case "AIPLayer3" -> new AIPLayer3(name, sleeptime, manualNextMsg,true);
                            default -> new Player(name, sleeptime, manualNextMsg,true);
                        };
                    }else{
                        p = new Player(name, sleeptime, manualNextMsg,true);
                    }

                    //set all attributes
                    p.setCards(cards);
                    p.setLuckCards(luckCards);
                    p.setScore(score);
                    p.setDiceCount(diceCount);
                    p.setRolls(rolls);
                    if(active){
                        p.setActive();
                    }

                    //add to result set
                    players.add(p);
                }

                return players;
            }catch (SQLException e){
                String exception=e.getMessage();
                outCon.exceptionMessage(exception);
                return null;
            }
    }

    /**
     * Function to load a table from the DB
     * @param r_id id of the round the table is referring to
     * @return Table-Object, null if something failed
     * */
    private Table loadTable(int r_id){
        try{
            //load the table belonging to that round
            Statement stm = con.createStatement();
            String request = "SELECT * FROM tisch WHERE r_id=" + r_id;
            ResultSet rs = stm.executeQuery(request);

            if(!rs.isBeforeFirst()){
                return null;
            }

            //Gson configs
            Gson gson = new Gson();
            Type cardToken = new TypeToken<Stack<Card>>(){}.getType();
            Type luckToken = new TypeToken<Stack<LuckCard>>(){}.getType();

            //table to be returned
            Table table = null;
            while(rs.next()){
                Stack<Card> cardStack = gson.fromJson(rs.getString("cardStack"), cardToken);
                Stack<LuckCard> luckCards = gson.fromJson(rs.getString("luckCardStack"), luckToken);
                Card[][] field = gson.fromJson(rs.getString("field"), Card[][].class);

                table = new Table(false);
                table.setCardStack(cardStack);
                table.setLuckStack(luckCards);
                table.setField(field);
            }

            return table;
        }catch (SQLException e){
            String exception=e.getMessage();
            outCon.exceptionMessage(exception);
            return null;
        }
    }

}
