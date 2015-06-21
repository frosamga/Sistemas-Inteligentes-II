package intsys.decision;

import java.util.Arrays;

public class LearningMDP {
	
	int NumTrials[][][];
	TransitionMDP Transition[][][][];
	WorldMDP w;
	double LearningRateParameter,OptimisticReward;
	int MinimumTrials,NumSteps;
	
	class TransitionMDP{
		int NextX,NextY;
		double Probability;
		TransitionMDP(int MyX,int MyY,double MyProb)
		{
			NextX=MyX;
			NextY=MyY;
			Probability=MyProb;
		}
	}
	
	// Reset the MDP and the learning algorithm
	public void Reset()
	{	
		int NdxX,NdxY;	
		
		w.MaxUtility=0.0;
		w.MinUtility=0.0;		
		for(NdxX=0;NdxX<w.NumRows;NdxX++)
		{
			Arrays.fill(w.Utility[NdxX],0.0);
			Arrays.fill(w.OptimalPolicy[NdxX],0);
			for(NdxY=0;NdxY<w.NumCols;NdxY++)
			{
				Arrays.fill(NumTrials[NdxX][NdxY], 0);
				Arrays.fill(w.QValue[NdxX][NdxY], 0.0);		
			}
		}	
	}
	
	// Find the transitions for a particular state and action
	TransitionMDP[] FindTransitions(int CurrX,int CurrY,int NdxAction)
	{
		TransitionMDP result[]=new TransitionMDP[5];
		int DeltaX[]={-1,0,1,0};
		int DeltaY[]={0,1,0,-1};
		int NextX,NextY;
		
		// Go to the intended direction ('up') 
		NextX=CurrX+DeltaX[NdxAction];
		NextY=CurrY+DeltaY[NdxAction];
		if ((NextX<0) || (NextX>=w.NumRows) || (NextY<0) || (NextY>=w.NumCols) || 
				w.StateType[NextX][NextY]==WorldMDP.InaccessibleState)
		{
			NextX=CurrX;
			NextY=CurrY;
		}
		result[0]=new TransitionMDP(NextX,NextY,w.ProbUp);
		
		// Go 90 degrees to the right of the intended direction ('right') 
		NextX=CurrX+DeltaX[(NdxAction+1)%WorldMDP.NumActions];
		NextY=CurrY+DeltaY[(NdxAction+1)%WorldMDP.NumActions];
		if ((NextX<0) || (NextX>=w.NumRows) || (NextY<0) || (NextY>=w.NumCols) || 
				w.StateType[NextX][NextY]==WorldMDP.InaccessibleState)
		{
			NextX=CurrX;
			NextY=CurrY;
		}
		result[1]=new TransitionMDP(NextX,NextY,w.ProbRight);

		// Go to the opposite direction of the intended one ('down') 
		NextX=CurrX+DeltaX[(NdxAction+2)%WorldMDP.NumActions];
		NextY=CurrY+DeltaY[(NdxAction+2)%WorldMDP.NumActions];
		if ((NextX<0) || (NextX>=w.NumRows) || (NextY<0) || (NextY>=w.NumCols) || 
				w.StateType[NextX][NextY]==WorldMDP.InaccessibleState)
		{
			NextX=CurrX;
			NextY=CurrY;
		}
		result[2]=new TransitionMDP(NextX,NextY,w.ProbDown);
		
		// Go 90 degrees to the left of the intended direction ('left') 
		NextX=CurrX+DeltaX[(NdxAction+3)%WorldMDP.NumActions];
		NextY=CurrY+DeltaY[(NdxAction+3)%WorldMDP.NumActions];
		if ((NextX<0) || (NextX>=w.NumRows) || (NextY<0) || (NextY>=w.NumCols) || 
				w.StateType[NextX][NextY]==WorldMDP.InaccessibleState)
		{
			NextX=CurrX;
			NextY=CurrY;
		}
		result[3]=new TransitionMDP(NextX,NextY,w.ProbLeft);		
		
		// Remain in the same state ('stay') 
		result[4]=new TransitionMDP(CurrX,CurrY,w.ProbStay);
		
		return result;
	}
	
	LearningMDP(WorldMDP world)
	{		
		w=world;
		NumTrials=new int[w.NumRows][w.NumCols][WorldMDP.NumActions];
		Transition=new TransitionMDP[w.NumRows][w.NumCols][WorldMDP.NumActions][];	
		LearningRateParameter=50.0;
		OptimisticReward=100.0;
		MinimumTrials=5000;
		NumSteps=100000;
	}
	
	// Exploration function to decide the next action to be tried
	double ExplorationFunction(double MyQValue,int NumTrials)
	{
		if (NumTrials<MinimumTrials)
		{
			// We add a small random number to break ties
			return OptimisticReward+0.001*Math.random();
		}
		else
		{
			return MyQValue;
		}		
	}
	
	// Learning rate for Q-learning
	double LearningRate(int NumTrials)
	{
		return (LearningRateParameter+1.0)/(LearningRateParameter+NumTrials);		
	}

	// Compute the transitions for this MDP
	void ComputeTransitions()
	{
		int NdxX,NdxY,NdxAction;	
		
		for(NdxX=0;NdxX<w.NumRows;NdxX++)
		{
			for(NdxY=0;NdxY<w.NumCols;NdxY++)
			{
				for(NdxAction=0;NdxAction<WorldMDP.NumActions;NdxAction++)
				{
					Transition[NdxX][NdxY][NdxAction]=FindTransitions(NdxX,NdxY,NdxAction);
				}
			}
		}
	}
	
