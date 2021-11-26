package mtirico.rdnet.layers;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import java.util.*;
import mtirico.graphtools.graphTool.GraphTool;
import mtirico.rdnet.framework.Framework;

public class LayerNet extends Framework{

// private Parameters
  private float incVfRD = 1f,incVfRw = 1f , minLenNotMove = 0.01f ;

  private String idGraph;
	private Graph g;
  private int idNodeInt = 0, idEdgeInt= 0;

  // create and remove seeds
  private float pbAddSeed, pbRemSeed;
//  private float lenRandomVector ;
  private float deltaAng , lenMinVec;
  private Random rdAng , rdAddSeed ,rdRemSeed;

  private int posMorpAdd, posMorpRem;
// constructors

  public LayerNet(String idGraph) {
		this.idGraph = idGraph;
		g = new SingleGraph(idGraph);
	}

	public LayerNet(  Graph sourceGraph ) {
		g = sourceGraph;
	}

// set parameters
  // not used -> lenRandomVector is an old parameter

  public void setParamsRandomVector(int rdAngInt , float deltaAng) {
    rdAng = new Random(rdAngInt);
    this.deltaAng = deltaAng ;
  }

  public void setParamsSeeds(int intAddSeed , int intRemSeed, float pbAddSeed, float pbRemSeed) {
    rdAddSeed = new Random(intAddSeed);
    rdRemSeed = new Random(intRemSeed);
    this.pbAddSeed = pbAddSeed;
    this.pbRemSeed = pbRemSeed;
  }

