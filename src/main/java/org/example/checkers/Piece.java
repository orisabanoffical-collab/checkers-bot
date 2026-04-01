package org.example.checkers;

public class Piece {
    private final PieceColor color;
    private boolean king;

    public Piece(PieceColor color, boolean king) {
        this.color = color;
        this.king = king;
    }

    public PieceColor getColor() {
        return color;
    }

    public boolean isKing() {
        return king;
    }

    public void promote() {
        king = true;
    }

    public Piece copy() {
        return new Piece(color, king);
    }
}
