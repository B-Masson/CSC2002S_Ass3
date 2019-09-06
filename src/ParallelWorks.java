

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
    static final int cut = 1;
    
    // convert linear position into 3D location in simulation grid
    private int[] locate(int pos)
    {
        int dimt = CloudDataP.dimt;
        int dimx = CloudDataP.dimx;
        int dimy = CloudDataP.dimy;
        int[] ind = new int[3];
        ind[0] = (int) pos / (dimx*dimy); // t
        ind[1] = (pos % (dimx*dimy)) / dimy; // x
        ind[2] = pos % (dimy); // y
        return ind;
    }
    public ParallelWorks(int l, int h, Vector[][][] a)
    {
        min = l;
        max = h;
        adList = a;
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
            }
            out.add(sumX);
            out.add(sumY);
            return out;
        }
        else
        {
            int mid = (max+min)/2;
            ParallelWorks left = new ParallelWorks(min, mid, adList);
            ParallelWorks right = new ParallelWorks(mid, max, adList);
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
