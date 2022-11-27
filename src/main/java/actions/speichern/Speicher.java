package actions.speichern;

import actions.ReUnDo.Runde;
import actions.Zuege.Action;

import java.util.ArrayList;

public  class Speicher {

    private ArrayList<Runde> verlaufRunden;
    private ArrayList<Action> verlaufAction;

    public Speicher(){
        this.verlaufAction=null;
        this.verlaufAction=null;
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

    public ArrayList<Runde> getVerlaufRunden() {
        return verlaufRunden;
    }
}
