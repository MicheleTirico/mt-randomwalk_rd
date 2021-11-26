package mtirico.rdnet.framework;

import mtirico.rdnet.layers.*;

public class Framework {
  public enum typeStatic {test}

  public static LayerCell lc ;
  public static  LayerVector lvRD ;

  // lv
  public static   LayerCell lcVfStat ;  // todo -> methods to create orography
  public static   LayerVector lvStat  ;

  // MultiLayerVector
  public  static   MultiLayerVector mlv ;

  // layerSeed
  public   static LayerSeed ls  ;

  // layer net
  public   static  LayerNet ln ;

}
