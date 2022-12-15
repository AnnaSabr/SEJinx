package actions.ReUnDo;
import adapter.primary.InputConsole;
import adapter.secondary.OutputConsole;
import entities.Player;
import java.util.ArrayList;


/**
 * Eine doppeltverkette Liste zum chronologischen Darstellen der einzelnen Runden im Spiel
 */
public class Course {

    private Round head;
    private Round tail;
    private Round position;
    private OutputConsole outCon;
    private InputConsole inCon;


    public Course() {
        this.head = new Round(null, null);
        this.tail = new Round(null, null);

        this.head.setBehind(tail);
        this.tail.setBefore(head);
        this.position = tail;
        this.outCon=new OutputConsole();
        this.inCon= new InputConsole();
    }


    /**
     * setzt eine neue Runde ans ende des Verlaufs
     *
     * @param newRound Runde die eingefuegt werden soll
     */
    public void addRound(Round newRound) {
        Round placeholder = tail.getBefore();
        placeholder.setBehind(newRound);
        tail.setBefore(newRound);
        newRound.setBefore(placeholder);
        newRound.setBehind(tail);
    }

    /**
     * @param round die geprueft werden soll, ob sie der Anfang oder das Ende ist
     * @return ob es Kopf oder Ende ist
     */
    public boolean headOrTail(Round round) {
        boolean empty = false;
        if (round.equals(head) || round.equals(tail) || round.equals(null)) {
            empty = true;
        }
        return empty;
    }

    /**
     * zum Ausgeben von Text Messages
     *
     * @param msg
     */
    private void log(String msg) {
        outCon.jinxMessage(msg);
    }


    /**
     * Menue zum Waehlen der geplanten Re und Un Do Schritte
     *
     * @return
     */
    public Round jump() {
        position = tail;


        while (true) {
            log("""
                    Choose your Manipulation!
                    S - Show regular status
                    J - choosen status
                    K - jump back
                    L - jump further
                    P - leave
                            \n""");

            String input =inCon.letterInput();
            if (input.equals("S")) {
                log("regular status:\n");
                showRound(tail.getBefore());
            } else if (input.equals("J")) {
                if (!headOrTail(position)) {
                    log("new choosen status:");
                    showRound(position);
                } else {
                    log("No new status choosen.");
                }

            } else if (input.equals("K")) {
                unDo();
            } else if (input.equals("L")) {
                reDo();
            } else if (input.equals("P")) {
                if (!headOrTail(position)) {
                    return position;
                }
                return tail;
            } else {
                log("incorrect input.");
            }
        }
    }

    /**
     * geht im Verlauf einen Schritt zurueck
     */
    public void unDo() {
        if (!headOrTail(position.getBefore())) {
            log("One step back");
            position = position.getBefore();
        } else {
            log("already at the beginning");
        }

    }

    /**
     * geht im Verlauf einen Schritt vor
     */
    public void reDo() {
        if (!position.equals(tail)) {
            log("One step further");
            position = position.getBehind();
        } else {
            log("already at the end");
        }
    }

    /**
     * Gibt im Terminal eine einzelne Uebergeben Runde aus
     *
     * @param choice einzelne Runde
     */
    public void showRound(Round choice) {
        for (Player player : choice.getAllPlayers()) {
            String info="Spieler: " + player.getName() + "\n" +
                    "Handkarten: " + player.getCards() + "\n" +
                    "LuckyKarten: " + player.getLuckCards() + "\n";
            outCon.loggerMessage(info);
        }
        String info= "Tisch Kartenstapel: " + choice.getTableStatus().getCardStack() + "\n" +
                "Tisch Luckykartenstapel: " + choice.getTableStatus().getLuckStack() + "\n" +
                "Spielfeld:\n " + choice.getTableStatus().toString();
        outCon.loggerMessage(info);
    }

    /**
     * gibt im Terminal den ganzen Verlauf aus
     */
    public void showHistory() {
        int round = 1;
        Round begin = head.getBehind();
        while (!begin.equals(tail)) {
            String info=round + ". Zug: \n" + round;
            outCon.loggerMessage(info);
            for (Player player : begin.getAllPlayers()) {
                info="Spieler: " + player.getName() + "\n" +
                        "Handkarten: " + player.getCards() + "\n" +
                        "LuckyKarten: " + player.getLuckCards() + "\n";
                outCon.loggerMessage(info);
            }
            info="Tisch Kartenstapel: " + begin.getTableStatus().getCardStack() + "\n" +
                    "Tisch Luckykartenstapel: " + begin.getTableStatus().getLuckStack() + "\n" +
                    "Spielfeld:\n " + begin.getTableStatus().toString();
            outCon.loggerMessage(info);
            round++;
            begin = begin.getBehind();
        }
    }

    /**
     * @return das Ende der Liste
     */
    public Round getTail() {
        return tail;
    }


    /**
     * wandelt den bisherigen Verlauf aus einer doppeltverketteten Liste in eine ArrayListe um
     *
     * @return
     */
    public ArrayList<Round> toSave() {
        ArrayList<Round> moveHistory = new ArrayList<>();
        Round begin = head;
        begin = begin.getBehind();
        while (!begin.equals(tail)) {
            moveHistory.add(begin);
            begin = begin.getBehind();
        }
        return moveHistory;
    }
}
