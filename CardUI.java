import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class CardUI extends JFrame {
    JLayeredPane layeredPane = new JLayeredPane();
    JTextArea headText = new JTextArea();
    JLabel labelPic = new JLabel(new ImageIcon("src/images/Connect Four Board.png"));
    JLabel[] redToken = new JLabel[21];
    JLabel[] blueToken = new JLabel[21];

    int numberOfRedTokensInPlay = 0;
    int numberOfBlueTokensInPlay = 0;

    JLabel labelRow = new JLabel();
    JTextField inputTextRow = new JTextField();

    JLabel labelCol = new JLabel();
    JTextField inputTextCol = new JTextField();

    JButton submitButton = new JButton();

    ConnectFour main;

    public CardUI(){
        setTitle("Connect Four");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 940); //add 860 x 740
        setBackground(Color.BLACK);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        layeredPane.setBounds(0,0,860,940);
        add(layeredPane);

        headText.setBounds(0,0,860,75);
        headText.setEditable(false);

        labelPic.setBounds(0,75,860,740);
        //labelPic.setIcon(new ImageIcon("src/resources/images/board.png"));

        labelRow.setText("Row: ");
        labelRow.setBounds(0,815,50,30);
        inputTextRow.setBounds(40,815,50,30);

        labelCol.setText("Col: ");
        labelCol.setBounds(0,845,50,30);
        inputTextCol.setBounds(40,845,50,30);

        submitButton.setBounds(25,875,450,35);
        submitButton.setText("Click To Submit Row and Columns");


        layeredPane.add(headText, Integer.valueOf(0));
        layeredPane.add(labelPic, Integer.valueOf(0));
        layeredPane.add(inputTextRow, Integer.valueOf(0));
        layeredPane.add(labelRow, Integer.valueOf(0));
        layeredPane.add(inputTextCol, Integer.valueOf(0));
        layeredPane.add(labelCol, Integer.valueOf(0));
        layeredPane.add(submitButton, Integer.valueOf(0));

        layeredPane.getRootPane().setDefaultButton(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.buttonUnclicked = false;
            }
        });


    }

    public void setMain(ConnectFour object){
        main = object;
    }

    public void setDisplayedText(String t)
    {
        headText.setText(t);
    }

    public void placeTokens (int x, int y, int token) { //token takes in either -1 or 1 | -1 -> Red & 1 -> Blue
        y += 75;

        //System.out.println("x: " + x + " and y: " + y);    //debug positions

        if(token == -1) {
            redToken[numberOfRedTokensInPlay] = new JLabel(new ImageIcon("src/images/red_token.png"));
            redToken[numberOfRedTokensInPlay].setBounds(x,y,102,102);
            layeredPane.add(redToken[numberOfRedTokensInPlay], Integer.valueOf(1));
            numberOfRedTokensInPlay++;
        } else {
            blueToken[numberOfBlueTokensInPlay] = new JLabel(new ImageIcon("src/images/blue_token.png"));
            blueToken[numberOfBlueTokensInPlay].setBounds(x,y,102,102);
            layeredPane.add(blueToken[numberOfBlueTokensInPlay], Integer.valueOf(1));
            numberOfBlueTokensInPlay++;
        }
    }

    public boolean checkNoMorePieces() {
        if(numberOfBlueTokensInPlay == blueToken.length){
            JOptionPane.showMessageDialog(null, "Well it happens sometimes! Nice job " + main.player1[1] + " you just made a catsgame with " + main.player2[1]
                    + " at Four In A Row");
            if (JOptionPane.showConfirmDialog(null, "Would you like to play again?", "Message",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                main.playAgain = true;
            }
            return true;
        } else {
            return false;
        }
    }

    public String getResponse(int num) {
        if(num == 1) {
            if(inputTextRow.getText().isEmpty()) {
                return "-1"; //Null
            } else if (Integer.parseInt(inputTextRow.getText()) < 0) {
                return "-2"; //input below 0
            } else {
                return inputTextRow.getText();
            }
        } else {
            if(inputTextCol.getText().isEmpty()) {
                return "-1"; //Null
            } else if (Integer.parseInt(inputTextCol.getText()) < 0) {
                return "-2";  //input below 0
            } else {
                return inputTextCol.getText();
            }
        }

    }

    public void resetEverything() {
        for(int i = 0; i < numberOfRedTokensInPlay; i++) {
            layeredPane.remove(redToken[i]);
        }
        for(int i = 0; i < numberOfBlueTokensInPlay; i++) {
            layeredPane.remove(blueToken[i]);
        }

        Arrays.fill(redToken, null);
        Arrays.fill(blueToken, null);
        numberOfRedTokensInPlay = 0;
        numberOfBlueTokensInPlay = 0;

        layeredPane.repaint();
    }
}
