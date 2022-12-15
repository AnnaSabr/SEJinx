package actions.Zuege;

import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.LuckCard;
import entities.Player;

/**
 * Stellt einen einzelnen Spielzug da
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
     * @return die Art des Spielzuges
     */
    public Moves getMove() {
        return move;
    }

    /**
     * @return gespielte Karte in dem Zug
     */
    public Card getCard() {
        return card;
    }

    /**
     *
     * @return gespielte LuckyKarte
     */
    public LuckCard getLuckCard() {
        return luckCard;
    }

    /**
     * @return den Spieler, der den Zug getaetigt hat
     */
    public Player getActivePlayer() {
        return activPlayer;
    }


    /**
     * @return den folgenden Spielzug
     */
    public Action getBehind() {
        return behind;
    }

    /**
     * @return den vorherigen Spielzug
     */
    public Action getBefore() {
        return before;
    }

    /**
     * @param behind neuer Zug der dem aktuelln Zug folgen soll
     */
    public void setBehind(Action behind) {
        this.behind = behind;
    }

    /**
     * @param before neuer Zug der dem aktuellen vor geschobenw erden soll
     */
    public void setBefore(Action before) {
        this.before = before;
    }


}
