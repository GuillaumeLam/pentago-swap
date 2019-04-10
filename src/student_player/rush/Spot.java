package student_player.rush;

import pentago_swap.PentagoBoardState;

public class Spot {
    private int x;
    private int y;
    private PentagoBoardState.Piece player;

    public Spot (int x, int y, PentagoBoardState.Piece player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PentagoBoardState.Piece getPlayer() {
        return player;
    }

    public void setPlayer(PentagoBoardState.Piece player) {
        this.player = player;
    }
}
