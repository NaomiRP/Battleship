package battleship.objects;

import java.util.List;

public enum Indicator {

    UNKNOWN('~'),
    SHIP('O', '~'),
    HIT('X'),
    MISS('M');

    private final char symbol;
    private final char maskedSymbol;

    Indicator(char symbol) {
        this(symbol, symbol);
    }

    Indicator(char symbol, char maskedSymbol) {
        this.symbol = symbol;
        this.maskedSymbol = maskedSymbol;
    }

    public char getSymbol(boolean fogOfWar) {
        if (fogOfWar)
            return maskedSymbol;
        return symbol;
    }

    static List<Indicator> hit() {
        return List.of(SHIP, HIT);
    }

    static List<Indicator> miss() {
        return List.of(UNKNOWN, MISS);
    }
}
