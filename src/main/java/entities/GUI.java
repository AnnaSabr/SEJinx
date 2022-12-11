package entities;

import actions.ReUnDo.Runde;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI {

    JFrame gui;
    JLabel label=new JLabel();
    int returnIntValue=0;
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
        //label.setText(question);
        JLabel jLabel=new JLabel(question);
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
        JPanel buttons=new JPanel();
        buttons.add(noButton);
        buttons.add(yesButton);
        gui.add(jLabel, BorderLayout.NORTH);
        gui.add(buttons,BorderLayout.CENTER);
        gui.setVisible(true);
    }

    public boolean returningYesOrNO(String question){
        gui.getContentPane().removeAll();
        gui.repaint();
        this.yesOrNo(question);
        System.out.println("out of method");
        while(true){
            //TODO only works with sleep or print

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

    public int getInputNumber(String text){
        gui.getContentPane().removeAll();
        gui.repaint();
        inputNumber(text);
        while(true){
            //TODO only works with sleep or print

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println(this.returnValue);
            if(returnIntValue!=0){
                return returnIntValue;
            }
        }
    }

    public void inputNumber(String text){
        returnIntValue=0;
        //label.setText(text);
        JLabel jLabel=new JLabel(text);
        JTextField number=new JTextField("Enter the number here");
        number.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                number.setText("");
            }
        });
        JButton send=new JButton("enter");
        JPanel input=new JPanel();
        input.add(number);
        input.add(send);
        gui.add(jLabel, BorderLayout.NORTH);
        gui.add(input, BorderLayout.CENTER);

        ActionListener sendListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String info = number.getText();
                    returnIntValue=Integer.parseInt(info);
                    if(returnIntValue<=0){
                        JOptionPane.showOptionDialog(null, "Enter a number greater than 0.","Wrong input",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                    }
                }catch (NullPointerException exception){
                    JOptionPane.showOptionDialog(null, "Enter a number.","Wrong input",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                }catch (NumberFormatException exception){
                    JOptionPane.showOptionDialog(null, "Enter a number.","Wrong input",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                }
            }
        };
        send.addActionListener(sendListener);
        gui.setVisible(true);

    }

    public void updateGUI(Runde displaying){


        //TODO
    }
}
