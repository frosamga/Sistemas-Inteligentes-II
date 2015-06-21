package intsys.decision;

import aima.core.environment.cellworld.CellWorld;

/**
 * 
 * @author Ciaran O'Reilly
 * 
 */
public class CellWorldFactoryLab {

	/**
	 * Create the cell world as defined in Figure 17.1 in AIMA3e. (a) A simple 4
	 * x 3 environment that presents the agent with a sequential decision
	 * problem.
	 * 
	 * @return a cell world representation of Fig 17.1 in AIMA3e.
	 */
	public static CellWorld<Double> createCellWorldForLaboratory() {
		CellWorld<Double> cw = new CellWorld<Double>(4, 3, -0.02);

		cw.removeCell(2, 2);

		cw.getCellAt(1, 3).setContent(-0.06);
		cw.getCellAt(2, 3).setContent(-0.04);
		cw.getCellAt(4, 3).setContent(1.0);
		cw.getCellAt(1, 2).setContent(-0.06);
		cw.getCellAt(4, 2).setContent(-1.0);
		cw.getCellAt(1, 1).setContent(-0.06);
		cw.getCellAt(2, 1).setContent(-0.04);

		return cw;
	}
}
