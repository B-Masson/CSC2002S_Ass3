//package cloudscapes;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

public class CloudData {

	Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	float [][][] convection; // vertical air movement strength, that evolves over time
	int [][][] classification; // cloud type per grid point, evolving over time
	int dimx, dimy, dimt; // data dimensions
        Vector sum = new Vector(); //stores average X and Y wind values for entire grid
        
        public CloudData()
        {
            
        }
        
	// overall number of elements in the timeline grids
	int dim(){
		return dimt*dimx*dimy;
	}
	
	// convert linear position into 3D location in simulation grid
	void locate(int pos, int [] ind)
	{
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
	}
	
	// read cloud simulation data from file
	void readData(String fileName){ 
		try{ 
			Scanner sc = new Scanner(new File(fileName), "UTF-8");
			
			// input grid dimensions and simulation duration in timesteps
			dimt = sc.nextInt();
			dimx = sc.nextInt(); 
			dimy = sc.nextInt();
			
			// initialize and load advection (wind direction and strength) and convection
			advection = new Vector[dimt][dimx][dimy];
			convection = new float[dimt][dimx][dimy];
			for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						advection[t][x][y] = new Vector();
						advection[t][x][y].add(sc.nextFloat());
						advection[t][x][y].add(sc.nextFloat());
						convection[t][x][y] = sc.nextFloat();
					}
			
			classification = new int[dimt][dimx][dimy];
			sc.close(); 
		} 
		catch (IOException e){ 
			System.out.println("Unable to open input file "+fileName);
			e.printStackTrace();
		}
		catch (java.util.InputMismatchException e){ 
			System.out.println("Malformed input file "+fileName);
			e.printStackTrace();
		}
	}
        
        public void setClass()
        {
            for (int t = 0; t < dimt; t++)
            {
                for (int x = 0; x < dimx; x++)
                {
                    for (int y = 0; y < dimy; y++)
                    {                       
                        double windMag = getMag(t,x,y);
                        if (Math.abs(convection[t][x][y]) > windMag)
                        {
                            classification[t][x][y] = 0;
                            //System.out.print("0");
                        }
                        else if (windMag > 0.2)
                        {
                            classification[t][x][y] = 1;
                            //System.out.print("1");
                        }
                        else
                        {
                            classification[t][x][y] = 2;
                            //System.out.print("2");
                        }
                    }
                }
                System.out.println("");
            }
        }
        
        public double getMag(int t, int x, int y)
        {
            float localX = 0;
            float localY = 0;
            for (int i = x-1; i <= x+1; i++) 
            {
                if (i >= 0 && i < dimx)
                {
                    for (int j = y-1; j <= y+1; j++)
                    {
                        if (!(i == x && j == y) && j >= 0 && j < dimy)
                        {
                            localX += ((Float)advection[t][i][j].get(0)).floatValue();
                            localY += ((Float)advection[t][i][j].get(1)).floatValue();
                        }
                    }
                }
            }
            //System.out.println("");
            double out = Math.pow(localX, 2) + Math.pow(localY, 2);
            out = Math.sqrt(out);
            return (double)((int)out*10)/10;
        }
        
        public void setAve()
        {
            float windX = 0;
            float windY = 0;
            for (int t = 0; t < dimt; t++)
            {
                for (int x = 0; x < dimx; x++)
                {
                    for (int y = 0; y < dimy; y++)
                    {
                        windX += ((Float)advection[t][x][y].get(0)).floatValue();
                        windY += ((Float)advection[t][x][y].get(1)).floatValue();
                    }
                }
            }
            double outX = (double)((int)(windX/dim()*1000))/1000;
            double outY = (double)((int)(windY/dim()*1000))/1000;
            sum.add(outX);
            sum.add(outY);
        }
	
        public void printAve() //test method
        {
            System.out.println("Averages are [" +sum.get(0) +";" +sum.get(1) +"]");
        }
        
	// write classification output to file
	void writeData(String fileName, Vector wind){
		 try{ 
			 FileWriter fileWriter = new FileWriter(fileName);
			 PrintWriter printWriter = new PrintWriter(fileWriter);
			 printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			 printWriter.printf("%f %f\n", wind.get(0), wind.get(1));
			 
			 for(int t = 0; t < dimt; t++){
				 for(int x = 0; x < dimx; x++){
					for(int y = 0; y < dimy; y++){
						printWriter.printf("%d ", classification[t][x][y]);
					}
				 }
				 printWriter.printf("\n");
		     }
				 
			 printWriter.close();
		 }
		 catch (IOException e){
			 System.out.println("Unable to open output file "+fileName);
				e.printStackTrace();
		 }
	}
}
