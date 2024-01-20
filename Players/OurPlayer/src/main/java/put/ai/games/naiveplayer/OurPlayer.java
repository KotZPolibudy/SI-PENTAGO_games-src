/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.naiveplayer;

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

    public double evaluate(Board b, Color onMove)
    {
        //For now just calculate the longest line
        int maxPlayer1 = 0;
        int maxPlayer2 = 0;
        long size = b.getSize();
        int currPlayer1 = 0;
        int currPlayer2 = 0;
        //Check vertically
        for (int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
            {
                if(b.getState(i,j) == PLAYER1)
                {
                    currPlayer1++;
                    if(currPlayer1>maxPlayer1) maxPlayer1=currPlayer1;
                    currPlayer2=0;
                }
                else if (b.getState(i,j) == PLAYER2)
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
                if(b.getState(j,i) == PLAYER1)
                {
                    currPlayer1++;
                    if(currPlayer1>maxPlayer1) maxPlayer1=currPlayer1;
                    currPlayer2=0;
                }
                else if (b.getState(j,i) == PLAYER2)
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

    public double Search(int depth, Board b)
    {
        //Evaluate if reached max depth
        if(depth == MaxDepth)
        {
            if(depth % 2 == 0) return evaluate(b, me);
            else return evaluate(b, opponent);
        }
        //First check if someone wins
        if(b.getWinner(EMPTY) == EMPTY) return -100.0; //Both win, but according to the rules it counts as loss
        if(b.getWinner(EMPTY) == PLAYER1) return 100.0;
        if(b.getWinner(EMPTY) == PLAYER2) return -100.0;

        //Begin search
        double bestEval = -100000;
        //Get moves list
        Color onMove;
        if(depth % 2 == 0) onMove = me;
        else onMove = opponent;
        List<Move> moves = b.getMovesFor(onMove);
        //Check all moves
        for(int i=0; i<moves.size(); i++)
        {
            b.doMove(moves.get(i));
            double eval = -Search(depth + 1, b);
            if(eval > bestEval)
            {
                bestEval = eval;
                if(depth == 0) bestMove = moves.get(i);
            }
            b.undoMove(moves.get(i));
        }
        return bestEval;
    }

    @Override
    public Move nextMove(Board b) {
        //Let's leave this time for now
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
        Search(0, b);
        return bestMove;
    }
}
