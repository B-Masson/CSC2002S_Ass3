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
    public static void main(String[] args)
    {
        CloudData data = new CloudData();
        data.readData("simplesample_input.txt");
        data.setAve();
        data.printAve();
        data.setClass();
    }
}
