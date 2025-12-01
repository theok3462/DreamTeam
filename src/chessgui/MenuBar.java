package chessgui;

import javax.swing.*;
import java.awt.*;

/**
 * Main menu bar for the Chess GUI.
 *
 * - Game → New Game, Exit
 * - Help → About
 *
 * It resets GameState, clears history, refreshes the board and timer.
 */
public class MenuBar extends JMenuBar {

    private final GameState state;
    private final ChessBoardPanel boardPanel;
    private final HistoryPanel historyPanel;
    private final TimerPanel timerPanel;
    private final JFrame owner;

    public MenuBar(GameState state,
                   ChessBoardPanel boardPanel,
                   HistoryPanel historyPanel,
                   TimerPanel timerPanel,
                   JFrame owner) {
        this.state = state;
        this.boardPanel = boardPanel;
        this.historyPanel = historyPanel;
        this.timerPanel = timerPanel;
        this.owner = owner;

        buildMenus();
    }

    private void buildMenus() {
        add(createGameMenu());
        add(createHelpMenu());
    }

    private JMenu createGameMenu() {
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> newGame());
        gameMenu.add(newGameItem);

        gameMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);

        return gameMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    private void newGame() {
        int result = JOptionPane.showConfirmDialog(
                owner,
                "Start a new game?\nCurrent game will be lost.",
                "New Game",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            state.reset();
            if (historyPanel != null) {
                historyPanel.clear();
            }
            if (boardPanel != null) {
                boardPanel.clearSelection();
                boardPanel.reload();
            }
            if (timerPanel != null) {
                timerPanel.reset();
            }
        }
    }

    private void showAboutDialog() {
        String msg = """
                Chess Game GUI (Phase 3)
                
                - Two-player chess with full move validation
                - Check / checkmate detection
                - Move history + undo
                - Player timers
                
                Developed as part of the course project.
                """;
        JOptionPane.showMessageDialog(
                owner,
                msg,
                "About",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
