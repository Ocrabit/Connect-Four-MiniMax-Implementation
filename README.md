# Connect Four MiniMax Implementation

Description: 
	This is a connect four project where I implemented arrays and a minimax algorithm that has a selection method which scores board states up to three turns in advance and compares them to choose the best option. It also filters with alpha beta pruning to not waste calculations on useless paths. The connect four game I built is a little different as you can choose any position on a 6x7 board. The issue that I came upon is one of quantity. By expanding the depth we get vastly more board states to calculate a ranking for. So I worked with alpha beta pruning which cuts off pathways that will not lead to a winning score, reducing extra problems and increasing efficiency. 

Deeper description of problem complexity:
If you look at the figure.pdf file you will see an equation that represents the number of board states that need to be scored. The "g" variable is where you put in the value of how deep you want to go in predicting moves. The "d" variable represents the current depth that is being iterated. The "n" variable represents the number of spaces possible. This means the number of board states possible for the current move. 
