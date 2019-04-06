package student_player;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoPlayer;
import student_player.mcts.MCTS;

public class MCTSPlayer extends PentagoPlayer {
    public MCTSPlayer() {
        super("mcts");
    }

    public Move chooseMove(PentagoBoardState boardState) {
        long start = System.currentTimeMillis();
        Move myMove;

        MCTS mcts = new MCTS();
        myMove = mcts.mcts(boardState,boardState.getTurnPlayer());

        long end = System.currentTimeMillis();

        System.out.println("move took " + (double)(end-start)/1000 + "s");
        return myMove;
    }
}