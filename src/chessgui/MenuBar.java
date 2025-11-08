package chessgui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Menu bar with New, Save, Load, Settings and Exit.
 */
public class MenuBar extends JMenuBar {
    public MenuBar(ChessFrame frame) {
        JMenu game = new JMenu("Game");

        JMenuItem newItem = new JMenuItem(new AbstractAction("New Game") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.newGame();
            }
        });

        JMenuItem saveItem = new JMenuItem(new AbstractAction("Save Game") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    try {
                        frame.saveGame(f);
                        JOptionPane.showMessageDialog(frame, "Saved successfully.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Save failed: " + ex.getMessage());
                    }
                }
            }
        });

        JMenuItem loadItem = new JMenuItem(new AbstractAction("Load Game") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    try {
                        frame.loadGame(f);
                        JOptionPane.showMessageDialog(frame, "Loaded successfully.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Load failed: " + ex.getMessage());
                    }
                }
            }
        });

        JMenuItem settingsItem = new JMenuItem(new AbstractAction("Settings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsWindow w = new SettingsWindow(frame, frame.getSettings());
                w.setVisible(true);
            }
        });

        JMenuItem exitItem = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        game.add(newItem);
        game.add(saveItem);
        game.add(loadItem);
        game.addSeparator();
        game.add(settingsItem);
        game.addSeparator();
        game.add(exitItem);

        add(game);
    }
}
