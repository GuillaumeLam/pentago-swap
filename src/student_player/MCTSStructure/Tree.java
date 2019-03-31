package student_player.MCTSStructure;

import pentago_swap.PentagoBoardState;

public class Tree {
    Node root;

    public Tree(PentagoBoardState pentagoBoardState) {
        root = new Node(pentagoBoardState);
    }

    public Tree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void addChild(Node parent, Node child) {
        parent.getChildArray().add(child);
    }
}
