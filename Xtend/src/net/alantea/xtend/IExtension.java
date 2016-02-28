package net.alantea.xtend;

/**
 * The IExtension Interface.
 */
public interface IExtension
{
  
  /**
   * Gets the extended interface.
   *
   * @return the extended interface
   */
  public Class<?> getExtendedInterface();
  
  /**
   * Adds an implementation.
   *
   * @param object the extension object
   * @throws Xception the xception
   */
  public void addImplementation(Object object) throws Xception;

}
