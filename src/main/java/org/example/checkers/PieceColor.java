package org.example.checkers;

public enum PieceColor {
    WHITE(-1),
    BLACK(1);

    private final int forward;

    PieceColor(int forward) {
        this.forward = forward;
    }

    public int forward() {
        return forward;
    }

    public PieceColor opponent() {
        return this == WHITE ? BLACK : WHITE;
    }
}
