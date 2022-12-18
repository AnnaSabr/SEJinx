import actions.Zuege.MoveHistory;
import adapter.primary.InOutGUI;
import entities.GUI;
import entities.GameLoop;
import adapter.primary.InputConsole;
import adapter.secondary.OutputConsole;
import ports.inbound.MessageInput;
import ports.outbound.MessageOutput;


public class Main {

    private static MessageOutput outCon = new OutputConsole();
    private static MessageInput inCon = new InputConsole();
    private static InOutGUI inOut = null;


    public static void main(String[] args) {

        // does user want to load a default config
        boolean config = false;
        boolean manualSleepTime = false;
        int nextMsgTime = 200;
        boolean gui = false;
        while (true) {
            outCon.simpleMessage("Do you want to use a GUI? [y/n]");
            String choice = inCon.letterInput("[y/n]");
            if ("y".equals(choice)) {
                gui = true;
                GUI g = new GUI(false);
                inOut = new InOutGUI(g);
                outCon = inOut;
                inCon = inOut;

                break;
            } else if ("n".equals(choice)) {
                gui = false;
                break;
            }
            outCon.simpleMessage("Invalid choice");
        }
        while (true) {
            String con = inCon.yesNo("Do you wish to load a config file? [y/n]");
            if ("y".equals(con)) {
                config = true;
                break;
            } else if ("n".equals(con)) {
                outCon.configJinxMessage("Shuffling the cards!");
                break;
            } else {
                outCon.configJinxMessage("Not an option! Try again!");
            }
        }

        while (true) {
            String con = inCon.yesNo("Do you wish to manually control the game flow? [y/n]");
            if ("y".equals(con)) {
                manualSleepTime = true;
                break;
            } else if ("n".equals(con)) {
                outCon.configJinxMessage("How long should the time between messages be? [ms]");
                try {
                    nextMsgTime = inCon.inputINTTime();
                    break;
                } catch (Exception e) {
                    outCon.errorSelfMessage("Please enter a valid number!");
                }
            } else {
                outCon.configJinxMessage("Not an option! Try again!");
            }
        }
        boolean dataSource;
        //String data=inCon.inputAnything();

        while (true) {
            String con = inCon.yesNo("Press 'y' in order to get data from the database. Press 'n' in order to get data from the textfile.");

            if ("y".equals(con)) {
                dataSource = true;
                break;
            } else if ("n".equals(con)) {
                dataSource = false;
                break;
            } else {
                outCon.configJinxMessage("Not an option! Try again!");
            }
        }

        GameLoop game = new GameLoop(config, manualSleepTime, nextMsgTime, dataSource, gui);
        game.run();

        boolean next = true;
        while (next) {
            String nextGame = inCon.yesNo("Next Game? y for yes, n for no");
            if (nextGame.equals("y")) {
                GameLoop gameA = new GameLoop(config, manualSleepTime, nextMsgTime, dataSource, gui);
                MoveHistory.empty();
                gameA.run();
            } else if (nextGame.equals("n")) {
                outCon.configJinxMessage("End initialized");
                next = false;
            } else {
                outCon.errorSelfMessage("Wrong input.");
            }
        }
    }
}
