package org.example.checkers;

import java.util.Objects;

public record Position(int row, int col) {
    public Position {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Position must be inside 8x8 board");
        }
    }

    public Position offset(int dr, int dc) {
        return new Position(row + dr, col + dc);
    }
}
