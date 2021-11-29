/*
* explore feed and kill parameters with OpenMOLE. Run the class run with the jar to show results.
* parameters are in the /params.csv. Set the path in the args, otherwise the program find the file params.csv in the folder of the jar
* the program create a folder /data where it put results
* libraries are in the lib/ folder to put at the same lavel of the jar
*/
package mtirico.rdnet.run;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkDGS;

import mtirico.graphtools.simplifyNetwork.Simplifier;
import mtirico.rdnet.framework.Framework;
import mtirico.rdnet.framework.Utils;
import mtirico.rdnet.framework.VizLayerCell;
import mtirico.rdnet.layers.*;
import mtirico.tools.generictools.StoreInfo;
import mtirico.tools.handleFile.HandleFolder;

/**
 *
 * @author mtirico
 */
public class ExploreFk extends Framework {
    private static Map<String,String> params; 
    private static float [][] kernel ; 
    private static double feed, kill, pr , pc, Da , Db, perturbA , perturbB, initB , initA, normVf_min, lenRw, normVf_max, deltaAng, minLenNotMove, incVfRw, incVfRd; 
    private static int sizeGrid, stepMax,nb, posMorpVec, rdRem,rdAng, rdAdd, radiusCircleG, radiusPerturbRD ;
    private static boolean viz, store, outputTerminal;
    private static String pathStore ;
    private static int [] size , coreG ;
    private static String p1_name = "feed", p2_name = "kill";
    
    public static void main (String[] args) throws IOException {
        String path ;
        try { path = args[0];} 
        catch (Exception ArrayIndexOutOfBoundsException ) { path  = "src/resources/params.csv" ;}
        
        params = StoreInfo.getMapParams(path) ;
        if (outputTerminal) System.out.println("params: "+params);  
        kernel = Utils.getKernel () ;
        pr = Double.parseDouble(params.get("pr")) ;
        pc = Double.parseDouble(params.get("pc"));
        Da = Double.parseDouble(params.get("Da")) ;
        Db = Double.parseDouble(params.get("Db")) ;
        perturbA = Double.parseDouble(params.get("perturbA")) ;
        perturbB= Double.parseDouble(params.get("perturbB"));
        initB =  Double.parseDouble(params.get("initB"));
        initA= Double.parseDouble(params.get("initA"));
        normVf_min= Double.parseDouble(params.get("normVf_min")) ;
        lenRw= Double.parseDouble(params.get("lenRw")) ;
        normVf_max =  Double.parseDouble(params.get("normVf_max")) ;
        deltaAng= Double.parseDouble(params.get("deltaAng")) ;
        minLenNotMove = Double.parseDouble(params.get("minLenNotMove")) ;
        incVfRw= Double.parseDouble(params.get("incVfRw"));
        incVfRd= Double.parseDouble(params.get("incVfRd")) ;
        sizeGrid=Integer.parseInt(params.get("sizeGrid")) ; 
        stepMax=Integer.parseInt(params.get("stepMax")) ; 
        nb=Integer.parseInt(params.get("nb")) ;
        posMorpVec=Integer.parseInt(params.get("posMorpVec")) ;
        rdAdd=Integer.parseInt(params.get("rdAdd")) ;
        rdRem=Integer.parseInt(params.get("rdRem")) ;  
        rdAng=Integer.parseInt(params.get("rdAng")) ;
        radiusCircleG=Integer.parseInt(params.get("radiusCircleG"));
        radiusPerturbRD=Integer.parseInt(params.get("radiusPerturbRD")) ;
        viz=Boolean.parseBoolean(params.get("viz")) ;
        store=Boolean.parseBoolean(params.get("store"));
        pathStore = params.get("store");
        size = new int [] {(int) Math.pow(2, sizeGrid), (int) Math.pow(2, sizeGrid )} ;
        coreG = size ;
        outputTerminal = Boolean.parseBoolean(params.get("outputTerminal"));
        
        double f=-1, k=-1;
        run(f, k );
    }   
    
    public static void run (double p1, double p2 ) throws IOException {        
        if (feed < 0) p1 = Double.parseDouble(params.get(p1_name)) ;
        if (kill < 0) p2 = Double.parseDouble(params.get(p2_name)) ;
      
        if (outputTerminal) System.out.println("start sim at " + new Date().toString() );
       
        // layer lc
        lc = new LayerCell("LayerCell", size);
        lc.initCells(new float[] {1.0f , 0.0f});
        lc.initPerturb(new int[] {(int) size[0]/2,(int) size[1]/2},new float[] {1.0f , 0.5f},1);
        lc.setGsParameters((float) feed , (float) kill, (float) Da, (float) Db, kernel);

        // layer RD
        lvRD = new LayerVector ("lcrd", size , posMorpVec );
        lvRD.setLc (lc);
        lvRD.setKernel(kernel);
        lvRD.setParamsNormVfRd(true,new float[] {0f,.1f});

        // layer vf
        lcVfStat = new LayerCell("LayerStat", size);  // todo -> methods to create orography
        lvStat = new LayerVector("lvStat0", size);
        lvStat.initStatic(new float[] {-.00f,.00f}); // test a static vf, each v are similar

        // MultiLayerVector
        mlv = new MultiLayerVector ("mlv",size, lvRD, lvStat) ;

        // layerSeed
        ls = new LayerSeed("ls") ;

        // layer net
        ln = new LayerNet ("ln");
        ln.initCircle(nb, new double[] { (double) coreG[0]/2, (double) coreG[1]/2 }, 0.5);
        ln.setParamsRandomVector(rdAng, (float) deltaAng);
        ln.setParamsSeeds(rdAdd,rdRem,(float) pc,(float) pr);
        ln.setParamsCreateRemoveSeed(0,0,0.01f);
        Graph g = ln.getGraph();
        VizLayerCell vizlc = null; 

        // viz
        if (viz) {
          vizlc = new VizLayerCell(lc, 1);
          vizlc.step();
          ln.createSquare(true, size[0], 0);
          g.display(false);
        }

        // sim
        long T0 = System.currentTimeMillis(), T = 0;
        int step = 0;
        while (step < stepMax) {
            if (step % 100 == 0 ) {
                T = System.currentTimeMillis() - T0;
                if (outputTerminal) System.out.print("step -> " + step + " " + new Date().toString() + " speed -> " + step / (T / 1000f) + " step/s , seeds -> " + ls.getSeeds().size() + " / nodes -> "+g.getNodeCount());
                Graph sim = Simplifier.getSimplifiedGraph(g, true);
                if (outputTerminal) System.out.println("/ nodes simGr -> " +sim.getNodeCount() + " / distr deg -> " + Arrays.toString(Toolkit.degreeDistribution(sim)));
            }

            lc.updateLayer();
            mlv.updateLayers();
            ln.compute();
            if (viz) vizlc.step();
            step++;
        }
        if (store) {
            FileSinkDGS fsd = new FileSinkDGS() ;
            g.addSink(fsd);
            String pathStoreComplete = pathStore +"_"+p1_name+"_"+p2_name+"_"+String.format("%.5f",p1) + "-"+String.format("%.5f",p2)+ ".dgs" ; 
            HandleFolder.removeFileIfExist(pathStoreComplete);
            g.write(fsd, pathStoreComplete);
        }
        g.clear();
        if (outputTerminal) System.out.println("end sim at " + new Date().toString() );
    }
}
