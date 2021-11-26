package mtirico.rdnet.layers;

public class Vector {

  private int id  ; // todo
  private float mag;
  private float[] magXY;
  private int[] pos;

  public Vector (int id, int[] pos, float[] magXY) {
      this.id = id ;
      this.pos = pos ;
      this.magXY = magXY;
      mag = computeMag(magXY); //(float) Math.pow(Math.pow(magXY[0],2) + Math.pow(magXY[1],2), 0.5);
  }

  public float[] getMagXY () { return magXY; }
  public float getMag () {  mag = computeMag(magXY); return mag; }
  public int[] getPos () { return pos; }

  public void setMagXY (float[] magXY) { this.magXY = magXY; this.mag = computeMag(magXY); }
  public void setMag (float mag) { this.mag = mag;  }
  public int getId() { return id; }
  protected static float computeMag (float magX, float magY) { return  (float) Math.pow(Math.pow(magX,2) + Math.pow(magY,2), 0.5); }
  protected static float computeMag (float[] magXY) { return  (float) Math.pow(Math.pow(magXY[0],2) + Math.pow(magXY[1],2), 0.5); }
    public static void main (String[] args ){System.out.println("mtirico.rdnet.layers.Cell.main()");}

}
