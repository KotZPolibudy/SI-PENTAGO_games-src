/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.bestie;

import java.util.*;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import static put.ai.games.game.Player.Color.*;

public class Bestia extends Player {

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

    private int evaluate(Board b, int depth)
    {
        int score = 0;
        return score;
        /*if(b.getState(pivot, pivot) == opponent) score++;
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


    public int Search(Board b, int depth, int alpha, int beta)
    {
        List<Move> moves;
        //do the simplest evaluation
        if(depth % 2 == 0)
        {
            Color Winner = b.getWinner(me);
            if(Winner == me) return 100*depth;
            if(Winner == opponent) return -100*depth;
            if(Winner == EMPTY)return 0;
            //generate moves
            moves = b.getMovesFor(me);
        }
        else
        {
            Color Winner = b.getWinner(opponent);
            if(Winner == me) return -100*depth;
            if(Winner == opponent) return 100*depth;
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
        if(depthFromPrevIter < 3) depthFromPrevIter=3;
        for(int i=depthFromPrevIter-3; i<=GlobalMax; i++)
        {
            /*PrintStream p = null;
            try {
                // Use FileOutputStream with append mode (true)
                FileOutputStream fileOutputStream = new FileOutputStream("C:/Users/Miki Nowak/Downloads/prep.txt", true);

                // Wrap the FileOutputStream with a PrintStream
                p = new PrintStream(fileOutputStream);

                p.println("Rozpoczynam glebie ");
                p.println(i);
                p.println("A najlepszym ruchem jest");
                p.println(bestMoveLastIter.toString());
                p.println("Z ocena");
                b.doMove(bestMoveLastIter);
                p.println(evalMapping.get(b.toString()));
                p.println("W tej pozycji po ruchu");
                p.println(b.toString());
                b.undoMove(bestMoveLastIter);
                p.println("");

            } catch (FileNotFoundException ex) {
                Logger.getLogger(OurPlayer.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                Logger.getLogger(OurPlayer.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } finally {
                if (p != null) {
                    p.close();
                }
            }*/
            // Clear the map before each search
            evalMapping.clear();
            MaxDepth = i;
            if(System.currentTimeMillis() >= timeLimit) break;
            eval = Search(b,0, -beta, -alpha);
            evalMapping.put(b.toString(), eval);
            bestMoveLastIter = bestMove;
            /*p = null;
            try {
                // Use FileOutputStream with append mode (true)
                FileOutputStream fileOutputStream = new FileOutputStream("C:/Users/Miki Nowak/Downloads/prep.txt", true);

                // Wrap the FileOutputStream with a PrintStream
                p = new PrintStream(fileOutputStream);

                p.println(b.toString());
                p.println("Numer ruchu");
                p.println(moveNo);
                p.println("wynik");
                p.println(evalMapping.get(b.toString()));
                p.println("glebia");
                p.println(i);
                p.println("");

            } catch (FileNotFoundException ex) {
                Logger.getLogger(OurPlayer.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                Logger.getLogger(OurPlayer.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } finally {
                if (p != null) {
                    p.close();
                }
            }*/
        }
        depthFromPrevIter = MaxDepth;
        /*p = null;
        try {
            // Use FileOutputStream with append mode (true)
            FileOutputStream fileOutputStream = new FileOutputStream("C:/Users/Miki Nowak/Downloads/prep.txt", true);

            // Wrap the FileOutputStream with a PrintStream
            p = new PrintStream(fileOutputStream);

            p.println("Glebia zostala");
            p.println(depthFromPrevIter);
            p.println("");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(OurPlayer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            Logger.getLogger(OurPlayer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } finally {
            if (p != null) {
                p.close();
            }
        }*/
        return bestMoveLastIter;
    }
}