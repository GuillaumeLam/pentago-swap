package student_player;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoPlayer;
import student_player.rush.Rush;

public class StudentPlayer extends PentagoPlayer {
    private static final String PLAYER ="260736117";
    private Rush rp = new Rush();

    public StudentPlayer() {
        super(PLAYER);
    }

    public Move chooseMove(PentagoBoardState boardState) {
        PentagoMove myMove;
        myMove = rp.play(boardState);
        return myMove;
    }
}



/*TODO for speed:
*  NOPE-reduce the number of legal moves to account for rotation and immediate loss/win
*
* TODO for accuracy
*  DONE-monte carlo implementation
*  NOPE-nn implementation for board evaluation
*  SORTA-in heuristic of board have some blocking concept
*  SORTA-in heuristic of board have points for draw
*  SORTA-in heuristic of board have points for blocking
*  DONE-first four move focus on center pieces
*  DONE-run ab but for opponent and if more favourable then play that instead
*
 * TODO general
*  DONE-run code/game on desktop
*
*  DONE-illegal move as second
* */