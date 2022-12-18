package ports.inbound;

import actions.ReUnDo.cards.Card;
import entities.Player;

import java.util.ArrayList;

/**
 * Port for simple message input
 * */
public interface MessageInput {

    String inputAnything();



    /**
     * Input for time between messages
     * @return time
     */
    int inputINTTime();

    /**
     * for single letters and menu choices
     *
     * @return Player input
     */
    String letterInput(String msg);

    /**
     * For int input during the player initialization
     * @return Player input
     * */
    int inputINTPlayerInitialization(String question);

    /**
     * For name input
     * @return Player input
     * */
    String inputName(String question);

    /**
     * To input an AIs difficulty
     * @return difficulty as string
     * */
    String inputLevel();

    /**
     * To input a password
     * @return password as string
     * */
    String inputPasswort(String question);

    /**
     * To input a simple int
     *
     * @return Player input
     */
    int inputINT();

    /**
     * To input a coordinate
     * @return coordinate as string (x,y)
     * */
    String inputCoord(String question);

    /**
     *
     * @param text for question
     * @return answer yes or no
     */
    String yesNo (String text);

    /**
     *
     * @param text question
     * @return choosenAction
     */
    String menueInput(String text);

    /**
     * Function to get input for the maxCards of a player
     * @param maxCards of current player
     * @return number in array of selected maxcard
     * */
    int inputMaxCard(ArrayList<Card> maxCards);

    /**
     * Function to let the player select a card to drop for a luck card
     * @param p the active player
     * @return selected int
     * */
    int inputINTDrawLuckCard(Player p);

}