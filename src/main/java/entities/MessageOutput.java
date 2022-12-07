package entities;

public interface MessageOutput {

   void simpleMessage(String text);

   void jinxMessage(String text);

   void loggerMessage(String text);

   void playerLog(String name,String text);
}
