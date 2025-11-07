package chess.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ChessGUI extends JFrame {

    private JButton[][] buttons = new JButton[8][8];
    private String[][] board = new String[8][8];

    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean darkMode = false;

    public ChessGUI() {
        try { 
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
        } catch (Exception e) {}

        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createMenuBar();
        initBoard();
        add(createBoardPanel(), BorderLayout.CENTER);

        setSize(600, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveItem = new JMenuItem("Save Game");
        JMenuItem loadItem = new JMenuItem("Load Game");

        newGameItem.addActionListener(e -> {
            initBoard();
            refreshBoard();
        });

        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> {
            loadGame();
            refreshBoard();
        });

        gameMenu.add(newGameItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem switchColors = new JMenuItem("Switch Board Colors");
        switchColors.addActionListener(e -> {
            darkMode = !darkMode;
            refreshBoard();
        });
        viewMenu.add(switchColors);

        bar.add(gameMenu);
        bar.add(viewMenu);

        setJMenuBar(bar);
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 8));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton b = new JButton();
                b.setFocusPainted(false);
                b.setMargin(new Insets(0, 0, 0, 0));

                final int row = r;
                final int col = c;
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleClick(row, col);
                    }
                });

                buttons[r][c] = b;
                panel.add(b);
            }
        }

        refreshBoard();
        return panel;
    }

    private void initBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = "";
            }
        }

        for (int c = 0; c < 8; c++) {
            board[1][c] = "p";
            board[6][c] = "P";
        }

        board[0][0] = "r"; board[0][7] = "r";
        board[0][1] = "n"; board[0][6] = "n";
        board[0][2] = "b"; board[0][5] = "b";
        board[0][3] = "q"; board[0][4] = "k";

        board[7][0] = "R"; board[7][7] = "R";
        board[7][1] = "N"; board[7][6] = "N";
        board[7][2] = "B"; board[7][5] = "B";
        board[7][3] = "Q"; board[7][4] = "K";

        selectedRow = -1;
        selectedCol = -1;
    }

    private void refreshBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton b = buttons[r][c];

                boolean light = (r + c) % 2 == 0;
                if (!darkMode) {
                    b.setBackground(light ? new Color(235, 235, 208) : new Color(119, 148, 85));
                } else {
                    b.setBackground(light ? new Color(80, 80, 80) : new Color(160, 160, 160));
                }

                String v = board[r][c];
                b.setText(v);
            }
        }
    }

    private void handleClick(int row, int col) {
        if (selectedRow == -1) {
            if (!board[row][col].equals("")) {
                selectedRow = row;
                selectedCol = col;
            }
        } else {
            String fromVal = board[selectedRow][selectedCol];
            String toVal = board[row][col];

            if (!fromVal.equals("")) {
                if (toVal.equals("K") || toVal.equals("k")) {
                    String winner = toVal.equals("K") ? "Black" : "White";
                    board[row][col] = fromVal;
                    board[selectedRow][selectedCol] = "";
                    refreshBoard();
                    JOptionPane.showMessageDialog(this, winner + " wins (king captured)");
                    System.exit(0);
                } else {
                    board[row][col] = fromVal;
                    board[selectedRow][selectedCol] = "";
                }
            }

            selectedRow = -1;
            selectedCol = -1;
            refreshBoard();
        }
    }

    private void saveGame() {
        try (PrintWriter out = new PrintWriter(new FileWriter("chess_save.txt"))) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    String v = board[r][c];
                    if (v.equals("")) v = ".";
                    out.print(v);
                    if (c < 7) out.print(" ");
                }
                out.println();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Save failed");
        }
    }

    private void loadGame() {
        try (BufferedReader br = new BufferedReader(new FileReader("chess_save.txt"))) {
            for (int r = 0; r < 8; r++) {
                String line = br.readLine();
                if (line == null) throw new IOException();
                String[] parts = line.split(" ");
                if (parts.length != 8) throw new IOException();
                for (int c = 0; c < 8; c++) {
                    String v = parts[c];
                    if (v.equals(".")) v = "";
                    board[r][c] = v;
                }
            }
            selectedRow = -1;
            selectedCol = -1;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Load failed");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessGUI());
    }
}
