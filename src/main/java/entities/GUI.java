package entities;

import actions.ReUnDo.Round;

import javax.swing.*;
import javax.swing.border.Border;
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

    /**
     * shows gui for yes or no choice
     *
     * @param question
     */
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

    /**
     * true or false, choice
     *
     * @param question
     * @return
     */
    public boolean returningYesOrNO(String question){
        gui.getContentPane().removeAll();
        gui.repaint();
        this.yesOrNo(question);
        System.out.println("out of method");
        boolean bool=true;
        while(true){
            //TODO only works with sleep or print

            /*try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            //System.out.println(this.returnValue);
            if(returnValue!=null){
                if(returnValue.equals("y")){
                    System.out.println(2);
                    break;
                }
                else{
                    System.out.println(1);
                    System.out.println(this.returnValue);
                    break;
                }
            }
        }
        return bool;
    }

    public static void main(String[] args){
        CardGUI cardGUI=new CardGUI(new Table(true));
        JFrame frame=new JFrame();
        frame.add(cardGUI,BorderLayout.CENTER);
        frame.setVisible(true);
    }

    /**
     * get a number from user
     *
     * @param text
     * @return
     */
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

    public int getInputNumber(String text, int min, int max){
        gui.getContentPane().removeAll();
        gui.repaint();
        inputNumber(text, min, max);
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
                        returnIntValue=0;
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

    /**
     * choose number between min and max
     *
     * @param text
     * @param min
     * @param max
     */
    public void inputNumber(String text, int min, int max){
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
                    if(returnIntValue>max||returnIntValue<min){
                        JOptionPane.showOptionDialog(null, "Enter a number greater than or equal to "+min+" and smaller than or equal to "+max+".","Wrong input",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                        returnIntValue=0;
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

    public void buildAI(){
        gui.getContentPane().removeAll();
        gui.repaint();
        JLabel jLabel=new JLabel("Enter a name for the AI");
        JTextField inputField=new JTextField("Enter here");
        inputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText("");
            }
        });
        JButton easy=new JButton("Easy");
        JButton medium=new JButton("Medium");
        JButton hard=new JButton("Hard");
        JPanel input=new JPanel();
        input.add(inputField);
        input.add(easy);
        input.add(medium);
        input.add(hard);
        gui.add(jLabel, BorderLayout.NORTH);
        gui.add(input, BorderLayout.CENTER);
        ActionListener easyListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AIType="Easy";
                if(inputField.getText().equals("")||inputField.getText().equals("Enter here")){
                    JOptionPane.showOptionDialog(null,"Enter a name for the AI","error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                    AIType=null;
                }else{
                    AIname=inputField.getText();
                }
            }
        };
        ActionListener mediumListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AIType="Medium";
                if(inputField.getText().equals("")||inputField.getText().equals("Enter a name for the AI")){
                    JOptionPane.showOptionDialog(null,"Enter a name for the AI","error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                    AIType=null;
                }else{
                    AIname=inputField.getText();
                }
            }
        };
        ActionListener hardListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AIType="Hard";
                if(inputField.getText().equals("")||inputField.getText().equals("Enter a name for the AI")){
                    JOptionPane.showOptionDialog(null,"Enter a name for the AI","error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                    AIType=null;
                }else{
                    AIname=inputField.getText();
                }
            }
        };
        easy.addActionListener(easyListener);
        medium.addActionListener(mediumListener);
        hard.addActionListener(hardListener);
        gui.setVisible(true);
    }

    /**
     * returns chosen values for one AI
     *
     * @return
     */
    public String[] AIsettings(){
        AIname=null;
        AIType=null;
        this.buildAI();
        String[] aiInfo=new String[2];
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("");
            }
            if(AIname!=null){
                aiInfo[0]=AIType;
                aiInfo[1]=AIname.replaceAll(" ","");
                return aiInfo;
            }
        }
    }

    String AIType;
    String AIname;
    String profileName;

    public String returnProfile(String question){
        profileName=null;
        getProfileName(question);
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("");
            }
            if(profileName!=null){
                profileName=profileName.replaceAll(" ","");
                return profileName;
            }
        }
    }

    public void getProfileName(String question){
        gui.getContentPane().removeAll();
        gui.repaint();
        JLabel jLabel=new JLabel(question);
        JTextField inputField=new JTextField("Enter here");
        inputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText("");
            }
        });
        JButton enter=new JButton("enter");
        ActionListener listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                profileName=inputField.getText();
            }
        };
        enter.addActionListener(listener);
        JPanel field=new JPanel();
        field.add(inputField);
        field.add(enter);
        gui.add(jLabel, BorderLayout.NORTH);
        gui.add(field,BorderLayout.CENTER);
        gui.setVisible(true);
    }

    public void updateGUI(Round displaying){


        //TODO
    }
}
