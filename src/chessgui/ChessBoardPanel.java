package chessgui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Main chessboard panel with:
 * - Drag and drop
 * - Click-to-move
 * - Color theme + piece style + resizing
 * - Supports three piece styles: default, vibrant, ocean
 */
public class ChessBoardPanel extends JPanel {
    private final GameState state;
    private final SettingsManager settings;
    private final Consumer<PieceColor> kingCapturedCallback;
    private final Consumer<Move> moveCallback;

    private int selectedRow = -1, selectedCol = -1;
    private JPanel grid;

    public ChessBoardPanel(GameState state, SettingsManager settings,
                           Consumer<PieceColor> kingCapturedCallback,
                           Consumer<Move> moveCallback) {
        this.state = state;
        this.settings = settings;
        this.kingCapturedCallback = kingCapturedCallback;
        this.moveCallback = moveCallback;

        setLayout(new BorderLayout());

        // Toolbar for board color settings
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton colorSettingsBtn = new JButton("🎨 Board Colors");
        colorSettingsBtn.addActionListener(e -> openColorSettings());
        toolbar.add(colorSettingsBtn);

        add(toolbar, BorderLayout.NORTH);

        grid = new JPanel();
        add(grid, BorderLayout.CENTER);

        reload();
    }

    /** Opens color selection dialog */
    private void openColorSettings() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        BoardColorSettingsDialog dialog = new BoardColorSettingsDialog(parentFrame, settings);

        // ✅ When user applies new settings, immediately update the board
        dialog.setApplyCallback(() -> {
            reload();
            repaint();
        });

        dialog.setVisible(true);
    }

    /** Rebuild the board completely with new theme and colors */
    public void reload() {
        grid.removeAll();
        int size = settings.getSquareSize();
        grid.setLayout(new GridLayout(8, 8));

        // Remember the current piece style in GameState
        state.setCurrentTheme(settings.getPieceStyle());

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                grid.add(new Square(r, c, size));
            }
        }

        grid.revalidate();
        grid.repaint();
        repaint();
    }

    /** Called externally to refresh */
    public void refreshBoard() {
        reload();
    }

    /** Inner class for each square */
    private class Square extends JComponent {
        final int r, c, size;

        Square(int r, int c, int size) {
            this.r = r;
            this.c = c;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);

            // Drag and drop handling
            setTransferHandler(new TransferHandler("text") {
                @Override
                public int getSourceActions(JComponent comp) {
                    return TransferHandler.MOVE;
                }

                @Override
                protected Transferable createTransferable(JComponent comp) {
                    Piece p = state.getPieceAt(r, c);
                    if (p == null || p.getColor() != state.getCurrentPlayer()) return null;
                    return new StringSelection(r + "," + c);
                }

                @Override
                public boolean canImport(TransferSupport support) {
                    return support.isDataFlavorSupported(DataFlavor.stringFlavor);
                }

                @Override
                public boolean importData(TransferSupport support) {
                    if (!canImport(support)) return false;
                    try {
                        String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                        String[] parts = s.split(",");
                        if (parts.length != 2) return false;
                        int sx = Integer.parseInt(parts[0]);
                        int sy = Integer.parseInt(parts[1]);
                        SwingUtilities.invokeLater(() -> doMove(sx, sy, r, c));
                        return true;
                    } catch (UnsupportedFlavorException | IOException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }
            });

            // ✅ Click-to-move logic
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!SwingUtilities.isLeftMouseButton(e)) return;

                    Piece clickedPiece = state.getPieceAt(r, c);

                    if (selectedRow == -1 && clickedPiece != null &&
                            clickedPiece.getColor() == state.getCurrentPlayer()) {
                        selectedRow = r;
                        selectedCol = c;
                        repaint();
                        return;
                    }

                    if (selectedRow == r && selectedCol == c) {
                        clearSelection();
                        return;
                    }

                    if (selectedRow != -1) {
                        doMove(selectedRow, selectedCol, r, c);
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    Piece p = state.getPieceAt(r, c);
                    if (p != null && p.getColor() == state.getCurrentPlayer()) {
                        TransferHandler th = getTransferHandler();
                        if (th != null) th.exportAsDrag(Square.this, e, TransferHandler.MOVE);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            boolean light = ((r + c) % 2 == 0);
            g2.setColor(light ? settings.getLightColor() : settings.getDarkColor());
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (selectedRow == r && selectedCol == c) {
                g2.setColor(new Color(255, 255, 0, 100));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            Piece p = state.getPieceAt(r, c);
            if (p != null) {
                ImageIcon icon = loadIcon(p, state.getCurrentTheme(),
                        settings.getSquareSize() - 8, settings.getSquareSize() - 8);

                if (icon != null) {
                    int x = (getWidth() - icon.getIconWidth()) / 2;
                    int y = (getHeight() - icon.getIconHeight()) / 2;
                    icon.paintIcon(this, g2, x, y);
                }
            }

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
        }
    }

    /** ✅ Loads icons for each style, defaults safely */
    private ImageIcon loadIcon(Piece p, String style, int w, int h) {
        try {
            String basePath;

            // Choose folder depending on selected style
            switch (style == null ? "default" : style.toLowerCase()) {
                case "vibrant":
                    basePath = "/chessgui/pieces/vibrant/";
                    break;
                case "ocean":
                    basePath = "/chessgui/pieces/ocean/";
                    break;
                default:
                    basePath = "/chessgui/pieces/default/";
                    break;
            }

            String fileName = p.getColor().name().toLowerCase() + "_" +
                    p.getType().name().toLowerCase() + ".png";
            URL res = getClass().getResource(basePath + fileName);

            if (res == null && !basePath.contains("default")) {
                res = getClass().getResource("/chessgui/pieces/default/" + fileName);
            }

            if (res == null) {
                System.err.println("⚠️ Missing image: " + basePath + fileName);
                return null;
            }

            ImageIcon ic = new ImageIcon(res);
            Image scaled = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** Perform a move */
    private void doMove(int sx, int sy, int dx, int dy) {
        if (sx < 0 || sy < 0 || dx < 0 || dy < 0) return;

        Piece mover = state.getPieceAt(sx, sy);
        if (mover == null || mover.getColor() != state.getCurrentPlayer()) return;

        Piece target = state.getPieceAt(dx, dy);
        if (target != null && target.getColor() == mover.getColor()) {
            JOptionPane.showMessageDialog(this, "Cannot capture your own piece.");
            clearSelection();
            return;
        }

        state.setPieceAt(dx, dy, mover);
        state.setPieceAt(sx, sy, null);
        Move move = new Move(sx, sy, dx, dy, mover, target, state.getCurrentPlayer());
        state.moves.push(move);
        moveCallback.accept(move);

        if (target != null && target.getType() == PieceType.KING) {
            kingCapturedCallback.accept(mover.getColor());
        }

        state.toggleCurrentPlayer();
        clearSelection();
        reload();
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        repaint();
    }
}
