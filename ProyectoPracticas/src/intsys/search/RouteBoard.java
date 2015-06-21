package intsys.search;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



/**
 * Represents a route problem board with a matrix of squares
 * 
 * Note: positions are 0-based indexes and the contents of the squares are represented by chars
 * 
 * @author Enrique and Ezequiel
 *
 */
public class RouteBoard implements Serializable {
	private static final long serialVersionUID = 1L;
	// Set of the allowed digits
	public static String VALUES = "123456789";
	// Size of the board
	public static int SIZE = 21;
	// Special value to mark inaccessible squares
	public static char EMPTY = '0';
	// The values of the squares
	transient char[][] board;
	// Current position in the board
	transient int CurrentX;
	transient int CurrentY;
	// Goal position
	transient int GoalX;
	transient int GoalY;
	

	public RouteBoard() {
		board = new char[SIZE][SIZE];
		

		clear();		
	}
	
	public void clear() {
		CurrentX=SIZE/2;
		CurrentY=SIZE/2;
		GoalX=SIZE-1;
		GoalY=SIZE-1;
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				board[i][j] = '1';				
			}
		}
	}
	
	public boolean isEmpty(int x, int y) {
		return (board[x][y]==EMPTY);
	}
	
	public boolean isValidNonEmpty(int x, int y) {
		if ((x<0) || (x>=SIZE) || (y<0) || (y>=SIZE))
		{
			return false;
		}
		else
		{
			return (!isEmpty(x,y));
		}
	}
	
	public char getDigit(int x, int y) {
		return board[x][y];
	}
	
	public int getCurrentX() {
		return CurrentX;
	}

	public int getCurrentY() {
		return CurrentY;
	}
	
	public int getGoalX() {
		return GoalX;
	}

	public int getGoalY() {
		return GoalY;
	}
	
	public void setCurrentPos(int x, int y) {
		CurrentX=x;
		CurrentY=y;
	}
	
	public void setGoal(int x, int y) {
		GoalX=x;
		GoalY=y;
	}


	public int getInteger(int x, int y) {
		return VALUES.indexOf(board[x][y])+1;
	}
	
	public RouteBoard clone() {
		RouteBoard s = new RouteBoard();
		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				s.board[i][j] = board[i][j];
			}
		}
		s.CurrentX=CurrentX;
		s.CurrentY=CurrentY;
		s.GoalX=GoalX;
		s.GoalY=GoalY;		
		return s;
	}
	
	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		RouteBoard s = (RouteBoard) o;

		if ((s.CurrentX!=this.CurrentX) || (s.CurrentY!=this.CurrentY) ||
				(s.GoalX!=this.GoalX) || (s.GoalY!=this.GoalY))
		{
			return false;
		}
		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				if (s.board[i][j] != this.board[i][j])
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = 17;
		
		result = 37 * result + this.CurrentX;
		result = 37 * result + this.CurrentY;
		result = 37 * result + this.GoalX;
		result = 37 * result + this.GoalY;
		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				result = 37 * result + this.board[i][j];
			}
		}
		
		return result;
	}
	
	public void setDigit(int x, int y, char digit) {
		if(VALUES.indexOf(digit) == -1) digit = EMPTY;
		board[x][y] = digit;
	}
	
	public void removeDigit(int x, int y) {
		board[x][y] = EMPTY;
	}

	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		stream.write(CurrentX);
		stream.write(CurrentY);
		stream.write(GoalX);
		stream.write(GoalY);		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				stream.write(board[i][j]);
			}
		}
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		board = new char[SIZE][SIZE];
		clear();
		stream.defaultReadObject();
		CurrentX=stream.read();
		CurrentY=stream.read();
		GoalX=stream.read();
		GoalY=stream.read();		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				board[i][j]=(char)stream.read();
			}
		}
	}

}
