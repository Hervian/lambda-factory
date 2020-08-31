package com.hervian.lambda;

import java.lang.reflect.Method;

import com.github.hervian.lambdas.Lambda;
import com.github.hervian.lambdas.LambdaFactory;
import com.github.hervian.lambdas.util.MethodParameter;
import org.junit.Test;

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
 * @author Anders Granau Høfft
 *
 */
public class InvokeLambdaForStaticMethods_Test {

	private static final String 	SOME_STRING 	= "some String";
		
	
	/*
	 * These methods exists to 
	 * A) assert that the logic is able to access protected methods and 
	 * B) that the logic is able to access static methods for the various argument types.
	 */
	protected static void staticMethod(int a){}
	protected static void staticMethod(char a){}
	protected static void staticMethod(boolean a){}
	protected static void staticMethod(long a){}
	protected static void staticMethod(double a){}
	protected static void staticMethod(float a){}
	protected static void staticMethod(short a){}
	protected static void staticMethod(byte a){}
	protected static void staticMethod(Object a){}
	protected static void staticMethod(String... a){}
	
	
	@Test
	public void testInvokeStaticMethod_ForAllArgumentTypes() throws Throwable{
		for (MethodParameter param : MethodParameter.values()) {
			Method method = InvokeLambdaForStaticMethods_Test.class.getDeclaredMethod("staticMethod", param.getType());
			Lambda lambda = LambdaFactory.create(method);
			switch (param) {
			case BYTE:
				lambda.invoke_for_void((byte) 127);
				break;
			case CHAR:
				lambda.invoke_for_void('c');
				break;
			case DOUBLE:
				lambda.invoke_for_void(3452d);
				break;
			case FLOAT:
				lambda.invoke_for_void(345.45f);
				break;
			case INT:
				lambda.invoke_for_void(2147483647);
				break;
			case LONG:
				lambda.invoke_for_void(3147483647l);
				break;
			case OBJECT:
				lambda.invoke_for_void(SOME_STRING);
				break;
			case SHORT:
				lambda.invoke_for_void((short) 32767);
				break;
			case BOOLEAN:
				lambda.invoke_for_void(true);
				break;
			default:
				throw new UnsupportedOperationException("It appears this test is incomplete - all types should be tested.");
			}
		}
	}	
	
	@Test
	public void testInvokeStaticMethod_ForObject() throws Throwable {
		Method method = InvokeLambdaForStaticMethods_Test.class.getDeclaredMethod("staticMethod", Object.class);
		Lambda lambda = LambdaFactory.create(method);
		
		lambda.invoke_for_void("some string");
	}
	
	@Test
	public void testInvokeStaticMethod_ForVarArgsOfStrings() throws Throwable {
		Method method = InvokeLambdaForStaticMethods_Test.class.getDeclaredMethod("staticMethod", String[].class);
		Lambda lambda = LambdaFactory.create(method);
		
		lambda.invoke_for_void(new String[]{"some string", "another string"});
	}
	

}
