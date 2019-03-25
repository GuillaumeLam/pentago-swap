package student_player;

import javafx.util.Pair;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

import java.util.ArrayList;

public class ABPrune {
    private static final int PLAYER_MOVE = 0;
    private static final int OPPONENT_MOVE = 1;
    protected PentagoBoardState boardState;
    protected StudentPlayer studentPlayer;


    public ABPrune(PentagoBoardState pentagoBoardState, StudentPlayer sp) {
        boardState = pentagoBoardState;
        studentPlayer = sp;
    }

    public Pair<Integer, PentagoMove> abp(int depth, int player, PentagoBoardState pbs) {
        ArrayList<PentagoMove> nextmoves = boardState.getAllLegalMoves();

        int bestScore = (player == PLAYER_MOVE) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        PentagoMove bestMove = nextmoves.get(0);

        if (nextmoves.isEmpty() || depth==0) {
            bestScore = evaluate(pbs);
        }
        else {
            for (PentagoMove currentmove: nextmoves){
                PentagoBoardState boardwMove = (PentagoBoardState)pbs.clone();
                boardwMove.processMove(currentmove);
                if (player == PLAYER_MOVE) {
                    currentScore = abp(depth -1, OPPONENT_MOVE, boardwMove).getKey();
                    if(currentScore > bestScore) {
                        bestScore = currentScore;
                        bestMove = currentmove;
                    }
                }
                else {
                    currentScore = abp(depth -1, PLAYER_MOVE, boardwMove).getKey();
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestMove = currentmove;
                    }
                }
            }
        }
        return new Pair<>(bestScore,bestMove);
    }

    private int evaluate(PentagoBoardState pentagoBoardState) {
        int moveValue = 0;
        /*if (pentagoBoardState.getWinner() == studentPlayer.getColor()){
            moveValue = 10;
        }
        else if ()*/
        // count the number of 3, 2, and 1 in a rows
        pentagoBoardState.get

        return moveValue;
    }
}
