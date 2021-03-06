package net.alantea.xtend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.alantea.tools.scan.Scanner;
import net.alantea.xmessages.XMessages;
import net.alantea.xtend.Xception.Why;

/**
 * Class to manage extensions. It has the ability to manage : - abstract extensions. These are
 * simple extensions. There should be an abstract base class with one or more abstract methods. Then
 * implementors will derive the base class, implementing the abstract methods as needed. Clients
 * will use the methods from the instances returned by XManager. - interfaced extensions (same
 * methods as raw extensions). As it may be simpler to implement interfaces in a class than deriving
 * classes from a base class, interfaced extensions are made of an interface and implementors will
 * implement the interface in their classes as needed. Clients will use the methods from the
 * instances returned by XManager. - container extensions. These extensions have a two-steps
 * implementation : first an extension class (same as for raw or interfaced) extending the
 * IExtension that offers some methods to clients, and an inner interface that the implementors
 * shall implement. The extension methods will call the implemented methods themselves, hiding it
 * from the client. - remote extensions
 * 
 * It also allows for every concerned extension class to register key/value pairs of properties.
 * 
 * @author Alantea
 * 
 */
public class XManager
{
   
   /** The extensions. */
   private static List<? extends Class<?>> extensions = new ArrayList<Class<?>>();

   /** The instances. */
   private static Map<String, Object> instances = new HashMap<>();

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
    * @param args the arguments, passed to extensions
    * @return the instantiated extension
    * @throws Xception when raised
    */
   public static <T> T loadContainerExtension(Class<?> baseClass, boolean forcedReload, Object... args) throws Xception
   {
      // Search for the extension itself
      List<T> list = loadExtensions(baseClass, false, forcedReload, args);
      if (list.isEmpty())
      {
         throw new Xception(Why.NO_EXTENSION);
      }
      if (list.size() > 1)
      {
         throw new Xception(Why.MULTIPLE_EXTENSION);
      }
      if (!(list.get(0) instanceof IExtension))
      {
         throw new Xception(Why.BAD_EXTENSION);
      }

      // get extension
      IExtension extend = (IExtension) list.get(0);

      // load implementations
      loadImplementations(extend, forcedReload);

      return (T) list.get(0);
   }

   /**
    * Load a list of container extensions, instantiate all implementations and notify the container.
    *
    * @param <T> the generic type
    * @param baseClass for extension to find
    * @param acceptMultiple the accept multiple
    * @param forcedReload to force for reflective research even if a target is found in cache
    * @param args the arguments, passed to extensions
    * @return the instantiated extension
    * @throws Xception when raised
    */
   public static <T> List<T> loadContainerExtensions(Class<?> baseClass, boolean acceptMultiple, boolean forcedReload,
         Object... args) throws Xception
   {
      // Search for the extensions
      List<T> list = loadExtensions(baseClass, acceptMultiple, forcedReload, args);
      if (list.isEmpty())
      {
         throw new Xception(Why.NO_EXTENSION);
      }

      for (T ext : list)
      {
         if (!(ext instanceof IExtension))
         {
            throw new Xception(Why.BAD_EXTENSION);
         }
         // load implementations
         loadImplementations((IExtension) ext, forcedReload);
      }

      return list;
   }

