package intsys.svm;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.agent.Percept;
import aima.core.agent.impl.AbstractEnvironment;
import aima.gui.framework.AgentAppController;
import aima.gui.framework.AgentAppEnvironmentView;
import aima.gui.framework.AgentAppFrame;
import aima.gui.framework.MessageLogger;
import aima.gui.framework.SimpleAgentApp;
import aima.gui.framework.SimulationThread;


public class SVM_App extends SimpleAgentApp {

	public static String[] KERNEL_NAMES = new String[] {
		"Linear",
		"Polynomial",
		"Radial Basis Function",
		"Sigmoid"
	};
	
	public static String[] DATASET_FILENAMES = new String[] {
		"iris.txt",
		"glass.txt",
		"satimage.txt"
	};

	
	public AgentAppEnvironmentView createEnvironmentView() {
		return new SVM_View();
	}
	
	public AgentAppFrame createFrame() {
		return new SVM_Frame();
	}
	
	public AgentAppController createController() {
		return new SVM_Controller();
	}
	
	public static void main(String argv[]) {
		new SVM_App().startApplication();
	}
	
	
	protected static class SVM_Frame extends AgentAppFrame {
		private static final long serialVersionUID = 1L;
		public static String FILE_SEL = "FileSelection";
		public static String DATASET_SEL = "DataSelection";
		
		public SVM_Frame() {
			setTitle("SVM Application");
			setSelectors(new String[] {FILE_SEL, DATASET_SEL},
					new String[] {"FIle", "Select DataSet"});
			setSelectorItems(FILE_SEL, new String[] {"Open", "Save"}, -1);
			setSelectorItems(DATASET_SEL, new String[] {
					"Iris",
					"Glass",
					"Satimage"
			}, 0);
			centerPane.setResizeWeight(0.6);
			setEnvView(new SVM_View());
			setSize(800,600);
		}
		
	}
	
	
	protected static class SVM_View extends AgentAppEnvironmentView implements ActionListener {
		private static final long serialVersionUID = 1L;

		protected JRadioButton[] rdbtnKernel;
		protected JLabel lblDegree, lblGamma, lblCoef, lblKernel;
		protected JSpinner spinDegree;
		protected JTextField txtCoef, txtGamma;
		protected ButtonGroup grpKernel;
		
		protected SVM_View() {
			setLayout(null);
			rdbtnKernel = new JRadioButton[KERNEL_NAMES.length];
			grpKernel = new ButtonGroup();
			for(int i=0; i<KERNEL_NAMES.length; i++) {
				JRadioButton rdbtn = new JRadioButton(KERNEL_NAMES[i]);
				rdbtn.setBounds(50, 50 + 25*i, 150, 25);
				rdbtn.addActionListener(this);
				rdbtnKernel[i] = rdbtn;
				grpKernel.add(rdbtn);
				add(rdbtn);
			}
			grpKernel.setSelected(rdbtnKernel[0].getModel(), true);
			
			lblKernel = new JLabel("Kernel function");
			lblKernel.setFont(new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 20));
			lblKernel.setBounds(20, 20, 200, 25);
			add(lblKernel);
			
			lblDegree = new JLabel("Degree");
			lblDegree.setBounds(300, 75, 50, 25);
			add(lblDegree);
			lblGamma = new JLabel("Gamma");
			lblGamma.setBounds(300, 100, 50, 25);
			add(lblGamma);
			lblCoef = new JLabel("Coef.");
			lblCoef.setBounds(300, 125, 50, 25);
			add(lblCoef);
			
			spinDegree = new JSpinner();
			spinDegree.setModel(new SpinnerNumberModel(new Integer(0), 0, null, 1));
			spinDegree.setBounds(350, 75, 40, 25);
			add(spinDegree);
			txtGamma= new JTextField("0.0");
			//txtGamma.setModel(new SpinnerNumberModel(new Integer(0), 0, null, 1));
			txtGamma.setBounds(350, 100, 40, 25);
			add(txtGamma);
			txtCoef = new JTextField("0");
			txtCoef.setBounds(350, 125, 40, 25);
			add(txtCoef);
		}
		
		protected void showState() {
			svm_parameter param = ((SVM_Environment) env).getParameters();
			grpKernel.setSelected(rdbtnKernel[param.kernel_type].getModel(), true);
			spinDegree.setValue(param.degree);
			txtGamma.setText(String.valueOf(param.gamma));
			txtCoef.setText(String.valueOf(param.coef0));
			updateVisibility();
		}
		
		protected void updateVisibility() {
			for(int i=0; i<KERNEL_NAMES.length; i++) {
				if(grpKernel.getSelection() == rdbtnKernel[i].getModel()) {
					lblDegree.setVisible(i==1);
					spinDegree.setVisible(i==1);	// Polynomial
					lblGamma.setVisible(i!=0);
					txtGamma.setVisible(i!=0);	// Not linear
					lblCoef.setVisible(i==1 || i==3);
					txtCoef.setVisible(i==1 || i==3);	// Polynomial or Sigmoid
				}
			}
		}
		
		@Override
		public void agentAdded(Agent agent, EnvironmentState resultingState) {
			
		}

