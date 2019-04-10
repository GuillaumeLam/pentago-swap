import csv

total = 0;
draw = 0;
mctswins = 0;
abwins = 0;
mctsfirst = 0;
mctsfirstwins = 0;
abfirst = 0;
abfirstwins = 0;

with open('./logs/outcomes.txt', newline='') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
    for row in spamreader:
        print(row)
        total = total + 1
        if row[3] == 'GAMEOVER DRAW':
            draw = draw + 1
        else:
            if row[1] == 'mcts':
                mctsfirst = mctsfirst + 1
                if row[3] == '0':
                    mctswins = mctswins + 1
                    mctsfirstwins = mctsfirstwins + 1
                else:
                    abwins = abwins + 1
            else:
                abfirst = abfirst + 1
                if row[3] == '0':
                    abwins = abwins + 1
                    abfirstwins = abfirstwins + 1
                else:
                    mctswins = mctswins + 1

print("MCTS won " + str(100 * mctswins/total) +"%, student won " +str(100 * abwins/total)+"%, and draw " +str(100 * draw/total)+"%")
print("MCTS went first " + str(100 * mctsfirst/total) +"% and student went first " + str(100 * abfirst/total) + "%")
print("MCTS won " + str(100 * mctsfirstwins/mctswins) + "% by going first, and student won " + str(100 * abfirstwins/abwins) + "% by going first")
