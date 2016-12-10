package net.alantea.xengine;

import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.alantea.xtend.Xception;

/**
 * The Class XEngine is used to simplify access to Javascript commands.
 */
public class XEngine
{

   /** The nashorn engine. */
   private ScriptEngine nashornEngine;

   /**
    * Instantiates a new engine.
    */
   public XEngine()
   {
      ScriptEngineManager scriptManager = new ScriptEngineManager();
      nashornEngine = scriptManager.getEngineByName("nashorn");
   }

   /**
    * Evaluate a command line.
    *
    * @param cmd the command line
    * @return the returned object
    * @throws Xception the exception
    */
   public Object eval(String cmd) throws Xception
   {
      try
      {
         return nashornEngine.eval(cmd);
      }
      catch (ScriptException e)
      {
         throw new Xception("error evaluating script", e);
      }
   }

   /**
    * Evaluate a stream.
    *
    * @param stream the stream
    * @return the returned object
    * @throws Xception the exception
    */
   public Object eval(Reader stream) throws Xception
   {
      try
      {
         return nashornEngine.eval(stream);
      }
      catch (ScriptException e)
      {
         throw new Xception("error evaluating script file", e);
      }
   }

   /**
    * Put a variable.
    *
    * @param name the variable name
    * @param object the content object
    */
   public void put(String name, Object object)
   {
      nashornEngine.put(name, object);
   }

   /**
    * Gets the variable.
    *
    * @param name the variable name
    * @return the returned object
    */
   public Object get(String name)
   {
      return nashornEngine.get(name);
   }
}
