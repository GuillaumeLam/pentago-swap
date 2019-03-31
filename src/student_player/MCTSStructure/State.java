package student_player.MCTSStructure;

import boardgame.Move;
import pentago_swap.PentagoBoard;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import pentago_swap.RandomPentagoPlayer;

import java.util.ArrayList;
import java.util.Random;

public class State {
    PentagoBoardState pentagoBoardState;
    PentagoMove pentagoMove;
    int playerNo;
    int visitCount;
    double winScore;
    Random rand = new Random();

    public State(PentagoBoardState pentagoBoardState) {
        this.pentagoBoardState = pentagoBoardState;
    }

    public State(PentagoBoardState pentagoBoardState, PentagoMove pentagoMove) {
        this.pentagoBoardState = pentagoBoardState;
        this.pentagoMove = pentagoMove;
    }

    public State(State state) {
        this.pentagoBoardState = (PentagoBoardState) state.getBoard().clone();
        this.playerNo = state.getPlayerNo();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
        this.pentagoMove = state.getPentagoMove();
    }

    public PentagoBoardState getBoard() {
        return pentagoBoardState;
    }

    public void setBoard(PentagoBoardState pentagoBoardState) {
        this.pentagoBoardState = pentagoBoardState;
    }

    public int getPlayerNo() {
        return playerNo;
    }

    public void setPlayerNo(int playerNo) {
        this.playerNo = playerNo;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public double getWinScore() {
        return winScore;
    }

    public void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public PentagoMove getPentagoMove() {
        return pentagoMove;
    }

    public void setPentagoMove(PentagoMove pentagoMove) {
        this.pentagoMove = pentagoMove;
    }

    public int getOpponent(){
        if (this.playerNo == PentagoBoardState.WHITE) {
            return PentagoBoardState.BLACK;
        }
        else if (this.playerNo == PentagoBoardState.BLACK) {
            return PentagoBoardState.WHITE;
        }
        else {
            return -1;
        }
    }

    public ArrayList<State> getAllPossibleStates() {
        /*List<State> possibleStates = new ArrayList<>();
        List<Position> availablePositions = this.board.getEmptyPositions();
        availablePositions.forEach(p -> {
            State newState = new State(this.board);
            newState.setPlayerNo(3 - this.playerNo);
            newState.getBoard().performMove(newState.getPlayerNo(), p);
            possibleStates.add(newState);
        });
        return possibleStates;*/

        ArrayList<State> possibleStates = new ArrayList<>();
        ArrayList<PentagoMove> availableMoves = this.pentagoBoardState.getAllLegalMoves();
        availableMoves.forEach(pentagoMove -> {
            State newState = new State((PentagoBoardState)this.getBoard().clone());
            newState.getBoard().processMove(pentagoMove);
            if (this.getPlayerNo() == PentagoBoardState.WHITE) {
                newState.setPlayerNo(PentagoBoardState.BLACK);
            }
            else if (this.getPlayerNo() == PentagoBoardState.BLACK) {
                newState.setPlayerNo(PentagoBoardState.WHITE);
            }
            newState.setPentagoMove(pentagoMove);
            possibleStates.add(newState);
        });
        return possibleStates;
    }

    public void incrementVisit() {
        this.visitCount++;
    }

    public void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    public void randomPlay() {
        /*List<Position> availablePositions = this.board.getEmptyPositions();
        int totalPossibilities = availablePositions.size();
        int selectRandom = (int) (Math.random() * totalPossibilities);
        this.board.performMove(this.playerNo, availablePositions.get(selectRandom));*/
        ArrayList<PentagoMove> randomMoves = this.pentagoBoardState.getAllLegalMoves();
        PentagoMove randomMove = randomMoves.get(rand.nextInt(randomMoves.size()));
        this.pentagoBoardState.processMove(randomMove);

    }

    public void togglePlayer() {
        /*this.playerNo = 3 - this.playerNo;*/
        if (this.playerNo == PentagoBoardState.WHITE) {
            this.playerNo = PentagoBoardState.BLACK;
        }
        else if (this.playerNo == PentagoBoardState.BLACK) {
            this.playerNo = PentagoBoardState.WHITE;
        }
    }
}
