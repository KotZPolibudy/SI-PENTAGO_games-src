package put.ai.games.bestie;

import java.util.List;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import static put.ai.games.game.Player.Color.*;

public class Prototyp35 extends Player {

    private Color me, opponent;
    private long timeLimit;
    private int MaxDepth;

    @Override
    public String getName() {
        return "Mikołaj Nowak 151813 Wojciech Kot 151879";
    }

    private int scoreOpponent(Board b, int depth, int alpha){
        Color winnerColor = b.getWinner(opponent);
        if (winnerColor == me) return -10000;
        else if(winnerColor == opponent) return 10000;
        else if(winnerColor == EMPTY) return 0;
        //Check if maxDepth was reached
        if(depth == MaxDepth) return 0;
        if (System.currentTimeMillis() >= timeLimit) return 0;
        //Add best moves from previous iteration
        //if (moveMapping.containsKey(globalBoard)) moves.add(moveMapping.get(globalBoard));
        int bestFound = -1;
        int eval;
        //generate Moves
        List<Move> moves = b.getMovesFor(opponent);
        for (int i = moves.size() - 1; i >= 0; i--){
            //for(int i=0; i<moves.size(); i++){
            //for(Move move: moves){
            Move move = moves.get(i);
            // Attempt to do the move
            b.doMove(move);
            // Check if we have already seen this position
            /*if (evalMapping.containsKey(globalBoard))
            {
                eval = evalMapping.get(globalBoard);
            }
            else
            {
                eval = -Search(depth + 1, -bestFound);
                evalMapping.put(globalBoard, eval);
            }*/
            eval = -score(b, depth+1, -bestFound);
            if (System.currentTimeMillis() >= timeLimit) return 0;
            // Undo move now before returning values
            b.undoMove(move);
            //analyse
            if(eval > bestFound) bestFound = eval;
            if(alpha <= bestFound) return bestFound;
        }
        return bestFound;
    }

    private int score(Board b, int depth, int alpha){
        Color winnerColor = b.getWinner(me);
        if (winnerColor == me) return 10000;
        else if(winnerColor == opponent) return -10000;
        else if(winnerColor == EMPTY) return 0;
        //Check if maxDepth was reached
        if(depth == MaxDepth) return 0;
        if (System.currentTimeMillis() >= timeLimit) return 0;
        //Add best moves from previous iteration
        //if (moveMapping.containsKey(globalBoard)) moves.add(moveMapping.get(globalBoard));
        int bestFound = -1;
        int eval;
        List<Move> moves = b.getMovesFor(me);
        for (int i = moves.size() - 1; i >= 0; i--){
            //for(int i=0; i<moves.size(); i++){
            //for(Move move: moves){
            Move move = moves.get(i);
            // Attempt to do the move
            b.doMove(move);
            // Check if we have already seen this position
            /*if (evalMapping.containsKey(globalBoard))
            {
                eval = evalMapping.get(globalBoard);
            }
            else
            {
                eval = -Search(depth + 1, -bestFound);
                evalMapping.put(globalBoard, eval);
            }*/
            eval = -scoreOpponent(b, depth+1, -bestFound);
            if (System.currentTimeMillis() >= timeLimit) return 0;
            // Undo move now before returning values
            b.undoMove(move);
            //analyse
            if(eval > bestFound) bestFound = eval;
            if(alpha <= bestFound) return bestFound;
        }
        return bestFound;
    }


    @Override
    public Move nextMove(Board b) {

        timeLimit = System.currentTimeMillis() + getTime() - 50;
        me = getColor();
        opponent = getOpponent(getColor());
        List<Move> moves = b.getMovesFor(me);
        Move selected = moves.get(moves.size()-1);
        Move bestMove;
        int bestFound;
        int eval;
        //Do iterative deepening
        int GlobalMax = 180;
        for(int i=1; i<180; i++)
        {
            // Clear the map before each search
            //evalMapping.clear();
            MaxDepth = i;
            bestMove = moves.get(moves.size()-1); //for whatever reason it needs to be there to shut down warnings
            bestFound = -1;
            //for(Move move: moves) {
            for (int j = moves.size() - 1; j >= 0; j--){
                Move move = moves.get(j);
                b.doMove(move);
                eval = -scoreOpponent(b, 0, -bestFound);
                if (System.currentTimeMillis() >= timeLimit) return selected;
                b.undoMove(move);
                if(eval > bestFound){
                    bestFound = eval;
                    bestMove = move;
                }
            }
            selected = bestMove;
        }
        //Probably we will never get there
        return selected;
    }
}

