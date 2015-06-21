package intsys.csp;

import java.util.ArrayList;
import java.util.List;

import aima.core.search.csp.CSP;
import aima.core.search.csp.Domain;
import aima.core.search.csp.Variable;


public class SudokuCSP extends CSP {

	public static final Variable[][] Digit = new Variable[SudokuBoard.SIZE][SudokuBoard.SIZE];
	
	private static List<Variable> collectVariables(SudokuBoard board) {
		List<Variable> vars = new ArrayList<Variable>();
		
		for(int i=0; i<SudokuBoard.SIZE; i++)
			for(int j=0; j<SudokuBoard.SIZE; j++)
				Digit[i][j] = new Variable(new String(i + "," + j));

		if(board == null) {
			for(int i=0; i<SudokuBoard.SIZE; i++)
				for(int j=0; j<SudokuBoard.SIZE; j++)
					vars.add(Digit[i][j]);
		}
		else {
			for(int i=0; i<SudokuBoard.SIZE; i++)
				for(int j=0; j<SudokuBoard.SIZE; j++)
					if(board.isEmpty(i, j)) vars.add(Digit[i][j]);
		}
		return vars;
		
	}
	
	private static List<String> splitString(String str) {
		List<String> list = new ArrayList<String>();
		
		for(int i=1; i<=str.length(); i++)
			list.add(str.substring(i-1, i));
		
		return list;
	}
	
	public SudokuCSP(SudokuBoard board) {
		super(collectVariables(board));
		
		if(board==null) {
			Domain values = new Domain(splitString(SudokuBoard.VALUES));
			
			for(Variable var : getVariables()) setDomain(var, values);
			
			// Row and column constraints
			for(int i=0; i<SudokuBoard.SIZE; i++) {
				List<Variable> rowCons = new ArrayList<Variable>();
				List<Variable> colCons = new ArrayList<Variable>();
				rowCons.add(Digit[i][0]);
				colCons.add(Digit[0][i]);
				for(int j=1; j<SudokuBoard.SIZE; j++) {
					rowCons.add(Digit[i][j]);
					colCons.add(Digit[j][i]);
				}
				addConstraint(new AllDiffConstraint(rowCons));
				addConstraint(new AllDiffConstraint(colCons));
			}
			
			// Block constraints
			for(int bi=0; bi<(SudokuBoard.SIZE/SudokuBoard.BLOCK_SIZE); bi++)
				for(int bj=0; bj<(SudokuBoard.SIZE/SudokuBoard.BLOCK_SIZE); bj++) {
					List<Variable> blockCons = new ArrayList<Variable>();
					for(int i=0; i<SudokuBoard.BLOCK_SIZE; i++)
						for(int j=0; j<SudokuBoard.BLOCK_SIZE; j++)
							blockCons.add(Digit[bi*SudokuBoard.BLOCK_SIZE + i][bj*SudokuBoard.BLOCK_SIZE + j]);
					addConstraint(new AllDiffConstraint(blockCons));
				}				
		}
		else {
			// Domains
			for(int i=0; i<SudokuBoard.SIZE; i++)
				for(int j=0; j<SudokuBoard.SIZE; j++)
					if(board.isEmpty(i, j))
						setDomain(Digit[i][j], new Domain(splitString(board.getAllowedDigits(i, j))));
					
			// Row and Columns constraints
			for(int i=0; i<SudokuBoard.SIZE; i++) {
				List<Variable> rowCons = new ArrayList<Variable>();
				List<Variable> colCons = new ArrayList<Variable>();
				for(int j=0; j<SudokuBoard.SIZE; j++) {
					if(board.isEmpty(i, j)) rowCons.add(Digit[i][j]);
					if(board.isEmpty(j, i))	colCons.add(Digit[j][i]);
				}
				if(rowCons.size() > 1) addConstraint(new AllDiffConstraint(rowCons));
				if(colCons.size() > 1) addConstraint(new AllDiffConstraint(colCons));
			}
			
			// Block constraints
			for(int bi=0; bi<(SudokuBoard.SIZE/SudokuBoard.BLOCK_SIZE); bi++)
				for(int bj=0; bj<(SudokuBoard.SIZE/SudokuBoard.BLOCK_SIZE); bj++) {
					List<Variable> blockCons = new ArrayList<Variable>();
					for(int i=0; i<SudokuBoard.BLOCK_SIZE; i++)
						for(int j=0; j<SudokuBoard.BLOCK_SIZE; j++)
							if(board.isEmpty(bi*SudokuBoard.BLOCK_SIZE + i, bj*SudokuBoard.BLOCK_SIZE + j))
								blockCons.add(Digit[bi*SudokuBoard.BLOCK_SIZE + i][bj*SudokuBoard.BLOCK_SIZE + j]);
					if(blockCons.size() > 1) addConstraint(new AllDiffConstraint(blockCons));
				}				
		}
	}

}
