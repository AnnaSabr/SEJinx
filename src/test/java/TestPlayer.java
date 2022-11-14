import cards.Card;
import cards.CardColor;
import cards.LuckCard;
import entities.Player;
import entities.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer {

    private Player player;
    private Card card;
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
        player = new Player("bob", 1, false);
        card = new Card(CardColor.RED, 1);
        cardsHand = new ArrayList<>();
        luckysHand = new ArrayList<>();
        table = new Table(false);
        table.setField(createField());
        fieldEmpty = null;

        valueInput="1";
        scan= new Scanner(System.in);

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

    //chooseCard(Table)
    @Test
    public void chooseCardExists() {
        System.setIn(new ByteArrayInputStream(valueInput.getBytes()));
        scan= new Scanner(System.in);
        player.setDiceCount(1);
        //table.setField(field);
        assertTrue(player.chooseCard(table));

    }
}
