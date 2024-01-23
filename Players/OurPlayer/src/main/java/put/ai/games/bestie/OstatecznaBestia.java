/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.bestie;

import java.io.*;
import java.util.*;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import static put.ai.games.game.Player.Color.*;

public class OstatecznaBestia extends Player {

    //This player has to have a main function
    public static void main(String[] args) {}

    private Random random = new Random(0xdeadbeef);


    @Override
    public String getName() {
        return "Mikołaj Nowak 151813 Wojciech Kot 151879";
    }

    public Color me, opponent;
    int MaxDepth;
    public Move bestMove;
    public double bestEval;
    Map<String, Integer> evalMapping = new HashMap<>();
    Map<String, Move> moveMapping = new HashMap<>();
    long timeLimit;
    private int moveNo = 0;
    public int depthFromPrevIter = 3;
    private int size, half, forWin, pivot;
    private int blocker;

    private int evaluate(Board b, int depth)
    {
        int score = 0;
        return score;
        /*if(b.getState(pivot, pivot) == opponent) score++; //plus for opponent -> big: opponent happy
        if(b.getState(pivot, pivot) == me) score--;
        if(b.getState(pivot, pivot + half) == opponent) score++;
        if(b.getState(pivot, pivot + half) == me) score--;
        if(b.getState(pivot + half, pivot + half) == opponent) score++;
        if(b.getState(pivot + half, pivot + half) == me) score--;
        if(b.getState(pivot + half, pivot) == opponent) score++;
        if(b.getState(pivot + half, pivot) == me) score--;
        //return score;
        if(depth % 2 == 0) score*=(-1);
        return score;*/
    }

    private int evaluateBIG(Board b, int depth)
    {
        int score = 0;
        if(moveNo % blocker != 0) return score;
        //Else make opponent uncomfortable
        //Calculate the biggest gap and how much of that gap do players fill in
        int scorePlayer1 = 0;
        int scorePlayer2 = 0;
        int currPlayer1 = 0;
        int currPlayer2 = 0;
        int prev1, prev2;
        int gap1, gap2;
        //Check vertically
        for (int i=0; i<size; i++)
        {
            gap1 = 0;
            gap2 = 0;
            prev1 = -1;
            prev2 = -1;
            for(int j=0; j<size; j++)
            {
                if(b.getState(i,j) == me)
                {
                    currPlayer1++;
                    if(j - prev1 > gap1) gap1 = j - prev1 - 1; //Update biggest gap
                    if(gap1 >= forWin)
                    {
                        scorePlayer2 += currPlayer2*currPlayer2;
                    }
                    prev1 = j;
                    currPlayer2=0;
                }
                else if (b.getState(i,j) == opponent)
                {
                    currPlayer2++;
                    if(j - prev2 > gap2) gap2 = j - prev2 - 1; //Update biggest gap
                    if(gap2 >= forWin)
                    {
                        scorePlayer1 += currPlayer1*currPlayer1;
                    }
                    prev2 = j;
                    currPlayer1=0;
                }
                else
                {
                    //do nothing
                }
            }
            //Handle the end of row
            if(size - prev1 > gap1) gap1 = size - prev1 - 1; //Update biggest gap
            if(gap1 >= forWin)
            {
                scorePlayer2 += currPlayer2*currPlayer2;
            }
            if(size - prev2 > gap2) gap2 = size - prev2 - 1; //Update biggest gap
            if(gap2 >= forWin)
            {
                scorePlayer1 += currPlayer1*currPlayer1;
            }
            //Reset current counter
            currPlayer1 = 0;
            currPlayer2 = 0;
        }
        //Check horizontally
        for (int i=0; i<size; i++)
        {
            gap1 = 0;
            gap2 = 0;
            prev1 = -1;
            prev2 = -1;
            for(int j=0; j<size; j++)
            {
                if(b.getState(j,i) == me)
                {
                    currPlayer1++;
                    if(j - prev1 > gap1) gap1 = j - prev1 - 1; //Update biggest gap
                    if(gap1 >= forWin)
                    {
                        scorePlayer2 += currPlayer2*currPlayer2;
                    }
                    prev1 = j;
                    currPlayer2=0;
                }
                else if (b.getState(j,i) == opponent)
                {
                    currPlayer2++;
                    if(j - prev2 > gap2) gap2 = j - prev2 - 1; //Update biggest gap
                    if(gap2 >= forWin)
                    {
                        scorePlayer1 += currPlayer1*currPlayer1;
                    }
                    prev2 = j;
                    currPlayer1=0;
                }
                else
                {
                    //do nothing
                }
            }
            //Handle the end of row
            if(size - prev1 > gap1) gap1 = size - prev1 - 1; //Update biggest gap
            if(gap1 >= forWin)
            {
                scorePlayer2 += currPlayer2*currPlayer2;
            }
            if(size - prev2 > gap2) gap2 = size - prev2 - 1; //Update biggest gap
            if(gap2 >= forWin)
            {
                scorePlayer1 += currPlayer1*currPlayer1;
            }
            //Reset current counter
            currPlayer1 = 0;
            currPlayer2 = 0;
        }
        //big -> opponent happy
        int evaluation = (scorePlayer2*scorePlayer2)-scorePlayer1; //effectively opponent**2 - me
        if(depth % 2 == 0) evaluation*=(-1);
        return evaluation;
    }


