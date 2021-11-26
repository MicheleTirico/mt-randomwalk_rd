package mtirico.rdnet.framework;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;
import mtirico.rdnet.framework.Utils.RdmType;
import mtirico.rdnet.layers.LayerCell;
import mtirico.rdnet.layers.LayerVector;
import mtirico.rdnet.layers.Vector;
        
public class VizVectorField extends JFrame {

	protected int[] sizeGrid;
        protected LayerVector vf ;
	protected float[][] gridVals;
	protected BufferedImage iRd;
	protected Graphics2D gfx;
	protected JPanel canvas;

	public VizVectorField(LayerVector vf) {
		this.sizeGrid = vf.getSizeGrid();
		this.vf = vf ;

		iRd = new BufferedImage(sizeGrid[0], sizeGrid[1], BufferedImage.TYPE_INT_ARGB);
		gfx = iRd.createGraphics();

		canvas = new RDPanel(iRd);

		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		pack();
		setSize(new Dimension(getInsets().right + getInsets().left + sizeGrid[0],
				getInsets().top + getInsets().bottom + sizeGrid[1]));
		setVisible(true);
		setResizable(true);


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void step() {
		renderImage();
		drawImage();
	}

	public void closePanel () {
			this.setVisible(false);
	}

	public void saveJPEG(String nameFile) throws IOException {
		ImageIO.write(iRd, "jpeg", new File(nameFile));
	}

	public void savePNG(String nameFile) throws IOException {
		ImageIO.write(iRd, "png", new File(nameFile + ".png"));
	}

	protected void renderImage() {
		int size = iRd.getWidth();
		assert (sizeGrid[0] == size && sizeGrid[1] == size);
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
        Vector v = vf.getVector(x, y);
        float magX = v.getMagXY()[0]  , magY = v.getMagXY()[1] , mag = v.getMag() ;
        float valX = magX/mag, valY = magY/mag;
  //      System.out.println(valX + " "+ valY +" " +mag)  ;
				iRd.setRGB(x, size - y - 1, new Color( (valX + 1)/2,(valY + 1)/2,0.0f).getRGB());
			}
		}
	}

	protected void drawImage() {
		canvas.repaint();
	}

	class RDPanel extends JPanel {
		protected BufferedImage iRd;

		public RDPanel(BufferedImage ird) {
			iRd = ird;
			setPreferredSize(new Dimension(iRd.getWidth(), iRd.getHeight()));
		}

		public void paintComponent(Graphics g) {
			g.drawImage(iRd, 0, 0, this);
		}
	}

	public static void main (String[] args) {
    RdmType rdm = RdmType.solitons;
    int sizeGrid = 7; // Integer.parseInt(args[pos++]);
    // params rd
    float feed = rdm.getFK()[0], kill=rdm.getFK()[1];
    float sigma = .5f;
    float Da = 2* sigma, Db = sigma;
    double[] perturb = new double[]{ 0.5, 0.5};
    int[] perturbRD = new int[] {(int) (perturb[0] * Math.pow(2,sizeGrid)),(int) (perturb[1] * Math.pow(2,sizeGrid))};
    float[][] kernel = Utils.getKernel() ;
    int posMorpVec = 0 ; // 0 = A , 1 = B
    int [] size = new int [] {(int) Math.pow(2, sizeGrid), (int) Math.pow(2, sizeGrid )} ;

    LayerCell lc = new LayerCell("LayerCell" ,size) ;
    lc.initCells(new float[] {1.0f , 0.0f});
    lc.initPerturb(perturbRD,new float[] {0.8f,1.0f }, new float[] {0.4f,0.6f },2);
    lc.setGsParameters(feed , kill, Da, Db, kernel);


    LayerVector lvRD = new LayerVector ("lcrd", size , posMorpVec );
    lvRD.setLc (lc);
    lvRD.setKernel(kernel);
    lvRD.setParamsNormVfRd(true,new float[] {0f,.5f});

    VizVectorField vizVf = new VizVectorField(lvRD);
    VizLayerCell vizlc = new VizLayerCell(lc, 1);

    long T0 = System.currentTimeMillis(), T = 0;
    int step = 1 , stepMax = 10000;
    while (step <= stepMax) {
      if (step % 100 == 0 ) {
        T = System.currentTimeMillis() - T0;
        System.out.println("step -> " + step + " " + new Date().toString());
//        Graph sim = Simplifier.getSimplifiedGraph(g, true);
  //      System.out.println("/ nodes simGr -> " +sim.getNodeCount() + " / distr deg -> " + Arrays.toString(Toolkit.degreeDistribution(sim)));
      }

      lc.updateLayer();
      lvRD.updateLayerRD();
      vizlc.step();
      vizVf.step();
      step++;
    }
/**
  	float[][] grid = Utils.getRandomGrid(7, 0, new float[] {0f,1f});
		VizGrid viz = new VizGrid(grid);
		viz.step();
**/
	}


}
