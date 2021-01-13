package student_player;

import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;



public class MyTools {
	/**
	 * Returns a random value
	 * @return random value
	 */
    public static double getSomething() {
        return Math.random();
    }

	/**
	 * Determine if we can find the winning move 
	 * @param boardState state of the board
	 * @return winning move
	 */
	public static SaboteurMove winningMove(SaboteurBoardState boardState){

		SaboteurMove winMove = null;

		//get goal location
		int[] buildLocation = findNugget(boardState);

		//get all legal moves
		ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
		//location of the entrance tile
		ArrayList<int[]> originTargets = new ArrayList<>();
		originTargets.add(new int[]{5,5});

		for(SaboteurMove currentMove : legalMoves){

			//calculate the distance for the current card being played
			double valueDistance = evaluateDistance(boardState, boardState.getTurnPlayer(), currentMove, buildLocation);

			//check if the distance from the nugget is 1 tile 
			if(valueDistance<=1){
				// determine what kind of path is required to get to the goal
				int []tileDirectiontoGoal = directionNeeded(buildLocation[0],buildLocation[1],currentMove);
				//get the current card tile's path
				int []cardPaths = cardDirection(currentMove);

				//check to see if the current card tile satisfies the path to reach the goal
				Boolean checkLR = checkLeftRight(tileDirectiontoGoal[0],tileDirectiontoGoal[1],cardPaths[0],cardPaths[1]);
				Boolean checkUD = checkUpDown(tileDirectiontoGoal[2], tileDirectiontoGoal[3],cardPaths[2],cardPaths[3]);

				//if satifies both the left,right & up, down requirements
				if(checkLR && checkUD){
					//check if the path from the entrance to the goal location is broken
					boolean pathExists = checkPath.cardPath(originTargets,buildLocation, true, boardState);
					
					//if the path is not broken, return the winning move
					if(pathExists) {
						return currentMove;
					}
					else {
						//if the path is broken, find the location that must be fixed
						int[] fixLocation = checkPath.fixPath(originTargets,buildLocation, boardState);
						
						if(fixLocation[0] ==0 && fixLocation[1]==0) {
							return currentMove;
						}
						
						//find availabe card that can be placed into the broken spot
						SaboteurMove fixPathTile = fixBrokenPath(fixLocation,boardState); 
						return fixPathTile;
					}

				}

			}

		}

		return winMove;
	}

	/**
	 * With the given coodinates in []fixLocation, determine best possible move to use for that location
	 * 
	 * @param fixLocation broken spot in the path from entrance to nugget
	 * @param boardState state of the board
	 * @return best tile to fix the path
	 */
	private static SaboteurMove fixBrokenPath(int[] fixLocation, SaboteurBoardState boardState) {
		
		//Get all possible legal moves
		ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();

		for(SaboteurMove currentMove : legalMoves){

			//get the played position for the current move
			int [] cardPos = currentMove.getPosPlayed();

			int cardX = cardPos[0];
			int cardY = cardPos[1];

			int fixX = fixLocation[0];
			int fixY = fixLocation[1];

			//If the card position is the same as the desired fixLocation
			if(cardX == fixX && cardY == fixY){
				double value = evaluteCardWorth(boardState,boardState.getTurnPlayer(),currentMove);

				//ensure that the tile is a useful tile (ones that are not dead end pieces)
				if(value<99){
					return currentMove;
				}
			}

		}

		return null;


	}

	/**
	 * Compare the left an right values of the path to the nugget and the current card
	 * @param goalL direction to the nugget left
	 * @param goalR direction to the nugget right
	 * @param cardL direction of the card left
	 * @param cardR direction of the card right
	 * @return
	 */
	private static Boolean checkLeftRight(int goalL, int goalR, int cardL,int cardR) {

		//egde pieces were given a value of 10
		if(cardL <5 && cardR <5) { //remove edge pieces
		
			//if both Left and Right is required
			if(goalL ==1 && goalR ==1) {
				if(cardL ==1 && cardR==1) {
					return true;
				}
			}
			
			else {	
				//if only left is required
				if(goalL ==1) {
					if(goalL==cardL) {
						return true;
					}
				}
				
				//if only right is required
				if(goalR ==1) {
					if(goalR==cardR) {
						return true;
					}
				}
				
				//if left and right is not required
				if(goalL ==0 && goalR==0) {
					return true;
				}
		
			}
		}
		
		return false;
	}

