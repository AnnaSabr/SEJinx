package actions.Zuege;

import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.LuckCard;
import entities.Player;

/**
 * Stellt einen einzelnen Spielzug da
 */
public class Action {
    private Zuege zug;
    private Card karte;
    private Player aktiverSpieler;

    private LuckCard glueckskarte;

    private Action davor;
    private Action dahinter;

    public Action(Zuege zug, Card karte, Player aktiv) {
        this.zug = zug;
        this.karte = karte;
        this.aktiverSpieler = aktiv;
        this.dahinter = null;
        this.davor = null;
    }

    public Action(Zuege zug, LuckCard karte, Player aktiv) {
        this.zug = zug;
        this.glueckskarte = karte;
        this.aktiverSpieler = aktiv;
        this.dahinter = null;
        this.davor = null;
    }

    /**
     * @return die Art des Spielzuges
     */
    public Zuege getZug() {
        return zug;
    }

    /**
     * @return gespielte Karte in dem Zug
     */
    public Card getKarte() {
        return karte;
    }

    /**
     *
     * @return gespielte LuckyKarte
     */
    public LuckCard getGlueckskarte() {
        return glueckskarte;
    }

    /**
     * @return den Spieler, der den Zug getaetigt hat
     */
    public Player getAktiverSpieler() {
        return aktiverSpieler;
    }


    /**
     * @return den folgenden Spielzug
     */
    public Action getDahinter() {
        return dahinter;
    }

    /**
     * @return den vorherigen Spielzug
     */
    public Action getDavor() {
        return davor;
    }

    /**
     * @param dahinter neuer Zug der dem aktuelln Zug folgen soll
     */
    public void setDahinter(Action dahinter) {
        this.dahinter = dahinter;
    }

    /**
     * @param davor neuer Zug der dem aktuellen vor geschobenw erden soll
     */
    public void setDavor(Action davor) {
        this.davor = davor;
    }


}