	// Compute the utilities and an optimal policy
	void ComputeUtilitiesPolicy()
	{
		int NdxX,NdxY,NdxAction,BestAction;
		double MyMaximum,NewMaximum;
		
		w.MaxUtility=-Double.MAX_VALUE;
		w.MinUtility=Double.MAX_VALUE;
		for(NdxX=0;NdxX<w.NumRows;NdxX++)
		{
			for(NdxY=0;NdxY<w.NumCols;NdxY++)
			{
				MyMaximum=w.QValue[NdxX][NdxY][0];
				BestAction=0;
				for(NdxAction=1;NdxAction<WorldMDP.NumActions;NdxAction++)
				{
					NewMaximum=w.QValue[NdxX][NdxY][NdxAction];
					if (NewMaximum>MyMaximum)
					{
						MyMaximum=NewMaximum;
						BestAction=NdxAction;
					}				
				}
				w.Utility[NdxX][NdxY]=MyMaximum;
				w.OptimalPolicy[NdxX][NdxY]=BestAction;
				if (w.StateType[NdxX][NdxY]!=WorldMDP.InaccessibleState)
				{
					if (MyMaximum>w.MaxUtility)
					{
						w.MaxUtility=MyMaximum;
					}
					if (MyMaximum<w.MinUtility)
					{
						w.MinUtility=MyMaximum;
					}
				}
			}
		}
	}
	
	// Temporal difference Q-learning algorithm
	void Learn()
	{
		int CurrX,CurrY,NdxAction,NdxResult;
		int PrevX,PrevY,PrevAction,NdxStep;
		double PrevReward,CurrReward,MyMaximum,NewMaximum,RandomValue,ProbSum;		
				
		/*
		 * PREPROCESSING
		 */
		ComputeTransitions();
		
		/*
		 * MAIN ALGORITHM
		 */
		CurrX=w.InitialRow;
		CurrY=w.InitialCol;
		CurrReward=w.Reward[CurrX][CurrY];
		PrevX=PrevY=PrevAction=-1;
		PrevReward=0.0;
		// Main loop: execute as many iterations as specified by NumSteps
		for(NdxStep=0;NdxStep<NumSteps;NdxStep++)
		{
			// Initialize the Q-values if the state is terminal
			if (w.StateType[CurrX][CurrY]==WorldMDP.TerminalState)
			{
				for(NdxAction=0;NdxAction<WorldMDP.NumActions;NdxAction++)
				{
					w.QValue[CurrX][CurrY][NdxAction]=CurrReward;
				}
			}
			// If this is not the initial step, update the Q-values and the number of trials
			if (PrevX!=-1)
			{
				// Increment the number of trials
				NumTrials[PrevX][PrevY][PrevAction]++;
				
				/* Find the Q-value of the best action for the current state and store it 
				in MyMaximum
				*/
				MyMaximum=w.QValue[CurrX][CurrY][0];
				for(NdxAction=1;NdxAction<WorldMDP.NumActions;NdxAction++)
				{
					NewMaximum=w.QValue[CurrX][CurrY][NdxAction];
					if (NewMaximum>MyMaximum)
					{
						MyMaximum=NewMaximum;
					}				
				}
				
				// Update the Q-value
				w.QValue[PrevX][PrevY][PrevAction]=
					w.QValue[PrevX][PrevY][PrevAction]+
					LearningRate(NumTrials[PrevX][PrevY][PrevAction])*
					(PrevReward-w.QValue[PrevX][PrevY][PrevAction]+
							w.DiscountFactor*MyMaximum);
			}
			
			// Backup previous state and reward
			PrevX=CurrX;
			PrevY=CurrY;
			PrevReward=CurrReward;
			
			// Choose an action to be tried and store it in PrevAction
			MyMaximum=ExplorationFunction(w.QValue[PrevX][PrevY][0],NumTrials[PrevX][PrevY][0]);
			PrevAction=0;
			for(NdxAction=1;NdxAction<WorldMDP.NumActions;NdxAction++)
			{
				NewMaximum=ExplorationFunction(w.QValue[PrevX][PrevY][NdxAction],
						NumTrials[PrevX][PrevY][NdxAction]);
				if (NewMaximum>MyMaximum)
				{
					MyMaximum=NewMaximum;
					PrevAction=NdxAction;
				}
			}
			
			/* Execute the action and store resulting state and reward in
			 * CurrX,CurrY and CurrReward 
			 */
			RandomValue=Math.random();
			ProbSum=0.0;
			for(NdxResult=0;NdxResult<5;NdxResult++)
			{
				ProbSum+=Transition[PrevX][PrevY][PrevAction][NdxResult].Probability;
				if (RandomValue<=ProbSum)
				{
					CurrX=Transition[PrevX][PrevY][PrevAction][NdxResult].NextX;
					CurrY=Transition[PrevX][PrevY][PrevAction][NdxResult].NextY;
					CurrReward=w.Reward[CurrX][CurrY];
					break;
				}				
			}
			
			// Restart the process if a terminal state was reached
			if (w.StateType[PrevX][PrevY]==WorldMDP.TerminalState)
			{
				CurrX=w.InitialRow;
				CurrY=w.InitialCol;
				CurrReward=w.Reward[CurrX][CurrY];
				PrevX=PrevY=PrevAction=-1;
				PrevReward=0.0;				
			}				
		}
		
		/*
		 * POSTPROCESSING
		 */
		ComputeUtilitiesPolicy();
	}

}
