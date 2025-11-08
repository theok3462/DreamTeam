package chessgui;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * A panel that displays move history, captured pieces, and provides an Undo feature.
 * <p>
 * Each move is appended to a text area, and captured pieces are displayed
 * as icons (based on the current theme).
 */
public class HistoryPanel extends JPanel {
    private final GameState state;
    private final ChessBoardPanel boardPanel;
    private final JTextArea historyArea = new JTextArea();
    private final JPanel capturedWhitePanel = new JPanel(new GridLayout(0, 1));
    private final JPanel capturedBlackPanel = new JPanel(new GridLayout(0, 1));

    /**
     * Creates a new HistoryPanel associated with a game state and board panel.
     *
     * @param state      the current {@link GameState}
     * @param boardPanel the {@link ChessBoardPanel} for board updates
     */
    public HistoryPanel(GameState state, ChessBoardPanel boardPanel) {
        this.state = state;
        this.boardPanel = boardPanel;
        setLayout(new BorderLayout());

        historyArea.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(280, 300));
        add(historyScroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(3, 1));
        JPanel topControls = new JPanel();
        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> undo());
        topControls.add(undo);
        bottom.add(topControls);

        // captured panels
        JPanel capturedContainer = new JPanel(new GridLayout(1, 2));
        JScrollPane cw = new JScrollPane(capturedWhitePanel);
        JScrollPane cb = new JScrollPane(capturedBlackPanel);
        capturedContainer.add(makeTitledPanel("Captured White (by Black)", cw));
        capturedContainer.add(makeTitledPanel("Captured Black (by White)", cb));
        bottom.add(capturedContainer);

        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Creates a titled subpanel for captured pieces.
     *
     * @param title panel title
     * @param comp  the component to display inside
     * @return the titled panel
     */
    private JPanel makeTitledPanel(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    /**
     * Adds a move entry to the history and updates captured pieces if any.
     *
     * @param m the move to add
     */
    public void addMove(Move m) {
        historyArea.append(m.toString() + "\n");
        if (m.captured != null) {
            addCaptured(m.captured, m.player == PieceColor.WHITE ? capturedBlackPanel : capturedWhitePanel);
        }
    }

    /**
     * Adds a captured piece to the appropriate captured panel with icon or text fallback.
     *
     * @param p      the captured piece
     * @param panel  the panel to add it to
     */
    private void addCaptured(Piece p, JPanel panel) {
        JLabel lbl = new JLabel();

        try {
            String theme = state.getCurrentTheme();
            if (theme == null || theme.isBlank()) theme = "default";

            String basePath = "/chessgui/pieces/" + theme.toLowerCase() + "/";
            String fileName = p.getColor().name().toLowerCase() + "_" + p.getType().name().toLowerCase() + ".png";
            java.net.URL res = getClass().getResource(basePath + fileName);

            if (res == null && !theme.equalsIgnoreCase("default")) {
                res = getClass().getResource("/chessgui/pieces/default/" + fileName);
            }

            if (res != null) {
                ImageIcon ic = new ImageIcon(res);
                Image img = ic.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(img));
            } else {
                lbl.setText(p.getColor() + " " + p.getType());
                System.err.println("⚠️ Missing captured piece image: " + basePath + fileName);
            }
        } catch (Exception ex) {
            lbl.setText(p.getColor() + " " + p.getType());
            ex.printStackTrace();
        }

        panel.add(lbl);
        revalidate();
        repaint();
    }

    /** Resets the move history and captured pieces list (used for new games). */
    public void reset() {
        historyArea.setText("");
        capturedWhitePanel.removeAll();
        capturedBlackPanel.removeAll();
        revalidate();
        repaint();
    }

    /** Rebuilds move history and captured lists from the current game state. */
    public void reloadFromState() {
        reset();
        for (Enumeration<Move> e = state.moves.elements(); e.hasMoreElements();) {
            Move m = e.nextElement();
            historyArea.append(m.toString() + "\n");
            if (m.captured != null) {
                addCaptured(m.captured, m.player == PieceColor.WHITE ? capturedBlackPanel : capturedWhitePanel);
            }
        }
    }

    /** Undoes the last move, restoring the board and updating history accordingly. */
    private void undo() {
        if (state.moves.isEmpty()) return;
        Move last = state.moves.pop();
        state.setPieceAt(last.sx, last.sy, last.moved);
        state.setPieceAt(last.dx, last.dy, last.captured);
        state.toggleCurrentPlayer();

        String text = historyArea.getText();
        int lastLineIndex = text.lastIndexOf("\n", text.length() - 2);
        if (lastLineIndex >= 0) historyArea.setText(text.substring(0, lastLineIndex + 1));
        else historyArea.setText("");

        if (last.captured != null) {
            if (last.player == PieceColor.WHITE) {
                if (capturedBlackPanel.getComponentCount() > 0)
                    capturedBlackPanel.remove(capturedBlackPanel.getComponentCount() - 1);
            } else {
                if (capturedWhitePanel.getComponentCount() > 0)
                    capturedWhitePanel.remove(capturedWhitePanel.getComponentCount() - 1);
            }
        }

        revalidate();
        repaint();
        boardPanel.reload();
    }
}
