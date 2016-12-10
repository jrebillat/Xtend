package net.alantea.xtend.demos.xtend;

import java.util.List;

import net.alantea.xtend.XManager;
import net.alantea.xtend.Xception;

public class XManagerSimpleDemo
{

   public static void main(String[] args)
   {
      List<ISimpleDemoObject> objects = null;
      try
      {
         objects = XManager.loadAbstractExtensions(ISimpleDemoObject.class, false);
      }
      catch (Xception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      if (objects != null)
      {
         for (ISimpleDemoObject object : objects)
         {
            System.out.println("One object said : '" + object.askMe() + "'");
         }
      }

   }

}
