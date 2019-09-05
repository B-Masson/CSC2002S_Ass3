

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
public class ParallelWorks extends RecursiveAction
{
    int min;
    int max;
    Vector[][][] adList;
    Vector[][][] classList;
    float sumX, sumY;
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
        sumX = 0;
        sumY = 0;
    }
    
    @Override
    protected void compute()
    {
        if ((max-min) <= cut)
        {
            for (int i = min; i < max; i++)
            {
                int[] loc = locate(i);
                int tt = loc[0];
                int tx = loc[1];
                int ty = loc[2];
                Vector temp = adList[tt][tx][ty];
                sumX += ((Float)temp.get(0)).floatValue();
                sumY += ((Float)temp.get(1)).floatValue();
            }
        }
        else
        {
            int mid = (max+min)/2;
            invokeAll(new ParallelWorks(min, mid, adList), new ParallelWorks(mid, max, adList));
        }
    }
    
    public void printAve()
    {
        int total = CloudDataP.dimt*CloudDataP.dimx*CloudDataP.dimy;
        double outX = (double)((int)(sumX/total*1000))/1000;
        double outY = (double)((int)(sumY/total*1000))/1000;
        System.out.println(outX +"," +outY);
    }
}
