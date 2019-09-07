
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
        String inFile = args[0];
        String outFile = args[1];
        float sumS = 0, sumP = 0;
        CloudData data = new CloudData();
        CloudDataP parallel = new CloudDataP();
        parallel.setCut(1);
        data.readData(inFile);
        parallel.readData(inFile);
        System.out.println("Read in");
        for (int i = 0; i < 3; i++)
        {
            data.setAll();
            parallel.calculate();
            System.out.println("Warming ..");
        }
        System.out.println("All warmed up");
        System.gc(); //Garbage collection called here to clean memory up
        for (int j = 0; j < 5; j++)
        {
            tick();
            data.setAll();
            float tempS = tock();
            System.out.println("Sequential Runtime " +(j+1) +": " +tempS +"s");     
            sumS += tempS;
            tick();
            parallel.calculate();
            float tempP = tock();
            sumP += tempP;
            System.out.println("Parallel Runtime " +(j+1) +": " +tempP +"s");
        }
        System.out.println("\nSequential Average: " +(sumS/5) +"s");
        System.out.println("Parallel Average: " +(sumP/5) +"s");
        //data.writeData("seqout_large.txt");
        parallel.writeData(outFile);
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
