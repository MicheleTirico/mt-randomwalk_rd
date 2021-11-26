package mtirico.rdnet.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import mtirico.graphtools.graphTool.GraphTool;
import mtirico.rdnet.framework.Framework;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class RandomWalk extends Framework{

  // params
  private String idGraph;
  private Graph g ;
  private int [] size;
  private float lenRw, incVfRw  ;
  private int idNodeInt = 0, idEdgeInt= 0;

  // create and remove seeds
  private float pbAddSeed, pbRemSeed;
  private float deltaAng;
  private Random rdAng , rdAddSeed ,rdRemSeed;

  // costructor
  public RandomWalk(String idGraph , int sizeGrid) {
    this.idGraph = idGraph ;
    this.g = new SingleGraph(idGraph);
    size = new int [] {(int) Math.pow(2, sizeGrid), (int) Math.pow(2, sizeGrid )} ;
    g.setStrict(false);
  }

  // set Parameters
  public void setParamsVector ( int rdAngInt , float deltaAng, float lenRw,float incVfRw) {
    rdAng = new Random(rdAngInt);
    this.deltaAng = deltaAng ;
    this.lenRw = lenRw;
    this.incVfRw = incVfRw ;
  }

  public void setParamsCreateRemoveSeed(int intAddSeed , int intRemSeed, float pbAddSeed, float pbRemSeed) {
    rdAddSeed = new Random(intAddSeed);
    rdRemSeed = new Random(intRemSeed);
    this.pbAddSeed = pbAddSeed;
    this.pbRemSeed = pbRemSeed;
  }

  // init

  public void initStar(int numNodes, double[] center, double radius) {
		double angle =0.01 + 2 * Math.PI / numNodes;
		Node nodeCenter = g.addNode(Integer.toString(idNodeInt++));
		nodeCenter.addAttribute("xyz", center[0], center[1], 0);
		while (idNodeInt < numNodes + 1) {
      double coordX = center[0] + radius * Math.cos(idNodeInt * angle),	coordY = center[1] + radius * Math.sin(idNodeInt * angle);
			Node n = g.addNode(Integer.toString(idNodeInt++));
			n.addAttribute("xyz", coordX, coordY, 0);
			double[] sCoords = new double[] { coordX, coordY };
      g.addEdge(Integer.toString(idEdgeInt++), n, nodeCenter);
      int idSeed = ls.getIdSeedInt();
      Seed s = new Seed (idSeed++, sCoords, n, new ArrayList<Node>(Arrays.asList(nodeCenter)), computeAng(sCoords, center));
      ls.addSeed(s);
    }
	}

  public void initCircle (int numNodes, double[] center, double radius) {
		double angle = 0.0 + 2 * Math.PI / numNodes;
    double coordX = center[0] + radius * Math.cos(idNodeInt * angle),	coordY = center[1] + radius * Math.sin(idNodeInt * angle);
		Node nPrevious  = g.addNode(Integer.toString(idNodeInt++));
		nPrevious .addAttribute("xyz", coordX, coordY, 0);
    Node first = nPrevious ;
    int p = 0 ;
		while (p < numNodes ) {
				coordX = center[0] + radius * Math.cos(idNodeInt * angle);
				coordY = center[1] + radius * Math.sin(idNodeInt * angle);
				Node n = g.addNode(Integer.toString(idNodeInt++));
				n.addAttribute("xyz", coordX, coordY, 0);
				double[] sCoords = new double[] { coordX, coordY };
        int idSeed = ls.getIdSeedInt();
        Seed s = new Seed (idSeed++, sCoords, n, new ArrayList<Node>(Arrays.asList(nPrevious)), computeAng(sCoords, center));
        ls.addSeed(s);
				g.addEdge(Integer.toString(idEdgeInt++), n, nPrevious);
				nPrevious = n ;
        p++;
		}
		Node a = g.getNode(Integer.toString(idNodeInt-1));
		g.addEdge(Integer.toString(idEdgeInt++), first, a);
	}

  // compute
  public void compute () {  //System.out.println(ls.getNodeWithSeeds());
    // ADD SEEDS
    Collection<Seed> seedsToRemove = new HashSet<Seed>();
    for ( Node n : g ) {
      if (n.getAttribute("seed")== null  && n.getAttribute("scale")== null) {
    //    float pbDeg = 1f / n.getDegree();
      //  float ranVal = pbDeg * getRandom(rdAddSeed.nextInt(), new float[] {0f, 1f});
      //  if (ranVal  < pbAddSeed) {
      float pbDeg = ((n.getDegree() <=2) ? 1.0f : 1.0f / (n.getDegree() - 2 )) ;

      //float pbDeg = 1.0f / ((float) Math.pow(n.getDegree(),2));// n.getDegree() ; //((float) Math.pow(n.getDegree(),1));
      float ran = getRandom(rdAddSeed.nextInt(), new float[] {0f, 1f}) ;
      float ranVal = pbDeg * ran ;
      if (ranVal  > (1-pbAddSeed) ){ //pbAddSeed) {
            int idSeed = ls.getIdSeedInt();
            double[] nCoords =  GraphPosLengthUtils.nodePosition(n);
            double angle = rdAng.nextDouble() * Math.PI * 2;
            double mag = lenRw ;
            double distx = mag * Math.cos(angle);
            double disty = mag * Math.sin(angle);
            double[] sCoords = new double[] {distx+nCoords[0], disty+nCoords[1]};
            Node newNode = g.addNode(Integer.toString(idNodeInt++));
            newNode.addAttribute("xyz", sCoords[0], sCoords[1], 0);//	newNode.addAttribute("ui.color", 1 );
            g.addEdge(Integer.toString(idEdgeInt++),newNode,n);
            Seed s = new Seed (idSeed++, sCoords, newNode, new ArrayList<Node>(Arrays.asList(n)), computeAng(sCoords, nCoords));
            ls.addSeed(s);
        }
      }
    }
    // MOVE SEEDS
    for (Seed s : ls.seeds ){  //    System.out.println(s);
  //    float v = getRandom(rdRemSeed.nextInt(), new float[] {0f, 1f});//    System.out.println(v);
    //  if (v > pbRemSeed) { //        System.out.println("no rem " + s);
      float v = getRandom(rdRemSeed.nextInt(), new float[] {0f, 1f});//    System.out.println(v);
      if (v >pbRemSeed) { //        System.out.println("no rem " + s);

        ArrayList<Node> path = s.getPath(2, true);
        Node sNode = s.getNode(), pNode = path.get(path.size() - 2);
        double[] sCoords = s.getCoords(), pCoords = GraphPosLengthUtils.nodePosition(pNode);
        Collection<Edge> collEdgeNear = g.getEdgeSet();
        try { // handle if seed is out of space
          double[] vecRw = getRandomVector(lenRw, sCoords, pCoords);
          double[] vec = new double[] {(double) incVfRw * vecRw[0] , (double) incVfRw * vecRw[1]};
          pNode.setAttribute("ui.style", "fill-color: rgb(0,0,0);") ;
      //    if (Math.pow(Math.pow(vec[0],2) + Math.pow(vec[1],2), 0.5) > minLenNotMove ){ // param len vector
            double[] fCoords = { +vec[0] + sCoords[0]  , +vec[1] + sCoords[1]  };


            if (fCoords[0] > 0 && fCoords[1] > 0 && fCoords[0] < size[0] && fCoords[1] < size[1]) { // kill seed if out of the word

              Edge ex = GraphTool.getEgeIntersecInEdgeSet(sCoords, fCoords, collEdgeNear);
              Collection<Edge> colXEdge = GraphTool.getEdgeSetIntersectWithsegment(sCoords, fCoords,collEdgeNear);
              colXEdge.add(ex);
              if (ex == null) {
                Node newNode = g.addNode(Integer.toString(idNodeInt++));
                newNode.setAttribute("ui.style", "fill-color: rgb(0,0,0);") ;
                newNode.addAttribute("xyz", fCoords[0], fCoords[1], 0);//	newNode.addAttribute("ui.color", 1 );
                g.addEdge(Integer.toString(idEdgeInt++), newNode, sNode );//e.addAttribute( "ui.style", "fill-color: rgb(255,0,0);");
                s.setNode(newNode);
              } else {
                Iterator<Edge> itEd = colXEdge.iterator();
                boolean test = false;
                while (test == false && itEd.hasNext()) {
                  ex = itEd.next();
                  Node n0 = ex.getNode0(), n1 = ex.getOpposite(n0);
                  double[] n0Coords = GraphPosLengthUtils.nodePosition(n0), n1Coords = GraphPosLengthUtils.nodePosition(n1);
                  double[] intersection = GraphTool.getCoordIntersectionLine(n0Coords, n1Coords, sCoords, fCoords);
                  if (GraphTool.getEgeIntersecInEdgeSet(intersection, sCoords, collEdgeNear) == null) {
                    Node interNode = g.addNode(Integer.toString(idNodeInt++));
                    interNode.addAttribute("hasSeed", false); // interNode.addAttribute("ui.color", 1 );
                    interNode.setAttribute("xyz", intersection[0], intersection[1], 0);
                    try {
                      g.addEdge(Integer.toString(idEdgeInt++), sNode, interNode );
                      g.addEdge(Integer.toString(idEdgeInt++), n0, interNode);
                      g.addEdge(Integer.toString(idEdgeInt++), n1, interNode);
                      g.removeEdge(ex);									//	 e0.addAttribute("ui.style", "fill-color: rgb(255,0,0);");e1.addAttribute("ui.style", "fill-color: rgb(255,0,0);");	 e2.addAttribute("ui.style", "fill-color: rgb(255,0,0);");
                      test = true ;
                      sNode.setAttribute("ui.style", "fill-color: rgb(0,0,0);") ;
                      seedsToRemove.add(s);
                    } catch (Exception exc) {
                      test = false ;
                      seedsToRemove.add(s);
                    }
                  }
                }
              }
          } else { seedsToRemove.add(s);}
        //  }/**else {  seedsToRemove.add(s); you can remove the seed if it does not been moved, better to not remove it}**/
        } catch (Exception ex) { seedsToRemove.add(s);g.removeNode(s.getNode()); } // handle seeds out limits space
      }else {  seedsToRemove.add(s); } // remove with the prob
    }
    seedsToRemove.stream().forEach(s -> ls.removeSeed(s));
  }

  private static float getRandom(int seed, float[] range) {
    Random rd = new Random (seed);
    return range[0] + rd.nextFloat() * (range[1] - range[0]);
  }

  private double[] getRandomVector(double vec, double[] sCoords, double[] pCoords) {
    double dist = GraphTool.getDistGeom(pCoords, sCoords),
      cos = (sCoords[0] - pCoords[0]) / dist,
  		ang = Math.acos(cos),
  		newAng = ang + (rdAng.nextBoolean() == true ? 1 : -1) * deltaAng,
  		radius = (float) vec; // lenRandomVector ; // extLen[0] + (extLen[1] - extLen[0]) * rdLen.nextDouble();
		return new double[] {  radius * Math.cos(newAng), radius * Math.sin(newAng) * (sCoords[1] - pCoords[1] >= 0 ? 1 : -1) } ;
	}

  // get, set, viz
  public Graph getGraph ( ) { return g; }
  public void createSquare (boolean run , double XYmax , double XYmin ) {
		if ( run ) {
			Node n00 = g.addNode("b00");
			n00.addAttribute("xyz", XYmin , XYmin, 0 );
			n00.setAttribute("scale", true);
			Node n10 = g.addNode("b10");
			n10.addAttribute("xyz", XYmax , XYmin , 0 );
			n10.setAttribute("scale", true);

      Node n01 = g.addNode("b01");
			n01.addAttribute("xyz", XYmin , XYmax , 0 );
			n01.setAttribute("scale", true);

      Node n11 = g.addNode("b11");
			n11.addAttribute("xyz", XYmax , XYmax , 0 );
			n11.setAttribute("scale", true);

			g.addEdge("bord0", n00, n10) ;
			g.addEdge("bord1", n00, n01) ;
			g.addEdge("bord2", n01, n11) ;
			g.addEdge("bord3", n10, n11) ;
		}
	}

  // private
  private double computeAng(double[] sCoords, double[] pCoords) {
		return Math.acos((sCoords[0] - pCoords[0]) / GraphTool.getDistGeom(pCoords, sCoords));
	}




public static void main (String[] args ){System.out.println("mtirico.rdnet.layers.Cell.main()");}

}
