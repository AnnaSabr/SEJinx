package actions.speichern;

import actions.ReUnDo.Round;
import actions.ReUnDo.Course;
import actions.Zuege.Action;
import actions.Zuege.MoveHistory;

import java.util.ArrayList;

/**
 * Object with all information to save the game
 */
public class Storage {

    private ArrayList<Round> roundHistory;
    private ArrayList<Action> actionHistory;

    public Storage() {
        this.roundHistory = null;
        this.actionHistory = null;
    }

    /**
     * update the ActionHistory
     *
     * @param actionHistory ArrayList with the new correct order of Actions
     */
    public void setActionHistory(ArrayList<Action> actionHistory) {
        this.actionHistory = actionHistory;
    }

    /**
     * update RoundHistory
     * @param roundHistory ArrayListe with the new correct order of Rounds
     */
    public void setRoundHistory(ArrayList<Round> roundHistory) {
        this.roundHistory = roundHistory;
    }

    /**
     *
     * @return ArrayList with ActionHistory
     */
    public ArrayList<Action> getActionHistory() {
        return actionHistory;
    }

    /**
     *
     * @return ArrayList with RoundHistory
     */
    public ArrayList<Round> getRoundHistory() {
        return roundHistory;
    }
    /**
     * to get the last Round element of the Game
     *
     * @return last played Round
     */
    public Round getLastRound() {
        int size = roundHistory.size();
        return roundHistory.get(size - 1);
    }


    /**
     * build new Course History from List
     *
     * @return Copy of CourseHistory
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
     * Overwrite all Actions
     */
    public void overwriteActions() {
        int size = actionHistory.size();
        for (int a = 0; a < size; a++) {
            MoveHistory.addNewAction(actionHistory.get(a));
        }
    }

}
