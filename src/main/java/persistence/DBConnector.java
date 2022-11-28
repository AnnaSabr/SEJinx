package persistence;
import actions.ReUnDo.Runde;
import actions.Zuege.Action;
import actions.speichern.Speicher;
import com.google.gson.Gson;
import entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

public class DBConnector {

    private static final DBConnector singleton = new DBConnector();
    private Connection con;
    private DBConnector(){
        String url = "jdbc:mysql://localhost:3306/jinx?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String user = "root";
        String pass = "passwort";

        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("Database connection established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("DB-Connector initialized");
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
            e.printStackTrace();
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
            System.out.println("Something went wrong while trying to save the player");
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
            System.out.println("Something went wrong while checking the player login");
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
            System.out.println("Something went wrong while trying to save the playerhistory");
            e.printStackTrace();
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
                Player p = new Player(rs.getString("player"),0,false);
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
                        Player enemie = new Player(rs_enemy.getString("enemy"),0,false);
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
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function to create a 'Speicherstand'
     * */
    public boolean createSpeicher(Speicher speicher){

        //create the speicher reference
        int speicher_id = handleSpeicher(Date.valueOf("22.02.2022"));

        //create entry for every round in the speicher object
        for(Runde r: speicher.getVerlaufRunden()){

            //create the runde itself first
            int runden_id = handleRunde(r.getAction(), speicher_id);

            //make sure runde was created successfully
            if(runden_id == 0){
                return false;
            }

            //add tisch for runde
            int tisch_id = handleTisch(r.getTischStand(), runden_id);

            //make sure tisch was created
            if(tisch_id == 0){
                return false;
            }

            //add all players for this round
            for(Player p: r.getSpieler()){

                //create each player
                int spieler_id = handleSpieler(p, runden_id);

                //check if a player was created
                if(spieler_id == 0){
                    return false;
                }
            }
        }

        //create entry for every action in the speicher object
        for(Action a : speicher.getVerlaufAction()){
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
            String request = "INSTER INTO spieler (name, cards, luckCards, score, sleeptime, " +
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

            stm.setInt(11, runde);


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
            e.printStackTrace();
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
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * Function that creates a new runde
     * @param action action that was made in that round
     * @return 0 if something could not be created, id of runde if created
     * */
    private int handleRunde(Action action, int speicher){
        try{
            //handle the action for this round
            int action_id = handleAction(action);

            //check if action was created
            if (action_id == 0){
                return 0;
            }

            String request =  "INSERT INTO spieler (a_id, s_id) VALUES (?,?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);


            //Set information in statement
            stm.setInt(1, action_id);
            stm.setInt(2, speicher);

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
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Function that creates an action
     * @param action action to be created
     * @return 0 if nothing was created, id of action otherwise
     * */
    private int handleAction(Action action){
        try{
            //create player for this action, runde = 0 default
            int player_id = handleSpieler(action.getAktiverSpieler(), 0);

            //check if player created
            if(player_id == 0){
                return 0;
            }

            //prepare request
            String request = "INSERT INTO action (zug, luckCard, card, p_id) VALUES (?,?,?,?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //parse cards into JSON
            Gson gson = new Gson();
            String luckCard = gson.toJson(action.getGlueckskarte());
            String card = gson.toJson(action.getKarte());

            //set information
            stm.setString(1, action.getZug().toString());
            stm.setString(2, luckCard);
            stm.setString(3, card);
            stm.setInt(4, player_id);

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
            e.printStackTrace();
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
            int player_id = handleSpieler(action.getAktiverSpieler(), 0);

            //check if player created
            if(player_id == 0){
                return 0;
            }

            //prepare request
            String request = "INSERT INTO action (zug, luckCard, card, p_id, s_id) VALUES (?,?,?,?,?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //parse cards into JSON
            Gson gson = new Gson();
            String luckCard = gson.toJson(action.getGlueckskarte());
            String card = gson.toJson(action.getKarte());

            //set information
            stm.setString(1, action.getZug().toString());
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
            e.printStackTrace();
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
            String request = "INSERT INTO Speicher (date) VALUES (?)";
            PreparedStatement stm = con.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);

            //set information in statement
            stm.setDate(1, date); //TODO CHANGE TO CURRENT DATE

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
            System.out.println("Something went wrong while trying to save the Speicherstand");
            e.printStackTrace();
            return 0;
        }
    }
}
