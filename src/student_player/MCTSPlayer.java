package student_player;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoPlayer;
import student_player.mcts.MCTS;

import java.util.ArrayList;
import java.util.Random;

public class MCTSPlayer extends PentagoPlayer {
    private static final String PLAYER ="mcts";
    private ArrayList<PentagoMove> centers = new ArrayList<>();
    private Random rnd = new Random();
    private int firstTurn = 0;

    public MCTSPlayer() {
        super(PLAYER);
    }

    public Move chooseMove(PentagoBoardState boardState) {

        long start = System.currentTimeMillis();

        // first turn
        if (firstTurn == 0) {
            centers.add(new PentagoMove(1,1, PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(4,1, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(1,4, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(4,4, PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));
        }

        PentagoMove myMove = null;
        
        if(centers.size() > 2) {
            while (!(myMove != null && boardState.isPlaceLegal(myMove.getMoveCoord()) ) && centers.size() != 0) {
                int index = rnd.nextInt(centers.size());
                myMove = centers.get(index);
                centers.remove(index);
            }
        }

        if (centers.size() <= 1 && (myMove == null ||!boardState.isPlaceLegal(myMove.getMoveCoord())) ) {
            MCTS mcts = new MCTS();
            myMove = mcts.mcts(boardState,boardState.getTurnPlayer());
        }


        long end = System.currentTimeMillis();

        System.out.println("mcts move took " + (double)(end-start)/1000 + "s");

        firstTurn++;

        return myMove;
    }
}
