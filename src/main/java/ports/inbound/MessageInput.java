package ports.inbound;

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
    String inputCoord();

    /**
     *
     * @param text for question
     * @return answer yes or no
     */
    String yesNo (String text);
}