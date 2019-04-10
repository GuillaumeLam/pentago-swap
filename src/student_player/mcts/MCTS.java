package student_player.mcts;

import boardgame.Board;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import student_player.mcts.MCTSStructure.Node;
import student_player.mcts.MCTSStructure.State;
import student_player.mcts.MCTSStructure.Tree;

import java.util.ArrayList;

public class MCTS {
    private static final int WIN_SCORE = 10;
    private static final long MCTS_TIME = 40;
    private int level;
    private int opponent;

    public MCTS() {
        this.level = 10;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private int getMillisForCurrentLevel() {
        return 2 * (this.level - 1) + 1;
    }

    public PentagoMove mcts(PentagoBoardState pentagoBoardState, int player) {
        long start = System.currentTimeMillis();
        long end = start + MCTS_TIME * getMillisForCurrentLevel();

        if (player == PentagoBoardState.WHITE) {
            opponent = PentagoBoardState.BLACK;
        }
        else {
            opponent = PentagoBoardState.WHITE;
        }

        Tree tree = new Tree(pentagoBoardState);
        Node rootNode = tree.getRoot();
        rootNode.getState().setPlayerNo(opponent);

        while (System.currentTimeMillis() < end) {
            // phase 1
            Node promisingNode = selectPromisingNode(rootNode);

            // phase 2
            if (promisingNode.getState().getBoard().getWinner() == Board.NOBODY) {
                expandNode(promisingNode);
            }

            // phase 3
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);

            // phase 4
            backPropagation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getPentagoMove();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) {
        ArrayList<State> possibleStates = node.getState().getAllPossibleStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state);
            newNode.setParent(node);
            newNode.getState().setPlayerNo(node.getState().getOpponent());
            node.getChildArray().add(newNode);
        });
    }

    private void backPropagation(Node nodeToExplore, int playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo) {
                tempNode.getState().addScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }

    private int simulateRandomPlayout(Node node) {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        int boardStatus = tempState.getBoard().getWinner();
        if (boardStatus == opponent) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }
        while (boardStatus == Board.NOBODY) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getBoard().getWinner();
        }
        return boardStatus;
    }
}
