package net.alantea.xtend.demos.xengine;

import net.alantea.xtend.Xception;
import xengine.XEngine;

public class XEngineDemonstration
{

   /**
    * The main method.
    *
    * @param args the arguments
    */
   public static void main(String[] args)
   {
      XEngine engine = new XEngine();
      
      engine.put("myVariable", 69);
      Double value = 0.0;
      try
      {
         value = (Double) engine.eval("myVariable = myVariable - 27;");
      }
      catch (Xception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("Returned value is : " + value);
   }


}
