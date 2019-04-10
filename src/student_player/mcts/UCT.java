package student_player.mcts;

import student_player.mcts.MCTSStructure.Node;

import java.util.Collections;
import java.util.Comparator;

public class UCT {
    public static final double C = 1.41;
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        else {
            double value = ((double) nodeWinScore/ (double) nodeVisit) + C * Math.sqrt(Math.log(totalVisit)/ (double) nodeVisit);
            return value;
        }
    }

    public static Node findBestNodeWithUCT(Node node){
        int parentVisit = node.getState().getVisitCount();
        return Collections.max(
                node.getChildArray(),
                Comparator.comparing(c ->
                        uctValue(parentVisit, c.getState().getWinScore(), c.getState().getVisitCount())
                )
        );
    }
}
