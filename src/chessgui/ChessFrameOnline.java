package chessgui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ChessFrameOnline extends JFrame {

    private final NetConnection connection;
    private final PieceColor localColor;

    private GameState state;
    private SettingsManager settings;

    private ChessBoardPanel boardPanel;
    private HistoryPanel historyPanel;
    private TimerPanel timerPanel;

    public ChessFrameOnline(NetConnection connection, PieceColor localColor) {
        super("Chess Game - Online");

        this.connection = connection;
        this.localColor = localColor;

        this.state = new GameState();
        this.settings = new SettingsManager();

        state.setCurrentTheme(settings.getPieceTheme());

        this.timerPanel = new TimerPanel(state);
        this.historyPanel = new HistoryPanel(state, settings, this::undoLastMove);
        this.boardPanel = new ChessBoardPanel(state, settings, historyPanel);

        this.boardPanel.setGameOverHandler(this::handleGameOverFromBoard);
        this.boardPanel.setAfterHumanMove(this::handleAfterHumanMove);

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

        applyDarkModeToUI();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int res = JOptionPane.showConfirmDialog(
                        ChessFrameOnline.this,
                        "Exit the game?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );
                if (res == JOptionPane.YES_OPTION) {
                    closeConnection();
                    dispose();
                }
            }
        });

        startReceiverThread();
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(e ->
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

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

    private void startReceiverThread() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    int[] mv = connection.readMove();
                    if (mv == null) break;
                    SwingUtilities.invokeLater(() -> applyRemoteMove(mv));
                }
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            ChessFrameOnline.this,
                            "Connection lost: " + ex.getMessage(),
                            "Network",
                            JOptionPane.ERROR_MESSAGE
                    );
                    closeConnection();
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void applyRemoteMove(int[] mv) {
        if (mv == null || mv.length != 4) return;
        boolean ok = state.makeMove(mv[0], mv[1], mv[2], mv[3]);
        if (ok) {
            historyPanel.reloadFromState();
            boardPanel.clearSelection();
            boardPanel.reload();
            timerPanel.repaint();
            if (state.isGameOver()) {
                handleGameOverFromBoard();
            }
        }
    }

    private void handleAfterHumanMove() {
        if (state.isGameOver()) return;

        if (state.getHistory().isEmpty()) return;
        Move last = state.getHistory().get(state.getHistory().size() - 1);

        Piece moved = state.getPiece(last.dx, last.dy);
        if (moved == null || moved.getColor() != localColor) {
            return;
        }

        try {
            connection.sendMove(last);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to send move: " + ex.getMessage(),
                    "Network",
                    JOptionPane.ERROR_MESSAGE
            );
            closeConnection();
        }
    }

    private void closeConnection() {
        connection.close();
    }

    private void newGame() {
        int res = JOptionPane.showConfirmDialog(
                this,
                "Start a new game?",
                "New Game",
                JOptionPane.YES_NO_OPTION
        );
        if (res != JOptionPane.YES_OPTION) return;

        state.reset();
        state.setCurrentTheme(settings.getPieceTheme());

        historyPanel.reloadFromState();
        boardPanel.clearSelection();
        boardPanel.reload();
        timerPanel.reset();
    }

    private void handleGameOverFromBoard() {
        if (!state.isGameOver()) return;

        String msg;
        if (state.getWinner() == null) {
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

        if (choice == 0) {
            newGame();
        } else if (choice == 1) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
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

    public void applySettingsChanged() {
        state.setCurrentTheme(settings.getPieceTheme());
        boardPanel.reload();
        historyPanel.reloadFromState();

        applyDarkModeToUI();

        int stateFlags = getExtendedState();
        if (stateFlags == Frame.NORMAL) {
            pack();
        } else {
            revalidate();
            repaint();
        }
    }

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
}
