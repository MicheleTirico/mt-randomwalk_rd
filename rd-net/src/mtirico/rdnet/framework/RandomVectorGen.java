package mtirico.rdnet.framework;

import java.util.Random;

import mtirico.graphtools.graphTool.GraphTool;

public class RandomVectorGen {

	// params
	private String id ;
	private Random rdLen, rdAng;
	private double[] extLen;
	private double deltaAng ; 
	
	public RandomVectorGen (String id , double[] extLen, double deltaAng, int seedRdLen, int seedRdAng) {
		this.id = id;
		this.extLen = extLen;
		this.deltaAng = deltaAng; 
		rdLen = new Random(seedRdLen);
		rdAng = new Random(seedRdAng);
	}
	
	public double[] getRandomVector (double[] sCoords, double[] pCoords) {
		double dist = GraphTool.getDistGeom(pCoords, sCoords);
		double cos = (sCoords[0] - pCoords[0]) / dist;
		double ang = Math.acos(cos);
		double newAng = ang + (rdAng.nextBoolean() == true ? 1 : -1) * deltaAng;
		double radius = extLen[0] + (extLen[1] - extLen[0]) * rdLen.nextDouble();
		double coordX = radius * Math.cos(newAng),
			   coordY = radius * Math.sin(newAng) * (sCoords[1] - pCoords[1] >= 0 ? 1 : -1); /// * * signY */ *sin ;
		return new double[] { coordX, coordY };
	}
	
	public String getId () { return id ; }
	public static void main(String[] args) { System.out.print(new Object(){}.getClass().getName()); }
}
