package intsys.csp;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.search.csp.Assignment;
import aima.core.search.csp.BacktrackingStrategy;
import aima.core.search.csp.CSP;
import aima.core.search.csp.CSPStateListener;
import aima.core.search.csp.ImprovedBacktrackingStrategy;
import aima.core.search.csp.MinConflictsStrategy;
import aima.core.search.csp.SolutionStrategy;
import aima.core.search.csp.Variable;
import aima.core.search.framework.Search;
import aima.core.util.datastructure.FIFOQueue;
import aima.gui.applications.search.csp.CSPEnvironment;
import aima.gui.applications.search.csp.CSPEnvironment.StateChangeAction;
import aima.gui.framework.AgentAppController;
import aima.gui.framework.AgentAppEnvironmentView;
import aima.gui.framework.AgentAppFrame;
import aima.gui.framework.MessageLogger;
import aima.gui.framework.SimpleAgentApp;
import aima.gui.framework.SimulationThread;

/**
 * Graphical sudoku application which demonstrates the performance of different
 * constraint algorithms.
 * 
 * @author Enrique
 *
 */
public class SudokuApp extends SimpleAgentApp {

	/** List of supported search algorithm names. */
	protected static List<String> SEARCH_NAMES = new ArrayList<String>();
	/** List of supported search algorithms. */
	protected static List<Search> SEARCH_ALGOS = new ArrayList<Search>();

	/** Adds a new item to the list of supported search algorithms. */
	public static void addSearchAlgorithm(String name, Search algo) {
		SEARCH_NAMES.add(name);
		SEARCH_ALGOS.add(algo);
	}

	/** Returns a <code>NQueensView</code> instance. */
	public AgentAppEnvironmentView createEnvironmentView() {
		return new SudokuView();
	}

	/** Returns a <code>NQueensFrame</code> instance. */
	@Override
	public AgentAppFrame createFrame() {
		return new SudokuFrame();
	}

	/** Returns a <code>NQueensController</code> instance. */
	@Override
	public AgentAppController createController() {
		return new SudokuController();
	}

	
	public static void main(String args[]) {
		new SudokuApp().startApplication();
	}
	
	protected static class SudokuFrame extends AgentAppFrame {
		private static final long serialVersionUID = 1L;
		public static String FILE_SEL = "FileSelection";
		public static String STRATEGY_SEL = "SearchSelection";
		
		public SudokuFrame() {
			setTitle("Sudoku Application");
			setSelectors(new String[] {FILE_SEL, STRATEGY_SEL}, 
					new String[] {"File", "Select Search"});
			setSelectorItems(FILE_SEL, new String[] {"Open", "Save"}, -1);
			setSelectorItems(STRATEGY_SEL, new String[] { "Backtracking",
					"Backtracking + MRV",
					"Backtracking + Forward Checking",
					"Backtracking + Forward Checking + MRV",
					"Backtracking + AC3",
					"Backtracking + AC3 + MRV",
					"Min-Conflicts (50)" }, 0);
			setEnvView(new SudokuView());
			setSize(800, 600);
		}
	}
	
	protected static class SudokuView extends AgentAppEnvironmentView implements ActionListener{
		private static final long serialVersionUID = 1L;

		protected JButton[][] squareButton;
		private Font fixedFont, unfixedFont;
		
		protected SudokuView() {		
			fixedFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
			unfixedFont = new Font(Font.SANS_SERIF, Font.ITALIC, 16);
			squareButton = new JButton[SudokuBoard.SIZE][SudokuBoard.SIZE];
			
			setLayout(new GridLayout(SudokuBoard.SIZE, SudokuBoard.SIZE));
			for(int x=0; x<SudokuBoard.SIZE; x++) {
				int bx = x / SudokuBoard.BLOCK_SIZE;
				
				for(int y=0; y<SudokuBoard.SIZE; y++) {
					JButton square = new JButton("");
					int by = y / SudokuBoard.BLOCK_SIZE;
					
					square.setMargin(new Insets(0,0,0,0));
					square.setFont(fixedFont);
					square.setBackground(Color.WHITE);
					square.addActionListener(this);
					square.setBackground((bx+by) % 2 == 1 ? Color.LIGHT_GRAY : Color.WHITE);
					squareButton[x][y] = square;
					add(square);
				}
			}		
		}
		
