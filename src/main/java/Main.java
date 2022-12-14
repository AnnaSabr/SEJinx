import actions.Zuege.ZugHistorie;
import entities.GUI;
import entities.GameLoop;
import adapter.primary.InputConsole;
import adapter.secondary.OutputConsole;



public class Main {

    private static OutputConsole outCon = new OutputConsole();
    private static InputConsole inCon = new InputConsole();


    public static void main(String[] args) {
        GUI g=new GUI();
        // does user want to load a default config
        boolean config = false ;
        boolean manualSleepTime = false;
        int nextMsgTime = 200;
        boolean gui=false;
        while(true){
            outCon.simpleMessage("Do you want to use a GUI? [y/n]");
            String choice=inCon.letterInput();

            if("y".equals(choice)){
                gui=true;
                break;
            }else if("n".equals(choice)){
                gui=false;
                break;
            }
            outCon.simpleMessage("Invalid choice");
        }
        while(true){
            if(!gui){
                outCon.jinxMessage("Do you wish to load a config file? [y/n]");

                String con = inCon.letterInput();

                if ("y".equals(con)) {
                    config = true;
                    break;
                } else if ("n".equals(con)) {
                    outCon.jinxMessage("Shuffling the cards!");
                    break;
                } else {
                    outCon.jinxMessage("Not an option! Try again!");
                }
            }else{
                //TODO change with presenter
                config=g.returningYesOrNO("Do you wish to load a config file?");
                break;
            }
        }


        while(true){
            if(!gui){
                outCon.jinxMessage("Do you wish to manually control the game flow? [y/n]");
                String con = inCon.letterInput();

                if ("y".equals(con)) {
                    manualSleepTime = true;
                    break;
                } else if ("n".equals(con)) {
                    outCon.jinxMessage("How long should the time between messages be? [ms]");
                    try{
                        nextMsgTime = inCon.inputINTTime();
                        break;
                    }catch (Exception e){
                        outCon.errorSelfMessage("Please enter a valid number!");
                    }
                } else {
                    outCon.jinxMessage("Not an option! Try again!");
                }
            }else{
                //TODO change to presenter
                manualSleepTime=g.returningYesOrNO("Do you wish to manually control the game flow? [y/n]");
                if(!manualSleepTime){
                    nextMsgTime=g.getInputNumber("How long should the time between messages be? [ms]");
                    break;
                }else{
                    break;
                }

            }
        }
        boolean dataSource;
        //String data=inCon.inputAnything();

        while(true){
            if(!gui){
                outCon.jinxMessage("Press 'y' in order to get data from the database. Press 'n' in order to get data from the textfile.");
                String con = inCon.letterInput();

                if ("y".equals(con)) {
                    dataSource = true;
                    break;
                } else if ("n".equals(con)) {
                    dataSource = false;
                    break;
                } else {
                    outCon.jinxMessage("Not an option! Try again!");
                }
            }else{
                //TODO use presenter
                dataSource=g.returningYesOrNO("Do you wish to load data from the database? Otherwise data will be taken from a textfile.");
                break;
            }
        }
        GameLoop game = new GameLoop(config,manualSleepTime,nextMsgTime, dataSource, gui);

        game.run();
        boolean naechstes = true;
        while (naechstes) {
            if(!gui){
                outCon.jinxMessage("Next Game? y for yes, n for no");

                String eingabe = inCon.letterInput();
                if (eingabe.equals("y")) {
                    GameLoop gameA = new GameLoop(config, manualSleepTime, nextMsgTime, dataSource, gui);
                    ZugHistorie.leeren();
                    gameA.run();
                } else if (eingabe.equals("n")) {
                    outCon.jinxMessage("End initialized");
                    naechstes = false;
                } else {
                    outCon.errorSelfMessage("Wrong input.");
                }
            }else{
                boolean startRound=g.returningYesOrNO("Would you like to start a new game?");
                if(startRound){
                    GameLoop gameA = new GameLoop(config, manualSleepTime, nextMsgTime, dataSource, gui);
                    ZugHistorie.leeren();
                    gameA.run();
                }else{
                    //TODO show game over message on gui
                    naechstes=false;
                }
            }
        }
    }
}
