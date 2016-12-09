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

## XMessage usage

## XManager usage

## XEngine usage

