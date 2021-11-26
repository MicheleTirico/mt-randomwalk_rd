package mtirico.rdnet.framework;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import mtirico.graphtools.graphanalysis.IndicatorSet;
import mtirico.graphtools.graphanalysis.IndicatorSet.indicator;
import mtirico.graphtools.graphgenerator.RandomWalkSeed;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;

public class Utils {

  public static ArrayList<indicator> listInd = new ArrayList<indicator> ( Arrays.asList(
  indicator.nodeCount
  ,indicator.averageDegree
  ,indicator.edgeCount
  ,indicator.totalEdgeLength
  ,indicator.averageLen
  ,indicator.gammaIndex
  ,indicator.organicRatio,
  indicator.meshedness,
  indicator.cost
  )) ;

  public static enum RdmType {
    holes("holes",0.039f,0.058f),
    solitons("solitons",0.030f, 0.062f),
    mazes("mazes", 0.029f, 0.057f),
    movingSpots ("movingSpots",0.014f, 0.054f),
    pulsatingSolitions("pulsatingSolitions",0.025f, 0.060f),
    U_SkateWorld("U_SkateWorld", 0.062f, 0.061f),
    flower ("flower",0.055f, 0.062f),
    chaos("chaos",0.026f,0.051f),
    spotsAndLoops("spotsAndLoops",0.018f, 0.051f),
    worms("worms",0.078f, 0.061f),
    waves("waves",0.014f, 0.045f),
    equilibrium("equilibrium",0.0625f,0.0625f);

    private String id;
    private float f,k;
    RdmType(String id, float f , float k ){this.id=id; this.f=f; this.k=k; }

    public String getId() {return id;}
    public float[] getFK () {return new float[]{this.f,this.k};}
    public float getF () { return this.f;}
    public float getK () { return this.k;}
    public String getName() {return this.toString(); };
  }

  public static RdmType getRdmFomId (String id) {
    for (RdmType rdm : RdmType.values())
      if (rdm.getId().equals(id)) return rdm;
    return null ;
  }

  public static float[][] getKernel () {
      return new float[][] {
        new float[]{0.05f,0.2f,0.05f},
        new float[]{0.2f,-1f,0.2f},
        new float[]{0.05f,0.2f,0.05f}
      };
  }

  public static float[][] getKernelNeg () {
      return new float[][] {
        new float[]{-0.05f,-0.2f,-0.05f},
        new float[]{-0.2f,1f,-0.2f},
        new float[]{-0.05f,-0.2f,-0.05f}
      };
  }

  public static ArrayList<Double> getArrayListVals (double min , double max , double increm  ) {
    ArrayList<Double> list = new ArrayList<Double> ();
    int num = (int) (( max - min ) / increm)  ;
    int pos = 0 ;
    while ( pos < num ) {
      list.add(  (double) Math.round( (min + increm * pos) *10000 )/10000 )  ;
      pos++;
    }
    return list ;
  }

  public static float[][] getGridValCost(int sizeGrid, float val) {
    float[][] grid = new float [(int)Math.pow(2,sizeGrid)][(int)Math.pow(2,sizeGrid)] ;
    for (int x = 0 ; x < grid[0].length ; x++ )
      for (int y = 0 ; y  < grid.length ; y++ )
        grid[x][y] = val;
    return grid;
  }

  public static float[][] getRandomGrid (int sizeGrid , int seedRd, float[] minMax) {
    Random rd = new Random(seedRd);
    float[][] grid = new float [(int)Math.pow(2,sizeGrid)][(int)Math.pow(2,sizeGrid)] ;
    for (int x = 0 ; x < grid[0].length; x++  )
      for (int y = 0 ; y < grid.length; y++  ){
        grid[x][y] = minMax[0] + rd.nextFloat() * (minMax[1] - minMax[0]);
      }
    return grid ;
  }

  public static Graph getPathStar (int [] sizeGrid, int numNodes, double[] center, double[] extLen , double deltaAng , double angleStart ) {
    Graph g = null ;
    RandomWalkSeed rws = new RandomWalkSeed(sizeGrid);
    rws.setSeedRandom(1	,1 ,1 , 1 );
    rws.setParamsCreateSeed(  100, 0, false);
    rws.setParamsRemoveSeed(  100, 0);
    rws.initStar(numNodes, center, extLen[1] , angleStart);
    rws.setParamsVector(extLen, deltaAng  * Math.PI  );
    g =  rws.getGraph() ;
//    g.getNodeSet().stream().forEach(n-> n.addAttribute("hasSeed", false));
    double t = 0 ;
    while ( t <= 10000) {
      g.stepBegins(t);
      rws.compute();
      t++;
    }
    return g ;
  }
  public static float[][] getGridCircle (int sizeGrid, int[] centre, float radius , float[] minMax ) {
    float[][] grid = getGridValCost(sizeGrid, 0.0f) ; // new float [(int)Math.pow(2,sizeGrid)][(int)Math.pow(2,sizeGrid)] ;
    float incX = .5f, incAng = 0.001f;
    for (float ang =0 ; ang < 1 ; ang += incAng ){
        for (float x = 0f ; x < radius ; x += incX  ){
        double deltaX= Math.cos(ang * Math.PI * 2 )*x , deltaY =  Math.sin(ang * Math.PI * 2 )*x ;
        int posX = (int) deltaX + centre[0];
        int posY = (int) deltaY + centre[1];
        float mag = minMax[0] + (x / radius)* (minMax[1]-minMax[0])  ; //* minMax[1] / minMax[0] / radius;
        grid[posX][posY] = mag ;
      }
    }
    return grid;
  }

