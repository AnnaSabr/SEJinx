package adapter.primary;

import actions.ReUnDo.cards.Card;
import adapter.secondary.OutputConsole;
import entities.AIPLayer3;
import entities.EasyKI;
import entities.MediumAI;
import entities.Player;
import ports.inbound.MessageInput;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implements an input adapter for the input port
 * Takes input from the console and provides the gameloop with parsed input
 * */
public class InputConsole implements MessageInput {
    private OutputConsole outCon= new OutputConsole();

    /**
     * Lets the player input any string without checks
     * @return Simple input string
     * */
    @Override
    public String inputAnything() {
        Scanner sc = new Scanner(System.in);
        String inputPlayer = sc.nextLine();
        return inputPlayer;
    }

    /**
     * Lets the player input a time in ms
     * @return time in ms as int
     * */
    @Override
    public int inputINTTime() {
        Scanner sc = new Scanner(System.in);
        int inputPlayer=sc.nextInt();
        return inputPlayer;
    }

    /**
     *  Takes an integer input from console
     * @return integer entered from player
     * */
    @Override
    public int inputINT() {
        Scanner sc = new Scanner(System.in);
        int inputINT=sc.nextInt();
        return inputINT;
    }

    /**
     * Takes a single character input from console
     * @return single character entered from player
     * */
    @Override
    public String letterInput(String msg) {
        Scanner sc= new Scanner(System.in);
        String inputLetter=sc.nextLine();
        return inputLetter;
    }

    /**
     *
     * @param text question
     * @return wich Action should be chosen
     */
    @Override
    public String menueInput(String text) {
        Scanner sc= new Scanner(System.in);
        String inputLetter=sc.nextLine();
        return inputLetter;
    }

    /**
     * Takes input regarding the creation of player at the start of a game
     * @return int, input done by player
     * */
    @Override
    public int inputINTPlayerInitialization(String question) {
        outCon.jinxMessage(question);
        Scanner sc = new Scanner(System.in);
        int inputPlayer=sc.nextInt();
        return inputPlayer;
    }

    /**
     * Takes a name as input
     * @return the name entered by the player
     * */
    @Override
    public String inputName(String question) {
        outCon.jinxMessage(question);
        Scanner sc= new Scanner(System.in);
        String inputName=sc.nextLine();
        return inputName;
    }

    /**
     * Takes a difficulty level as input
     * Easy, medium, hard are considered valid inputs
     * @return difficulty level as string
     * */
    @Override
    public String inputLevel() {
        String name=inputName("Please enter a Name for your KI:");
        String level="";
        String ki="";
        if (!name.equals("")) {
            ki=name;
            outCon.simpleMessage("Please choose a level for your KI:  " +
                    "easy / medium / hard");
            level=inputAnything();
            switch (level) {
                case "easy" -> ki = name + "," + "easy";
                case "medium" -> ki = name + "," + "medium";
                case "hard" -> ki = name + "," + "hard";
                default -> outCon.simpleMessage("Not an option. Try again.");
            }
        } else {
            outCon.simpleMessage("Wrong input.");
        }
        System.out.println(ki);
        return ki;
    }

    /**
     * Lets the player input a password
     * @return password as plain string !needs to be hashed after call!
     * */
    @Override
    public String inputPasswort(String question) {
        outCon.jinxMessage(question);
        Scanner sc= new Scanner(System.in);
        String passwort=sc.nextLine();
        return passwort;
    }

    /**
     * Takes a coordinate input
     * @return String of coordinates like x,y - comma seperated
     * */
    @Override
    public String inputCoord(String question) {
        Scanner sc = new Scanner(System.in);
        String inputINT = sc.nextLine();
        return inputINT;
    }

    @Override
    public String yesNo(String question) {
        outCon.jinxMessage(question);
        Scanner sc = new Scanner(System.in);
        String yesno = sc.nextLine();
        return yesno;
    }

    /**
     * Function to display the maxCards of a player
     * @param maxCards of current player
     * @return number in array of selected maxcard
     * */
    @Override
    public int inputMaxCard(ArrayList<Card> maxCards){
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }

    @Override
    public int inputINTDrawLuckCard(Player p) {
        return inputINT();
    }
}
