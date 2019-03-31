package student_player.MCTSStructure;

import pentago_swap.PentagoBoard;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Node {
    State state;
    Node parent;
    ArrayList<Node> childArray;

    public Node(PentagoBoardState pentagoBoardState) {
        this.state = new State(pentagoBoardState);
    }

    public Node(State state) {
        this.state = state;
        childArray = new ArrayList<>();
    }

    public Node(State state, Node node, ArrayList<Node> childArr) {
        this.state = state;
        this.parent = node;
        this.childArray = childArr;
    }

    public Node(Node node) {
        this.childArray = new ArrayList<>();
        this.state = new State(node.getState());
        if (node.getParent() != null) {
            this.parent = node.getParent();
        }
        ArrayList<Node> childArray = node.getChildArray();
        for (Node child : childArray) {
            this.childArray.add(new Node(child));
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node node) {
        this.parent = node;
    }

    public ArrayList<Node> getChildArray(){
        return childArray;
    }

    public void setChildArray(ArrayList<Node> childArray) {
        this.childArray = childArray;
    }

    public Node getRandomChildNode() {
        int possMoves = this.childArray.size();
        return this.childArray.get((int) (Math.random() * possMoves));
    }

    public Node getChildWithMaxScore() {
        return Collections.max(this.childArray, Comparator.comparing(c -> {
            return c.getState().getVisitCount();
        }));
    }
}
