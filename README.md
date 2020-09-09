# lambda-factory
*lambda-factory* is a Java utility project that provides a fast alternative to [Reflection](https://docs.oracle.com/javase/9/docs/api/java/lang/reflect/package-summary.html-based) based method invocation. 

## Releases
Available in [Maven Central](https://search.maven.org/search?q=g:%22com.github.hervian%22%3D%20AND%20a:%22lambda-factory%22%3D) and [mvnrepository.com](https://mvnrepository.com/artifact/com.github.hervian/lambda-factory):
```
  <groupId>com.github.hervian</groupId>
  <artifactId>lambda-factory</artifactId>
  <version>2.0.3 (or see Maven Central or mvnrepository.com for newest version)</version>
 </dependency>
```
Requires **Java 9** or above.

## Overview

The API consists of 2 types:  
  1. `LambdaFactory.create(java.lang.Method myMethod)` returns a dynamically generated Lambda implementation.  
  2. `Lambda.invoke_for_<return-type>(...)` which is **as fast as a direct method invocation**.
     1. Or one of the overloaded alternatives, such as `LambdaFactory.create(MyClass::myAccessableMethod)` 
     (but if you have access to the Method Reference notation you are probably better of
     using the [safety-mirror](https://github.com/Hervian/safety-mirror) project..)

Here are some sample runtimes for 10E8 iterations (reproducable with the class PerformanceTest):

Parameters|Lambda|Direct|Reflection
--- | --- | --- | ---
    (int, int)	 | 0.02s| 0.01s| 4.64s
    (Object, int)	 | 0.03s| 0.02s| 3.23s
 
Do note that creating the Lambda object (`com.github.hervian.lambdas.Lambda`) is "time consuming" - 
i.e above test results do not include the creation time of the Lambda object. 

## Requirements
lambda-factory **requires Java 1.9 or later**.

If you wish to use this project with *Java 8* you must clone the project and 
1. change the pom.xml's properties section 
such that source and target is set to 1.8 (instead of 1.9) and 
1. delete the  module-info.java file.
1. since the project has a dependency to a Java 9 module called *safety.mirror* you must either:
   1. clone the [*safety-mirror*](https://github.com/Hervian/safety-mirror) github project and build it with java 8 
   (this is quite easy, see guide on referenced project) and update your clone's dependency accordingly.
   1. or delete the dependency to *safety-mirror* and the few convenience methods in `LambdaFactory.java` 
   that makes use of the library.
   
After this you should be able to build using JDK-8.

## Java 9+ setup guide...
This project is built with *JDK-9* and is modularized in that in contains a module-info.java file.
The module's name is `lambda.factory`.

Since this library uses reflection to inspect your classes to do what it does you will need to grant it access 
to read your code.  
In Java 9 this is done by adding a command line argument to the Java command:   
`--add-reads lambda.factory=your.module.name`

If you do not add this option you will get a runtime exception when using the `LambdaFactory`'s *create(...)* method: `java.lang.IllegalAccessException: module lambda.factory does not read module`

Remember to change *your.module.name* with the name of your module.  
This command line option can be set up in your IDE such that it is used whenever you run you application via the IDEA. 
In IntelliJ this is done via the menu Run -> Edit configurations. 
Here you must enter the command line option in the text box for VM options.


## Java 9+ setup guide...
This project is built with *JDK-9* and is modularized in that in contains a module-info.java file.
The module's name is `lambda.factory`.

Since this library uses reflection to inspect your classes to do what it does you will need to grant it access 
to read your code.  
In Java 9 this is done by adding a command line argument to the Java command:   
`--add-reads lambda.factory=your.module.name`

If you do not add this option you will get a runtime exception when using the `LambdaFactory`'s *create(...)* method: `java.lang.IllegalAccessException: module lambda.factory does not read module`

Remember to change *your.module.name* with the name of your module.  
This command line option can be set up in your IDE such that it is used whenever you run you application via the IDEA. 
In IntelliJ this is done via the menu Run -> Edit configurations. 
Here you must enter the command line option in the text box for VM options.


## Using lambda-factory
Let's say we have a class called `MyClass`, which defines the following methods:  
```java
private static String myStaticMethod(int a, Integer b){ /*some logic*/ }
private float myInstanceMethod(String a, Boolean b){ /*some logic*/ }
```
We can access these methods as follows:

```java
Method method = MyClass.class.getDeclaredMethod("myStaticMethod", int.class, Integer.class); //Regular reflection call
Lambda lambda = LambdaFactory.create(method);  
String result = (String) lambda.invoke_for_Object(1000, (Integer) 565); //Don't rely on auto boxing of arguments!

Method method = MyClass.class.getDeclaredMethod("myInstanceMethod", String.class, Boolean.class);
Lambda lambda = LambdaFactory.create(method);
float result = lambda.invoke_for_float(new MyClass(), "Hello", (Boolean) null);  //No need to cast primitive results!

Lambda lambda = LambdaFactory.create(MyClass::myStaticMethod); //The overloaded create method accepts Method References!
String result = (String) lambda.invoke_for_Object(1000, (Integer) 565);
```

Notice that the overloaded create method can accept either a Method object, or a double colon Method Reference.  
Also, notice that when invoking the lambda, you must choose an invocation method that contains the target method's return type in its name. In the example above, the chosen `invoke_for_float` method indicates that we are invoking a method, which returns a `float`. If the method you are trying to access returns fx a String, a boxed primitive (Integer, Boolean etc) or some custom Object, you would call `invoke_for_Object`. There are 10 invoke methods:  
* `invoke_for_Object`
* `invoke_for_void`
* `invoke_for_boolean`
* `invoke_for_char`
* `invoke_for_byte`
* `invoke_for_short`
* `invoke_for_int`
* `invoke_for_float`
* `invoke_for_long`
* `invoke_for_double`

Remember not to rely on autoboxing when passing arguments to the invocation method. If your method expects an Integer, and you pass an int, you will get an ´AbstractMethodException´.  
There are several, overloaded `create(...)` methods:

* `Lambda create(Method method)`
* `Lambda create(Method method, MethodHandles.Lookup lookup)`
* `T create(Method method, Class<T> interface, String interfaceMethodName)`
* `T create(Method method, MethodHandles.Lookup lookup, Class<T> interface, String interfaceMethodName)` 
* `createSpecial(...)` - each of the create methods come in a "createSpecial" variant. See explanation below. 

The method `create(Method method)` creates a Lambda with the same access rights as a Method with `setAccessible()==true`. That is, both private, package, protected and public methods are accessible to the created Lambda.  
The method `create(Method method, MethodHandles.Lookup lookup)` creates a Lambda that has the access rights of the argument provided Lookup. The lookup's acceess rights reflect the class, which created it. To access private methods of a class using this constructor, the Lookup must either have been created in the given class, or the Method must have `setAccessible()==true`. Create a Lookup like this: `MethodHandles.lookup()`.  
The signatures that accept an interface class can be used if one wishes to create a dynamic implementation of some other interface than the default Lambda.  
The `createSpecial` variants create a Lambda that will _not_ be subject to dynamic method dispatch.  
Example:  
Let class A implement a method called 'someMethod'. And let class B extend A and override 'someMethod'.  
Then, calling {@link #createSpecial(Method)} with a Method object referring to A.someMethod, will return a Lambda that calls A.someMethod, even when invoked with B as the instance.

This is the price we pay for the speed:  
* We have no single varargs based invocation method as in Reflection. Instead, we have one for every combination of parameters (primitives, Object + void) up until some maximum (see _Implementation comments_ section). 
* The invocation must include the return type in its name.  
* We can't rely on autoboxing when passing arguments to the given `invoke_for_<return-type>(...)` method. Instead, we must explicitly cast.

## Licensing
 lambda-factory is licensed under the Apache License, Version 2.0 (the "License"). You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

## Building from source
lambda-factory uses Maven for its build. To build the complete project you must have Maven installed.  
Then, open a terminal, navigate to the root of the project directory and run:  
`mvn clean install`  
Be aware that the compilation generates source code, namely an interface containing various signatures. The generated source code is placed the conventional place, namely in the folder target/generated-sources/annotations. In your IDE you must enable annotation processing. Typically, this will make the generated source code folder visible to the IDE. Otherwise, you must manually point your IDE to this folder, so that the project will compile in the given IDE.

## Implementation comments
The logic is based on `java.lang.invoke.LambdaMetafactory`.

The LambdaFactory's `create(...)` methods dynamically creates an implementation of the Lambda interface. The Lambda interface actually contains signatures for all combinations of the 8 primitive types + Object (+ void). These are all auto-generated by an `AbstractProcessor`.  
If the project supports the invocation of methods with, say, 3 arguments, the Lambda interface will contain over 10^4 signatures! (1 return type, 3 arguments. Plus support for instance methods, which require an addition argument, namely the instance).  
The number of created signatures can easily be extended: Simply increment an integer in the source code and recompile the project. See `GenerateLambdaMarkerClass.java`.

The dynamically generated class *will only implement a single* of the Lambda interface's methods, namely the one matching the arguments provided to the Lambda's create method.

Since it is not possible in Java to overload methods based on different return types, we need to incorporate the return value in the signature's name and specify it upon invocation.

The many signatures are necessary to provide the desired speed. If one tries to make do with just a few Object based signatures, methods working on primitives will become as slow* as reflection due to auto boxing. Performance will also degrade if one tries to make do with just a single varargs based invocation method, since the involved array creation is relatively expensive.
</br>
</br>
*Method invocations, whether direct or reflection based (or Lambda based) are actually _extremely_ fast. As such, one should generally not stay away from Reflection out of performance concerns, but because of readability: Reflection based invocations (and Lambda based) makes your code difficult to navigate. A lot of an IDE's cool features won't work on the the reflection calls. This library are for those rare occasions, where direct invocations are not an option, and reflective invocations are too slow.

## Dismissed APIs
This section outlines some alternative designs, that for various reasons have been dismissed.  

### Dismissed API no.1
The current code contains 10 invocation methods. It is actually possible to make do with just 3 without compromising the invocation speed:  
* `invoke_for_void`: This is similar to the one we have in the current api, namely for handling void return values.
* `invoke_for_primitiveExclBoolean`: This signature would actually return a double. All methods returning a primitive, _except for boolean_, could be be handled by this signature due to primitive widening conversions (which excludes boolean). Performance do not seem to suffer from the widening. The allowed widening of primitive results is explained in the javadoc of LambdaMetafactory.
* `ìnvoke_for_booleanOrObject`: This signature would handle primitive boolean and Objects. The implied autoboxing of boolean did not seem to degrade performance.

In the end, another design was chosen. Here are the pros and cons of this API:
* PROS
  * Fewer invocation methods, thus a smaller size (kilobytes) of the autogenerated interface Lambda (about 3/10th the size of current API)
* CONS
  * It can be argued that the invocation names are hard to remember as opposed to the current design which follows a strict naming pattern.
  * The need to cast the result of primitives. Even worse: Failing to do an explicit cast (fx from double to int) caused an extreme performance penalty that made the lambda almost as "slow" as reflection.

### Dismissed API no.2
It is tempting to create just a 5-10 purely Object based interface signatures. While this will work - you can create a factory that adjustes the call to LambdaMetafactory accordingly - it was very slow.  
It is also tempting to create a central varargs based `invoke(Object...)` method, but the involved autoboxing and array creation is bad for performance.

### Dismissed API no.3
Method overloading on methods differing on return type only is actually possible in the byte code - the JVM fully accepts this ( - see fx 'covariant return type'). Only, one cannot compile such code, - the byte code must be created without compiling from source code.  
Could this be used to create a "common" invoke(...) method (instead of `invoke_for_<return-type>`)?  
It would be challenging. Say we generated the Lambda interface directly as byte code, and all methods were called `invoke`. How should the compiler know what to compile, when a caller _calls_ invoke(...)? If the return type is used, ie assigned to some variable then _perhaps_ the compiler can choose the correct overloaded method. I suspect, though, that some "ambiguious method call" error would be thrown.

### Disclaimer
This is a hobby project. I do not pretend to be an expert, rather I try to increase my knowledge about Java and programming in general. My knowledge of LambdaMetafactory comes solely from javadoc, experiments and, not least, the many answers on stackoverflow from a german user called Holger.
