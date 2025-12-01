package chess.board;

import chess.pieces.*;
import chess.position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an 8x8 chessboard and implements core game rules:
 * setup, moves, captures, castling, check/checkmate/stalemate, and console display.
 *
 * This is your Phase-1 backend board class used by Game (console)
 * and compatible with your piece classes in chess.pieces.*.
 */
public class Board {

    /** 8x8 board. Null = empty square. */
    private final Piece[][] board = new Piece[8][8];

    /** Captured pieces (for display). */
    private final List<Piece> capturedWhite = new ArrayList<>();
    private final List<Piece> capturedBlack = new ArrayList<>();

    /** Constructs a new board in the standard starting position. */
    public Board() {
        setupBoard();
    }

    /** Set pieces in starting position. */
    private void setupBoard() {
        // Black major pieces (top)
        board[0][0] = new Rook(false, new Position(0, 0));
        board[0][1] = new Knight(false, new Position(0, 1));
        board[0][2] = new Bishop(false, new Position(0, 2));
        board[0][3] = new Queen(false, new Position(0, 3));
        board[0][4] = new King(false, new Position(0, 4));
        board[0][5] = new Bishop(false, new Position(0, 5));
        board[0][6] = new Knight(false, new Position(0, 6));
        board[0][7] = new Rook(false, new Position(0, 7));

        // Black pawns
        for (int c = 0; c < 8; c++) {
            board[1][c] = new Pawn(false, new Position(1, c));
        }

        // White major pieces (bottom)
        board[7][0] = new Rook(true, new Position(7, 0));
        board[7][1] = new Knight(true, new Position(7, 1));
        board[7][2] = new Bishop(true, new Position(7, 2));
        board[7][3] = new Queen(true, new Position(7, 3));
        board[7][4] = new King(true, new Position(7, 4));
        board[7][5] = new Bishop(true, new Position(7, 5));
        board[7][6] = new Knight(true, new Position(7, 6));
        board[7][7] = new Rook(true, new Position(7, 7));

        // White pawns
        for (int c = 0; c < 8; c++) {
            board[6][c] = new Pawn(true, new Position(6, c));
        }
    }

