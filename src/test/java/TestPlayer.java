import entities.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer {

    Player player;

    @BeforeEach
    public void create(){
        player= new Player("bob",1,true);
    }

    @Test
    public void getScoreTest(){
        assertEquals(0,player.getScore());
    }
}
