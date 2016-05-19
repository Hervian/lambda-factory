package com.hervian.lambda;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

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
 * This test class does not contain any jUnit tests. The results produced are not deterministic.
 * Rather, it has a main method, and is intended to be called periodically during development, to ensure that a given change
 * does not reduce the Lambda invocations' "direct invocation speed", which is the overall goal of this project.
 * Don't use your computer for anything else when running the test - this will distort the outcome.
 * The tests takes about 1 minute to run on a Macbook pro 2010. 
 * On newer computers it may be necessary to increase the number of iterations, so as to get numbers bigger than 0.
 * 
 * @author Anders Granau Høfft
 */
public class PerformanceTest {
	
	//Constants:
	private static final char 	SOME_CHAR 	= 'c';
	private static final byte 	SOME_BYTE 	= (byte) 127;
	private static final short 	SOME_SHORT 	= (short) 32767;
	private static final int 		SOME_INT 		= 2147483647;
	private static final float 	SOME_FLOAT 	= 345.45f;
	private static final long 	SOME_LONG 	= 3147483647l;
	private static final double SOME_DOUBLE = 3147483647d;
	private static final String SOME_STRING = "some random String/Object";
	
	/*
	 * Methods with simple logic designed to highlight differences in invocation speed.
	 * Changing the logic in a given method may cause the runtimes to converge. 
	 * This seems to indicate that invocation (whether by reflection or by lambda or by direct invocation) is extremely fast. 
	 * Only methods with very simple logic can be used to demonstrate the difference. 
	 * On the other hand, the methods must contain enough logic for the jvm optimizer not to optimize any differences away.
	 */
	protected static int 			staticMethod(int a, int b)		{ return a > b ? a : b; }
	protected static char 		staticMethod(char a, int b)		{ return (char) (a > b ? a : b); }
	protected static boolean 	staticMethod(boolean a, int b){ return b%2==0 && b%4==1 && a; }
	protected static long 		staticMethod(long a, int b)		{ return a > b ? a : b; }
	protected static double 	staticMethod(double a, int b)	{ return a > b ? a : b; }
	protected static float 		staticMethod(float a, int b)	{ return a > b ? a : b; }
	protected static short 		staticMethod(short a, int b)	{ return (short) (a > b ? a : b); }
	protected static byte 		staticMethod(byte a, int b)		{ return (byte) (a > b ? a : b); }
	protected static String 	staticMethod(Object a, int b)	{ return b>((String)a).length() ? "y" : "n"; }
	
	protected void instanceMethod(int a){}
	protected void instanceMethod(char a){}
	protected void instanceMethod(boolean a){}
	protected void instanceMethod(long a){}
	protected void instanceMethod(double a){}
	protected void instanceMethod(float a){}
	protected void instanceMethod(short a){}
	protected void instanceMethod(byte a){}
	protected void instanceMethod(Object a){}

	 //Depending on your computer's speed you may want to increase or decrease the number of iterations:
	private static final int 	ITERATIONS 	= 100_000_000;
	private static final int 	WARM_UP 		= 100*3;

	private static float[] lambdaOverDirect = new float[10];
	
	public static void main(String... args) throws Throwable {
		//warmup:
		runPerformanceTests(true);
		
		//tests:
		runPerformanceTests(false);
	}
	
	private static void runPerformanceTests(boolean warmup) throws Throwable{
		test_boolean(warmup);
		test_char(warmup);
		test_byte(warmup);
		test_short(warmup);
		test_int(warmup);
		test_float(warmup);
		test_long(warmup);
		test_double(warmup);
		test_Object(warmup);
		
		float result = 0;
		for (float ratio : lambdaOverDirect){
			result += ratio;
		}
		if (!warmup)
			System.out.println("Lambda/direct : "+ result*100/(float)9 + "%");
	}

	private static void test_boolean(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.BOOLEAN;
		boolean[] results = new boolean[3];
		boolean arg = true;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] = results[0] || lambda.invoke_for_boolean(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] = results[1] || staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] = results[2] || (boolean) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[0] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_byte(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.BYTE;
		byte[] results = new byte[3];
		byte arg = SOME_BYTE;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] += lambda.invoke_for_byte(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] += staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] += (byte) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[1] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_char(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.CHAR;
		char[] results = new char[3];
		char arg = SOME_CHAR;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] += lambda.invoke_for_char(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] += staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] += (char) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[2] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_short(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.SHORT;
		short[] results = new short[3];
		short arg = SOME_SHORT;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] += lambda.invoke_for_short(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] += staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] += (short) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[3] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_int(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.INT;
		int[] results = new int[3];
		int arg = SOME_INT;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] += lambda.invoke_for_int(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] += staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] += (int) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[4] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_float(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.FLOAT;
		float[] results = new float[3];
		float arg = SOME_FLOAT;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] += lambda.invoke_for_float(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] += staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] += (float) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[5] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_long(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.LONG;
		long[] results = new long[3];
		long arg = SOME_LONG;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] += lambda.invoke_for_long(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] += staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] += (long) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[6] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_double(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.DOUBLE;
		double[] results = new double[3];
		double arg = SOME_DOUBLE;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] += lambda.invoke_for_double(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] += staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] += (double) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[7] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void test_Object(boolean warmup) throws Throwable{
		MethodParameter param = MethodParameter.OBJECT;
		String[] results = new String[3];
		String arg = SOME_STRING;
		Method method = PerformanceTest.class.getDeclaredMethod("staticMethod", param.getType(), int.class);
		Lambda lambda = LambdaFactory.create(method, MethodHandles.lookup());
		long t0 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[0] = (String) lambda.invoke_for_Object(arg, i);
		long t1 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[1] = staticMethod(arg, i);
		long t2 = System.nanoTime();
		for (int i = 0; i < (warmup ? WARM_UP : ITERATIONS); i++)
			results[2] = (String) method.invoke(null, arg, i);
		long t3 = System.nanoTime();
		if (!warmup)
			printPerformance(param, t0, t1, t2, t3);
		lambdaOverDirect[8] = (float) (t1-t0)/ (t2-t1);
	}
	
	private static void printPerformance(MethodParameter param, long t0, long t1, long t2, long t3) {
		System.out.printf("%1$26s\t %2$8s \tLambda: %3$.2fs, Direct: %4$.2fs, Reflection: %5$.2fs%n", 
				"staticMethod("+param.getType().getSimpleName()+", int)",":"+param.getType().getSimpleName(), (t1 - t0) * 1e-9, (t2 - t1) * 1e-9, (t3 - t2) * 1e-9);
	}
	
	
}
