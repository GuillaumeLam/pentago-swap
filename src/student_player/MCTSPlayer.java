package student_player;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoPlayer;

public class MCTSPlayer extends PentagoPlayer {
    public MCTSPlayer() {
        super("mcts");
    }

    public Move chooseMove(PentagoBoardState boardState) {
        Move myMove;

        MCTS mcts = new MCTS();
        myMove = mcts.mcts(boardState,boardState.getTurnPlayer());

        return myMove;
    }
}
