import cards.Card;
import cards.CardColor;
import cards.CardType;
import cards.LuckCard;
import entities.GameLoop;
import entities.Player;
import entities.Table;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // does user want to load a default config
        boolean config = false ;
        boolean manualSleepTime = false;
        int nextMsgTime = 200;
        while(true){

            System.out.println("Do you wish to load a config file? [y/n]");
            Scanner s = new Scanner(System.in);
            String con = s.nextLine();

            if ("y".equals(con)) {
                config = true;
                break;
            } else if ("n".equals(con)) {
                System.out.println("Shuffling the cards!");
                break;
            } else {
                System.out.println("Not an option! Try again!");
            }
        }

        while(true){
            System.out.println("Do you wish to manually control the game flow? [y/n]");
            Scanner s = new Scanner(System.in);
            String con = s.nextLine();

            if ("y".equals(con)) {
                manualSleepTime = true;
                break;
            } else if ("n".equals(con)) {
                System.out.println("How long should the time between messages be? [ms]");
                try{
                    nextMsgTime = s.nextInt();
                    break;
                }catch (Exception e){
                    System.out.println("Please enter a valid number!");
                }
            } else {
                System.out.println("Not an option! Try again!");
            }
        }
        GameLoop game = new GameLoop(config,manualSleepTime,nextMsgTime);

        game.run();

        System.out.println("DONE");
    }
}
