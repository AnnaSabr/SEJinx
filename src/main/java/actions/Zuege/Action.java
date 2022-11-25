package actions.Zuege;

import cards.Card;
import entities.Player;

import java.util.ArrayList;

public class Action {
    private ArrayList<String> zuege;
    private Card karte;
    private Player aktiverSpieler;

    public Action(ArrayList<String> zug, Card karte, Player aktiv){
        this.zuege=zug;
        this.karte=karte;
        this.aktiverSpieler=aktiv;
    }

    public ArrayList<String> getZuege() {
        return zuege;
    }

    public Card getKarte() {
        return karte;
    }
}
