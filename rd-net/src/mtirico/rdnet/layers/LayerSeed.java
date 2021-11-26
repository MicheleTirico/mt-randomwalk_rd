package mtirico.rdnet.layers;

import java.util.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import mtirico.rdnet.framework.Framework;

public class LayerSeed extends Framework{

  private String id ;
  private Graph graphSeeds ;
  protected Set<Seed> seeds = new HashSet<Seed>();
  protected int idSeedInt = 0 ;
  protected Collection<Node> nodesWithSeeds = new HashSet<Node>();

  public LayerSeed (String id) {
    this.id=id;
  }

  public void removeSeed (Seed s) {
//    s.getPath().get(s.getPath().size()-1).setAttribute("ui.style", "fill-color: rgb(0,100,0);") ;
    s.getNode().setAttribute("ui.style", "fill-color: rgb(0,0,0);") ;
    seeds.remove(s);
  }

  public void addSeed (Seed s) {seeds.add(s); }
  public String getId() {		return id;	}
  public Collection getSeeds () { return seeds; }
  public int getIdSeedInt() { return idSeedInt;}
  public void setIdSeedInt (int id) { this.idSeedInt = id; }
  public Collection getNodeWithSeeds () { return nodesWithSeeds;}
  public static void main (String[] args ){System.out.println("mtirico.rdnet.layers.LayerSeed.main()");;}

}
