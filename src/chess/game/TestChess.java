package chess.game;

import chess.board.Board;
import chess.pieces.*;
import chess.position.Position;
import java.util.List;

/**
 * Test class for Chess pieces, moves, and game mechanics.
 * Demonstrates possible moves, pawn promotion, captures, castling,
 * blocked moves, and basic piece functionality for both white and black.
 */
public class TestChess {

    /**
     * Main method to run all chess piece tests.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== Chess Piece Tests ===\n");

        testPawnMoves();
        testPawnCapture();
        testPawnPromotion();
        testRookMoves();
        testBlockedRookMoves();
        testKnightMoves();
        testBishopMoves();
        testBlockedBishopMoves();
        testQueenMoves();
        testBlockedQueenMoves();
        testKingMoves();
        testBlackPieces();

        System.out.println("\nAll tests executed.");
    }

    /** Test white pawn moves from initial position */
    private static void testPawnMoves() {
        Board board = new Board();
        board.clear();
        Pawn pawn = new Pawn(true, Position.fromString("E2"));
        board.setPiece(pawn.getPosition(), pawn);

        System.out.println("Pawn moves from E2: " + pawn.possibleMoves(board));
    }

    /** Test white pawn diagonal captures */
    private static void testPawnCapture() {
        Board board = new Board();
        board.clear();
        Pawn pawn = new Pawn(true, Position.fromString("D4"));
        board.setPiece(pawn.getPosition(), pawn);
        Pawn blackPawn = new Pawn(false, Position.fromString("C5"));
        board.setPiece(blackPawn.getPosition(), blackPawn);

        System.out.println("Pawn at D4 can capture: " + pawn.possibleMoves(board));
    }

    /** Test pawn promotion to a queen */
    private static void testPawnPromotion() {
        Board board = new Board();
        board.clear();
        Pawn pawn = new Pawn(true, Position.fromString("A7"));
        board.setPiece(pawn.getPosition(), pawn);

        Position promoteTo = Position.fromString("A8");
        boolean success = pawn.possibleMoves(board).contains(promoteTo);
        System.out.println("Promoting pawn at A7 to A8 possible? " + success);

        if (success) {
            board.movePiece(pawn.getPosition(), promoteTo);
            Queen q = new Queen(true, promoteTo);
            board.setPiece(promoteTo, q);
            System.out.println("Piece at A8 after promotion: " + board.getPiece(promoteTo));
        }
    }

    /** Test rook moves from starting position */
    private static void testRookMoves() {
        Board board = new Board();
        board.clear();
        Rook rook = new Rook(true, Position.fromString("A1"));
        board.setPiece(rook.getPosition(), rook);

        System.out.println("Rook moves from A1: " + rook.possibleMoves(board));
    }

    /** Test rook moves blocked by friendly and enemy pieces */
    private static void testBlockedRookMoves() {
        Board board = new Board();
        board.clear();
        Rook rook = new Rook(true, Position.fromString("D4"));
        board.setPiece(rook.getPosition(), rook);
        Pawn friendly = new Pawn(true, Position.fromString("D5"));
        board.setPiece(friendly.getPosition(), friendly);
        Pawn enemy = new Pawn(false, Position.fromString("B4"));
        board.setPiece(enemy.getPosition(), enemy);

        System.out.println("Blocked rook moves from D4: " + rook.possibleMoves(board));
    }

    /** Test knight moves from starting position */
    private static void testKnightMoves() {
        Board board = new Board();
        board.clear();
        Knight knight = new Knight(true, Position.fromString("B1"));
        board.setPiece(knight.getPosition(), knight);

        System.out.println("Knight moves from B1: " + knight.possibleMoves(board));
    }

    /** Test bishop moves from starting position */
    private static void testBishopMoves() {
        Board board = new Board();
        board.clear();
        Bishop bishop = new Bishop(true, Position.fromString("C1"));
        board.setPiece(bishop.getPosition(), bishop);

        System.out.println("Bishop moves from C1: " + bishop.possibleMoves(board));
    }

    /** Test bishop moves blocked by friendly and enemy pieces */
    private static void testBlockedBishopMoves() {
        Board board = new Board();
        board.clear();
        Bishop bishop = new Bishop(true, Position.fromString("D4"));
        board.setPiece(bishop.getPosition(), bishop);
        Pawn friendly = new Pawn(true, Position.fromString("E5"));
        board.setPiece(friendly.getPosition(), friendly);
        Pawn enemy = new Pawn(false, Position.fromString("B2"));
        board.setPiece(enemy.getPosition(), enemy);

        System.out.println("Blocked bishop moves from D4: " + bishop.possibleMoves(board));
    }

    /** Test queen moves from starting position */
    private static void testQueenMoves() {
        Board board = new Board();
        board.clear();
        Queen queen = new Queen(true, Position.fromString("D1"));
        board.setPiece(queen.getPosition(), queen);

        System.out.println("Queen moves from D1: " + queen.possibleMoves(board));
    }

    /** Test queen moves blocked by friendly and enemy pieces */
    private static void testBlockedQueenMoves() {
        Board board = new Board();
        board.clear();
        Queen queen = new Queen(true, Position.fromString("D4"));
        board.setPiece(queen.getPosition(), queen);
        Pawn friendly = new Pawn(true, Position.fromString("D5"));
        board.setPiece(friendly.getPosition(), friendly);
        Pawn enemy = new Pawn(false, Position.fromString("F6"));
        board.setPiece(enemy.getPosition(), enemy);

        System.out.println("Blocked queen moves from D4: " + queen.possibleMoves(board));
    }

    /** Test king moves including castling */
    private static void testKingMoves() {
        Board board = new Board();
        board.clear();
        King king = new King(true, Position.fromString("E1"));
        board.setPiece(king.getPosition(), king);
        Rook kingsideRook = new Rook(true, Position.fromString("H1"));
        Rook queensideRook = new Rook(true, Position.fromString("A1"));
        board.setPiece(kingsideRook.getPosition(), kingsideRook);
        board.setPiece(queensideRook.getPosition(), queensideRook);

        System.out.println("King moves from E1 including castling: " + king.possibleMoves(board));
    }

    /** Test black pieces moves from starting positions */
    private static void testBlackPieces() {
        Board board = new Board();
        board.clear();
        Pawn blackPawn = new Pawn(false, Position.fromString("D7"));
        board.setPiece(blackPawn.getPosition(), blackPawn);
        Rook blackRook = new Rook(false, Position.fromString("H8"));
        board.setPiece(blackRook.getPosition(), blackRook);

        System.out.println("Black pawn moves from D7: " + blackPawn.possibleMoves(board));
        System.out.println("Black rook moves from H8: " + blackRook.possibleMoves(board));
    }
}
