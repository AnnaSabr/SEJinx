package entities;

import actions.ReUnDo.Runde;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {

    JFrame gui;

    String returnValue;

    public GUI(){
        gui=new JFrame();
        gui.setTitle("Jinx");
        gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * displays GUI on screen
     */
    public void runGUI(){
        gui.setVisible(true);

    }

    public void yesOrNo(String question){
        //TODO
        returnValue=null;
        JLabel label=new JLabel(question);
        JButton yesButton=new JButton("yes");
        JButton noButton=new JButton("no");
        ActionListener yesListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnValue="y";
                System.out.println("Button works");
            }
        };
        ActionListener noListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnValue="n";
                System.out.println("Button works");
            }
        };
        yesButton.addActionListener(yesListener);
        noButton.addActionListener(noListener);
        gui.add(label, BorderLayout.NORTH);
        gui.add(noButton,BorderLayout.CENTER);
        gui.add(yesButton,BorderLayout.SOUTH);
        gui.setVisible(true);
    }

    public boolean returningYesOrNO(String question){
        this.yesOrNo(question);
        System.out.println("out of method");
        while(true){
            //TODO buttons do nothing yet ......

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println(this.returnValue);
            if(returnValue!=null){
                if(returnValue.equals("y")){
                    System.out.println(2);
                    return true;
                }
                else{
                    System.out.println(1);
                    System.out.println(this.returnValue);
                    return false;
                }
            }
        }
    }

    public void updateGUI(Runde displaying){


        //TODO
    }
}
