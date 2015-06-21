package intsys.search;

import java.awt.Color;
import java.awt.FileDialog;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.agent.Percept;
import aima.core.agent.impl.AbstractEnvironment;
import aima.core.search.framework.GraphSearch;
import aima.core.search.framework.Problem;
import aima.core.search.framework.Search;
import aima.core.search.framework.SearchAgent;
import aima.core.search.framework.TreeSearch;
import aima.core.search.informed.AStarSearch;
import aima.core.search.local.HillClimbingSearch;
import aima.core.search.local.Scheduler;
import aima.core.search.local.SimulatedAnnealingSearch;
import aima.core.search.uninformed.BreadthFirstSearch;
import aima.core.search.uninformed.DepthFirstSearch;
import aima.core.search.uninformed.DepthLimitedSearch;
import aima.core.search.uninformed.IterativeDeepeningSearch;
import aima.gui.framework.AgentAppController;
import aima.gui.framework.AgentAppEnvironmentView;
import aima.gui.framework.AgentAppFrame;
import aima.gui.framework.MessageLogger;
import aima.gui.framework.SimpleAgentApp;
import aima.gui.framework.SimulationThread;

/**
 * Graphical route application. It demonstrates the performance of different
 * search algorithms.
 * 
 * @author Enrique and Ezequiel
 *
 */
public class RouteApp extends SimpleAgentApp {

	/** List of supported search algorithm names. */
	protected static List<String> SEARCH_NAMES = new ArrayList<String>();
	/** List of supported search algorithms. */
	protected static List<Search> SEARCH_ALGOS = new ArrayList<Search>();

	/** Adds a new item to the list of supported search algorithms. */
	public static void addSearchAlgorithm(String name, Search algo) {
		SEARCH_NAMES.add(name);
		SEARCH_ALGOS.add(algo);
	}

	static {
		addSearchAlgorithm("Depth First Search (Graph Search)",
				new DepthFirstSearch(new GraphSearch()));
		addSearchAlgorithm("Breadth First Search (Tree Search)",
				new BreadthFirstSearch(new TreeSearch()));
		addSearchAlgorithm("Breadth First Search (Graph Search)",
				new BreadthFirstSearch(new GraphSearch()));
		addSearchAlgorithm("Depth Limited Search (8)",
				new DepthLimitedSearch(8));
		addSearchAlgorithm("Iterative Deepening Search",
				new IterativeDeepeningSearch());
		addSearchAlgorithm("A* search (Euclidean distance heuristic)",
				new AStarSearch(new GraphSearch(),
						RouteFunctionFactory.getHeuristicFunction()));
		addSearchAlgorithm("Hill Climbing Search", new HillClimbingSearch(
				RouteFunctionFactory.getHeuristicFunction()));
		addSearchAlgorithm("Simulated Annealing Search",
				new SimulatedAnnealingSearch(RouteFunctionFactory.getHeuristicFunction(),
						new Scheduler(20, 0.045, 1000)));
	}

	/** Returns a <code>RouteView</code> instance. */
	public AgentAppEnvironmentView createEnvironmentView() {
		return new RouteView();
	}

	/** Returns a <code>RouteFrame</code> instance. */
	@Override
	public AgentAppFrame createFrame() {
		return new RouteFrame();
	}

	/** Returns a <code>RouteController</code> instance. */
	@Override
	public AgentAppController createController() {
		return new RouteController();
	}

	
	public static void main(String args[]) {
		new RouteApp().startApplication();
	}
	
	protected static class RouteFrame extends AgentAppFrame {
		private static final long serialVersionUID = 1L;
		public static String FILE_SEL = "FileSelection";
		public static String SEARCH_SEL = "SearchSelection";
		
		public RouteFrame() {
			setTitle("Route Application");
			setSelectors(new String[] {FILE_SEL, SEARCH_SEL}, 
					new String[] {"File", "Select Search"});
			setSelectorItems(FILE_SEL, new String[] {"Open", "Save"}, -1);
			setSelectorItems(SEARCH_SEL, (String[])SEARCH_NAMES.toArray(new String[]{}), 0);
			setEnvView(new RouteView());
			setSize(800, 600);
		}
	}
	
	protected static class RouteView extends AgentAppEnvironmentView implements ActionListener{
		private static final long serialVersionUID = 1L;

		protected JButton[][] squareButton;
		
