package com.hervian.lambda;

import static org.junit.Assert.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.junit.Test;

import com.hervian.lambda.testmethods.ClassWithPrivateAndProtectedMethodInSeparatePackage;

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
 * This class will not compile before a 'mvn compile' have been executed.
 * This is because a Maven compilation plugin is responsible for:
 * <ol> 
 * <li> compiling the {@link GenerateLambdaProcessor} and dependencies
 * <li> compiling everything else, using the already compiled {@link GenerateLambdaProcessor} to generate source code.
 * </ol>
 * The generated source code is an interface, Lambda.
 * This test is about 
 * 	A) ensuring that a compilation of the project produces a functioning Lambda interface and 
 * 	B) that the various invoke methods work.
 * @throws Throwable 
 */
public class InvokeLambda_AccessRights_Test extends ClassWithPrivateAndProtectedMethodInSeparatePackage {
	
	@Test (expected = IllegalAccessException.class)
	public void test_Lambda_createWithLookup_setAccessibleIsFalse_Inaccessible() throws Throwable {
		Method method = ClassWithPrivateAndProtectedMethodInSeparatePackage.class.getDeclaredMethod("myMethod");
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		
		lambda.invoke_for_Object();
	}
	
	@Test
	public void test_Lambda_createWithLookup_protectedMethod() throws Throwable {
		Method method = ClassWithPrivateAndProtectedMethodInSeparatePackage.class.getDeclaredMethod("protectedMethod");
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		
		String result = (String) lambda.invoke_for_Object();
		
		assertEquals(ClassWithPrivateAndProtectedMethodInSeparatePackage.RESULT, result);
	}
	
	@Test
	public void test_Lambda_createWithoutLookup_protectedMethod() throws Throwable {
		Method method = ClassWithPrivateAndProtectedMethodInSeparatePackage.class.getDeclaredMethod("protectedMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		String result = (String) lambda.invoke_for_Object();
		
		assertEquals(ClassWithPrivateAndProtectedMethodInSeparatePackage.RESULT, result);
	}
	
	@Test
	public void test_Lambda_createWithLookup_setAccessibleIsTrue() throws Throwable {
		Method method = ClassWithPrivateAndProtectedMethodInSeparatePackage.class.getDeclaredMethod("myMethod");
		method.setAccessible(true);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		
		String invokeResult = (String) lambda.invoke_for_Object();
		
		assertEquals(ClassWithPrivateAndProtectedMethodInSeparatePackage.RESULT, invokeResult);
	}
	
	
	

}
