import java.util.Arrays;

public class MiniMax extends ConnectFour {

    public MiniMax() {
        super();
    }

    public int[][][] createPossibleGameStates(int[][] currentBoardState, int tokenNum){
        int[][] possibleMoves = possibleArrayOfMoves(currentBoardState);
        int[][][] gameStates = new int[possibleMoves[0].length + 1][6][7];  //add an extra row to rank every gameState with
        //System.out.println("Number of Possible Game States: " + gameState.length);
        int ci = 0, cj = 0;
        int highestVal = -1000;
        int lowestVal = 1000;

        for(int i = 0; i < gameStates.length - 1; i++) { //42
            for (int j = 0; j < gameStates[i].length; j++) { //row: size = 6
                gameStates[i][j] = Arrays.copyOf(currentBoardState[j], currentBoardState[j].length);
            }
            gameStates[i][possibleMoves[0][i]][possibleMoves[1][i]] = tokenNum;
            //System.out.println("i: " + i + " ci: " + ci + " cj: " + cj);   //debug iterate
            gameStates[gameStates.length - 1][ci][cj] = rankGameState(gameStates[i], tokenNum);
            if(tokenNum == 1) {
                if(highestVal < gameStates[gameStates.length-1][ci][cj]) {
                    highestVal = gameStates[gameStates.length-1][ci][cj];
                }
            } else {
                if (lowestVal > gameStates[gameStates.length - 1][ci][cj]) {
                    lowestVal = gameStates[gameStates.length - 1][ci][cj];
                }
            }
            cj++;
            if(cj >= gameStates[0][0].length) {cj = 0; ci++;}
        }
        if(tokenNum == 1) {gameStates[gameStates.length - 1][5][6] = highestVal; }
        else {gameStates[gameStates.length - 1][5][6] = lowestVal;}

        /*
        //print GameState  // use to debug
        for(int i = 0; i < gameStates.length; i++) { //42
            System.out.println("\n Game State: " + i);     //debug
            if(i == gameStates.length-1) { System.out.println("Ranked");}  //debug

            for(int j = 0; j < gameStates[i].length; j++) { //6
                for(int k = 0; k < gameStates[i][j].length; k++) { //42
                    System.out.print(gameStates[i][j][k] + " ");
                }
                System.out.println();
            }

        }*/

        return gameStates;
    }

    public int rankGameState(int[][] boardState, int tokenNum){  //tokenNum determines whether to sum for -1s or 1s
        int[][] diags = getDiagonals(boardState);
        int[][] rows = getRows(boardState);
        int[][] cols = getCols(boardState);
        int importantValue;


        if(tokenNum == -1){   //minimize get 0
            importantValue = 10;
            for(int i = 0; i < diags.length; i++) {
                for(int j = 0; j < diags[i].length; j++) {
                    if(diags[i][j] < importantValue) {
                        importantValue = diags[i][j];
                    }
                }
            }
            for(int i = 0; i < rows.length; i++) {
                for(int j = 0; j < rows[i].length; j++) {
                    if(rows[i][j] < importantValue) {
                        importantValue = rows[i][j];
                    }
                }
            }
            for(int i = 0; i < cols.length; i++) {
                for(int j = 0; j < cols[i].length; j++) {
                    if(cols[i][j] < importantValue) {
                        importantValue = cols[i][j];
                    }
                }
            }
        } else {
            importantValue = -10;
            for(int i = 0; i < diags.length; i++) {
                for(int j = 0; j < diags[i].length; j++) {
                    if(diags[i][j] > importantValue) {
                        importantValue = diags[i][j];
                    }
                }
            }
            for(int i = 0; i < rows.length; i++) {
                for(int j = 0; j < rows[i].length; j++) {
                    if(rows[i][j] > importantValue) {
                        importantValue = rows[i][j];
                    }
                }
            }
            for(int i = 0; i < cols.length; i++) {
                for(int j = 0; j < cols[i].length; j++) {
                    if(cols[i][j] > importantValue) {
                        importantValue = cols[i][j];
                    }
                }
            }
        }
        return importantValue;
    }

}