		protected RouteView() {		
			
			squareButton = new JButton[RouteBoard.SIZE][RouteBoard.SIZE];
			setLayout(new GridLayout(RouteBoard.SIZE, RouteBoard.SIZE));
			for(int x=0; x<RouteBoard.SIZE; x++) {
				for(int y=0; y<RouteBoard.SIZE; y++) {
					JButton square = new JButton("");
					square.addActionListener(this);
					squareButton[x][y] = square;
					add(square);
				}
			}		
		}
		
		protected void showState() {
			
			RouteBoard board = ((RouteEnvironment) env).getBoard();
			int curx=board.getCurrentX();
			int cury=board.getCurrentY();
			int gx=board.getGoalX();
			int gy=board.getGoalY();
			
			
			for(int x=0; x<RouteBoard.SIZE; x++) {
				for(int y=0; y<RouteBoard.SIZE; y++) {

					if (board.getInteger(x, y)==0)
					{
						squareButton[x][y].setBackground(Color.BLUE);
					}
					else
					{
						float h,s,b;
						float coef=board.getInteger(x, y)/9.0f;
						// Compute color palette 
						h=0.3137f+coef*(0.1569f-0.3137f);
						s=0.9412f+coef*(0.3137f-0.9412f);
						b=1.0f+coef*(0.1f-1.0f);
						squareButton[x][y].setBackground(Color.getHSBColor(h, s, b));
					}
					

				}
			}
			squareButton[curx][cury].setBackground(Color.RED);
			squareButton[gx][gy].setBackground(Color.MAGENTA);

		}
		
		@Override
		public void agentAdded(Agent agent, EnvironmentState resultingState) {
			showState();
		}

		@Override
		public void agentActed(Agent agent, Action action, EnvironmentState resultingState) {
			showState();
			notify((agent == null ? "User: " : "") + action.toString());	
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			for(int x=0; x<RouteBoard.SIZE; x++) {
				for(int y=0; y<RouteBoard.SIZE; y++) {
					if(ae.getSource() == squareButton[x][y]) {					
						
						// Ask the user which change he wants 
						String[] options = new String[RouteBoard.VALUES.length()+3];
						
						options[0] = new String("Current");
						options[1] = new String("Goal");
						options[2] = new String("Empty");
						for(int i=1; i<=RouteBoard.VALUES.length(); i++) {
							options[i+2] = new String(RouteBoard.VALUES.substring(i-1, i));
						}
						Object value = JOptionPane.showInputDialog(null, "Option", "Input option", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
						if(value != null) {
							if (value==options[0])
							{
								((RouteController) getController()).setCurrentPos(x,y);
							}
							else
							{								
								if (value==options[1])
								{
									((RouteController) getController()).setGoal(x,y);
								}
								else
								{
									if(value==options[2]) {
										((RouteController) getController()).modifySquare(x,y,RouteBoard.EMPTY);
									}
									else {
										((RouteController) getController()).modifySquare(x,y,((String)value).charAt(0));
									}									
								}
							}							
						}
						
					}
				}
			}
		}
		
	}
	
	
	protected static class RouteController extends AgentAppController {

		protected RouteEnvironment env = null;
		protected SearchAgent agent = null;
		protected boolean boardOK = false;
		
		/**
		 * Creates a new search agent and adds it to the current environment if
		 * necessary.
		 */
		protected void addAgent() throws Exception {
			if (agent != null && agent.isDone()) {
				env.removeAgent(agent);
				agent = null;
			}
			if (agent == null) {
				int sSel = frame.getSelection().getValue(
						RouteFrame.SEARCH_SEL);
				
				Problem problem = new Problem(env.getBoard(), 
						RouteFunctionFactory.getActionsFunction(),
						RouteFunctionFactory.getResultFunction(),
						RouteFunctionFactory.getGoalTest(),
						RouteFunctionFactory.getStepCostFunction());
				Search search = SEARCH_ALGOS.get(sSel);
				agent = new SearchAgent(problem, search);
				env.addAgent(agent);
			}
		}

		/** Provides a text with statistical information about the last run. */
		private String getStatistics() {
			StringBuffer result = new StringBuffer();
			Properties properties = agent.getInstrumentation();
			Iterator<Object> keys = properties.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String property = properties.getProperty(key);
				result.append("\n" + key + " : " + property);
			}
			return result.toString();
		}

		public void modifySquare(int x, int y, char digit) {
			
			env.setBoardCell(x, y, digit);

			agent = null;
			frame.updateEnabledState();
			((RouteView) frame.getEnvView()).showState();
		}
		
