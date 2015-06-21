package intsys.csp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aima.core.search.csp.Assignment;
import aima.core.search.csp.Constraint;
import aima.core.search.csp.Variable;


public class AllDiffConstraint implements Constraint {

	private List<Variable> scope;
	
	public AllDiffConstraint(List<Variable> vars) {
		scope = new ArrayList<Variable>(vars);
	}

	@Override
	public List<Variable> getScope() {
		return scope;
	}

	@Override
	public boolean isSatisfiedWith(Assignment assignment) {
		// TODO: Provide a fulfillment function
		return true;
	}

}