		protected void showState() {
			SudokuBoard board = ((SudokuEnvironment) env).getBoard();
			
			for(int x=0; x<SudokuBoard.SIZE; x++) {
				for(int y=0; y<SudokuBoard.SIZE; y++) {
					if(board.isFixed(x, y)) {
						squareButton[x][y].setFont(fixedFont);
						squareButton[x][y].setForeground(Color.BLACK);
					} else if(!board.isEmpty(x, y)) {
						squareButton[x][y].setFont(unfixedFont);
						squareButton[x][y].setForeground(board.isAllowedDigit(x, y, board.getDigit(x, y)) ? Color.BLUE : Color.RED);
						squareButton[x][y].setText(Character.toString(board.getDigit(x, y)));
					}
					else squareButton[x][y].setText("");
				}
			}
		}
		
		@Override
		public void agentAdded(Agent agent, EnvironmentState resultingState) {
			showState();
		}

		@Override
		public void agentActed(Agent agent, Action action, EnvironmentState resultingState) {
			StateChangeAction a = (StateChangeAction)action;
			SudokuBoard board = ((SudokuEnvironment) env).getBoard();
			if(a.updateAssignment()) {
				List<Variable> vars = a.getAssignment().getVariables();
				for(Variable v : vars) {
					String[] str = v.getName().split(",");
					int i = new Integer(str[0]);
					int j = new Integer(str[1]);
					char digit = ((String) a.getAssignment().getAssignment(v)).charAt(0);
					board.setDigit(i, j, digit);
				}
			}
			showState();
			notify((agent == null ? "User: " : "") + action.toString());	
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			for(int x=0; x<SudokuBoard.SIZE; x++) {
				for(int y=0; y<SudokuBoard.SIZE; y++) {
					if(ae.getSource() == squareButton[x][y]) {
						String[] options = new String[SudokuBoard.VALUES.length()+1];
						
						options[0] = new String("<empty>");
						for(int i=1; i<options.length; i++) {
							options[i] = new String(SudokuBoard.VALUES.substring(i-1, i));
						}
						Object value = JOptionPane.showInputDialog(null, "Digit", "Input digit", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
						if(value != null) {
							if(value==options[0]) {
								squareButton[x][y].setText("");
								((SudokuController) getController()).modifySquare(x,y,SudokuBoard.EMPTY);
							}
							else {
								squareButton[x][y].setText(value.toString());
								((SudokuController) getController()).modifySquare(x,y,((String)value).charAt(0));
							}
						}
						
					}
				}
			}
		}
		
	}
	
	
	protected static class SudokuController extends AgentAppController {

		protected SudokuEnvironment env = null;
		protected boolean boardOK;
		protected SolutionStrategy strategy;
		protected FIFOQueue<CSPEnvironment.StateChangeAction> actions;
		protected int actionCount;
		
		protected SudokuController() {
			actions = new FIFOQueue<CSPEnvironment.StateChangeAction>();			
		}

		public void modifySquare(int x, int y, char digit) {
			if(digit==SudokuBoard.EMPTY) {
				env.board.removeDigit(x, y);
				if(!boardOK) boardOK = env.getBoard().isBoardOK();
			} else {
				env.board.setDigit(x, y, digit);
				if(boardOK) boardOK = env.getBoard().isAllowedDigit(x, y, digit);
			}
			frame.updateEnabledState();
			((SudokuView) frame.getEnvView()).showState();
		}

		@Override
		public void clear() {
			env = new SudokuEnvironment(new SudokuBoard());
			frame.getEnvView().setEnvironment(env);
			((SudokuView) frame.getEnvView()).showState();
			boardOK = false;
		}

