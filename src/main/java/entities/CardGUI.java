package entities;

import actions.ReUnDo.cards.Card;
import cards.CardColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CardGUI extends JPanel {

    ArrayList<Rectangle> tableCards=new ArrayList<>();
    Card[][] cardTableCards;

    public CardGUI(Table table){
        cardTableCards=table.getField();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        this.setBackground(Color.CYAN);
        Graphics2D g2d = (Graphics2D) graphics;
        /*g2d.setColor(Color.WHITE);
        for (Rectangle rectangle : tableCards) {
            g2d.fill(rectangle);
            //TODO use drawString method from g2d to draw number, first set color of g2d
        }*/
        for(int a=0;a<this.cardTableCards.length;a++){
            for(int b=0;b<this.cardTableCards.length;b++){
                if(cardTableCards[a][b]!=null){
                    if(cardTableCards[a][b].getColor().getValue()==0){
                        g2d.setColor(Color.RED);
                    } else if (cardTableCards[a][b].getColor().getValue()==1) {
                        g2d.setColor(Color.GREEN);
                    }else if (cardTableCards[a][b].getColor().getValue()==2) {
                        g2d.setColor(Color.BLUE);
                    }else if (cardTableCards[a][b].getColor().getValue()==3) {
                        g2d.setColor(Color.YELLOW);
                    }else if (cardTableCards[a][b].getColor().getValue()==4) {
                        g2d.setColor(Color.PINK);
                    }else if (cardTableCards[a][b].getColor().getValue()==5) {
                        g2d.setColor(Color.ORANGE);
                    }else if (cardTableCards[a][b].getColor().getValue()==6) {
                        g2d.setColor(Color.GRAY);
                    }else{
                        g2d.setColor(Color.WHITE);
                    }
                    g2d.fill(drawCardToTable(cardTableCards[a][b],b,a));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(Integer.toString(cardTableCards[a][b].getValue()),40+a*80,60+b*140);
                }else{

                }
            }

        }

    }

    public Rectangle drawCardToTable(Card card,int yPos,int xPos){
        //calculate position of new card
        int x=10+xPos*80;
        int y=10+yPos*140;
        Rectangle rectangle=new Rectangle(x,y,60,120);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                    if (rectangle.contains(me.getPoint())) {//check if mouse is clicked within shape
                            System.out.println("Clicked a rectangle");
                    }
                }
        });
        return rectangle;

    }

}
