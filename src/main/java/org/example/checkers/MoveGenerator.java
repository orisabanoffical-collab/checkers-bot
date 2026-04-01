package org.example.checkers;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    public List<Move> generateLegalMoves(GameState state, PieceColor player) {
        List<Move> captures = new ArrayList<>();
        List<Move> quietMoves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = state.getPiece(r, c);
                if (p == null || p.getColor() != player) {
                    continue;
                }
                Position from = new Position(r, c);
                List<Move> pieceCaptures = generateCapturesFrom(state, from);
                captures.addAll(pieceCaptures);
                if (pieceCaptures.isEmpty()) {
                    quietMoves.addAll(generateQuietMovesFrom(state, from));
                }
            }
        }

        return captures.isEmpty() ? quietMoves : captures;
    }

    public List<Move> generateMovesFrom(GameState state, Position from, PieceColor player) {
        List<Move> all = generateLegalMoves(state, player);
        List<Move> filtered = new ArrayList<>();
        for (Move move : all) {
            if (move.from().equals(from)) {
                filtered.add(move);
            }
        }
        return filtered;
    }

    private List<Move> generateQuietMovesFrom(GameState state, Position from) {
        List<Move> moves = new ArrayList<>();
        Piece piece = state.getPiece(from);
        if (piece == null) {
            return moves;
        }

        for (int[] dir : directions(piece)) {
            int nr = from.row() + dir[0];
            int nc = from.col() + dir[1];
            if (state.inBounds(nr, nc) && state.getPiece(nr, nc) == null) {
                moves.add(new Move(List.of(from, new Position(nr, nc)), List.of()));
            }
        }
        return moves;
    }

    private List<Move> generateCapturesFrom(GameState state, Position from) {
        List<Move> out = new ArrayList<>();
        Piece piece = state.getPiece(from);
        if (piece == null) {
            return out;
        }
        dfsCapture(state, from, piece, new ArrayList<>(List.of(from)), new ArrayList<>(), out);
        return out;
    }

    private void dfsCapture(GameState state,
                            Position current,
                            Piece piece,
                            List<Position> path,
                            List<Position> captured,
                            List<Move> out) {
        boolean continued = false;

        for (int[] dir : directions(piece)) {
            int mr = current.row() + dir[0];
            int mc = current.col() + dir[1];
            int lr = current.row() + (2 * dir[0]);
            int lc = current.col() + (2 * dir[1]);

            if (!state.inBounds(mr, mc) || !state.inBounds(lr, lc)) {
                continue;
            }

            Piece jumped = state.getPiece(mr, mc);
            if (jumped == null || jumped.getColor() == piece.getColor()) {
                continue;
            }
            if (state.getPiece(lr, lc) != null) {
                continue;
            }

            GameState next = state.copy();
            Piece mover = next.getPiece(current);
            next.setPiece(current, null);
            next.setPiece(mr, mc, null);
            Position landing = new Position(lr, lc);
            next.setPiece(landing, mover);

            List<Position> nextPath = new ArrayList<>(path);
            nextPath.add(landing);
            List<Position> nextCaptured = new ArrayList<>(captured);
            nextCaptured.add(new Position(mr, mc));

            dfsCapture(next, landing, mover, nextPath, nextCaptured, out);
            continued = true;
        }

        if (!continued && !captured.isEmpty()) {
            out.add(new Move(path, captured));
        }
    }

    private List<int[]> directions(Piece piece) {
        List<int[]> dirs = new ArrayList<>();
        if (piece.isKing()) {
            dirs.add(new int[]{1, 1});
            dirs.add(new int[]{1, -1});
            dirs.add(new int[]{-1, 1});
            dirs.add(new int[]{-1, -1});
        } else {
            int f = piece.getColor().forward();
            dirs.add(new int[]{f, 1});
            dirs.add(new int[]{f, -1});
        }
        return dirs;
    }
}
