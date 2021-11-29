package mtirico.rdnet.layers;

public class Cell {
    private int id;
    private float val ;
    private int[] pos = new int[2] ; // the position in the grid
    private float[] AB = new float[2] ; // a set of values
    private Vector vector ; // the sum of vectors

    public Cell(int id , int[] pos , float[] AB ) {
        this.id = id ;
        this.pos = pos;
        this.AB = AB ;
    }

    public Cell(int id , int[] pos , Vector vector ) {
        this.id = id ;
        this.pos = pos;
        this.vector = vector ;
    }

    public Cell(int id , int[] pos , float val ) {
        this.id = id ;
        this.pos = pos;
        this.val = val ;
    }


public int[] getPos () { return pos;}
public float[] getAB () { return AB;}
public int getId () { return id;}
public Vector getVector () { return vector; }
public float getVal () { return val;}


public void setId (int id ) {this.id = id; }
public void setVal (float val ) {this.val = val; }
public void setAB (float[] AB ) {this.AB = AB; }
public void setVector (Vector vector ) { this.vector = vector; }

public static void main (String[] args ){System.err.println(new Object(){}.getClass().getName());;}

}
