package com.github.hervian.lambdas;

public interface InterfaceWithDefaultMethodAndAbstractMethod {

	public static final String RETURN_VALUE_DEFAULT_METHOD = "RETURN_VALUE_DEFAULT_METHOD";
	
	String abstractMethod();
	
	default String defaultMethod(){
		return RETURN_VALUE_DEFAULT_METHOD;
	}
	
}
