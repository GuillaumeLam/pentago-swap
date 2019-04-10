package student_player;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoPlayer;
import student_player.mcts.MCTS;

public class MCTSPlayer extends PentagoPlayer {
    private static final String PLAYER ="mcts";

    public MCTSPlayer() {
        super(PLAYER);
    }

    public Move chooseMove(PentagoBoardState boardState) {

        long start = System.currentTimeMillis();

        PentagoMove myMove;
        MCTS mcts = new MCTS();
        myMove = mcts.mcts(boardState,boardState.getTurnPlayer());

        long end = System.currentTimeMillis();

        System.out.println("mcts move took " + (double)(end-start)/1000 + "s");

        System.out.println(boardState.toString());
        return myMove;
    }
}

