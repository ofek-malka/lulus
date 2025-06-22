package dev.ofekmalka.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TwoArgumentNames {
	String firstArgumentName();

	String secondArgumentName();

	String methodName(); // Method name

}
