import actions.Zuege.ZugHistorie;
import cards.Card;
import cards.CardColor;
import cards.CardType;
import cards.LuckCard;
import entities.GameLoop;
import entities.Player;
import entities.Table;
import persistence.DBConnector;
import persistence.PlayerHistory;

import java.sql.Date;
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

        Scanner s = new Scanner(System.in);
        while(true){
            System.out.println("Do you wish to manually control the game flow? [y/n]");
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
        boolean dataSource;
        String data=s.nextLine();

        while(true){
            System.out.println("Press 'y' in order to get data from the database. Press 'n' in order to get data from the textfile.");
            String con = s.nextLine();

            if ("y".equals(con)) {
                dataSource = true;
                break;
            } else if ("n".equals(con)) {
                dataSource = false;
                break;
            } else {
                System.out.println("Not an option! Try again!");
            }
        }
        GameLoop game = new GameLoop(config,manualSleepTime,nextMsgTime, dataSource);

        game.run();
        boolean naechstes = true;
        while (naechstes) {
            System.out.println("Next Game? y for yes, n for no");
            Scanner sc = new Scanner(System.in);
            String eingabe = sc.nextLine();
            if (eingabe.equals("y")) {
                GameLoop gameA = new GameLoop(config, manualSleepTime, nextMsgTime, dataSource);
                ZugHistorie.leeren();
                gameA.run();
            } else if (eingabe.equals("n")) {
                System.out.println("End initialized");
                naechstes = false;
            } else {
                System.out.println("Wrong input.");
            }
        }
    }
}
