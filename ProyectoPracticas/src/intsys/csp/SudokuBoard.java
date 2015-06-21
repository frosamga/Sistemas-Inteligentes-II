package intsys.csp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;

/**
 * Represents a sudoku board with a matrix of squares on which digits can be placed
 * 
 * Note: positions are 0-based indexes and digits are represented by chars
 * 
 * @author Enrique
 *
 */
public class SudokuBoard  implements Serializable {
	private static final long serialVersionUID = 1L;
	// Set of the allowed digits
	public static String VALUES = "123456789";
	// Number of digits must be a perfect square
	public static int SIZE = VALUES.length();
	public static int BLOCK_SIZE = (int) Math.sqrt(SIZE);
	public static char EMPTY = 0;
	
	transient char[][] board;
	private boolean[][] fixed;
	
	public SudokuBoard() {
		board = new char[SIZE][SIZE];
		fixed = new boolean[SIZE][SIZE];
	}
	
	public void clear() {
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				board[i][j] = EMPTY;
				fixed[i][j] = false;
			}
		}
	}

	public boolean isEmpty(int x, int y) {
		return (board[x][y]==EMPTY);
	}
	
	public boolean isFixed(int x, int y) {
		return fixed[x][y];
	}
	
	public char getDigit(int x, int y) {
		return board[x][y];
	}
	
	public SudokuBoard clone() {
		SudokuBoard s = new SudokuBoard();
		
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				s.board[i][j] = board[i][j];
				s.fixed[i][j] = fixed[i][j];
			}
		}
		return s;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if ((o == null) || (this.getClass() != o.getClass()))
			return false;
		
		SudokuBoard b = (SudokuBoard) o;

		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				if(b.board[i][j] != this.board[i][j]) return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		double base = 1;
		int code = 0;
		
		for(int i=0; i<SIZE && base*SIZE<Integer.MAX_VALUE; i++) {
			for(int j=0; j<SIZE && base*SIZE<Integer.MAX_VALUE; j++) {
				if(!isFixed(i,j)) {
					code += base*VALUES.indexOf(getDigit(i,j));
					base *= SIZE;
				}
			}
		}
		return code;
	}
	
	public void setDigit(int x, int y, char digit) {
		if(VALUES.indexOf(digit) == -1) digit = EMPTY;
		board[x][y] = digit;
	}
	
	public void removeDigit(int x, int y) {
		board[x][y] = EMPTY;
	}
	
	public void fixDigit(int x, int y) {
		fixed[x][y] = true;
	}
	
	public void unfixDigit(int x, int y) {
		fixed[x][y] = false;
	}
	
	public void fixBoard() {
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				fixed[i][j] = (board[i][j] != EMPTY);
			}
		}
	}
	
	/**
	 * Returns the set of all allowed digits that can be placed at square (x,y)
	 * 
	 * @param x	row of the square (0-based  index)
	 * @param y column of the square (0-based index)
	 * 
	 * @return a string with all allowed digits
	 */
	public String getAllowedDigits(int x, int y) {
		BitSet s = new BitSet(VALUES.length());
		
		// Checks vertically
		for(int i=0; i<SIZE; i++) {
			if((i != x) && !isEmpty(i,y)) s.set(VALUES.indexOf(getDigit(i,y)));
		}
		// Checks horizontally
		for(int i=0; i<SIZE; i++) {
			if((i != y) && !isEmpty(x,i)) s.set(VALUES.indexOf(getDigit(x,i)));
		}
		// Checks inside the block
		int bx = (x/BLOCK_SIZE) * BLOCK_SIZE;
		int by = (y/BLOCK_SIZE) * BLOCK_SIZE;
		for(int i=0; i<BLOCK_SIZE; i++) {
			for(int j=0; j<BLOCK_SIZE; j++) {
				if((bx+i != x) && (by+j != y) && !isEmpty(bx+i, by+j))
					s.set(VALUES.indexOf(getDigit(bx+i, by+j)));
			}
		}
		
		// Constructs a string containing all allowed digits
		String str = new String();
		for(int i=0; i<VALUES.length(); i++) {
			if(!s.get(i)) str += VALUES.substring(i, i+1);
		}
		return str;
	}
	
	/**
	 * Checks whether a digit can be placed at square (x,y)
	 * 
	 * @return digit can be placed at square (x,y)?
	 */
	public boolean isAllowedDigit(int x, int y, char digit) {
		// Checks vertically
		for(int i=0; i<SIZE; i++) {
			if((i != x) && (digit==getDigit(i,y))) return false;
		}
		// Checks horizontally
		for(int i=0; i<SIZE; i++) {
			if((i != y) && (digit==getDigit(x,i))) return false;
		}
		// Checks inside the block
		int bx = (x/BLOCK_SIZE) * BLOCK_SIZE;
		int by = (y/BLOCK_SIZE) * BLOCK_SIZE;	
		for(int i=0; i<BLOCK_SIZE; i++) {
			for(int j=0; j<BLOCK_SIZE; j++) {
				if((bx+i != x) && (by+j != y) && (digit==getDigit(bx+i,by+j)))
					return false;
			}
		}
		return true;
	}

	public boolean isBoardOK() {
		for(int x=0; x<SIZE; x++) {
			for(int y=0; y<SIZE; y++) {
				if(!isEmpty(x,y) && !isAllowedDigit(x,y,getDigit(x,y))) return false;
			}
		}
		return true;
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				if(!isEmpty(i,j)) {
					stream.write(i);
					stream.write(j);
					stream.write(board[i][j]);
				}
			}
		}
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		board = new char[SIZE][SIZE];
		fixed = new boolean[SIZE][SIZE];
		clear();
		stream.defaultReadObject();
		int i = stream.read();
		while(i != -1) {
			int j = stream.read();
			board[i][j] = (char) stream.read();
			i = stream.read();
		}
	}

}
