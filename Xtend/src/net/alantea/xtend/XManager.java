package net.alantea.xtend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.alantea.xtend.Xception.Why;

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
   * Load a list of container extensions, instantiate all implementations and notify the container.
   *
   * @param <T> the generic type
   * @param baseClass for extension to find
   * @param acceptMultiple the accept multiple
   * @param forcedReload to force for reflective research even if a target is found in cache
   * @return the instantiated extension
   * @throws Xception when raised
   */
  public static <T> List<T> loadContainerExtensions(Class<?> baseClass, boolean acceptMultiple,
		  boolean forcedReload) throws Xception
  {
    // Search for the extensions
    List<T> list = loadExtensions(baseClass, acceptMultiple, forcedReload);
    if (list.isEmpty())
    {
      throw new Xception(Why.NO_EXTENSION);
    }
    
    for (T ext : list)
    {
        if (! (ext instanceof IExtension))
        {
          throw new Xception(Why.BAD_EXTENSION);
        }
        // load implementations
        loadImplementations((IExtension)ext, forcedReload);	
    }
    
    return list;
  }
  
  /**
   * Given an object implementing IExtension, instantiate all implementations and notify the object.
   * @param extend extension for which to instantiate implementations
   * @param forcedReload to force for reflective research even if a target is found in cache
   * @throws Xception when raised
   */
  private static void loadImplementations(IExtension extend, boolean forcedReload) throws Xception
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
   * Gets the extension class for a base class.
   *
   * @param baseClass the base class
   * @return the extension class
   * @throws Xception the xception
   */
  public static Class<?> getExtensionClass(Class<?> baseClass) throws Xception
  {
     List<Class<?>> classes = loadExtensionClasses(baseClass,false, false);
     return classes.get(0);
  }

  private static List<Class<?>> loadExtensionClasses(Class<?> baseClass,
      boolean acceptMultiple, boolean forcedReload) throws Xception
  {
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
        List<String> set;
        if (baseClass.isInterface())
          {
              set = new FastClasspathScanner().scan().getNamesOfClassesImplementing(baseClass);
          }
        else
        {
             set = new FastClasspathScanner().scan().getNamesOfSubclassesOf(baseClass);
        }

        for (String o : set)
        {
           Class<?> cl;
           try
           {
              cl = ClassLoader.getSystemClassLoader().loadClass(o);
           }
           catch (ClassNotFoundException e)
           {
              throw new Xception(Why.BAD_EXTENSION);
           }
           if (!Modifier.isAbstract(cl.getModifiers()))
           {
              classes.add(cl);
           }
        }
     }
     if (classes.isEmpty())
     {
        throw new Xception(Why.NO_EXTENSION);
     }

     if ((!acceptMultiple) && (classes.size() > 1))
     {
        throw new Xception(Why.MULTIPLE_EXTENSION);
     }
     
     return classes;
  }

  private static <T> List<T> loadExtensions(Class<?> baseClass,
      boolean acceptMultiple, boolean forcedReload) throws Xception
  {
     ArrayList<T> ret = new ArrayList<T>();
     
     List<Class<?>> classes = loadExtensionClasses( baseClass, acceptMultiple, forcedReload);

    // load instances.
    for (Class<?> obj : classes)
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
          XMessages.addAssociatedBundle(t);
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException 
           | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
          throw new Xception(Why.BAD_CONSTRUCTOR);
        }
    }
    return ret;
  }
}