package mtirico.rdnet.layers;

import java.util.*;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import mtirico.rdnet.framework.Framework;

public class LayerCell extends Framework {

	private String id;
	private Cell[][] cells;
        private int[] size ;
	private static float f ,  k ,  Da,  Db ;
	private static float[][] kernel ;

  public LayerCell (String id, int[] size ) {
    this.id = id;
    this.size = size ;
		cells = new Cell[size[0]][size[1]];
  }

	public LayerCell (String id, int[] size, float[][] grid ) {
    this.id = id;
    this.size = size ;
		cells = new Cell[size[0]][size[1]];
		initGrid(grid);
  }

// init
// --------------------------------------------------------------------------------------------------------------------------------------
	public void setGsParameters ( float f , float k , float Da, float Db, float[][] kernel) {
		this.f = f ;
		this.k = k ;
		this.Da = Da ;
		this.Db = Db ;
		this.kernel = kernel;
	}

	public void initGrid(float[][] grid) {
		int id = 0 ;
		for (int x = 0; x < size[0]; x++)
			for (int y = 0; y < size[1]; y++) {
				Cell c = new Cell(id++, new int [] {x,y}, grid[x][y]);
				cells[x][y] = c;
			}
	}

	public void initCells (float[] AB) {
		int id = 0 ;
		for (int x = 0; x < size[0]; x++)
			for (int y = 0; y < size[1]; y++) {
				Cell c = new Cell(id++, new int [] {x,y}, AB);
				cells[x][y] = c;
		}
	}

	public void initCells (float val) { // use for static vf
		int id = 0 ;
		for (int x = 0; x < size[0]; x++)
			for (int y = 0; y < size[1]; y++) {
				Cell c = new Cell(id++, new int [] {x,y}, val );
				cells[x][y] = c;
			}
	}

	public void initSquare(float[] vals, int[] extX, int[] extY) {
		for ( int x = extX[0] ; x < extX[1] ; x++ ) {
			for ( int y = extY[0] ; y < extY[1] ; y++ ) {
				int setx = x % size[0] ;
				int sety = y % size[1] ;		//  System.out.println(x + "-> " + setx +", "+ y +"-> "+ sety);
				cells[setx][sety].setAB(vals);
			}
		}
	}
	public void initRandom (int seed, float[] rangeA, float [] rangeB ) {
		System.out.println("To test");
		Random rd = new Random( seed );
		for (int x = 0; x<size[0]; x++)
			for (int y = 0; y<size[1]; y++) {
				float[] vals = new float[2] ;
				vals[0] = rangeA[0] + rd.nextFloat() * (rangeA[1]-rangeA[0]);
				vals[1] = rangeB[0] + rd.nextFloat() * (rangeB[1]-rangeB[0]);
				Cell c = cells[x][y] ;	//				System.out.println(c + " " + vals[0] + " " + vals[1]);
				c.setAB(vals);
		}
	}

	public void initPerturb (int[] pos , float[] AB) { cells[pos[0]][pos[1]].setAB(AB); }


	public void initPerturb (int[] pos , float[] AB, int radius) {
		for (int x = -radius; x < radius ; x++)
			for (int y = -radius; y < radius ; y++)
				cells[pos[0]+ x][pos[1]+y].setAB(AB);
	}

	public void initPerturb (int[] pos , float[] rangeA, float[] rangeB, int radius) {
		Random rdA = new Random(0),rdB = new Random(1);
		for (int x = -radius; x < radius ; x++)
			for (int y = -radius; y < radius ; y++){
				float[] AB = new float[] {
					rangeA[0] + rdA.nextFloat()*(rangeA[1]-rangeA[0]),
					rangeB[0] + rdB.nextFloat()*(rangeB[1]-rangeB[0])
				};
				cells[pos[0]+ x][pos[1]+y].setAB(AB);
			}
	}

	public void initRandomPerturb (int seed , int num, float[] AB, int[] minXY , int[] maxXY) {
		System.out.println("To test");
		Random random = new Random(seed);
		for (int i = 0 ; i < num ; i++ ) {
			int x = random.nextInt(maxXY[0] - minXY[0]) + minXY[0];
			int y = random.nextInt(maxXY[1] - minXY[1]) + minXY[1];
			cells[x][y].setAB(AB);
		}
	}

