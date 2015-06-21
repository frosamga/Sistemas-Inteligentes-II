package intsys.neural;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.agent.Percept;
import aima.core.agent.impl.AbstractEnvironment;
import aima.core.learning.neural.BackPropLearning;
import aima.core.learning.neural.FeedForwardNeuralNetwork;
import aima.core.learning.neural.NNConfig;
import aima.core.learning.neural.NNDataSet;
import aima.core.learning.neural.NNExample;
import aima.gui.framework.AgentAppController;
import aima.gui.framework.AgentAppEnvironmentView;
import aima.gui.framework.AgentAppFrame;
import aima.gui.framework.MessageLogger;
import aima.gui.framework.SimpleAgentApp;
import aima.gui.framework.SimulationThread;

public class NeuralApp extends SimpleAgentApp {

	/** Returns an <code>NeuralView</code> instance. */
	@Override
	public AgentAppEnvironmentView createEnvironmentView() {
		return new NeuralView();
	}

	/** Returns a <code>NeuralFrame</code> instance. */
	@Override
	public AgentAppFrame createFrame() {
		return new NeuralFrame();
	}

	/** Returns a <code>NeuralController</code> instance. */
	@Override
	public AgentAppController createController() {
		return new NeuralController();
	}

	
	// ///////////////////////////////////////////////////////////////
	// main method

	/**
	 * Starts the application.
	 */
	public static void main(String args[]) {
		new NeuralApp().startApplication();
	}

	
	
	// ///////////////////////////////////////////////////////////////
	// some inner classes	
	
	protected static class NeuralFrame extends AgentAppFrame {
		private static final long serialVersionUID = 1L;
		public static String DATASET_SEL = "DataSelSelection";
		public static String NEURAL_SEL = "NetworkSelection";

		public NeuralFrame() {
			setTitle("Nerual Aproximator Application");
			setSelectors(new String[] {DATASET_SEL, NEURAL_SEL},
					new String[] {"Select DataSet", "Select Neural Network"});
			setSelectorItems(DATASET_SEL, FunctionNNDataSet.FCN_NAME, 0);
			setSelectorItems(NEURAL_SEL, new String[] {
					"FeedForward (10 hidden neurons)",
					"FeedForward (20 hidden neurons)",
					"FeedForward (50 hidden neurons)"
					}, 0);
			setEnvView(new NeuralView());
			setSize(800,600);
		}
	}


	protected static class NeuralController extends AgentAppController {

		NeuralEnvironment env = null;
		int epochs;
		
		@Override
		public void clear() {
			env = null;
		}

