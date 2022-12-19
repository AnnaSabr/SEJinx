import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import actions.ReUnDo.cards.CardType;
import actions.ReUnDo.cards.LuckCard;
import entities.Player;
import entities.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer {

    private Player player;
    private Player[] players;
    private Card card;
    private LuckCard luckCard;
    private ArrayList<Card> cardsHand;
    private ArrayList<LuckCard> luckysHand;
    private Table table;
    private Card[][] field;
    private Card[][] fieldEmpty;
    private Scanner scan;
    private String valueInput;

    private Card[][] createField() {
        field = new Card[4][4];
        int count = 1;
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                field[a][b] = new Card(CardColor.RED, count);
                count++;
            }
        }
        return field;
    }

    @BeforeEach
    public void create() {
        player = new Player("bob", 1, false, false);
        players = new Player[1];
        card = new Card(CardColor.RED, 1);
        luckCard = new LuckCard(CardType.PLUSONE);
        cardsHand = new ArrayList<>();
        luckysHand = new ArrayList<>();
        table = new Table(false);
        table.setField(createField());
        fieldEmpty = null;

        valueInput = "1";
        scan = new Scanner(System.in);

    }


    //removeCard(Card)
    @Test
    public void removeCardExists() {
        player.addCard(card);
        assertTrue(player.removeCard(card));
    }

    /**
     * @Test public void removeCardNonExisting() {
     * player.setCards(cardStack);
     * assertFalse(player.removeCard(card));
     * }
     **/


    //getScore()
    @Test
    public void getScoreEmpty() {
        assertEquals(0, player.getScore());
    }

    @Test
    public void getScoreOne() {
        player.addCard(card);
        assertEquals(1, player.getScore());
    }

    /**
     * //chooseCard(Table)
     *
     * @Test public void chooseCardExists() {
     * <p>
     * }
     * @Test public void chooseCardNonExisting() {
     * <p>
     * }
     **/


    //drawLuckyCard
    @Test
    public void drawLuckCardAvailable() {
        players[0] = player;
        cardsHand.add(card);
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan = new Scanner(System.in);
        player.setCards(cardsHand);
        assertTrue(player.drawLuckCard(table, players));
    }

    @Test
    public void drawLuckCardNotAvailable() {
        players[0] = player;
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan = new Scanner(System.in);
        player.setCards(cardsHand);
        assertFalse(player.drawLuckCard(table, players));
    }


    //selectCard(Player[])
    @Test
    public void selectCardAvailable() {
        players[0] = player;
        cardsHand.add(card);
        player.setCards(cardsHand);
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan = new Scanner(System.in);
        assertNotNull(player.selectCard(players));
    }

    @Test
    public void selectCardNonAvailable() {
        players[0] = player;
        player.setCards(cardsHand);
        valueInput = "0";
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan = new Scanner(System.in);
        assertNull(player.selectCard(players));
    }


    //selectHighCard()
    @Test
    public void selectHighCardAvailable() {
        cardsHand.add(card);
        player.setCards(cardsHand);
        valueInput = "0";
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan = new Scanner(System.in);
        assertTrue(player.selectHighCard());
    }

    @Test
    public void selectHighCardNonAvailable() {
        player.setCards(cardsHand);
        assertFalse(player.selectHighCard());
    }


    //selectLuckCard(Table)
    @Test
    public void selectLuckCardAvailable() {
        table.setField(field);
        luckysHand.add(luckCard);
        player.setLuckCards(luckysHand);
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan = new Scanner(System.in);
        assertNotNull(player.selectLuckCard(table));
    }

    @Test
    public void selectLuckCardNotAvailable() {
        table.setField(field);
        player.setLuckCards(luckysHand);
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan = new Scanner(System.in);
        assertNull(player.selectLuckCard(table));
    }


    //chooseAction()
    @Test
    public void chooseActionValid() {
        String input = "N";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scan = new Scanner(System.in);
        assertEquals(input, player.chooseAction(table));
    }
    /**
     @Test public void chooseActionUnvalid(){

     }
     **/


    //roll()

    /**
     * @Test public void rollPossible(){
     * <p>
     * }
     **/
    @Test
    public void rollImpossible() {
        player.setRolls(2);
        player.setDiceCount(5);
        assertEquals(player.getDiceCount(), player.roll());
    }
}
