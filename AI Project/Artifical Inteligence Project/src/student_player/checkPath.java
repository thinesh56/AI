package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import Saboteur.SaboteurBoardState;



public class checkPath {

    public static final int BOARD_SIZE = 14;


    /**
     * search along the path from entrance to nugget and determine if there is a tile missing
     * @param originTargets entrance location
     * @param targetPos nugget location
     * @param boardState state of the board
     * @return position of missing tile to connect the 2 broken paths
     */
    public static int[] fixPath(ArrayList<int[]> originTargets,int[] targetPos, SaboteurBoardState boardState){

        ArrayList<int[]> queueOrigin = new ArrayList<>();
        ArrayList<int[]> queueTarget = new ArrayList<>();
        int [] missingPos;
        
        //get all the empty neighbours starting from the entrance
        queueOrigin = getMissingOriginPos(queueOrigin,originTargets, boardState);
        //get all the empty neighbours starting from the nugget
        queueTarget = getMissingTargetPos(queueTarget,targetPos, boardState);

        if(queueOrigin.size()>0 && queueTarget.size()>0){
            //find the common missing tile position
            missingPos = compareMissingTiles(queueOrigin, queueTarget);
        }
        else{
        	if(queueTarget.size()<1) {
        		missingPos = new int []{0,0};
        	}
        	else {
        		//System.out.println("2 tiles away");
        		missingPos = new int[] {0,0};
        	}
        }

        return missingPos;
    }


     /**
     * Traverse the path starting from the nugget and determine all the empty tiles, modified original code found in SaboteurBoardState
     * @param queueOrigin queue with all the empty neighbours
     * @param originTargets starting location
     * @param boardState   state of board
     * @return queue with empty neigbours 
     */
    private static ArrayList<int[]> getMissingTargetPos(ArrayList<int[]> queueTarget, int[] targetPos,  SaboteurBoardState boardState) {

        ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
        visited.add(targetPos);
 
        addUnvisitedNeighborToQueueNodes(targetPos,queue,visited,BOARD_SIZE,boardState);
      
        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
            visited.add(visitingPos);
            queueTarget.addAll(addUnvisitedNeighborToQueueNodes(visitingPos,queue,visited,BOARD_SIZE,boardState));
        }
        return queueTarget;
    }

    /**
     * Traverse the path starting from the entrace and determine all the empty tiles, modified original code found in SaboteurBoardState
     * @param queueOrigin queue with all the empty neighbours
     * @param originTargets starting location
     * @param boardState state of board
     * @return queue with empty neighbours
     */
    private static ArrayList<int[]> getMissingOriginPos(ArrayList<int[]> queueOrigin, ArrayList<int[]> originTargets,  SaboteurBoardState boardState) {

    	ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
         
        int [] originalPos = originTargets.get(0);
         
        visited.add(originalPos);
    
        addUnvisitedNeighborToQueueNodes(originalPos,queue,visited,BOARD_SIZE,boardState);

        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
            visited.add(visitingPos);
                queueOrigin.addAll(addUnvisitedNeighborToQueueNodes(visitingPos,queue,visited,BOARD_SIZE,boardState));
        }
        return queueOrigin;
    }

    /**
     * Check if there are any common missing tile position from the entrance and nugget
     * @param queueOrigin missing tiles starting from the entrance position
     * @param queueTarget missing tiles starting from the nugget position
     * @return
     */
    private static int[] compareMissingTiles(ArrayList<int[]> queueOrigin, ArrayList<int[]> queueTarget) {

        int [] answer = new int []{0,0};

        for(int i =0; i<queueOrigin.size();i++){
            int []temp = queueOrigin.get(i);
           
                for(int j=queueTarget.size()-1;j>=0;j--){
                    int []temp2 = queueTarget.get(j);

                    int tempX = temp[0];
                    int tempY = temp[1];

                    int temp2X = temp2[0];
                    int temp2Y = temp[1];

                    //if X and Y are the same, return this tile position
                    if(tempX == temp2X && tempY == temp2Y){
                        answer = temp;
                        break;
                    }
                }
            }

        return answer;
    }

    /**
     * Search for all the missing nodes and return the empty nodes as a queue, modified original code found in SaboteurBoardState
     * @param pos position
     * @param queue queue of nodes
     * @param visited  nodes that have been visited already
     * @param maxSize max size
     * @param boardState state of board
     * @return
     */
    public static ArrayList<int[]> addUnvisitedNeighborToQueueNodes(int[] pos,ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize,SaboteurBoardState boardState){
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0},{-2,0},{-1,-1},{1,1}};
        int i = pos[0];
        int j = pos[1];
        ArrayList<int[]> queue1 = new ArrayList<>(); 

        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(!containsIntArray(visited,neighborPos)){
                    if(boardState.getBoardForDisplay()[neighborPos[0]][neighborPos[1]]!=null) {
                    	queue.add(neighborPos);
                    }
                    else{
                        queue1.add(neighborPos);
                    }
                }
            }
        }

        return queue1;
    }

    /**
     * Used to simply check if the path is broken or not, was taken from SaboteurBoardState
     * @param originTargets entrance tile
     * @param targetPos nugget tile
     * @param usingCard using the cards
     * @param boardState state of board
     * @return
     */
    public static Boolean cardPath(ArrayList<int[]> originTargets, int[] targetPos, Boolean usingCard,
            SaboteurBoardState boardState) {
        // the search algorithm, usingCard indicate weither we search a path of cards (true) or a path of ones (aka tunnel)(false).
        ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
        visited.add(targetPos);
        if(usingCard) addUnvisitedNeighborToQueue(targetPos,queue,visited,BOARD_SIZE,usingCard,boardState);
        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
            if(containsIntArray(originTargets,visitingPos)){
                return true;
            }
            visited.add(visitingPos);
            if(usingCard) {
            	addUnvisitedNeighborToQueue(visitingPos,queue,visited,BOARD_SIZE,usingCard,boardState);
            }
        }
        return false;
    }


    /**
     * Used to aid in checking if the path is broken or not,was taken from SaboteurBoardState
     * @param pos entrance tile
     * @param queue nodes
     * @param visited visited nodes
     * @param maxSize maximum size
     * @param usingCard card tiles being used
     * @param boardState state of board
     */
    public static void addUnvisitedNeighborToQueue(int[] pos,ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize,boolean usingCard,SaboteurBoardState boardState){
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0},{-2,0},{-1,-1},{1,1}};
        int i = pos[0];
        int j = pos[1];
        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(!containsIntArray(visited,neighborPos)){
                    if(usingCard && boardState.getBoardForDisplay()[neighborPos[0]][neighborPos[1]]!=null) {
                    	queue.add(neighborPos);
                    }
                }
            }
        }
    }

    /**
     * Used to comapre 2 arraylists, was taken from SaboteurBoardState
     * @param a list of int[] arrays
     * @param o int [] array
     * @return true or false
     */
    public static boolean containsIntArray(ArrayList<int[]> a,int[] o){
        if (o == null) {
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i) == null)
                    return true;
            }
        } else {
            for (int i = 0; i < a.size(); i++) {
                if (Arrays.equals(o, a.get(i)))
                    return true;
            }
        }
        return false;
    }
}