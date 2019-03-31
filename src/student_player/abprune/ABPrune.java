package student_player.abprune;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

import java.util.AbstractMap;
import java.util.ArrayList;

public class ABPrune {

    public static AbstractMap.SimpleEntry<Integer, PentagoMove> minimax(int depth, int player, PentagoBoardState pbs) {
        ArrayList<PentagoMove> nextmoves = pbs.getAllLegalMoves();

        int bestScore = (player == PentagoBoardState.WHITE) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        PentagoMove bestMove = nextmoves.get(0);

        if (nextmoves.isEmpty() || depth==0) {
            bestScore = evaluate(pbs, player);
        }
        else {
            for (PentagoMove currentmove: nextmoves){
                PentagoBoardState boardwMove = (PentagoBoardState)pbs.clone();
                boardwMove.processMove(currentmove);
                if (player == PentagoBoardState.WHITE) {
                    currentScore = minimax(depth -1, PentagoBoardState.BLACK, boardwMove).getKey();
                    if(currentScore > bestScore) {
                        bestScore = currentScore;
                        bestMove = currentmove;
                    }
                }
                else {
                    currentScore = minimax(depth -1, PentagoBoardState.WHITE, boardwMove).getKey();
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestMove = currentmove;
                    }
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(bestScore, bestMove);
    }

    public static AbstractMap.SimpleEntry<Integer, PentagoMove> abp(int depth, int player, PentagoBoardState pbs, int alpha, int beta) {
        ArrayList<PentagoMove> nextmoves = pbs.getAllLegalMoves();

        int bestScore;
        PentagoMove bestMove = nextmoves.get(0);

        if (nextmoves.isEmpty() || depth==0) {
            bestScore = evaluate(pbs, player);
            return new AbstractMap.SimpleEntry<>(bestScore, bestMove);
        }
        else {
            for (PentagoMove currentmove: nextmoves){
                PentagoBoardState boardwMove = (PentagoBoardState)pbs.clone();
                boardwMove.processMove(currentmove);
                if (player == PentagoBoardState.WHITE) {
                    bestScore = abp(depth -1, PentagoBoardState.BLACK, boardwMove, alpha, beta).getKey();
                    if(bestScore > alpha) {
                        alpha = bestScore;
                        bestMove = currentmove;
                    }
                }
                else {
                    bestScore = abp(depth -1, PentagoBoardState.WHITE, boardwMove, alpha, beta).getKey();
                    if (bestScore < beta) {
                        beta = bestScore;
                        bestMove = currentmove;
                    }
                }
                if (alpha >= beta) {
                    //System.out.println("pruned!");
                    break;
                }
            }
            return new AbstractMap.SimpleEntry<>((player == PentagoBoardState.WHITE) ? alpha: beta, bestMove);
        }
    }

    private static int evaluate(PentagoBoardState pentagoBoardState, int player) {
        return boardHeuristics.streakcount(pentagoBoardState, player);
    }
}
