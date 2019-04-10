package student_player.rush;

import pentago_swap.PentagoBoardState;

import java.util.ArrayList;

public class Line {
    private ArrayList<Spot> line;
    private String direction;
    private int length;

    public Line (PentagoBoardState boardState, int x, int y, String direction, int length) {
        this.direction = direction;
        this.line = new ArrayList<>();
        this.length = length;
        setLine(boardState, x, y, length);
    }

    public void updateLine (PentagoBoardState boardState) {
        int x = line.get(0).getX();
        int y = line.get(0).getY();
        line = new ArrayList<>();
        setLine(boardState, x, y, length);
    }

    private void setLine (PentagoBoardState boardState, int x, int y, int length) {
        if (direction == "vl" || direction == "vr") {
            for (int i = 0; i <  length; i++) {
                line.add(new Spot(x+i,y,boardState.getPieceAt(x+i,y)));
            }
        }
        else if (direction == "ht" || direction == "hb") {
            for (int i = 0; i <  length; i++) {
                line.add(new Spot(x,y+i,boardState.getPieceAt(x,y+i)));
            }
        }
        else if (direction == "d") {
            for (int i = 0; i <  length; i++) {
                line.add(new Spot(x+i,y+i,boardState.getPieceAt(x+i,y+i)));
            }
        }
        else if (direction == "p") {
            for (int i = 0; i <  length; i++) {
                line.add(new Spot(x+i,y-i,boardState.getPieceAt(x+i,y-i)));
            }
        }
    }

    public ArrayList<Spot> getLine() {
        return line;
    }

    public ArrayList<Spot> getAvailMoves() {
        ArrayList<Spot> availMoves = new ArrayList<>();
        for (Spot spot : line) {
            if (spot.getPlayer() == PentagoBoardState.Piece.EMPTY) {
                availMoves.add(spot);
            }
        }
        return availMoves;
    }

    public String getDirection() {
        return direction;
    }
}