  public void setParamsCreateRemoveSeed(int posMorpAdd, int posMorpRem , float minLenNotMove) {
    this.posMorpAdd=posMorpAdd;
    this.posMorpRem=posMorpRem;
    this.minLenNotMove = minLenNotMove;
  }

// init
// --------------------------------------------------------------------------------------------------------------------------------------------------
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
// --------------------------------------------------------------------------------------------------------------------------------------------------
  public void compute_rw (float lenRw, float incVfRD,float incVfRw, float minLenNotMove  ) {  //System.out.println(ls.getNodeWithSeeds());
    // ADD SEEDS
    Collection<Seed> seedsToRemove = new HashSet<Seed>();
    for ( Node n : g ) {
      if (n.getAttribute("seed")== null  && n.getAttribute("scale")== null) {
// float pbDeg = 1.0f / n.getDegree() ;
// float pbDeg = 1.0f / ((float) Math.pow(n.getDegree(),2));
        float pbDeg = ((n.getDegree() <= 2) ? 1.0f : 1.0f / (n.getDegree() - 2 )) ;
//        float pbDeg = ((n.getDegree() <= 1) ? 1.0f : 1.0f / (n.getDegree() -1 )) ;
        float ran = getRandom(rdAddSeed.nextInt(), new float[] {0f, 1f}) ;
        float ranVal = pbDeg * ran ;
        if (ranVal  > (1-pbAddSeed) ){ //pbAddSeed) {
// System.out.println(pbDeg);
// System.out.print(String.format("%.2f ",ran));
// System.out.println(String.format("deg = %.2f, ran = %.2f (deg = %d), ranval = %.2f  /",pbDeg,ran,n.getDegree(),ranVal));
        //  try {
            int idSeed = ls.getIdSeedInt();
            double[] nCoords =  GraphPosLengthUtils.nodePosition(n);
  //          int[] pos = new int [] {(int)Math.floor(nCoords[0]), (int)Math.floor(nCoords[1])} ;
            Vector vv = mlv.getVector(new int [] {(int)Math.floor(nCoords[0]), (int)Math.floor(nCoords[1])} ) ;
            // the new seed is located with a random direction
            double angle = rdAng.nextDouble() * Math.PI * 2;
            double mag = vv.getMag() ;
            double distx = mag * Math.cos(angle);
            double disty = mag * Math.sin(angle);
            double[] sCoords = new double[] {distx+nCoords[0], disty+nCoords[1]};
            double[] magXY = new double[]{(double)vv.getMagXY()[0],(double)vv.getMagXY()[1]}  ;
            // the seed is located with the corresponding vector
  //        double[] sCoords = new double[] {magXY[0]+nCoords[0], magXY[1]+nCoords[1]};
            Node newNode = g.addNode(Integer.toString(idNodeInt++));
            newNode.addAttribute("xyz", sCoords[0], sCoords[1], 0);//	newNode.addAttribute("ui.color", 1 );
            g.addEdge(Integer.toString(idEdgeInt++),newNode,n);
            Seed s = new Seed (idSeed++, sCoords, newNode, new ArrayList<Node>(Arrays.asList(n)), computeAng(sCoords, nCoords));
            ls.addSeed(s);
        //  } catch (Exception ex )             System.out.println(ex); }
        }
      }
    }
    // MOVE SEEDS
    for (Seed s : ls.seeds ){  //    System.out.println(s);
      float v = getRandom(rdRemSeed.nextInt(), new float[] {0f, 1f});//    System.out.println(v);
      if (v >pbRemSeed) { //        System.out.println("no rem " + s);
        ArrayList<Node> path = s.getPath(2, true);
        Node sNode = s.getNode(), pNode = path.get(path.size() - 2);
        double[] sCoords = s.getCoords(), pCoords = GraphPosLengthUtils.nodePosition(pNode);
        Collection<Edge> collEdgeNear = g.getEdgeSet();
        try { // handle if seed is out of space
          Vector vv = mlv.getVector(new int [] {(int)Math.floor(sCoords[0]), (int)Math.floor(sCoords[1])}) ;
          double[] vecRw = getRandomVector(lenRw, sCoords, pCoords);
          vecRw = new double[] {(double) incVfRw * vecRw[0] , (double) incVfRw * vecRw[1]};
          double[] magXY = new double[] {(double)vv.getMagXY()[0]*incVfRD,(double)vv.getMagXY()[1]*incVfRD};
          double[] vec = mlv.getSumMag(magXY, vecRw);
          pNode.setAttribute("ui.style", "fill-color: rgb(0,0,0);") ;
/**
          float testMag = mlv.getVectorRD(new int [] {(int)Math.floor(sCoords[0]), (int)Math.floor(sCoords[1])}).getMag();
          System.out.println(testMag + " " +Math.pow(Math.pow(vec[0],2) + Math.pow(vec[1],2), 0.5));
          Vector vecRD = mlv.getVectorRD(new int [] {(int)Math.floor(sCoords[0]), (int)Math.floor(sCoords[1])});
          vec = mlv.getSumMag(vecRD, vecRw);
**/
          if (Math.pow(Math.pow(vec[0],2) + Math.pow(vec[1],2), 0.5) > minLenNotMove ){ // param len vector
            double[] fCoords = { +vec[0] + sCoords[0]  , +vec[1] + sCoords[1]  };
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
          }/**else {  seedsToRemove.add(s); you can remove the seed if it does not been moved, better to not remove it}**/
        } catch (Exception ex) { seedsToRemove.add(s);g.removeNode(s.getNode()); } // handle seeds out limits space
      }else {  seedsToRemove.add(s); } // remove with the prob
    }
    seedsToRemove.stream().forEach(s -> ls.removeSeed(s));
  }
  public void compute_rw_test (float lenRw, float incVfRD,float incVfRw, float minLenNotMove  ) {  //System.out.println(ls.getNodeWithSeeds());
    // ADD SEEDS
    Collection<Seed> seedsToRemove = new HashSet<Seed>();
    for ( Node n : g ) {
      if (n.getAttribute("seed")== null  && n.getAttribute("scale")== null) {
        double[] nCoords =  GraphPosLengthUtils.nodePosition(n);
//        max = (a > b) ? a : b;
//       float pbDeg = 1.0f / (n.getDegree()  );
//        float pbDeg = ((n.getDegree() == 1) ? .0f : 1f / (n.getDegree() - 1 )) ;
        float pbDeg =  1.0f/ ((n.getDegree() <= 2) ? 1.0f : 1f / (n.getDegree() - 1 )) ;

        Cell c = lc.getCell( (int) nCoords[0] , (int) nCoords[1] );
        float val = c.getAB()[1];
        float ranVal = val * pbDeg  ;

        //System.out.println(pbDeg + " " + ranVal + " " + c.getAB()[0] + " " + c.getAB()[1]);
        int idSeed = ls.getIdSeedInt();
        if (  ranVal > 0.5f )  { //.985
        //  try {
            Vector vv = mlv.getVector(new int [] {(int)Math.floor(nCoords[0]), (int)Math.floor(nCoords[1])} ) ;
            // the new seed is located with a random direction
            double angle = rdAng.nextDouble() * Math.PI * 2;
            double mag = vv.getMag() ;
            double distx = mag * Math.cos(angle);
            double disty = mag * Math.sin(angle);
            double[] sCoords = new double[] {distx+nCoords[0], disty+nCoords[1]};
            double[] magXY = new double[]{(double)vv.getMagXY()[0],(double)vv.getMagXY()[1]}  ;
            // the seed is located with the corresponding vector
  //        double[] sCoords = new double[] {magXY[0]+nCoords[0], magXY[1]+nCoords[1]};
            Node newNode = g.addNode(Integer.toString(idNodeInt++));
            newNode.addAttribute("xyz", sCoords[0], sCoords[1], 0);//	newNode.addAttribute("ui.color", 1 );
            g.addEdge(Integer.toString(idEdgeInt++),newNode,n);
            Seed s = new Seed (idSeed++, sCoords, newNode, new ArrayList<Node>(Arrays.asList(n)), computeAng(sCoords, nCoords));
            ls.addSeed(s);
        //  } catch (Exception ex )             System.out.println(ex); }
        }
      }
    }
    // MOVE SEEDS
    for (Seed s : ls.seeds ){  //    System.out.println(s);
      float v = getRandom(rdRemSeed.nextInt(), new float[] {0f, 1f});//    System.out.println(v);
      ArrayList<Node> path = s.getPath(2, true);
      Node sNode = s.getNode(), pNode = path.get(path.size() - 2);
      double[] sCoords = s.getCoords(), pCoords = GraphPosLengthUtils.nodePosition(pNode);
      Collection<Edge> collEdgeNear = g.getEdgeSet();
      Cell cRem = lc.getCell( (int) sCoords[0] , (int) sCoords[1] );
      float valRem = cRem.getAB()[0];

//      if (valRem > 0.999f   ) { seedsToRemove.add(s);
      //  System.out.println(valRem);
//      if (v > pbRemSeed) { //        System.out.println("no rem " + s);
  //    }else {
        try { // handle if seed is out of space
        Vector vv = mlv.getVector(new int [] {(int)Math.floor(sCoords[0]), (int)Math.floor(sCoords[1])}) ;
        double[] vecRw = getRandomVector(lenRw, sCoords, pCoords);
        vecRw = new double[] {(double) incVfRw * vecRw[0] , (double) incVfRw * vecRw[1]};
        double[] magXY = new double[] {(double)vv.getMagXY()[0]*incVfRD,(double)vv.getMagXY()[1]*incVfRD};
        double[] vec = mlv.getSumMag(magXY, vecRw);
        pNode.setAttribute("ui.style", "fill-color: rgb(0,0,0);") ;
/**
        float testMag = mlv.getVectorRD(new int [] {(int)Math.floor(sCoords[0]), (int)Math.floor(sCoords[1])}).getMag();
        System.out.println(testMag + " " +Math.pow(Math.pow(vec[0],2) + Math.pow(vec[1],2), 0.5));
        Vector vecRD = mlv.getVectorRD(new int [] {(int)Math.floor(sCoords[0]), (int)Math.floor(sCoords[1])});
        vec = mlv.getSumMag(vecRD, vecRw);
**/
        if (Math.pow(Math.pow(vec[0],2) + Math.pow(vec[1],2), 0.5) > minLenNotMove ){ // param len vector
          double[] fCoords = { +vec[0] + sCoords[0]  , +vec[1] + sCoords[1]  };
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
        }/**else {  seedsToRemove.add(s); you can remove the seed if it does not been moved, better to not remove it}**/
      }         catch (Exception ex) { seedsToRemove.add(s);g.removeNode(s.getNode()); } // handle seeds out limits space
//  } // remove with the prob
    }
    seedsToRemove.stream().forEach(s -> ls.removeSeed(s));
  }

