package intsys.neural;

import java.util.ArrayList;

import aima.core.learning.framework.DataSet;
import aima.core.learning.framework.DataSetFactory;
import aima.core.learning.framework.DataSetSpecification;
import aima.core.learning.neural.NNDataSet;
import aima.core.learning.neural.NNExample;
import aima.core.learning.neural.Numerizer;


public class FunctionNNDataSet extends NNDataSet {
	public static String[] FCN_NAME = {"Squared cosine", "Humps function"};

	protected double minX, maxX, minY, maxY;
	
	public FunctionNNDataSet(int fcn) throws Exception {

		minX = minY = Double.MAX_VALUE;
		maxX = maxY = Double.MIN_VALUE;
		DataSetSpecification dss = new DataSetSpecification();
		dss.defineNumericAttribute("x");
		dss.defineNumericAttribute("y");
		dss.setTarget("y");
		
		DataSet ds = new DataSet(dss);
		switch(fcn) {
		case 0:	// Squared cosine
			for(double x = -2; x<= 2; x += .1) {
				double y =  Math.cos(x)*Math.cos(x);
				ds.add(DataSetFactory.exampleFromString(x + "," + y, dss, ","));
			}
			break;
		case 1:	// Humps function
			for(double x = -1; x<= 3; x += .1) {
				double y =  1.0/((x-.3)*(x-.3) + .01) + 1.0/((x-.9)*(x-.9) + .04) - 6.0;
				ds.add(DataSetFactory.exampleFromString(x + "," + y, dss, ","));
			}
			break;
		default:		
			throw new Exception("ERROR! Function unrecognized");
		}
		
		Numerizer numerizer = new FunctionDataSetNumerizer();
		createExamplesFromDataSet(ds, numerizer);
		adjustLimits();
	}
	
	private void adjustLimits() {
		refreshDataset();
		while(hasMoreExamples()) {
			NNExample nne = getExample(0);
			double x = nne.getInput().getValue(0);
			double y = nne.getTarget().getValue(0);
			if(x < minX) minX = x;
			if(x > maxX) maxX = x;
			if(y < minY) minY = y;
			if(y > maxY) maxY = y;
		}		
	}
	
	@Override
	public void setTargetColumns() {
		targetColumnNumbers = new ArrayList<Integer>();
		int size = nds.get(0).size();
		targetColumnNumbers.add(size - 1);		
	}

	public double getMinX() {
		return minX;
	}
	
	public double getMinY() {
		return minY;
	}
	
	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}
}
