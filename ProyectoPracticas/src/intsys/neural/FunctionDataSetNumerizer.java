package intsys.neural;

import java.util.ArrayList;
import java.util.List;

import aima.core.learning.framework.Example;
import aima.core.learning.neural.Numerizer;
import aima.core.util.datastructure.Pair;

public class FunctionDataSetNumerizer implements Numerizer {

	@Override
	public Pair<List<Double>, List<Double>> numerize(Example e) {
		List<Double> input = new ArrayList<Double>();
		List<Double> desiredOutput = new ArrayList<Double>();

		input.add(e.getAttributeValueAsDouble("x"));
		desiredOutput.add(e.getAttributeValueAsDouble("y"));
		
		return new Pair<List<Double>,List<Double>>(input, desiredOutput);
	}

	@Override
	public String denumerize(List<Double> outputValue) {
		return outputValue.get(0).toString();
	}

}
