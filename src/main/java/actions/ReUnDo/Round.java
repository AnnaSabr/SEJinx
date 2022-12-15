package actions.ReUnDo;

import entities.Player;
import entities.Table;

import java.util.ArrayList;


/**
 * Abbild einer gespielten Runde mit allen Spielvariablen, die sich veraendern
 */
public class Round {

    private ArrayList<Player> allPlayers;
    private Table tableStatus;
    private Player active;

    private int playerCount =0;
    private Round before;
    private Round behind;

    public Round(ArrayList<Player> allPlayers, Table tableStatus) {
        this.allPlayers = allPlayers;
        this.tableStatus = tableStatus;
        this.before = null;
        this.behind = null;


    }

    /**
     * fuegt eine neue Runde vor dieser ein
     *
     * @param before wird eingefuegt
     */
    public void setBefore(Round before) {
        this.before = before;
    }

    /**
     * @return gibt die vorherige Runde zurueck
     */
    public Round getBefore() {
        return before;
    }

    /**
     * fuegt eine neue Runde hinter diese ein
     *
     * @param behind die dahinter eingefuegt werden soll
     */
    public void setBehind(Round behind) {
        this.behind = behind;
    }

    /**
     * @return die folgende Runde
     */
    public Round getBehind() {
        return behind;
    }

    /**
     * @return die Spieler der Runde und somit ihre Spielstaende
     */
    public ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     * @return die Tisch positionen der Runde
     */
    public Table getTableStatus() {
        return tableStatus;
    }

    /**
     *
     * @return Anzahl der Spieler in der Runde
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

    public void setActive(Player active) {
        this.active = active;
    }
}