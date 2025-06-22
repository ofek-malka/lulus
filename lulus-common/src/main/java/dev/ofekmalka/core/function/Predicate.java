package dev.ofekmalka.core.function;

import dev.ofekmalka.core.assertion.result.Result;

@FunctionalInterface
public interface Predicate<T> extends Function<T, Boolean> {

	// Alternatively:
	// Boolean evaluate(T t);
	// Boolean check(T t);
	// ... or any other name that makes sense in your context

	default Boolean matches(final T arg) {//
		return apply(arg);
	}

	default Result<Boolean> safeMatchesOn(final T arg) {//
		return this.safeMatchesOnWithArgName(arg, "argument");
	}

	default Result<Boolean> safeMatchesOnWithArgName(final T arg, final String argName) {//
		return safeApplyOnWithArgName(arg, argName);
	}
}
