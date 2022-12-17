package ports.inbound;

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
    String letterInput();

    /**
     * For int input during the player initialization
     * @return Player input
     * */
    int inputINTPlayerInitialization();

    /**
     * For name input
     * @return Player input
     * */
    String inputName();

    /**
     * To input an AIs difficulty
     * @return difficulty as string
     * */
    String inputLevel();

    /**
     * To input a password
     * @return password as string
     * */
    String inputPasswort();

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
}