package student_player.rush;

import pentago_swap.PentagoBoard;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import student_player.mcts.MCTS;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Random;

public class Rush {
    private ArrayList<PentagoMove> centers = new ArrayList<>();
    private Random rnd = new Random();
    private int turn = 0;
    private ArrayList<Line> possibleFives = new ArrayList<>();
    private PentagoBoardState.Piece mypiece;
    private PentagoBoardState.Piece oppiece;
    private int placedPieces = 0;
    private boolean secondrush = true;

    public PentagoMove play(PentagoBoardState boardState) {

        // first turn
        if (turn == 0) {
            centers.add(new PentagoMove(1,1, PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(4,1, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(1,4, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));
            centers.add(new PentagoMove(4,4, PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer()));

            if (boardState.getTurnPlayer() == PentagoBoardState.WHITE) {
                mypiece = PentagoBoardState.Piece.WHITE;
                oppiece = PentagoBoardState.Piece.BLACK;
            }
            else {
                mypiece = PentagoBoardState.Piece.BLACK;
                oppiece = PentagoBoardState.Piece.WHITE;
            }
        }

        PentagoMove myMove = null;

        if (boardState.getTurnPlayer() == PentagoBoardState.WHITE) {
            myMove = rush(boardState);
        }
        else {
            // logic for going second
            // rush until move three if the player has deviated from putting their third in that row abandon else go for block
            if (turn < 2) {
                while(!((myMove != null && boardState.isPlaceLegal(myMove.getMoveCoord())) || centers.size() == 0)) {
                    int index = rnd.nextInt(centers.size());
                    myMove = centers.get(index);
                }
            }
            else if (secondrush) {
                if (turn == 2) {
                    AbstractMap.SimpleEntry<Boolean, ArrayList<Line>> norush = otherPlayerNoRush(boardState);
                    if (norush.getKey()) {
                        myMove = rush(boardState);
                    }
                    else {
                        secondrush = false;

                        myMove = block(norush.getValue(), boardState);

                    }
                }
                else {
                    myMove = rush(boardState);
                }
            }
            else if (!secondrush && turn == 3) {
                secondBlock(boardState);
            }

            if (myMove == null) {
                myMove = postrush(boardState);
            }
        }

        turn++;
        return myMove;
    }

    private PentagoMove rush(PentagoBoardState boardState) {
        PentagoMove myMove = null;

        // get two centers cuz op
        if(turn < 2) {
            while (!(myMove != null && boardState.isPlaceLegal(myMove.getMoveCoord()) ) && centers.size() != 0) {
                int index = rnd.nextInt(centers.size());
                myMove = centers.get(index);
            }
        }
        // keep rushing unless blocked in line of 5, in that case mcts
        else if(turn < 4) {
            if (turn == 2) {
                possibleFives = findFiveArray(boardState, mypiece);
            }
            else {
                updateFiveArray(boardState);
            }

            // no possible win with lines of 5
            if (possibleFives.isEmpty()) {
                myMove = postrush(boardState);
            }
            else {
                Line line = possibleFives.get(0);

                if (islineFilledOrEmpty(line, "filled") && possibleFives.size()==2) {
                    line = possibleFives.get(1);
                }

                myMove = pickRnd(line, boardState);
            }
        }
        // mcts if game keeps going from there
        else {
            myMove = postrush(boardState);
        }

        return myMove;
    }

    private PentagoMove postrush(PentagoBoardState boardState) {
        PentagoMove move = hasWinMove(boardState);

        if (move == null) {
            MCTS mcts = new MCTS();
            move = mcts.mcts(boardState,boardState.getTurnPlayer());
        }
        return move;
    }

    private ArrayList<Line> findFiveArray(PentagoBoardState boardState, PentagoBoardState.Piece piece) {
        ArrayList<Line> possibleFives = new ArrayList<>();

        if (boardState.getPieceAt(1,1) == piece) {
            if (boardState.getPieceAt(1,4) == piece) {
                // board is
                // x x
                //
                possibleFives.add(new Line(boardState,1, 0, "ht", 5));
                possibleFives.add(new Line(boardState,1, 1, "ht", 5));
            }
            else if (boardState.getPieceAt(4,4) == piece) {
                // board is
                // x
                //   x
                possibleFives.add(new Line(boardState,0, 0, "d", 5));
                possibleFives.add(new Line(boardState,1, 1, "d", 5));
            }
            else {
                // board is
                // x
                // x
                possibleFives.add(new Line(boardState,0, 1, "vl", 5));
                possibleFives.add(new Line(boardState,1, 1, "vl", 5));
            }
        }
        else if (boardState.getPieceAt(1,4) == piece) {
            if (boardState.getPieceAt(4,4) == piece) {
                // board is
                //   x
                //   x
                possibleFives.add(new Line(boardState,0, 4, "vr", 5));
                possibleFives.add(new Line(boardState,1, 4, "vr", 5));
            }
            else {
                // board is
                //   x
                // x
                possibleFives.add(new Line(boardState,0, 5, "p", 5));
                possibleFives.add(new Line(boardState,1, 4, "p", 5));
            }
        }
        else if (boardState.getPieceAt(4,1) == piece && boardState.getPieceAt(4,4) == piece){
            // board is
            //
            // x x
            possibleFives.add(new Line(boardState,4, 0, "hb", 5));
            possibleFives.add(new Line(boardState,4, 1, "hb", 5));
        }
        return possibleFives;
    }

    private void updateFiveArray(PentagoBoardState boardState) {
        boolean nofivewin = false;
        for (Line line : possibleFives) {
            line.updateLine(boardState);

            int white = 0;
            int black = 0;
            for (Spot spot : line.getLine()) {
                if (spot.getPlayer() == PentagoBoardState.Piece.WHITE) {
                    white++;
                }
                else if (spot.getPlayer() == PentagoBoardState.Piece.BLACK) {
                    black++;
                }
            }

            if (boardState.getTurnPlayer() == PentagoBoardState.WHITE && black >= 2 && white < placedPieces - 1) {
                nofivewin = true;
                break;
            }
            else if (boardState.getTurnPlayer() == PentagoBoardState.BLACK && white >= 2 && black < placedPieces - 1) {
                nofivewin = true;
                break;
            }

            if (boardState.getTurnPlayer() == PentagoBoardState.WHITE && white > placedPieces) {
                placedPieces = white;
            }
            else if (boardState.getTurnPlayer() == PentagoBoardState.BLACK && black > placedPieces) {
                placedPieces = black;
            }
        }

        // if in some permutation of a line there are 2 of the opponent's pieces, it's impossible to win on that line, abandon
        if (nofivewin) {
            possibleFives = new ArrayList<>();
        }
    }

    private boolean islineFilledOrEmpty(Line line, String cond) {
        for (Spot spot : line.getLine()) {
            if (cond == "filled") {
                if (spot.getPlayer() == PentagoBoardState.Piece.EMPTY) {
                    return false;
                }
            }
            else if (cond == "empty") {
                if (spot.getPlayer() == oppiece) {
                    return false;
                }
            }
        }
        return true;
    }

    private PentagoMove pickRnd(Line line, PentagoBoardState boardState) {
        PentagoBoardState.Quadrant q1 = getSwapQ(line, boardState).getKey();
        PentagoBoardState.Quadrant q2 = getSwapQ(line, boardState).getValue();

        PentagoBoardState cpb = (PentagoBoardState)boardState.clone();

        PentagoMove uselessMove = chooseRandomMove(cpb, q1, q2);
        PentagoMove goodSwap = new PentagoMove(uselessMove.getMoveCoord().getX(), uselessMove.getMoveCoord().getY(), q1, q2, boardState.getTurnPlayer());

        cpb.processMove(goodSwap);

        Line cpl = new Line(cpb, line.getLine().get(0).getX(), line.getLine().get(0).getY(), line.getDirection(), 5);

        ArrayList<Spot> availMoves = cpl.getAvailMoves();

        if (availMoves.size() == 0) {
            possibleFives = new ArrayList<>();
            return postrush(boardState);
        }

        int index = rnd.nextInt(availMoves.size());

        int xoffset = 0;
        int yoffset = 0;

        int x = availMoves.get(index).getX();
        int y = availMoves.get(index).getY();

        if (isInQS(x, y, q1, q2)) {
            switch (line.getDirection()) {
                case "ht":
                    if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = 3;
                        yoffset = 3;

                    } else if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = 3;
                        yoffset = -3;
                    }
                    break;
                case "hb":
                    if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = -3;
                        yoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = -3;
                        yoffset = 3;
                    }
                    break;
                case "vl":
                    if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)) {
                        yoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = 3;
                        yoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = -3;
                        yoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        yoffset = 3;
                    }
                    break;
                case "vr":
                    if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)) {
                        yoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = -3;
                        yoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = 3;
                        yoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        yoffset = -3;
                    }
                    break;
                case "d":
                    if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)) {
                        yoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        yoffset = -3;
                    }
                    break;
                case "p":
                    if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)) {
                        yoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TL || q2 == PentagoBoardState.Quadrant.TL)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        xoffset = -3;
                    } else if ((q1 == PentagoBoardState.Quadrant.TR || q2 == PentagoBoardState.Quadrant.TR)
                            && (q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)) {
                        xoffset = 3;
                    } else if ((q1 == PentagoBoardState.Quadrant.BR || q2 == PentagoBoardState.Quadrant.BR)
                            && (q1 == PentagoBoardState.Quadrant.BL || q2 == PentagoBoardState.Quadrant.BL)) {
                        yoffset = 3;
                    }
                    break;
            }
        }

        return new PentagoMove(x+xoffset, y+yoffset, q1, q2, boardState.getTurnPlayer());
    }

    private AbstractMap.SimpleEntry<PentagoBoardState.Quadrant, PentagoBoardState.Quadrant> getSwapQ(Line line, PentagoBoardState boardState) {
        AbstractMap.SimpleEntry<PentagoBoardState.Quadrant, PentagoBoardState.Quadrant> swapQ;

        switch (line.getDirection()) {
            case "ht":
                if (boardState.getPieceAt(1,1) == mypiece) {
                    if (boardState.getPieceAt(4,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BR, PentagoBoardState.Quadrant.TR);
                    }
                    else if (boardState.getPieceAt(4,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TR);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR);
                    }
                }
                else {
                    if (boardState.getPieceAt(4,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR);
                    }
                }
                break;
            case "hb":
                if (boardState.getPieceAt(4,1) == mypiece) {
                    if (boardState.getPieceAt(4,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR);
                    }
                    else if (boardState.getPieceAt(1,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BR, PentagoBoardState.Quadrant.TR);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR);
                    }
                }
                else {
                    if (boardState.getPieceAt(1,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL);
                    }
                }
                break;
            case "vl":
                if (boardState.getPieceAt(1,1) == mypiece) {
                    if (boardState.getPieceAt(4,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BR, PentagoBoardState.Quadrant.TR);
                    }
                    else if (boardState.getPieceAt(4,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TR);
                    }
                }
                else {
                    if (boardState.getPieceAt(1,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.TL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR);
                    }
                }
                break;
            case "vr":
                if (boardState.getPieceAt(1,4) == mypiece) {
                    if (boardState.getPieceAt(4,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TL);
                    }
                    else if (boardState.getPieceAt(4,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BR, PentagoBoardState.Quadrant.BL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR);
                    }
                }
                else {
                    if (boardState.getPieceAt(1,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.TL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL);
                    }
                }
                break;
            case "d":
                if (boardState.getPieceAt(1,1) == mypiece) {
                    if (boardState.getPieceAt(4,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TR);
                    }
                    else if (boardState.getPieceAt(1,4) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BR, PentagoBoardState.Quadrant.TR);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR);
                    }
                }
                else {
                    if (boardState.getPieceAt(4,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR);
                    }
                }
                break;
            case "p":
                if (boardState.getPieceAt(1,4) == mypiece) {
                    if (boardState.getPieceAt(4,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BR, PentagoBoardState.Quadrant.TL);
                    }
                    else if (boardState.getPieceAt(1,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.TL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR);
                    }
                }
                else {
                    if (boardState.getPieceAt(1,1) == mypiece) {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.TL);
                    }
                    else {
                        swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR);
                    }
                }
                break;
            default:
                swapQ = new AbstractMap.SimpleEntry<>(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR);
                break;
        }

        return swapQ;
    }

    private PentagoMove chooseRandomMove(PentagoBoardState boardState, PentagoBoardState.Quadrant q1, PentagoBoardState.Quadrant q2) {
        ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
        ArrayList<PentagoMove> toDel = new ArrayList<>();

        int xl1;
        int xu1;
        int yl1;
        int yu1;
        int xl2;
        int xu2;
        int yl2;
        int yu2;

        switch (q1) {
            case TL:
                xl1 = 0;
                xu1 = 2;
                yl1 = 0;
                yu1 = 2;
                break;
            case TR:
                xl1 = 0;
                xu1 = 2;
                yl1 = 3;
                yu1 = 5;
                break;
            case BL:
                xl1 = 3;
                xu1 = 5;
                yl1 = 0;
                yu1 = 2;
                break;
            case BR:
                xl1 = 3;
                xu1 = 5;
                yl1 = 3;
                yu1 = 5;
                break;
            default:
                xl1 = 0;
                xu1 = 0;
                yl1 = 0;
                yu1 = 0;
        }

        switch (q2) {
            case TL:
                xl2 = 0;
                xu2 = 2;
                yl2 = 0;
                yu2 = 2;
                break;
            case TR:
                xl2 = 0;
                xu2 = 2;
                yl2 = 3;
                yu2 = 5;
                break;
            case BL:
                xl2 = 3;
                xu2 = 5;
                yl2 = 0;
                yu2 = 2;
                break;
            case BR:
                xl2 = 3;
                xu2 = 5;
                yl2 = 3;
                yu2 = 5;
                break;
            default:
                xl2 = -1;
                xu2 = -1;
                yl2 = -1;
                yu2 = -1;
        }

        for (PentagoMove move : moves) {
            if (((move.getMoveCoord().getX() >= xl1 && move.getMoveCoord().getX() <= xu1) && (move.getMoveCoord().getY() >= yl1 && move.getMoveCoord().getY() <= yu1))
                    || ((move.getMoveCoord().getX() >= xl2 && move.getMoveCoord().getX() <= xu2) && (move.getMoveCoord().getY() >= yl2 && move.getMoveCoord().getY() <= yu2))) {
                toDel.add(move);
            }
        }

        for (PentagoMove move : toDel) {
            moves.remove(move);
        }

        int index = rnd.nextInt(moves.size());
        return moves.get(index);
    }

    private boolean isInQS (int x, int y, PentagoBoardState.Quadrant q1, PentagoBoardState.Quadrant q2) {
        int xl1;
        int xu1;
        int yl1;
        int yu1;
        int xl2;
        int xu2;
        int yl2;
        int yu2;

        switch (q1) {
            case TL:
                xl1 = 0;
                xu1 = 2;
                yl1 = 0;
                yu1 = 2;
                break;
            case TR:
                xl1 = 0;
                xu1 = 2;
                yl1 = 3;
                yu1 = 5;
                break;
            case BL:
                xl1 = 3;
                xu1 = 5;
                yl1 = 0;
                yu1 = 2;
                break;
            case BR:
                xl1 = 3;
                xu1 = 5;
                yl1 = 3;
                yu1 = 5;
                break;
            default:
                xl1 = 0;
                xu1 = 0;
                yl1 = 0;
                yu1 = 0;
        }

        switch (q2) {
            case TL:
                xl2 = 0;
                xu2 = 2;
                yl2 = 0;
                yu2 = 2;
                break;
            case TR:
                xl2 = 0;
                xu2 = 2;
                yl2 = 3;
                yu2 = 5;
                break;
            case BL:
                xl2 = 3;
                xu2 = 5;
                yl2 = 0;
                yu2 = 2;
                break;
            case BR:
                xl2 = 3;
                xu2 = 5;
                yl2 = 3;
                yu2 = 5;
                break;
            default:
                xl2 = -1;
                xu2 = -1;
                yl2 = -1;
                yu2 = -1;
        }

        if (((x >= xl1 && x <= xu1) && (y >= yl1 && y <= yu1)) || ((x >= xl2 && x <= xu2) && (y >= yl2 && y <= yu2))) {
            return true;
        }
        else {
            return false;
        }
    }

    private PentagoMove hasWinMove (PentagoBoardState boardState) {
        for (PentagoMove move : boardState.getAllLegalMoves()) {
            PentagoBoardState cp = (PentagoBoardState)boardState.clone();
            cp.processMove(move);
            if (cp.getWinner() == boardState.getTurnPlayer()) {
                return move;
            }
        }
        return null;
    }

    private AbstractMap.SimpleEntry<Boolean, ArrayList<Line>> otherPlayerNoRush(PentagoBoardState boardState) {
        AbstractMap.SimpleEntry<Boolean, ArrayList<Line>> opNoRush = new AbstractMap.SimpleEntry<>(true, null);

        ArrayList<Line> opLines = new ArrayList<>();

        ArrayList<Spot> points = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (i == 0) {
                    opLines.add(new Line(boardState, i, j, "vl", 6));
                }
                if (j == 0) {
                    opLines.add(new Line(boardState, i, j, "ht", 6));
                }
                if (boardState.getPieceAt(i,j) == oppiece) {
                    points.add(new Spot(i, j, oppiece));
                }
            }
        }

        ArrayList<Line> empty = new ArrayList<>();

        for (Line line: opLines) {
            if (islineFilledOrEmpty(line, "empty")) {
                empty.add(line);
            }
        }

        for (Line line: empty) {
            opLines.remove(line);
        }

        ArrayList<Integer> xs = new ArrayList<>();
        ArrayList<Integer> ys = new ArrayList<>();

        for (Spot spot : points) {
            if (!xs.contains(spot.getX())) {
                xs.add(spot.getX());
            }
            if (!ys.contains(spot.getY())) {
                ys.add(spot.getY());
            }
        }

        // op's pieces are all in a line, horizontally/vertically or diagonally
        if (usedQuadrants(points) < 3 && (
                (xs.size() == 3 && ys.size() == 1) || (xs.size() == 1 && ys.size() == 3)
                || (xs.size() == 2 && Math.abs(xs.get(0) - xs.get(1)) == 3 && ys.size() == 2 && Math.abs(ys.get(0) - ys.get(1)) < 3)
                || (xs.size() == 2 && Math.abs(xs.get(0) - xs.get(1)) < 3 && ys.size() == 2 && Math.abs(ys.get(0) - ys.get(1)) == 3)
                || (xs.size() == 3 && ys.size() == 2 && Math.abs(ys.get(0) - ys.get(1)) == 3)
                || (xs.size() == 2 && Math.abs(xs.get(0) - xs.get(1)) == 3 && ys.size() == 3)
                || (isPointsDiagonal(points)))) {
            opNoRush = new AbstractMap.SimpleEntry<>(false, opLines);
        }

        return opNoRush;
    }

    private boolean isPointsDiagonal(ArrayList<Spot> spots) {
        boolean diagonal = true;

        if (!specialDiagonal(spots)) {
            for (int i = 0; i < spots.size()-1; i++) {
                Spot piece = spots.get(i);
                Spot next = spots.get(i+1);
                if (Math.abs(next.getX()-piece.getX()) != Math.abs(next.getY()-piece.getY())) {
                    diagonal = false;
                    break;
                }
            }
        }

        return diagonal;
    }

    private int usedQuadrants(ArrayList<Spot> pieces) {
        ArrayList quadrants = new ArrayList<>();
        for(Spot spot : pieces) {
            if (spot.getX() >= 0 && spot.getX() <= 2) {
                if (spot.getY() >= 0 && spot.getY() <= 2) {
                    if (!quadrants.contains("TL")) {
                        quadrants.add("TL");
                    }
                }
                else {
                    if (!quadrants.contains("TR")) {
                        quadrants.add("TR");
                    }
                }
            }
            else {
                if (spot.getY() >= 0 && spot.getY() <= 2) {
                    if (!quadrants.contains("BL")) {
                        quadrants.add("BL");
                    }
                }
                else {
                    if (!quadrants.contains("BR")) {
                        quadrants.add("BR");
                    }
                }
            }
        }

        return quadrants.size();
    }

    private boolean specialDiagonal(ArrayList<Spot> spots) {
        ArrayList<Spot> centers = new ArrayList<>();
        ArrayList<Spot> extra = new ArrayList<>();
        ArrayList<Spot> corners = new ArrayList<>();
        for (Spot spot : spots) {
            if (spot.getX() == 1) {
                if (spot.getY() == 1) {
                    centers.add(spot);
                    continue;
                }
                else if (spot.getY() == 4) {
                    centers.add(spot);
                    continue;
                }
            }
            else if (spot.getX() == 4) {
                if (spot.getY() == 1) {
                    centers.add(spot);
                    continue;
                }
                else if (spot.getY() == 4) {
                    centers.add(spot);
                    continue;
                }
            }
            extra.add(spot);
        }

        for (Spot spot : centers) {
            corners.add(new Spot(spot.getX()-1, spot.getY()-1, oppiece));
            corners.add(new Spot(spot.getX()+1, spot.getY()-1, oppiece));
            corners.add(new Spot(spot.getX()-1, spot.getY()+1, oppiece));
            corners.add(new Spot(spot.getX()+1, spot.getY()+1, oppiece));
        }

        if (centers.size()==2 && listContainsSpot(corners, extra.get(0))) {
            return true;
        }

        return false;
    }

    private boolean listContainsSpot (ArrayList<Spot> list, Spot tile) {
        for (Spot spot : list) {
            if (spot.getX() == tile.getX() && spot.getY() == tile.getY() && spot.getPlayer() == spot.getPlayer()) {
                return true;
            }
        }
        return false;
    }

    private PentagoMove block (ArrayList<Line> lines, PentagoBoardState boardState) {
        PentagoMove moveToBlock = null;
        Line mostpieces = null;

        int x = -1;
        int y = -1;
        PentagoBoardState.Quadrant q1 = PentagoBoardState.Quadrant.TL;
        PentagoBoardState.Quadrant q2 = PentagoBoardState.Quadrant.BR;

        ArrayList<Spot> oppieces = new ArrayList<>();
        for (Line line : lines) {
            if (line.getLine().get(0).getY() == 0) {
                for (Spot spot : line.getLine()) {
                    if (spot.getPlayer() == oppiece) {
                        oppieces.add(spot);
                    }
                }
            }
        }

        if (specialDiagonal(oppieces)) {

            ArrayList<Spot> centers = new ArrayList<>();
            ArrayList<Spot> extra = new ArrayList<>();
            for (Spot spot : oppieces) {
                if (spot.getX() == 1) {
                    if (spot.getY() == 1) {
                        centers.add(spot);
                        continue;
                    }
                    else if (spot.getY() == 4) {
                        centers.add(spot);
                        continue;
                    }
                }
                else if (spot.getX() == 4) {
                    if (spot.getY() == 1) {
                        centers.add(spot);
                        continue;
                    }
                    else if (spot.getY() == 4) {
                        centers.add(spot);
                        continue;
                    }
                }
                extra.add(spot);
            }

            ArrayList<Spot> corners1 = new ArrayList<>();
            ArrayList<Spot> corners2 = new ArrayList<>();

            int xstep;
            int ystep;


            corners1.add(new Spot(centers.get(0).getX()-1, centers.get(0).getY()-1, oppiece));
            corners1.add(new Spot(centers.get(0).getX()+1, centers.get(0).getY()-1, oppiece));
            corners1.add(new Spot(centers.get(0).getX()-1, centers.get(0).getY()+1, oppiece));
            corners1.add(new Spot(centers.get(0).getX()+1, centers.get(0).getY()+1, oppiece));


            corners2.add(new Spot(centers.get(1).getX()-1, centers.get(1).getY()-1, oppiece));
            corners2.add(new Spot(centers.get(1).getX()+1, centers.get(1).getY()-1, oppiece));
            corners2.add(new Spot(centers.get(1).getX()-1, centers.get(1).getY()+1, oppiece));
            corners2.add(new Spot(centers.get(1).getX()+1, centers.get(1).getY()+1, oppiece));



            if (listContainsSpot(corners1, extra.get(0))) {
                xstep = centers.get(0).getX() - extra.get(0).getX();
                ystep = centers.get(0).getY() - extra.get(0).getY();

                x = centers.get(0).getX() + xstep;
                y = centers.get(0).getY() + ystep;
            }
            else if (listContainsSpot(corners2, extra.get(0))) {
                xstep = centers.get(1).getX() - extra.get(0).getX();
                ystep = centers.get(1).getY() - extra.get(0).getY();
                x = centers.get(1).getX() + xstep;
                y = centers.get(1).getY() + ystep;
            }

            moveToBlock = new PentagoMove(x, y, q1, q2, boardState.getTurnPlayer());
        }
        else if (lines.size() == 6) {
            ArrayList<Spot> pieces = new ArrayList<>();
            for (Line line : lines) {
                for (Spot spot: line.getLine()) {
                    if (spot.getPlayer() == oppiece) {
                        pieces.add(spot);
                    }
                }
            }

            Spot a = pieces.get(0);
            Spot b = pieces.get(1);

            int stepx = b.getX()-a.getX();
            int stepy = b.getY()-a.getY();

            if (stepx == stepy) {
                if (stepx == 2) {
                    x = a.getX() + 1;
                    y = a.getY() + 1;
                }
                else if (stepx == 1) {
                    if (boardState.getPieceAt(a.getX()+2, a.getY()+2) == PentagoBoardState.Piece.EMPTY) {
                        x = a.getX()+2;
                        y = a.getY()+2;
                    }
                    else if (boardState.getPieceAt(a.getX()-1, a.getY()-1) == PentagoBoardState.Piece.EMPTY) {
                        x = a.getX()-1;
                        y = a.getY()-1;
                    }
                }

                q1 = PentagoBoardState.Quadrant.TR;
                q2 = PentagoBoardState.Quadrant.BL;
            }
            else if (stepx == -stepy) {
                if (Math.abs(stepx) == 2) {
                    x = a.getX() + 1;
                    y = a.getY() - 1;
                }
                else if (Math.abs(stepx) == 1) {
                    if (boardState.getPieceAt(a.getX()+2, a.getY()-2) == PentagoBoardState.Piece.EMPTY) {
                        x = a.getX()+2;
                        y = a.getY()-2;
                    }
                    else if (boardState.getPieceAt(a.getX()-1, a.getY()+1) == PentagoBoardState.Piece.EMPTY) {
                        x = a.getX()-1;
                        y = a.getY()+1;
                    }
                }

                q1 = PentagoBoardState.Quadrant.TL;
                q2 = PentagoBoardState.Quadrant.BR;
            }

            moveToBlock = new PentagoMove(x, y, q1, q2, boardState.getTurnPlayer());
        }
        else {
            for (Line line : lines) {
                int opp = 0;
                for (Spot spot: line.getLine()) {
                    if (spot.getPlayer() == oppiece) {
                        opp++;
                    }
                }
                if (opp == 2 || opp == 3) {
                    mostpieces = line;
                    break;
                }
            }
            if (mostpieces != null) {
                ArrayList<Spot> lpieces = new ArrayList<>();

                for (Spot spot : mostpieces.getLine()) {
                    if (spot.getPlayer() == oppiece) {
                        lpieces.add(spot);
                    }
                }

                if ((lpieces.get(0).getY()-lpieces.get(1).getY() == 0)) {
                    if (lpieces.get(0).getX() < 3 && lpieces.get(1).getX() < 3 ) {
                        if (mostpieces.getLine().get(0).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 0;
                        }
                        else if (mostpieces.getLine().get(1).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 1;
                        }
                        else if (mostpieces.getLine().get(2).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 2;
                        }
                        else if (mostpieces.getLine().get(3).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 3;
                        }

                        q1 = PentagoBoardState.Quadrant.TL;
                        q2 = PentagoBoardState.Quadrant.BL;
                    }
                    else {
                        if (mostpieces.getLine().get(3).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 3;
                        }
                        else if (mostpieces.getLine().get(4).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 4;
                        }
                        else if (mostpieces.getLine().get(5).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 5;
                        }
                        else if (mostpieces.getLine().get(2).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = lpieces.get(0).getY();
                            x = 2;
                        }
                        q1 = PentagoBoardState.Quadrant.TL;
                        q2 = PentagoBoardState.Quadrant.TR;
                    }
                }
                else if ((lpieces.get(0).getX()-lpieces.get(1).getX() == 0)) {
                    if (lpieces.get(0).getY() < 3 && lpieces.get(1).getY() < 3 ) {
                        if (mostpieces.getLine().get(0).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 0;
                            x = lpieces.get(0).getX();
                        }
                        else if (mostpieces.getLine().get(1).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 1;
                            x = lpieces.get(0).getX();
                        }
                        else if (mostpieces.getLine().get(2).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 2;
                            x = lpieces.get(0).getX();
                        }
                        else if (mostpieces.getLine().get(3).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 3;
                            x = lpieces.get(0).getX();
                        }
                    }
                    else {
                        if (mostpieces.getLine().get(3).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 3;
                            x = lpieces.get(0).getX();
                        }
                        else if (mostpieces.getLine().get(4).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 4;
                            x = lpieces.get(0).getX();
                        }
                        else if (mostpieces.getLine().get(5).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 5;
                            x = lpieces.get(0).getX();
                        }
                        else if (mostpieces.getLine().get(2).getPlayer() == PentagoBoardState.Piece.EMPTY) {
                            y = 2;
                            x = lpieces.get(0).getX();
                        }
                    }
                }

                moveToBlock = new PentagoMove(x, y, q1, q2, boardState.getTurnPlayer());

            }

        }

        return moveToBlock;
    }

    private PentagoMove secondBlock (PentagoBoardState boardState) {
        PentagoMove myMove = null;

        for (int x = 1; x < 5; x++) {
            for (int y = 1; y < 5; y++) {
                if ((x == 1 || x == 4) && (y == 1 || y == 4)) {
                    if (boardState.getPieceAt(x, y) == oppiece) {
                        for (int i = -1; i < 1; i++) {
                            for (int j = -1; j < 2; j++) {
                                if(i != 0 || j != 0) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && boardState.getPieceAt(x-i, y-j) == oppiece ) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                            }
                        }
                    }
                    else if (boardState.getPieceAt(x,y) == PentagoBoardState.Piece.EMPTY) {
                        for (int i = -1; i < 2; i++) {
                            for (int j = -1; j < 2; j++) {
                                if (i == -1 && j == -1) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && ( (boardState.getPieceAt(x,y+j) == oppiece && boardState.getPieceAt(x+1,y+j) == oppiece)
                                                || (boardState.getPieceAt(x+i,y) == oppiece && boardState.getPieceAt(x+i,y+1) == oppiece))) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                                else if (i == 0 && j == -1) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && boardState.getPieceAt(x-1,y+j) == oppiece && boardState.getPieceAt(x+1,y+j) == oppiece) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                                else if (i == 1 && j == -1) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && ( (boardState.getPieceAt(x,y+j) == oppiece && boardState.getPieceAt(x-1,y+j) == oppiece)
                                            || (boardState.getPieceAt(x+i,y) == oppiece && boardState.getPieceAt(x+i,y+1) == oppiece))) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                                else if (i == -1 && j == 0) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && boardState.getPieceAt(x+i,y-1) == oppiece && boardState.getPieceAt(x+i,y+1) == oppiece) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                                else if (i == 1 && j == 0) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && boardState.getPieceAt(x+i,y-1) == oppiece && boardState.getPieceAt(x+i,y+1) == oppiece) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                                else if (i == -1 && j == 1) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && ( (boardState.getPieceAt(x,y+j) == oppiece && boardState.getPieceAt(x+1,y+j) == oppiece)
                                            || (boardState.getPieceAt(x+i,y) == oppiece && boardState.getPieceAt(x+i,y-1) == oppiece))) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                                else if (i == 0 && j == 1) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && boardState.getPieceAt(x-1,y+j) == oppiece && boardState.getPieceAt(x+1,y+j) == oppiece) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }
                                else if (i == 1 && j == 1) {
                                    if (boardState.getPieceAt(x+i, y+j) == PentagoBoardState.Piece.EMPTY
                                            && ( (boardState.getPieceAt(x,y+j) == oppiece && boardState.getPieceAt(x-1,y+j) == oppiece)
                                            || (boardState.getPieceAt(x+i,y) == oppiece && boardState.getPieceAt(x+i,y-1) == oppiece))) {
                                        myMove = new PentagoMove(x+i, y+j, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, boardState.getTurnPlayer());
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        return myMove;
    }
}
