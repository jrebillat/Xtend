package net.alantea.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;

/**
 * The Class PropertyContainer. This is a generic type to hold properties marked with the @Property annotation.
 */
public class PropertyContainer
{
   
   /** The properties map. */
   private Map<String, Object> properties;
   
   /**
    * Never instantiates a new property container.
    */
   protected PropertyContainer()
   {
      // Do not create rough instance !
   }
   
   /**
    * Initialize the map.
    */
   protected void initialize() 
   {
      if (properties != null)
      {
         return;
      }
      properties = new HashMap<>();
      
      Class<?> c = this.getClass();
      while (c != null)
      {
         for (Field field : c.getDeclaredFields())
         {
            if (field.isAnnotationPresent(net.alantea.utils.Property.class))
            {
               field.setAccessible(true);
               Object o = null;
               try
               {
                  o = field.get(this);
               }
               catch (IllegalArgumentException | IllegalAccessException e)
               {
                  continue;
               }
               if (o != null)
               {
                  net.alantea.utils.Property prop = field.getAnnotation(net.alantea.utils.Property.class);
                  String name = prop.name();
                  if (name.isEmpty())
                  {
                     name = field.getName();
                  }
                  System.out.println("found " + name);
                  if (o instanceof Property)
                  {
                     properties.put(name, (Property<?>) o);
                  }
                  else
                  {
                     properties.put(name, field);
                  }
               }
            }
         }
         c = c.getSuperclass();
      }
   }

   /**
    * Bind contained properties to other properties.
    *
    * @param <T> the generic type
    * @param propertyName the property name
    * @param value the value
    * @param args others propertyName/property pairs.
    * @return true, if successful
    */
   public  <T> boolean bind(String propertyName, Property<?> value, Object... args)
   {
      initialize();
      boolean ret = bindOne(propertyName, value);
      if (args.length >= 2)
      {
         for (int i = 0; i < (args.length - 1); i += 2)
         {
            ret |= bindOne((String)args[i], (Property<?>) args[i + 1]);
         }
      }
      
      return ret;
   }

   /**
    * Bind one contained property to another property.
    *
    * @param <T> the generic type
    * @param propertyName the property name
    * @param value the value
    * @return true, if successful
    */
   @SuppressWarnings("unchecked")
   private  <T> boolean bindOne(String propertyName, Property<?> value)
   {
      Property<T> prop = (Property<T>) properties.get(propertyName);
      if (prop == null)
      {
         return false;
      }
      prop.bind((Property<T>) value);
      return true;
   }

   /**
    * Sets the properties values. May be used as a simple key/value setter, or as a list of key/value setters.
    *
    * @param propertyName the property name
    * @param value the value
    * @param args others propertyName/value pairs.
    * @return true, if all is successful
    */
   public  boolean set(String propertyName, Object value, Object... args)
   {
      initialize();
      boolean ret = setOne(propertyName, value);
      if (args.length >= 2)
      {
         for (int i = 0; i < (args.length - 1); i += 2)
         {
            ret |= setOne((String)args[i], args[i + 1]);
         }
      }
      return ret;
   }
   
   /**
    * Sets one property/value pair.
    *
    * @param propertyName the property name
    * @param value the value
    * @return true, if successful
    */
   private boolean setOne(String propertyName, Object value)
   {
      System.out.println("set " + propertyName + " as " + value);
      Object object = properties.get(propertyName);
      if (object == null)
      {
         return false;
      }
      if (object instanceof Property)
      {
         @SuppressWarnings("unchecked")
         Property<Object> prop = (Property<Object>) object;
         prop.unbind();
         prop.setValue(value);
      }
      else
      {
         Field field = (Field) object;
         try
         {
            field.set(this, value);
         }
         catch (IllegalArgumentException | IllegalAccessException e)
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Gets a property by its name.
    *
    * @param <T> the property content type
    * @param propertyName the property name
    * @return the found value
    */
   @SuppressWarnings("unchecked")
   public  <T> T get(String propertyName)
   {
      initialize();
      Object object = properties.get(propertyName);
      if (object == null)
      {
         return null;
      }
      Object val = null;
      if (object instanceof Property)
      {
         Property<Object> prop = (Property<Object>) object;
         val = prop.getValue();
      }
      else
      {
         Field field = (Field) object;
         try
         {
            val = field.get(this);
         }
         catch (IllegalArgumentException | IllegalAccessException e)
         {
            // Sorry...
         }
      }
      return (T) val;
   }

   /**
    * get a property.
    *
    * @param <T> the generic type
    * @param propertyName the property name
    * @return the property
    */
   @SuppressWarnings("unchecked")
   public  <T> Property<T> property(String propertyName)
   {
      initialize();
      Object object = properties.get(propertyName);
      if (object instanceof Property)
      {
         return (Property<T>) object;
      }
      return null;
   }
   
   protected void resetProperties()
   {
      properties = null;
      initialize();
   }
}
