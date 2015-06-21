package intsys.decision;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class LearningOptions extends JDialog implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5899188392780206383L;
	
	
	double LearningRateParameter,OptimisticReward;
	int MinimumTrials,NumSteps;
	
	boolean DoChange;
	private Container cp;
	private JTextField tfLearningRateParameter,tfOptimisticReward,tfMinimumTrials,tfNumSteps;
	private Box myBox,VertBox;
    private JButton OKButton = null;
    private JButton CancelButton = null;

    
    public LearningOptions(JFrame frame, boolean modal, LearningMDP learning) {
		super(frame, modal);
		
		// Loading previous values
		LearningRateParameter=learning.LearningRateParameter;
		OptimisticReward=learning.OptimisticReward;
		MinimumTrials=learning.MinimumTrials;
		NumSteps=learning.NumSteps;
		
		// Adding controls to this JDialog
        cp=getContentPane();
        cp.setLayout(new FlowLayout());
        
        VertBox=Box.createVerticalBox();
        

        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Learning rate parameter"));
        tfLearningRateParameter = new JTextField(Double.toString(LearningRateParameter),5);
        tfLearningRateParameter.addActionListener(this);
        myBox.add(tfLearningRateParameter);
        VertBox.add(myBox);
        
        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Optimistic reward"));
        tfOptimisticReward = new JTextField(Double.toString(OptimisticReward),5);
        tfOptimisticReward.addActionListener(this);
        myBox.add(tfOptimisticReward);
        VertBox.add(myBox);        

        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Minimum trials"));
        tfMinimumTrials = new JTextField(Integer.toString(MinimumTrials),5);
        tfMinimumTrials.addActionListener(this);
        myBox.add(tfMinimumTrials);
        VertBox.add(myBox);   
        
        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Number of steps"));
        tfNumSteps = new JTextField(Integer.toString(NumSteps),5);
        tfNumSteps.addActionListener(this);
        myBox.add(tfNumSteps);
        VertBox.add(myBox);     
        
        myBox=Box.createHorizontalBox();
        OKButton = new JButton("OK");
        OKButton.addActionListener(this);
        myBox.add(OKButton); 
        CancelButton = new JButton("Cancel");
        CancelButton.addActionListener(this);
        myBox.add(CancelButton);  
        VertBox.add(myBox);
        
        cp.add(VertBox);
        
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }
    
    // Check whether the dialog data is valid
	public boolean ValidDialogData()
	{
		boolean FormatError=false;
			
		try
		{
			LearningRateParameter=Double.parseDouble(tfLearningRateParameter.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid learning rate parameter (must be a strictly positive real number)");
			tfLearningRateParameter.setText(Double.toString(LearningRateParameter));	
			FormatError=true;
		}
		
		try
		{
			OptimisticReward=Double.parseDouble(tfOptimisticReward.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid optimistic reward (must be a strictly positive real number)");
			tfOptimisticReward.setText(Double.toString(OptimisticReward));	
			FormatError=true;
		}
		
		try
		{
			MinimumTrials=Integer.parseInt(tfMinimumTrials.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid minimum number of trials (must be a natural number)");
			tfMinimumTrials.setText(Integer.toString(MinimumTrials));	
			FormatError=true;
		}
		
		try
		{
			NumSteps=Integer.parseInt(tfNumSteps.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid minimum number of steps (must be a natural number)");
			tfNumSteps.setText(Integer.toString(NumSteps));
			FormatError=true;
		}
		
		return  (!FormatError) && (LearningRateParameter>0.0) && (OptimisticReward>0.0) 
			&& (MinimumTrials>=0) && (NumSteps>=0);
	}
	
	// Manage dialog buttons
	public void actionPerformed(ActionEvent e) {
        if(OKButton == e.getSource()) {
        	if ( ValidDialogData() )
        	{
        		DoChange=true;
        		setVisible(false);
        	}
        	else
        	{
        		JOptionPane.showMessageDialog(this,"Invalid learning parameters, please correct them");
        	}
    
        }
        else if(CancelButton == e.getSource()) {
        	DoChange=false;            
            setVisible(false);
        }
    }


}
