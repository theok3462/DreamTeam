package chess.game;

import chess.board.Board;
import chess.position.Position;
import chess.player.Player;
import chess.pieces.Piece; // backend piece
import chessgui.GameState;
import chessgui.Move;
import chessgui.PieceColor;
import chessgui.PieceType;

/**
 * Main class to run a console-based chess game.
 * Handles the game loop, player input, move execution, and basic game state detection.
 */
public class Game {

    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean whiteTurn;
    private GameState gameState; // GUI-only
    private boolean gameOver;

    public Game(GameState gameState) {
        this.gameState = gameState;
        reset();
    }

    /**
     * Resets the game to initial state.
     */
    public void reset() {
        this.board = new Board(); // Backend board
        this.whitePlayer = new Player(true, board);
        this.blackPlayer = new Player(false, board);
        this.whiteTurn = true;
        this.gameOver = false;

        if (this.gameState != null) {
            this.gameState.reset(); // GUI board
        }
    }

    /**
     * Copies the state from another Game instance.
     * Syncs backend board → GUI GameState.
     */
    public void copyFrom(Game other) {
        if (other == null) return;

        this.board = other.getBoard();
        this.whitePlayer = other.whitePlayer;
        this.blackPlayer = other.blackPlayer;
        this.whiteTurn = other.whiteTurn;
        this.gameOver = other.gameOver;

        // Copy backend board into GUI
        this.gameState = new GameState();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece backendPiece = other.board.getPiece(new Position(row, col));
                this.gameState.setPieceAt(row, col, convertToGuiPiece(backendPiece));
            }
        }

        this.gameState.setCurrentPlayer(other.gameState.getCurrentPlayer());
        this.gameState.moves.clear();
        this.gameState.moves.addAll(other.gameState.moves);
    }

    /**
     * Starts console game loop.
     */
    public void start() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (!gameOver) {
            board.display();
            System.out.println((whiteTurn ? "White" : "Black")
                    + "'s turn. Enter move (E2 E4; EXIT to quit):");

            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("EXIT")) break;

            String[] tokens = input.split("\\s+");
            if (tokens.length != 2) {
                System.out.println("Invalid input. Use format like E2 E4.");
                continue;
            }

            Position from = Position.fromString(tokens[0]);
            Position to = Position.fromString(tokens[1]);

            if (from == null || to == null) {
                System.out.println("Invalid positions.");
                continue;
            }

            Player currentPlayer = whiteTurn ? whitePlayer : blackPlayer;
            Piece targetPiece = board.getPiece(to);
            boolean moveSuccessful = currentPlayer.makeMove(from, to);

            if (moveSuccessful) {
                if (targetPiece != null) {
                    System.out.println((targetPiece.isWhite() ? "White" : "Black")
                            + " captured: " + targetPiece);
                }

                updateGameState(from, to, targetPiece);

                if (board.isCheckmate(!whiteTurn)) {
                    board.display();
                    System.out.println((whiteTurn ? "White" : "Black")
                            + " wins by checkmate!");
                    gameOver = true;
                    break;
                }

                if (board.isStalemate(!whiteTurn)) {
                    board.display();
                    System.out.println("Stalemate! Draw.");
                    gameOver = true;
                    break;
                }

                whiteTurn = !whiteTurn;
            } else {
                System.out.println("Move not allowed. Try again.");
            }
        }

        scanner.close();
    }

    /**
     * Convert backend piece → GUI piece
     */
    private chessgui.Piece convertToGuiPiece(Piece backendPiece) {
        if (backendPiece == null) return null;

        PieceType type;

        // Use instanceof to detect piece type
        if (backendPiece instanceof chess.pieces.Pawn) {
            type = PieceType.PAWN;
        } else if (backendPiece instanceof chess.pieces.Rook) {
            type = PieceType.ROOK;
        } else if (backendPiece instanceof chess.pieces.Knight) {
            type = PieceType.KNIGHT;
        } else if (backendPiece instanceof chess.pieces.Bishop) {
            type = PieceType.BISHOP;
        } else if (backendPiece instanceof chess.pieces.Queen) {
            type = PieceType.QUEEN;
        } else if (backendPiece instanceof chess.pieces.King) {
            type = PieceType.KING;
        } else {
            type = null;
        }

        return new chessgui.Piece(
                backendPiece.isWhite() ? PieceColor.WHITE : PieceColor.BLACK,
                type
        );
    }

    /**
     * Updates GUI GameState after a move.
     */
    private void updateGameState(Position from, Position to, Piece capturedPiece) {
        Piece movedPiece = board.getPiece(to);

        gameState.setPieceAt(to.getRow(), to.getCol(), convertToGuiPiece(movedPiece));
        gameState.setPieceAt(from.getRow(), from.getCol(), null);

        gameState.moves.push(new Move(
                from.getRow(), from.getCol(),
                to.getRow(), to.getCol(),
                convertToGuiPiece(movedPiece),
                convertToGuiPiece(capturedPiece),
                gameState.getCurrentPlayer()
        ));

        gameState.toggleCurrentPlayer();
    }

    public boolean isWhiteTurn() { return whiteTurn; }
    public Player getCurrentPlayer() { return whiteTurn ? whitePlayer : blackPlayer; }
    public Board getBoard() { return board; }

    public static void main(String[] args) {
        GameState gs = new GameState();
        gs.initializeClassic();
        Game game = new Game(gs);
        game.start();
    }
}