    /** Clears the board and captured lists . */
    public void clear() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = null;
        capturedWhite.clear();
        capturedBlack.clear();
    }

    /** True if a position is within the 8x8 board. */
    public boolean isInBounds(Position pos) {
        int r = pos.getRow();
        int c = pos.getCol();
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    /** Get piece at a Position (or null). */
    public Piece getPiece(Position pos) {
        if (!isInBounds(pos)) return null;
        return board[pos.getRow()][pos.getCol()];
    }

    /**
     * Helper used by GUI: get piece directly by row/col.
     */
    public Piece getPieceAt(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return null;
        return board[row][col];
    }

    /** Put a piece on the board (can be null to clear square). */
    public void setPiece(Position pos, Piece piece) {
        if (!isInBounds(pos)) return;
        board[pos.getRow()][pos.getCol()] = piece;
    }

    /**
     * Move a piece from one square to another.
     * Handles captures and castling and updates piece "hasMoved" flags.
     */
    public void movePiece(Position from, Position to) {
        Piece moving = getPiece(from);
        if (moving == null) return;

        // Capture
        Piece target = getPiece(to);
        if (target != null) {
            if (target.isWhite()) capturedWhite.add(target);
            else capturedBlack.add(target);
        }

        // Move piece
        board[to.getRow()][to.getCol()] = moving;
        board[from.getRow()][from.getCol()] = null;
        moving.setPosition(to);

        // Mark king/rook as having moved (for castling rules)
        if (moving instanceof King) {
            ((King) moving).setHasMoved(true);
        }
        if (moving instanceof Rook) {
            ((Rook) moving).setHasMoved(true);
        }

        // Handle castling rook move
        if (moving instanceof King && Math.abs(from.getCol() - to.getCol()) == 2) {
            int row = from.getRow();
            if (to.getCol() > from.getCol()) {
                // Kingside: rook from h-file to f-file
                Piece rook = getPiece(new Position(row, 7));
                board[row][5] = rook;
                board[row][7] = null;
                rook.setPosition(new Position(row, 5));
                if (rook instanceof Rook) ((Rook) rook).setHasMoved(true);
            } else {
                // Queenside: rook from a-file to d-file
                Piece rook = getPiece(new Position(row, 0));
                board[row][3] = rook;
                board[row][0] = null;
                rook.setPosition(new Position(row, 3));
                if (rook instanceof Rook) ((Rook) rook).setHasMoved(true);
            }
        }
    }

    /**
     * Returns true if the king of the given color is in check.
     *
     * @param white true = white king, false = black king
     */
    public boolean isCheck(boolean white) {
        Position kingPos = findKing(white);
        if (kingPos == null) return false;

        for (Piece[] row : board) {
            for (Piece p : row) {
                if (p != null && p.isWhite() != white) {
                    if (p.canAttackPosition(this, kingPos)) return true;
                }
            }
        }
        return false;
    }

    /** Find the Position of the given color king. */
    public Position findKing(boolean white) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p instanceof King && p.isWhite() == white) {
                    return p.getPosition();
                }
            }
        }
        return null;
    }

    /**
     * Simulate a move for a given piece and destination and see
     * if that would leave its own king in check.
     */
    public boolean wouldBeInCheck(Piece piece, Position to) {
        Position from = piece.getPosition();
        Piece captured = getPiece(to);

        // Simulate
        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = null;
        piece.setPosition(to);

        boolean check = isCheck(piece.isWhite());

        // Undo
        board[from.getRow()][from.getCol()] = piece;
        board[to.getRow()][to.getCol()] = captured;
        piece.setPosition(from);

        return check;
    }

    /** True if kingside castling is currently legal for the given color. */
    public boolean canCastleKingside(boolean white) {
        int row = white ? 7 : 0;
        Piece king = board[row][4];
        Piece rook = board[row][7];

        if (!(king instanceof King) || !(rook instanceof Rook)) return false;
        if (((King) king).hasMoved() || ((Rook) rook).hasMoved()) return false;
        if (board[row][5] != null || board[row][6] != null) return false;

        if (isCheck(white)
                || wouldBeInCheck(king, new Position(row, 5))
                || wouldBeInCheck(king, new Position(row, 6))) {
            return false;
        }
        return true;
    }

    /** True if queenside castling is currently legal for the given color. */
    public boolean canCastleQueenside(boolean white) {
        int row = white ? 7 : 0;
        Piece king = board[row][4];
        Piece rook = board[row][0];

        if (!(king instanceof King) || !(rook instanceof Rook)) return false;
        if (((King) king).hasMoved() || ((Rook) rook).hasMoved()) return false;
        if (board[row][1] != null || board[row][2] != null || board[row][3] != null) return false;

        if (isCheck(white)
                || wouldBeInCheck(king, new Position(row, 3))
                || wouldBeInCheck(king, new Position(row, 2))) {
            return false;
        }
        return true;
    }

    /**
     * True if the given color has at least one legal move.
     * Used for checkmate / stalemate.
     */
    public boolean hasAnyValidMoves(boolean white) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.isWhite() == white) {
                    for (Position move : p.possibleMoves(this)) {
                        if (!wouldBeInCheck(p, move)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /** True if color is in checkmate. */
    public boolean isCheckmate(boolean white) {
        return isCheck(white) && !hasAnyValidMoves(white);
    }

    /** True if color is in stalemate. */
    public boolean isStalemate(boolean white) {
        return !isCheck(white) && !hasAnyValidMoves(white);
    }

    /** Console display for Phase-1 game. */
    public void display() {
        System.out.print("   ");
        for (char file = 'A'; file <= 'H'; file++) {
            System.out.print(file + "  ");
        }
        System.out.println();

        for (int r = 0; r < 8; r++) {
            System.out.print(8 - r + " ");
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null) {
                    System.out.print(p + " ");
                } else {
                    // mimic "##" pattern for dark squares
                    System.out.print(((r + c) % 2 == 0) ? "## " : "   ");
                }
            }
            System.out.println();
        }

        System.out.print("White captured: ");
        for (Piece p : capturedWhite) {
            System.out.print(p + " ");
        }
        System.out.println();

        System.out.print("Black captured: ");
        for (Piece p : capturedBlack) {
            System.out.print(p + " ");
        }
        System.out.println();
    }
}
