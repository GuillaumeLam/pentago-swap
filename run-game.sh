#!/bin/bash
rm -rf ./logs
rm -rf ./bin
ant compile;
java -cp bin autoplay.Autoplay 100 student_player.MCTSPlayer student_player.RushPlayer;
#java -cp bin autoplay.Autoplay 100 student_player.AlphaBetaPlayer pentago_swap.RandomPentagoPlayer;
