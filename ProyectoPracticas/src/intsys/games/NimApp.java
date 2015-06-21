package intsys.games;

import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.AlphaBetaSearch;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import aima.core.search.adversarial.MinimaxSearch;
import aima.core.search.framework.Metrics;
//import aima.core.util.datastructure.XYLocation;
import aima.gui.framework.MessageLoggerPanel;

/**
 * Simple graphical Nim game application. It demonstrates the Minimax
 * algorithm for move selection as well as alpha-beta pruning.
 * 
 * @author Enrique Dominguez and Ezequiel Lopez-Rubio
 */
public class NimApp {

	/** Used for integration into the universal demo application. */
	public JFrame constructApplicationFrame() {
		JFrame frame = new JFrame();
		JPanel panel = new NimPanel();
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}

	/** Application starter. */
	public static void main(String[] args) {
		JFrame frame = new NimApp().constructApplicationFrame();
		frame.setTitle("Nim Game");
		frame.setSize(800, 400);
		frame.setVisible(true);
	}

	/** Simple panel to control the game. */
	private static class NimPanel extends JPanel implements
			ActionListener {
		private static final long serialVersionUID = 1L;
		JComboBox<String> strategy;
		JButton clear;
		JButton proposal;

		JLabel[][] boardHeaps;
		ImageIcon icon;
		JComboBox[] remove;
		MessageLoggerPanel msgPane;
		JLabel status;

		Nim game;
		NimBoard currState;
		Metrics searchMetrics;
		int maxHeap;

		/** Standard constructor. */
		NimPanel() {
			game=new Nim();
			this.setLayout(new BorderLayout());

			JToolBar tbar = new JToolBar();
			tbar.setFloatable(false);
			strategy = new JComboBox<String>(new String[] { "Minimax",
					"Alpha-Beta", "Iterative Deepening Alpha-Beta",
				"Iterative Deepening Alpha-Beta (log)" });
			strategy.setSelectedIndex(1);
			tbar.add(strategy);
			tbar.add(Box.createHorizontalGlue());
			clear = new JButton("Clear");
			clear.addActionListener(this);
			tbar.add(clear);
			proposal = new JButton("Propose Move");
			proposal.addActionListener(this);
			tbar.add(proposal);
			add(tbar, BorderLayout.NORTH);
			
			JSplitPane centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			centerPane.setDividerSize(5);
			centerPane.setResizeWeight(0.1);
			
			JPanel spanel = new JPanel();
			spanel.setBackground(Color.white);
			maxHeap = getMaxHeap();
			spanel.setLayout(new GridLayout(maxHeap+1,NimBoard.HEAP_SIZE.length));
			boardHeaps = new JLabel[NimBoard.HEAP_SIZE.length][maxHeap];
			icon = new ImageIcon("cerilla.jpg");
			for(int i=0; i<maxHeap; i++) {
				for(int j=0; j<NimBoard.HEAP_SIZE.length; j++) {
					JLabel label = new JLabel("");
					if((maxHeap-i) <= NimBoard.HEAP_SIZE[j]) label.setIcon(icon);
					//JLabel label = new JLabel((maxHeap-i) > NimBoard.HEAP_SIZE[j] ? " " : "*");
					label.setHorizontalAlignment(JLabel.CENTER);
					boardHeaps[j][i] = label;
					spanel.add(label);
				}
			}

			remove = new JComboBox[NimBoard.HEAP_SIZE.length];
			for(int i=0; i<NimBoard.HEAP_SIZE.length; i++) {
				remove[i] = new JComboBox<Integer>();
				for(int j=0; j<=NimBoard.HEAP_SIZE[i]; j++)
					remove[i].addItem(j);
				remove[i].addActionListener(this);
				remove[i].setToolTipText("Items to be removed");
				spanel.add(remove[i]);
			}
			centerPane.add(JSplitPane.LEFT, spanel);

			msgPane = new MessageLoggerPanel();
			centerPane.add(JSplitPane.RIGHT, msgPane);

			add(centerPane, BorderLayout.CENTER);
			
			status = new JLabel(" ");
			status.setBorder(BorderFactory.createEtchedBorder());
			add(status, BorderLayout.SOUTH);
			
			actionPerformed(null);
			
		}
		
		private int getMaxHeap() {
			int m = NimBoard.HEAP_SIZE[0];
			
			for(int i=1; i<NimBoard.HEAP_SIZE.length; i++)
				if(m < NimBoard.HEAP_SIZE[i]) m = NimBoard.HEAP_SIZE[i];
			return m;
		}

		/** Handles all button events and updates the view. */
		@Override
		public void actionPerformed(ActionEvent ae) {
			searchMetrics = null;
			if (ae == null || ae.getSource() == clear)
			{
				currState = game.getInitialState();
				refreshPanel();
				msgPane.clear();
			}
			else if (!game.isTerminal(currState)) {
				if (ae.getSource() == proposal)
				{
					String currPlayer=game.getPlayer(currState).toString();
					proposeMove();		
					refreshPanel();
					msgPane.log(currPlayer + ": Proposed");
				}
				else {
					for (int i = 0; i < NimBoard.HEAP_SIZE.length; i++)
						if (ae.getSource() == remove[i] && remove[i].getSelectedIndex() >= 0) {
							int items = (Integer) remove[i].getSelectedItem();
							NimMove move = new NimMove(i, items);
							
							if(move.isValid(currState)) {
								msgPane.log(game.getPlayer(currState) + ": Remove " + items + " items from heap " + i);
								//msgPane.log(": Remove " + items + " items from heap " + i + "\n");
								currState=game.getResult(currState, move);

								for(int j=0; j<maxHeap; j++) {
									boardHeaps[i][j].setIcon((maxHeap-j) > currState.items(i) ? null : icon);
								}
								for(int lastItem=remove[i].getItemCount()-1; items>0; items--) {
									remove[i].removeItemAt(lastItem);
									lastItem--;
								}
								remove[i].setSelectedIndex(-1);
							}
						}
				}
			}

			
			updateStatus();
		}

		/** Uses adversarial search for selecting the next action. */
		private void proposeMove() {
			AdversarialSearch<NimBoard, NimMove> search;
			NimMove action;
			switch (strategy.getSelectedIndex()) {
			case 0:
				search = MinimaxSearch.createFor(game);
				break;
			case 1:
				search = AlphaBetaSearch.createFor(game);
				break;
			case 2:
				search = IterativeDeepeningAlphaBetaSearch.createFor(game, 0.0,
						1.0, 1000);
				break;
			default:
				search = IterativeDeepeningAlphaBetaSearch.createFor(game, 0.0,
						1.0, 1000);
				((IterativeDeepeningAlphaBetaSearch<?, ?, ?>) search)
						.setLogEnabled(true);
			}
			action = search.makeDecision(currState);
			searchMetrics = search.getMetrics();
			currState = game.getResult(currState, action);
		}

		/** Updates the status bar. */
		private void updateStatus() {
			String statusText;
			if (game.isTerminal(currState))
				if (game.getUtility(currState, NimPlayer.PLAYER_1) == 1)
					statusText = NimPlayer.PLAYER_1+" has won :-)";
				else if (game.getUtility(currState, NimPlayer.PLAYER_2) == 1)
					statusText = NimPlayer.PLAYER_2+" has won :-)";
				else
					statusText = "No winner...";
			else
				statusText = "Next move: " + game.getPlayer(currState);
			if (searchMetrics != null)
				statusText += "    " + searchMetrics;
			status.setText(statusText);
		}
		
		private void refreshPanel() {
			for (int i = 0; i < NimBoard.HEAP_SIZE.length; i++) {	
				for(int j=0; j<maxHeap; j++) {
					boardHeaps[i][j].setIcon((maxHeap-j) > currState.items(i) ? null : icon);
				}
				remove[i].removeAllItems();
				for(int j=0; j<=currState.items(i); j++)
					remove[i].addItem(j);
			}		
		}
	}
}
