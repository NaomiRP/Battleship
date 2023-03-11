package battleship;

import battleship.objects.GameBoard;

import java.io.PrintStream;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PrintStream out = System.out;

        GameBoard player1 = new GameBoard();
        player1.placeShips(out, in);

        out.println("The game starts!");
        player1.print(out, true);

        player1.takeShot(out, in);
        player1.print(out, false);
    }
}
