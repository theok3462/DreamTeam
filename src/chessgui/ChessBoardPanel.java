```java
package chessgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Central board component: draws the 8x8 board and pieces, handles
 * both click-and-move and drag-and-drop, and talks to GameState.
 *
 * In this version the BOARD itself fills the whole panel area:
 * - tiles are stretched to cover the full width/height (no big border),
 * - the "Square / Piece Size" spinner controls how big pieces are
 *   inside each tile (not the physical board size).
 */
public class ChessBoardPanel extends JPanel {

    private GameState state;
    private SettingsManager settings;
    private final HistoryPanel historyPanel;

    // selection / dragging
    private int selectedRow = -1, selectedCol = -1;
    private boolean dragging = false;
    private int dragRow = -1, dragCol = -1;
    private int dragOffsetX, dragOffsetY;
    private int dragX, dragY;

    // optional callback for game-over handling (set by ChessFrame)
    private Runnable gameOverHandler;

    // optional callback after a successful human move
    private Runnable afterHumanMove;

    public ChessBoardPanel(GameState state,
                           SettingsManager settings,
                           HistoryPanel historyPanel) {
        this.state = state;
        this.settings = settings;
        this.historyPanel = historyPanel;

        setOpaque(true);

        BoardMouseAdapter adapter = new BoardMouseAdapter();
        addMouseListener(adapter);
        addMouseMotionListener(adapter);

        updatePreferredSize();
    }

    public void setAfterHumanMove(Runnable r) {
        this.afterHumanMove = r;
    }

    /** Allow ChessFrame to swap in a loaded state & settings. */
    public void setStateAndSettings(GameState state, SettingsManager settings) {
        this.state = state;
        this.settings = settings;
        clearSelection();
        updatePreferredSize();
        repaint();
    }

    /** Called when theme / piece size changed. */
    public void reload() {
        updatePreferredSize();
        revalidate();
        repaint();
    }

    public void clearSelection() {
        selectedRow = selectedCol = -1;
        dragging = false;
        repaint();
    }

    public void setGameOverHandler(Runnable r) {
        this.gameOverHandler = r;
    }

    /**
     * Preferred size for layout when window is not maximised.
     * We keep this constant so the spinner changes **piece size**,
     * not the physical board size. The board will stretch to fill
     * whatever space it gets.
     */
    private void updatePreferredSize() {
        int base = 8 * 64; // 8x8 board, 64px tiles by default
        setPreferredSize(new Dimension(base, base));
    }

    /** Current tile width based on panel size. */
    private int getTileWidth() {
        int w = getWidth();
        return (w <= 0) ? 64 : Math.max(1, w / 8);
    }

    /** Current tile height based on panel size. */
    private int getTileHeight() {
        int h = getHeight();
        return (h <= 0) ? 64 : Math.max(1, h / 8);
    }

    /** Converts mouse coordinates to a board square, or (-1,-1) if outside board. */
    private Point mouseToBoardSquare(int mouseX, int mouseY) {
        int tileW = getTileWidth();
        int tileH = getTileHeight();
        int boardW = 8 * tileW;
        int boardH = 8 * tileH;

        if (mouseX < 0 || mouseY < 0 ||
                mouseX >= boardW || mouseY >= boardH) {
            return new Point(-1, -1);
        }

        int col = mouseX / tileW;
        int row = mouseY / tileH;
        return new Point(col, row);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == null || settings == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int tileW = getTileWidth();
        int tileH = getTileHeight();

        // Fill whole panel with dark square color.
        g2.setColor(settings.getDarkColor());
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw board, using the entire panel area.
        Color light = settings.getLightColor();
        Color dark = settings.getDarkColor();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isLight = ((row + col) % 2 == 0);
                g2.setColor(isLight ? light : dark);
                int x = col * tileW;
                int y = row * tileH;
                g2.fillRect(x, y, tileW, tileH);
            }
        }

        // Highlight selected square
        if (selectedRow >= 0 && selectedCol >= 0) {
            g2.setColor(new Color(0, 255, 0, 120));
            g2.fillRect(selectedCol * tileW, selectedRow * tileH, tileW, tileH);
        }

        // Highlight last move (if any)
        if (!state.getHistory().isEmpty()) {
            Move last = state.getHistory().get(state.getHistory().size() - 1);
            g2.setColor(new Color(255, 255, 0, 90));
            g2.fillRect(last.sy * tileW, last.sx * tileH, tileW, tileH);
            g2.fillRect(last.dy * tileW, last.dx * tileH, tileW, tileH);
        }

        // Piece size = spinner value, capped so it fits inside the square.
        int pieceSizeSetting = settings.getSquareSize(); // interpreted as piece size
        int maxTile = Math.min(tileW, tileH);
        int drawSize = Math.min(pieceSizeSetting, maxTile);

        // Draw pieces
        String theme = state.getCurrentTheme();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = state.getPiece(row, col);
                if (p == null) continue;

                // If dragging this piece, don't draw at its original square.
                if (dragging && row == dragRow && col == dragCol) continue;

                int squareX = col * tileW;
                int squareY = row * tileH;

                // Center the piece within the square
                int px = squareX + (tileW - drawSize) / 2;
                int py = squareY + (tileH - drawSize) / 2;

                p.draw(g2, px, py, drawSize, theme);
            }
        }

        // Draw dragged piece on top if any
        if (dragging && dragRow >= 0 && dragCol >= 0) {
            Piece p = state.getPiece(dragRow, dragCol);
            if (p != null) {
                p.draw(g2, dragX - dragOffsetX, dragY - dragOffsetY,
                        drawSize, state.getCurrentTheme());
            }
        }

        g2.dispose();
    }

    // ==== Input handling ========

    private class BoardMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (state == null || state.isGameOver()) return;

            Point sq = mouseToBoardSquare(e.getX(), e.getY());
            int col = sq.x;
            int row = sq.y;
            if (!inBounds(row, col)) return;

            Piece clicked = state.getPiece(row, col);

            // No selection yet select own piece if correct color
            if (selectedRow < 0 || selectedCol < 0) {
                if (clicked != null && clicked.getColor() == state.getCurrentPlayer()) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                } else if (clicked != null) {
                    warnWrongTurn();
                }
                return;
            }

            // There is already a selected piece
            if (clicked != null &&
                    clicked.getColor() == state.getCurrentPlayer() &&
                    (row != selectedRow || col != selectedCol)) {
                // Change selection to a different own piece (no move attempt)
                selectedRow = row;
                selectedCol = col;
                repaint();
                return;
            }

            // Otherwise, try click to move from selected square to this square
            attemptMove(selectedRow, selectedCol, row, col);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (state == null || state.isGameOver()) return;

            if (!dragging) {
                // Start drag if there is a selected piece and we begin moving
                if (selectedRow >= 0 && selectedCol >= 0) {
                    Piece p = state.getPiece(selectedRow, selectedCol);
                    if (p != null && p.getColor() == state.getCurrentPlayer()) {
                        startDrag(selectedRow, selectedCol, e.getX(), e.getY());
                    }
                }
            }

            if (dragging) {
                dragX = e.getX();
                dragY = e.getY();
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!dragging || state == null || state.isGameOver()) {
                dragging = false;
                return;
            }

            dragging = false;

            Point sq = mouseToBoardSquare(e.getX(), e.getY());
            int col = sq.x;
            int row = sq.y;

            if (!inBounds(row, col)) {
                // drop off board -> cancel drag but keep selection
                repaint();
                return;
            }

            attemptMove(dragRow, dragCol, row, col);
        }

        private void startDrag(int row, int col, int mouseX, int mouseY) {
            dragging = true;
            dragRow = row;
            dragCol = col;

            int tileW = getTileWidth();
            int tileH = getTileHeight();
            int pieceSizeSetting = settings.getSquareSize();
            int maxTile = Math.min(tileW, tileH);
            int drawSize = Math.min(pieceSizeSetting, maxTile);

            int squareX = col * tileW;
            int squareY = row * tileH;
            int px = squareX + (tileW - drawSize) / 2;
            int py = squareY + (tileH - drawSize) / 2;

            dragOffsetX = mouseX - px;
            dragOffsetY = mouseY - py;
            dragX = mouseX;
            dragY = mouseY;
        }
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    // ==== Move / promotion / game over logic ====

    private void attemptMove(int sr, int sc, int dr, int dc) {
        if (!inBounds(sr, sc) || !inBounds(dr, dc)) return;
        if (sr == dr && sc == dc) {
            // same square: keep selection as-is
            selectedRow = sr;
            selectedCol = sc;
            repaint();
            return;
        }

        Piece moving = state.getPiece(sr, sc);
        if (moving == null) {
            clearSelection();
            return;
        }

        // Enforce turn here so we can show a clear message.
        if (moving.getColor() != state.getCurrentPlayer()) {
            warnWrongTurn();
            clearSelection();
            return;
        }

        boolean success;
        // Promotion case?
        if (moving.getType() == PieceType.PAWN &&
                ((dr == 0 && moving.getColor() == PieceColor.WHITE) ||
                        (dr == 7 && moving.getColor() == PieceColor.BLACK))) {

            PieceType promoteTo = askPromotionPiece(moving.getColor());
            if (promoteTo == null) {
                // user cancelled promotion -> cancel move
                clearSelection();
                return;
            }
            success = state.makeMove(sr, sc, dr, dc, promoteTo);
        } else {
            success = state.makeMove(sr, sc, dr, dc);
        }

        if (!success) {
            warnInvalidMove();
            // keep the piece selected so user can try a different target
            selectedRow = sr;
            selectedCol = sc;
        } else {
            // move accepted
            selectedRow = selectedCol = -1;
            historyPanel.reloadFromState();
            repaint();

            if (afterHumanMove != null) {
                afterHumanMove.run();
            }

            // Check for game over
            if (state.isGameOver() && gameOverHandler != null) {
                // Let the frame show the restart / exit dialog
                SwingUtilities.invokeLater(gameOverHandler);
            }
        }
    }

    private void warnWrongTurn() {
        String who = (state.getCurrentPlayer() == PieceColor.WHITE) ? "White" : "Black";
        JOptionPane.showMessageDialog(
                this,
                "It is " + who + "'s turn.",
                "Wrong Player",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void warnInvalidMove() {
        String who = (state.getCurrentPlayer() == PieceColor.WHITE) ? "White" : "Black";
        JOptionPane.showMessageDialog(
                this,
                "Invalid move.",
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Dialog for pawn promotion.
     *
     * @return chosen PieceType, or null if user cancels.
     */
    private PieceType askPromotionPiece(PieceColor color) {
        String side = (color == PieceColor.WHITE) ? "White" : "Black";
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int res = JOptionPane.showOptionDialog(
                this,
                side + " pawn promotion: choose piece:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        return switch (res) {
            case 1 -> PieceType.ROOK;
            case 2 -> PieceType.BISHOP;
            case 3 -> PieceType.KNIGHT;
            case 0 -> PieceType.QUEEN;
            default -> null; // user closed dialog
        };
    }
}
```
