package ports.outbound;

import actions.ReUnDo.Round;
import actions.ReUnDo.cards.Card;
import entities.Table;

import java.util.ArrayList;

/**
 * Port for message output
 * */
public interface MessageOutput {

   void simpleMessage(String text);


   /**
    * everything a Ki says
    * @param name activ KI
    * @param text
    */
   void logKiPlayer(String name,String text);

   /**
    * Output function for exceptions
    * @param text String with getMessage from the exception
    */
   void exceptionMessage(String text);

   /**
    * Output function for self created error messages
    * @param text
    */
   void errorSelfMessage(String text);

   /**
    * Messages with infos not said by Jinx
    * @param text
    */
   void loggerMessage(String text);

   /**
    * Everything Jinx says without match field
    * @param text
    */
   void jinxMessage(String text);

   /**
    * shows gamefield
    * @param table staus of the table
    */
   void tablePicture(Table table, Round current);

   /**
    * Function to display config feedback
    * @param text feedback to be displayed
    * */
   void configJinxMessage(String text);

   /**
    * Function to display simple text
    * Should be used by text based outputs
    * @param text text to be displayed
    * */
   void simpleText(String text);

   /**
    * Function to display a help message
    * @param text help message to be displayed
    * */
   void helpMessage(String text);

   /**
    * Function to display a message with a manual delay
    * @param text should be like [ENTER] - Next move
    * */
   void manualMessage(String text);
}
