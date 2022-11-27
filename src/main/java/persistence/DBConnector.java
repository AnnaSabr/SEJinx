package persistence;
import entities.Player;

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
            e.printStackTrace();
            return null;
        }
    }
}
