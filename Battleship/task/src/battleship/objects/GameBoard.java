package battleship.objects;

import java.io.PrintStream;
import java.util.*;

import static battleship.objects.Indicator.SHIP;
import static battleship.objects.Indicator.UNKNOWN;

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

    /** Validates provided coordinates & places the ship if the coordinates are valid. */
    private List<String> validateCoordinates(String input1, String input2, int length, PrintStream out) {
        int len1 = input1.length();
        int len2 = input2.length();
        if (len1 < 2 || len1 > 3 || len2 < 2 || len2 > 3) {
            out.println("Error! Invalid coordinates. Try again:");
            return null;
        }
        String row1 = input1.substring(0,1);
        String row2 = input2.substring(0,1);
        String col1 = input1.substring(1);
        String col2 = input2.substring(1);
        if (!(ALPHA_LABELS.contains(row1) && ALPHA_LABELS.contains(row2) //invalid row
              && NUM_LABELS.contains(col1) && NUM_LABELS.contains(col2)) //invalid column
            || (!row1.equals(row2) && !col1.equals(col2))) //coordinates are not aligned
        {
            out.println("Error! Invalid coordinates. Try again:");
            return null;
        }
        int r1index = ALPHA_LABELS.indexOf(row1);
        int r2index = ALPHA_LABELS.indexOf(row2);
        int c1int = Integer.parseInt(col1);
        int c2int = Integer.parseInt(col2);
        // alignment check above ensures one of these is zero
        int shipLen = r2index - r1index;
        int shipWid = c2int - c1int;
        if (length != (Math.abs(shipLen + shipWid) + 1)) {
            out.println("Error! Wrong ship length. Try again:");
            return null;
        }
        // define adjacent area to check for placed ships
        int adjRowMin = Math.min(r1index, r2index) - 1;
        int adjRowMax = Math.max(r1index, r2index) + 1;
        // column is off by one (0- vs 1-based indexing)
        int adjColMin = Math.min(c1int, c2int) - 2;
        int adjColMax = Math.max(c1int, c2int);
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


    public void print(PrintStream out, boolean fogOfWar) {
        out.println(NUM_LABELS);
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
