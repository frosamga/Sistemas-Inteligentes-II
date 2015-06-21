package intsys.nonparametric;

import java.awt.EventQueue;

import javax.swing.UIManager;

import intsys.nonparametric.NonparametricFrame;

public class NonparametricApp {
	public static void main(String[] args) {
		 
    	try{
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	}
    	catch(Exception e)
    	{
    		System.out.println("Unable to set look and feel");
    	}
        // Make sure all Swing/AWT instantiations and accesses are done on the
        // Event Dispatch Thread (EDT)
        EventQueue.invokeLater(new Runnable() {
            public void run() {
            	new NonparametricFrame();
            }
        });
    }
}
