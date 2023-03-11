package battleship;

import battleship.objects.GameBoard;

import java.io.PrintStream;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PrintStream out = System.out;

        GameBoard player1 = new GameBoard(1, 2);
        player1.placeShips(out, in);
        switchPlayer(out, in);
        GameBoard player2 = new GameBoard(2, 1);
        player2.placeShips(out, in);

        out.println("The game starts!");
        switchPlayer(out, in);

        boolean p1shipsSunk = false;
        boolean p2shipsSunk = false;

        while (!p1shipsSunk) {
            printBoards(player1, player2, out);
            // player 1 shoots at player 2's board
            if (player2.takeShot(out, in)) {
                p2shipsSunk = player2.allShipsSunk();
                if (p2shipsSunk)
                    break;
            }
            switchPlayer(out, in);
            printBoards(player2, player1, out);
            if (player1.takeShot(out, in))
                p1shipsSunk = player1.allShipsSunk();
            if (!p1shipsSunk)
                switchPlayer(out, in);
        }

        int winningPlayer = p2shipsSunk ? 1 : 2;
        out.println("You sank the last ship, Player " + winningPlayer + ". You won. Congratulations!");
    }

    private static void switchPlayer(PrintStream out, Scanner in) {
        if (in.hasNextLine()) { // most input doesn't consume the \n, so advance past it before asking the player to 'enter'
            in.nextLine();
        }
        out.println("Press Enter and pass the move to another player");
        in.nextLine();
        out.print("\n".repeat(20));
    }

    private static void printBoards(GameBoard curPlayer, GameBoard opponent, PrintStream out) {
        out.println();
        opponent.print(out, true, false);
        out.println("-".repeat(22));
        curPlayer.print(out, false, false);
        out.println();
    }
}
