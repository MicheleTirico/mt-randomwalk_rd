package mtirico.rdnet.framework;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class VizGrid extends JFrame {

	protected int[] sizeGrid;
	protected float[][] gridVals;
	protected BufferedImage iRd;
	protected Graphics2D gfx;
	protected JPanel canvas;

	public VizGrid(float[][] gridVals) {
		this.sizeGrid = new int[] {gridVals.length, gridVals[0].length };
		this.gridVals = gridVals;

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
			setVisible(false);
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
				iRd.setRGB(x, size - y - 1, Color.HSBtoRGB((float) gridVals[x][y]*100, 1f, 1f));
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
		float[][] grid = Utils.getRandomGrid(7, 0, new float[] {0f,1f});
		VizGrid viz = new VizGrid(grid);
		viz.step();
	}


}
