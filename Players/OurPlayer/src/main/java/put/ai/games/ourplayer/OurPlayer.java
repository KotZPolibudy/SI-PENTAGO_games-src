/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.ourplayer;

import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import static put.ai.games.game.Player.Color.*;

public class OurPlayer extends Player {

    //This player has to have a main function
    public static void main(String[] args) {}

    private Random random = new Random(0xdeadbeef);


    @Override
    public String getName() {
        return "Mikołaj Nowak 151813 Wojciech Kot 151879";
    }

    public int mySide;
    public Color me, opponent;
    long MaxDepth = 4;
    public Move bestMove;
    public Board globalBoard;
    public double evaluate(Color onMove)
    {
        //For now just calculate the longest line
        int maxPlayer1 = 0;
        int maxPlayer2 = 0;
        long size = globalBoard.getSize();
        int currPlayer1 = 0;
        int currPlayer2 = 0;
        //Check vertically
        for (int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
            {
                if(globalBoard.getState(i,j) == PLAYER1)
                {
                    currPlayer1++;
                    if(currPlayer1>maxPlayer1) maxPlayer1=currPlayer1;
                    currPlayer2=0;
                }
                else if (globalBoard.getState(i,j) == PLAYER2)
                {
                    currPlayer2++;
                    if(currPlayer2>maxPlayer2) maxPlayer2=currPlayer2;
                    currPlayer1=0;
                }
                else
                {
                    currPlayer1=0;
                    currPlayer2=0;
                }
            }
            //Reset current counter
            currPlayer1 = 0;
            currPlayer2 = 0;
        }
        //Check horizontally
        for (int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
            {
                if(globalBoard.getState(j,i) == PLAYER1)
                {
                    currPlayer1++;
                    if(currPlayer1>maxPlayer1) maxPlayer1=currPlayer1;
                    currPlayer2=0;
                }
                else if (globalBoard.getState(j,i) == PLAYER2)
                {
                    currPlayer2++;
                    if(currPlayer2>maxPlayer2) maxPlayer2=currPlayer2;
                    currPlayer1=0;
                }
                else
                {
                    currPlayer1=0;
                    currPlayer2=0;
                }
            }
            //Reset current counter
            currPlayer1 = 0;
            currPlayer2 = 0;
        }
        double evaluation = maxPlayer1-maxPlayer2;
        int side = 1;
        if(onMove == opponent) side = -1;
        return evaluation * side;
    }

    public double Search(int depth, double alpha, double beta)
    {
        //alpha represents the best known value that the maximizing player (Max) can guarantee
        //beta represents the best known value that the minimizing player (Min) can guarantee
        //Evaluate if reached max depth
        if(depth == MaxDepth)
        {
            if(depth % 2 == 0) return evaluate(me);
            else return evaluate(opponent);
        }
        //First check if someone wins
        if(globalBoard.getWinner(EMPTY) == EMPTY) return -100.0; //Both win, but according to the rules it counts as loss
        if(globalBoard.getWinner(EMPTY) == PLAYER1) return 100.0;
        if(globalBoard.getWinner(EMPTY) == PLAYER2) return -100.0;

        //Get moves list
        Color onMove;
        if(depth % 2 == 0) onMove = me;
        else onMove = opponent;
        List<Move> moves = globalBoard.getMovesFor(onMove);
        //Check all moves
        for(int i=0; i<moves.size(); i++)
        {
            globalBoard.doMove(moves.get(i));
            double eval = -Search(depth + 1, -beta, -alpha);
            globalBoard.undoMove(moves.get(i));
            if(eval >= beta)
            {
                if(depth == 0)
                {
                    bestMove = moves.get(i);
                }
                return beta;
            }
            if(eval > alpha)
            {
                alpha = eval;
                if (depth == 0) {
                    bestMove = moves.get(i);
                }
            }
        }
        return alpha;
    }

    @Override
    public Move nextMove(Board b) {
        //Let's leave tracking time for now
        long ThinkingTime = getTime();
        if(getColor()==PLAYER1)
        {
            mySide = 1;
            me = PLAYER1;
            opponent = PLAYER2;
        }
        else
        {
            mySide = -1;
            me = PLAYER2;
            opponent = PLAYER1;
        }
        globalBoard = b;
        double initialAlpha = Double.NEGATIVE_INFINITY;
        double initialBeta = Double.POSITIVE_INFINITY;
        Search(0, initialAlpha, initialBeta);
        return bestMove;
    }
}
