package battleship.objects;

import java.io.PrintStream;
import java.util.*;

import static battleship.objects.Indicator.*;
import static java.util.Map.Entry;

public class GameBoard {

    private static final String NUM_LABELS = "  1 2 3 4 5 6 7 8 9 10";
    private static final String ALPHA_LABELS = "ABCDEFGHIJ";

    private Indicator[][] board = new Indicator[10][10];

    // track the coordinates of each ship, removing each one as it is hit
    private Map<Ship, List<String>> ships = new HashMap<>();


    public GameBoard() {
        for (int i = 0; i < 10; i++) {
            board[i] = new Indicator[]{UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN};
        }
    }

    public boolean allShipsSunk() {
        return ships.size() == 0;
    }

    /** Invite player to take a shot, return true if a ship is sunk */
    public boolean takeShot(PrintStream out, Scanner in) {
        out.println("Take a shot!");
        String input = null;
        int[] coordinate = new int[]{-1, -1};
        while (coordinate[0] == -1 || coordinate[1] == -1) {
            input = in.next();
            coordinate = inputToCoordinate(input, out);
        }
        Indicator cur = board[coordinate[0]][coordinate[1]];
        if (hit().contains(cur)) {
            boolean shipSunk = false;
            if (SHIP.equals(cur)) {
                board[coordinate[0]][coordinate[1]] = HIT;
                shipSunk = recordHit(input);
            }
            print(out, true);
            out.println(shipSunk ? "You sank a ship!" : "You hit a ship!");
            return shipSunk;
        } else {
            if (UNKNOWN.equals(cur))
                board[coordinate[0]][coordinate[1]] = MISS;
            print(out, true);
            out.println("You missed!");
            return false;
        }
    }

    private boolean recordHit(String shot) {
        Ship sunk = null;
        for (Entry<Ship, List<String>> ship: ships.entrySet()) {
            if (ship.getValue().remove(shot)) {
                if (ship.getValue().size() == 0)
                    sunk = ship.getKey();
            }
        }
        if (sunk != null)
            ships.remove(sunk);
        return sunk != null;
    }

    public void placeShips(PrintStream out, Scanner in) {
        for (Ship ship : Ship.values()) {
            print(out, false);
            out.println("Enter the coordinates of the " + ship.getName() + " (" + ship.getLength() + " cells):");
            String input1 = in.next();
            String input2 = in.next();
            // valid coordinates (on board, in line), correct length, not adjacent to placed ships, place ship
            List<String> coordinates;
            do {
                coordinates = validateCoordinates(input1, input2, ship.getLength(), out);
                if (coordinates == null) {
                    input1 = in.next();
                    input2 = in.next();
                }
            } while (coordinates == null);
            ships.put(ship, coordinates);
        }
        print(out, false);
    }

    /** Convert an input string to row/column indices (a coordinate) */
    private int[] inputToCoordinate(String input, PrintStream out) {
        int[] coordinate = new int[]{-1, -1};
        int len1 = input.length();
        if (len1 < 2 || len1 > 3) {
            out.println("Error! Invalid coordinates. Try again:");
            return coordinate;
        }
        String row1 = input.substring(0,1);
        String col1 = input.substring(1);
        if (!(ALPHA_LABELS.contains(row1) //invalid row
             && NUM_LABELS.contains(col1))) //invalid column
        {
            out.println("Error! Invalid coordinates. Try again:");
            return coordinate;
        }
        coordinate[0] = ALPHA_LABELS.indexOf(row1);
        coordinate[1] = Integer.parseInt(col1) - 1;
        return coordinate;
    }

    /** Validates provided coordinates & places the ship if the coordinates are valid. */
    private List<String> validateCoordinates(String input1, String input2, int length, PrintStream out) {
        int[] coord1 = inputToCoordinate(input1, out);
        int[] coord2 = inputToCoordinate(input2, out);
        if (coord1[0] == -1 || coord1[1] == -1 || coord2[0] == -1 || coord1[1] == -1 //coordinates are invalid
               || (coord1[0] != coord2[0] && coord1[1] != coord2[1])) //coordinates are not aligned
        {
            out.println("Error! Invalid coordinates. Try again:");
            return null;
        }
        // alignment check above ensures one of these is zero
        int shipLen = coord2[0] - coord1[0];
        int shipWid = coord2[1] - coord1[1];
        if (length != (Math.abs(shipLen + shipWid) + 1)) {
            out.println("Error! Wrong ship length. Try again:");
            return null;
        }
        // define adjacent area to check for placed ships
        int adjRowMin = Math.min(coord1[0], coord2[0]) - 1;
        int adjRowMax = Math.max(coord1[0], coord2[0]) + 1;
        int adjColMin = Math.min(coord1[1], coord2[1]) - 1;
        int adjColMax = Math.max(coord1[1], coord2[1]) + 1;
        for (int i = Math.max(adjRowMin, 0); i <= Math.min(adjRowMax, 9); i++) {
            for (int j = Math.max(adjColMin, 0); j <= Math.min(adjColMax, 9); j++) {
                if (!UNKNOWN.equals(board[i][j])) {
                    out.println("Error! Too close to another ship. Try again:");
                    return null;
                }
            }
        }
        // coordinates are valid, place the ships
        List<String> coordinates = new ArrayList<>(length);
        for (int i = adjRowMin + 1; i < adjRowMax; i++) {
            for (int j = adjColMin + 1; j < adjColMax; j++) {
                board[i][j] = SHIP;
                String col = String.valueOf(j + 1);
                coordinates.add(ALPHA_LABELS.charAt(i) + col);
            }
        }
        return coordinates;
    }

    /** Print the current state of this board, masked according to the {@param fogOfWar}. */
    public void print(PrintStream out, boolean fogOfWar) {
        out.println("\n" + NUM_LABELS);
        StringBuilder sb;
        for (int i = 0; i < 10; i++) {
            sb = new StringBuilder().append(ALPHA_LABELS.charAt(i)).append(' ');
            for (int j = 0; j < 10; j++) {
                sb.append(board[i][j].getSymbol(fogOfWar)).append(' ');
            }
            out.println(sb);
        }
        out.println();
    }
}
