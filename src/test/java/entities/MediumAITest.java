package entities;

import cards.Card;
import cards.CardColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MediumAITest {

    // create a table and read a set configuration to predict the contents
    Table table;
    Player ai;

    @BeforeEach
    void setUp() {
        this.table = new Table(true);
        this.ai = new MediumAI("Hans", 200, false);
    }

    @Test
    void chooseAction() {
        //AI didn't roll yet, so it should roll
        assertEquals("R",ai.chooseAction(table));
    }

    @Test
    void chooseAction_afterRoll() {
        //Set ai rolls
        ai.setRolls(1);
        ai.setDiceCount(2);
        //Ai rolled, but didn't reach the threshold of 3 --> roll again
        assertEquals("R", ai.chooseAction(table));
    }

    @Test
    void chooseAction_noOptions() {
        //Set ai rolls
        ai.setRolls(2);
        ai.setDiceCount(0);
        //Ai rolled 2 times, but couldn't find a fitting card --> end round by choosing C
        assertEquals("C", ai.chooseAction(table));
    }

    @Test
    void chooseCard_successful() {
        /* Cards on the field, since loaded from config
        6,WHITE 4,YELLOW 2,WHITE 2,ORANGE
        2,GREY 6,GREY 5,GREEN 5,BLUE
        2,YELLOW 3,PURPLE 3,RED 5,YELLOW
        4,WHITE 3,YELLOW 3,ORANGE 2,GREEN
        * */

        ai.setDiceCount(5);

        //Ai should be able to find the best card and choose it
        assertTrue(ai.chooseCard(table));

        ArrayList<Card> hand = ai.getCards();
        //best card the Ai chose should be the 5/BLUE
        assertEquals(CardColor.BLUE, hand.get(0).getColor());
        assertEquals(5,hand.get(0).getValue());
    }

    @Test
    void selectHighCard_noCardsLeft() {
        //Ai has no cards left to drop
        assertFalse(ai.selectHighCard());
    }

    @Test
    void selectHighCard_specificCard() {

        ArrayList<Card> dummy = new ArrayList<>();
        dummy.add(new Card(CardColor.GREEN,3));
        dummy.add(new Card(CardColor.GREEN, 3));
        dummy.add(new Card(CardColor.RED, 2));
        dummy.add(new Card(CardColor.RED, 3));
        dummy.add(new Card(CardColor.GREEN, 6));
        dummy.add(new Card(CardColor.YELLOW, 6));


        ai.setCards(dummy);

        assertTrue(ai.selectHighCard());

        ArrayList<Card> hand = ai.getCards();

        System.out.println(ai);
        //the yellow card should have been removed
        for(Card c : hand){
            if(c.getColor() == CardColor.YELLOW && c.getValue() == 6){
                System.out.println("FAIL");
                fail();
            }else{
                assert true;
            }
        }
    }

    @Test
    void selectHighCard_onlyOneCard() {
        ArrayList<Card> dummy = new ArrayList<>();
        dummy.add(new Card(CardColor.GREEN,3));

        ai.setCards(dummy);
        assertTrue(ai.selectHighCard());
    }

    @Test
    void drawLuckCard() {
        //MediumAi should never draw a luckCard
        assertFalse(ai.drawLuckCard(table, null));
    }
}