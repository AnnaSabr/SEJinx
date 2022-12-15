package actions.Zuege;


import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;

import java.util.ArrayList;

/**
 * doppelt verkettete Liste mit den gespielten zuegen im Spiel
 */
public class MoveHistory {

    static Card placeholder = new Card(CardColor.RED, 420);
    static Action head = new Action(null, placeholder, null);
    static Action tail = new Action(null, placeholder, null);


    /**
     * Fuegt einen weiteren Spielzug der Historie hinzu
     *
     * @param newAction Action die hinzugefuegt werden soll
     */
    public static void addNewAction(Action newAction) {
        if (tail.getBefore()==null){
            tail.setBefore(head);
            head.setBehind(tail);
        }
        Action hold = tail.getBefore();
        hold.setBehind(newAction);
        tail.setBefore(newAction);
        newAction.setBefore(hold);
        newAction.setBehind(tail);
    }

    /**
     * @return eine ArrayListe mit allen bisher gespielten Zuegen
     */
    public static ArrayList<Action> toSave() {
        ArrayList<Action> moves = new ArrayList<>();
        Action begin = head;
        begin = begin.getBehind();
        while (!begin.equals(tail)) {
            moves.add(begin);
            begin = begin.getBehind();
        }
        return moves;
    }

    /**
     * resetet die ZugHistorie, so dass sie leer ist
     */
    public static void empty() {
        head.setBehind(tail);
        tail.setBefore(head);
    }

    /**
     * @return start der Zughistorie
     */
    public static Action getHead() {
        return head;
    }

    /**
     * @return Ende der Zughistorie
     */
    public static Action getTail() {
        return tail;
    }


}