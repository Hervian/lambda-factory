package com.hervian.lambda;

import java.lang.reflect.Method;

import org.junit.Test;

public class InvokeLambdaForInstanceMethod_Test {

	private static final String 	SOME_STRING 	= "some String";
	
	/*
	 * These methods exists to 
	 * A) assert that the logic is able to access private methods and 
	 * B) that the logic is able to access instance methods for the various argument types.
	 */
	@SuppressWarnings("unused")
	private void instanceMethod(int a){}
	@SuppressWarnings("unused")
	private void instanceMethod(char a){}
	@SuppressWarnings("unused")
	private void instanceMethod(boolean a){}
	@SuppressWarnings("unused")
	private void instanceMethod(long a){}
	@SuppressWarnings("unused")
	private void instanceMethod(double a){}
	@SuppressWarnings("unused")
	private void instanceMethod(float a){}
	@SuppressWarnings("unused")
	private void instanceMethod(short a){}
	@SuppressWarnings("unused")
	private void instanceMethod(byte a){}
	@SuppressWarnings("unused")
	private void instanceMethod(Object a){}
	
	@Test
	public void testInvokeInstanceMethod_ForAllArgumentTypes() throws Throwable{
		InvokeLambdaForInstanceMethod_Test instance = new InvokeLambdaForInstanceMethod_Test();
		for (MethodParameter param : MethodParameter.values()){
			Method method = InvokeLambdaForInstanceMethod_Test.class.getDeclaredMethod("instanceMethod", param.getType());
			Lambda lambda = LambdaFactory.create(method);
			if (param.getType()==boolean.class){
				lambda.invoke_for_void(instance, true);
			} else {
				switch (param) {
				case BYTE:
					lambda.invoke_for_void(instance, (byte)127); break;
				case CHAR:
					lambda.invoke_for_void(instance, 'c'); break;
				case DOUBLE:
					lambda.invoke_for_void(instance, 3452d); break;
				case FLOAT:
					lambda.invoke_for_void(instance, 345.45f); break;
				case INT:
					lambda.invoke_for_void(instance, 2147483647); break;
				case LONG:
					lambda.invoke_for_void(instance, 3147483647l); break;
				case OBJECT:
					lambda.invoke_for_void(instance, SOME_STRING); break;
				case SHORT:
					lambda.invoke_for_void(instance, (short)32767); break;
				default:
					throw new UnsupportedOperationException("It appears this test is incomplete - all types should be tested.");
				}
			}
		}
	}

	@Test
	public void testInvokeInstanceMethod_ForObject() throws Throwable {
		Method method = InvokeLambdaForInstanceMethod_Test.class.getDeclaredMethod("instanceMethod", Object.class);
		Lambda lambda = LambdaFactory.create(method);
		
		lambda.invoke_for_void(new InvokeLambdaForInstanceMethod_Test(), "some string");
	}

}
