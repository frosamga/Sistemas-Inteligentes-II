package intsys.nonparametric;

//Import the swing and AWT classes needed
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
 
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;



import java.awt.image.BufferedImage; 
import java.io.File;
import java.io.IOException; 
import javax.imageio.ImageIO;
 


class ImagePanel extends JPanel{

	private static final long serialVersionUID = 6801938518188484876L;
	BufferedImage img;
	double scale;
	JMenuItem elements[]={new JMenuItem("Zoom in"),new JMenuItem("Zoom out")};
	JPopupMenu pm=new JPopupMenu();
	PopupListener oe=new PopupListener();

	ActionListener al[]={new ActionListener(){
		public void actionPerformed(ActionEvent ev){			
			// Zoom in
			scale*=2.0;
			Dimension d=new Dimension((int)(scale*img.getWidth()),(int)(scale*img.getHeight()));
			
			setMaximumSize(d);
			setPreferredSize(d);
			setSize(d);
			repaint();
		}
			
	},
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			// Zoom out
			
			if (scale<1.0)
			{
				return;
			}
			scale/=2.0;
			Dimension d=new Dimension((int)(scale*img.getWidth()),(int)(scale*img.getHeight()));
			setMaximumSize(d);
			setPreferredSize(d);
			setSize(d);
			repaint();

			}
	}
	};
	
	public void setImage(BufferedImage img){
		Dimension d;
		this.img=img;
		d=new Dimension(img.getWidth(),img.getHeight());
		scale=1.0;

		// Set the preferred size so that the image fits in the window
		setMaximumSize(d);
		setPreferredSize(d);		
		setSize(d);
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (img!=null)
		{
			g.drawImage(img,0,0,(int)(scale*img.getWidth()),(int)(scale*img.getHeight()),null);
		}
		
	}
	
	ImagePanel(){
		for(int i=0;i<elements.length;i++)
        {
        	elements[i].addActionListener(al[i]);
        	pm.add(elements[i]);
        }
        addMouseListener(oe);

	}
	
	class PopupListener extends MouseAdapter {
		
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);			
		}
		public void mouseReleased (MouseEvent e) {

			maybeShowPopup(e);
		}
		private void maybeShowPopup(MouseEvent e){
			if (e.isPopupTrigger()) {
			pm.show(e.getComponent(),
					e.getX(), e.getY() ) ;
			}
		}
	}
}

public class ScrollableImage extends JFrame {

	private static final long serialVersionUID = 2156972484320199712L;
	boolean CanLoad;
	JMenuBar mb;
	JMenu menu=new JMenu("File");
	JMenuItem elements[]={new JMenuItem("Load"),new JMenuItem("Save")};
	BufferedImage img = null;
	Container cp;
	ImagePanel pnl=new ImagePanel();
	JScrollPane jsp;
	
	ComponentListener cl=new ComponentListener(){
		public void componentResized( ComponentEvent e ) {
			int width = ScrollableImage.this.getWidth();
			int height = ScrollableImage.this.getHeight();

			boolean NeedsCorrection=false;
			
			if (img!=null)
			{
				int maxWidth=(int)(pnl.scale*img.getWidth())+
					getWidth()-cp.getWidth();
				int maxHeight=(int)(pnl.scale*img.getHeight())+
					getHeight()-cp.getHeight();
				
				if (height>maxHeight)
			
				{
					NeedsCorrection=true;
					height=maxHeight;
				}
				if (width>maxWidth)
				{
					NeedsCorrection=true;
					width=maxWidth;
				}
				if (NeedsCorrection)
				{
					setSize( width, height );
				}
			}

		}

		@Override
		public void componentHidden(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	ActionListener al[]={new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			
			JFileChooser fc=new JFileChooser();
			
			if (fc.showOpenDialog(ScrollableImage.this)==JFileChooser.APPROVE_OPTION) 
			{
				try {
					img = ImageIO.read(fc.getSelectedFile());
					pnl.setImage(img);					
					setSize(getPreferredSize());
					
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(ScrollableImage.this, "Error while loading the image");
				}
			}


		}
	},
	new ActionListener(){
		public void actionPerformed(ActionEvent ev){
			JFileChooser fc=new JFileChooser();
			
			// Add a filter to save only in .png format
			// to avoid losing image quality
			fc.addChoosableFileFilter(new FileFilter (){
				public boolean accept(File f){
				    String extension;
				    int dotPos = f.getName().lastIndexOf(".");
				    extension = f.getName().substring(dotPos+1);
				    return extension.toLowerCase().equals("png");
				}
				public String getDescription() {
					return "Portable network graphics files (.png)";
				}
				});
		
			// Save the image if no errors were found
			if ((fc.showSaveDialog(ScrollableImage.this)==JFileChooser.APPROVE_OPTION) &&
					(fc.accept(fc.getSelectedFile())))
			{
				try {
					
					ImageIO.write(img,"png", fc.getSelectedFile());					
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(ScrollableImage.this, "Error while saving the image");
				}
			}
			else
			{
				JOptionPane.showMessageDialog(ScrollableImage.this, "Invalid file name");
			}

			}
	}
	};
	
	ScrollableImage(String Title,boolean CanLoad) {
        // Create a JFrame, which is a Window with "decorations", i.e.
        // title, border and close-button
        this.CanLoad=CanLoad;
        
        // Window title
        setTitle(Title);
        
        // Initialize the menu elements, the menu and the menu bar
        mb=new JMenuBar();
        elements[0].setEnabled(CanLoad);
        for(int i=0;i<elements.length;i++)
        {
        	elements[i].addActionListener(al[i]);
        	menu.add(elements[i]);
        }
        mb.add(menu);
        
        // Set the menu bar of the window
        setJMenuBar(mb);
        
        cp=getContentPane();
        
        jsp=new JScrollPane(pnl,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cp.add(jsp);
        
        addComponentListener(cl);
        

        // Set the size of the window.
        setSize(400,400);

        // Set the default close operation for the window
        if (CanLoad)
        {
        	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        else
        {
        	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        	

        // Set the visibility as true, thereby displaying it
        setVisible(true);

	}
	
}

