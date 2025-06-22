package dev.ofekmalka.core.function;

import dev.ofekmalka.core.assertion.result.Result;

@FunctionalInterface
public interface Supplier<T> extends CheckedOperation {

	T get();

	default Result<T> safeGet() {//

		try {//
			final var result = this.get();//

			if (result == null)

				return Result.failure(ERROR_MESSAGE_NULL_RESULT.getMessage());
			return Result.success(result);//
		}

		catch (final Exception e) {
			return Result.failure(ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage());
		}

	}

}
