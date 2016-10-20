package net.alantea.xmessages;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.TreeMap;

import net.alantea.xtend.Xception;

/**
 * Class to manage messages from extensions.
 * 
 * @author Alantea
 * 
 */
public class XMessages
{
  
  /** The bundles. */
  private static Map<XMessagesKey, ResourceBundle> bundles = new TreeMap<XMessagesKey, ResourceBundle>();

  /** The locale. */
  private static Locale locale = Locale.getDefault();

  /** Private singleton constructor. */
  private XMessages()
  {
  }

  /**
   * Adds the associated bundle.
   *
   * @param object the object
   * @return true, if successful
   * @throws Xception the xception
   */
  public static boolean addAssociatedBundle(Object object) throws Xception
  {
     return addAssociatedBundle(object, 0);
  }

  /**
   * Adds the associated bundle.
   *
   * @param object the object
   * @param level the association level (the lowest priority is the higher value).
   * @return true, if successful
   * @throws Xception the xception
   */
  public static boolean addAssociatedBundle(Object object, int level) throws Xception
  {
    try
    {
      return (manageBundle(object, level, true) != null);
    }
    catch (MissingResourceException e)
    {
        return false;
    }
  }

  /**
   * Gets the bundle.
   *
   * @param object the object
   * @return the bundle
   * @throws MissingResourceException the missing resource exception
   */
  public static ResourceBundle getBundle(Object object)
      throws MissingResourceException
  {
    return getBundle(object, 0);
  }

  /**
   * Gets the bundle.
   *
   * @param object the object
   * @param level the association level (the lowest priority is the higher value).
   * @return the bundle
   * @throws MissingResourceException the missing resource exception
   */
  public static ResourceBundle getBundle(Object object, int level)
      throws MissingResourceException
  {
    return manageBundle(object, level, false);
  }

  /**
   * Manage bundle.
   *
   * @param object the object
   * @param level the association level (the lowest priority is the higher value).
   * @param storeIt the store it
   * @return the resource bundle
   * @throws MissingResourceException the missing resource exception
   */
  private static ResourceBundle manageBundle(Object object, int level, boolean storeIt)
      throws MissingResourceException
  {
    String name = object.getClass().getName();
    if (object instanceof String)
    {
       name = (String) object;
    }
    ResourceBundle bundle = null;

    bundle = bundles.get(new XMessagesKey(name, -1));
    if (bundle == null)
    {
      bundle = ResourceBundle.getBundle(name, locale, new UTF8Control());
      if (storeIt)
      {
         bundles.put(new XMessagesKey(name, level), bundle);
      }
    }
    return bundle;
  }

  /**
   * Manage bundle.
   *
   * @param name the bundle name
   * @return the resource bundle
   */
  public static boolean addBundle(String name)
  {
     return addBundle(name, 0);
  }

  /**
   * Manage bundle.
   *
   * @param name the bundle name
   * @param level the association level (the lowest priority is the higher value).
   * @return the resource bundle
   */
  public static boolean addBundle(String name, int level)
  {
     try
     {
        ResourceBundle bundle = null;

        bundle = bundles.get(new XMessagesKey(name, -1));
        if (bundle == null)
        {
          bundle = ResourceBundle.getBundle(name, locale, new UTF8Control());
          bundles.put(new XMessagesKey(name, level), bundle);
        }
        return (bundle != null);
     }
     catch (MissingResourceException e)
     {
         return false;
     }
  }

  /**
   * Get a value as int from its key.
   * 
   * @param key to search
   * @return the int value or 0 if nothing is found
   */
  public static int getInteger(String key)
  {
    int ret = 0;
    String sVal = get(key);
    try
    {
       ret = Integer.parseInt(sVal);
    }
    catch (NumberFormatException e)
    {
      // nothing
    }
    
    return ret;
  }

