package chess.game;

import chess.board.Board;
import chess.player.Player;
import chess.position.Position;
import chess.pieces.Piece;

import java.util.Scanner;

/**
 * Main class to run a console-based chess game.
 * Handles the game loop, player input, move execution, and basic game state detection
 * including checkmate and stalemate.
 */
public class Game {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean whiteTurn;

    /**
     * Initializes a new game with a standard chess board setup.
     * Creates both white and black players and sets the initial turn to white.
     */
    public Game() {
        board = new Board();
        whitePlayer = new Player(true, board);
        blackPlayer = new Player(false, board);
        whiteTurn = true; // White always starts
    }

    /**
     * Starts the main game loop.
     * Continuously prompts players for moves, executes moves, handles captures and promotions,
     * and checks for game-ending conditions such as checkmate and stalemate.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            board.display();
            System.out.println((whiteTurn ? "White" : "Black") + "'s turn. Enter move (e.g., E2 E4; EXIT to quit):");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("EXIT")) {
                System.out.println("Game exited.");
                break;
            }

            // Split input into source and destination positions
            String[] tokens = input.split("\\s+");
            if (tokens.length != 2) {
                System.out.println("Invalid input. Please enter moves like E2 E4.");
                continue;
            }

            // Convert notation to Position objects
            Position from = Position.fromString(tokens[0]);
            Position to = Position.fromString(tokens[1]);

            if (from == null || to == null) {
                System.out.println("Invalid positions. Use format like E2 E4.");
                continue;
            }

            Player currentPlayer = whiteTurn ? whitePlayer : blackPlayer;

            // Check if there's a piece to capture
            Piece targetPiece = board.getPiece(to);

            // Attempt to make the move
            boolean moveSuccessful = currentPlayer.makeMove(from, to);

            if (moveSuccessful) {
                // Report captures
                if (targetPiece != null) {
                    System.out.println((targetPiece.isWhite() ? "White" : "Black") + " captured: " + targetPiece);
                }

                // Check for checkmate
                if (board.isCheckmate(!whiteTurn)) { // Opponent just moved into checkmate
                    board.display();
                    System.out.println((whiteTurn ? "White" : "Black") + " wins by checkmate!");
                    break;
                }

                // Check for stalemate
                if (board.isStalemate(!whiteTurn)) { // Opponent has no legal moves but is not in check
                    board.display();
                    System.out.println("Game is a stalemate! It's a draw.");
                    break;
                }

                // Switch turns
                whiteTurn = !whiteTurn;
            } else {
                System.out.println("Move not allowed. Try again.");
            }
        }

        scanner.close();
    }

    /**
     * Entry point of the program. Starts a new chess game.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
