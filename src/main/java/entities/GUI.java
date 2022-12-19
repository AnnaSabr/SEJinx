package entities;

import actions.ReUnDo.Round;
import actions.ReUnDo.cards.Card;
import actions.ReUnDo.cards.CardColor;
import actions.ReUnDo.cards.CardType;
import actions.ReUnDo.cards.LuckCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GUI {

    JFrame gui;
    JLabel label=new JLabel();
    int returnIntValue=0;
    String returnValue;
    int chosenLuckcard;
    boolean nextMessage;
    PlayerHandGUI hand;

    public GUI(boolean nextMessage){
        gui=new JFrame();
        gui.setTitle("Jinx");
        gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.nextMessage=nextMessage;
    }



    /**
     * shows gui for yes or no choice
     *
     * @param question question the user has to answer
     */
    public void yesOrNo(String question){
        returnValue=null;
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
     * returns user's answer from yes/no question
     *
     * @param question question for user
     * @return true/false depending on user's choice
     */
    public boolean returningYesOrNO(String question){
        returnValue=null;
        gui.getContentPane().removeAll();
        gui.repaint();
        this.yesOrNo(question);
        System.out.println("out of method");
        boolean bool=true;
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(returnValue!=null){
                if(returnValue.equals("y")){
                    System.out.println(2);
                    bool = true;
                    break;
                }
                else{
                    System.out.println(1);
                    System.out.println(this.returnValue);
                    bool = false;
                    break;
                }
            }
        }
        return bool;
    }

    /**
     * displays player's luckcards
     *
     * @param currentPlayer Player who is currently playing
     * @return JPanel with a button for each luckcard
     */
    public JPanel luckcardGUI(Player currentPlayer){

        JPanel playerLuckcards=new JPanel();
        playerLuckcards.setBackground(new Color(160,82,45));
        int numberOfCard=0;
        for(LuckCard card:currentPlayer.getLuckCards()){
            ImageIcon cardImage;
            if(card.getCardType().equals(CardType.PLUSONE)){
                cardImage=new ImageIcon("src/main/java/cards/LuckCardImages/plus_one.png");
            }else if(card.getCardType().equals(CardType.MINUSONE)){
                cardImage=new ImageIcon("src/main/java/cards/LuckCardImages/minus_one.png");
            } else if (card.getCardType().equals(CardType.EXTRATHROW)) {
                cardImage=new ImageIcon("src/main/java/cards/LuckCardImages/throw_again.png");
            } else if (card.getCardType().equals(CardType.FOURTOSIX)) {
                cardImage=new ImageIcon("src/main/java/cards/LuckCardImages/four_to_six.png");
            }else if(card.getCardType().equals(CardType.CARDSUM)){
                cardImage=new ImageIcon("src/main/java/cards/LuckCardImages/draw_many.png");
            }else{
                cardImage=new ImageIcon("src/main/java/cards/LuckCardImages/one_to_three.png");
            }
            JButton cardButton=new JButton(cardImage);
            ActionListener actionListener=new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chosenLuckcard=currentPlayer.getLuckCards().indexOf(card) + 1;
                    chosenAction="L";
                }
            };
            cardButton.addActionListener(actionListener);
            playerLuckcards.add(cardButton);
            numberOfCard++;
        }
        return playerLuckcards;
    }

    /**
     * return a number greater than 0 from user
     *
     * @param text
     * @return entry from inputNumber(String text)
     */
    public int getInputNumber(String text){
        returnIntValue=0;
        gui.getContentPane().removeAll();
        gui.repaint();
        inputNumber(text);
        while(true){
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

    /**
     * returns input inputNumber(String text,int min,int max)
     *
     * @param text question or instruction for user
     * @param min smallest accepted value
     * @param max biggest accepted value
     * @return user input from inputNumber-method
     */
    public int getInputNumber(String text, int min, int max){
        returnIntValue=0;
        gui.getContentPane().removeAll();
        gui.repaint();
        inputNumber(text, min, max);
        while(true){
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

    /**
     * user enters a number greater than 0
     *
     * @param text instruction or question for user
     */
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
     * user chooses number between min and max via textfield
     *
     * @param text instruction or message for user
     * @param min smallest accepted value
     * @param max biggest accepted value
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

    /**
     * GUI to choose name and level of AI
     *
     */
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
                AIType="easy";
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
                AIType="medium";
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
                AIType="hard";
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

    /**
     * returns user input from textfield
     *
     * @param question will be displayed on top of screen
     * @return Profile's name
     */
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

    /**
     * GUI to enter input in textfield
     *
     * @param question displayed on top of screen
     */
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

    boolean orderByScore=true;

    /**
     * creates the GUI with table, histories, luckcards and player's hand
     *
     * @param displaying Round the GUI will display
     * @param text text that will be displayed in the right bottom corner, null to not use the textarea
     */
    public void updateGUI(Round displaying,String[] text){
        gui.getContentPane().removeAll();
        tableGui=new CardGUI(displaying.getTableStatus(),this);
        gui.add(tableGui,BorderLayout.CENTER);
        JPanel luckCards=this.luckcardGUI(displaying.getActive());
        //gui.add(luckCards,BorderLayout.EAST);

        JPanel compRight=new JPanel(new BorderLayout());
        JPanel rightCenter=new JPanel(new BorderLayout());
        rightCenter.add(luckCards,BorderLayout.NORTH);

        String showingText="";
        if(text!=null){
            for(String line:text){
                showingText=showingText+line+"\n";
            }
        }
        JTextArea messageToUser=new JTextArea(showingText);
        messageToUser.setEditable(false);

        rightCenter.add(messageToUser,BorderLayout.SOUTH);
        compRight.add(rightCenter,BorderLayout.CENTER);

        JLabel playerName=new JLabel(displaying.getActive().getName()+", it's your turn!");
        playerName.setFont(new Font("Serif", Font.PLAIN, 24));
        gui.add(playerName,BorderLayout.NORTH);

        JButton diceRoll=new JButton("Roll the dice");
        int dice=displaying.getActive().diceCount;
        String diceStringValue=String.valueOf(dice);
        if(dice==0){
            diceStringValue="No value yet";
        }
        JLabel diceCount=new JLabel("Rolled: " + diceStringValue);
        ActionListener diceListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenAction="R";
            }
        };
        diceRoll.addActionListener(diceListener);
        JPanel diceDisplay=new JPanel();
        diceDisplay.add(diceRoll);
        diceDisplay.add(diceCount);
        compRight.add(diceDisplay,BorderLayout.SOUTH);

        JButton advise=new JButton("get help");
        ActionListener adviseListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenAction="A";
            }
        };
        advise.addActionListener(adviseListener);
        compRight.add(advise,BorderLayout.NORTH);

        hand=new PlayerHandGUI(displaying.getActive().getCards());
        hand.setPreferredSize(new Dimension(100,130));
        gui.add(hand,BorderLayout.SOUTH);

        JTable table=this.historyGUI(displaying.getActive().history);
        JPanel tablePanel=new JPanel();
        table.setAutoCreateRowSorter(true);
        tablePanel.add(new JScrollPane(table));

        JPanel leftComp=new JPanel(new BorderLayout());
        leftComp.add(tablePanel,BorderLayout.NORTH);
        JPanel leftButtons=new JPanel();
        JButton reUnDOButton=new JButton("Re/Undo");
        ActionListener reUnDoListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenAction="M";
            }
        };
        reUnDOButton.addActionListener(reUnDoListener);
        leftButtons.add(reUnDOButton);

        if(this.nextMessage){
            JButton next=new JButton("next");
            leftButtons.add(next);
            ActionListener nextListener=new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showNextMessage=true;
                }
            };
            next.addActionListener(nextListener);
        }

        //TODO english name
        JButton verlauf=new JButton("Verlauf zeigen");
        ActionListener verlaufListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenAction="N";
            }
        };
        verlauf.addActionListener(verlaufListener);
        leftButtons.add(verlauf);
        leftComp.add(new JScrollPane(showHighscores(displaying.getHighscores())),BorderLayout.CENTER);


        JButton save=new JButton("save Game");
        ActionListener saveListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenAction="S";
            }
        };
        save.addActionListener(saveListener);
        leftButtons.add(save);

        JButton showMoves=new JButton("show moves");
        ActionListener movesListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenAction="Z";
            }
        };
        showMoves.addActionListener(movesListener);
        leftButtons.add(showMoves);

        JButton loadGame=new JButton("load a game");
        ActionListener loadListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenAction="X";
            }
        };
        loadGame.addActionListener(loadListener);
        leftButtons.add(loadGame);

        leftComp.add(leftButtons,BorderLayout.SOUTH);

        gui.add(leftComp,BorderLayout.WEST);
        gui.add(compRight,BorderLayout.EAST);
        gui.setVisible(true);
    }

    /**
     *
     * @return chosenAction from menu input
     */
    public String getChosenAction() {
        return chosenAction;
    }

    public CardGUI tableGui;
    boolean showNextMessage =false;

    /**
     * waits for player to request the next move
     *
     * @return true if player chose next move
     */
    public boolean nextMove(){
        showNextMessage =false;
        while(true){
            if(showNextMessage){
                return showNextMessage;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * creates a JTable with player's histories
     *
     * @param playerHistory history of currently playing player
     * @return table with player's history
     */
    public JTable historyGUI(ArrayList<String> playerHistory){
        //5 columns, as many rows as histories
        String[] columnNames={"name","score","used luckcards","date","opponents with score"};
        String[][] lines=new String[playerHistory.size()][5];
        for(int a=0; a<playerHistory.size();a++){
            String[] splittedHistory=playerHistory.get(a).split(",");
            int valScore=Integer.parseInt(splittedHistory[1]);
            if(valScore<100){
                splittedHistory[1]="0"+splittedHistory[1];
                if(valScore<10){
                    splittedHistory[1]="0"+splittedHistory[1];
                }
            }
            for (int b=0;b<splittedHistory.length;b++) {
                lines[a][b]=splittedHistory[b];
            }
        }
        JTable table=new JTable(lines,columnNames){

            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };


        return table;
    }

    String chosenAction;

    /**
     * returns chosen action from menu
     *
     * @param currentRound round that is currently shown
     * @return player's chosen action
     */
    public String actionChosen(Round currentRound, String[] text){
        chosenAction=null;
        this.updateGUI(currentRound,text);
        while(true){
            //chosen luckcard is saved in int chosenLuckcard
            //chosen card saved in tableGui.chosenCardCoord

            if(chosenAction!=null){
                return chosenAction;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * returns coordinates of card on table
     *
     * @return
     */
    public String getChosenCardCoord(){
        return tableGui.chosenCardCoord;
    }

    public int getChosenLuckcard(){
        return chosenLuckcard;
    }

    /**
     * shows advice for player in optionpane
     *
     * @param advice Advice for the player
     */
    public void showAdvice(String advice, String title){
        JOptionPane.showOptionDialog(null, advice,title,JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,null,null);
    }


    /**
     * creates table with values from highscore
     *
     * @param scores all highscores
     * @return table with scores
     */
    public JTable showHighscores(ArrayList<String> scores){
        String[] columnNames={"Name","Score"};
        String[][] lines=new String[scores.size()][2];
        for(int a=0; a<scores.size();a++){
            String[] splittedScore=scores.get(a).split(" ");
            for (int b=0;b<splittedScore.length;b++) {
                lines[a][b]=splittedScore[b];
            }
        }
        JTable table=new JTable(lines,columnNames){

            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        table.setAutoCreateRowSorter(true);

        return table;
    }

    /**
     * waits for player to pick a card from his hand
     *
     * @return index of card from player's hand
     */
    public int pickCardFromHand(){
        hand.chosen=-1;
        while(true){
            if(hand.chosen!=-1){
                return hand.chosen;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * Player can pick a card from his hand to drop
     *
     * @return card that will be dropped
     */
    public int pickCardFromHandToDrop(){
        hand.drop=-1;
        while(true){
            if(hand.drop!=-1){
                return hand.drop;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
