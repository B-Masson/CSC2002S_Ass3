

import java.util.Vector;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Richard
 */
public class ParallelWorks extends RecursiveTask<Vector>
{
    int min;
    int max;
    Vector[][][] adList;
    float sumX, sumY;
    int[][][] classList;
    int cut = 1;
    int dimt, dimx, dimy;
    
    // convert linear position into 3D location in simulation grid
    
    public ParallelWorks(int l, int h, int c, Vector[][][] a, int[][][] k)
    {
        min = l;
        max = h;
        cut = 1;
        adList = a;
        classList = k;
        dimt = CloudDataP.dimt;
        dimx = CloudDataP.dimx;
        dimy = CloudDataP.dimy;
    }
    
    private int[] locate(int pos)
    {
        
        int[] ind = new int[3];
        ind[0] = (int) pos / (dimx*dimy); // t
        ind[1] = (pos % (dimx*dimy)) / dimy; // x
        ind[2] = pos % (dimy); // y
        return ind;
    }
    
    public void setClass(int t, int x, int y)
        {
            double windMag = getMag(t,x,y);
            if (Math.abs(CloudDataP.convection[t][x][y]) > windMag)
            {
                classList[t][x][y] = 0;
            }
            else if (windMag > 0.2)
            {
                classList[t][x][y] = 1;
            }
            else
            {
                classList[t][x][y] = 2;
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
                        if (j >= 0 && j < dimy)
                        {
                            localX += ((Float)adList[t][i][j].get(0));
                            localY += ((Float)adList[t][i][j].get(1));
                        }
                    }
                }
            }
            //System.out.println("");
            double out = Math.pow(localX, 2) + Math.pow(localY, 2);
            out = Math.sqrt(out);
            return out;
        }
    
    @Override
    protected Vector compute()
    {
        if ((max-min) <= cut)
        {
            sumX = 0;
            sumY = 0;
            Vector out = new Vector();
            for (int i = min; i < max; i++)
            { 
                int[] loc = locate(i);
                int tt = loc[0];
                int tx = loc[1];
                int ty = loc[2];
                Vector temp = adList[tt][tx][ty];
                sumX += ((Float)temp.get(0));
                sumY += ((Float)temp.get(1));
                setClass(tt, tx, ty);
            }
            out.add(sumX);
            out.add(sumY);
            return out;
        }
        else
        {
            int mid = (max+min)/2;
            ParallelWorks left = new ParallelWorks(min, mid, cut, adList, classList);
            ParallelWorks right = new ParallelWorks(mid, max, cut, adList, classList);
            left.fork();
            Vector rVec = right.compute();
            Vector lVec = left.join();
            float tempx = ((Float)rVec.get(0)) + ((Float)lVec.get(0));
            float tempy = ((Float)rVec.get(1)) + ((Float)lVec.get(1));
            Vector resultant = new Vector();
            resultant.add(tempx);
            resultant.add(tempy);
            return resultant;
        }
    }
    
}