		@Override
		public void agentActed(Agent agent, Action action,
				EnvironmentState resultingState) {
			
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			((SVM_Controller)getController()).modifyParam();
			updateVisibility();
		}
		
	}
	
	
	
	protected static class SVM_Controller extends AgentAppController {

		SVM_Environment env = null;
		boolean updatedParam = false;

		protected svm_parameter createDefaultParam() {
			svm_parameter param = new svm_parameter();
			// default values
			param.svm_type = svm_parameter.C_SVC;
			param.kernel_type = svm_parameter.RBF;
			param.degree = 3;
			param.gamma = 0;
			param.coef0 = 0;
			param.nu = 0.5;
			param.cache_size = 40;
			param.C = 1;
			param.eps = 1e-3;
			param.p = 0.1;
			param.shrinking = 1;
			param.probability = 0;
			param.nr_weight = 0;
			param.weight_label = new int[0];
			param.weight = new double[0];
			
			return param;
		}
		
		protected svm_problem loadProblem(String filename) {
			Vector<Double> target = new Vector<Double>();
			Vector<svm_node[]> input = new Vector<svm_node[]>();
			
			try {
				BufferedReader fp = new BufferedReader(new FileReader(filename));
				String line;
				while((line = fp.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
					target.addElement(Double.valueOf(st.nextToken()));
					svm_node[] x = new svm_node[st.countTokens()/2];
					for(int i=0; i<x.length; i++) {
						x[i] = new svm_node();
						x[i].index = Integer.valueOf(st.nextToken());
						x[i].value = Double.valueOf(st.nextToken());
					}
					input.addElement(x);
				}
				fp.close();
			} catch (IOException e) { System.err.print(e); }

			svm_problem prob = new svm_problem();
			prob.l = target.size();
			prob.x = new svm_node[prob.l][];
			prob.y = new double[prob.l];
			for(int i=0; i<prob.l; i++) prob.x[i] = input.elementAt(i);
			for(int i=0; i<prob.l; i++) prob.y[i] = target.elementAt(i);
			
			return prob;
		}
		
		public void modifyParam() {
			updatedParam = false;
			frame.updateEnabledState();
		}
		
		@Override
		public void clear() {
			env = new SVM_Environment(
					loadProblem(DATASET_FILENAMES[frame.getSelection().getValue(SVM_Frame.DATASET_SEL)]),
					createDefaultParam());
			updatedParam = false;
			frame.getEnvView().setEnvironment(env);
			((SVM_View) frame.getEnvView()).showState();
		}

		@Override
		public void prepare(String changedSelector) {
			if(changedSelector == SVM_Frame.FILE_SEL) {
				FileDialog fd = new FileDialog(frame);
				switch(frame.getSelection().getValue(SVM_Frame.FILE_SEL)) {
				case 0:	// Open
					fd.setTitle("Load Kernel function...");
					fd.setMode(FileDialog.LOAD);
					fd.setVisible(true);
					if(fd.getFile() != null) {
						try {
							ObjectInputStream in = new ObjectInputStream(new FileInputStream(fd.getFile()));
							svm_parameter param = (svm_parameter) in.readObject();
							in.close();
							env.setParameters(param);
							frame.getMessageLogger().clear();
							((SVM_View) frame.getEnvView()).showState();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
					break;
				case 1:	// Save
					fd.setTitle("Save Kernel function...");
					fd.setMode(FileDialog.SAVE);
					fd.setVisible(true);
					if(fd.getFile() != null) {
						try {
							ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fd.getFile()));
							out.writeObject(env.getParameters());
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
					break;
				}
			}
			else if(changedSelector == SVM_Frame.DATASET_SEL) {
				env.setProblem(loadProblem(DATASET_FILENAMES[frame.getSelection().getValue(SVM_Frame.DATASET_SEL)]));
			}
			else {
				if(env == null) clear();
				else {
					SVM_View view = (SVM_View) frame.getEnvView();
					svm_parameter param = env.getParameters();
					
					for(int i=0; i<KERNEL_NAMES.length; i++)
						if(view.rdbtnKernel[i].isSelected()) param.kernel_type = i;
					param.degree = (Integer) view.spinDegree.getValue();
					param.gamma = Math.abs(Double.valueOf(view.txtGamma.getText()));
					view.txtGamma.setText(String.valueOf(param.gamma));
					param.coef0 = Double.valueOf(view.txtCoef.getText());

					env.setParameters(param);
					updatedParam = true;
				}
			}
		}

		@Override
		public boolean isPrepared() {
			return updatedParam;
		}

		@Override
		public void run(MessageLogger logger) {
			step(logger);
		}

		@Override
		public void step(MessageLogger logger) {
			frame.getMessageLogger().log("Training...");
			svm_model model = svm.svm_train(env.getProblem(), env.getParameters());
			int total = env.getProblem().l;
			frame.getMessageLogger().log("Trainned patterns: " + total);
			int correct = 0;
			for(int i=0; i<total; i++) {
				if(env.getProblem().y[i] == svm.svm_predict(model, env.getProblem().x[i])) correct++;
			}
			frame.getMessageLogger().log("Accuracy: " + (double)correct*100/total + "%\n");
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
	
	
	protected static class SVM_Environment extends AbstractEnvironment {

		svm_problem prob;
		svm_parameter param;
		svm_model svm;
		
		public SVM_Environment(svm_problem prob, svm_parameter param) {
			this.prob = prob;
			this.param = param;
		}
		
		public svm_problem getProblem() {
			return prob;
		}
		
		public svm_parameter getParameters() {
			return param;
		}
		
		public void setProblem(svm_problem prob) {
			this.prob = prob;
		}
		
		public void setParameters(svm_parameter param) {
			this.param = param;
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
}