	public void initPercentGraph (Graph g, float percent , float[] AB, boolean createSeeds) {
		ArrayList<Node> nodes = new ArrayList<Node>(g.getNodeSet());
		Collections.shuffle(nodes, new Random(0));
		Iterator<Node> it = nodes.iterator();
		int t = 0 ;
		while (t < g.getNodeSet().size() * percent && it.hasNext()) {
			Node n = it.next();
			double[] coords = GraphPosLengthUtils.nodePosition(n);
			initPerturb(new int[] {(int)coords[0],(int) coords[1]}, AB);
			if (createSeeds){
		    int idSeed = ls.getIdSeedInt();
	      Seed s = new Seed (idSeed++, coords, n, new ArrayList<Node>(Arrays.asList(n)), 0.f);
	      ls.addSeed(s);
		}
			t++;
		}
		System.out.println(t + " " + g.getNodeSet().size());
	}


// RD
// --------------------------------------------------------------------------------------------------------------------------------------
// does not work, try to make a toroid
/**	public void updateLayer (  ) {
		for ( int x = 0 ; x < size[0] ; x++) {
			for ( int y = 0 ; y < size[1] ; y++) {
				Cell c = cells[x][y] ;
				float[] vals = c.getAB();
				float	val0 = vals[0], val1 = vals[1];
				float [] diff  = getDiffusion(c) ;
				float diff0 = Da * diff[0],
						diff1 = Db * diff[1] ,
						react = val0 * val1 * val1 ,
						extA = f * ( 1 - val0 ) ,
						extB = ( f + k ) * val1 ;
				float	newval0 =  val0 + diff0 - react + extA,
						newval1 =  val1 + diff1 + react - extB;
				float [] newVals = new float[] { newval0 ,newval1 } ;
				c.setAB(newVals);
			}
		}
	}
	private float[] getDiffusion (  Cell c  ) {
		int r = 1;
		int[] posCore = c.getPos();
		float sumA = 0.0f , sumB = 0.0f ;
		for ( int x = -1 ; x <= 1 ;x++) {
			for ( int y = -1 ; y <= 1 ;y ++) {
				int px = c.getPos()[0]  , py = c.getPos()[1]  ;	//System.out.println(px + " " + py);
				int setx = px % size[0], sety = py % size[1];		//	System.out.println(setx + " " + sety);
				int posKx = x +1 , posKy = y +1 ;			//		System.out.println(posKx + " " + posKy);
				sumA = sumA + kernel[posKx][posKy] * cells[setx][sety].getAB()[0];
				sumB = sumB + kernel[posKx][posKy] * cells[setx][sety].getAB()[1];
			}
		}
		return new float[] { sumA , sumB };
	}
**/

	public void updateLayer (  ) {
		for ( int x = 0 ; x < size[0] ; x++) {
			for ( int y = 0 ; y < size[1] ; y++) {
				Cell c = cells[x][y] ;
				float[] vals = c.getAB();
				float 	val0 = vals[0],	val1 = vals[1];
				float [] diff  = getDiffusion(c) ;
				float diff0 = Da * diff[0], diff1 = Db * diff[1] ,
						react = val0 * val1 * val1 ,
						extA = f * ( 1 - val0 ) ,	extB = ( f + k ) * val1 ;
				c.setAB(new float[] { val0 + diff0 - react + extA ,val1 + diff1 + react - extB });
			}
		}
	}

	private float[] getDiffusion (  Cell c  ) {
		int[] posCore = c.getPos();
		float sumA = 0f , sumB = 0f ;
		for ( int x = 0 ; x < 3 ;x++) {
			for ( int y = 0 ; y < 3 ;y ++) {
				int posX =posCore[0] + x -1, posY = posCore[1] + y -1 ;

				if ( posCore[0] == 0 ) posX = size[0]-1;
				if ( posCore[1] == 0 ) posY = size[1]-1;
				if ( posCore[0] == size[0]-1) posX = 0 ;
				if ( posCore[1] == size[1]-1) posY = 0;

				sumA = sumA + kernel[x][y] * cells[posX][posY].getAB()[0];
				sumB = sumB + kernel[x][y] * cells[posX][posY].getAB()[1];
			}
		}
		return new float[] { sumA , sumB };
	}

// GET METHODS
// --------------------------------------------------------------------------------------------------------------------------------------
  public String getId () { return id; }
  public int[] getSize () { return size; }
	public Cell[][] getCells () { return cells; }
	public Cell getCell (int[] pos) { return cells[pos[0]][pos[1]]; }
	public Cell getCell (int x, int y ) { return cells[x][y]; }
public static void main (String[] args ){System.err.println(new Object(){}.getClass().getName());;}


}
