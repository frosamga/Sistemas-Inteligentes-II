package intsys.nonparametric;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageProcessor {
	ScrollableImage OriginalImage;
	BufferedImage InputImage;
	int NumRowsImg,NumColsImg;
	int NewNumRowsImg,NewNumColsImg;
	int InputRGBint[];
	double InputRGBdouble[];
	int[] CorruptedRGBint;
	double[] CorruptedRGBdouble;
	int[] OutputRGBint;
	double[] OutputRGBdouble;
	boolean[] Present;

	
	
	ImageProcessor(ScrollableImage simg)	
	{
		OriginalImage=simg;
		InputImage=simg.img;
		NumRowsImg=simg.img.getHeight();
		NumColsImg=simg.img.getWidth();
	}
	
	/* 
	 * Clip a color value (double precision) to lie in the valid range [0,1]
	 */
	public double ClipColor(double Value)
	{
		if (Value<0.0)
		{
			Value=0.0;
		}
		if (Value>1.0)
		{
			Value=1.0;
		}
		return Value;
		
	}
	
	/*
	 * Convert an RGB image where each pixel is codified as a unique int value
	 * (as specified by the Java constant BufferedImage.TYPE_INT_RGB)
	 * to a representation where each channel is stored separately as double values
	 * in the range [0,1]. Note that the double matrix is three dimensional:
	 * DestMatrix[NdxRow,NdxCol,NdxChannel]
	 * On the other hand, the int matrix is two dimensional:
	 * SrcMatrix[NdxRow,NdxCol]
	 */
	public void RGBint2double(int[] SrcMatrix,double[] DestMatrix,int NumRowsImg,int NumColsImg)
	{
		int CntRow,CntCol;
		Color MyColor;
		
		// Convert color values to the [0,1] interval
		for(CntCol=0;CntCol<NumColsImg;CntCol++)
		{
			for(CntRow=0;CntRow<NumRowsImg;CntRow++)
			{
					
				// Get the original RGB values
				MyColor=new Color(SrcMatrix[CntRow+CntCol*NumRowsImg],false);
					
				// Process red component
				DestMatrix[CntRow+CntCol*NumRowsImg]=((double)MyColor.getRed())/255.0;
					
				// Process green component
				DestMatrix[CntRow+CntCol*NumRowsImg+NumRowsImg*NumColsImg]=
					((double)MyColor.getGreen())/255.0;
					
				// Process blue component
				DestMatrix[CntRow+CntCol*NumRowsImg+2*NumRowsImg*NumColsImg]=
					((double)MyColor.getBlue())/255.0;
			
				
			}
		}		
	}
	
	/*
	 * Convert an RGB image where each channel is stored separately as double values
	 * in the range [0,1] to a representation where each pixel is codified as a unique int value
	 * (as specified by the Java constant BufferedImage.TYPE_INT_RGB).
	 * Note that the double matrix is three dimensional:
	 * SrcMatrix[NdxRow,NdxCol]
	 * On the other hand, the int matrix is two dimensional:
	 * DestMatrix[NdxRow,NdxCol,NdxChannel]
	 */
	public void RGBdouble2int(double[] SrcMatrix,int[] DestMatrix,int NumRowsImg,int NumColsImg)
	{
		int CntRow,CntCol;
		Color MyColor;
		
		for(CntCol=0;CntCol<NumColsImg;CntCol++)
		{
			for(CntRow=0;CntRow<NumRowsImg;CntRow++)
			{	
				MyColor=new Color((float)ClipColor(SrcMatrix[CntRow+CntCol*NumRowsImg]),
						(float)ClipColor(SrcMatrix[CntRow+CntCol*NumRowsImg+NumRowsImg*NumColsImg]),
						(float)ClipColor(SrcMatrix[CntRow+CntCol*NumRowsImg+2*NumRowsImg*NumColsImg]));
				DestMatrix[CntRow+CntCol*NumRowsImg]=MyColor.getRGB();
			}
		}
	}
	
	/*
	 * Run the regression demo. The original image is corrupted by Gaussian noise, some pixels
	 * are removed, and the output image can be of a different size
	 */
	public ScrollableImage RegressionDemo(int WindowSize,double NoiseLevel,double MissingData,double HorizScale,double VertScale)
	{		
		ScrollableImage Result,Corrupted;
		BufferedImage OutputImage;
		BufferedImage CorruptedImage=new BufferedImage(NumColsImg, NumRowsImg, BufferedImage.TYPE_INT_RGB);

		NewNumColsImg=(int)(NumColsImg*HorizScale);
		NewNumRowsImg=(int)(NumRowsImg*VertScale);
		InputRGBdouble=new double[3*NumColsImg*NumRowsImg];
		CorruptedRGBdouble=new double[3*NumColsImg*NumRowsImg];
		CorruptedRGBint=new int[NumColsImg*NumRowsImg];
		OutputRGBint=new int[NewNumColsImg*NewNumRowsImg];
		OutputRGBdouble=new double[3*NewNumColsImg*NewNumRowsImg];
		OutputImage=new BufferedImage(NewNumColsImg, NewNumRowsImg, BufferedImage.TYPE_INT_RGB);
		Present=new boolean[NumRowsImg*NumColsImg];

		// Get the original image (not corrupted)
		InputRGBint=InputImage.getRGB(0, 0, NumColsImg, NumRowsImg, null, 0, NumColsImg);
		RGBint2double(InputRGBint,InputRGBdouble,NumRowsImg,NumColsImg);
		
		// Corrupt the original image
		CorruptImage(NoiseLevel,MissingData);
		
		// Get the corrupted image
		RGBdouble2int(CorruptedRGBdouble,CorruptedRGBint,NumRowsImg,NumColsImg);		
		CorruptedImage.setRGB(0, 0, NumColsImg, NumRowsImg, CorruptedRGBint, 0, NumColsImg);
		
		// Create a new window with the corrupted image
		Corrupted=new ScrollableImage("Corrupted image",false);
		Corrupted.img=CorruptedImage;
		Corrupted.pnl.setImage(CorruptedImage);		
		Corrupted.setSize(Corrupted.getPreferredSize());		

		// Carry out the regression on the corrupted image
		ChannelRegression(WindowSize);

		// Get the output image
		RGBdouble2int(OutputRGBdouble,OutputRGBint,NewNumRowsImg,NewNumColsImg);		
		OutputImage.setRGB(0, 0, NewNumColsImg, NewNumRowsImg, OutputRGBint, 0, NewNumColsImg);
		
		// Create a new window with the output image
		Result=new ScrollableImage("Output image",false);
		Result.img=OutputImage;
		Result.pnl.setImage(OutputImage);		
		Result.setSize(Result.getPreferredSize());

		return Result;
		
	}
	
	/*
	 * Given an image codified as double values in the range [0,1], add Gaussian noise
	 * and remove some values
	 */
	public void CorruptImage(double NoiseLevel,double MissingData)
	{
		int CntRow,CntCol;
		Random generator = new Random();
		double NewValue;
		
		// Add noise and remove values
		for(CntCol=0;CntCol<NumColsImg;CntCol++)
		{
			for(CntRow=0;CntRow<NumRowsImg;CntRow++)
			{
				// See if this pixel has to be removed
				if (generator.nextDouble()<0.01*MissingData)
				{
					// This pixel must be removed, so we set its color to black
					Present[CntRow+CntCol*NumRowsImg]=false;
					
					CorruptedRGBdouble[CntRow+CntCol*NumRowsImg]=0.0;
					CorruptedRGBdouble[CntRow+CntCol*NumRowsImg+NumRowsImg*NumColsImg]=0.0;
					CorruptedRGBdouble[CntRow+CntCol*NumRowsImg+2*NumRowsImg*NumColsImg]=0.0;
					
				}
				else
				{
					// The pixel must not be removed, so proceed to Gaussian noise addition
					Present[CntRow+CntCol*NumRowsImg]=true;
					
					// Process red component
					NewValue=InputRGBdouble[CntRow+CntCol*NumRowsImg]+NoiseLevel*generator.nextGaussian();
					CorruptedRGBdouble[CntRow+CntCol*NumRowsImg]=NewValue;
					
					// Process green component
					NewValue=InputRGBdouble[CntRow+CntCol*NumRowsImg+NumRowsImg*NumColsImg]+NoiseLevel*generator.nextGaussian();
					CorruptedRGBdouble[CntRow+CntCol*NumRowsImg+NumRowsImg*NumColsImg]=NewValue;
					
					// Process blue component
					NewValue=InputRGBdouble[CntRow+CntCol*NumRowsImg+2*NumRowsImg*NumColsImg]+NoiseLevel*generator.nextGaussian();
					CorruptedRGBdouble[CntRow+CntCol*NumRowsImg+2*NumRowsImg*NumColsImg]=NewValue;
				}
				
			}
		}		
	}
	
	/*
	 * Carry out the regression on the three RGB channels of an image
	 */
	public void ChannelRegression(int WindowSize)
	{
		int CntRow,CntCol,NdxChannel;
		double[] YTrain;
		LocallyWeightedRegressor Regressor=new LocallyWeightedRegressor();
		double[] Result;

		
		YTrain=new double[NumRowsImg*NumColsImg];
		
		for(NdxChannel=0;NdxChannel<3;NdxChannel++)
		{
			// Prepare the input data for the regression
			for(CntCol=0;CntCol<NumColsImg;CntCol++)
			{
				for(CntRow=0;CntRow<NumRowsImg;CntRow++)
				{
					YTrain[CntRow+CntCol*NumRowsImg]=
						CorruptedRGBdouble[CntRow+CntCol*NumRowsImg+NdxChannel*NumRowsImg*NumColsImg];
				}
			}	
			// Carry out the regression
			Result=Regressor.LocallyWeightedRegression(NumColsImg,NumRowsImg,NewNumColsImg,NewNumRowsImg,YTrain,Present,WindowSize);			
			// Copy the result of the regression to the output matrix
			System.arraycopy(Result,0,OutputRGBdouble,NdxChannel*NewNumRowsImg*NewNumColsImg,NewNumRowsImg*NewNumColsImg);
		}

	}

}
