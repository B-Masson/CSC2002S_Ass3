import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.PrintStream;
import java.util.Vector;
import java.util.concurrent.ForkJoinPool;

public class CloudDataP{

	Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	static float [][][] convection; // vertical air movement strength, that evolves over time
	int [][][] classification; // cloud type per grid point, evolving over time
	static int dimx, dimy, dimt; // data dimensions
        Vector sum = new Vector(); //stores average X and Y wind values for entire grid
        int min, max;
        int cut;
        static final ForkJoinPool swimpool = new ForkJoinPool();
        
        public CloudDataP()
        {
            
        }
        
        public void setCut(int c)
        {
            cut = c;
        }
        
	// overall number of elements in the timeline grids
	int dim(){
		return dimt*dimx*dimy;
	}
	
        public void calculate()
        {
            Vector aveWinds = swimpool.invoke(new ParallelWorks(0, dim(), cut, advection, classification));
            float windX = (Float)aveWinds.get(0);
            float windY = (Float)aveWinds.get(1);
            double outX = (double)((int)(windX/dim()*1000))/1000;
            double outY = (double)((int)(windY/dim()*1000))/1000;
            sum.add(outX);
            sum.add(outY);
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
                        //System.out.println("Mag is " +windMag);
                        //System.out.println("And convection is " +convection[t][x][y]);
                        //System.out.print("Therefore class: ");
                        if (Math.abs(convection[t][x][y]) > windMag)
                        {
                            classification[t][x][y] = 0;
                            //System.out.println("0");
                        }
                        else if (windMag > 0.2)
                        {
                            classification[t][x][y] = 1;
                            //System.out.println("1");
                        }
                        else
                        {
                            classification[t][x][y] = 2;
                            //System.out.println("2");
                        }
                    }
                }
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
            return out;
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
	void writeData(String fileName){
		 try{ 
			 PrintStream out = new PrintStream(new File(fileName));
			 out.println(dimt +" " +dimx +" " +dimy);
			 out.println(sum.get(0) +" " +sum.get(1));
			 
			 for(int t = 0; t < dimt; t++){
				 for(int x = 0; x < dimx; x++){
					for(int y = 0; y < dimy; y++){
						out.print(classification[t][x][y] +" ");
					}
				 }
				 out.println("");
		     }
				 
			 out.close();
                         System.out.println("Written!");
		 }
		 catch (IOException e){
			 System.out.println("Unable to open output file "+fileName);
				e.printStackTrace();
		 }
	}        
}
