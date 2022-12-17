package adapter.primary;

import ports.inbound.MessageInput;

import java.util.Scanner;

/**
 * Implements an input adapter for the input port
 * Takes input from the console and provides the gameloop with parsed input
 * */
public class InputConsole implements MessageInput {

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
    public String letterInput() {
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
        System.out.println(question);
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
        System.out.println(question);
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
        Scanner sc= new Scanner(System.in);
        String level=sc.nextLine();
        return level;
    }

    /**
     * Lets the player input a password
     * @return password as plain string !needs to be hashed after call!
     * */
    @Override
    public String inputPasswort(String question) {
        System.out.println(question);
        Scanner sc= new Scanner(System.in);
        String passwort=sc.nextLine();
        return passwort;
    }

    /**
     * Takes a coordinate input
     * @return String of coordinates like x,y - comma seperated
     * */
    @Override
    public String inputCoord() {
        Scanner sc = new Scanner(System.in);
        String inputINT = sc.nextLine();
        return inputINT;
    }

    @Override
    public String yesNo(String question) {
        System.out.println(question);
        Scanner sc = new Scanner(System.in);
        String yesno = sc.nextLine();
        return yesno;
    }
}