  private Random rdCreateShuffle = new Random(0), rdRemoveShuffle = new Random(0) ;


  public void compute() {  //System.out.println(ls.getNodeWithSeeds());
    // ADD SEEDS
    ArrayList<Node> nodeSet = new ArrayList<Node>(g.getNodeSet());
    Collections.shuffle(nodeSet, rdCreateShuffle);
    Iterator<Node> itNode = nodeSet.iterator();
    boolean testCreate = false;
    while (testCreate == false && itNode.hasNext()  ) {
      Node n = itNode.next();
      if (n.getAttribute("seed")== null  && n.getAttribute("scale")== null) {
        int idSeed = ls.getIdSeedInt();
        double[] nCoords =  GraphPosLengthUtils.nodePosition(n);
        int[] pos = new int [] {(int)Math.floor(nCoords[0]), (int)Math.floor(nCoords[1])} ;
        Vector vv = mlv.getVector(pos) ;
        // the new seed is located with a random direction
        double angle = rdAng.nextDouble() * Math.PI * 2;
        double mag = vv.getMag() ;
        double distx =mag * Math.cos(angle);
        double disty =mag * Math.sin(angle);
        double[] sCoords = new double[] {distx+nCoords[0], disty+nCoords[1]};
        double[] magXY = new double[]{(double)vv.getMagXY()[0],(double)vv.getMagXY()[1]}  ;
        Seed s = new Seed (idSeed++, sCoords, n, new ArrayList<Node>(Arrays.asList(n)), computeAng(sCoords, nCoords));
        ls.addSeed(s);
        testCreate=true;
//        count++;
      }
    }

    ArrayList<Seed> listSeeds = new ArrayList<Seed>(ls.getSeeds());
    Collections.shuffle(listSeeds, rdCreateShuffle);
  //  if (listSeeds.size() != 0 ) ls.removeSeed(listSeeds.get(0));
    Collection<Seed> seedsToRemove = new HashSet<Seed>();
    // MOVE SEEDS
    for (Seed s : ls.seeds ){  //    System.out.println(s);
      float v = getRandom(rdRemSeed.nextInt(), new float[] {0f, 1f});//    System.out.println(v);
      if (v > pbRemSeed) { //        System.out.println("no rem " + s);
        ArrayList<Node> path = s.getPath(2, true);
        Node sNode = s.getNode(), pNode = path.get(path.size() - 2);
        double[] sCoords = s.getCoords(), pCoords = GraphPosLengthUtils.nodePosition(pNode);
        Collection<Edge> collEdgeNear = g.getEdgeSet();
        int[] pos = new int [] {(int)Math.floor(sCoords[0]), (int)Math.floor(sCoords[1])} ;
        try { // handle if seed is out of space
          Vector vv = mlv.getVector(pos) ;
          double[] vecRw = getRandomVector(vv.getMag(), sCoords, pCoords);
          double[] magXY = new double[] {(double)vv.getMagXY()[0]*incVfRD,(double)vv.getMagXY()[1]*incVfRD};
          double[] vec = mlv.getSumMag(magXY, vecRw);
          if (Math.pow(Math.pow(vec[0],2) + Math.pow(vec[1],2), 0.5) > minLenNotMove ){ // param len vector
            double[] fCoords = { +vec[0] + sCoords[0]  , +vec[1] + sCoords[1]  };
            Edge ex = GraphTool.getEgeIntersecInEdgeSet(sCoords, fCoords, collEdgeNear);
            Collection<Edge> colXEdge = GraphTool.getEdgeSetIntersectWithsegment(sCoords, fCoords,collEdgeNear);
            colXEdge.add(ex);
            pNode.setAttribute("ui.style", "fill-color: rgb(0,0,0);") ;
            if (ex == null) {
              Node newNode = g.addNode(Integer.toString(idNodeInt++));
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
                  } catch (EdgeRejectedException exc) {
                    test = false ;
                    seedsToRemove.add(s);
                  }
                }
              }
            }

        }/**else {  seedsToRemove.add(s); you can remove the seed if it does not been moved, better to not remove it}**/
        } catch (java.lang.ArrayIndexOutOfBoundsException ex) { seedsToRemove.add(s);  } // handle seeds out limits space
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

  private double[] getRandomVector_test(Vector vec) {
    float mag = vec.getMag();
    float[] magXY = vec.getMagXY();
    double cos = magXY[0] / mag ;
    double
      ang = Math.acos(cos),
      newAng = ang + (rdAng.nextBoolean() == true ? 1 : -1) * deltaAng,
      radius = mag ;
      System.out.println(mag);
      return new double[] {  radius * Math.cos(newAng), radius * Math.sin(newAng) * (magXY[1] >= 0 ? 1 : -1) } ;
  	}

	private double computeAng(double[] sCoords, double[] pCoords) {
		return Math.acos((sCoords[0] - pCoords[0]) / GraphTool.getDistGeom(pCoords, sCoords));
	}

// GET AND SET METHODS
// ------------------------------------------------------------------------------------------------------------------------------
	public Graph getGraph() {	return g;	}
	public void getImage(String path, String nameFile) { g.addAttribute("ui.screenshot", path + "/" + nameFile + ".png");}

// viz graph
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
public static void main (String[] args ){System.err.println(new Object(){}.getClass().getName());;}

}