		@Override
		public void prepare(String changedSelector) {
			if(changedSelector==null) {
				if(env==null) clear();
				else {
					env.getBoard().fixBoard();
					env.init(new SudokuCSP(env.getBoard()));
					boardOK = env.getBoard().isBoardOK();
					//if(boardOK) completeBoard();
					((SudokuView) frame.getEnvView()).showState();
				}
				actions.clear();
				actionCount = 0;
			}
			else if(changedSelector==SudokuFrame.FILE_SEL) {
				FileDialog fd = new FileDialog(frame);
				
				switch(frame.getSelection().getValue(SudokuFrame.FILE_SEL)) {
				case 0:	// Open
					fd.setTitle("Load Sudoku board...");
					fd.setMode(FileDialog.LOAD);
					fd.setVisible(true);
					if(fd.getFile() != null) {
						SudokuBoard board = null;
						try {
							ObjectInputStream in = new ObjectInputStream(new FileInputStream(fd.getFile()));
							board = (SudokuBoard) in.readObject();
							in.close();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						env = new SudokuEnvironment(board);
						frame.getMessageLogger().clear();
						frame.getEnvView().setEnvironment(env);
						((SudokuView) frame.getEnvView()).showState();
						//agent = null;
					}
					break;
				case 1:	// Save
					fd.setTitle("Save Sudoku board...");
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
			return env.getCSP() != null	&& (!actions.isEmpty() || env.getAssignment() == null) && boardOK;
			//return (agent==null || !agent.isDone()) && boardOK;
		}

		@Override
		public void run(MessageLogger logger) {
			logger.log("<simulation-log>");
			prepareActions();
			try {
				while (!actions.isEmpty() && !frame.simulationPaused()) {
					env.executeAction(null, actions.pop());
					actionCount++;
					Thread.sleep(200);
				}
				logger.log("Number of Steps: " + actionCount);
			} catch (InterruptedException e) {
				// nothing to do...
			} catch (Exception e) {
				e.printStackTrace(); // probably search has failed...
			}
			//logger.log(getStatistics());
			logger.log("</simulation-log>\n");
		}

		@Override
		public void step(MessageLogger logger) {
			prepareActions();
			if (!actions.isEmpty()) {
				env.executeAction(null, actions.pop());
				actionCount++;
				if (actions.isEmpty())
					logger.log("Number of Steps: " + actionCount);
			}
		}

		protected void prepareActions() {
			ImprovedBacktrackingStrategy iStrategy = null;
			if (actions.isEmpty()) {
				SolutionStrategy strategy = null;
				switch (frame.getSelection().getValue(SudokuFrame.STRATEGY_SEL)) {
				case 0:
					strategy = new BacktrackingStrategy();
					break;
				case 1: // MRV
					strategy = new ImprovedBacktrackingStrategy(true, false, false, false);
					break;
				case 2: // FC
					iStrategy = new ImprovedBacktrackingStrategy();
					iStrategy.setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);
					break;
				case 3: // MRV + FC 
					iStrategy = new ImprovedBacktrackingStrategy(true, false, false, false);
					iStrategy.setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);
					break;
				case 4: // AC3
					strategy = new ImprovedBacktrackingStrategy(false, false, true, false);
					break;
				case 5: // MRV + AC3
					strategy = new ImprovedBacktrackingStrategy(true, false, true, false);
					break;
				case 6:
					strategy = new MinConflictsStrategy(50);
					break;
				}
				if (iStrategy != null)
					strategy = iStrategy;
				
				try {
					strategy.addCSPStateListener(new CSPStateListener() {
						@Override
						public void stateChanged(Assignment assignment, CSP csp) {
							actions.add(new CSPEnvironment.StateChangeAction(
									assignment, csp));
						}
						@Override
						public void stateChanged(CSP csp) {
							actions.add(new CSPEnvironment.StateChangeAction(
									csp));
						}
					});
					strategy.solve(env.getCSP().copyDomains());
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	
	
	protected static class SudokuEnvironment extends CSPEnvironment {

		SudokuBoard board;
		
		public SudokuEnvironment(SudokuBoard b) {
			board = b;
			init(new SudokuCSP(b));
		}
		
		public SudokuBoard getBoard() {
			return board;
		}
		
		/*@Override
		public EnvironmentState getCurrentState() {
			return null;
		}

		@Override
		public EnvironmentState executeAction(Agent agent, Action action) {
			if(action instanceof StateChangeAction) {
				SudokuAction act = (SudokuAction)action;
				if(act.getName() == SudokuAction.SWAP_DIGIT) {
					char digit1 = board.getDigit(act.getX1(), act.getY1());
					board.setDigit(act.getX1(), act.getY1(), board.getDigit(act.getX2(), act.getY2()));
					board.setDigit(act.getX2(), act.getY2(), digit1);
				} else if(act.getName() == SudokuAction.PLACE_DIGIT)
					board.setDigit(act.getX(), act.getY(), act.getDigit());
				else
					board.removeDigit(act.getX(), act.getY());
				if (agent == null)
					updateEnvironmentViewsAgentActed(agent, action, null);
			}
			return null;
		}

		@Override
		public Percept getPerceptSeenBy(Agent anAgent) {
			return null;
		}*/
		
	}
}
