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

    int inputINTPlayerInitialization();

    String inputName();

    String inputLevel();

    String inputPasswort();

    /**
     *
     * @return
     */
    int inputINT();

    String inputCoord();
}