#!/bin/bash
ant compile;
java -cp bin autoplay.Autoplay 100 student_player.MCTSPlayer student_player.AlphaBetaPlayer;
java -cp bin autoplay.Autoplay 100 student_player.AlphaBetaPlayer student_player.MCTSPlayer;

