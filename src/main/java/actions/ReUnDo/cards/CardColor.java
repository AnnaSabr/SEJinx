package actions.ReUnDo.cards;

/**
 * Each card can be one of 8 Colors
 */
public enum CardColor implements Cloneable {
    RED(0),
    GREEN(1),
    BLUE(2),
    YELLOW(3),
    PURPLE(4),
    ORANGE(5),
    GREY(6),
    WHITE(7);

    /**
     * value for the specific Color
     */
    private final int value;

    CardColor(final int newValue) {
        value = newValue;
    }

    /**
     * @return value of the CardColor
     */
    public int getValue() {
        return value;
    }

}