   /**
    * Given an object implementing IExtension, instantiate all implementations and notify the
    * object.
    *
    * @param extend extension for which to instantiate implementations
    * @param forcedReload to force for reflective research even if a target is found in cache
    * @param args the args
    * @throws Xception when raised
    */
   private static void loadImplementations(IExtension extend, boolean forcedReload, Object... args) throws Xception
   {
      // instantiate implementations
      List<Object> impls = loadExtensions(extend.getExtendedInterface(), true, forcedReload, args);

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
    * @param args the arguments, passed to extensions
    * @return the instantiated extension
    * @throws Xception when raised
    */
   public static <T> T loadAbstractExtension(Class<?> baseClass, boolean forcedReload, Object... args) throws Xception
   {
      // get implementations
      List<T> list = loadExtensions(baseClass, false, forcedReload, args);

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
    * Load an abstract extension, i.e. one specific implementation of it (a derived class)
    *
    * @param <T> the generic type
    * @param implementationClass for extension to find
    * @param forcedReload to force for reflective research even if a target is found in cache
    * @param args the arguments, passed to extensions
    * @return the instantiated extension
    * @throws Xception when raised
    */
   @SuppressWarnings("unchecked")
   public static <T> T loadSpecificAbstractExtension(Class<?> implementationClass, boolean forcedReload, Object... args)
         throws Xception
   {
      // get implementations
      try
      {
         return (T) loadSpecificExtension(implementationClass, args);
      }
      catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e)
      {
         throw new Xception(Why.NO_EXTENSION);
      }
   }

   /**
    * Load an abstract extension, i.e. all implementations of it (derived classes)
    *
    * @param <T> the generic type
    * @param baseClass for extension to find
    * @param forcedReload to force for reflective research even if a target is found in cache
    * @param args the arguments, passed to extensions
    * @return the instantiated extension
    * @throws Xception when raised
    */
   public static <T> List<T> loadAbstractExtensions(Class<?> baseClass, boolean forcedReload, Object... args)
         throws Xception
   {
      return loadExtensions(baseClass, true, forcedReload, args);
   }

   /**
    * Gets the extension class for a base class.
    *
    * @param <T> the generic type
    * @param baseClass the base class
    * @return the extension class
    * @throws Xception the exception
    */
   @SuppressWarnings("unchecked")
   public static <T> Class<T> getExtensionClass(Class<T> baseClass) throws Xception
   {
      List<Class<?>> classes = loadExtensionClasses(baseClass, false, false);
      return (Class<T>) classes.get(0);
   }

   /**
    * Gets the named instance of something.
    *
    * @param <T> the generic type
    * @param reference the reference
    * @return single instance found or null
    */
   @SuppressWarnings("unchecked")
   public static <T> T getInstance(String reference)
   {
      return (T) instances.get(reference);
   }

   /**
    * Sets a named instance of something.
    *
    * @param reference the reference
    * @param instance the instance to save
    */
   public static void setInstance(String reference, Object instance)
   {
      instances.put(reference, instance);
   }

   /**
    * Load an instance of something with Xtend mechanism.
    *
    * @param <T> the generic type
    * @param reference the reference
    * @param cl the base class use as extension pattern
    * @param args the arguments to give to instance for creation
    * @return the loaded instance
    * @throws Xception if something went wrong
    */
   @SuppressWarnings("unchecked")
   public static <T> T loadInstance(String reference, Class<?> cl, Object... args) throws Xception
   {
         Object object = XManager.loadAbstractExtension(cl, false, args);
         if ((reference != null) && (object != null))
         {
            instances.put(reference, object);
         }
         return (T) object;
   }

   /**
    * Load extension classes.
    *
    * @param <T> the generic type
    * @param baseClass the base class
    * @param acceptMultiple the accept multiple
    * @param forcedReload the forced reload
    * @return the list
    * @throws Xception the xception
    */
   @SuppressWarnings("unchecked")
   private static <T> List<Class<?>> loadExtensionClasses(Class<T> baseClass, boolean acceptMultiple,
         boolean forcedReload) throws Xception
   {
      List<Class<?>> classes = new ArrayList<Class<?>>();
      if (!forcedReload)
      {
         for (Class<?> ext : extensions)
         {
            if (baseClass.isAssignableFrom(ext))
            {
               classes.add((Class<T>) ext);
            }
         }
      }

      if ((classes.isEmpty()) || forcedReload)
      {
         // list all classes derived from the base class.
         List<String> set;
         if (baseClass.isInterface())
         {
            set = Scanner.getNamesOfClassesImplementing(baseClass);
         }
         else
         {
            set = Scanner.getNamesOfSubclassesOf(baseClass);
         }

         for (String o : set)
         {
            Class<T> cl;
            try
            {
               cl = (Class<T>) ClassLoader.getSystemClassLoader().loadClass(o);
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

   /**
    * Load extensions.
    *
    * @param <T> the generic type
    * @param baseClass the base class
    * @param acceptMultiple the accept multiple
    * @param forcedReload the forced reload
    * @param args the args
    * @return the list
    * @throws Xception the xception
    */
   private static <T> List<T> loadExtensions(Class<?> baseClass, boolean acceptMultiple, boolean forcedReload,
         Object... args) throws Xception
   {
      ArrayList<T> ret = new ArrayList<T>();

      List<Class<?>> classes = loadExtensionClasses(baseClass, acceptMultiple, forcedReload);

      // load instances.
      for (Class<?> obj : classes)
      {
         @SuppressWarnings("unchecked")
         Class<? extends T> cl = (Class<? extends T>) obj;
         try
         {
            // create instance
            T t = loadSpecificExtension(cl, args);
            XMessages.addAssociatedBundle(t);
            ret.add(t);
         }
         catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
               | InvocationTargetException e)
         {
            System.out.println(e);
            throw new Xception(Why.BAD_CONSTRUCTOR);
         }
      }
      return ret;
   }

   /**
    * Load specific extension.
    *
    * @param <T> the generic type
    * @param cl the cl
    * @param args the args
    * @return the extension instance
    * @throws InstantiationException the instantiation exception
    * @throws IllegalAccessException the illegal access exception
    * @throws IllegalArgumentException the illegal argument exception
    * @throws InvocationTargetException the invocation target exception
    * @throws Xception the xception
    */
   private static <T> T loadSpecificExtension(Class<? extends T> cl, Object... args) throws InstantiationException,
         IllegalAccessException, IllegalArgumentException, InvocationTargetException, Xception
   {
      // search constructor
      Constructor<?> constructor = null;
      Constructor<?>[] cons = cl.getConstructors();
      for (Constructor<?> con : cons)
      {
         Class<?>[] parms = con.getParameterTypes();
         if (parms.length == args.length)
         {
            boolean ok = true;
            for (int i = 0; i < parms.length; i++)
            {
               ok &= ((args[i] == null) || (parms[i].isAssignableFrom(args[i].getClass())));
            }
            if (ok)
            {
               constructor = con;
            }
         }
      }

      // create instance
      @SuppressWarnings("unchecked")
      T t = (T) constructor.newInstance();
      XMessages.addAssociatedBundle(t);
      return t;
   }
}