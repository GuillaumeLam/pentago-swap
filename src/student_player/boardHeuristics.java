package student_player;

import boardgame.Board;
import pentago_swap.PentagoBoardState;

// check if insta win
// count the number of 3, 2, and 1 in a rows
public abstract class boardHeuristics {
    public static int streakcount(PentagoBoardState pentagoBoardState, int player) {
        int moveValue = 0;
        int streak = 0;

        int winner = pentagoBoardState.getWinner();

        if (winner == Board.NOBODY) {
            // count horizontally
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    // player 0 plays white pieces
                    if (pentagoBoardState.getPieceAt(i,j) == PentagoBoardState.Piece.WHITE &&
                            pentagoBoardState.getPieceAt(i,j+1) == PentagoBoardState.Piece.WHITE) {
                        moveValue += streak + 1;
                        streak++;
                    }
                    else if(pentagoBoardState.getPieceAt(i,j) == PentagoBoardState.Piece.BLACK &&
                            pentagoBoardState.getPieceAt(i,j+1) == PentagoBoardState.Piece.BLACK) {
                        moveValue -= streak - 1;
                        streak++;
                    }
                    else {
                        streak = 0;
                    }
                }
            }

            // count vertically
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (pentagoBoardState.getPieceAt(j,i) == PentagoBoardState.Piece.WHITE &&
                            pentagoBoardState.getPieceAt(j+1,i) == PentagoBoardState.Piece.WHITE) {
                        moveValue += streak + 1;
                        streak++;
                    }
                    else if(pentagoBoardState.getPieceAt(j,i) == PentagoBoardState.Piece.BLACK &&
                            pentagoBoardState.getPieceAt(j+1,i) == PentagoBoardState.Piece.BLACK) {
                        moveValue -= streak - 1;
                        streak++;
                    }
                    else {
                        streak = 0;
                    }
                }
            }

            // count main diagonal;
            for (int i = 0; i > 5; i++) {
                if (pentagoBoardState.getPieceAt(i,i) == PentagoBoardState.Piece.WHITE &&
                        pentagoBoardState.getPieceAt(i+1,i+1) == PentagoBoardState.Piece.WHITE) {
                    moveValue += streak + 1;
                    streak++;
                }
                else if(pentagoBoardState.getPieceAt(i,i) == PentagoBoardState.Piece.BLACK &&
                        pentagoBoardState.getPieceAt(i+1,i+1) == PentagoBoardState.Piece.BLACK) {
                    moveValue -= streak - 1;
                    streak++;
                }
                else {
                    streak = 0;
                }
            }

            for (int i = 0; i > 5; i++) {
                if (pentagoBoardState.getPieceAt(i,5-i) == PentagoBoardState.Piece.WHITE &&
                        pentagoBoardState.getPieceAt(i+1,4-i) == PentagoBoardState.Piece.WHITE) {
                    moveValue += streak + 1;
                    streak++;
                }
                else if(pentagoBoardState.getPieceAt(i,5-i) == PentagoBoardState.Piece.BLACK &&
                        pentagoBoardState.getPieceAt(i+1,4-i) == PentagoBoardState.Piece.BLACK) {
                    moveValue -= streak - 1;
                    streak++;
                }
                else {
                    streak = 0;
                }
            }

            // count the offset diagonals
        }
        else {
            if (winner == PentagoBoardState.WHITE) {
                moveValue = Integer.MAX_VALUE;
            }
            else {
                moveValue = Integer.MIN_VALUE;
            }
        }


        // swap points if opponent is actually playing first
        if(player == PentagoBoardState.BLACK) {
            moveValue = -moveValue;
        }

        return moveValue;
    }

    public static int haswinner(PentagoBoardState pentagoBoardState) {
        int winner = 0;
        if (pentagoBoardState.getWinner() == Board.NOBODY) {
            winner = 0;
        }
        /*else {
            if
        }*/
        return winner;
    }
}
