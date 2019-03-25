package student_player;

import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260736117");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        MyTools.getSomething();

        ABPrune abp = new ABPrune(boardState, this);

        Move myMove = boardState.getAllLegalMoves().get(0);

        // Return your move to be processed by the server.
        return myMove;
    }
}

/*TODO for speed:
*  -reduce the number of legal moves to account for rotation and immediate loss/win
*  -implement in C++
* TODO for accuracy
*  -monte carlo implementation
*  -nn implementation for board evaluation
* TODO general
*  -run code/game on desktop
* */