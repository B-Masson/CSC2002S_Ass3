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
        tick();
        CloudData data = new CloudData();
        CloudDataP parallel = new CloudDataP();
        data.readData("simplesample_input.txt");
        System.out.println("Read time: " +tock() +"s");
        /*for (int i = 0; i < 3; i++)
        {
            tick();
            data.setAve();
            data.setClass();
            //data.writeData("testoutputlarge.txt");
            System.out.println("Runtime (warm-up):" +tock() +"s");
        }*/
        /*tick();
        data.setAve();
        data.setClass();     
        System.out.println("Runtime (true) " +tock() +"s");
        System.gc();
        tick();
        data.writeData("testoutputmedium.txt");
        System.out.println("Write time: " +tock() +"s");*/
        data.setAve();
        data.printAve();
        parallel.readData("simplesample_input.txt");
        parallel.calculate();
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
