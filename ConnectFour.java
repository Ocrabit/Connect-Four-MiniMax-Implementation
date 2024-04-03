import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import javax.swing.*;
import java.util.Random;

public class ConnectFour extends JPanel {
    int rows = 6;
    int cols = 7;
    int[][] board2 = new int[rows][cols];

    private boolean isRunning = false;
    private boolean game = false;
    boolean playAgain = false;
    private boolean singlePlayer = false;
    boolean easyMode = true;

    private final String[] possibleNames = {"Jim", "John", "Jessica", "Pablo", "March", "Edward", "Dr. Gaines"};
    private int rowMove, colMove; //The move that will be set by getMove() method
    private final String[] options = {"Two Player", "One Player"};
    private final String[] difficultyOptions = {"Easy", "Hard"};

    String[] player1 = {"-1", "player1"};  //player*[0] = token (-1 is "R"), player*[1] = name
    String[] player2 = {"1", "player2"};  //player*[0] = token (1 is "B"), player*[1] = name
    boolean playerChoosing;
    volatile boolean buttonUnclicked = true;
    private int numberOfTurnsAtPlay = 0;
    private MiniMax miniMax;
    public CardUI cardUI;


    public ConnectFour() {
        buildBoard();
        getMove();

    }

    void storeMiniMax(MiniMax miniMax){
        this.miniMax = miniMax;
    }

