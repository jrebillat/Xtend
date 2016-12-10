package net.alantea.xtend.demos.xengine;

import net.alantea.xengine.XEngine;
import net.alantea.xtend.Xception;

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
      
      // setting a variable
      engine.put("myVariable1", 69);
      System.out.println("Put value : " + engine.get("myVariable1"));
      
      // running a script, getting last operation value
      Double value1 = 0.0;
      try
      {
         value1 = (Double) engine.eval("myVariable1 = myVariable1 - 27;");
      }
      catch (Xception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("Returned value is : " + value1);
      
      // Testing working with several variables
      engine.put("myVariable2", 666);
      Double value2 = 0.0;
      try
      {
         value2 = (Double) engine.eval("myVariable2 - myVariable1;");
      }
      catch (Xception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("Returned value is : " + value2);
      
      // Changing existing variable
      try
      {
         engine.eval("myVariable2 = 999;");
      }
      catch (Xception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("Got value : " + engine.get("myVariable2"));
   }


}
