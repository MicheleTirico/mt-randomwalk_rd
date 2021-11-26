package mtirico.rdnet.layers;

import mtirico.rdnet.framework.Framework;

public class LayerVector extends Framework{

  // common
  private String id ;
  private Vector[][] vectors ;
  private float[][] kernel ,gridVals ;
  private int [] size ; //  todo private float[][]  vfColorMag ;  viz the vf with colors
  // static
  private typeStatic ts; // todo
  // rd
  private LayerCell lc ;
  private int posMorp ;
  // normalize vf rd
  private boolean isNorm;
  private float[] normMinMax ;

// constructors
  // constructor for rd
  public LayerVector (String id , int[]  size, int posMorp) {
    this.id = id ;
    this.size = size ;
    this.posMorp = posMorp  ;
    initEmpty();
  }
  //constructor for static
  public LayerVector (String id , int[]  size ) {
    this.id = id ;
    this.size = size ;
    initEmpty() ;
  }

//set initial parameters
public void setParamsNormVfRd (boolean isNorm,float[] normMinMax) {
    this.isNorm=isNorm;
    this.normMinMax=normMinMax;
}

// initialization
  // useful, test a simple vf, set to each vector the same magnitude
  public void initStatic(float[] magXY) {
    int id = 0 ;
    for (int x = 0 ; x < size[0] ; x++)
      for (int y = 0 ; y < size[1] ; y++)
        vectors[x][y] = new Vector (id++, new int [] {x,y}, magXY);
  }

  public void initStaticConvolution () {
    int id = 0 ;
    for (int x = 0 ; x < size[0] ; x++)
      for (int y = 0 ; y < size[1] ; y++){
        float[] magXY = computeMagVectorConvolution(new int[] {x,y},gridVals);
        vectors[x][y] = new Vector (id++, new int [] {x,y}, magXY);
      }
  }

  private void initEmpty() {
    vectors = new Vector[size[0]][size[1]] ;
    int id = 0 ;
    for (int x = 0 ; x < size[0] ; x++)
      for (int y = 0 ; y < size[1] ; y++)
        vectors[x][y] = new Vector (id++, new int [] {x,y}, new float [] {0f,0f});
  }

  public float[] computeMagVectorConvolution (int[] pos, float[][] grid) {
    int coordCellX = pos[0],  coordCellY = pos[1];
		float magX = 0, magY = 0;
    float val00 = lc.getCell( pos[0], pos[1]).getVal();
  		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
        Cell c = lc.getCell( ( (pos[0] + x) % size[0] + size[0])%size[0], ( (pos[1] + y) % size[1] + size[1]) % size[1]);
  //      System.out.println(c);
        float valxy = c.getVal() ; //grid[x] [y] ; ;
				magX += (x) * (val00 + kernel[x + 1][y + 1] * valxy);
				magY += (y) * (val00 + kernel[x + 1][y + 1] * valxy);
			}
		}
    return new float[] { magX,  magY };
  }

  // compute the magnitude of a vector with convolution
  public float[] computeMagVectorConvolution (int[] pos) {
    int coordCellX = pos[0],  coordCellY = pos[1];
		float magX = 0, magY = 0;
    float val00 = lc.getCell( pos[0], pos[1]).getAB()[posMorp];
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
        Cell c = lc.getCell( ( (pos[0] + x) % size[0] + size[0])%size[0], ( (pos[1] + y) % size[1] + size[1]) % size[1]);
        float valxy = c.getAB()[posMorp];
				magX += (x) * (val00 + kernel[x + 1][y + 1] * valxy);
				magY += (y) * (val00 + kernel[x + 1][y + 1] * valxy);
			}
		}

    float mag = Vector.computeMag(magX,magY);
//    float valNorm = 0.5f , valMin = 0.0f;
    if (isNorm) {
      if (mag > normMinMax[1] ){
        magX = (normMinMax[1] * magX / mag);  magY = (normMinMax[1] * magY / mag);
      } else if (Float.isNaN(mag)) {
        magX = 0f ; magY = 0f ;
      }
      else if (Float.isInfinite(mag)) {
        magX = 0f ; magY = 0f ;
      }
      else if (mag < normMinMax[0] ){
        magX =(float) (normMinMax[0] * magX / mag);  magY =(float) (normMinMax[0] * magY / mag);//      System.out.println(mag);
      }
    }
    return new float[] { magX,  magY };
  }

  public void updateLayerRD () {
    for (int x = 0 ; x < size[0] ; x++)
      for (int y = 0 ; y < size[1] ; y++) {
        float[] magXY = this.computeMagVectorConvolution(new int[] {x,y});
        vectors[x][y].setMagXY(magXY);
      }
  }

// get and set
  public void setLc (LayerCell lc) { this.lc = lc; }
  public void setKernel (float[][] kernel ) { this.kernel = kernel; }
  public void setMagXY (int x,int y , float[] magXY) { getVector(x, y).setMagXY(magXY);}
  public int[] getSizeGrid () { return size; }
  public Vector getVector (int[] pos) { return vectors[pos[0]][pos[1]]; }
  public Vector getVector (int x, int y) { return vectors[x][y]; }
  public String getId () { return id; }

// test class
  public static void main (String[] args) {
    System.out.println("test the class -> "+ new Object(){}.getClass().getName());
    int[] size = new int[] {16,16};
    System.out.println( (-1 % 16.0)) ;
    for (int v = 0 ; v < 16 ; v++)
      for (int x = -1; x <= 1; x++)
        System.out.println("v = "+ v +"/ x = "+ x + " / val = " + ((v+x)%size[0] + 16)% 16 ) ;
      System.out.println("mtirico.rdnet.layers.LayerVector.main()");

}


}
