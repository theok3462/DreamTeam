package chessgui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.List;

/**
 * Side panel that shows captured pieces and move history.
 */
public class HistoryPanel extends JPanel {

    private GameState state;
    private SettingsManager settings;
    private final Runnable undoCallback;

    private final JPanel capturedWhitePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
    private final JPanel capturedBlackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
    private final JTextArea historyArea = new JTextArea(20, 22);
    private final JLabel statusLabel = new JLabel(" ");

    public HistoryPanel(GameState state,
                        SettingsManager settings,
                        Runnable undoCallback) {
        this.state = state;
        this.settings = settings;
        this.undoCallback = undoCallback;
        buildUI();
        reloadFromState();
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(260, 500));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        capturedWhitePanel.setOpaque(false);
        capturedBlackPanel.setOpaque(false);

        JPanel whiteRow = new JPanel(new BorderLayout());
        whiteRow.setOpaque(false);
        whiteRow.add(new JLabel("Captured White:"), BorderLayout.NORTH);
        whiteRow.add(capturedWhitePanel, BorderLayout.CENTER);

        JPanel blackRow = new JPanel(new BorderLayout());
        blackRow.setOpaque(false);
        blackRow.add(new JLabel("Captured Black:"), BorderLayout.NORTH);
        blackRow.add(capturedBlackPanel, BorderLayout.CENTER);

        infoPanel.add(whiteRow);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(blackRow);
        infoPanel.add(Box.createVerticalStrut(4));

        statusLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        infoPanel.add(statusLabel);

        add(infoPanel, BorderLayout.NORTH);

        historyArea.setEditable(false);
        historyArea.setLineWrap(false);
        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Move History"));
        add(scroll, BorderLayout.CENTER);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (undoCallback != null) {
                undoCallback.run();
            }
        });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(undoButton);
        add(south, BorderLayout.SOUTH);
    }

    public void setStateAndSettings(GameState state, SettingsManager settings) {
        this.state = state;
        this.settings = settings;
        reloadFromState();
    }

    /**
     * Backwards-compatible clear method so older code (MenuBar, etc.)
     * that calls historyPanel.clear() still compiles.
     * This just wipes the UI; New Game typically calls reloadFromState()
     * right after resetting GameState.
     */
    public void clear() {
        capturedWhitePanel.removeAll();
        capturedBlackPanel.removeAll();
        historyArea.setText("");
        statusLabel.setText(" ");
        capturedWhitePanel.revalidate();
        capturedWhitePanel.repaint();
        capturedBlackPanel.revalidate();
        capturedBlackPanel.repaint();
        repaint();
    }

    /** Rebuilds captured pieces and move list from the GameState. */
    public void reloadFromState() {
        capturedWhitePanel.removeAll();
        capturedBlackPanel.removeAll();
        historyArea.setText("");

        List<Move> moves = state.getHistory();
        int moveNumber = 1;
        for (Move m : moves) {
            // Captured pieces
            if (m.captured != null) {
                if (m.captured.getColor() == PieceColor.WHITE) {
                    capturedWhitePanel.add(new JLabel(getPieceIcon(m.captured)));
                } else {
                    capturedBlackPanel.add(new JLabel(getPieceIcon(m.captured)));
                }
            }

            String line = String.format(
                    "%2d. %5s: %s%n",
                    moveNumber++,
                    m.player,
                    m.toString()
            );
            historyArea.append(line);
        }

        PieceColor current = state.getCurrentPlayer();
        if (state.isGameOver()) {
            PieceColor winner = state.getWinner();
            if (winner == null) {
                statusLabel.setText("Game over: stalemate (draw).");
            } else {
                statusLabel.setText("Game over: " + winner + " wins.");
            }
        } else if (state.isCheck(current)) {
            statusLabel.setText(current + " is in check.");
        } else {
            statusLabel.setText(current + " to move.");
        }

        capturedWhitePanel.revalidate();
        capturedWhitePanel.repaint();
        capturedBlackPanel.revalidate();
        capturedBlackPanel.repaint();
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    // Loads a small icon for a captured piece using the current theme.
    private Icon getPieceIcon(Piece p) {
        if (p == null) return null;
        String theme = settings.getPieceTheme();
        if (theme == null || theme.isEmpty()) {
            theme = "default";
        }
        String color = (p.getColor() == PieceColor.WHITE) ? "white" : "black";
        String type = p.getType().name().toLowerCase();
        String path = "/chessgui/pieces/" + theme + "/" + color + "_" + type + ".png";
        URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Captured piece image not found: " + path);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
