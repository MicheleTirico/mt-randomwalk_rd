/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mtirico.rdnet.run;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author mtirico
 */
public class RunDemo {

    public static void main(String[] args)  throws IOException {
    String text =
    "Parameters:\n"+
    "TIP: for a basic example, use: 1000 7 0.039 0.058 1.0 0.5 0.7 0.7 0.5 0.5 10 0 0.02 0 0 0.00001 0.001 true test test2 \n"+
    "step -> number of step to compute+\n"+
    "size -> size of grid 2^size[2-10]\n"+
    "feed -> feed rate of RD [0.0-0.1]\n" +
    "kill -> kill rate of RD [0.0-0.1]\n" +
    "Da -> diffusion rate of morphogen A [0-1.0]\n"+
    "Db -> diffusion rate of morphogen B [0-1.0]\n" +
    "RDxy -> coordinates where put perturbation (% of length) x,y = [0.0-1.0]\n"+
    "coreG -> coordinates where put the starting graph (% of length) x,y = [0.0-1.0]\n"+
    "nb -> cardinality of starting graph [1-20]\n" +
    "rdAng -> random seed parameter for angles [0 -oo]\n"+
    "deltaAng -> range of new angle [0-1.0]\n"+
    "rdAdd -> random seed parameter for generate seeds [0-oo]\n"+
    "rdRem -> random seed parameter for remove seeds [0-oo]\n"+
    "pc -> probability to create a new seed [0.0-1.0]\n" +
    "pd -> probability to remove existing seeds [0.0-1.0]\n"+
    "viz -> show layers\n"+
    "pathRD -> path to store RD \n"+
    "pathGraph -> path to store dgs file \n"
    ;

    System.out.println("test the class -> "+ new Object(){}.getClass().getName() + " at " + new Date().toString()  +"\n"+text) ;
    try {
      new RunDemo(args);
    } catch (ArrayIndexOutOfBoundsException ex) {
      System.out.println(    "There a problem with initial parameters. \n"+
      "Tip: for a basic example, use: 1000 7 0.039 0.058 1.0 0.5 0.7 0.7 0.5 0.5 10 0 0.02 0 0 0.00001 0.001 true pathStoreRd pathStoreGraph");
    }
  }

  public RunDemo (String [] args) {
// parameters
    int pos= 0;
    int stepMax = Integer.parseInt(args[pos++]);
    int sizeGrid = Integer.parseInt(args[pos++]);
    float feed= Float.parseFloat(args[pos++]),
    kill= Float.parseFloat(args[pos++]),
    Da = Float.parseFloat(args[pos++]),
    Db= Float.parseFloat(args[pos++]) ;
    int[] perturbRD = new int[] {(int) (Double.parseDouble(args[pos++]) * Math.pow(2,sizeGrid)),(int) (Double.parseDouble(args[pos++]) * Math.pow(2,sizeGrid))};
    double[] coreG = new double[] {(int) (Double.parseDouble(args[pos++]) * Math.pow(2,sizeGrid)),(int) (Double.parseDouble(args[pos++]) * Math.pow(2,sizeGrid))};
    int nb =Integer.parseInt(args[pos++]);
    int rdAng =Integer.parseInt(args[pos++]);
    float deltaAng = Float.parseFloat(args[pos++]);
    int rdAdd=Integer.parseInt(args[pos++]),
    rdRem=Integer.parseInt(args[pos++]);
    float pc = Float.parseFloat(args[pos++]),
    pr=Float.parseFloat(args[pos++]);
    boolean viz = Boolean.parseBoolean(args[pos++]);
    String pathStoreRD = args[pos++], pathStoreGraph = args[pos++] ;
    System.out.println("list of parameters: "+Arrays.toString(args)+"\n"+"start simulation");

    
  }
    
}
