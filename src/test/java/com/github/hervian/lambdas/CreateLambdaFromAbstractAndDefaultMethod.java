package com.github.hervian.lambdas;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import com.github.hervian.lambdas.Lambda;
import org.junit.Test;

public class CreateLambdaFromAbstractAndDefaultMethod {

	@Test
	public void test_invokeDefaultMethod() throws Throwable {
		Method method = InterfaceWithDefaultMethodAndAbstractMethod.class.getDeclaredMethod("defaultMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		String result = (String) lambda.invoke_for_Object(new Implementation());
		
		assertEquals(InterfaceWithDefaultMethodAndAbstractMethod.RETURN_VALUE_DEFAULT_METHOD, result);
	}
	
	@Test
	public void test_invokeOverriddenDefaultMethod() throws Throwable {
		Method method = InterfaceWithDefaultMethodAndAbstractMethod.class.getDeclaredMethod("defaultMethod");
		Lambda lambda = LambdaFactory.create(method);
		String resultFromOverridenDefaultMethod = "resultFromOverridenDefaultMethod";
		Implementation instance = new Implementation(){
			@Override public String defaultMethod(){
				return resultFromOverridenDefaultMethod;
			}
		};
		
		String result = (String) lambda.invoke_for_Object(instance);
		
		assertEquals(resultFromOverridenDefaultMethod, result);
	}

	@Test
	public void test_createSpecial_invokeDefaultMethod() throws Throwable {
		Method method = InterfaceWithDefaultMethodAndAbstractMethod.class.getDeclaredMethod("defaultMethod");
		Lambda lambda = LambdaFactory.createSpecial(method);
		String resultFromOverridenDefaultMethod = "resultFromOverridenDefaultMethod";
		Implementation instance = new Implementation(){
			@Override public String defaultMethod(){
				return resultFromOverridenDefaultMethod;
			}
		};
		
		String result = (String) lambda.invoke_for_Object(instance);
		
		assertEquals(InterfaceWithDefaultMethodAndAbstractMethod.RETURN_VALUE_DEFAULT_METHOD, result);
	}
	
	@Test
	public void test_OverriddenAbstractMethod() throws Throwable {
		Method method = InterfaceWithDefaultMethodAndAbstractMethod.class.getDeclaredMethod("abstractMethod");
		Lambda lambda = LambdaFactory.create(method);
		
		String result = (String) lambda.invoke_for_Object(new Implementation());
		
		assertEquals(Implementation.RETURN_VALUE_IMPLEMENTATION_ABSTRACT_METHOD, result);
	}
	
	
	private static class Implementation implements InterfaceWithDefaultMethodAndAbstractMethod{

		static final String RETURN_VALUE_IMPLEMENTATION_ABSTRACT_METHOD = "RETURN_VALUE_IMPLEMENTATION_ABSTRACT_METHOD"; 
		
		@Override
		public String abstractMethod() {
			return RETURN_VALUE_IMPLEMENTATION_ABSTRACT_METHOD;
		}
		
	}

}
