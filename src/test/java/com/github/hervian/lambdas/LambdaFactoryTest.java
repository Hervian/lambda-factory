package com.github.hervian.lambdas;

import java.lang.reflect.Method;
import java.util.function.IntBinaryOperator;

import org.junit.Test;

import junit.framework.TestCase;

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
public class LambdaFactoryTest extends TestCase {

	
	@Test
	public void test_create_forInterface() throws Throwable{
		Method method = LambdaFactoryTest.class.getDeclaredMethod("staticIntMethod", int.class, int.class);
		IntBinaryOperator sam = LambdaFactory.create(method, IntBinaryOperator.class, "applyAsInt");
		
		int result = sam.applyAsInt(3, 11);
		
		assertTrue(3+11==result);
	}
	
	@SuppressWarnings("unused")
	private static int staticIntMethod(int a, int b){
		return a+b;
	}

	@Test
	public void testZeroParams() throws Throwable {
		Lambda lambda = LambdaFactory.create(new LambdaFactoryTest()::returnString0Params);
		assertEquals("hello world", lambda.invoke_for_Object(new LambdaFactoryTest()));
	}

	public String returnString0Params(){ return "hello world";}
	public String returnString1Param(String p1){ return p1;}
	public String returnString2Params(String p1, String p2){ return p1 + p2;}
	
	
}
