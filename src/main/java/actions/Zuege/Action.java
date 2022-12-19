package actions.Zuege;

import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.LuckCard;
import entities.Player;

/**
 * one Action made by a single Player
 */
public class Action {
    private Moves move;
    private Card card;
    private Player activPlayer;

    private LuckCard luckCard;

    private Action before;
    private Action behind;

    public Action(Moves move, Card card, Player active) {
        this.move = move;
        this.card = card;
        this.activPlayer = active;
        this.behind = null;
        this.before = null;
    }

    public Action(Moves move, LuckCard luckCard, Player active) {
        this.move = move;
        this.luckCard = luckCard;
        this.activPlayer = active;
        this.behind = null;
        this.before = null;
    }

    /**
     * @return Kind of the move the Player did
     */
    public Moves getMove() {
        return move;
    }

    /**
     * @return played card
     */
    public Card getCard() {
        return card;
    }

    /**
     * @return played luckcard
     */
    public LuckCard getLuckCard() {
        return luckCard;
    }

    /**
     * @return Player who has done the action
     */
    public Player getActivePlayer() {
        return activPlayer;
    }


    /**
     * @return Action wich was played after this
     */
    public Action getBehind() {
        return behind;
    }

    /**
     * @return Action wich was played before this
     */
    public Action getBefore() {
        return before;
    }

    /**
     * @param behind Action wich is put after this
     */
    public void setBehind(Action behind) {
        this.behind = behind;
    }

    /**
     * @param before Action wich is put before this
     */
    public void setBefore(Action before) {
        this.before = before;
    }


}
