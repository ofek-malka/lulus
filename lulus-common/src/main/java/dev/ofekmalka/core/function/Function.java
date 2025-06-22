package dev.ofekmalka.core.function;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;

@FunctionalInterface
public interface Function<T, U> extends CheckedOperation {

	U apply(T arg);

	default Result<U> safeApplyOn(final T arg) {//
		return this.safeApplyOnWithArgName(arg, "argument");
	}

	default Result<U> safeApplyOnWithArgName(final T arg, final String argName) {//

		if (arg == null)
			return Result.failure(If.formatErrorMessageForNullValue(argName));

		try {
			final var result = this.apply(arg);//

			if (result == null)
				return Result.failure(ERROR_MESSAGE_NULL_RESULT.getMessage());
			return Result.success(result); //
		} //
		catch (final Exception e) {
			return Result.failure(ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage());
		}

	}

	default <V> Function<V, U> compose(final Function<V, T> f) {
		return x -> this.apply(f.apply(x));
	}

	default <V> Function<T, V> andThen(final Function<U, V> f) {
		return x -> f.apply(this.apply(x));
	}

}
