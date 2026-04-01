package org.example.checkers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {

    @Test
    void initialBoardHas12PiecesEach() {
        GameState state = new GameState();
        assertEquals(12, state.countPieces(PieceColor.WHITE));
        assertEquals(12, state.countPieces(PieceColor.BLACK));
    }

    @Test
    void mandatoryCaptureOnlyReturnsCaptureMoves() {
        GameState state = new GameState();
        state.clearBoard();
        state.setCurrentPlayer(PieceColor.WHITE);

        state.setPiece(5, 0, new Piece(PieceColor.WHITE, false));
        state.setPiece(4, 1, new Piece(PieceColor.BLACK, false));
        state.setPiece(5, 4, new Piece(PieceColor.WHITE, false));

        List<Move> moves = new MoveGenerator().generateLegalMoves(state, PieceColor.WHITE);
        assertFalse(moves.isEmpty());
        assertTrue(moves.stream().allMatch(Move::isCapture));
    }

    @Test
    void multiCaptureSequenceIsGenerated() {
        GameState state = new GameState();
        state.clearBoard();
        state.setCurrentPlayer(PieceColor.WHITE);
        state.setPiece(5, 0, new Piece(PieceColor.WHITE, false));
        state.setPiece(4, 1, new Piece(PieceColor.BLACK, false));
        state.setPiece(2, 3, new Piece(PieceColor.BLACK, false));

        Move move = new MoveGenerator().generateLegalMoves(state, PieceColor.WHITE).get(0);
        assertEquals(3, move.getPath().size());
        assertEquals(List.of(new Position(4, 1), new Position(2, 3)), move.getCaptured());
        assertEquals(new Position(1, 4), move.to());
    }

    @Test
    void illegalMoveIsRejected() {
        GameState state = new GameState();
        state.clearBoard();
        state.setCurrentPlayer(PieceColor.WHITE);
        state.setPiece(5, 0, new Piece(PieceColor.WHITE, false));
        state.setPiece(4, 1, new Piece(PieceColor.BLACK, false));

        GameEngine engine = new GameEngine();
        Move illegal = new Move(List.of(new Position(5, 0), new Position(4, 1)), List.of());
        assertFalse(engine.applyMove(state, illegal));
    }

    @Test
    void promotionWorks() {
        GameState state = new GameState();
        state.clearBoard();
        state.setCurrentPlayer(PieceColor.WHITE);
        state.setPiece(1, 2, new Piece(PieceColor.WHITE, false));

        GameEngine engine = new GameEngine();
        Move move = new Move(List.of(new Position(1, 2), new Position(0, 1)), List.of());
        assertTrue(engine.applyMove(state, move));
        assertTrue(state.getPiece(0, 1).isKing());
    }
}
