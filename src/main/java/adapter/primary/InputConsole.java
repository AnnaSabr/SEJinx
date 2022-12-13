package adapter.primary;

import ports.inbound.MessageInput;

import java.util.Scanner;

public class InputConsole implements MessageInput {

    @Override
    public String inputAnything() {
        Scanner sc = new Scanner(System.in);
        String inputPlayer = sc.nextLine();
        return inputPlayer;
    }

    @Override
    public int inputINTTime() {
        Scanner sc = new Scanner(System.in);
        int inputPlayer=sc.nextInt();
        return inputPlayer;
    }

    @Override
    public int inputINT() {
        Scanner sc = new Scanner(System.in);
        int inputINT=sc.nextInt();
        return inputINT;
    }

    @Override
    public String letterInput() {
        Scanner sc= new Scanner(System.in);
        String inputLetter=sc.nextLine();
        return inputLetter;
    }

    @Override
    public int inputINTPlayerInitialization() {
        Scanner sc = new Scanner(System.in);
        int inputPlayer=sc.nextInt();
        return inputPlayer;
    }

    @Override
    public String inputName() {
        Scanner sc= new Scanner(System.in);
        String inputName=sc.nextLine();
        return inputName;
    }

    @Override
    public String inputLevel() {
        Scanner sc= new Scanner(System.in);
        String level=sc.nextLine();
        return level;
    }

    @Override
    public String inputPasswort() {
        Scanner sc= new Scanner(System.in);
        String passwort=sc.nextLine();
        return passwort;
    }

    @Override
    public String inputCoord() {
        Scanner sc = new Scanner(System.in);
        String inputINT = sc.nextLine();
        return inputINT;
    }
}
