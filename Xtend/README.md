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

## Javascript
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
To get a parametrized value, use the `String get(String key, String... parameters)` with parameters. But for parameters to be used, the value string must provide slots to insert the parameter values in it. If the values are coming from your application and do not need to be internationalized, this is done using the `[n]` syntax, with *n* being an integer value. The matching parameter (starting at 1 being the first parameter) is included to replace the [n] part of the string. If the parameter is in fact a key name that need to be internationalized (recursively using XMessages), this is done using the `{n}` syntax, with *n* being an integer value. The matching parameter (starting at 1 being the first parameter) is searched in the bundles and the found vlue is included to replace the {n} part of the string. 

As an example, with the following key defined in a bundle :
`Error.display.message = An error of type [1] occurred with messages '[2]'.` (and the same, localized, in other languages), you may call (with an error named *error* being thrown) the method like `get("", error.class.getName(), error.getLocalizedMessage();` to get a good-looking error message string.

Note that the same parameter may be inserted more than once in the value string (or not inserted at all). If there are too much arguments, the last ones will be ignored. If there are nor enough, the string `null` will replace the corresponding [n] string (currently not internationalized).

### Getting the bundles
Sometimes, you will not want to use overridden values, but the one from a specific bundle. Thus to help you, you can get a bundle associated to an object using the `ResourceBundle getBundle(Object associated)`. Be careful, as this will create the corresponding bundle, at level 0, if it did not exist. You may use `ResourceBundle getBundle(Object associated, int level)` to create the bundle at a correct level if it did not exist. If it exists, the level value is ignored.

## XEngine usage
The XEngine class is used to create instances of javascript scripting engines in a simple way, to set values to variables, evaluate scripts and get values from variables. It may, in the future, provide engines for other languages.
To create a new XEngine, it is simple as calling `XEngine engine = new XEngine();`.

To put a value in a global variable in the engine, call `engine.put(String name, Object value);` The new *name* variable will be available, at global scope.

To evaluate a script, use `Object returned = engine.eval(String script);` that will evaluate the text of the script and return the value returned by the script execution. You may also use any type of Reader as an input for the evaluation : `Object returned = eval(Reader reader);`

To get a value from a global variable in the engine, call `Object value = engine.get(String name);` The content of the *name* variable will be returned, if it exists at global scope.

## XManager usage
The main usage of the extension mechanism is get all (one or more) extension classes that correspond to a base class or interface definition. This may be also creating an instance of all the existing derived classes (excluding abstract ones). To achieve this, there are several method, depending on what to really do, with parameters. Of course, most of the time, if you known all the jars from the application, this is straightforward. But the mechanisme here allows you to build the application and calling methods *without knowing the implementing class* and *without any configuration file. for example : if you want to change the database type, just add the corresponding jars and a wrapper jar with an extension to your *abstract database wrapper (or interface)*. The key here is : ***no configuration when changing***.

### getting the derived classes
At first, you maybe just want to know the list of the classes implementing your base class or interface. Note that this is not required for the other methods to work. Use `List<Class<MyClass>> myClassList = XManager.loadExtensionClasses(MyClass.class, boolean allowsMultipe, boolean forceReload);`. This will give you a list of classes, all deriving from the base *MyClass* class. The parameters allows you to specify if you are waiting for only one derived class (this may be the case for a database system) or any number of elements in the list (as for a bunch of drivers). As parsing the jars for classes may be quite long, all searches are cached for speed. You may want to refresh the cache. To get only one extension without refreshing, use `Class<MyClass> myderivedClass = XManager.loadExtensionClass(MyClass.class);`.

### Getting instances
The process to get one instance of all the derived classes from the base class or interface is quite the same. The big difference is that you may need to add parameters to the *new* call to create the instances. Thus the call is `List<MyClass> myInstanceList = XManager.loadAbstractExtensions(MyClass.class, boolean forceReload, Object... arguments);`. The mechanism will search for the classes (refreshing the cache if required) and try to instantiate an instance of each one using the given arguments. Note that the arguments are the same for all creation calls.

The call to get the instance of a single derived class (generating an Exception if more than one is found) is `MyClass myInstance = XManager.loadAbstractExtension(MyClass.class, boolean forceReload, Object... arguments);`. The mechanism will search for the class (refreshing the cache if required) and try to instantiate an instance of it using the given arguments.

Extensions and internationalization are closely linked : XMessages is automatically called by XManager to load an associated bundle each time it loads a new extension. You may simply put properties files along with your extensions and do not bother with it in the code.

### Container extensions
TBW 