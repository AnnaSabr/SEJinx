package actions.Zuege;

import actions.ReUnDo.Runde;
import actions.ReUnDo.Verlauf;

import java.util.logging.Logger;

public class ZugHistorie {
    private Verlauf verlauf;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public ZugHistorie(){
        this.verlauf=new Verlauf();
    }

    public void setVerlauf(Verlauf verlauf) {
        this.verlauf = verlauf;
    }

    public void historieAnzeigen(){
        int zug =1;
        Runde anfang=verlauf.getHead().getDahinter();
        while (!anfang.equals(verlauf.getTail())){
            System.out.println(zug+". ");
            zug++;
            anfang=anfang.getDahinter();

        }
    }


}