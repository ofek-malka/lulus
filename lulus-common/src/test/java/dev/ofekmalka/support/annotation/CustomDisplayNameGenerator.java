package dev.ofekmalka.support.annotation;

import java.lang.reflect.Method;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayNameGenerator;

public class CustomDisplayNameGenerator extends DisplayNameGenerator.Standard {

	@Override
	public String generateDisplayNameForClass(final Class<?> testClass) {
		return this.generateDisplayName()//
				.apply(super.generateDisplayNameForClass(testClass));
	}

	@Override
	public String generateDisplayNameForNestedClass(final Class<?> nestedClass) {
		return this.generateDisplayName()//
				.apply(super.generateDisplayNameForNestedClass(nestedClass));

	}

	@Override
	public String generateDisplayNameForMethod(final Class<?> testClass, final Method testMethod) {
		return this.generateDisplayName().apply(testMethod.getName());

	}

	private Function<String, String> generateDisplayName() {
		return name -> name.replaceAll("(\\p{Upper})", " $1").toLowerCase();
	}

}