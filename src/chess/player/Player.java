package chess.player;

import chess.pieces.*;
import chess.board.Board;
import chess.position.Position;

import java.util.List;
import java.util.Scanner;

/**
 * Represents a chess player in the game.
 * Responsible for making moves and handling pawn promotions.
 */
public class Player {
    private boolean white;
    private Board board;

    /**
     * Constructs a Player object.
     *
     * @param white True if this player controls the white pieces, false for black.
     * @param board Reference to the current Board object.
     */
    public Player(boolean white, Board board) {
        this.white = white;
        this.board = board;
    }

    /**
     * Returns true if this player is controlling white pieces.
     *
     * @return True if white, false if black.
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Attempts to make a move from the source position to the destination position.
     * Performs basic validation: checks that a piece exists at the source, that the piece belongs to the player,
     * and that the move is legal for that piece. Handles pawn promotion if a pawn reaches the last rank.
     *
     * @param from Source position.
     * @param to   Destination position.
     * @return True if the move was successfully executed, false otherwise.
     */
    public boolean makeMove(Position from, Position to) {
        Piece moving = board.getPiece(from);

        if (moving == null) {
            System.out.println("No piece at source square.");
            return false;
        }

        if (moving.isWhite() != white) {
            System.out.println("It's " + (white ? "White" : "Black") + "'s turn!");
            return false;
        }

        List<Position> validMoves = moving.possibleMoves(board);
        if (!validMoves.contains(to)) {
            System.out.println("Invalid move for that piece.");
            return false;
        }

        // Execute move
        board.movePiece(from, to);

        // Handle pawn promotion
        if (moving instanceof Pawn) {
            if ((white && to.getRow() == 0) || (!white && to.getRow() == 7)) {
                String promotionChoice = "Q"; // default
                Scanner scanner = new Scanner(System.in);

                // Prompt user for promotion piece
                System.out.print((white ? "White" : "Black") + " pawn promotion! Enter piece to promote to (Q,R,B,N, default Q): ");
                String input = scanner.nextLine().trim().toUpperCase();
                if (input.matches("[QRBN]")) {
                    promotionChoice = input;
                }

                Piece promoted;
                switch (promotionChoice) {
                    case "R": promoted = new Rook(white, to); break;
                    case "B": promoted = new Bishop(white, to); break;
                    case "N": promoted = new Knight(white, to); break;
                    default: promoted = new Queen(white, to); break;
                }

                board.setPiece(to, promoted);
                System.out.println((white ? "White" : "Black") + " pawn promoted to " + promoted.getClass().getSimpleName() + "!");
            }
        }

        return true;
    }
}
