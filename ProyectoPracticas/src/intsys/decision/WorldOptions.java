package intsys.decision;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class WorldOptions extends JDialog implements ActionListener {

	int NumRows,NumCols;
	double ProbUp,ProbLeft,ProbRight,ProbDown,ProbStay,DefaultReward,DiscountFactor;
	boolean ChangeWorldSize;
	boolean DoChange;
	private Container cp;
	private JTextField tfNumRows,tfNumCols,tfProbUp,tfProbLeft,tfProbRight,tfProbDown,
		tfProbStay,tfDefaultReward,tfDiscountFactor;
	private Box myBox,VertBox;
    private JButton OKButton = null;
    private JButton CancelButton = null;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8556042912126474232L;
	
	public WorldOptions(JFrame frame, boolean modal, WorldMDP world,boolean ChangeSize) {
		super(frame, modal);
		ChangeWorldSize=ChangeSize;
		
		// Loading previous values
		NumRows=world.NumRows;
		NumCols=world.NumCols;
		ProbUp=world.ProbUp;
		ProbLeft=world.ProbLeft;
		ProbRight=world.ProbRight;
		ProbDown=world.ProbDown;
		ProbStay=world.ProbStay;
		DefaultReward=world.DefaultReward;
		DiscountFactor=world.DiscountFactor;
		
		// Adding controls to this JDialog
        cp=getContentPane();
        cp.setLayout(new FlowLayout());
        
        VertBox=Box.createVerticalBox();
        
        if (ChangeWorldSize)
        {
            myBox=Box.createHorizontalBox();
            myBox.add(new JLabel("Map rows"));
            tfNumRows = new JTextField(Integer.toString(NumRows),5);
            tfNumRows.addActionListener(this);
            myBox.add(tfNumRows);
            VertBox.add(myBox);
            
            myBox=Box.createHorizontalBox();
            myBox.add(new JLabel("Map columns"));
            tfNumCols = new JTextField(Integer.toString(NumCols),5);
            tfNumCols.addActionListener(this);
            myBox.add(tfNumCols);
            VertBox.add(myBox);        	
        }
  

        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Up probability"));
        tfProbUp = new JTextField(Double.toString(ProbUp),5);
        tfProbUp.addActionListener(this);
        myBox.add(tfProbUp);
        VertBox.add(myBox);
        
        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Down probability"));
        tfProbDown = new JTextField(Double.toString(ProbDown),5);
        tfProbDown.addActionListener(this);
        myBox.add(tfProbDown);
        VertBox.add(myBox);  

        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Left probability"));
        tfProbLeft = new JTextField(Double.toString(ProbLeft),5);
        tfProbLeft.addActionListener(this);
        myBox.add(tfProbLeft);
        VertBox.add(myBox);
        
        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Right probability"));
        tfProbRight = new JTextField(Double.toString(ProbRight),5);
        tfProbRight.addActionListener(this);
        myBox.add(tfProbRight);
        VertBox.add(myBox);   
        
        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Stay probability"));
        tfProbStay = new JTextField(Double.toString(ProbStay),5);
        tfProbStay.addActionListener(this);
        myBox.add(tfProbStay);
        VertBox.add(myBox); 
        
        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Default reward"));
        tfDefaultReward = new JTextField(Double.toString(DefaultReward),5);
        tfDefaultReward.addActionListener(this);
        myBox.add(tfDefaultReward);
        VertBox.add(myBox);   
        
        myBox=Box.createHorizontalBox();
        myBox.add(new JLabel("Discount factor"));
        tfDiscountFactor = new JTextField(Double.toString(DiscountFactor),5);
        tfDiscountFactor.addActionListener(this);
        myBox.add(tfDiscountFactor);
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
		
		if (ChangeWorldSize)
		{
			try
			{
				NumRows=Integer.parseInt(tfNumRows.getText());
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(this,"Invalid number of rows (must be an integer greater than 1)");
				tfNumRows.setText(Integer.toString(NumRows));	
				FormatError=true;
			}
			
			try
			{
				NumCols=Integer.parseInt(tfNumCols.getText());
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(this,"Invalid number of columns (must be an integer greater than 1)");
				tfNumCols.setText(Integer.toString(NumCols));	
				FormatError=true;
			}
		}
		
		try
		{
			ProbUp=Double.parseDouble(tfProbUp.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid up probability (must be an real number in the interval [0,1])");
			tfProbUp.setText(Double.toString(ProbUp));	
			FormatError=true;
		}
		
		try
		{
			ProbDown=Double.parseDouble(tfProbDown.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid down probability (must be an real number in the interval [0,1])");
			tfProbDown.setText(Double.toString(ProbDown));	
			FormatError=true;
		}
		
		try
		{
			ProbLeft=Double.parseDouble(tfProbLeft.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid left probability (must be an real number in the interval [0,1])");
			tfProbLeft.setText(Double.toString(ProbLeft));	
			FormatError=true;
		}
		
		try
		{
			ProbRight=Double.parseDouble(tfProbRight.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid left probability (must be an real number in the interval [0,1])");
			tfProbRight.setText(Double.toString(ProbRight));
			FormatError=true;
		}
		
		try
		{
			ProbStay=Double.parseDouble(tfProbStay.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid stay probability (must be an real number in the interval [0,1])");
			tfProbStay.setText(Double.toString(ProbStay));
			FormatError=true;
		}
		
		try
		{
			DefaultReward=Double.parseDouble(tfDefaultReward.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid default reward (must be an real number)");
			tfDefaultReward.setText(Double.toString(DefaultReward));
			FormatError=true;
		}
		
		try
		{
			DiscountFactor=Double.parseDouble(tfDiscountFactor.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,"Invalid discount factor (must be an real number in the interval (0,1])");
			tfDiscountFactor.setText(Double.toString(DiscountFactor));
			FormatError=true;
		}		
		
		return  (!FormatError) && (NumRows>1) && (NumCols>1) && (ProbUp>=0.0)
			&& (ProbDown>=0.0) && (ProbLeft>=0.0) && (ProbRight>=0.0) && (ProbStay>=0.0)
			&& (DiscountFactor>0.0) && (DiscountFactor<=1.0)
			&& (Math.abs(ProbUp+ProbDown+ProbLeft+ProbRight+ProbStay-1.0)<1.0e-10);
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
        		JOptionPane.showMessageDialog(this,"Invalid world parameters, please correct them");
        	}
    
        }
        else if(CancelButton == e.getSource()) {
        	DoChange=false;            
            setVisible(false);
        }
    }


}
