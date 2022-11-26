package actions.Zuege;

import cards.Card;
import cards.LuckCard;
import entities.Player;

import java.util.ArrayList;

public class Action {
    private Zuege zug;
    private Card karte;
    private Player aktiverSpieler;

    private LuckCard glueckskarte;

    private Action davor;
    private Action dahinter;

    public Action(Zuege zug, Card karte, Player aktiv){
        this.zug=zug;
        this.karte=karte;
        this.aktiverSpieler=aktiv;
        this.dahinter=null;
        this.davor=null;
    }

    public Action(Zuege zug, LuckCard karte, Player aktiv){
        this.zug=zug;
        this.glueckskarte=karte;
        this.aktiverSpieler=aktiv;
    }

    public Zuege getZug() {
        return zug;
    }

    public Card getKarte() {
        return karte;
    }

    public LuckCard getGlueckskarte() {
        return glueckskarte;
    }
    public Player getAktiverSpieler() {
        return aktiverSpieler;
    }


    public Action getDahinter() {
        return dahinter;
    }

    public Action getDavor() {
        return davor;
    }

    public void setDahinter(Action dahinter) {
        this.dahinter = dahinter;
    }
    public void setDavor(Action davor) {
        this.davor = davor;
    }


}
