import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import entities.EasyKI;
import entities.MediumAI;
import entities.Player;
import entities.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestEasyKI {
    Table table;
    Player ki;

    @BeforeEach
    void setUp() {
        this.table = new Table(true);
        this.ki = new EasyKI("Hans", 0, false,false);
    }


    @Test
    public void chooseActionNoRoll(){
        assertEquals("R",ki.chooseAction(table));
    }

    @Test
    public void chooseActionWithRoll(){
        ki.setRolls(1);
        ki.setDiceCount(5);
        assertEquals("C",ki.chooseAction(table));
    }

    @Test
    public void chooseCard(){
        ki.setDiceCount(5);
        assertTrue(ki.chooseCard(table));
        ArrayList<Card> hand = ki.getCards();
        assertEquals(CardColor.GREEN,hand.get(0).getColor());
        assertEquals(5,hand.get(0).getValue());
    }

    @Test
    public void selectHighCardFalse(){
        assertFalse(ki.selectHighCard());
    }


    @Test
    public void drawLuckCard(){
        assertFalse(ki.drawLuckCard(table,null));
    }

}