  public static float[][] getBarrierCircle (int sizeGrid, int[] centre, float radius, float offset, float[] minMax) {
    float[][] grid = getGridValCost(sizeGrid, 0.0f) ; // new float [(int)Math.pow(2,sizeGrid)][(int)Math.pow(2,sizeGrid)] ;
    float incX = .5f, incAng = 0.001f;
    for (float ang =0 ; ang < 1 ; ang += incAng ){
        for (float x = radius - offset ; x < radius + offset ; x += incX  ){
          double deltaX= Math.cos(ang * Math.PI * 2 )*x , deltaY =  Math.sin(ang * Math.PI * 2 )*x ;
          int posX = (int) deltaX + centre[0];
          int posY = (int) deltaY + centre[1];
          float mag = minMax[0] + (x / (radius+offset))* (minMax[1]-minMax[0])  ; //* minMax[1] / minMax[0] / radius;
          grid[posX][posY] = mag ;
      }
    }
    return grid;
  }

  public static float[][] getBarrierArc (int sizeGrid, int[] centre, float radius, float offset, float[] minMax,float[]minMaxAng) {
    float[][] grid = getGridValCost(sizeGrid, 0.0f) ; // new float [(int)Math.pow(2,sizeGrid)][(int)Math.pow(2,sizeGrid)] ;
    float incX = .5f, incAng = 0.001f;
    for (float ang =minMaxAng[0] ; ang < minMaxAng[1] ; ang += incAng ){
        for (float x = radius - offset ; x < radius + offset ; x += incX  ){
          double deltaX= Math.cos(ang * Math.PI * 2 )*x , deltaY =  Math.sin(ang * Math.PI * 2 )*x ;
          int posX = (int) deltaX + centre[0];
          int posY = (int) deltaY + centre[1];
          float mag = minMax[0] + (x / (radius+offset))* (minMax[1]-minMax[0])  ; //* minMax[1] / minMax[0] / radius;
          grid[posX][posY] = mag ;
      }
    }
    return grid;
  }

  public static float [][] getBarrierLine (int sizeGrid, int[] minMaxX, float[] minMax) {
    float[][] grid = getGridValCost(sizeGrid, 0.0f) ; // new float [(int)Math.pow(2,sizeGrid)][(int)Math.pow(2,sizeGrid)] ;
    float incX = .5f;
    for (float x = minMaxX[0] ; x <= minMaxX[1]; x += incX  ){
      for (int y = 0; y < (int)Math.pow(2,sizeGrid); y++ ){
        float mag = minMax[0] + (x / (x - minMax[0]))* (minMax[1]-minMax[0]) ; // x / (minMaxX[1]-minMaxX[0]);
  //      System.out.println(x +" " + y + " " + mag );
        grid[(int)x][y] = mag ;
      }
    }
    return grid;
  }

  public static float[][] getBumps (int sizeGrid, float incremX  ,float  incremY ,float incremZ ) {
    float[][] grid = getGridValCost(sizeGrid, 0.0f) ; // new float [(int)Math.pow(2,sizeGrid)][(int)Math.pow(2,sizeGrid)] ;
    for (int x=0 ; x <Math.pow(2,sizeGrid); x++)
      for (int y=0 ; y <Math.pow(2,sizeGrid); y++) {
        double val =  incremZ *( Math.sin(x*incremX) * Math.cos( y*incremY ) ) ;
        grid[x][y] = (float)val;
      }
    return grid;
  }

  public static double[] getDegreeDistrPercent(Graph g) {
    int[] degreeDistr = Toolkit.degreeDistribution(g);
    double[] percent = new double[degreeDistr.length];
    int nodeCount = g.getNodeCount();
    for (int p = 0; p <degreeDistr.length;p++ ) percent[p] = degreeDistr[p] * 1.0 / nodeCount;
    return percent;
  }

  public static void printDegreeDistr(Graph g){
//    System.out.print("Deg distr percent : ");
    int p = 0 ;
    for (double v : getDegreeDistrPercent(g))  System.out.format("%d=%.3f, ",p++,v);
    System.out.println();
  }

  public static ArrayList<Double> getArrayIndicators (Graph g , ArrayList<indicator> listInd){
    ArrayList<Double> vals = new ArrayList<Double>();
    IndicatorSet is = new IndicatorSet();
    is.setGraph(g);
    for ( indicator in : listInd ) vals.add( is.getValue(in))  ;
    return vals;
  }

  public static void printArray (ArrayList<Double> vals) {
    for (Double i : vals) System.out.print(String.format("%.3f ",i));
    System.out.println();
  }

  public static void main (String[] args) throws FileNotFoundException, UnsupportedEncodingException  {
    String test = args[0];

    System.out.println(test + " " + getRdmFomId(test));
  }

}
