package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece{
    private final static int[] CANDIDATE_MOVE_COORDINATE = {8, 16, 7, 9};
    public Pawn(final int piecePosition, final Alliance pieceAlliance) {

        super(PieceType.PAWN,piecePosition, pieceAlliance, true);
    }
    public Pawn(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove) {

        super(PieceType.PAWN,piecePosition, pieceAlliance, isFirstMove);
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> LegalMoves = new ArrayList<>();
        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }
            if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    LegalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                }else {
                LegalMoves.add(new PawnMove(board, this,candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 16 && this.isFirstMove()
                    && ((BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isWhite()))) {
                    final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                    if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                            !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                        LegalMoves.add(new PawnJump(board, this,candidateDestinationCoordinate));
                    }
            } else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()
                    || (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            LegalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,candidateDestinationCoordinate, pieceOnCandidate)));
                        }else {
                        LegalMoves.add(new PawnAttackMove(board, this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }else if (board.getEnPassantPawn() != null){
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            LegalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }

            } else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()
                    || (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            LegalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,candidateDestinationCoordinate, pieceOnCandidate)));
                        }else {
                        LegalMoves.add(new PawnAttackMove(board, this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }else if (board.getEnPassantPawn() != null){
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            LegalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }

        }
        return ImmutableList.copyOf(LegalMoves);
    }
    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
    public Piece getPromotionPiece(){
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }
}
