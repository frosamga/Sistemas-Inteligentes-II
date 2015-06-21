package intsys.nonparametric;


import java.util.Arrays;

public class LocallyWeightedRegressor {

	/*
	 * Solve the linear system of equations A*x=b
	 * where A is a 3x3 matrix of coefficients,
	 * b is a 3x1 column vector of independent terms,
	 * and x is the 3x1 column vector of unknowns.
	 * It is assumed that the system has a unique solution.
	 * The system is solved by Cramer's rule
	 */
	public void SolveLinearSystem(double[] A,double[] b,double[] x)
	{
		double Determinant;
		
		Determinant=A[0]*A[4]*A[8]+A[2]*A[3]*A[7]+A[1]*A[5]*A[6]
             -A[2]*A[4]*A[6]-A[0]*A[5]*A[7]-A[1]*A[3]*A[8];
		if (Determinant==0.0)
		{
			Arrays.fill(x,0.0);
		}
		else
		{
			x[0]=(b[0]*A[4]*A[8]+b[2]*A[3]*A[7]+b[1]*A[5]*A[6]
			    -b[2]*A[4]*A[6]-b[0]*A[5]*A[7]-b[1]*A[3]*A[8])/Determinant;
			x[1]=(A[0]*b[1]*A[8]+A[2]*b[0]*A[7]+A[1]*b[2]*A[6]
			    -A[2]*b[1]*A[6]-A[0]*b[2]*A[7]-A[1]*b[0]*A[8])/Determinant;
			x[2]=(A[0]*A[4]*b[2]+A[2]*A[3]*b[1]+A[1]*A[5]*b[0]
			    -A[2]*A[4]*b[0]-A[0]*A[5]*b[1]-A[1]*A[3]*b[2])/Determinant;
		}
	}
	/*
	 * Input image has size NumRowsIn x NumColsIn
	 * Output image has size NumRowsOut x NumColsOut
	 * Please note that rows and columns are swapped with respect to Java's image
	 * representation.
	 * The pixel values of the input image are in InputSamples
	 * The matrix Present has the same size as the input image, and their elements
	 * are true if and only if the value of the corresponding pixel of the input image is
	 * available
	 * k is the size of the regression window (must be a positive integer)
	 */
	public double[] LocallyWeightedRegression(int NumRowsIn,int NumColsIn,int NumRowsOut,int NumColsOut,double[] InputSamples,boolean[] Present,int k)
	{
		double[] OutputSamples;
		int NdxRow,NdxCol,WindowSize,CenterRow,CenterCol,NdxWinRow,NdxWinCol,Radius;
		int PixelRow,PixelCol,NdxVar,NdxEcn;
		double[] KernelVal,A,b,x_j,x_q,Weights;
		double RowScale,ColScale,ColDiff,RowDiff,Dist,MyKernelVal,y_j;

		
		// Prepare output array
		OutputSamples=new double[NumRowsOut*NumColsOut];
		
		// Compute regression parameters
		Radius=1+k/2;  // Radius of the regression window
		WindowSize=2*Radius+1; // Side length of the regression window
		RowScale=(double)NumRowsIn/(double)NumRowsOut;
		ColScale=(double)NumColsIn/(double)NumColsOut;
		
		// Prepare auxiliary arrays
		KernelVal=new double[WindowSize*WindowSize];
		A=new double[3*3];
		b=new double[3];
		x_j=new double[3];
		x_j[2]=1.0;
		x_q=new double[3];
		x_q[2]=1.0;
		Weights=new double[3];
		
		
		
		for(NdxCol=0;NdxCol<WindowSize;NdxCol++)
		{
			ColDiff=NdxCol-Radius;
			for(NdxRow=0;NdxRow<WindowSize;NdxRow++)
			{
				/* Apply the quadratic kernel
				 Kernel=@(x)(max(0,1-(2*abs(x)/k)^2));
				 KernelVal=Kernel(Dist); */
				RowDiff=NdxRow-Radius;
				Dist=Math.sqrt(ColDiff*ColDiff+RowDiff*RowDiff);
				KernelVal[NdxRow+NdxCol*WindowSize]=
					Math.max(0.0,1.0-(2.0*Math.abs(Dist)/k)*(2.0*Math.abs(Dist)/k));
			}
		}
		
		// Compute output values
		for(NdxCol=0;NdxCol<NumColsOut;NdxCol++)
		{
			x_q[1]=((double)NdxCol)*ColScale;
			for(NdxRow=0;NdxRow<NumRowsOut;NdxRow++)
			{
				x_q[0]=(double)NdxRow*RowScale;
				// Compute the coordinates of the center of the regression window in the input image
				CenterRow=(int)(NdxRow*RowScale);
				CenterCol=(int)(NdxCol*ColScale);
				
				// Initialize the linear system of equations
				Arrays.fill(A, 0.0);
				Arrays.fill(b, 0.0);
				
				// Examine the elements of the regression window
				for(NdxWinCol=0;NdxWinCol<WindowSize;NdxWinCol++)
				{
					// Compute the row coordinate of the current pixel in the input image
					// and skip it if we are out of bounds
					PixelCol=CenterCol+NdxWinCol-Radius;
					
					if ((PixelCol<0) || (PixelCol>=NumColsIn))
					{
						continue;
					}
					for(NdxWinRow=0;NdxWinRow<WindowSize;NdxWinRow++)
					{
						// Compute the column coordinate of the current pixel in the input image
						// and skip it if we are out of bounds
						PixelRow=CenterRow+NdxWinRow-Radius;

						if ((PixelRow<0) || (PixelRow>=NumRowsIn))
						{
							continue;
						}
						// See if this input pixel is available; otherwise continue
						if (!Present[PixelRow+PixelCol*NumRowsIn])
						{
							continue;
						}
						// Get the kernel value for the current pixel
						MyKernelVal=KernelVal[NdxWinRow+NdxWinCol*WindowSize];
						x_j[0]=PixelRow;
						x_j[1]=PixelCol;						
						y_j=InputSamples[PixelRow+PixelCol*NumRowsIn];
						for(NdxEcn=0;NdxEcn<3;NdxEcn++)
						{
							for(NdxVar=0;NdxVar<3;NdxVar++)
							{
				                /* A(NdxEcn,NdxVar)=A(NdxEcn,NdxVar)+...
			                    KernelVal*x_j(NdxVar)*(2*x_j(NdxEcn)); */
								A[NdxEcn+3*NdxVar]+=MyKernelVal
									*x_j[NdxVar]*(2.0*x_j[NdxEcn]);								
							}
							// b(NdxEcn)=b(NdxEcn)+2*KernelVal*y_j*x_j(NdxEcn);
							b[NdxEcn]+=2.0*MyKernelVal*y_j*x_j[NdxEcn];
						}
					}
				}
				// Solve the linear system of equations
				// Weights=A\b;
				SolveLinearSystem(A,b,Weights);
				// Obtain the output value
			    // YTest(NdxTest)=Weights'*x_q;
				OutputSamples[NdxRow+NdxCol*NumRowsOut]=0.0;
				for(NdxVar=0;NdxVar<3;NdxVar++)
				{
					OutputSamples[NdxRow+NdxCol*NumRowsOut]+=Weights[NdxVar]*x_q[NdxVar];
				}
				
			}
		}
		
		return OutputSamples;
	}
	

}