	/**
	 * Compare the up an down values of the path to the nugget and the current card
	 * @param goalU direction to the nugget up
	 * @param goalD direction to the nugget down
	 * @param cardU direction of the card up
	 * @param cardD direction of the card down
	 * @return
	 */
	private static Boolean checkUpDown(int goalU, int goalD, int cardU, int cardD) {

		//egde pieces were given a value of 10
		if(cardU <5 && cardD <5) { //remove edge pieces
				
					//if both up and down is required
					if(goalU ==1 && goalD ==1) {
						if(cardU ==1 && cardD==1) {
							return true;
						}
					}
	
					else {		
						//if only up is required	
						if(goalU ==1) {
							if(goalU==cardU) {
								return true;
							}
						}
						//if only down is required
						if(goalD ==1) {
							if(goalD==cardD) {
								return true;
							}
						}

						//if up and down is not required
						if(goalU==0 && goalD==0) {
							return true;
						}
					}
		}
		
		return false;
	}

	/**
	 * Searches for the best possible move for the current player to perform
	 * @param boardState state of the board
	 * @return best possible move for the current state
	 */
	public static SaboteurMove bestChoice(SaboteurBoardState boardState) {

		SaboteurMove bestMove= null;
		double evalValue = 100000;
		
		//get all legal moves
		ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();

		//get location to build towards
		int[] buildLocation = findNugget(boardState);
		
		//make a list of all the drop moves
		ArrayList<SaboteurMove> dropMoves = new ArrayList<SaboteurMove>();

		for(SaboteurMove currentMove : legalMoves){
			//calcualte the distance from the current move to the goal location
			double valueDistance = evaluateDistance(boardState, boardState.getTurnPlayer(), currentMove, buildLocation);
			//get the value of the current card
			double valueCard = evaluteCardWorth(boardState, boardState.getTurnPlayer(), currentMove);

			double value = valueDistance + valueCard;

			//keep track of the best card (the one with the lowest value)
			if(value<evalValue){
				bestMove = currentMove;
				evalValue = value;
			}

			String s = currentMove.toPrettyString();
			String[] sp = s.split(" ");
			String q = sp[3];
			
			//add drop tile moves to a list
			if(q.equals("(Drop),")){
				dropMoves.add(currentMove);
			}
		}

		//if all possible moves are bad moves (edge pieces), then drop them
		if(evalValue>100){
			bestMove = dropMoves.get(0);
		}
		
		return bestMove;
	}

	/**
	 * Determine the location of the nugget
	 * If nugget is not shown, use middle hidden tile as first location, followed by left hidden tile and lastly right hidden tole
	 * @param boardState
	 * @return
	 */
	public static int[] findNugget(SaboteurBoardState boardState){

	//get the map for the current player
	SaboteurTile[][] map = boardState.getBoardForDisplay();
	
	int x = -1;
	int y =-1;
	
	boolean nuggetFound = false;

	
	ArrayList<Integer> xGoal = new ArrayList<Integer>();
	ArrayList<Integer> yGoal = new ArrayList<Integer>();
	
	//go through the board
	for(int row =0;row<map.length;row++){
            for(int col=0;col<map[row].length;col++){
                
                if(map[row][col]!= null) {
                	SaboteurTile temp = map[row][col];                	
					
					//if the current tile is the nugget, save the location
                	if(temp.getIdx().equals("nugget")) {
						x = row;
						y = col;
						nuggetFound=true;
						break;
                		
                	}

					//if nugget is not found yet, keep track of all 3 goal tiles
                	if(temp.getIdx().equals("goalTile")) {
                		xGoal.add(row);
                		yGoal.add(col);
                	}
                	
                }
            }
		}

		//if nugget is not found, first start to build towards the middle hidden tile
		if(!nuggetFound){
			if(xGoal.size()==3){
				x = xGoal.get(1);
				y = yGoal.get(1);
			}

			//if 1 of the hidden tiles is empty, build towards the another hidden tile
			if(xGoal.size()==2){
				x = xGoal.get(0);
				y = yGoal.get(0);
			}

			//if 2 hidden tiles are empty, then build towards the one that will have the nugget
			if(xGoal.size()==1){
				nuggetFound = true;
				x = xGoal.get(0);
				y = yGoal.get(0);
			}

		}
		
		int []nuggetLocation = new int[]{x,y};

		return nuggetLocation;

	}

	/**
	 * Calculate the distance from the desired location and where the current move will be placed
	 * @param boardState state of board
	 * @param move current move being placed
	 * @param nuggetLocation desired location to build to
	 * @return
	 */
	public static double evaluateDistance(SaboteurBoardState boardState, int player, SaboteurMove move, int[] nuggetLocation) {

        //get the position the card will be played
		int[] currentPosition = move.getPosPlayed();
		
		int x=0;
		int y=0;

		//calculate the x and y difference
		x = Math.abs(currentPosition[0] - nuggetLocation[0]);
		y = Math.abs(nuggetLocation[1] - currentPosition[1]);

		//calculate the distance
		double distance = Math.pow(x,2) + Math.pow(y,2);
		double distance2 = Math.sqrt(distance);
		return distance2;

	}

