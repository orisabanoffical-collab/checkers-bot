package org.example.checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicBot {
    private final MoveGenerator generator = new MoveGenerator();
    private final PieceColor botColor;
    private final Random random = new Random();

    public HeuristicBot(PieceColor botColor) {
        this.botColor = botColor;
    }

    public Move chooseMove(GameState state) {
        List<Move> moves = generator.generateLegalMoves(state, botColor);
        if (moves.isEmpty()) {
            return null;
        }

        int best = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();

        for (Move move : moves) {
            GameState copy = state.copy();
            applyWithoutTurn(copy, move);
            int score = evaluate(copy, move);

            if (score > best) {
                best = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == best) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private void applyWithoutTurn(GameState state, Move move) {
        Piece mover = state.getPiece(move.from());
        state.setPiece(move.from(), null);
        for (Position captured : move.getCaptured()) {
            state.setPiece(captured, null);
        }
        state.setPiece(move.to(), mover);

        if (!mover.isKing()) {
            if (mover.getColor() == PieceColor.WHITE && move.to().row() == 0) {
                mover.promote();
            }
            if (mover.getColor() == PieceColor.BLACK && move.to().row() == 7) {
                mover.promote();
            }
        }
    }

    private int evaluate(GameState state, Move move) {
        int score = 0;
        score += materialScore(state) * 10;
        score += centerScore(state) * 2;
        score += move.getCaptured().size() * 6;
        if (isPromotion(state, move)) {
            score += 9;
        }
        score -= threatenedPieces(state, botColor) * 3;
        return score;
    }

    private int materialScore(GameState state) {
        int my = 0;
        int enemy = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = state.getPiece(r, c);
                if (p == null) {
                    continue;
                }
                int value = p.isKing() ? 3 : 1;
                if (p.getColor() == botColor) {
                    my += value;
                } else {
                    enemy += value;
                }
            }
        }
        return my - enemy;
    }

    private int centerScore(GameState state) {
        int score = 0;
        for (int r = 2; r <= 5; r++) {
            for (int c = 2; c <= 5; c++) {
                Piece p = state.getPiece(r, c);
                if (p == null) {
                    continue;
                }
                score += p.getColor() == botColor ? 1 : -1;
            }
        }
        return score;
    }

    private int threatenedPieces(GameState state, PieceColor color) {
        int threatened = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = state.getPiece(r, c);
                if (p != null && p.getColor() == color && isCapturable(state, new Position(r, c))) {
                    threatened++;
                }
            }
        }
        return threatened;
    }

    private boolean isCapturable(GameState state, Position target) {
        Piece targetPiece = state.getPiece(target);
        if (targetPiece == null) {
            return false;
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece attacker = state.getPiece(r, c);
                if (attacker == null || attacker.getColor() == targetPiece.getColor()) {
                    continue;
                }
                for (int[] dir : attackerDirs(attacker)) {
                    int mr = r + dir[0];
                    int mc = c + dir[1];
                    int lr = r + 2 * dir[0];
                    int lc = c + 2 * dir[1];
                    if (state.inBounds(mr, mc) && state.inBounds(lr, lc)
                            && mr == target.row() && mc == target.col()
                            && state.getPiece(lr, lc) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<int[]> attackerDirs(Piece piece) {
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

    private boolean isPromotion(GameState state, Move move) {
        Piece p = state.getPiece(move.to());
        return p != null && p.getColor() == botColor && p.isKing();
    }
}
