package net.alantea.xtend.demos.xmessages;

import net.alantea.xmessages.XMessages;
import net.alantea.xtend.Xception;

/**
 * The Class XmessagesSimpleDemonstration3 test associated bundles.
 */
public class XmessagesSimpleDemonstration3
{

   /**
    * The main method.
    *
    * @param args the arguments
    */
   public static void main(String[] args)
   {
      // loading the bundles, using association with an object
      try
      {
         XMessages.addAssociatedBundle(new XmessagesSimpleDemonstration3Object());
      }
      catch (Xception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } 
      
      // Test overriden value
      System.out.println(XMessages.get("key.test"));
   }

}
