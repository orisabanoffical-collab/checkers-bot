package org.example.checkers;

import java.util.List;

public class GameEngine {
    private final MoveGenerator moveGenerator = new MoveGenerator();

    public List<Move> legalMoves(GameState state, PieceColor player) {
        return moveGenerator.generateLegalMoves(state, player);
    }

    public boolean applyMove(GameState state, Move move) {
        if (move == null) {
            return false;
        }

        Piece mover = state.getPiece(move.from());
        if (mover == null || mover.getColor() != state.getCurrentPlayer()) {
            return false;
        }

        List<Move> legal = legalMoves(state, state.getCurrentPlayer());
        if (!legal.contains(move)) {
            return false;
        }

        state.setPiece(move.from(), null);
        for (Position captured : move.getCaptured()) {
            state.setPiece(captured, null);
        }
        state.setPiece(move.to(), mover);

        maybePromote(mover, move.to());
        state.switchTurn();
        return true;
    }

    private void maybePromote(Piece piece, Position to) {
        if (piece.isKing()) {
            return;
        }
        if (piece.getColor() == PieceColor.WHITE && to.row() == 0) {
            piece.promote();
        }
        if (piece.getColor() == PieceColor.BLACK && to.row() == 7) {
            piece.promote();
        }
    }

    public boolean isGameOver(GameState state) {
        return state.countPieces(PieceColor.WHITE) == 0
                || state.countPieces(PieceColor.BLACK) == 0
                || legalMoves(state, state.getCurrentPlayer()).isEmpty();
    }

    public PieceColor winner(GameState state) {
        if (state.countPieces(PieceColor.WHITE) == 0) {
            return PieceColor.BLACK;
        }
        if (state.countPieces(PieceColor.BLACK) == 0) {
            return PieceColor.WHITE;
        }
        if (legalMoves(state, state.getCurrentPlayer()).isEmpty()) {
            return state.getCurrentPlayer().opponent();
        }
        return null;
    }
}
