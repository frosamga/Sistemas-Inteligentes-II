package intsys.decision;


import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FrameMDP extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7503443588565448851L;
	
	WorldMDP world;
	LearningMDP learning;
	WorldPanel wp;
	
	// View types
	int CurrentView;
	public static final int StandardView = 0;
	public static final int RewardsView = 1;
	public static final int UtilitiesNumbersView = 2;
	public static final int UtilitiesColorsView = 3;
	public static final int OptimalPolicyView = 4;
	
	JMenuBar mb;
	JMenu menus[]={new JMenu("File"),new JMenu("Edit"),new JMenu("View"),new JMenu("Run"),new JMenu("Help")};
	int NumElems[]={4,2,5,2,1};
	JMenuItem elements[]={
			// File menu elements
			new JMenuItem("New"),new JMenuItem("Load"),
			new JMenuItem("Save"),new JMenuItem("Exit"),
			// Edit menu elements
			new JMenuItem("World"),new JMenuItem("Q-learning"),
			// View menu elements
			new JMenuItem("Standard"),new JMenuItem("Rewards"),new JMenuItem("Utilities as numbers"),new JMenuItem("Utilities as colors"),new JMenuItem("Optimal policy"),
			// Run menu elements
			new JMenuItem("Run"),new JMenuItem("Reset"),
			// Help menu elements
			new JMenuItem("About")};




	ActionListener al[]={
	// New menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			
			world=new WorldMDP();
			WorldOptions wo=new WorldOptions(FrameMDP.this,true,world,true);
			if (wo.DoChange)
			{
				world.NumRows=wo.NumRows;
				world.NumCols=wo.NumCols;
				world.ProbUp=wo.ProbUp;
				world.ProbDown=wo.ProbDown;
				world.ProbLeft=wo.ProbLeft;
				world.ProbRight=wo.ProbRight;
				world.ProbStay=wo.ProbStay;
				world.DefaultReward=wo.DefaultReward;
				world.DiscountFactor=wo.DiscountFactor;
				world.Setup();
				
				wp.SetWorld(world);
				wp.revalidate();
			}
			learning=new LearningMDP(world);

		}
	},
	// Load menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			FileDialog fd = new FileDialog(FrameMDP.this);
			fd.setTitle("Load Markov decision process...");
			fd.setMode(FileDialog.LOAD);
			fd.setVisible(true);
			if(fd.getFile() != null) {
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(fd.getFile()));
					world = (WorldMDP) in.readObject();
					in.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			wp.SetWorld(world);
			wp.revalidate();	
			learning=new LearningMDP(world);

		}
	},
	// Save menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			FileDialog fd = new FileDialog(FrameMDP.this);
			
			fd.setTitle("Save Markov decision process...");
			fd.setMode(FileDialog.SAVE);
			fd.setVisible(true);
			if(fd.getFile() != null) {
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fd.getFile()));
					out.writeObject(world);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}						
			}
 
		}
	},
	// Exit menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			processEvent(new WindowEvent(FrameMDP.this, WindowEvent.WINDOW_CLOSING));
 
		}
	},
	// World menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			
			if (world!=null)
			{
				WorldOptions wo=new WorldOptions(FrameMDP.this,true,world,false);
				if (wo.DoChange)
				{
					world.ProbUp=wo.ProbUp;
					world.ProbDown=wo.ProbDown;
					world.ProbLeft=wo.ProbLeft;
					world.ProbRight=wo.ProbRight;
					world.ProbStay=wo.ProbStay;
					world.DefaultReward=wo.DefaultReward;
					world.DiscountFactor=wo.DiscountFactor;
					
					wp.SetWorld(world);
					wp.revalidate();
				}
			} 
			else
			{
				JOptionPane.showMessageDialog(FrameMDP.this,"Sorry, you must create or load a MDP first");
			}
		}
	},
	// Q-learning menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			if (world!=null)
			{				
				LearningOptions lo=new LearningOptions(FrameMDP.this,true,learning);
				if (lo.DoChange)
				{
					learning.LearningRateParameter=lo.LearningRateParameter;
					learning.OptimisticReward=lo.OptimisticReward;
					learning.MinimumTrials=lo.MinimumTrials;
					learning.NumSteps=lo.NumSteps;
				}
			}
			else
			{
				JOptionPane.showMessageDialog(FrameMDP.this,"Sorry, you must create or load a MDP first");
			}
		}
	},
	// Standard menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			CurrentView=FrameMDP.StandardView;
			wp.revalidate();
			
		}		
	},
	// Rewards menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			CurrentView=FrameMDP.RewardsView;
			wp.revalidate();
			
		}		
	},
	// Utilities as numbers menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			CurrentView=FrameMDP.UtilitiesNumbersView;
			wp.revalidate();
			
		}		
	},
	// Utilities as colors element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			CurrentView=FrameMDP.UtilitiesColorsView;
			wp.revalidate();
			
		}		
	},
	// Optimal policy element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			CurrentView=FrameMDP.OptimalPolicyView;
			wp.revalidate();
			
		}		
	},	
	// Run menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
 
			if (world!=null)
			{				
				learning.Learn();
			}
			else
			{
				JOptionPane.showMessageDialog(FrameMDP.this,"Sorry, you must create or load a MDP first");
			}
		}
	},
	// Reset menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
 
			if (world!=null)
			{				
				learning.Reset();
			}
			else
			{
				JOptionPane.showMessageDialog(FrameMDP.this,"Sorry, you must create or load a MDP first");
			}
		}
	},
	// About menu element
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			JOptionPane.showMessageDialog(FrameMDP.this,"Markov decision processes demo application\nEzequiel López Rubio\nUniversity of Málaga (Spain)\nThis software is for academic evaluation purposes only");

		}
	}
	
	};
	
	// This class manages the panel where the MDP is shown
	class WorldPanel extends JPanel implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7684990030054582429L;

		JButton[][] squareButton;
		int NumRows=0,NumCols=0;
		WorldMDP world=null;
		
		// Compute the hue for the rainbow color map
		float GetHue(double x)
		{
			double ColorRange;
			float Aux;
			ColorRange=world.MaxUtility-world.MinUtility+0.000001;
			
			// Aux is between 0.0f and 1.0f
			Aux=(float)((x-world.MinUtility)/ColorRange);
			return 0.666666f-0.666666f*Aux;
			
		}
		
		// Paint a state of the MDP
		public void paintButton(Graphics g, int x, int y)
		{
			
			NumberFormat nf=NumberFormat.getInstance(Locale.ENGLISH);			
			nf.setMaximumFractionDigits(3);
			
			// Plot the state according to the current view type
			switch(CurrentView)			
			{
			case FrameMDP.StandardView:
				switch(world.StateType[x][y])
				{
				case WorldMDP.NormalState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.TerminalState:					
					squareButton[x][y].setText(nf.format(world.Reward[x][y]));
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.InaccessibleState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(Color.DARK_GRAY);
					break;
				case WorldMDP.InitialState:
					squareButton[x][y].setText("START");
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				}
				break;
			case FrameMDP.RewardsView:	
				switch(world.StateType[x][y])
				{
				case WorldMDP.NormalState:
					squareButton[x][y].setText(nf.format(world.Reward[x][y]));
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.TerminalState:
					squareButton[x][y].setText(nf.format(world.Reward[x][y]));
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.InaccessibleState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(Color.DARK_GRAY);
					break;
				case WorldMDP.InitialState:
					squareButton[x][y].setText(nf.format(world.Reward[x][y]));
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				}
				break;
			case FrameMDP.UtilitiesNumbersView:	
				switch(world.StateType[x][y])
				{
				case WorldMDP.NormalState:
					squareButton[x][y].setText(nf.format(world.Utility[x][y]));
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.TerminalState:
					squareButton[x][y].setText(nf.format(world.Utility[x][y]));
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.InaccessibleState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(Color.DARK_GRAY);
					break;
				case WorldMDP.InitialState:
					squareButton[x][y].setText(nf.format(world.Utility[x][y]));
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				}
				break;
			case FrameMDP.UtilitiesColorsView:			
				switch(world.StateType[x][y])
				{
				case WorldMDP.NormalState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(
							Color.getHSBColor(GetHue(world.Utility[x][y]),
									1.0f,0.8f));
					break;
				case WorldMDP.TerminalState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(
							Color.getHSBColor(GetHue(world.Utility[x][y]),
									1.0f,0.8f));
					break;
				case WorldMDP.InaccessibleState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(Color.DARK_GRAY);
					break;
				case WorldMDP.InitialState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(
							Color.getHSBColor(GetHue(world.Utility[x][y]),
									1.0f,0.8f));
					break;
				}
				break;
			case FrameMDP.OptimalPolicyView:			
				switch(world.StateType[x][y])
				{
				case WorldMDP.NormalState:
					squareButton[x][y].setText(WorldMDP.ActionSymbols[world.OptimalPolicy[x][y]]);
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.TerminalState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				case WorldMDP.InaccessibleState:
					squareButton[x][y].setText("");
					squareButton[x][y].setBackground(Color.DARK_GRAY);
					break;
				case WorldMDP.InitialState:
					squareButton[x][y].setText(WorldMDP.ActionSymbols[world.OptimalPolicy[x][y]]);
					squareButton[x][y].setBackground(Color.WHITE);
					break;
				}
				break;
			
			}
			
		}
		
		// Repaint the panel
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// If a world is loaded, then paint its states 
			if (world!=null)
			{
				for(int x=0; x<NumRows; x++) {
					for(int y=0; y<NumCols; y++) {
						paintButton(g,x,y);						
					}
				}				
			}	
		}
		
		// Prepare the JButton's which represent the MDP states
		public void SetWorld(WorldMDP myworld){
			if (world!=null)
			{
				for(int x=0; x<NumRows; x++) {
					for(int y=0; y<NumCols; y++) {
						remove(squareButton[x][y]);
					}
				}					
			}
			world=myworld;
			NumRows=world.NumRows;
			NumCols=world.NumCols;

			squareButton = new JButton[NumRows][NumCols];

			setLayout(new GridLayout(NumRows, NumCols));
			for(int x=0; x<NumRows; x++) {
				for(int y=0; y<NumCols; y++) {
					JButton square = new JButton("");
					square.addActionListener(this);
					squareButton[x][y] = square;
					add(square);						
				}
			}	
		}

		// Manage the JButton's which represent the states of the MDP
		@Override
		public void actionPerformed(ActionEvent ae) {
			boolean Located=false;
			int ButtonX=0,ButtonY=0;
			
			// Locate the pressed button
			for(int x=0; x<NumRows; x++) {
				for(int y=0; y<NumCols; y++) {
					if(ae.getSource() == squareButton[x][y]) {
						Located=true;
						ButtonX=x;
						ButtonY=y;
					}
				}
			}
			
			if (Located)
			{
				if ((ae.getModifiers() & ActionEvent.SHIFT_MASK)>0) {
					// SHIFT+left click = Set state type
					Object[] options = { "Normal", "Terminal", "Inaccessible", "Initial" };
					int ChosenOption=JOptionPane.showOptionDialog(FrameMDP.this, "Select state type", "Modify state type", 
							JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
							null, options, options[0]);
					switch(ChosenOption) {
					case 0:
						world.StateType[ButtonX][ButtonY]=WorldMDP.NormalState;
						break;
					case 1:
						if (world.StateType[ButtonX][ButtonY]!=WorldMDP.InitialState)
						{
							world.StateType[ButtonX][ButtonY]=WorldMDP.TerminalState;							
						}
						else
						{
							JOptionPane.showMessageDialog(FrameMDP.this, "You must move the initial state first",
									"Error",JOptionPane.ERROR_MESSAGE);
						}						
						break;
					case 2:
						if (world.StateType[ButtonX][ButtonY]!=WorldMDP.InitialState)
						{
							world.StateType[ButtonX][ButtonY]=WorldMDP.InaccessibleState;							
						}
						else
						{
							JOptionPane.showMessageDialog(FrameMDP.this, "You must move the initial state first",
									"Error",JOptionPane.ERROR_MESSAGE);
						}						
						break;
					case 3:
						world.StateType[world.InitialRow][world.InitialCol]=WorldMDP.NormalState;
						world.StateType[ButtonX][ButtonY]=WorldMDP.InitialState;
						world.InitialRow=ButtonX;
						world.InitialCol=ButtonY;
						break;							
					}	
					repaint();					
				}
				else if ((ae.getModifiers() & ActionEvent.CTRL_MASK)>0) {
					// CTRL+left click = Set reward
					String inputValue = JOptionPane.showInputDialog("Please input the state reward");
					if (inputValue!=null)
					{
						try
						{
							world.Reward[ButtonX][ButtonY]=Double.parseDouble(inputValue);
						}
						catch (NumberFormatException e)
						{
							JOptionPane.showMessageDialog(this,"Invalid reward (must be an real number)");
						}
						repaint();						
					}
					
				}
				else
				{
					// Left click = See state information

					String message="Information for state ("
						+ButtonX+","+ButtonY+"):\n"+
						"Type: "+WorldMDP.StateTypeNames[world.StateType[ButtonX][ButtonY]];
					if (world.StateType[ButtonX][ButtonY]!=WorldMDP.InaccessibleState)
					{
						message=message+"\n"+
						"Reward: "+world.Reward[ButtonX][ButtonY]+"\n"+
						"Utility: "+world.Utility[ButtonX][ButtonY];
						if (world.StateType[ButtonX][ButtonY]!=WorldMDP.TerminalState)
						{
							message=message+"\n"+
							"Recommended action: "+WorldMDP.ActionNames[world.OptimalPolicy[ButtonX][ButtonY]];
						}
					}
					JOptionPane.showMessageDialog(FrameMDP.this, message,
							"State information", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	
	FrameMDP(){
		// Window title
        setTitle("Markov decision processes demo application");
        
        // Initialize view type
        CurrentView=FrameMDP.StandardView;
        
        // Initialize the menu elements, the menu and the menu bar
        mb=new JMenuBar();
        for(int NdxMenu=0,NdxElem=0;NdxMenu<menus.length;NdxMenu++)
        {
        	mb.add(menus[NdxMenu]);
        	for(int i=0;i<NumElems[NdxMenu];i++,NdxElem++)
        	{
        		elements[NdxElem].addActionListener(al[NdxElem]);
        		menus[NdxMenu].add(elements[NdxElem]);        		
        	}        	
        }
        
        // Set the menu bar of the window
        setJMenuBar(mb);
        
        // Create and add the panel for the MDP
        wp=new WorldPanel();
        getContentPane().add(wp);

        // Set the size of the window
        setSize(400,400);
        
        // Set the default close operation for the window, or else the
        // program won't exit when clicking close button
        //  (The default is HIDE_ON_CLOSE, which just makes the window
        //  invisible, and thus doesn't exit the app)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the visibility as true, thereby displaying it
        setVisible(true);
	}
	
}
