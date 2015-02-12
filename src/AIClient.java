import org.json.simple.*;

import java.util.*;

public class AIClient {
	public static void main(String[] argv) {		
		JSONObject obj = (JSONObject)JSONValue.parse(argv[0]);
		Board board = Board.initializeBoardFromJSON(obj);

		int max_score = 0;
		Point centerFinal = new Point(0,0);
		int rotationAngle = 0;
		
		Block block = board._block;
		Block copy = board._block;
		ArrayList<Block> orientations = new ArrayList<Block>();
		ArrayList<ArrayList<Point>> locations = new ArrayList<ArrayList<Point>>();
		
		ArrayList<Point> possibleA = dropSpots(board, copy);
		orientations.add(copy); 
		locations.add(possibleA); 
		
		copy.rotate();
		ArrayList<Point> possibleB = dropSpots(board, copy);
		orientations.add(copy); 
		locations.add(possibleB);
		
		copy.rotate();
		ArrayList<Point> possibleC = dropSpots(board, copy);
		orientations.add(copy); 
		locations.add(possibleC);
		
		copy.rotate();
		ArrayList<Point> possibleD = dropSpots(board, copy);
		orientations.add(copy); 
		locations.add(possibleD);
		
		int score = 0;
		int rot = 0;
		
		for (ArrayList<Point> rotation : locations) {
			for (Point center : rotation) {
				score = analyzePositionOfCenter(center, orientations.get(rot), board);
				if (max_score < score) {
					max_score = score;
					centerFinal = center;
					rotationAngle = rot;
				}
			}
			rot += 1;
		} 
		
		int currentAngle = 0;
		while (currentAngle < rotationAngle) {
			board._block.rotate();
			currentAngle += 1;
		}
		
		block._translation = new Point();
		int centerj = block.getCenter().j;
		
		if(centerj < centerFinal.j) {
			for(int i = 0; i < centerFinal.j - centerj; i++) {
				System.out.println("right");
			}
		}
		else {
			for(int i = 0; i < centerj - centerFinal.j; i++) {
				System.out.println("left");
			}
		}
		
		System.out.flush();
	}

	public static ArrayList<Point> dropSpots(Board board, Block block) {
		ArrayList<Point> result = new ArrayList<Point>();
		//first move it all the way to the left
		// the following "AI" moves a piece as far left as possible
		while (block.checkedLeft(board)) {
			block.left();
		}
		for (int i = 0; i < board.COLS; i++) {
			//move all the way down
			while (block.checkedDown(board)) {
				block.down();
			}
			result.add(block.getCenter());
			//return the block up to the top
			while (block.checkedUp(board)) {
				block.up();
			}
			block.right();
		}
		while (block.checkedUp(board)) {
			block.up();
		}
		while (block.checkedLeft(board)) {
			block.left();
		}
		return result;
	}

	public static int analyzePositionOfCenter(Point center, Block b, Board board) {
		int i = center.i;
		int j = center.j;
		int score = 0;
		for (Point p : b.getOffsets()) {
			Point p1 = new Point(i + p.i, j + p.j);
			score += countAdjacency(p1, board);
		}
		return score;
	}	

	public static int countAdjacency(Point p, Board board){
		int adjacent = 0;
		
		if (p.i == board.ROWS - 1){
			adjacent += 1000;
		} else if( insideBounds(p.i + 1,  p.j, board) && board._bitmap[p.i + 1][p.j] != 0){
			adjacent += 10;
		} 
		
		if (p.i == 0){
			adjacent += 0;
		} else if(insideBounds(p.i - 1,  p.j, board) && board._bitmap[p.i - 1][p.j] != 0){
			adjacent += 10;
		} 
		
		
		if (p.j == board.COLS - 1){
			adjacent += 1;
		} else if(insideBounds(p.i,  p.j + 1, board) && board._bitmap[p.i][p.j + 1] != 0){
			adjacent += 10;
		}
		
		if (p.j == 0){
			adjacent += 1;
		} else if(insideBounds(p.i,  p.j - 1, board) && board._bitmap[p.i][p.j - 1] != 0){
			adjacent += 10;
		}
	
		return adjacent;
	}
	
	public static  boolean insideBounds(int i, int j, Board b){
		return i >=0 && i < b.ROWS && j >= 0 && j < b.COLS;
	}
}
