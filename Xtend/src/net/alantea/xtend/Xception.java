package net.alantea.xtend;

/**
 * The Class Xception.
 */
public class Xception extends Exception
{
  private static final long serialVersionUID = 1L;

  /**
   * Why this exception was raised.
   */
  public enum Why
  {
    
    /** no extension. */
    NO_EXTENSION,
    
    /** multiple extension. */
    MULTIPLE_EXTENSION,
    
    /** bad constructor. */
    BAD_CONSTRUCTOR,
    
    /** not implemented. */
    NOT_IMPLEMENTED,
    
    /** bad extension. */
    BAD_EXTENSION,
    
    /** no bundle. */
    NO_BUNDLE,
    
    /** extension error. */
    EXTENSION_ERROR
  }

  /** The reason why. */
  private Why why;

  /**
   * Instantiates a new exception.
   *
   * @param type the type
   */
  public Xception(Why type)
  {
    super();
    why = type;
  }

  /**
   * Instantiates a new exception.
   *
   * @param type the type
   * @param message the message
   */
  public Xception(Why type, String message)
  {
    this(type, message, null);
  }

  /**
   * Instantiates a new exception.
   *
   * @param message the message
   */
  public Xception(String message)
  {
    this(Why.EXTENSION_ERROR, message, null);
  }

  /**
   * Instantiates a new exception.
   *
   * @param message the message
   * @param root the root
   */
  public Xception(String message, Exception root)
  {
    this(Why.EXTENSION_ERROR, message, root);
  }

  /**
   * Instantiates a new exception.
   *
   * @param type the type
   * @param message the message
   * @param root the root
   */
  public Xception(Why type, String message, Exception root)
  {
    super(message, root);
    why = type;
  }

  /**
   * Gets the reason why.
   *
   * @return the why
   */
  Why getWhy()
  {
    return why;
  }
}
