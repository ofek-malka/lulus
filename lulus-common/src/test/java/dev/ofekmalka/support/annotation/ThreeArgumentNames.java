package dev.ofekmalka.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ThreeArgumentNames {
	String firstArgumentName();

	String secondArgumentName();

	String thirdArgumentName();

	String methodName(); // Method name

}
