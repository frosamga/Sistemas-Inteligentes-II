package intsys.decision;

/*import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;*/
import java.io.Serializable;
import java.util.Arrays;

public class WorldMDP implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3138131396658603625L;
	
	// Actions
	public static final int NumActions = 4;
	public static final int ActionUp = 0;
	public static final int ActionRight = 1;
	public static final int ActionDown = 2;
	public static final int ActionLeft = 3;
	public static final String ActionNames[]=
		{"Up","Right","Down","Left"};
	public static final String ActionSymbols[]=
		{"\u2191","\u2192","\u2193","\u2190"};
	
	
	// State types
	public static final int NormalState = 0;
	public static final int TerminalState = 1;
	public static final int InaccessibleState = 2;
	public static final int InitialState = 3;
	public static final String StateTypeNames[]=
		{"Normal","Terminal","Inaccessible","Initial"};
	
	
	int NumRows,NumCols,InitialRow,InitialCol;
	double ProbUp,ProbLeft,ProbRight,ProbDown,ProbStay,DefaultReward,DiscountFactor;
	double QValue[][][];
	double Utility[][];
	int OptimalPolicy[][];
	double MaxUtility,MinUtility;

	double Reward[][];
	int StateType[][];
	
	WorldMDP()
	{
		NumRows=3;
		NumCols=4;
		ProbUp=0.8;
		ProbLeft=0.1;
		ProbRight=0.1;
		ProbDown=0.0;
		ProbStay=0.0;
		DefaultReward=-0.04;
		DiscountFactor=1.0;
		MaxUtility=0.0;
		MinUtility=0.0;
	}
	
	// Initialize the dynamic structures of this instance
	public void Setup()
	{		
		QValue=new double[NumRows][NumCols][NumActions];
		Utility=new double[NumRows][NumCols];
		Reward=new double[NumRows][NumCols];
		StateType=new int[NumRows][NumCols];
		OptimalPolicy=new int[NumRows][NumCols];
		InitialRow=0;
		InitialCol=0;
		StateType[0][0]=InitialState;
		StateType[NumRows-1][NumCols-1]=TerminalState;
		for(int NdxRow=0;NdxRow<NumRows;NdxRow++)
		{
			Arrays.fill(Reward[NdxRow], DefaultReward);
		}	
	}
	
	

}
