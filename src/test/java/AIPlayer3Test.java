import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import actions.ReUnDo.cards.CardType;
import actions.ReUnDo.cards.LuckCard;
import entities.AIPLayer3;
import entities.Player;
import entities.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AIPlayer3Test {

    Table table;
    AIPLayer3 ai;

    @BeforeEach
    void setup(){
        //cards from config-file
        table=new Table(true);
        ai=new AIPLayer3("bot",20,false,false);
    }

    @Test
    void firstChooseAction(){
        //first time AI rolls the dice
        assertEquals("R",ai.chooseAction(table));
    }

    @Test
    void chooseActionNoCardAvailable(){
        ai.setRolls(1);
        ai.setDiceCount(1);
        //no available card, AI rolls again
        assertEquals(ai.chooseAction(table),"R");
    }

    @Test
    void chooseActionAfterRolling(){
        // can pick white or yellow, should pick white (coordinates at 4,1)
        ai.setRolls(1);
        ai.setDiceCount(4);
        ai.chooseAction(table);
        assertEquals(ai.cardOnTable,"4,1");
        //takes the chosen card
        assertEquals(ai.chooseCard(table),true);
    }

    @Test
    void drawLuckCard(){
        Player opponent=new AIPLayer3("player",10,false,false);
        opponent.addCard(new Card(CardColor.BLUE,2));
        Player[] players=new Player[2];
        players[0]=ai;
        players[1]=opponent;
        ai.addCard(new Card(CardColor.BLUE, 5));
        //draws a card because it has a higher score
        assertNotEquals(ai.selectCard(players),null);
        //does not draw a card, it has a lower score
        assertEquals(opponent.selectCard(players),null);
    }

    @Test
    void useLuckcard(){
        ArrayList<LuckCard> luckCards=new ArrayList<>();
        luckCards.add(new LuckCard(CardType.EXTRATHROW));
        ai.setLuckCards(luckCards);
        ai.setDiceCount(1);
        ai.setRolls(2);
        assertEquals(ai.chooseAction(table),"L");
    }

    @Test
    void findValidCards(){
        ai.setDiceCount(2);
        String[] coord=ai.findValidCards(table);
        String[] expected=new String[16];
        expected[0]="1,3";
        expected[1]="1,4";
        expected[2]="2,1";
        expected[3]="3,1";
        expected[4]="4,4";
        assertEquals(coord[0],expected[0]);
        assertEquals(coord[1],expected[1]);
        assertEquals(coord[2],expected[2]);
        assertEquals(coord[3],expected[3]);
        assertEquals(coord[4],expected[4]);
    }

    @Test
    void cardsWithPlusOne(){
        //player has a plus one card
        ArrayList<LuckCard> luckCards=new ArrayList<>();
        luckCards.add(new LuckCard(CardType.PLUSONE));
        ai.setLuckCards(luckCards);

        //player can take all cards with value 2 or 3
        ai.setDiceCount(2);
        String[] coord=ai.findValidCards(table);
        String[] expected=new String[16];
        expected[0]="1,3";
        expected[1]="1,4";
        expected[2]="2,1";
        expected[3]="3,1";
        expected[4]="3,2";
        expected[5]="3,3";
        expected[6]="4,2";
        expected[7]="4,3";
        expected[8]="4,4";
        assertEquals(coord[0],expected[0]);
        assertEquals(coord[1],expected[1]);
        assertEquals(coord[2],expected[2]);
        assertEquals(coord[3],expected[3]);
        assertEquals(coord[4],expected[4]);
        assertEquals(coord[5],expected[5]);
        assertEquals(coord[6],expected[6]);
        assertEquals(coord[7],expected[7]);
        assertEquals(coord[8],expected[8]);

    }

    @Test
    void cardsWithMinusOne(){
        //player has a minus one card
        ArrayList<LuckCard> luckCards=new ArrayList<>();
        luckCards.add(new LuckCard(CardType.MINUSONE));
        ai.setLuckCards(luckCards);
        ai.setDiceCount(3);

        //can take all cards with value 3 or 2
        String[] coord=ai.findValidCards(table);
        String[] expected=new String[16];
        expected[0]="1,3";
        expected[1]="1,4";
        expected[2]="2,1";
        expected[3]="3,1";
        expected[4]="3,2";
        expected[5]="3,3";
        expected[6]="4,2";
        expected[7]="4,3";
        expected[8]="4,4";
        assertEquals(coord[0],expected[0]);
        assertEquals(coord[1],expected[1]);
        assertEquals(coord[2],expected[2]);
        assertEquals(coord[3],expected[3]);
        assertEquals(coord[4],expected[4]);
        assertEquals(coord[5],expected[5]);
        assertEquals(coord[6],expected[6]);
        assertEquals(coord[7],expected[7]);
        assertEquals(coord[8],expected[8]);
    }

    @Test
    void oneToThree(){
        ai.setRolls(1);
        ai.setDiceCount(5);
        ArrayList<LuckCard> luckCards = new ArrayList<>();
        luckCards.add(new LuckCard(CardType.ONETOTHREE));
        luckCards.add(new LuckCard(CardType.FOURTOSIX));
        //get a card with value 2 by using 'onetothree' luckcard
        ai.cardOnTable="2,1";
        ai.setLuckCards(luckCards);
        assertEquals(ai.selectLuckCard(table).getCardType(),CardType.ONETOTHREE);
    }

    @Test
    void fourToSix(){
        ai.setRolls(1);
        ai.setDiceCount(1);
        ArrayList<LuckCard> luckCards = new ArrayList<>();
        luckCards.add(new LuckCard(CardType.ONETOTHREE));
        luckCards.add(new LuckCard(CardType.FOURTOSIX));
        //get a card with value 6 by using 'fourtosix' luckcard
        ai.cardOnTable="1,1";
        ai.setLuckCards(luckCards);
        assertEquals(ai.selectLuckCard(table).getCardType(),CardType.FOURTOSIX);
    }
}
