package actions.speichern;

import actions.ReUnDo.Runde;
import actions.ReUnDo.Verlauf;
import actions.Zuege.Action;
import actions.Zuege.ZugHistorie;

import java.util.ArrayList;

public  class Speicher {

    private ArrayList<Runde> verlaufRunden;

    private ArrayList<Action> verlaufAction;
    public Speicher(){
        this.verlaufRunden=null;
        this.verlaufAction=null;
    }

    public ArrayList<Runde> getVerlaufRunden() {
        return verlaufRunden;
    }

    public void setVerlaufAction(ArrayList<Action> verlaufAction) {
        this.verlaufAction = verlaufAction;
    }

    public void setVerlaufRunden(ArrayList<Runde> verlaufRunden) {
        this.verlaufRunden = verlaufRunden;
    }

    public ArrayList<Action> getVerlaufAction() {
        return verlaufAction;
    }



    public Runde zumLadenRunden(){
        int size=verlaufRunden.size();
        Runde letzteRunde =verlaufRunden.get(size);
        return letzteRunde;
    }

    public Verlauf zumLadenVerlauf(){
        Verlauf verlaufsKopie = new Verlauf();
            int size=verlaufRunden.size();
            for(int a =1; a<=size; a++){
                verlaufsKopie.rundeHinzufuegen(verlaufRunden.get(a));
            }
        return verlaufsKopie;
    }

    public void zugHistorieUeberschreiben(){
       int size =verlaufAction.size();
       for (int a =1; a<=size; a++){
           ZugHistorie.actionHinzufuegen(verlaufAction.get(a));
       }
    }
}
