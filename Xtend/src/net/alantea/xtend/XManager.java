package net.alantea.xtend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.alantea.xtend.Xception.Why;

import org.reflections.Reflections;

/**
 * Class to manage extensions. It has the ability to manage :
 * - abstract extensions.
 *   These are simple extensions. There should be an abstract base class with one
 *   or more abstract methods. Then implementors will derive the base class,
 *   implementing the abstract methods as needed. Clients will use the methods
 *   from the instances returned by XManager.
 * - interfaced extensions (same methods as raw extensions).
 *   As it may be simpler to implement interfaces in a class than deriving classes
 *   from a base class, interfaced extensions are made  of an interface and
 *   implementors will implement the interface in their classes as needed. Clients
 *   will use the methods from the instances returned by XManager.
 * - container extensions.
 *   These extensions have a two-steps implementation : first an extension class
 *   (same as for raw or interfaced) extending the IExtension that offers some methods
 *   to clients, and an inner interface that the implementors shall implement. The
 *   extension methods will call the implemented methods themselves, hiding it from the client.
 * - remote extensions
 * 
 * It also allows for every concerned extension class to register key/value pairs of properties.
 * 
 * @author Alantea
 * 
 */
public class XManager
{	
  private static List<? extends Class<?>> extensions = new ArrayList<Class<?>>();
  
  /** Private singleton constructor. */
  private XManager()
  {
  }
  
  /**
   * Load a container extension, instantiate all implementations and notify the container.
   *
   * @param <T> the generic type
   * @param baseClass for extension to find
   * @param forcedReload to force for reflective research even if a target is found in cache
   * @return the instantiated extension
   * @throws Xception when raised
   */
  public static <T> T loadContainerExtension(Class<?> baseClass, boolean forcedReload) throws Xception
  {
    // Search for the extension itself
    List<T> list = loadExtensions(baseClass, false, forcedReload);
    if (list.isEmpty())
    {
      throw new Xception(Why.NO_EXTENSION);
    }
    if (list.size() > 1)
    {
      throw new Xception(Why.MULTIPLE_EXTENSION);
    }
    if (! (list.get(0) instanceof IExtension))
    {
      throw new Xception(Why.BAD_EXTENSION);
    }
    
    // get extension
    IExtension extend = (IExtension)list.get(0);
    
    // load implementations
    loadImplementations(extend, forcedReload);
    
    return (T)list.get(0);
  }
  
  /**
   * Given an object implementing IExtension, instantiate all implementations and notify the object.
   * @param extend extension for which to instantiate implementations
   * @param forcedReload to force for reflective research even if a target is found in cache
   * @throws Xception when raised
   */
  public static void loadImplementations(IExtension extend, boolean forcedReload) throws Xception
  {
    // instantiate implementations
    List<Object> impls = loadExtensions(extend.getExtendedInterface(), true, forcedReload);
    
    // notify extension
    for (Object impl : impls)
    {
      extend.addImplementation(impl);
    }
  }

  /**
   * Load an abstract extension, i.e. one implementation of it (the derived class)
   *
   * @param <T> the generic type
   * @param baseClass for extension to find
   * @param forcedReload to force for reflective research even if a target is found in cache
   * @return the instantiated extension
   * @throws Xception when raised
   */
  public static <T> T loadAbstractExtension(Class<?> baseClass, boolean forcedReload) throws Xception
  {
    // get implementations
    List<T> list = loadExtensions(baseClass, false, forcedReload);
    
    // verify unicity
    if (list.isEmpty())
    {
      throw new Xception(Why.NO_EXTENSION);
    }
    if (list.size() > 1)
    {
      throw new Xception(Why.MULTIPLE_EXTENSION);
    }
    return list.get(0);
  }

  /**
   * Load an abstract extension, i.e. all implementations of it (derived classes)
   *
   * @param <T> the generic type
   * @param baseClass for extension to find
   * @param forcedReload to force for reflective research even if a target is found in cache
   * @return the instantiated extension
   * @throws Xception when raised
   */
  public static <T> List<T> loadAbstractExtensions(Class<?> baseClass, boolean forcedReload)
      throws Xception
  {
    return loadExtensions(baseClass, true, forcedReload);
  }

  /**
   * Load extensions.
   *
   * @param <T> the generic type
   * @param baseClass the base class
   * @param acceptMultiple the accept multiple
   * @param forcedReload the forced reload
   * @return the list
   * @throws Xception the xception
   */
  private static <T> List<T> loadExtensions(Class<?> baseClass,
      boolean acceptMultiple, boolean forcedReload) throws Xception
  {
    ArrayList<T> ret = new ArrayList<T>();
    
    List<Class<?>> classes = new ArrayList<Class<?>>();
    if (!forcedReload)
    {
      for (Class<?> ext : extensions)
      {
        if ( baseClass.isAssignableFrom(ext) )
        {
          classes.add(ext);
        }
      }
    }

    if ((classes.isEmpty()) || forcedReload)
    {
       // list all classes derived from the base class.
       Reflections reflect = new Reflections(baseClass.getClassLoader());
       Set<?> set = reflect.getSubTypesOf(baseClass);
       if (set.isEmpty())
       {
         throw new Xception(Why.NO_EXTENSION);
       }
       for (Object o : set)
       {
         classes.add((Class<?>)o);
       }
    }

    if (!acceptMultiple)
    {
      int num = 0;
      for (Class<?> obj : classes)
      {
        if (!Modifier.isAbstract(((Class<?>) obj).getModifiers()))
        {
          num++;
          if (num > 1)
          {
            throw new Xception(Why.MULTIPLE_EXTENSION);
          }
        }
      }

    }

    // load instances.
    for (Class<?> obj : classes)
    {
      if (!Modifier.isAbstract(((Class<?>) obj).getModifiers()))
      {
        @SuppressWarnings("unchecked")
        Class<? extends T> cl = (Class<? extends T>) obj;
        try
        {
          // search constructor
          Constructor<?> constructor = (Constructor<?>) cl
              .getDeclaredConstructor();
          // create instance
          @SuppressWarnings("unchecked")
          T t = (T) constructor.newInstance();
          ret.add(t);
          try
          {
             XMessages.addAssociatedBundle(t);
          }
          catch ( Xception e)
          {
              if (e.getWhy() != Why.NO_BUNDLE)
              {
            	  throw e;
              }
          }
        }
        catch (NoSuchMethodException e)
        {
          throw new Xception(Why.BAD_CONSTRUCTOR);
        }
        catch (SecurityException e)
        {
          throw new Xception(Why.BAD_CONSTRUCTOR);
        }
        catch (InstantiationException e)
        {
          throw new Xception(Why.BAD_CONSTRUCTOR);
        }
        catch (IllegalAccessException e)
        {
          e.printStackTrace();
          throw new Xception(Why.BAD_CONSTRUCTOR);
        }
        catch (IllegalArgumentException e)
        {
          throw new Xception(Why.BAD_CONSTRUCTOR);
        }
        catch (InvocationTargetException e)
        {
          throw new Xception(Why.BAD_CONSTRUCTOR);
        }
      }
    }
    return ret;
  }
}