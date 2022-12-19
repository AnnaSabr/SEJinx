package ports.outbound;

import actions.Zuege.Action;
import actions.speichern.Storage;
import entities.Player;
import entities.Table;
import persistence.PlayerHistory;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Port to handle database connection, should be implemented as singleton
 */
public interface Database {

    /**
     * Call this to get an instance of this class
     *
     * @return instance of a database object
     */
    //TODO Make this work as singleton if possible
    Database getInstance();

    /**
     * Function to check if a player exists
     *
     * @param name name of the player
     * @return true if player exists, false otherwise
     */
    boolean checkPlayer(String name);

    /**
     * Function to create a new player
     *
     * @param name     name of the player
     * @param password password of this player
     * @return true if created false if otherwise
     */
    boolean createPlayer(String name, String password);

    /**
     * Function to login a player
     *
     * @param name     name of the player
     * @param password password of the player
     * @return true if combination of player and password was found / false if not
     */
    boolean playerLogin(String name, String password);

    /**
     * Function to store the game history of a player
     * !Currently only stores enemys information if enemy has profile in DB!
     *
     * @param history history object storing the important game information
     */
    boolean createHistory(PlayerHistory history);

    /**
     * Function to create a 'Storage'
     *
     * @param storage storage to be created
     * @return true if storage was created, false otherwise
     */
    boolean createSpeicher(Storage storage);

    /**
     * Function to load a speicher object from Storage
     *
     * @param speicher_id the id of the speicher object
     * @return a fully constructed speicher object
     */
    Storage getSpeicher(int speicher_id);

    /**
     * Function that returns a list of all available speicher-objects
     *
     * @return array of ids, null if something failed/nothing was found
     */
    Integer[] getSpeicherList();

}
