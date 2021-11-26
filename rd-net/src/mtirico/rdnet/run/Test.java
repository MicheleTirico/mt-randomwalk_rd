/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mtirico.rdnet.run;

/**
 *
 * @author mtirico
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mtirico.rdnet.framework.Framework;
import mtirico.tools.generictools.StoreInfo;

public class Test extends Framework {    
     
    public static void main(String[] args)  throws IOException {
        
        Map<String,String> params = StoreInfo.getMapParams("src/resources/params.csv") ;
        System.out.println(params);
        

//        Graph g = new SingleGraph ("a");
//        g.display(false);
//        System.err.println(g.getId());
        
//        List<String> totLines = new ArrayList<String>();
//        File f = new File ("${data}/commonparameters.csv"); 
        

    }
      
}