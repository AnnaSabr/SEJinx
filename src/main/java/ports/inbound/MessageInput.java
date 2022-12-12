package ports.inbound;

public interface MessageInput {

    String inputConsole();

    int inputConsoleINT();


    /**
     * for single letters and menu choices
     *
     * @return Player input
     */
    String letterInput();
}