    public int Search(Board b, int depth, int alpha, int beta)
    {
        List<Move> moves;
        //do the simplest evaluation
        if(depth % 2 == 0)
        {
            Color Winner = b.getWinner(me);
            if(Winner == me) return 100*(MaxDepth - depth + 1);
            if(Winner == opponent) return -100*(MaxDepth - depth + 1);
            if(Winner == EMPTY)return 0;
            //generate moves
            moves = b.getMovesFor(me);
        }
        else
        {
            Color Winner = b.getWinner(opponent);
            if(Winner == me) return -100*(MaxDepth - depth + 1);
            if(Winner == opponent) return 100*(MaxDepth - depth + 1);
            if(Winner == EMPTY)return 0;
            //generate moves
            moves = b.getMovesFor(opponent);
        }
        //Check if maxDepth was reached
        if(depth == MaxDepth) return evaluate(b, depth);
        //Add best moves from previous iteration
        if (moveMapping.containsKey(b.toString())) moves.add(moveMapping.get(b.toString()));
        int eval;
        //Check all moves
        for (int i = moves.size() - 1; i >= 0; i--) //do from the back
        //for(int i=0; i<moves.size(); i++)
        //for (Move move : moves)
        {
            if (System.currentTimeMillis() >= timeLimit)
            {
                return evaluate(b, depth);
            }
            Move move = moves.get(i);
            // Attempt to do the move
            b.doMove(move);
            String board = b.toString();
            // Check if we have already seen this position
            if (evalMapping.containsKey(board))
            {
                eval = evalMapping.get(board);
            }
            else
            {
                eval = -Search(b, depth + 1, -beta, -alpha);
                evalMapping.put(board, eval);
            }
            //eval = -Search(b, depth + 1, -beta, -alpha);
            // Undo move now before returning values
            b.undoMove(move);
            //analyse
            board = b.toString();
            if (eval >= beta)
            {
                moveMapping.put(board, move);
                if (depth == 0) {
                    bestMove = move;
                }
                return beta;
            }
            if (alpha < eval)
            {
                moveMapping.put(board, move);
                if (depth == 0) {
                    bestMove = move;
                }
                alpha = eval;
            }
        }
        return alpha;
    }

