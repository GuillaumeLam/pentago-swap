package student_player;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

import java.util.AbstractMap;
import java.util.ArrayList;

public class ABPrune {

    public static AbstractMap.SimpleEntry<Integer, PentagoMove> abp(int depth, int player, PentagoBoardState pbs) {
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
                    currentScore = abp(depth -1, PentagoBoardState.BLACK, boardwMove).getKey();
                    if(currentScore > bestScore) {
                        bestScore = currentScore;
                        bestMove = currentmove;
                    }
                }
                else {
                    currentScore = abp(depth -1, PentagoBoardState.WHITE, boardwMove).getKey();
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestMove = currentmove;
                    }
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(bestScore, bestMove);
    }

    private static int evaluate(PentagoBoardState pentagoBoardState, int player) {
        return boardHeuristics.streakcount(pentagoBoardState, player);
    }
}
