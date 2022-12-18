package entities;

import actions.ReUnDo.cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PlayerHandGUI extends JPanel {

    ArrayList<Card> cardOnHand;

    public PlayerHandGUI(ArrayList<Card> hand){
        cardOnHand=hand;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        this.setBackground(new Color(182, 160, 106));
        Graphics2D g2d = (Graphics2D) graphics;
        for(int a=0; a<this.cardOnHand.size();a++){
            Card card=cardOnHand.get(a);
                if(card.getColor().getValue()==0){
                    g2d.setColor(Color.RED);
                } else if (card.getColor().getValue()==1) {
                    g2d.setColor(Color.GREEN);
                }else if (card.getColor().getValue()==2) {
                    g2d.setColor(Color.BLUE);
                }else if (card.getColor().getValue()==3) {
                    g2d.setColor(Color.YELLOW);
                }else if (card.getColor().getValue()==4) {
                    g2d.setColor(Color.PINK);
                }else if (card.getColor().getValue()==5) {
                    g2d.setColor(Color.ORANGE);
                }else if (card.getColor().getValue()==6) {
                    g2d.setColor(Color.GRAY);
                }else{
                    g2d.setColor(Color.WHITE);
                }
                g2d.fill(drawHand(a));
                g2d.setColor(Color.BLACK);
                g2d.drawString(Integer.toString(card.getValue()),40+a*80,30);
        }
        g2d.setColor(Color.BLACK);
        Rectangle r=new Rectangle(10+(cardOnHand.size()+1)*80,10,60,100);
        g2d.fill(r);
        g2d.setColor(Color.WHITE);
        g2d.drawString("None",40+(cardOnHand.size()+1)*80,30);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                if (r.contains(me.getPoint())) {//check if mouse is clicked within shape
                    //exchange for luckcard
                    chosen=0;
                    //forced to drop
                    drop=0;
                }
            }
        });

    }

    /**
     * returns Rectangle that will represent the card
     *
     * @param indexInArray position of card in JLabel
     * @return
     */
    public Rectangle drawHand(int indexInArray){
        int x=10+ indexInArray*80;
        int y=10;
        Rectangle rectangle=new Rectangle(x,y,60,100);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                if (rectangle.contains(me.getPoint())) {//check if mouse is clicked within shape
                    //exchange for luckcard
                    chosen=indexInArray+1;
                    //forced to drop
                    drop=indexInArray;
                }
            }
        });
        return rectangle;
    }

    int drop=0;
    int chosen=-1;
}
