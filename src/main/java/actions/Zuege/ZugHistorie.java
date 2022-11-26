package actions.Zuege;

import actions.ReUnDo.Runde;
import actions.ReUnDo.Verlauf;
import cards.Card;
import cards.CardColor;

import java.util.logging.Logger;

public class ZugHistorie {

    Action head;
    Action tail;
    Action aktuellePosition;
    Card halter;

    private Logger logger = Logger.getLogger(this.getClass().getName());


    public ZugHistorie(){
        halter=new Card(CardColor.RED,420);
        this.head=new Action(null,halter,null);
        this.tail=new Action(null,halter,null);
    }
    public void actionHinzufuegen(Action neu){
        Action halter = tail.getDavor();
        halter.setDahinter(neu);
        tail.setDavor(neu);
        neu.setDavor(halter);
        neu.setDahinter(tail);
    }




}