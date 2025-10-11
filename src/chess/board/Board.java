package chess.board;

import chess.pieces.*;
import chess.position.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chessboard with an 8x8 grid of squares.
 * Handles piece placement, movement, captures, castling, check, checkmate, stalemate, and board display.
 */
public class Board {

    /** 8x8 grid representing the chessboard. Null indicates an empty square. */
    private Piece[][] board = new Piece[8][8];

    /** List of captured white pieces */
    private List<Piece> capturedWhite = new ArrayList<>();

    /** List of captured black pieces */
    private List<Piece> capturedBlack = new ArrayList<>();

    /**
     * Constructs a new Board and sets up pieces in their initial positions.
     */
    public Board() {
        setupBoard();
    }

    /**
     * Initializes the board with all pieces in their standard starting positions.
     */
    private void setupBoard() {
        // Black pieces
        board[0][0] = new Rook(false, new Position(0, 0));
        board[0][1] = new Knight(false, new Position(0, 1));
        board[0][2] = new Bishop(false, new Position(0, 2));
        board[0][3] = new Queen(false, new Position(0, 3));
        board[0][4] = new King(false, new Position(0, 4));
        board[0][5] = new Bishop(false, new Position(0, 5));
        board[0][6] = new Knight(false, new Position(0, 6));
        board[0][7] = new Rook(false, new Position(0, 7));
        for (int i = 0; i < 8; i++) board[1][i] = new Pawn(false, new Position(1, i));

        // White pieces
        board[7][0] = new Rook(true, new Position(7, 0));
        board[7][1] = new Knight(true, new Position(7, 1));
        board[7][2] = new Bishop(true, new Position(7, 2));
        board[7][3] = new Queen(true, new Position(7, 3));
        board[7][4] = new King(true, new Position(7, 4));
        board[7][5] = new Bishop(true, new Position(7, 5));
        board[7][6] = new Knight(true, new Position(7, 6));
        board[7][7] = new Rook(true, new Position(7, 7));
        for (int i = 0; i < 8; i++) board[6][i] = new Pawn(true, new Position(6, i));
    }

    /**
     * Clears the board of all pieces and resets captured piece lists.
     * Useful for testing or restarting a game.
     */
    public void clear() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = null;

