package actions.Zuege;

import actions.ReUnDo.Runde;
import actions.ReUnDo.Verlauf;
import cards.Card;
import cards.CardColor;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ZugHistorie {

    static Action head;
    static Action tail;
    Action aktuellePosition;
    Card platzhalter;


    private Logger logger = Logger.getLogger(this.getClass().getName());


    public ZugHistorie(){
        platzhalter=new Card(CardColor.RED,420);
        this.head=new Action(null,platzhalter,null);
        this.tail=new Action(null,platzhalter,null);
    }
    public static void actionHinzufuegen(Action neu){
        Action halter = tail.getDavor();
        halter.setDahinter(neu);
        tail.setDavor(neu);
        neu.setDavor(halter);
        neu.setDahinter(tail);
    }

    public static ArrayList<Action> zumSpeichern(){
        ArrayList<Action> zugVerlauf= new ArrayList<>();
        Action start=head.getDahinter();
        while (!start.equals(tail)){
            zugVerlauf.add(start);
            start=start.getDahinter();
        }
        return zugVerlauf;
    }





}