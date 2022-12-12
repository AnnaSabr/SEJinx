package ports.outbound;

import entities.Table;

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
   void tablePicture(Table table);
}
