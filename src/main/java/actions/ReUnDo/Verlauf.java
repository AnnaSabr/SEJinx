package actions.ReUnDo;


import entities.OutputConsole;
import entities.Player;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Eine doppeltverkette Liste zum chronologischen Darstellen der einzelnen Runden im Spiel
 */
public class Verlauf {

    private Runde head;
    private Runde tail;
    private Runde aktuellePosition;
    private OutputConsole outCon;


    public Verlauf() {
        this.head = new Runde(null, null);
        this.tail = new Runde(null, null);

        this.head.setDahinter(tail);
        this.tail.setDavor(head);
        this.aktuellePosition = tail;
        this.outCon=new OutputConsole();
    }


    /**
     * setzt eine neue Runde ans ende des Verlaufs
     *
     * @param neu Runde die eingefuegt werden soll
     */
    public void rundeHinzufuegen(Runde neu) {
        Runde halter = tail.getDavor();
        halter.setDahinter(neu);
        tail.setDavor(neu);
        neu.setDavor(halter);
        neu.setDahinter(tail);
    }

    /**
     * @param runde die geprueft werden soll, ob sie der Anfang oder das Ende ist
     * @return ob es Kopf oder Ende ist
     */
    public boolean headOrTail(Runde runde) {
        boolean leer = false;
        if (runde.equals(head) || runde.equals(tail) || runde.equals(null)) {
            leer = true;
        }
        return leer;
    }

    /**
     * Menue zum Waehlen der geplanten Re und Un Do Schritte
     *
     * @return
     */
    public Runde jump() {
        aktuellePosition = tail;


        while (true) {
            String log="""
                    Choose your Manipulation!
                    S - Show regular status
                    J - choosen status
                    K - jump back
                    L - jump further
                    P - leave
                            \n""";
            outCon.jinxMessage(log);
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            if (input.equals("S")) {
                outCon.jinxMessage("regular status:\n");
                rundeAnzeigen(tail.getDavor());
            } else if (input.equals("J")) {
                if (!headOrTail(aktuellePosition)) {
                    outCon.jinxMessage("new choosen status:");
                    rundeAnzeigen(aktuellePosition);
                } else {
                    outCon.jinxMessage("No new status choosen.");
                }

            } else if (input.equals("K")) {
                unDo();
            } else if (input.equals("L")) {
                reDo();
            } else if (input.equals("P")) {
                if (!headOrTail(aktuellePosition)) {
                    return aktuellePosition;
                }
                return tail;
            } else {
                outCon.jinxMessage("incorrect input.");
            }
        }
    }

    /**
     * geht im Verlauf einen Schritt zurueck
     */
    public void unDo() {
        if (!headOrTail(aktuellePosition.getDavor())) {
            outCon.jinxMessage("One step back");
            aktuellePosition = aktuellePosition.getDavor();
        } else {
            outCon.jinxMessage("already at the beginning");
        }

    }

    /**
     * geht im Verlauf einen Schritt vor
     */
    public void reDo() {
        if (!aktuellePosition.equals(tail)) {
            outCon.jinxMessage("One step further");
            aktuellePosition = aktuellePosition.getDahinter();
        } else {
            outCon.jinxMessage("already at the end");
        }
    }

    /**
     * Gibt im Terminal eine einzelne Uebergeben Runde aus
     *
     * @param auswahl einzelne Runde
     */
    public void rundeAnzeigen(Runde auswahl) {
        for (Player player : auswahl.getSpieler()) {
            String info="Spieler: " + player.getName() + "\n" +
                    "Handkarten: " + player.getCards() + "\n" +
                    "LuckyKarten: " + player.getLuckCards() + "\n";
            outCon.loggerMessage(info);
        }
        String info= "Tisch Kartenstapel: " + auswahl.getTischStand().getCardStack() + "\n" +
                "Tisch Luckykartenstapel: " + auswahl.getTischStand().getLuckStack() + "\n" +
                "Spielfeld:\n " + auswahl.getTischStand().toString();
        outCon.loggerMessage(info);
    }

    /**
     * gibt im Terminal den ganzen Verlauf aus
     */
    public void verlaufAnzeigen() {
        int runde = 1;
        Runde start = head.getDahinter();
        while (!start.equals(tail)) {
            String info=runde + ". Zug: \n" + runde;
            outCon.loggerMessage(info);
            for (Player player : start.getSpieler()) {
                info="Spieler: " + player.getName() + "\n" +
                        "Handkarten: " + player.getCards() + "\n" +
                        "LuckyKarten: " + player.getLuckCards() + "\n";
                outCon.loggerMessage(info);
            }
            info="Tisch Kartenstapel: " + start.getTischStand().getCardStack() + "\n" +
                    "Tisch Luckykartenstapel: " + start.getTischStand().getLuckStack() + "\n" +
                    "Spielfeld:\n " + start.getTischStand().toString();
            outCon.loggerMessage(info);
            runde++;
            start = start.getDahinter();
        }
    }

    /**
     * @return das Ende der Liste
     */
    public Runde getTail() {
        return tail;
    }


    /**
     * wandelt den bisherigen Verlauf aus einer doppeltverketteten Liste in eine ArrayListe um
     *
     * @return
     */
    public ArrayList<Runde> zumSpeichern() {
        ArrayList<Runde> zugVerlauf = new ArrayList<>();
        Runde start = head;
        start = start.getDahinter();
        while (!start.equals(tail)) {
            zugVerlauf.add(start);
            start = start.getDahinter();
        }
        return zugVerlauf;
    }
}
