package student_player;

import boardgame.Board;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;

import java.util.ArrayList;
import java.util.Arrays;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
//import MiniMax;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    Boolean revealedAllHidden = false;

        public StudentPlayer() {
        super("26074&2601");
    }
        

    public Move chooseMove(SaboteurBoardState boardState) {
        
        //return the best possible move
        Move myMove = MyTools.bestChoice(boardState);

        return myMove;
    }
   
}



	