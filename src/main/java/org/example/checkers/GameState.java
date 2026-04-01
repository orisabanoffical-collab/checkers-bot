package org.example.checkers;

public class GameState {
    private final Piece[][] board = new Piece[8][8];
    private PieceColor currentPlayer = PieceColor.WHITE;

    public GameState() {
        initializeBoard();
    }

    public void initializeBoard() {
        clearBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (!isDarkSquare(row, col)) {
                    continue;
                }
                if (row < 3) {
                    board[row][col] = new Piece(PieceColor.BLACK, false);
                } else if (row > 4) {
                    board[row][col] = new Piece(PieceColor.WHITE, false);
                }
            }
        }
        currentPlayer = PieceColor.WHITE;
    }

    public void clearBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
    }

    public static boolean isDarkSquare(int row, int col) {
        return (row + col) % 2 != 0;
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public Piece getPiece(int row, int col) {
        return inBounds(row, col) ? board[row][col] : null;
    }

    public Piece getPiece(Position p) {
        return getPiece(p.row(), p.col());
    }

    public void setPiece(int row, int col, Piece piece) {
        if (!inBounds(row, col)) {
            throw new IllegalArgumentException("Out of board");
        }
        board[row][col] = piece;
    }

    public void setPiece(Position p, Piece piece) {
        setPiece(p.row(), p.col(), piece);
    }

    public PieceColor getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PieceColor player) {
        this.currentPlayer = player;
    }

    public void switchTurn() {
        currentPlayer = currentPlayer.opponent();
    }

    public int countPieces(PieceColor color) {
        int count = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getColor() == color) {
                    count++;
                }
            }
        }
        return count;
    }

    public GameState copy() {
        GameState copy = new GameState();
        copy.clearBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != null) {
                    copy.board[r][c] = board[r][c].copy();
                }
            }
        }
        copy.currentPlayer = currentPlayer;
        return copy;
    }
}
