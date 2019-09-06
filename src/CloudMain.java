
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Richard
 */
public class CloudMain
{
    static long startTime = 0; //used for timing methods
    public static void main(String[] args)
    {
        CloudData data = new CloudData();
        CloudDataP parallel = new CloudDataP();
        data.readData("largesample_input.txt");
        parallel.readData("largesample_input.txt");
        System.out.println("Read in");
        for (int i = 0; i < 3; i++)
        {
            tick();
            //data.setAve();
            //data.setClass();
            data.setAll();
            System.out.println("Seq warm-up " +(i+1) +": " +tock() +"s");
            tick();
            parallel.calculate();
            System.out.println("Parallel warm-up " +(i+1) +": " +tock() +"s");
        }
        System.gc();
        tick();
        //data.setAve();
        //data.setClass();
        data.setAll();
        System.out.println("Sequential Runtime (true): " +tock() +"s");     
        tick();
        parallel.calculate();
        System.out.println("Parallel Runtime (true): " +tock() +"s");
        //data.writeData("seqout_large.txt");
        //parallel.writeData("parout_large.txt");
    }
    
    private static void tick() //Taken from lecture notes at permission of James Gain
        {
            startTime = System.currentTimeMillis();
        }
        
        private static float tock()
        {
            return (System.currentTimeMillis() - startTime) /1000.0f;
        }
}
