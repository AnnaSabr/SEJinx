package persistence;

import entities.Player;

import java.sql.Date;

/**
 * Object to represent a players game history
 * Used as DTO between DBConnector and core
 */
public class PlayerHistory {

    //player data
    private Player player;
    private int luckCardCount;
    private Date date;

    //enemy data
    private Player[] enemys;

    /**
     * Create a new playerHistory to store in DB
     *
     * @param player        main player linked to this history
     * @param luckCardCount amount of luckcards played by player
     * @param date          date of the game
     * @param enemys        other players who participated
     */
    public PlayerHistory(Player player, int luckCardCount, Date date, Player[] enemys) {
        this.player = player;
        this.luckCardCount = luckCardCount;
        this.date = date;
        this.enemys = enemys;
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getLuckCardCount() {
        return luckCardCount;
    }

    public void setLuckCardCount(int luckCardCount) {
        this.luckCardCount = luckCardCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Player[] getEnemys() {
        return enemys;
    }

    public void setEnemys(Player[] enemys) {
        this.enemys = enemys;
    }

    /**
     * Returns true, if all important information is there
     */
    public boolean isComplete() {
        return this.date != null && this.player != null && this.enemys != null;
    }


}
