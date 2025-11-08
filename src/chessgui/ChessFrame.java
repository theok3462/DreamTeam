package chessgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

/**
 * Main application frame: contains board, history, timer, and menu bar.
 */
public class ChessFrame extends JFrame {
    private final GameState state;
    private final SettingsManager settings;
    private final ChessBoardPanel boardPanel;
    private final HistoryPanel historyPanel;
    private final TimerPanel timerPanel;
    private final MenuBar menuBar;

    public ChessFrame() {
        super("Chess Game GUI - Phase 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        state = new GameState();
        settings = new SettingsManager();
        state.initializeClassic();

        // Panels
        boardPanel = new ChessBoardPanel(state, settings, this::onKingCaptured, this::onMoveMade);
        historyPanel = new HistoryPanel(state, boardPanel);
        timerPanel = new TimerPanel(state);

        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);

        JPanel east = new JPanel(new BorderLayout());
        east.add(historyPanel, BorderLayout.CENTER);
        east.setPreferredSize(new Dimension(300, 0));
        add(east, BorderLayout.EAST);

        add(timerPanel, BorderLayout.SOUTH);

        menuBar = new MenuBar(this);
        setJMenuBar(menuBar);

        // Window listener to stop timers
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timerPanel.stopTimers();
            }
        });
    }

    /**
     * Called when a King is captured.
     *
     * @param winner the color of the winner.
     */
    private void onKingCaptured(PieceColor winner) {
        timerPanel.stopTimers();
        JOptionPane.showMessageDialog(this, winner + " wins! King captured.",
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /**
     * Called when a move is made to update history and UI.
     *
     * @param move the Move that was made.
     */
    private void onMoveMade(Move move) {
        historyPanel.addMove(move);
        boardPanel.reload();
    }

    /**
     * Resets the game to the initial setup.
     */
    public void newGame() {
        state.initializeClassic();
        state.moves.clear();
        historyPanel.reset();
        boardPanel.reload();
        timerPanel.reset();
    }

    /**
     * Save game state & settings to file (serialization).
     *
     * @param f target file
     * @throws IOException if failed
     */
    public void saveGame(File f) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(state);
            out.writeObject(settings);
        }
    }

    /**
     * Load game state & settings from file (serialization).
     *
     * @param f source file
     * @throws IOException            if IO error
     * @throws ClassNotFoundException if class mismatch
     */
    public void loadGame(File f) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            GameState s = (GameState) in.readObject();
            SettingsManager sm = (SettingsManager) in.readObject();
            // replace internal state
            state.copyFrom(s);
            settings.copyFrom(sm);
            historyPanel.reloadFromState();
            boardPanel.reload();
            timerPanel.reset();
        }
    }

    // ✅ renamed to avoid clash with java.awt.Frame#getState()
    public GameState getGameState() {
        return state;
    }

    public SettingsManager getSettings() {
        return settings;
    }

    public ChessBoardPanel getBoardPanel() {
        return boardPanel;
    }

    public HistoryPanel getHistoryPanel() {
        return historyPanel;
    }
}
