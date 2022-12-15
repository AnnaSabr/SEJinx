package actions.speichern;

import actions.ReUnDo.Round;
import actions.ReUnDo.Course;
import actions.Zuege.Action;
import actions.Zuege.MoveHistory;

import java.util.ArrayList;

/**
 * ein Objekt, welches alle Relevanten Informationen zum Spielstand enthaelt und zum Speichern in der DB gedacht ist
 */
public class Storage {

    private ArrayList<Round> roundHistory;
    private ArrayList<Action> actionHistory;

    public Storage() {
        this.roundHistory = null;
        this.actionHistory = null;
    }

    /**
     * Aktualisert die Klassen VerlaufsListe
     *
     * @param actionHistory ArrayListe mit den Spielzuegen die neu dem Objekt zu geschieben erden soll
     */
    public void setActionHistory(ArrayList<Action> actionHistory) {
        this.actionHistory = actionHistory;
    }

    /**
     * @param roundHistory ArrayListe mit Verlauf die in das Speicherobjekt sollen
     */
    public void setRoundHistory(ArrayList<Round> roundHistory) {
        this.roundHistory = roundHistory;
    }

    public ArrayList<Action> getActionHistory() {
        return actionHistory;
    }
    public ArrayList<Round> getRoundHistory() {
        return roundHistory;
    }
    /**
     * Gibt die Letzte Runde aus der ArrayListe der Runden zurueck
     *
     * @return letzte gespielte Runde
     */
    public Round getLastRound() {
        int size = roundHistory.size();
        return roundHistory.get(size - 1);
    }


    /**
     * Erstellt einen neuen Verlauf aus der ArrayListe
     *
     * @return Verlaufsobjekt
     */
    public Course HistoryToLoad() {
        Course courseCopy = new Course();
        int size = roundHistory.size();
        for (int a = 0; a < size; a++) {
            courseCopy.addRound(roundHistory.get(a));
        }
        return courseCopy;
    }

    /**
     * Ueberschreibt die bisher gespielten Actionen
     */
    public void overwriteActions() {
        int size = actionHistory.size();
        for (int a = 0; a < size; a++) {
            MoveHistory.addNewAction(actionHistory.get(a));
        }
    }

}
