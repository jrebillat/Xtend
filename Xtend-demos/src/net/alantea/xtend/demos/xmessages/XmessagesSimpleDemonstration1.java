package net.alantea.xtend.demos.xmessages;

import net.alantea.xmessages.XMessages;

/**
 * The Class XmessagesSimpleDemonstration1 demonstrates basic use of XMessages.
 */
public class XmessagesSimpleDemonstration1
{

   /**
    * The main method.
    *
    * @param args the arguments
    */
   public static void main(String[] args)
   {
      // loading the bundle
      XMessages.addBundle("net.alantea.xtend.demos.xmessages.SimpleDemonstration1");
      
      // Simple output
      System.out.println(XMessages.get("key.hiall"));
      
      // Composition of values
      System.out.println(XMessages.get("key.hello") + " " + XMessages.get("key.name"));
      
      // Calling with parameters
      System.out.println(XMessages.get("key.hellowithparams", "girls", "boys"));
      
      // Calling with keyed parameters
      System.out.println(XMessages.get("key.hellowithkeys", "key.girls", "key.boys"));
   }

}
