package chessgui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

/**
 * Main application window for the Chess GUI.
 */
public class ChessFrame extends JFrame {

    private GameState state;
    private SettingsManager settings;

    private ChessBoardPanel boardPanel;
    private HistoryPanel historyPanel;
    private TimerPanel timerPanel;

    public ChessFrame() {
        super("Chess Game");

        this.state = new GameState();
        this.settings = new SettingsManager();

        // Keep theme in sync with settings
        state.setCurrentTheme(settings.getPieceTheme());

        this.timerPanel = new TimerPanel(state);
        this.historyPanel = new HistoryPanel(state, settings, this::undoLastMove);
        this.boardPanel = new ChessBoardPanel(state, settings, historyPanel);

        // Let the board notify us on game over so we can offer
        // "new game / exit" using the full reset logic.
        this.boardPanel.setGameOverHandler(this::handleGameOverFromBoard);

        setLayout(new BorderLayout(5, 5));
        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5));

        boardPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        historyPanel.setBorder(
                BorderFactory.createTitledBorder("Game Info"));

        add(boardPanel, BorderLayout.CENTER);
        add(historyPanel, BorderLayout.EAST);
        add(timerPanel, BorderLayout.SOUTH);

        setJMenuBar(createMenuBar());

        // Apply dark / light mode colors initially
        applyDarkModeToUI();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int res = JOptionPane.showConfirmDialog(
                        ChessFrame.this,
                        "Exit the game?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );
                if (res == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveItem    = new JMenuItem("Save Game...");
        JMenuItem loadItem    = new JMenuItem("Load Game...");
        JMenuItem exitItem    = new JMenuItem("Exit");

        newGameItem.addActionListener(e -> newGame());
        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> loadGame());
        exitItem.addActionListener(e ->
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        gameMenu.add(newGameItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo Move");
        undoItem.addActionListener(e -> undoLastMove());
        editMenu.add(undoItem);

        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem boardSettingsItem = new JMenuItem("Board & Theme...");
        boardSettingsItem.addActionListener(e -> {
            BoardColorSettingsDialog dlg =
                    new BoardColorSettingsDialog(this, settings);
            dlg.setApplyCallback(this::applySettingsChanged);
            dlg.setVisible(true);
        });
        settingsMenu.add(boardSettingsItem);

        bar.add(gameMenu);
        bar.add(editMenu);
        bar.add(settingsMenu);

        return bar;
    }

    // ---- Game control ----

    private void newGame() {
        int res = JOptionPane.showConfirmDialog(
                this,
                "Start a new game?",
                "New Game",
                JOptionPane.YES_NO_OPTION
        );
        if (res != JOptionPane.YES_OPTION) return;

        state.reset();
        // keep theme consistent after reset
        state.setCurrentTheme(settings.getPieceTheme());

        historyPanel.reloadFromState();
        boardPanel.clearSelection();
        boardPanel.reload();
        timerPanel.reset();
    }

    /** Called by ChessBoardPanel when a checkmate / stalemate happens. */
    private void handleGameOverFromBoard() {
        if (!state.isGameOver()) return;

        String msg;
        if (state.getWinner() == null) {
            // Stalemate or other draw
            msg = "Game over: Draw by stalemate.\n"
                    + "Result: no winner (½–½).\n"
                    + "Start a new game or exit?";
        } else {
            String winnerName = (state.getWinner() == PieceColor.WHITE) ? "White" : "Black";
            msg = "Game over: " + winnerName + " wins by checkmate.\n"
                    + "Winner: " + winnerName + ".\n"
                    + "Start a new game or exit?";
        }

        Object[] options = {"New Game", "Exit", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                msg,
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {          // New Game
            newGame();
        } else if (choice == 1) {   // Exit
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        // Cancel -> do nothing, board remains in final position.
    }

    /**
     * Public helper so board (or other UI) can trigger full "new game" behaviour
     * including timers and history.
     */
    public void requestNewGame() {
        newGame();
    }

    public void undoLastMove() {
        if (state.undoLastMove()) {
            historyPanel.reloadFromState();
            boardPanel.clearSelection();
            boardPanel.reload();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No move to undo.",
                    "Undo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // ---- Save / Load ----

    private void saveGame() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(state);
            out.writeObject(settings);
            JOptionPane.showMessageDialog(
                    this,
                    "Game saved.",
                    "Save",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error saving game: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadGame() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {
            Object sObj = in.readObject();
            Object setObj = in.readObject();

            if (sObj instanceof GameState && setObj instanceof SettingsManager) {
                this.state = (GameState) sObj;
                this.settings.copyFrom((SettingsManager) setObj);

                // keep theme in sync
                state.setCurrentTheme(settings.getPieceTheme());

                this.timerPanel = new TimerPanel(state);
                this.historyPanel.setStateAndSettings(state, settings);
                this.boardPanel.setStateAndSettings(state, settings);

                historyPanel.reloadFromState();
                boardPanel.clearSelection();
                boardPanel.reload();
                timerPanel.reset();

                getContentPane().removeAll();
                add(boardPanel, BorderLayout.CENTER);
                add(historyPanel, BorderLayout.EAST);
                add(timerPanel, BorderLayout.SOUTH);

                // Re-apply dark / light mode after loading
                applyDarkModeToUI();

                revalidate();
                repaint();

                JOptionPane.showMessageDialog(
                        this,
                        "Game loaded.",
                        "Load",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                throw new IOException("Invalid save file format.");
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading game: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---- Settings integration ----

    /**
     * Called after the settings dialog hits "Apply".
     * We avoid weird shrinking when the window was maximised:
     * - if normal: repack (so new piece size fits)
     * - if maximised: just revalidate + repaint.
     *
     * Also reapplies dark / light mode.
     */
    public void applySettingsChanged() {
        state.setCurrentTheme(settings.getPieceTheme());
        boardPanel.reload();
        historyPanel.reloadFromState();

        // Apply dark / light mode to all UI parts
        applyDarkModeToUI();

        int stateFlags = getExtendedState();
        if (stateFlags == Frame.NORMAL) {
            pack(); // allow window size to adjust when not maximised
        } else {
            revalidate();
            repaint();
        }
    }

    /**
     * Apply dark-mode vs light-mode colors to the top-level frame and
     * main panels, based on settings.isDarkMode().
     */
    private void applyDarkModeToUI() {
        boolean dark = settings.isDarkMode();

        Color panelBg;
        Color textFg;

        if (dark) {
            panelBg = new Color(25, 25, 25);
            textFg = Color.WHITE;
        } else {
            Color defaultBg = UIManager.getColor("Panel.background");
            if (defaultBg == null) defaultBg = Color.LIGHT_GRAY;
            panelBg = defaultBg;
            textFg = Color.BLACK;
        }

        getContentPane().setBackground(panelBg);

        if (historyPanel != null) {
            historyPanel.setBorder(
                    BorderFactory.createTitledBorder(
                            null,
                            "Game Info",
                            TitledBorder.LEADING,
                            TitledBorder.TOP,
                            null,
                            textFg
                    )
            );
        }

        applyDarkModeRecursive(getContentPane(), dark, panelBg, textFg);

        JMenuBar mb = getJMenuBar();
        if (mb != null) {
            mb.setBackground(panelBg);
            mb.setForeground(textFg);
            for (int i = 0; i < mb.getMenuCount(); i++) {
                JMenu m = mb.getMenu(i);
                if (m != null) {
                    m.setBackground(panelBg);
                    m.setForeground(textFg);
                    for (int j = 0; j < m.getItemCount(); j++) {
                        JMenuItem item = m.getItem(j);
                        if (item != null) {
                            item.setBackground(panelBg);
                            item.setForeground(textFg);
                        }
                    }
                }
            }
        }

        repaint();
    }

    private void applyDarkModeRecursive(Component c,
                                        boolean dark,
                                        Color panelBg,
                                        Color textFg) {

        c.setForeground(textFg);

        if (c instanceof JScrollPane sp) {
            sp.getViewport().setBackground(panelBg);
            sp.setBackground(panelBg);
        } else if (c instanceof JPanel) {
            c.setBackground(panelBg);
        }

        if (c instanceof JTextArea ta) {
            if (dark) {
                ta.setBackground(new Color(30, 30, 30));
            } else {
                ta.setBackground(Color.WHITE);
            }
            ta.setForeground(textFg);
        } else if (c instanceof JButton btn) {
            Color defaultBtnBg =
                    UIManager.getColor("Button.background");
            if (defaultBtnBg == null) defaultBtnBg = new Color(240, 240, 240);

            btn.setBackground(dark ? new Color(60, 60, 60) : defaultBtnBg);
            btn.setForeground(textFg);
        }

        if (c instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                applyDarkModeRecursive(child, dark, panelBg, textFg);
            }
        }
    }

    // ---- Accessors ----

    public SettingsManager getSettings() {
        return settings;
    }

    public GameState getGameState() {
        return state;
    }

    public ChessBoardPanel getBoardPanel() {
        return boardPanel;
    }

    public HistoryPanel getHistoryPanel() {
        return historyPanel;
    }
}
