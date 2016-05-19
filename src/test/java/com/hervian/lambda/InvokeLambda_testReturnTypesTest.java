package com.hervian.lambda;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class InvokeLambda_testReturnTypesTest {

	private static final byte 		SOME_BYTE 		= (byte)1234;
	private static final short 		SOME_SHORT 		= (short)1234;
	private static final boolean 	SOME_BOOLEAN 	= false;
	private static final float 		SOME_FLOAT 		= 234.34f;
	private static final int 			SOME_INT 			= 1234;
	private static final double 	SOME_DOUBLE 	= 1234321d;
	private static final char 		SOME_CHAR 		= 'c';
	private static final long 		SOME_LONG 		= 1234123412l;
	private static final String 	SOME_STRING 	= "some String";
	
	protected static int 			intMethod(){ 					return SOME_INT; }
	protected static char 		charMethod(){ 				return SOME_CHAR; }
	protected static boolean 	booleanMethod(){ 			return SOME_BOOLEAN; }
	protected static long 		longMethod(){ 				return SOME_LONG; }
	protected static double 	doubleMethod(){ 			return SOME_DOUBLE; }
	protected static float 		floatMethod(){ 				return SOME_FLOAT; }
	protected static short 		shortMethod(){ 				return SOME_SHORT; }
	protected static byte 		byteMethod(){ 				return SOME_BYTE; }
	protected static Object 	objectMethod(){ 			return SOME_STRING; }
	
	
	@Test
	public void testInvokeStaticMethod_returnObject() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("objectMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		String result = (String) lambda.invoke_for_Object();
		
		assertEquals(SOME_STRING, result);
	}
	
	@Test
	public void testInvokeStaticMethod_returnBoolean() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("booleanMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		boolean result = lambda.invoke_for_boolean();
		
		assertEquals(SOME_BOOLEAN, result);
	}
	
	@Test
	public void testInvokeStaticMethod_returnChar() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("charMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		char result = lambda.invoke_for_char();
		
		assertEquals(SOME_CHAR, result);
	}
	
	@Test
	public void testInvokeStaticMethod_return_long() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("longMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		long result = lambda.invoke_for_long();
		
		assertTrue(SOME_LONG==result);
	}
	
	@Test
	public void testInvokeStaticMethod_return_double() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("doubleMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		double result = lambda.invoke_for_double();
		
		assertTrue(SOME_DOUBLE==result);
	}
	
	@Test
	public void testInvokeStaticMethod_return_int() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("intMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		int result = lambda.invoke_for_int();
		
		assertTrue(SOME_INT==result);
	}
	
	@Test
	public void testInvokeStaticMethod_return_float() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("floatMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		float result = lambda.invoke_for_float();
		
		assertTrue(SOME_FLOAT==result);
	}
	
	@Test
	public void testInvokeStaticMethod_return_short() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("shortMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		short result = lambda.invoke_for_short();
		
		assertTrue(SOME_SHORT==result);
	}
	
	@Test
	public void testInvokeStaticMethod_return_byte() throws Throwable {
		Method method = InvokeLambda_testReturnTypesTest.class.getDeclaredMethod("byteMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		byte result = lambda.invoke_for_byte();
		
		assertTrue(SOME_BYTE==result);
	}

}
