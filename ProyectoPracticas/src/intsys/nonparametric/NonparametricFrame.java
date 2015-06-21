package intsys.nonparametric;

import java.awt.Container;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



import javax.swing.Box;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.JTextField;



public class NonparametricFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6721568863337470731L;
	
	ScrollableImage OriginalImage;
	ImageProcessor Processor;
	
	JMenuBar mb;
	JMenu menu=new JMenu("Demos");
	JMenuItem elements[]={new JMenuItem("Denoise"),new JMenuItem("Missing data"),
			new JMenuItem("Resize"),new JMenuItem("Full demo")};
	JMenu menu2=new JMenu("Help");
	JMenuItem elements2[]={new JMenuItem("About")};

	Container cp;
	JTextField tfKernelWidth,tfNoiseLevel,tfMissingData,tfHorizScale,tfVertScale;
	Box bKernelWidth,bVertScale,bHorizScale,bMissingData,bNoiseLevel;
	
	int DefaultKernelWidth=4;
	int KernelWidth;
	double DefaultNoiseLevel=0.05,DefaultMissingData=10.0,DefaultHorizScale=2.0,DefaultVertScale=2.0;
	double NoiseLevel,MissingData,HorizScale,VertScale;

	
	ActionListener al[]={new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			
			if (OriginalImage.img==null)
			{
				JOptionPane.showMessageDialog(null,"First you must load the original image");
			}
			else
			{
				// Carry out the denoising demo
				Processor=new ImageProcessor(OriginalImage);
				Processor.RegressionDemo(KernelWidth,NoiseLevel,0.0,1.0,1.0);				
				
			}

		}
	},
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			
			if (OriginalImage.img==null)
			{
				JOptionPane.showMessageDialog(null,"First you must load the original image");
			}
			else
			{
				// Carry out the missing data demo
				Processor=new ImageProcessor(OriginalImage);
				Processor.RegressionDemo(KernelWidth,0.0,MissingData,1.0,1.0);
			}
		}
	},
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			if (OriginalImage.img==null)
			{
				JOptionPane.showMessageDialog(null,"First you must load the original image");
			}
			else
			{
				// Carry out the rescaling demo
				Processor=new ImageProcessor(OriginalImage);
				Processor.RegressionDemo(KernelWidth,0.0,0.0,HorizScale,VertScale);
			}  
		}
	},
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			if (OriginalImage.img==null)
			{
				JOptionPane.showMessageDialog(null,"First you must load the original image");
			}
			else
			{
				// Carry out the full demo
				Processor=new ImageProcessor(OriginalImage);
				Processor.RegressionDemo(KernelWidth,NoiseLevel,MissingData,HorizScale,VertScale);
			}  
		}
	}
	};

	ActionListener al2[]={new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			JOptionPane.showMessageDialog(null,"Nonparametric models demo application\nEzequiel López Rubio\nUniversity of Málaga (Spain)\nThis software is for academic evaluation purposes only");			
		}
	}
	};


	
	NonparametricFrame() {
		
		// Set the parameters to their defaults
		KernelWidth=DefaultKernelWidth;
		NoiseLevel=DefaultNoiseLevel;
		MissingData=DefaultMissingData;
		HorizScale=DefaultHorizScale;
		VertScale=DefaultVertScale;
		
        // Create a JFrame, which is a Window with "decorations", i.e.
        // title, border and close-button

		
        // Window title
        setTitle("Nonparametric models demo application");
        
        // Initialize the menu elements, the menu and the menu bar
        mb=new JMenuBar();
        for(int i=0;i<elements.length;i++)
        {
        	elements[i].addActionListener(al[i]);
        	menu.add(elements[i]);
        }
        mb.add(menu);
        for(int i=0;i<elements2.length;i++)
        {
        	elements2[i].addActionListener(al2[i]);
        	menu2.add(elements2[i]);
        }
        mb.add(menu2);
        
        // Set the menu bar of the window
        setJMenuBar(mb);
        
        
        // Adding controls to this JFrame
        cp=getContentPane();
        cp.setLayout(new FlowLayout());

        bKernelWidth=Box.createHorizontalBox();
        bKernelWidth.add(new JLabel("Kernel width"));
        tfKernelWidth = new JTextField(Integer.toString(DefaultKernelWidth),5);
        tfKernelWidth.addActionListener(this);
        bKernelWidth.add(tfKernelWidth);
        cp.add(bKernelWidth);
        
        bNoiseLevel=Box.createHorizontalBox();
        bNoiseLevel.add(new JLabel("Noise level"));
        tfNoiseLevel = new JTextField(Double.toString(DefaultNoiseLevel),5);
        tfNoiseLevel.addActionListener(this);
        bNoiseLevel.add(tfNoiseLevel);
        cp.add(bNoiseLevel);
        
        bMissingData=Box.createHorizontalBox();
        bMissingData.add(new JLabel("% Missing data"));
        tfMissingData = new JTextField(Double.toString(DefaultMissingData),5);
        tfMissingData.addActionListener(this);
        bMissingData.add(tfMissingData);
        cp.add(bMissingData);
        
        bHorizScale=Box.createHorizontalBox();
        bHorizScale.add(new JLabel("Horizontal scale"));
        tfHorizScale = new JTextField(Double.toString(DefaultHorizScale),5);
        tfHorizScale.addActionListener(this);
        bHorizScale.add(tfHorizScale);
        cp.add(bHorizScale);
        
        bVertScale=Box.createHorizontalBox();
        bVertScale.add(new JLabel("Vertical scale"));
        tfVertScale = new JTextField(Double.toString(DefaultVertScale),5);
        tfVertScale.addActionListener(this);
        bVertScale.add(tfVertScale);
        cp.add(bVertScale);
               

        // Set the size of the window
        setSize(400,400);

        // Set the default close operation for the window, or else the
        // program won't exit when clicking close button
        //  (The default is HIDE_ON_CLOSE, which just makes the window
        //  invisible, and thus doesn't exit the app)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the visibility as true, thereby displaying it
        setVisible(true);
        
        OriginalImage=new ScrollableImage("Original image",true);

	}
	
	public void actionPerformed(ActionEvent evt) {
		
		// Validate the kernel width and store it
		try
		{
			KernelWidth=Integer.parseInt(tfKernelWidth.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null,"Invalid kernel width (must be an integer greater than 2)");
			tfKernelWidth.setText(Integer.toString(DefaultKernelWidth));
			KernelWidth=DefaultKernelWidth;
		}	
		if (KernelWidth<3)
		{
			JOptionPane.showMessageDialog(null,"Invalid kernel width (must be an integer greater than 2)");
			tfKernelWidth.setText(Integer.toString(DefaultKernelWidth));
			KernelWidth=DefaultKernelWidth;
			
		}

		// Validate the noise level and store it
		try
		{
			NoiseLevel=Double.parseDouble(tfNoiseLevel.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null,"Invalid noise level (must be between 0.0 and 1.0)");
			tfNoiseLevel.setText(Double.toString(DefaultNoiseLevel));
			NoiseLevel=DefaultNoiseLevel;
		}	
		if ((NoiseLevel<0.0) || (NoiseLevel>1.0))
		{
			JOptionPane.showMessageDialog(null,"Invalid noise level (must be between 0.0 and 1.0)");
			tfNoiseLevel.setText(Double.toString(DefaultNoiseLevel));
			NoiseLevel=DefaultNoiseLevel;
			
		}
		
		// Validate the missing data percentage and store it
		try
		{
			MissingData=Double.parseDouble(tfMissingData.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null,"Invalid missing data percentage (must be between 0.0 and 100.0)");
			tfMissingData.setText(Double.toString(DefaultMissingData));
			MissingData=DefaultMissingData;
		}	
		if ((MissingData<0.0) || (MissingData>100.0))
		{
			JOptionPane.showMessageDialog(null,"Invalid missing data percentage (must be between 0.0 and 100.0)");
			tfMissingData.setText(Double.toString(DefaultMissingData));
			MissingData=DefaultMissingData;
			
		}
		
		// Validate the horizontal scale and store it
		try
		{
			HorizScale=Double.parseDouble(tfHorizScale.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null,"Invalid horizontal scale (must be a positive real number)");
			tfHorizScale.setText(Double.toString(DefaultHorizScale));
			HorizScale=DefaultHorizScale;
		}	
		if (HorizScale<=0.0)
		{
			JOptionPane.showMessageDialog(null,"Invalid horizontal scale (must be a positive real number)");
			tfHorizScale.setText(Double.toString(DefaultHorizScale));
			HorizScale=DefaultHorizScale;
			
		}
        
		// Validate the vertical scale and store it
		try
		{
			VertScale=Double.parseDouble(tfVertScale.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null,"Invalid vertical scale (must be a positive real number)");
			tfVertScale.setText(Double.toString(DefaultVertScale));
			VertScale=DefaultVertScale;
		}	
		if (VertScale<=0.0)
		{
			JOptionPane.showMessageDialog(null,"Invalid vertical scale (must be a positive real number)");
			tfVertScale.setText(Double.toString(DefaultVertScale));
			VertScale=DefaultVertScale;
			
		}
		
    }


}
