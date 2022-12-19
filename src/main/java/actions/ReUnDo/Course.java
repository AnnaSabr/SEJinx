package actions.ReUnDo;
import adapter.primary.InOutGUI;
import adapter.primary.InputConsole;
import adapter.secondary.OutputConsole;
import entities.Player;
import ports.inbound.MessageInput;
import ports.outbound.MessageOutput;

import java.util.ArrayList;


/**
 * double chained List to realise History of played Rounds
 */
public class Course {

    private Round head;
    private Round tail;
    private Round position;
    private MessageOutput outCon;
    private MessageInput inCon;
    private InOutGUI inOutGUI;


    public Course(InOutGUI inOutGUI) {
        this.head = new Round(null, null);
        this.tail = new Round(null, null);
        this.head.setBehind(tail);
        this.tail.setBefore(head);
        this.position = tail;

        this.inOutGUI=inOutGUI;
        if (inOutGUI!=null){
            this.outCon=inOutGUI;
            this.inCon=inOutGUI;

        }else{
            this.outCon=new OutputConsole();
            this.inCon= new InputConsole();
        }

    }


    /**
     * put new Round at the end of the History
     *
     * @param newRound to put at the end
     */
    public void addRound(Round newRound) {
        Round placeholder = tail.getBefore();
        placeholder.setBehind(newRound);
        tail.setBefore(newRound);
        newRound.setBefore(placeholder);
        newRound.setBehind(tail);
    }

    /**
     * @param round to check if it is head or tail
     * @return true or false if head or tail
     */
    public boolean headOrTail(Round round) {
        boolean empty = false;
        if (round.equals(head) || round.equals(tail) || round.equals(null)) {
            empty = true;
        }
        return empty;
    }

    /**
     * to log a message to output
     *
     * @param msg for output
     */
    private void log(String msg) {
        outCon.jinxMessage(msg);
    }


    /**
     * menu to choose from re and un do function
     *
     * @return Round wich was selected to jump to
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

            String input =inCon.letterInput("Choose your manipulation");
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
     * position steps one back in RoundHistory until head or tail
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
     * position steps one step for in RoundHistory until head or tail
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
     *
     * @return head of double chained List
     */
    public Round getHead() {
        return head;
    }

    /**
     * Output for one specific Round
     *
     * @param choice Round wich should be put in Output
     */
    public void showRound(Round choice) {
        for (Player player : choice.getAllPlayers()) {
            String info="Player: " + player.getName() + "\n" +
                    "Handcards: " + player.getCards() + "\n" +
                    "Luckycards: " + player.getLuckCards() + "\n";
            outCon.loggerMessage(info);
        }
        String info= "Table CardStack: " + choice.getTableStatus().getCardStack() + "\n" +
                "Table LuckyStack: " + choice.getTableStatus().getLuckStack() + "\n" +
                "Gamefield:\n " + choice.getTableStatus().toString();
        outCon.loggerMessage(info);
    }

    /**
     * complete History to Output
     */
    public void showHistory() {
        int round = 1;
        ArrayList<String> lines= new ArrayList<>();
        String history="";
        Round begin = head.getBehind();
        while (!begin.equals(tail)) {
            history=round + ". Move: \n" + round;
            lines.add(history);
            for (Player player : begin.getAllPlayers()) {
                history="Player: " + player.getName();
                lines.add(history);
                history="Handcards: " + player.getCards();
                lines.add(history);
                history="Luckycards: " + player.getLuckCards() + "\n";
                lines.add(history);
            }
            history="Table CardStack: " + begin.getTableStatus().getCardStack() + "\n";
            lines.add(history);
            history="Table Luckstack: " + begin.getTableStatus().getLuckStack() + "\n";
            lines.add(history);
            history="Gamefield:\n " + begin.getTableStatus().toString();
            lines.add(history);
            round++;
            begin = begin.getBehind();
        }
        int line=lines.size();
        String[] h= new String[line];
        int count=0;
        for (String e: lines){
            h[count]=e;
            count++;
        }
        outCon.historyOutput(h);
    }

    /**
     * @return tail from History
     */
    public Round getTail() {
        return tail;
    }


    /**
     * put all Rounds from this History into an Arraylist
     *
     * @return Arraylist with all Rounds from this Game
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
