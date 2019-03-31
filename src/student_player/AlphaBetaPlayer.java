package student_player;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoPlayer;

public class AlphaBetaPlayer extends PentagoPlayer {
    public AlphaBetaPlayer() {
        super("alpha-beta");
    }

    public Move chooseMove(PentagoBoardState boardState) {
        Move myMove;

        //myMove = ABPrune.minimax(2, boardState.getTurnPlayer(), boardState).getValue();
        myMove = ABPrune.abp(3, boardState.getTurnPlayer(), boardState, Integer.MIN_VALUE, Integer.MAX_VALUE).getValue();

        return myMove;
    }
}