  /**
   * Get a value as double from its key.
   * 
   * @param key to search
   * @return the double value or 0.0 if nothing is found
   */
  public static double getDouble(String key)
  {
    double ret = 0;
    String sVal = get(key);
    try
    {
       ret = Double.parseDouble(sVal);
    }
    catch (NumberFormatException e)
    {
      // nothing
    }
    
    return ret;
  }

  /**
   * Get a message from its key or return null.
   *
   * @param key to search
   * @param args the args
   * @return the message value or the key if nothing is found
   */
  public static String getOrNull(String key, String... args)
  {
     String ret = get(key, args);
     if (ret.equals(key))
     {
        ret = null;
     }
     return ret;
  }

  /**
   * Get a message from its key.
   *
   * @param key to search
   * @param args the args
   * @return the message value or the key if nothing is found
   */
  public static String get(String key, String... args)
  {
    String ret = key;

    // search in registered bundles
    if ((ret == null) || (ret.equals(key)))
    {
        for (ResourceBundle bundle : bundles.values())
        {
          if ((ret == null) || (ret.equals(key)))
          {
             try
             {
               ret = bundle.getString(key);
             }
             catch (MissingResourceException e)
             {
               ret = key;
             }
          }
        }
    }

    // replace arguments
    for (int i = 0; i < args.length; i++)
    {
      String arg = args[i];
      if (arg == null)
      {
        arg = "'null'";
      }
      ret = ret.replaceAll("\\[" + (i + 1) + "\\]", arg);
    }
    return ret;
  }
  
  /**
   * The Class XMessagesKey.
   */
  static class XMessagesKey implements Comparable<Object>
  {
   
   /** The name. */
   String name;
     
     /** The level. */
     int level;
     
     /**
      * Instantiates a new key.
      *
      * @param name the name
      * @param level the level
      */
     public XMessagesKey(String name, int level)
   {
      this.name = name;
      this.level = level;
   }

     /**
      * Compare to.
      *
      * @param object the object
      * @return the int
      */
     @Override
     public int compareTo(Object object)
     {
        if (!(object instanceof XMessagesKey))
        {
           return -1;
        }
        
        XMessagesKey other = (XMessagesKey) object;
        if ((other.level == level) || (other.level == -1 || (level == -1)))
        {
           return other.name.compareTo(name);
        }
        else if (other.level > level)
        {
           return -1;
        }
        else
        {
           return 1;
        }
     }
  }
}

/**
 * Control for getting bundle from an UTF-8 file. This work is got from the
 * article "How to use UTF-8 in resource properties with ResourceBundle" found
 * on internet at :
 * http://stackoverflow.com/questions/4659929/how-to-use-utf-8-in
 * -resource-properties-with-resourcebundle
 *
 */
class UTF8Control extends Control
{
  /**
   * Bundle creation.
   * 
   * @throws IllegalAccessException
   *           when raised.
   * @throws InstantiationException
   *           when raised.
   * @throws IOException
   *           when raised.
   */
  public ResourceBundle new1Bundle(String baseName, Locale locale,
      String format, ClassLoader loader, boolean reload)
      throws IllegalAccessException, InstantiationException, IOException
  {
    // The below is a copy of the default implementation.
    String bundleName = toBundleName(baseName, locale);
    String resourceName = "/" + toResourceName(bundleName, "properties");
    ResourceBundle bundle = null;
    InputStream stream = null;

    stream = XMessages.class.getResourceAsStream(resourceName);

    if (stream == null)
    {
      resourceName = "/" + toResourceName(baseName, "properties");
      stream = XMessages.class.getResourceAsStream(resourceName);

    }

    if (stream != null)
    {
      try
      {
        // Only this line is changed to make it to read properties files as
        // UTF-8.
        bundle = new PropertyResourceBundle(new InputStreamReader(stream,
            "UTF-8"));
      }
      finally
      {
        stream.close();
      }
    }
    return bundle;
  }
}
