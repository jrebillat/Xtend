# Xtend library quick documentation

## Concept
The Xtend library is offering two separate type of functionalities.
The first one is to allow an application to manage *extensions* to some class or interface. It is as an example a way to simply manage plugins in an application.
The second one is an simple way to offer internationalization of messages (based on the *locale* concept).

## Internationalization
### What is it ?
In Java, the internationalization is done through the locale and an instance of ResourceBundle. But what to do if you have to manage several bundles, located in various jars, each being associated with a bunch of classes and texts ? A possible answer is here.
As said before, we will use bundles, thus normal properties files, but bundled together in a global international message meta-bundle named *XMessages*.

### Adding a bundle
To bundle the bundles, you will have to add them to the Xmessages global system. There are two ways to add a new bundle. Before explaining how to add them, just remember that the bundles are sets of property files located somewhere in the Java class path. To manage bundles, you thus normally have to know the path to the bundle.
You may add bundles by giving the path - there are methods in Xmessages to do that. This is useful for straight bundles. Sometimes - most of the time in fact - bundles are associated with some bunch of classes, located in a jar and the bundle is also in this jar. And many times, there is a *master class* in this jar. Provided that the root name of the bundle id the same as the class name, you have  methods to add bundles by giving a class instance.

### getting values
The principle of the bundles is to associate internationalized string values to keys (also strings). Getting the value from XMessages is as easy as to ask for the key and get the value back. But, as there may be several bundles, there also may exists several values for a key, located in different bundles. There must be a way to select which value to be returned.
This is done by giving a priority level to each bundle. Values will be searched using the priority level.

### Using parameters
Sometimes (for example for error message strings), returning the value alone may not be enough : you may have to add some information before returning the string . This is a possibility in Xmessages.

## Extensions
Most of the time, programs are just programs. They use some libraries, add some valuable code on it and, when launched, do the job. They are built on a closed set of coding elements.
But sometimes, the things are becoming a little more complex and, when coding things, you may not really know what be underneath your code. This may be a special device driver, a specific widget toolkit, a variation on database access or anything else that make your code depend on libraries that may vary. Or, even worst, you may have several libraries in parallel to manage the same way (ex: drivers for several devices).
The best way to deal with this problem is to built an API, on top of the variable part, that will level all the calls from the top part. Then you write implementations for this API, one for every variation (ex : one for PostgreSQL, on for TinySQl, one for MariaDB...). And your application code will, using the same API, be portable, whatever the implementation solution is the chosen one.
OK, but, how to tell the application which value to return ? The answer is to level your bundles. The returned value will depend one the various levels of bundles containing the searched key.

## Bonus
The start of a javascript engine encapsulation is also available in XEngine.

## XMessages usage
The Xmessages is an utility class, with only static methods. This internationalization system is meant to be application-wide.

### Adding bundles
As said before, bundles may be added from their path, or from a class instance. The class must have the same name and the same package as the properties files.
* To simply add a bundle, use `boolean addBundle(String path)`. The bundle located at the given path will be loaded with the correct international version. Concerning levels his bundle will be considered as level 0. The method will return *true* if the bundle has been correctly loaded.
* To add a bundle at a given level, use `addBundle(String path, int level)`. The bundle located at the given path will be loaded with the correct international version, at the given level.The method will return *true* if the bundle has been correctly loaded.
* To load a bundle associated with an instance from a class, use `boolean addAssociatedBundle(Object associated)`. The bundle located near the class path of the associated object will be loaded with the correct international version. Concerning levels his bundle will be considered as level 0. The method will return *true* if the bundle has been correctly loaded.
* To load a bundle associated with an instance from a class at a given level, use `boolean addAssociatedBundle(Object associated, int level)`. The bundle located near the class path of the associated object will be loaded with the correct international version, at the given level. The method will return *true* if the bundle has been correctly loaded.

### Getting simple values
The base method to get values is `String get(String key, String... parameters)`, that may be most of the time used as `String get(String key)` if you do not need to get a value including parameter strings. We will discuss later about parameters. The returned value is the found value, if any, or the key itself if no corresponding value has been found.
There are a few helpers, to get an integer : `int getInteger(String key)` that returns 0 if no value has been found, to get a double : `double getDouble(String key)` that returns 0.0 if no value has been found, and `String getOrNull(String key)` that try to find a value and returns *null* if no value has been found.

### Using parameters
To get a parametrized value, use the `String get(String key, String... parameters)` with parameters. But for parameters to be used, the value string must provide slots to insert the parameter values in it. This is done using the `[n]` syntax, with *n* being an integer value. The matching parameter (starting at 1 being the first parameter) is in included to replace the [n] part of the string.
As an example, with the following key defined in a bundle :
`Error.display.message = An error of type [1] occurred with messages '[2]'.` (and the same, localized, in other languages), you may call (with an error named *error* being thrown) the method like `get("", error.class.getName(), error.getLocalizedMessage();` to get a good-looking error message string.
Note that the same parameter may be inserted more than once in the value string (or not inserted at all). If there are too much arguments, the last ones will be ignored. If there are nor enough, the string `null` will replace the corresponding [n] string (currently not internationalized).

### Getting the bundles
Sometimes, you will not want to use overridden values, but the one from a specific bundle. Thus to help you, you can get a bundle associated to an object using the `ResourceBundle getBundle(Object associated)`. Be careful, as this will create the corresponding bundle, at level 0, if it did not exist. You may use `ResourceBundle getBundle(Object associated, int level)` to create the bundle at a correct level if it did not exist. If it exists, the level value is ignored.

## XManager usage

## XEngine usage

