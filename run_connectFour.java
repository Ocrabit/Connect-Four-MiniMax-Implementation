public class run_connectFour {
    public static void main(String[] args) {
        ConnectFour main = new ConnectFour();
        CardUI cardUI = new CardUI();
        MiniMax miniMax = new MiniMax();
        main.storeMiniMax(miniMax);
        main.gameLoop2(cardUI);

    }
}
