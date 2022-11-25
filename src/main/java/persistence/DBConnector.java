package persistence;
import java.sql.*;

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
            String request = "SELECT * FROM Player WHERE name=" + name;
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
}