	/**
	 * Determine the path needed to get to the goal tile when 1 position away
	 * @param y
	 * @param x
	 * @param currentMove
	 * @return
	 */
	public static int[] directionNeeded(int y, int x, SaboteurMove currentMove){

		int right =0;
		int left = 0;
		int up = 0;
		int down = 0;
		
		int cardpos[] = currentMove.getPosPlayed();
		
		int tempY = y - cardpos[0];
		int tempX = x- cardpos[1];
		
		if(tempY<0){
			up = 1;
		}
		else if(tempY>0){
			down = 1;
		}

		if(tempX<0){
			left =1;
		}
		else if(tempX>0){
			right = 1;
		}
		
		int []direction = new int[]{left,right,up,down};

		return direction;
	}

	/**
	 * Give each useful card a direction indicating its direction
	 * @param move current move
	 * @return direction of the current move
	 */
	public static int[] cardDirection(SaboteurMove move){

		SaboteurCard currentCard = move.getCardPlayed();

		String cardName = currentCard.getName();

		int []cardDirection; // [left, right, up , down]

		switch (cardName){
		case "Tile:0":
			cardDirection = new int[]{0,0,1,1};
           return cardDirection ;  
       case "Tile:5":
			cardDirection = new int[]{0,0,0,1};
			return cardDirection ;
       case "Tile:5_flip":
			cardDirection = new int[]{1,0,0,0};
			return cardDirection ;
       case "Tile:6":
			cardDirection = new int[]{1,0,1,1};
			return cardDirection ;
       case "Tile:6_flip":
			cardDirection = new int[]{0,1,1,1};
			return cardDirection ;
       case "Tile:7":
			cardDirection = new int[]{0,1,0,0};
			return cardDirection ;
       case "Tile:7_flip":
			cardDirection = new int[]{0,0,0,1};
			return cardDirection ;
       case "Tile:8":
			cardDirection = new int[]{1,1,1,1};
			return cardDirection ;
       case "Tile:9":
			cardDirection = new int[]{1,1,0,1};
			return cardDirection ;
       case "Tile:9_flip":
			cardDirection = new int[]{1,1,0,0};
			return cardDirection ;
       case "Tile:10":
			cardDirection = new int[]{1,1,0,0};
			return cardDirection ;
       default:
           return new int[]{10,10,10,10};
       }	
	}

	/**
	 * Give each card a value, the lower the value, the better it is to use
	 * @param boardState state of board
	 * @param move current move
	 * @return
	 */
	public static double evaluteCardWorth(SaboteurBoardState boardState, int player, SaboteurMove move) {
		
		SaboteurCard currentCard = move.getCardPlayed();

		String cardName = currentCard.getName();

		double cardValue=0;

		switch (cardName){
		case "Tile:0":
            cardValue= 10;
            return cardValue ;
        case "Tile:1":
            cardValue= 100;
            return cardValue ;
        case "Tile:2":
            cardValue= 100;
            return cardValue ;
        case "Tile:2_flip":
            cardValue= 100;
            return cardValue ;
        case "Tile:3":
            cardValue= 100;
            return cardValue ;
        case "Tile:3_flip":
            cardValue= 100;
            return cardValue ;
        case "Tile:4":
           cardValue= 100;
            return cardValue ;
        case "Tile:4_flip":
           cardValue= 100;
            return cardValue ;
        case "Tile:5":
           cardValue= 40;
            return cardValue ;
        case "Tile:5_flip":
           cardValue= 40;
            return cardValue ;
        case "Tile:6":
           cardValue= 5;
            return cardValue ;
        case "Tile:6_flip":
           cardValue= 5;
            return cardValue ;
        case "Tile:7":
           cardValue= 40;
            return cardValue ;
        case "Tile:7_flip":
           cardValue= 40;
            return cardValue ;
        case "Tile:8":
           cardValue= 2;
            return cardValue ;
        case "Tile:9":
           cardValue= 15;
            return cardValue ;
        case "Tile:9_flip":
           cardValue= 15;
            return cardValue ;
        case "Tile:10":
           cardValue= 30;
            return cardValue ;
        case "Tile:11":
            cardValue= 100;
            return cardValue ;
        case "Tile:11_flip":
           cardValue= 100;
            return cardValue ;
        case "Tile:12":
           cardValue= 100;
            return cardValue ;
        case "Tile:12_flip":
           cardValue= 100;
            return cardValue ;
        case "Tile:13":
           cardValue= 100;
            return cardValue ;
        case "Tile:14":
           cardValue= 100;
            return cardValue ;
        case "Tile:14_flip":
           cardValue= 100;
            return cardValue ;
        case "Tile:15":
           cardValue= 100;
            return cardValue ;
        case "Map":
           cardValue= 0;
            return cardValue ;
        case "Malus":
           cardValue= 1;
            return cardValue ;
        case "Bonus":
           cardValue= 70;
            return cardValue ;
        case "Destroy":
           cardValue= 100;
			return cardValue ;
		case "Drop":
			cardValue = 75;
        default:
            return 100;
        }
	
	}
    
    
}

