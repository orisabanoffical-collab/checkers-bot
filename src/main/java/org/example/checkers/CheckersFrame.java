package org.example.checkers;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CheckersFrame extends JFrame {
    private final GameState state = new GameState();
    private final GameEngine engine = new GameEngine();
    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final HeuristicBot bot = new HeuristicBot(PieceColor.BLACK);

    private final JButton[][] cells = new JButton[8][8];
    private final JLabel status = new JLabel("Your turn (WHITE)", SwingConstants.CENTER);

    private Position selected;
    private List<Move> selectedMoves = List.of();

    public CheckersFrame() {
        setTitle("Checkers Bot");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(720, 760);
        setLocationRelativeTo(null);

        status.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        add(status, BorderLayout.NORTH);

        JPanel board = new JPanel(new GridLayout(8, 8));
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton b = new JButton();
                b.setFocusPainted(false);
                b.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
                int row = r;
                int col = c;
                b.addActionListener(e -> onClick(row, col));
                cells[r][c] = b;
                board.add(b);
            }
        }

        add(board, BorderLayout.CENTER);
        repaintBoard();
    }

    private void onClick(int row, int col) {
        if (state.getCurrentPlayer() != PieceColor.WHITE || engine.isGameOver(state)) {
            return;
        }
        if (!GameState.isDarkSquare(row, col)) {
            status.setText("Only dark squares are playable.");
            return;
        }

        Position pos = new Position(row, col);
        Piece piece = state.getPiece(pos);

        if (selected == null) {
            if (piece != null && piece.getColor() == PieceColor.WHITE) {
                List<Move> moves = moveGenerator.generateMovesFrom(state, pos, PieceColor.WHITE);
                if (!moves.isEmpty()) {
                    selected = pos;
                    selectedMoves = moves;
                    status.setText("Selected (" + row + ", " + col + ")");
                } else {
                    status.setText("This piece has no legal moves.");
                }
            } else {
                status.setText("Select a WHITE piece.");
            }
            repaintBoard();
            return;
        }

        Move chosen = null;
        for (Move move : selectedMoves) {
            if (move.to().equals(pos)) {
                chosen = move;
                break;
            }
        }

        if (chosen == null) {
            selected = null;
            selectedMoves = List.of();
            status.setText("Invalid destination.");
            repaintBoard();
            return;
        }

        if (!engine.applyMove(state, chosen)) {
            status.setText("Move rejected by rules.");
            selected = null;
            selectedMoves = List.of();
            repaintBoard();
            return;
        }

        selected = null;
        selectedMoves = List.of();
        repaintBoard();
        afterHumanMove();
    }

    private void afterHumanMove() {
        if (engine.isGameOver(state)) {
            showWinner();
            return;
        }

        status.setText("Bot thinking...");
        SwingUtilities.invokeLater(() -> {
            Move botMove = bot.chooseMove(state);
            if (botMove != null) {
                engine.applyMove(state, botMove);
            }
            repaintBoard();
            if (engine.isGameOver(state)) {
                showWinner();
            } else {
                status.setText("Your turn (WHITE)");
            }
        });
    }

    private void showWinner() {
        PieceColor winner = engine.winner(state);
        if (winner == null) {
            status.setText("Draw");
            JOptionPane.showMessageDialog(this, "Draw!");
            return;
        }
        status.setText("Winner: " + winner);
        JOptionPane.showMessageDialog(this, "Winner: " + winner);
    }

    private void repaintBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton b = cells[r][c];
                b.setBackground(GameState.isDarkSquare(r, c)
                        ? new Color(120, 72, 32)
                        : new Color(240, 210, 170));
                b.setForeground(Color.BLACK);

                Piece p = state.getPiece(r, c);
                if (p == null) {
                    b.setText("");
                } else if (p.getColor() == PieceColor.WHITE) {
                    b.setText(p.isKing() ? "WK" : "W");
                } else {
                    b.setText(p.isKing() ? "BK" : "B");
                }

                if (selected != null && selected.row() == r && selected.col() == c) {
                    b.setBackground(Color.YELLOW);
                }
                for (Move move : selectedMoves) {
                    if (move.to().row() == r && move.to().col() == c) {
                        b.setBackground(Color.GREEN);
                    }
                }
            }
        }
    }
}
