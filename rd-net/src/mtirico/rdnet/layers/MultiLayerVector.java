package mtirico.rdnet.layers;

import mtirico.rdnet.framework.Framework;

public class MultiLayerVector extends Framework {

  private String id ;
  private LayerVector lvrd , lvStat ;
  private Vector[][] vectors;
  private int[] size ;

  public MultiLayerVector ( String id , int[] size ,LayerVector lvrd , LayerVector lvStat ) {
    this.id = id ;
    this.size = size ;
    this.lvrd = lvrd ;
    this.lvStat = lvStat ;
    initVectors() ;
  }

  private void initVectors () {
    int id = 0 ;
    vectors = new Vector[size[0]][size[1]] ;
    for (int x = 0 ; x < size[0] ; x++) {
      for (int y = 0 ; y < size[1] ; y++) {
        vectors[x][y] = new Vector (id++, new  int[] {x,y}, new float[]{0f,0f});
      }
    }
  }

  public void updateLayers(){
    for (int x = 0 ; x < size[0] ; x++) {
      for (int y = 0 ; y < size[1] ; y++) {
        float[] magRD = lvrd.computeMagVectorConvolution(new int[] {x,y}) ,
          magStat = lvStat.getVector(x, y).getMagXY(),
          magSum = new float[] {magRD[0] + magStat[0], magRD[1] + magStat[1]};
          vectors[x][y].setMagXY(magSum);
          lvrd.setMagXY(x,y,magRD);
      }
    }
  }

  public Vector getVector (int[] pos )  {
    try {
      return vectors[pos [0]][pos[1]];
    } catch ( java.lang.ArrayIndexOutOfBoundsException  e) {
      System.out.println(e);
      return null;
    }
  }


  public Vector getVectorRD (int[] pos )  {
    try {
      return lvrd.getVector(pos);
    } catch ( java.lang.ArrayIndexOutOfBoundsException  e) {
      System.out.println(e);
      return null;
    }
  }


  public static double[] getSumMag (double[] magXY_00,double[] magXY_01) {
    return  new double [] {magXY_00[0] + magXY_01[0], magXY_00[1] + magXY_01[1]} ;
  }

  public String getId () { return id; }
    public static void main (String[] args ){System.out.println("mtirico.rdnet.layers.Cell.main()");}

}
