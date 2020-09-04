package com.github.hervian.lambdas;

import com.github.hervian.lambdas.util.GenerateLambdaProcessor;
import com.github.hervian.reflection.Fun;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;




/**
 * Copyright 2016 Anders Granau Høfft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * END OF NOTICE
 *
 * In essence, this class simply wraps the functionality of {@link LambdaMetafactory#metafactory(java.lang.invoke.MethodHandles.Lookup, String, MethodType, MethodType, MethodHandle, MethodType)}.
 * <br>
 * However, some additional logic is needed to handle the case of 
 * <ul>
 * <li>static Method vs instance method.
 * <li>primitive method parameters, vs Object (or subclasses thereof, including boxed primitives)
 * <li>private methods
 * </ul>
 * @author Anders Granau Høfft
 */
public class LambdaFactory {

	/**
   * <pre>
	 * Creates a {@link Lambda} from a Method Reference. Don't worry about the many overloaded methods.
	 * Just know that you can create a Lambda from either a java.lang.Method or a double colon Method Reference,
   * e.g. 'Lambda lambda = LambdaFactory.create(" hello world   "::trim);'
   *
   * Admittely, this method does not make much sense - if you have access to the Method Reference notation, you are better of using the safety-mirror project,
   * which allows you to turn Method References of any kind into function objects. @see <a href="https://github.com/Hervian/safety-mirror">The JSON site</a>
   * </pre>
	 */
	static <DUMMY> Lambda create(Fun.With0ParamsAndVoid<DUMMY> methodRef) throws Throwable { return create(methodRef.toMethod()); }

	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1> Lambda create(Fun.With1ParamAndVoid<T1> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2> Lambda create(Fun.With2ParamsAndVoid<T1, T2> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2, T3> Lambda create(Fun.With3ParamsAndVoid<T1, T2, T3> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2, T3, T4> Lambda create(Fun.With4ParamsAndVoid<T1, T2, T3, T4> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2, T3, T4, T5> Lambda create(Fun.With5ParamsAndVoid<T1, T2, T3, T4, T5> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2, T3, T4, T5, T6> Lambda create(Fun.With6ParamsAndVoid<T1, T2, T3, T4, T5, T6> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2, T3, T4, T5, T6, T7> Lambda create(Fun.With7ParamsAndVoid<T1, T2, T3, T4, T5, T6, T7> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2, T3, T4, T5, T6, T7, T8> Lambda create(Fun.With8ParamsAndVoid<T1, T2, T3, T4, T5, T6, T7, T8> methodRef) throws Throwable { return create(methodRef.toMethod()); }
	/**
	 * @see #create(Fun.With0ParamsAndVoid)
	 */
	static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Lambda create(Fun.With9ParamsAndVoid<T1, T2, T3, T4, T5, T6, T7, T8, T9> methodRef) throws Throwable { return create(methodRef.toMethod()); }

	/**
	 * creates a Lambda with the same access rights as a Method with setAccessible()==true. 
	 * That is, both private, package, protected and public methods are accessible to the created Lambda.
	 * @param method A Method object which defines what to invoke.
	 * @return A dynamically generated class that implements the Lambda interface and the a method that corresponds to the Method. 
	 * The implementation offers invocation speed similar to that of a direct method invocation.
	 * @throws Throwable
	 */
	public static Lambda create(Method method) throws Throwable {
		return privateCreate(method, false);
	}

	/**
	 * Same as {@link #create(Method)} except that this method returns a Lambda that will <em>not</em> be subject to dynamic method dispatch.
	 * <p>Example:
	 * <br>Let class A implement a method called 'someMethod'. And let class B extend A and override 'someMethod'.
	 * <br>Then, calling {@link #createSpecial(Method)} with a Method object referring to A.someMethod, will return a Lambda
	 * that calls A.someMethod, even when invoked with B as the instance.
	 * @param method
	 * @return
	 * @throws Throwable
	 */
	public static Lambda createSpecial(Method method) throws Throwable {
		return privateCreate(method, true);
	}

	private static Lambda privateCreate(Method method, boolean createSpecial) throws Throwable {
		Class<?> returnType = method.getReturnType();
		String signatureName = GenerateLambdaProcessor.getMethodName(returnType.getSimpleName());
		return createSpecial
				? createSpecial(method, Lambda.class, signatureName)
				: create(method, Lambda.class, signatureName);
	}

	private static Lambda create(Method method, MethodHandles.Lookup lookup, boolean invokeSpecial) throws Throwable {
		Class<?> returnType = method.getReturnType();
		String signatureName = GenerateLambdaProcessor.getMethodName(returnType.getSimpleName());
		return createLambda(method, lookup, Lambda.class, signatureName, invokeSpecial);
	}

	/**
	 * Similar to {@link #create(Method)}, except that this factory method returns a dynamically generated 
	 * implementation of the argument provided interface.
	 * The provided signatureName must identify a method, whose arguments corresponds to the Method. (If the Method
	 * is a non-static method the interface method's first parameter must be an Object, and the subsequent parameters
	 * must match the Method.)
	 * <p>Example:<br>
	 * Method method = MyClass.class.getDeclaredMethod("myStaticMethod", int.class, int.class);<br>
	 * IntBinaryOperator sam = LambdaFactory.create(method, IntBinaryOperator.class, "applyAsInt");<br>
	 * int result = sam.applyAsInt(3, 11);<br>
	 * @param method A Method object which defines what to invoke.
	 * @param interfaceClass The interface, which the dynamically generated class shall implement.
	 * @param signatatureName The name of an abstract method from the interface, which the dynamically create class shall implement.
	 * @return A dynamically generated implementation of the argument provided interface. The implementation offers invocation speed similar to that of a direct method invocation.
	 * @throws Throwable
	 */
	public static <T> T create(Method method, Class<T> interfaceClass, String signatatureName) throws Throwable {
		return create(method, interfaceClass, signatatureName, false);
	}

	/**
	 * * Same as {@link #create(Method)} except that this method returns a Lambda that will <em>not</em> be subject to dynamic method dispatch.
	 * 	 * See {@link #createSpecial(Method)}
	 * @param method
	 * @param interfaceClass
	 * @param signatatureName
	 * @param <T>
	 * @return
	 * @throws Throwable
	 */
	public static <T> T createSpecial(Method method, Class<T> interfaceClass, String signatatureName) throws Throwable {
		return create(method, interfaceClass, signatatureName, true);
	}

	private static <T> T create(Method method, Class<T> interfaceClass, String signatureName, boolean invokeSpecial) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
		return createLambda(method, lookup, interfaceClass, signatureName, invokeSpecial);
	}

	private static <T> T createLambda(Method method, MethodHandles.Lookup lookup, Class<T> interfaceClass, String signatatureName, boolean createSpecial) throws Throwable {
		lookup = lookup.in(method.getDeclaringClass());
		return privateCreateLambda(method, lookup, interfaceClass, signatatureName, createSpecial);
	}


	/**
	 *
	 * This method uses {@link LambdaMetafactory} to create a lambda.
	 * <br>
	 * The lambda will implement the argument provided interface.
	 * <br>
	 * This interface is expected to contain a signature that matches the method, for which we are creating the lambda.
	 * In the context of the lambda-factory project, this interface will always be the same, namely an auto-generate interface
	 * with abstract methods for all combinations of primitives + Object (up until some max number of arguments.)
	 * <p>
	 *
	 * @param method The {@link Method} we are trying to create "direct invocation fast" access to.
	 * @param lookup
	 * @param interfaceClass The interface, which the created lambda will implement. In the context of the lambda-factory project this will always be the same, namely a compile time auto-generated interface. See {@link GenerateLambdaProcessor}.
	 * @param signatureName
	 * @param createSpecial
	 * @param <T>
	 * @return An instance of the argument provided interface, which implements only 1 of the interface's methods, namely the one whose signature matches the method, we are creating fast access to.
	 * @throws Throwable
	 */
	private static <T> T privateCreateLambda(Method method, MethodHandles.Lookup lookup, Class<T> interfaceClass, String signatureName, boolean createSpecial) throws Throwable {
		MethodHandle methodHandle = createSpecial? lookup.unreflectSpecial(method, method.getDeclaringClass()) : lookup.unreflect(method);
		MethodType instantiatedMethodType = methodHandle.type();
		MethodType signature = createLambdaMethodType(method, instantiatedMethodType);

		CallSite site = createCallSite(signatureName, lookup, methodHandle, instantiatedMethodType, signature,interfaceClass);
		MethodHandle factory = site.getTarget();
		return (T) factory.invoke();
	}

	private static MethodType createLambdaMethodType(Method method, MethodType instantiatedMethodType) {
		boolean isStatic = Modifier.isStatic(method.getModifiers());
		MethodType signature = isStatic ? instantiatedMethodType : instantiatedMethodType.changeParameterType(0, Object.class);

		Class<?>[] params = method.getParameterTypes();
		for (int i=0; i<params.length; i++){
			if (Object.class.isAssignableFrom(params[i])){
				signature = signature.changeParameterType(isStatic ? i : i+1, Object.class);
			}
		}
		if (Object.class.isAssignableFrom(signature.returnType())){
			signature = signature.changeReturnType(Object.class);
		}

		return signature;
	}

	private static CallSite createCallSite(String signatureName, MethodHandles.Lookup lookup, MethodHandle methodHandle,
										   MethodType instantiatedMethodType, MethodType signature, Class<?> interfaceClass) throws LambdaConversionException {
		return LambdaMetafactory.metafactory(
				lookup,
				signatureName,
				MethodType.methodType(interfaceClass),
				signature,
				methodHandle,
				instantiatedMethodType);
	}

}