    public int SearchBigger(Board b, int depth, int alpha, int beta)
    {
        List<Move> moves;
        //do the simplest evaluation
        if(depth % 2 == 0)
        {
            Color Winner = b.getWinner(me);
            if(Winner == me) return 100*(MaxDepth - depth + 1);
            if(Winner == opponent) return -100*(MaxDepth - depth + 1);
            if(Winner == EMPTY)return 0;
            //generate moves
            moves = b.getMovesFor(me);
        }
        else
        {
            Color Winner = b.getWinner(opponent);
            if(Winner == me) return -100*(MaxDepth - depth + 1);
            if(Winner == opponent) return 100*(MaxDepth - depth + 1);
            if(Winner == EMPTY)return 0;
            //generate moves
            moves = b.getMovesFor(opponent);
        }
        //Check if maxDepth was reached
        if(depth == MaxDepth) return evaluateBIG(b, depth);
        //Add best moves from previous iteration
        if (moveMapping.containsKey(b.toString())) moves.add(moveMapping.get(b.toString()));
        int eval;
        //Check all moves
        for (int i = moves.size() - 1; i >= 0; i--) //do from the back
        //for(int i=0; i<moves.size(); i++)
        //for (Move move : moves)
        {
            if (System.currentTimeMillis() >= timeLimit)
            {
                return evaluateBIG(b, depth);
            }
            Move move = moves.get(i);
            // Attempt to do the move
            b.doMove(move);
            String board = b.toString();
            // Check if we have already seen this position
            if (evalMapping.containsKey(board))
            {
                eval = evalMapping.get(board);
            }
            else
            {
                eval = -SearchBigger(b, depth + 1, -beta, -alpha);
                evalMapping.put(board, eval);
            }
            //eval = -SearchBigger(b, depth + 1, -beta, -alpha);
            // Undo move now before returning values
            b.undoMove(move);
            //analyse
            board = b.toString();
            if (eval >= beta)
            {
                moveMapping.put(board, move);
                if (depth == 0) {
                    bestMove = move;
                }
                return beta;
            }
            if (alpha < eval)
            {
                moveMapping.put(board, move);
                if (depth == 0) {
                    bestMove = move;
                }
                alpha = eval;
            }
        }
        return alpha;
    }

    @Override
    public Move nextMove(Board b) {
        //Try tracking time
        timeLimit = System.currentTimeMillis() + getTime() - 50;
        me = getColor();
        opponent = getOpponent(getColor());
        moveNo++;
        size = b.getSize();
        half = size/2;
        pivot = half/2;
        forWin = half + (half + 1) / 2;
        blocker = 100;
        if(size == 10) blocker = 3;
        else if (size == 14) blocker = 4;
        else if (size == 18) blocker = 5;
        else if (size == 22) blocker = 6;
        else if (size == 26) blocker = 7;
        else if (size == 30) blocker = 8;
        blocker--;
        //Do iterative deepening
        int GlobalMax = 180;
        Move bestMoveLastIter;
        if(moveMapping.get(b.toString()) == null)
        {
            List<Move> moves = b.getMovesFor(me);
            bestMoveLastIter = moves.get(0);
            bestMove = moves.get(0);
        }
        else
        {
            bestMoveLastIter = moveMapping.get(b.toString());
            bestMove = bestMoveLastIter;
        }

        int eval;
        int alpha = -90000;
        int beta = 90000;
        PrintStream p;
        if(depthFromPrevIter < 3) depthFromPrevIter=3;
        for(int i=depthFromPrevIter-3; i<=GlobalMax; i++)
        {
            // Clear the map before each search
            evalMapping.clear();
            MaxDepth = i;
            if(System.currentTimeMillis() >= timeLimit) break;
            if(size == 6) eval = Search(b,0, -beta, -alpha);
            else eval = SearchBigger(b,0, -beta, -alpha);
            evalMapping.put(b.toString(), eval);
            bestMoveLastIter = bestMove;
        }
        depthFromPrevIter = MaxDepth;
        return bestMoveLastIter;
    }
}