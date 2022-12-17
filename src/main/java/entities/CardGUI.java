package entities;

import actions.ReUnDo.cards.Card;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class CardGUI extends JPanel {

    Card[][] cardTableCards;
    GUI gui;
    String chosenCardCoord;

    public CardGUI(Table table, GUI gui){
        cardTableCards=table.getField();
        this.gui=gui;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        this.setBackground(Color.CYAN);
        Graphics2D g2d = (Graphics2D) graphics;
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
                    g2d.drawString(Integer.toString(cardTableCards[a][b].getValue()),40+a*80,60+b*120);
                }
            }

        }

    }

    public Rectangle drawCardToTable(Card card,int yPos,int xPos){
        //calculate position of new card
        int x=10+xPos*80;
        int y=10+yPos*120;
        Rectangle rectangle=new Rectangle(x,y,60,100);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                    if (rectangle.contains(me.getPoint())) {//check if mouse is clicked within shape
                        gui.chosenAction="C";
                        chosenCardCoord=yPos+1+","+xPos+1;
                    }
                }
        });
        return rectangle;
    }

}
