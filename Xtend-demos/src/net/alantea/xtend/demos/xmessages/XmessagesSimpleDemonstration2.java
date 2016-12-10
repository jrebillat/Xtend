package net.alantea.xtend.demos.xmessages;

import net.alantea.xmessages.XMessages;

/**
 * The Class XmessagesSimpleDemonstration2 test priority and values in separate bundles.
 */
public class XmessagesSimpleDemonstration2
{

   /**
    * The main method.
    *
    * @param args the arguments
    */
   public static void main(String[] args)
   {
      // loading the bundles, managing priority
      XMessages.addBundle("net.alantea.xtend.demos.xmessages.SimpleDemonstration1"); // default level is 0
      XMessages.addBundle("net.alantea.xtend.demos.xmessages.SimpleDemonstration2", 1); // Priority set to this one
      
      // Test overriden value
      System.out.println(XMessages.get("key.hello") + " " + XMessages.get("key.name"));
      
      // Test new value
      System.out.println(XMessages.get("key.new"));
   }

}
