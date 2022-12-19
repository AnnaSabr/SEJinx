package actions.Zuege;


import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;

import java.util.ArrayList;

/**
 * double chained List with all moves from the game
 */
public class MoveHistory {

    static Card placeholder = new Card(CardColor.RED, 420);
    static Action head = new Action(null, placeholder, null);
    static Action tail = new Action(null, placeholder, null);


    /**
     * put a new Action in the History
     *
     * @param newAction Action to put into History
     */
    public static void addNewAction(Action newAction) {
        if (tail.getBefore() == null) {
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
     * @return List with all played moves in the Game
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
     * reset for the MoveHistory
     */
    public static void empty() {
        head.setBehind(tail);
        tail.setBefore(head);
    }

    /**
     * @return begin of History
     */
    public static Action getHead() {
        return head;
    }

    /**
     * @return end of History
     */
    public static Action getTail() {
        return tail;
    }


}