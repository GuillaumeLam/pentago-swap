package student_player;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoPlayer;
import student_player.rush.Rush;

public class RushPlayer extends PentagoPlayer {
    private static final String PLAYER ="rush";
    private Rush rp = new Rush();

    public RushPlayer() {
        super(PLAYER);
    }

    public Move chooseMove(PentagoBoardState boardState) {
        long start = System.currentTimeMillis();

        PentagoMove myMove;

        myMove = rp.play(boardState);

        long end = System.currentTimeMillis();

        System.out.println("mcts move took " + (double)(end-start)/1000 + "s");

        System.out.println(boardState.toString());
        return myMove;
    }
}