package battleship.objects;

public enum Ship {

    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);


    private final String displayName;
    private final int length;

    Ship (String displayName, int length) {
        this.displayName = displayName;
        this.length = length;
    }

    public String getName() {
        return displayName;
    }

    public int getLength() {
        return length;
    }
}
