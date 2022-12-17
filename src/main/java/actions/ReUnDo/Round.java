package actions.ReUnDo;

import entities.Player;
import entities.Table;

import java.util.ArrayList;


/**
 * One Round from the Game with all information about Player and Table
 */
public class Round {

    private ArrayList<Player> allPlayers;
    private Table tableStatus;
    private Player active;
    private ArrayList<String> highscores;

    private int playerCount =0;
    private Round before;
    private Round behind;

    public Round(ArrayList<Player> allPlayers, Table tableStatus) {
        this.allPlayers = allPlayers;
        this.tableStatus = tableStatus;
        this.before = null;
        this.behind = null;
        this.highscores=new ArrayList<>();

    }

    /**
     * put new Round in front of this
     *
     * @param before round to put in front of this
     */
    public void setBefore(Round before) {
        this.before = before;
    }

    /**
     * @return round before this one
     */
    public Round getBefore() {
        return before;
    }

    /**
     * put new Round behind this
     *
     * @param behind Round to put behind this
     */
    public void setBehind(Round behind) {
        this.behind = behind;
    }

    /**
     * @return the next Round from this
     */
    public Round getBehind() {
        return behind;
    }

    /**
     * List with the players from this Round
     * @return all players from this Round
     */
    public ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     * @return table with his status of Cards etc
     */
    public Table getTableStatus() {
        return tableStatus;
    }

    /**
     *
     * @return Count of Players in the Round
     */
    public int getPlayerCount() {
        playerCount = allPlayers.size();
        return playerCount;
    }

    /**
     *
     * @return activ Player in this Round
     */
    public Player getActive() {
        return active;
    }

    /**
     *
     * @param active Player wich played this round
     */
    public void setActive(Player active) {
        this.active = active;
    }

    /**
     *
     * @return ArrayList with highscores
     */
    public ArrayList<String> getHighscores() {
        return highscores;
    }

    /**
     *
     * @param highscores ArrayList to save in Round objcet
     */
    public void setHighscores(ArrayList<String> highscores) {
        this.highscores = highscores;
    }
}