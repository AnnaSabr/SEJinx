package actions.Zuege;


import cards.Card;
import cards.CardColor;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * doppelt verkettete Liste mit den gespielten zuegen im Spiel
 */
public class ZugHistorie {

    static Action head;
    static Action tail;

    Card platzhalter;


    private Logger logger = Logger.getLogger(this.getClass().getName());


    public ZugHistorie(){
        platzhalter=new Card(CardColor.RED,420);
        this.head=new Action(null,platzhalter,null);
        this.tail=new Action(null,platzhalter,null);
    }

    /**
     * Fuegt einen weiteren Spielzug der Historie hinzu
     * @param neu Action die hinzugefuegt werden soll
     */
    public static void actionHinzufuegen(Action neu){
        Action halter = tail.getDavor();
        halter.setDahinter(neu);
        tail.setDavor(neu);
        neu.setDavor(halter);
        neu.setDahinter(tail);
    }

    /**
     *
     * @return eine ArrayListe mit allen bisher gespielten Zuegen
     */
    public static ArrayList<Action> zumSpeichern(){
        ArrayList<Action> zugVerlauf= new ArrayList<>();
        Action start=head;
        while (!start.equals(tail)){
            zugVerlauf.add(start);
            start=start.getDahinter();
        }
        zugVerlauf.add(tail);
        return zugVerlauf;
    }

    /**
     * resetet die ZugHistorie, so das sie leer ist
     */
    public static void leeren(){
        head.setDahinter(tail);
        tail.setDavor(head);
    }

    /**
     *
     * @return start der Zughistorie
     */
    public static Action getHead(){
        return head;
    }

    /**
     *
     * @return Ende der Zughistorie
     */
    public static Action getTail(){
        return tail;
    }





}