		@Override
		public void prepare(String changedSelector) {
			//Squared cosine 10 neuronas 20000 epochs error(MSE): 1.0
			//Squared cosine 20 neuronas 20000 epochs error(MSE): 0.425
			//Squared cosine 50 neuronas 20000 epochs error(MSE): 0.35
			
			//el de 50 neuronas aprende mas rapido en Squared cosine
			
			//Humps function 10 neuronas 15000 epochs error(MSE): 7.45
			//Humps function 20 neuronas 12000 epochs error(MSE): 37.225
			//Humps function 50 neuronas 12000 epochs error(MSE): 28.925
			
			//el de 10 neuronas aprende mas rapido en el Humps function
			
			//Aprende mas rapido el de 50 neuronas en Humps function y en Squared cosine
			
			
			epochs = 0;
			if(changedSelector != null) clear();
			else {
				// TODO students
				try {
					NNDataSet ds = new FunctionNNDataSet(
							frame.getSelection().getValue(NeuralFrame.DATASET_SEL));
					// Set the neural architecture NNConfig
					NNConfig config = new NNConfig();
					config.setConfig(FeedForwardNeuralNetwork.NUMBER_OF_INPUTS, 1);
					config.setConfig(FeedForwardNeuralNetwork.NUMBER_OF_OUTPUTS, 1);
					config.setConfig(FeedForwardNeuralNetwork.LOWER_LIMIT_WEIGHTS, -2.0);
					config.setConfig(FeedForwardNeuralNetwork.UPPER_LIMIT_WEIGHTS, 2.0);
					
					switch(frame.getSelection().getValue(NeuralFrame.NEURAL_SEL)) {
					case 0:	config.setConfig(FeedForwardNeuralNetwork.NUMBER_OF_HIDDEN_NEURONS, 10);
						break;
					case 1:	config.setConfig(FeedForwardNeuralNetwork.NUMBER_OF_HIDDEN_NEURONS, 20);
						break;
					case 2:	config.setConfig(FeedForwardNeuralNetwork.NUMBER_OF_HIDDEN_NEURONS, 50);
						break;
					}
					// Create the neural network and training scheme
					FeedForwardNeuralNetwork ffnn = new FeedForwardNeuralNetwork(config);
					ffnn.setTrainingScheme(new BackPropLearning(0.1, 0.9));
					
					env = new NeuralEnvironment(ds, ffnn);
					frame.getEnvView().setEnvironment(env);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


		@Override
		public boolean isPrepared() {
			return env != null;
		}

		@Override
		public void run(MessageLogger logger) {
			String value = JOptionPane.showInputDialog("Number of epochs: ", "");
			int num_epochs = Integer.valueOf(value);
//			while(num_epochs>0 && !frame.simulationPaused()) {
//				step(logger);
//				num_epochs--;
//			}
			if(num_epochs>0) {
				logger.log("Training (" + num_epochs + " epochs)");
				env.step(num_epochs);
				epochs += num_epochs;
				frame.getEnvView().repaint();
			}
			logger.log("\nTotal epochs: " + epochs);
			logger.log("Error (MSE): " + env.getError() + "\n");
		}

		@Override
		public void step(MessageLogger logger) {
			epochs++;
			logger.log("Training (epoch: " + epochs + ")");
			env.step();
			frame.getEnvView().repaint();
		}

		@Override
		public void update(SimulationThread simulationThread) {
			
		}
		
	}
	
	
	protected static class NeuralEnvironment extends AbstractEnvironment {

		NNDataSet ds;
		FeedForwardNeuralNetwork net;
		double mse;		// Mean Squared Error
		
		public NeuralEnvironment(NNDataSet ds, FeedForwardNeuralNetwork ffnn) {
			this.ds = ds;
			this.net = ffnn;
		}
		
		public NNDataSet getDataSet() {
			return ds;
		}
		
		public FeedForwardNeuralNetwork getNN() {
			return net;
		}
		
		public double getError() {
			return mse;
		}
		
		public void setError(double error) {
			mse = error;
		}
		
		public void step(int epochs) {
			net.trainOn(ds, epochs);
		}
		
		public void step() {
			step(1);
		}
		
		@Override
		public EnvironmentState getCurrentState() {
			return null;
		}

		@Override
		public EnvironmentState executeAction(Agent agent, Action action) {
			return null;
		}

		@Override
		public Percept getPerceptSeenBy(Agent anAgent) {
			return null;
		}
		
	}
	
	
	protected static class NeuralView extends AgentAppEnvironmentView {
		private static final long serialVersionUID = 1L;
		private int borderTop = 10;
		private int borderLeft = 10;
		private int borderBottom = 10;
		private int borderRight = 10;

		private double offsetX = 0;
		private double offsetY = 0;
		private double scaleX = 1;
		private double scaleY = 1;

		/**
		 * Specifies the number of pixels left blank on each side of the agent view
		 * panel.
		 */
		public void setBorder(int top, int left, int bottom, int right) {
			borderTop = top;
			borderLeft = left;
			borderBottom = bottom;
			borderRight = right;
		}

		/**
		 * Specifies a bounding box in world coordinates. The resulting
		 * transformation is able to display everything within this bounding box
		 * without scrolling.
		 */
		public void adjustTransformation(double minXW, double minYW, double maxXW, double maxYW) {
			// adjust coordinates relative to the left upper corner of the graph area
			scaleX = 1.0;
			scaleY = 1.0;
			if (maxXW > minXW)
				scaleX = (getWidth() - borderLeft - borderRight) / (maxXW - minXW);
			if (maxYW > minYW)
				scaleY = (getHeight() - borderTop - borderBottom) / (maxYW - minYW);
			offsetX = -minXW;
			offsetY = -minYW;
		}
		
		/** Returns the x_view of a given x-value in world coordinates. */
		protected int x(double xW) {
			return (int) Math.round(scaleX*(xW + offsetX) + borderLeft);
		}

		/** Returns the y_view of a given y-value in world coordinates. */
		protected int y(double yW) {
			return (int) Math.round(scaleY*(yW + offsetY) + borderTop);
		}

		public void paint(Graphics g) {
			if(env == null) return;
			FunctionNNDataSet ds = (FunctionNNDataSet) ((NeuralEnvironment)env).getDataSet();
			FeedForwardNeuralNetwork net = ((NeuralEnvironment)env).getNN();
			Graphics2D g2 = (Graphics2D) g;
			
			adjustTransformation(ds.getMinX(), ds.getMinY(), ds.getMaxX(), ds.getMaxY());
			g2.setBackground(Color.white);
			g2.clearRect(0, 0, getWidth(), getHeight());
			ds.refreshDataset();
			int[] x = new int[ds.howManyExamplesLeft()];
			int[] y1 = new int[ds.howManyExamplesLeft()];
			int[] y2 = new int[ds.howManyExamplesLeft()];
			double error = 0;
			g2.setColor(Color.blue);
			for(int i=0; ds.hasMoreExamples(); i++) {
				NNExample nne = ds.getExample(0);
				x[i] = x(nne.getInput().getValue(0));
				y1[i] = y(nne.getTarget().getValue(0));
				y2[i] = y(net.predict(nne).getValue(0));
				error += (y1[i] - y2[i])*(y1[i] - y2[i]);
				g2.drawOval(x[i]-4, y1[i]-4, 8, 8);
			}
			((NeuralEnvironment)env).setError(error/x.length);
			g2.setColor(Color.blue);
			g2.drawPolyline(x, y1, x.length);
			g2.setColor(Color.red);
			g2.drawPolyline(x, y2, x.length);
		}

		@Override
		public void agentAdded(Agent agent, EnvironmentState resultingState) {
			
		}

		@Override
		public void agentActed(Agent agent, Action action, EnvironmentState resultingState) {
			
		}
	}
		
}