        capturedWhite.clear();
        capturedBlack.clear();
    }

    /**
     * Checks if a given position is within the bounds of the board.
     *
     * @param pos Position to check
     * @return true if the position is valid, false otherwise
     */
    public boolean isInBounds(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < 8 && pos.getCol() >= 0 && pos.getCol() < 8;
    }

    /**
     * Returns the piece at a given position.
     * @param pos Position to query
     * @return Piece at the position, or null if empty
     */
    public Piece getPiece(Position pos) {
        if (!isInBounds(pos)) return null;
        return board[pos.getRow()][pos.getCol()];
    }

    /**
     * Places a piece at the specified position.
     * @param pos Position to place the piece
     * @param piece Piece to place (can be null)
     */
    public void setPiece(Position pos, Piece piece) {
        if (!isInBounds(pos)) return;
        board[pos.getRow()][pos.getCol()] = piece;
    }

    /**
     * Moves a piece from one position to another.
     * Handles captures, castling, and updating piece movement flags.
     * @param from Starting position
     * @param to Destination position
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

        // Update board
        board[to.getRow()][to.getCol()] = moving;
        board[from.getRow()][from.getCol()] = null;
        moving.setPosition(to);

        // Mark king or rook as moved
        if (moving instanceof King) ((King) moving).setHasMoved(true);
        if (moving instanceof Rook) ((Rook) moving).setHasMoved(true);

        // Handle castling
        if (moving instanceof King && Math.abs(from.getCol() - to.getCol()) == 2) {
            int row = from.getRow();
            if (to.getCol() > from.getCol()) { // Kingside
                Piece rook = getPiece(new Position(row, 7));
                board[row][5] = rook;
                board[row][7] = null;
                rook.setPosition(new Position(row, 5));
                if (rook instanceof Rook) ((Rook) rook).setHasMoved(true);
            } else { // Queenside
                Piece rook = getPiece(new Position(row, 0));
                board[row][3] = rook;
                board[row][0] = null;
                rook.setPosition(new Position(row, 3));
                if (rook instanceof Rook) ((Rook) rook).setHasMoved(true);
            }
        }
    }

    /**
     * Returns true if the king of the given color is currently in check.
     * @param white Color of king
     * @return true if the king is under attack
     */
    public boolean isCheck(boolean white) {
        Position kingPos = findKing(white);
        if (kingPos == null) return false;

        for (Piece[] row : board)
            for (Piece p : row)
                if (p != null && p.isWhite() != white && p.canAttackPosition(this, kingPos))
                    return true;

        return false;
    }

    /**
     * Finds the king's position for the given color.
     * @param white Color of king
     * @return Position of the king, or null if not found
     */
    public Position findKing(boolean white) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p instanceof King && p.isWhite() == white)
                    return p.getPosition();
            }
        return null;
    }

    /**
     * Simulates a move and checks if it would leave the player's king in check.
     * @param piece Piece to move
     * @param to Destination position
     * @return true if the move would leave the king in check
     */
    public boolean wouldBeInCheck(Piece piece, Position to) {
        Position from = piece.getPosition();
        Piece target = getPiece(to);

        // Simulate move
        board[to.getRow()][to.getCol()] = piece;
        board[from.getRow()][from.getCol()] = null;
        piece.setPosition(to);

        boolean check = isCheck(piece.isWhite());

        // Revert move
        board[from.getRow()][from.getCol()] = piece;
        board[to.getRow()][to.getCol()] = target;
        piece.setPosition(from);

        return check;
    }

    /**
     * Checks if castling kingside is allowed for the given color.
     * @param white Color to check
     * @return true if kingside castling is legal
     */
    public boolean canCastleKingside(boolean white) {
        int row = white ? 7 : 0;
        Piece king = board[row][4];
        Piece rook = board[row][7];
        if (!(king instanceof King) || !(rook instanceof Rook)) return false;
        if (((King) king).hasMoved() || ((Rook) rook).hasMoved()) return false;
        if (board[row][5] != null || board[row][6] != null) return false;
        if (isCheck(white) || wouldBeInCheck(king, new Position(row, 5)) || wouldBeInCheck(king, new Position(row, 6)))
            return false;
        return true;
    }

    /**
     * Checks if castling queenside is allowed for the given color.
     * @param white Color to check
     * @return true if queenside castling is legal
     */
    public boolean canCastleQueenside(boolean white) {
        int row = white ? 7 : 0;
        Piece king = board[row][4];
        Piece rook = board[row][0];
        if (!(king instanceof King) || !(rook instanceof Rook)) return false;
        if (((King) king).hasMoved() || ((Rook) rook).hasMoved()) return false;
        if (board[row][1] != null || board[row][2] != null || board[row][3] != null) return false;
        if (isCheck(white) || wouldBeInCheck(king, new Position(row, 3)) || wouldBeInCheck(king, new Position(row, 2)))
            return false;
        return true;
    }

    /**
     * Returns true if the given color has at least one legal move available.
     * @param white Color to check
     * @return true if at least one valid move exists
     */
    public boolean hasAnyValidMoves(boolean white) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p != null && p.isWhite() == white) {
                    for (Position move : p.possibleMoves(this)) {
                        if (!wouldBeInCheck(p, move)) return true;
                    }
                }
            }
        return false;
    }

    /**
     * Returns true if the given color is in checkmate.
     * @param white Color to check
     * @return true if checkmate
     */
    public boolean isCheckmate(boolean white) {
        return isCheck(white) && !hasAnyValidMoves(white);
    }

    /**
     * Returns true if the given color is in stalemate.
     * @param white Color to check
     * @return true if stalemate
     */
    public boolean isStalemate(boolean white) {
        return !isCheck(white) && !hasAnyValidMoves(white);
    }

    /**
     * Displays the board and captured pieces in the console.
     */
    public void display() {
        System.out.print("   ");
        for (char c = 'A'; c <= 'H'; c++) System.out.print(c + "  ");
        System.out.println();
        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + " ");
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p != null) System.out.print(p + " ");
                else System.out.print((i + j) % 2 == 0 ? "## " : "   ");
            }
            System.out.println();
        }
        System.out.print("White captured: ");
        for (Piece p : capturedWhite) System.out.print(p + " ");
        System.out.println();
        System.out.print("Black captured: ");
        for (Piece p : capturedBlack) System.out.print(p + " ");
        System.out.println();
    }
}