		public void setCurrentPos(int x, int y) {
			
			env.setCurrentPos(x, y);

	
			env = new RouteEnvironment(env.getBoard());
			agent = null;
			frame.getEnvView().setEnvironment(env);
			((RouteView) frame.getEnvView()).showState();
			frame.updateEnabledState();
			
		}
		
		public void setGoal(int x, int y) {
			
			env.setGoal(x, y);

			env = new RouteEnvironment(env.getBoard());
			agent = null;
			frame.getEnvView().setEnvironment(env);
			((RouteView) frame.getEnvView()).showState();
			frame.updateEnabledState();
		}
		
		@Override
		public void clear() {
			env = new RouteEnvironment(new RouteBoard());
			frame.getEnvView().setEnvironment(env);
			((RouteView) frame.getEnvView()).showState();
			agent = null;
			boardOK = true;

		}

		@Override
		public void prepare(String changedSelector) {
			if(changedSelector==null) clear();
			else if(changedSelector==RouteFrame.FILE_SEL) {
				FileDialog fd = new FileDialog(frame);
				
				switch(frame.getSelection().getValue(RouteFrame.FILE_SEL)) {
				case 0:	// Open
					fd.setTitle("Load Route board...");
					fd.setMode(FileDialog.LOAD);
					fd.setVisible(true);
					if(fd.getFile() != null) {
						RouteBoard board = null;
						try {
							ObjectInputStream in = new ObjectInputStream(new FileInputStream(fd.getFile()));
							board = (RouteBoard) in.readObject();
							in.close();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						env = new RouteEnvironment(board);
						frame.getMessageLogger().clear();
						frame.getEnvView().setEnvironment(env);
						((RouteView) frame.getEnvView()).showState();
						agent = null;
					}
					break;
				case 1:	// Save
					fd.setTitle("Save Route board...");
					fd.setMode(FileDialog.SAVE);
					fd.setVisible(true);
					if(fd.getFile() != null) {
						try {
							ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fd.getFile()));
							out.writeObject(env.getBoard());
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
					break;
				}
			}
		}

		@Override
		public boolean isPrepared() {
			return (agent==null || !agent.isDone()) && boardOK;
		}

		@Override
		public void run(MessageLogger logger) {
			logger.log("<simulation-log>");
			try {
				addAgent();
				while (!agent.isDone() && !frame.simulationPaused()) {
					Thread.sleep(200);
					env.step();
				}
			} catch (InterruptedException e) {
				// nothing to do...
			} catch (Exception e) {
				e.printStackTrace(); // probably search has failed...
			}
			logger.log(getStatistics());
			logger.log("</simulation-log>\n");
		}

		@Override
		public void step(MessageLogger logger) {
			try {
				addAgent();
				env.step();
			} catch(Exception e) {
				e.printStackTrace();
			}			
		}

		@Override
		public void update(SimulationThread simulationThread) {
			if (simulationThread.isCanceled()) {
				frame.setStatus("Task canceled.");
			} else if (frame.simulationPaused()) {
				frame.setStatus("Task paused.");
			} else {
				frame.setStatus("Task completed.");
			}
		}
		
	}
	
	
	protected static class RouteEnvironment extends AbstractEnvironment {

		RouteBoard board;
		
		public RouteEnvironment(RouteBoard b) {
			board = b;
		}
		
		public RouteBoard getBoard() {
			return board;
		}
		
		public void setBoardCell(int x, int y,char digit)
		{
			if (digit==RouteBoard.EMPTY)
			{
				if ((board.getCurrentX()!=x) || (board.getCurrentY()!=y))
				{
					board.setDigit(x, y, digit);				
				}			
			}
			else
			{
				board.setDigit(x, y, digit);
			}
		}
		
		public void setCurrentPos(int x, int y)
		{
			if (board.getDigit(x, y)!=RouteBoard.EMPTY)
			{
				board.setCurrentPos(x,y);
			}
		}
		
		public void setGoal(int x, int y)
		{
			if (board.getDigit(x, y)!=RouteBoard.EMPTY)
			{
				board.setGoal(x,y);
			}
		}
		
		@Override
		public EnvironmentState getCurrentState() {
			return null;
		}

		@Override
		public EnvironmentState executeAction(Agent agent, Action action) {
			if(action instanceof RouteAction) {
				RouteAction act = (RouteAction)action;
				
				board.setCurrentPos(act.getX(), act.getY());
				if (agent == null)
					updateEnvironmentViewsAgentActed(agent, action, null);
			}
			return null;
		}

		@Override
		public Percept getPerceptSeenBy(Agent anAgent) {
			return null;
		}
		
	}
}