    public void gameLoop2(CardUI cardUI) {
        Random rand = new Random();
        this.cardUI = cardUI;
        cardUI.setMain(this);

        //printIntBoard();

        //cardUI.setDisplayedText("Would you like to play a game? Y/N");
        String answer = JOptionPane.showInputDialog(null, "Would you like to play a game? Y/N");

        //check if answer is yes
        if (answer != null && answer.equalsIgnoreCase("y")) {
            //System.out.println("Debug 1");
            isRunning = true;
            game = true;

            var playerSelection = JOptionPane.showOptionDialog(null, "Select one:", "Let's play a game!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (playerSelection == 1) {
                System.out.println("One");
                singlePlayer = true;
                setNames(1);
                player2[1] = possibleNames[rand.nextInt(0, possibleNames.length)];

                var difficultySelection = JOptionPane.showOptionDialog(null, "Choose your difficulty:", "Let's play a game!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, difficultyOptions, difficultyOptions[0]);
                easyMode = difficultySelection != 1;

                JOptionPane.showMessageDialog(null, "Now the game starts. " + player1[1] + " please go first! \n " +
                        "You will be playing against " + player2[1] + ".");

            } else {
                System.out.println("Two");
                setNames(2);
                JOptionPane.showMessageDialog(null, "Now the game starts. " + player1[1] + " please go first!");
            }
        }

        //start gameloop
        while (isRunning) {
            while (game) {
                //System.out.println("Started Game Loop");  //debug purposes
                getPlayerChoice(player1, cardUI);

                if (checkWinCondition() || cardUI.checkNoMorePieces()) {
                    isRunning = false;
                    printBoard();
                    break;
                }

                printBoard();

                if (!singlePlayer) {
                    getPlayerChoice(player2, cardUI);
                } else {
                    if(easyMode) {
                        getMove();
                        board2[rowMove][colMove] = Integer.parseInt(player2[0]);
                        placeToken(rowMove, colMove, Integer.parseInt(player2[0]), cardUI);
                    } else {
                        runMiniMax();
                    }
                }

                if (checkWinCondition() || cardUI.checkNoMorePieces()) {
                    isRunning = false;
                    printBoard();
                    break;
                }
                printBoard();
            }

            if (!playAgain) {
                JOptionPane.showMessageDialog(null, "Okay well I guess this is goodbye. :( Sad to see you go.");
                break;

            } else {
                if (JOptionPane.showConfirmDialog(null, "Would you like to change the current players?", "Message",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    setNames(1);
                }
                cardUI.resetEverything();
                buildBoard();
                numberOfTurnsAtPlay = -1;
                System.out.println("\n New Game \n");
                printBoard();

                isRunning = true;
                game = true;
                playAgain = false;
                JOptionPane.showMessageDialog(null, "A New Game Has Begun!", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        cardUI.dispose();
    }

    protected int[][] possibleArrayOfMoves(int[][] board3) {
        int[][] possibleMoves;

        int choiceCount = 0;
        int[] rowChoices = new int[42];
        int[] colChoices = new int[42];


        for (int i = 0; i < board3.length; i++) {
            for (int j = 0; j < board3[i].length; j++) {
                if (board3[i][j] == 0) {
                    rowChoices[choiceCount] = i;
                    colChoices[choiceCount] = j;
                    choiceCount++;
                    //System.out.println("ChoiceCount: " + choiceCount + " | i: " + i + " | j: " + j);
                }
            }
        }

        //Assign possibleMoves[][] size
        possibleMoves = new int[2][choiceCount];
        //System.out.println("Size of PossibleMoves is 2 x " + choiceCount);   //debug code

        //Assign row moves to possible
        for(int i = 0; i < choiceCount; i++) {
            possibleMoves[0][i] = rowChoices[i];
            possibleMoves[1][i] = colChoices[i];
        }
        /*//Assign col moves to possible
        for(int i = 0; i < choiceCount; i++) {

        }*/

        return possibleMoves;
    }

    private void getMove() {
        Random rand = new Random();
        int randomSelection;

        int choiceCount = 0;
        int[] rowChoices = new int[42];
        int[] colChoices = new int[42];

        //printIntBoard();

        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                if (board2[i][j] == 0) {
                    rowChoices[choiceCount] = i;
                    colChoices[choiceCount] = j;
                    choiceCount++;
                }
            }
        }

        /*     //Debug Possible Choices
        System.out.println("Possible Choices:");
        for(int i = 0; i < choiceCount; i++){
            System.out.println("Row: " + rowChoices[i] + " Col: " + colChoices[i]);
        }*/


        randomSelection = rand.nextInt(0, choiceCount);
        rowMove = rowChoices[randomSelection];
        colMove = colChoices[randomSelection];
    }

    //return true if a player wins
    protected boolean checkWinCondition() {
        if (checkRows()) {
            return true;
        } else if (checkCols()) {
            return true;
        } else return checkDiagonals();
    }

    protected boolean checkDiagonals() {
        int diagSkip = 0;

        int[][] diagTop = {
                {0, 0, 0}, //(0,0)
                {0, 0, 0}, //(0,1)
                {0, 0}, //(0,2)
                {0}, //(0,3) end of top row , left to right
                {0, 0},//(1,0)
                {0} //(2,0)
        };

        //Top down diagTop
        for (int z = 0; z < 4; z++) {
            //System.out.println("Next Diag " + z);
            for (int i = 0; i < board2.length; i++) {
                for (int j = diagSkip + z; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (i < 4) {
                        diagTop[z][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5 && z < 3) {
                        diagTop[z][1] += board2[i][j];
                    }
                    if (i > 1 && z < 2) {
                        diagTop[z][2] += board2[i][j];
                    }

                    diagSkip++;
                    j = board2[i].length;
                }
            }
            diagSkip = 0;
        }

        //Top down diagTop
        for (int z = 0; z < 2; z++) {
            //System.out.println("Next Diag -" + (z+1));
            for (int i = 1 + z; i < board2.length; i++) {
                for (int j = diagSkip; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (j < 4) {
                        diagTop[z + 4][0] += board2[i][j];
                    }
                    if (j > 0 && z < 1) {
                        diagTop[z + 4][1] += board2[i][j];
                    }


                    diagSkip++;
                    j = board2[i].length;
                }
            }
            diagSkip = 0;
        }

        //Check diagTop win
        for (int i = 0; i < diagTop.length; i++) {
            for (int j = 0; j < diagTop[i].length; j++) {
                if (Math.abs(diagTop[i][j]) == 4) {
                    System.out.println("Diagonal");
                    if (diagTop[i][j] == -4) {
                        winMessage(-1);
                    } else {
                        winMessage(1);
                    }
                    return true;
                }
            }
        }


        int[][] diagSide = {
                {0, 0, 0}, //(6,0)
                {0, 0, 0}, //(6,1)
                {0, 0}, //(6,2)
                {0}, //(6,3) end of top row , left to right
                {0, 0},//(5,0)
                {0} //(4,0)
        };

        //Bottom up diagSide
        for (int z = 0; z < 4; z++) {
            //System.out.println("Next Side Diag " + z);
            for (int i = board2.length - 1; i > -1; i--) {
                for (int j = diagSkip + z; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (i > 1) {
                        diagSide[z][0] += board2[i][j];
                    }
                    if (i < 5 && i > 0 && z < 3) {
                        diagSide[z][1] += board2[i][j];
                    }
                    if (i < 4 && z < 2) {
                        diagSide[z][2] += board2[i][j];
                    }

                    diagSkip++;
                    j = board2.length;
                }
            }
            diagSkip = 0;
        }

        //Bottom up diagSide
        for (int z = 0; z < 2; z++) {
            //System.out.println("Next Diag -" + (z+1));
            for (int i = (board2.length - 2) - z; i > -1; i--) {
                for (int j = diagSkip; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (j < 4) {
                        diagSide[z + 4][0] += board2[i][j];
                    }
                    if (j > 0 && z < 1) {
                        diagSide[z + 4][1] += board2[i][j];
                    }

                    diagSkip++;
                    j = board2[i].length;
                }
            }
            diagSkip = 0;
        }

        //Check diagSide win
        for (int i = 0; i < diagSide.length; i++) {
            for (int j = 0; j < diagSide[i].length; j++) {
                if (Math.abs(diagSide[i][j]) == 4) {
                    System.out.println("Diagonal");
                    if (diagSide[i][j] == -4) {
                        winMessage(-1);
                    } else {
                        winMessage(1);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean checkRows() {
        int[][] rows = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };

        //Sum Row Parts
        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                //Sum each section of 4 of each row
                if (j < 4) {
                    rows[i][0] += board2[i][j];
                }
                if (j > 0 && j < 5) {
                    rows[i][1] += board2[i][j];
                }
                if (j > 1 && j < 6) {
                    rows[i][2] += board2[i][j];
                }
                if (j > 2) {
                    rows[i][3] += board2[i][j];
                }
            }
        }

        //Check Row Win
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                if (Math.abs(rows[i][j]) == 4) {
                    System.out.println("Row");
                    if (rows[i][j] == -4) {
                        winMessage(-1);
                    } else {
                        winMessage(1);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean checkCols() {
        int[][] cols = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
        };

        //Sum Col Parts
        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                //Sum each section of 4 of each col
                if (j == 0) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 1) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 2) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 3) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 4) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 5) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
            }
        }

        //Check Col Win
        for (int i = 0; i < cols.length; i++) {
            for (int j = 0; j < cols[i].length; j++) {
                if (Math.abs(cols[i][j]) == 4) {
                    System.out.println("Column");
                    if (cols[i][j] == -4) {
                        winMessage(-1);
                    } else {
                        winMessage(1);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    protected int checkValidityOfMove(int row, int column) {
        int moveType = 0; //0 means valid move || 1 means spot already taken || 2 means index out of bounds

        if (row > 5 || column > 6) {
            moveType = 2; //Index out of bounds
        } else if (row == -1 || column == -1) {
            moveType = 3; //Entered a null box try again
        } else if (row == -2 || column == -2) {
            moveType = 4; //Entered a value below 0
        } else if (board2[row][column] != 0) {
            moveType = 1; // Spot already taken
        }

        return moveType;
    }

    protected void getPlayerChoice(String[] player, CardUI cardUI) {
        //System.out.println("Debug");
        int moveType;
        playerChoosing = true;
        while (playerChoosing) {
            int row, column;
            cardUI.setDisplayedText(" " + player[1] + " please select a tile coordinate to place token. \n For example: In a 4X4 Board, Row 2 Column 3 = [1,2] Index");
            //System.out.println(player[1] + " please select a tile coordinate to place token."); //change this
            //System.out.println("For example: In a 4X4 Board, Row 2 Column 3 = [1,2] Index");

            while (buttonUnclicked) {
                Thread.onSpinWait();
            }

            buttonUnclicked = true;

            row = Integer.parseInt(cardUI.getResponse(1));
            column = Integer.parseInt(cardUI.getResponse(2));

            moveType = checkValidityOfMove(row, column);
            if (moveType == 0) {
                board2[row][column] = Integer.parseInt(player[0]);
                placeToken(row, column, Integer.parseInt(player[0]), cardUI);
                playerChoosing = false;
            } else if (moveType == 1) {
                JOptionPane.showMessageDialog(null, "That place is already taken, Please try again with a new position!");
            } else if (moveType == 2 || moveType == 4) {
                JOptionPane.showMessageDialog(null, "You selected a place not on the board. PLease try again!!");
            } else {
                JOptionPane.showMessageDialog(null, "You accidentally forgot to place a value in one of the boxes above. Please try again.");
            }
        }

    }

    private void setNames(int numPlayers) {
        if (numPlayers == 1) {
            String tempName = JOptionPane.showInputDialog(null, "Enter Player One's Name");
            if (tempName == null) {
                tempName = "";
            }
            if (!tempName.isEmpty()) {
                player1[1] = tempName;
            }
        } else {
            String tempName = JOptionPane.showInputDialog(null, "Enter Player One's Name");
            if (tempName == null) {
                tempName = "";
            }
            if (!tempName.isEmpty()) {
                player1[1] = tempName;
            }
            tempName = JOptionPane.showInputDialog(null, "Enter Player Two's Name");
            if (tempName == null) {
                tempName = "";
            }
            if (!tempName.isEmpty()) {
                player2[1] = tempName;
            }
        }
    }

    public void placeToken(int row, int column, int token, CardUI cardUI) {
        //All tokens are offset by a pixel value of 20 x 20
        int x = 20;
        int y = 20;

        x += column * 120;
        y += row * 120;

        cardUI.placeTokens(x, y, token);
    }

    private void winMessage(int playerNum) {  //takes -1 or 1 | -1 -> Player 1 wins |  1 -> Player2 wins

        if (playerNum == -1) {
            JOptionPane.showMessageDialog(null, "Congratulations! " + player1[1] + " you just beat " + player2[1]
                    + " at Four In A Row");
            if (JOptionPane.showConfirmDialog(null, "Would you like to play again?", "Message",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                playAgain = true;
            }
        } else {
            if (!singlePlayer) {
                JOptionPane.showMessageDialog(null, "Too bad. " + player1[1] + " looks like you lost to " + player2[1] + " at Four In A Row");
                if (JOptionPane.showConfirmDialog(null, "Would you like to play again?", "Message",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    playAgain = true;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Congratulations! " + player2[1] + " you just beat " + player1[1]
                        + " at Four In A Row");
                if (JOptionPane.showConfirmDialog(null, "Would you like to play again?", "Message",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    playAgain = true;
                }
            }
        }
    }

    private void buildBoard() {
        for (int i = 0; i < board2.length; i++) {
            Arrays.fill(board2[i], 0);
        }
    }

    private void printBoard() {
        numberOfTurnsAtPlay++;
        System.out.println("Turn: " + numberOfTurnsAtPlay);

        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                if (board2[i][j] == -1) {
                    System.out.print("R ");
                } else if (board2[i][j] == 1) {
                    System.out.print("B ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printIntBoard() {
        System.out.println("Turn: " + numberOfTurnsAtPlay);

        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                System.out.print(board2[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printIntBoard(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    protected int[][] getDiagonals(int[][] board2) {
        int diagSkip = 0;

        int[][] diagTop = {
                {0, 0, 0}, //(0,0)
                {0, 0, 0}, //(0,1)
                {0, 0}, //(0,2)
                {0}, //(0,3) end of top row , left to right
                {0, 0},//(1,0)
                {0} //(2,0)
        };

        //Top down diagTop
        for (int z = 0; z < 4; z++) {
            //System.out.println("Next Diag " + z);
            for (int i = 0; i < board2.length; i++) {
                for (int j = diagSkip + z; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (i < 4) {
                        diagTop[z][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5 && z < 3) {
                        diagTop[z][1] += board2[i][j];
                    }
                    if (i > 1 && z < 2) {
                        diagTop[z][2] += board2[i][j];
                    }

                    diagSkip++;
                    j = board2[i].length;
                }
            }
            diagSkip = 0;
        }

        //Top down diagTop
        for (int z = 0; z < 2; z++) {
            //System.out.println("Next Diag -" + (z+1));
            for (int i = 1 + z; i < board2.length; i++) {
                for (int j = diagSkip; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (j < 4) {
                        diagTop[z + 4][0] += board2[i][j];
                    }
                    if (j > 0 && z < 1) {
                        diagTop[z + 4][1] += board2[i][j];
                    }


                    diagSkip++;
                    j = board2[i].length;
                }
            }
            diagSkip = 0;
        }


        int[][] diagSide = {
                {0, 0, 0}, //(6,0)
                {0, 0, 0}, //(6,1)
                {0, 0}, //(6,2)
                {0}, //(6,3) end of top row , left to right
                {0, 0},//(5,0)
                {0} //(4,0)
        };

        //Bottom up diagSide
        for (int z = 0; z < 4; z++) {
            //System.out.println("Next Side Diag " + z);
            for (int i = board2.length - 1; i > -1; i--) {
                for (int j = diagSkip + z; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (i > 1) {
                        diagSide[z][0] += board2[i][j];
                    }
                    if (i < 5 && i > 0 && z < 3) {
                        diagSide[z][1] += board2[i][j];
                    }
                    if (i < 4 && z < 2) {
                        diagSide[z][2] += board2[i][j];
                    }

                    diagSkip++;
                    j = board2.length;
                }
            }
            diagSkip = 0;
        }

        //Bottom up diagSide
        for (int z = 0; z < 2; z++) {
            //System.out.println("Next Diag -" + (z+1));
            for (int i = (board2.length - 2) - z; i > -1; i--) {
                for (int j = diagSkip; j < board2[i].length; j++) {
                    //System.out.println("i: " + i + " j: " + j);
                    //Sums the first 4 sets of diagonals
                    if (j < 4) {
                        diagSide[z + 4][0] += board2[i][j];
                    }
                    if (j > 0 && z < 1) {
                        diagSide[z + 4][1] += board2[i][j];
                    }

                    diagSkip++;
                    j = board2[i].length;
                }
            }
            diagSkip = 0;
        }
        int[][] diags = new int[diagTop.length + diagSide.length][];
        System.arraycopy(diagTop, 0, diags, 0, diagTop.length);
        System.arraycopy(diagSide, 0, diags, diagTop.length, diagSide.length);


        /*System.out.println("Diag Array: \n");    //Debug Diags
        for (int[] diag : diags) {
            for (int i : diag) {
                System.out.print(i + "");
            }
            System.out.println();
        }*/

        return diags;
    }

    protected int[][] getRows(int[][] board2) {
        int[][] rows = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };

        //Sum Row Parts
        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                //Sum each section of 4 of each row
                if (j < 4) {
                    rows[i][0] += board2[i][j];
                }
                if (j > 0 && j < 5) {
                    rows[i][1] += board2[i][j];
                }
                if (j > 1 && j < 6) {
                    rows[i][2] += board2[i][j];
                }
                if (j > 2) {
                    rows[i][3] += board2[i][j];
                }
            }
        }

        return rows;
    }

    protected int[][] getCols(int[][] board2) {
        int[][] cols = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
        };

        //Sum Col Parts
        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                //Sum each section of 4 of each col
                if (j == 0) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 1) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 2) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 3) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 4) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
                if (j == 5) {
                    if (i < 4) {
                        cols[j][0] += board2[i][j];
                    }
                    if (i > 0 && i < 5) {
                        cols[j][1] += board2[i][j];
                    }
                    if (i > 1) {
                        cols[j][2] += board2[i][j];
                    }
                }
            }
        }

        return cols;
    }

    protected int[] compareBoard(int[][] board) {   //this determines the location of the move it needs to make
        int[] posRowCol = new int[2];  //[0] = i | [1] = j
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board2[i][j] != board[i][j]){
                    posRowCol[0] = i;
                    posRowCol[1] = j;
                }
            }
        }
        return posRowCol;
    }
    protected int[] compareBoard(int[][] board2, int[][] board1) {   //this determines the location of the move it needs to make
        int[] posRowCol = new int[2];  //[0] = i | [1] = j
        for (int i = 0; i < board2.length; i++) {
            for (int j = 0; j < board2[i].length; j++) {
                if(board2[i][j] != board1[i][j]){
                    posRowCol[0] = i;
                    posRowCol[1] = j;
                }
            }
        }
        return posRowCol;
    }

    protected int randomPositionSelector(ArrayList<ArrayList<Integer>> pos) {
        Random rand = new Random();
        //System.out.println("size of rand: " + pos.getFirst().size()); //debug code
        return rand.nextInt(pos.getFirst().size());
    }

    public void runMiniMax() {
        if(numberOfTurnsAtPlay != 1) {
            boolean tryFirst = true;
            boolean trySecond = false;
            boolean tryThird = false;

            //Create dynamic arrays to store positions
            ArrayList<Integer> iValues = new ArrayList<>();
            ArrayList<Integer> jValues = new ArrayList<>();
            ArrayList<Integer> kValues = new ArrayList<>();
            ArrayList<ArrayList<Integer>> posArr = new ArrayList<>();

            //Initialize 1st set of gameStates
            int[][][] gameState_1st = miniMax.createPossibleGameStates(board2, 1); //Max
            int[][][] gameStateOptimized_1st;
            int n = gameState_1st.length - 1; //n is the # of possible moves

            //Create rest of 1st gameState Variables
            int highestVal_1st = -1000;
            int position_1st = 0;
            int[][] boardState_1st = new int[rows][cols];

            //Create 2nd gameState Variables
            int[][][][] gameState_2nd;
            int[][][] gameStateOptimized_2nd;
            int[][] gameStateRoot_2nd = new int[rows][cols];
            int lowestVal_2nd = 10;
            int position_2nd = 0;
            int[][] boardState_2nd = new int[rows][cols];

            //Create 3rd gameState Variables
            int[][][][] gameState_3rd;
            int[][][] gameStateRoot_3rd = new int[2][rows][cols]; // [0] = 1st | [1] = 2nd
            //int[][][] rankVals_3rd = new int[n][n - 1][n - 2];
            int highestVal_3rd = -1000;
            int position_3rd = 0;
            int[][] boardState_3rd = new int[rows][cols];
            ArrayList<int[][]> finalBoardSet = new ArrayList<>();

            if (tryFirst) {
                //Rank the values of the 1st set of gameStates
                /*for (int i = 0; i < gameState_1st.length; i++) {
                    rankVals_1st[i] = miniMax.rankGameState(gameState_1st[i], 1);
                    if (rankVals_1st[i] > highestVal_1st) {
                        highestVal_1st = rankVals_1st[i];
                    }
                }*/


                //set the highest value n is not n+1 because arrays are 0 - end not 1 - end
                highestVal_1st = gameState_1st[n][rows - 1][cols - 1];
                int count = 0;
                loop:
                for (int i = 0; i < gameState_1st[n].length; i++) {
                    for (int j = 0; j < gameState_1st[n][i].length; j++) {
                        //System.out.println("count: " + count + " i: " + i + " j: " + j);   //debug
                        if (count < n) {
                            if (highestVal_1st == gameState_1st[n][i][j]) {
                                iValues.add((i * cols) + j);
                            }
                        } else {
                            break loop;
                        }
                        count++;
                    }

                }

                posArr.add(iValues);
                position_1st = randomPositionSelector(posArr);

                //Get projected 2nd gameState and rank value
                //System.out.println("\nProjected 1st gameState: \n i: " + posArr.getFirst().get(position_1st) + "\n Value: " + highestVal_1st + "\nSelected board: "); //debug code
                boardState_1st = gameState_1st[posArr.getFirst().get(position_1st)];
                //printIntBoard(boardState_1st); //debug code

                //Set next step if no perfect move
                if (highestVal_1st != 4) {
                    finalBoardSet.add(boardState_1st);
                    trySecond = true;
                } else {
                    finalBoardSet.add(boardState_1st);
                }
            }

            //Do in main method
            //System.out.println("I val: " + iValues);  //debug code
            //Create new optimized gameState array
            gameStateOptimized_1st = new int[iValues.size()][rows][cols];
            int p = gameStateOptimized_1st.length;
            //System.out.println("------\np: " + p + "\n------");  //debug code
            gameState_2nd = new int[p][n][rows][cols]; //p is optimized len while n is number of moves left + 1 since there is an extra arr for ranked


            //then continue
            if (trySecond) {
                //Set new optimized array
                for (int i = 0; i < gameStateOptimized_1st.length; i++) {
                    for (int j = 0; j < gameStateOptimized_1st[i].length; j++) {
                        gameStateOptimized_1st[i][j] = Arrays.copyOf(gameState_1st[iValues.get(i)][j], gameState_1st[iValues.get(i)][j].length);
                    }
                }
                /*//print   // debug optimized gameStates
                for (int i = 0; i < gameStateOptimized_1st.length; i++) {
                    System.out.println("\nGameStateOptimized: " + i);
                    for (int j = 0; j < gameStateOptimized_1st[i].length; j++) {
                        for (int k = 0; k < gameStateOptimized_1st[i][j].length; k++) {
                            System.out.print(gameStateOptimized_1st[i][j][k] + " ");
                        }
                        System.out.println();
                    }
                }
                 */

                //Initialize 2nd set of gameStates
                for (int i = 0; i < gameState_2nd.length; i++) {
                    //System.out.println("\ngameState_2nd: " + i);   //list gamestates
                    gameState_2nd[i] = miniMax.createPossibleGameStates(gameStateOptimized_1st[i], -1);
                }

                //Clear previous values i, posArr
                iValues.clear();
                posArr.clear();

                /*
                System.out.println("\n Testing game rank place: ");   //debug code
                for (int i = 0; i < gameState_2nd.length; i++) {
                    for (int j = 0; j < 6; j++) {
                        for (int k = 0; k < 7; k++) {
                            System.out.print(gameState_2nd[i][n - 1][j][k] + " ");
                        }
                        System.out.println();
                    }
                }*/

                for (int i = 0; i < gameState_2nd.length; i++) {
                    if (gameState_2nd[i][n - 1][rows - 1][cols - 1] < lowestVal_2nd) {
                        lowestVal_2nd = gameState_2nd[i][n - 1][rows - 1][cols - 1];
                    }
                }
                //System.out.println("LowestVal: :" + lowestVal_2nd);  //debug code


                for (int i = 0; i < gameState_2nd.length; i++) {
                    int count = 0;
                    loop:
                    for (int j = 0; j < gameState_2nd[i][n - 1].length; j++) {
                        for (int k = 0; k < gameState_2nd[i][n - 1][j].length; k++) {
                            //System.out.println("count: " + count + " i: " + i + " j: " + j + " k: " + k + " lowest value " + lowestVal_2nd + " value: " + gameState_2nd[i][n - 1][j][k]);    //debug
                            if (count < n - 1) {
                                if (lowestVal_2nd == gameState_2nd[i][n - 1][j][k]) {
                                    iValues.add(i);
                                    jValues.add((j * cols) + k);
                                }
                            } else {
                                break loop;
                            }
                            count++;
                        }
                    }
                }

                posArr.add(iValues); //posArr.getFirst
                posArr.add(jValues); //posArr.get(1)
                //System.out.println(posArr);  //debug code
                position_2nd = randomPositionSelector(posArr);

                //Get projected 2nd gameState and rank value
                //System.out.println("\nProjected 2nd gameState: \n i: " + posArr.getFirst().get(position_2nd) + " j: " +
                //        posArr.get(1).get(position_2nd) + "\n Value: " + lowestVal_2nd + "\nSelected board: ");  //debug code
                boardState_2nd = gameState_2nd[posArr.getFirst().get(position_2nd)][posArr.get(1).get(position_2nd)];
                //printIntBoard(boardState_2nd); //debug code
                gameStateRoot_2nd = gameStateOptimized_1st[posArr.getFirst().get(position_2nd)];

                if (lowestVal_2nd != -4) {
                    if (Math.abs(lowestVal_2nd) > Math.abs(highestVal_1st)) {
                        finalBoardSet.addFirst(gameState_2nd[posArr.getFirst().get(position_2nd)][posArr.get(1).get(position_2nd)]);
                    } else {
                        finalBoardSet.add(gameState_2nd[posArr.getFirst().get(position_2nd)][posArr.get(1).get(position_2nd)]);
                    }
                    tryThird = true;
                } else {
                    finalBoardSet.addFirst(gameState_2nd[posArr.getFirst().get(position_2nd)][posArr.get(1).get(position_2nd)]);
                }
            } //end 2ndTry


            //Do in main method
            //System.out.println("I val: " + iValues);  //debug code
            //System.out.println("J val: " + jValues);  //debug code

            //Create new optimized gameState array
            gameStateOptimized_2nd = new int[iValues.size()][rows][cols];
            int q = gameStateOptimized_2nd.length;
            //System.out.println("------\nq: " + q + "\n------");  //debug code
            gameState_3rd = new int[q][n - 1][rows][cols]; //q is optimized len while n is number of moves left + 1 since there is an extra arr for ranked

            if (tryThird) {
                //Set new optimized array
                for (int i = 0; i < gameStateOptimized_2nd.length; i++) {
                    for (int j = 0; j < gameStateOptimized_2nd[i].length; j++) {
                        gameStateOptimized_2nd[i][j] = Arrays.copyOf(gameState_2nd[iValues.get(i)][jValues.get(i)][j], gameState_2nd[iValues.get(i)][jValues.get(i)][j].length);
                    }
                }
                /*//print   //debug optimized gameStates
                for (int i = 0; i < gameStateOptimized_2nd.length; i++) {
                    System.out.println("\nGameStateOptimized: " + i);
                    for (int j = 0; j < gameStateOptimized_2nd[i].length; j++) {
                        for (int k = 0; k < gameStateOptimized_2nd[i][j].length; k++) {
                            System.out.print(gameStateOptimized_2nd[i][j][k] + " ");
                        }
                        System.out.println();
                    }
                }*/

                //Initialize 3rd set of gameStates (new)
                for (int i = 0; i < gameState_3rd.length; i++) {
                    //System.out.println("\ngameState_3rd: " + i);    //list gameStates
                    gameState_3rd[i] = miniMax.createPossibleGameStates(gameStateOptimized_2nd[i], 1);
                }

                //Clear previous values i, j, posArr
                iValues.clear();
                jValues.clear();
                posArr.clear();

                /*System.out.println("\n Testing game rank place: ");  //index out bounds  debug code
                for (int i = 0; i < gameState_3rd.length; i++) {
                    for (int j = 0; j < 6; j++) {
                        for (int k = 0; k < 7; k++) {
                            System.out.print(gameState_3rd[i][n - 2][j][k] + " ");
                        }
                        System.out.println();
                    }
                    System.out.println();
                } */

                for (int i = 0; i < gameState_3rd.length; i++) {
                    if (gameState_3rd[i][n - 2][rows - 1][cols - 1] > highestVal_3rd) {
                        highestVal_3rd = gameState_3rd[i][n - 2][rows - 1][cols - 1];
                    }
                }
                //System.out.println("highestVal3rd: :" + highestVal_3rd);  //debug code


                for (int i = 0; i < gameState_3rd.length; i++) {
                    int count = 0;
                    loop:
                    for (int j = 0; j < gameState_3rd[i][n - 2].length; j++) {
                        for (int k = 0; k < gameState_3rd[i][n - 2][j].length; k++) {
                            //System.out.println("count: " + count + " i: " + i + " j: " + j + " k: " + k + " lowest value " + highestVal_3rd + " value: " + gameState_3rd[i][n - 2][j][k]);    //debug
                            if (count < n - 2) {
                                if (highestVal_3rd == gameState_3rd[i][n - 2][j][k]) {
                                    iValues.add(i);
                                    jValues.add((j * cols) + k);
                                }
                            } else {
                                break loop;
                            }
                            count++;
                        }
                    }
                }

                posArr.add(iValues); //posArr.getFirst
                posArr.add(jValues); //posArr.get(1)
                //System.out.println(posArr);  //debug code
                position_3rd = randomPositionSelector(posArr);

                //Get projected 2nd gameState and rank value
                //System.out.println("\nProjected 3rd gameState: \n i: " + posArr.getFirst().get(position_3rd) + " j: " + //debug code
                //        posArr.getLast().get(position_3rd) + "\n Value: " + highestVal_3rd + "\nSelected board: "); //debug code
                boardState_3rd = gameState_3rd[posArr.getFirst().get(position_3rd)][posArr.get(1).get(position_3rd)];
                //printIntBoard(boardState_3rd);  //debug code

                gameStateRoot_3rd[1] = gameStateOptimized_2nd[posArr.getFirst().get(position_3rd)]; //2nd board
                for (int i = 0; i < gameState_2nd.length; i++) {
                    for (int j = 0; j < gameState_2nd[i].length; j++) {
                        if (Arrays.deepEquals(gameState_2nd[i][j], gameStateRoot_3rd[1])) {
                            gameStateRoot_3rd[0] = gameStateOptimized_1st[i]; //1st Board
                            break;
                        }
                    }
                }

                if (highestVal_3rd != 4) {
                    if (highestVal_3rd > highestVal_1st || Math.abs(highestVal_3rd) > Math.abs(lowestVal_2nd)) {
                        finalBoardSet.addFirst(gameState_3rd[posArr.getFirst().get(position_3rd)][posArr.getLast().get(position_3rd)]);
                    } else {
                        finalBoardSet.add(gameState_3rd[posArr.getFirst().get(position_3rd)][posArr.getLast().get(position_3rd)]);
                    }
                } else {
                    finalBoardSet.addFirst(gameState_3rd[posArr.getFirst().get(position_3rd)][posArr.getLast().get(position_3rd)]);
                }
            } //end 3rdTry


            int[] fin_pos;
            //Select final position
            int fSize = finalBoardSet.size();
            if (fSize == 1) { //Can win in one turn
                fin_pos = compareBoard(finalBoardSet.getFirst());
                System.out.println("\nChosen Board 1st Move: ");
                printIntBoard(finalBoardSet.getFirst());
            } else if (fSize == 2) { //Block the win of the 2nd move
                fin_pos = compareBoard(finalBoardSet.getFirst(), gameStateRoot_2nd);
                System.out.println("\nChosen Board 2nd Move: ");
                printIntBoard(finalBoardSet.getFirst());
                System.out.println("\nChosen Board 1st Move: ");
                printIntBoard(gameStateRoot_2nd);
            } else { //Look at three moves
                fin_pos = compareBoard(gameStateRoot_3rd[0]);
                System.out.println("\nChosen Board 3rd Move: ");
                printIntBoard(boardState_3rd);
                System.out.println("\nChosen Board 2nd Move:");
                printIntBoard(gameStateRoot_3rd[1]);
                System.out.println("\nChosen Board 1st Move:");
                printIntBoard(gameStateRoot_3rd[0]);
            }

            //debug code
            /*System.out.println("\n\n3rd");
            printIntBoard(boardState_3rd);
            System.out.println("2nd");
            printIntBoard(boardState_2nd);
            System.out.println("1st");
            printIntBoard(boardState_1st);
            System.out.println("\n\n");*/

            //debug code
            /*for (int i = 0; i < fSize; i++) {
                System.out.println("\nFinalSetBoard: " + i);
                printIntBoard(finalBoardSet.get(i));
                System.out.println();
            }*/

            System.out.println("\nMove made: \n row: " + fin_pos[0] + " col: " + fin_pos[1] + "\n");


            board2[fin_pos[0]][fin_pos[1]] = Integer.parseInt(player2[0]);
            placeToken(fin_pos[0], fin_pos[1], Integer.parseInt(player2[0]), cardUI);
        } else {
            getMove();
            board2[rowMove][colMove] = Integer.parseInt(player2[0]);
            placeToken(rowMove, colMove, Integer.parseInt(player2[0]), cardUI);
        }
    }
}