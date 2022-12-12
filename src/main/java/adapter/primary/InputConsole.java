package adapter.primary;

import ports.inbound.MessageInput;

import java.util.Scanner;

public class InputConsole implements MessageInput {

    @Override
    public String inputConsole() {
        Scanner sc = new Scanner(System.in);
        String inputPlayer = sc.nextLine();
        return inputPlayer;
    }

    @Override
    public int inputConsoleINT() {
        Scanner sc = new Scanner(System.in);
        int inputPlayer=sc.nextInt();
        return inputPlayer;
    }

    @Override
    public String letterInput() {
        Scanner sc= new Scanner(System.in);
        String inputLetter=sc.nextLine();
        return inputLetter;
    }
}
