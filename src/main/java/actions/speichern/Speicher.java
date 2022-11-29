package actions.speichern;

import actions.ReUnDo.Runde;
import actions.ReUnDo.Verlauf;
import actions.Zuege.Action;
import actions.Zuege.ZugHistorie;

import java.util.ArrayList;

/**
 * ein Objekt, welches alle Relevanten Informationen zum Spielstand enthaelt und zum Speichern in der DB gedacht ist
 */
public class Speicher {

    private ArrayList<Runde> verlaufRunden;
    private ArrayList<Action> verlaufAction;

    public Speicher() {
        this.verlaufRunden = null;
        this.verlaufAction = null;
    }

    /**
     * Aktualisert die Klassen VerlaufsListe
     *
     * @param verlaufAction ArrayListe mit den Spielzuegen die neu dem Objekt zu geschieben erden soll
     */
    public void setVerlaufAction(ArrayList<Action> verlaufAction) {
        this.verlaufAction = verlaufAction;
    }

    /**
     * @param verlaufRunden ArrayListe mit Verlauf die in das Speicherobjekt sollen
     */
    public void setVerlaufRunden(ArrayList<Runde> verlaufRunden) {
        this.verlaufRunden = verlaufRunden;
    }

    public ArrayList<Action> getVerlaufAction() {
        return verlaufAction;
    }
    public ArrayList<Runde> getVerlaufRunden() {
        return verlaufRunden;
    }
    /**
     * Gibt die Letzte Runde aus der ArrayListe der Runden zurueck
     *
     * @return letzte gespielte Runde
     */
    public Runde zumLadenRunden() {
        int size = verlaufRunden.size();
        return verlaufRunden.get(size - 1);
    }


    /**
     * Erstellt einen neuen Verlauf aus der ArrayListe
     *
     * @return Verlaufsobjekt
     */
    public Verlauf zumLadenVerlauf() {
        Verlauf verlaufsKopie = new Verlauf();
        int size = verlaufRunden.size();
        for (int a = 0; a < size; a++) {
            verlaufsKopie.rundeHinzufuegen(verlaufRunden.get(a));
        }
        return verlaufsKopie;
    }

    /**
     * Ueberschreibt die bisher gespielten Actionen
     */
    public void zugHistorieUeberschreiben() {
        int size = verlaufAction.size();
        for (int a = 0; a < size; a++) {
            ZugHistorie.actionHinzufuegen(verlaufAction.get(a));
        }
    }
}
