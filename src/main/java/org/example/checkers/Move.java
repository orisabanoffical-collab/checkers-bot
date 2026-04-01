package org.example.checkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Move {
    private final List<Position> path;
    private final List<Position> captured;

    public Move(List<Position> path, List<Position> captured) {
        if (path == null || path.size() < 2) {
            throw new IllegalArgumentException("Move path must contain at least from and to");
        }
        this.path = List.copyOf(path);
        this.captured = List.copyOf(captured);
    }

    public Position from() {
        return path.get(0);
    }

    public Position to() {
        return path.get(path.size() - 1);
    }

    public List<Position> getPath() {
        return Collections.unmodifiableList(path);
    }

    public List<Position> getCaptured() {
        return Collections.unmodifiableList(captured);
    }

    public boolean isCapture() {
        return !captured.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move other)) {
            return false;
        }
        return path.equals(other.path) && captured.equals(other.captured);
    }

    @Override
    public int hashCode() {
        return 31 * path.hashCode() + captured.hashCode();
    }
}
