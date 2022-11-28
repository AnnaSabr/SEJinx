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

        System.out.println("STARTED");
        DBConnector conn = DBConnector.getInstance();

        /*
        Player p = new Player("Hans",0,false);
        Player[] enemys = new Player[3];
        enemys[0] = new Player("Werner",0,false);
        enemys[1] = new Player("Rasmus", 0,false);
        enemys[2] = new Player("Robert", 0, false);

        //date format yyyy-[m]m-[d]d
        PlayerHistory ph = new PlayerHistory(p,3, Date.valueOf("2002-12-10"),enemys);

        System.out.println(conn.createHistory(ph));
        */
        //System.out.println(conn.createPlayer("Hans","pw"));
        //System.out.println(conn.createPlayer("Werner","pw"));
        //System.out.println(conn.createPlayer("Rasmus","pw"));
        //System.out.println(conn.createPlayer("Robert","pw"));

        //PlayerHistory[] playerHistory = conn.getPlayerHistory("Hans");


        //System.exit(0);
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
        boolean naechstes = true;
        while (naechstes);
        {
            System.out.println("Next Game? y for yes, n for no");
            Scanner sc = new Scanner(System.in);
            String eingabe=sc.nextLine();
            if (eingabe.equals("y")) {
                game.run();
            }
            else  if (eingabe.equals("n")){
                System.out.println("End initialized");
                naechstes=false;
            }
            else{
                System.out.println("Wrong input.");
            }
        }

        System.out.println("DONE");
    }
}
