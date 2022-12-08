import actions.Zuege.ZugHistorie;
import entities.GameLoop;
import entities.InputConsole;
import entities.OutputConsole;



public class Main {

    private static OutputConsole outCon = new OutputConsole();
    private static InputConsole inCon = new InputConsole();


    public static void main(String[] args) {
        // does user want to load a default config
        boolean config = false ;
        boolean manualSleepTime = false;
        int nextMsgTime = 200;
        while(true){
            outCon.simpleMessage("Do you wish to load a config file? [y/n]");

            String con = inCon.inputConsole();

            if ("y".equals(con)) {
                config = true;
                break;
            } else if ("n".equals(con)) {
                outCon.simpleMessage("Shuffling the cards!");
                break;
            } else {
                outCon.simpleMessage("Not an option! Try again!");
            }
        }


        while(true){
            outCon.simpleMessage("Do you wish to manually control the game flow? [y/n]");
            String con = inCon.inputConsole();

            if ("y".equals(con)) {
                manualSleepTime = true;
                break;
            } else if ("n".equals(con)) {
                outCon.simpleMessage("How long should the time between messages be? [ms]");
                try{
                    nextMsgTime = inCon.inputConsoleINT();
                    break;
                }catch (Exception e){
                    outCon.simpleMessage("Please enter a valid number!");
                }
            } else {
                outCon.simpleMessage("Not an option! Try again!");
            }
        }
        boolean dataSource;
        String data=inCon.inputConsole();

        while(true){
            outCon.simpleMessage("Press 'y' in order to get data from the database. Press 'n' in order to get data from the textfile.");
            String con = inCon.inputConsole();

            if ("y".equals(con)) {
                dataSource = true;
                break;
            } else if ("n".equals(con)) {
                dataSource = false;
                break;
            } else {
                outCon.simpleMessage("Not an option! Try again!");
            }
        }
        GameLoop game = new GameLoop(config,manualSleepTime,nextMsgTime, dataSource);

        game.run();
        boolean naechstes = true;
        while (naechstes) {
            outCon.simpleMessage("Next Game? y for yes, n for no");

            String eingabe = inCon.inputConsole();
            if (eingabe.equals("y")) {
                GameLoop gameA = new GameLoop(config, manualSleepTime, nextMsgTime, dataSource);
                ZugHistorie.leeren();
                gameA.run();
            } else if (eingabe.equals("n")) {
                outCon.simpleMessage("End initialized");
                naechstes = false;
            } else {
                outCon.simpleMessage("Wrong input.");
            }
        }
    }
}
