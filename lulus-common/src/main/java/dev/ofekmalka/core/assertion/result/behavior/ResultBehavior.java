package dev.ofekmalka.core.assertion.result.behavior;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.Nothing;

/**
 * The ResultBehavior interface provides a well-structured, extensible framework
 * for working with results, supporting both basic and advanced operations. The
 * clear separation between foundational and advanced operations ensures that
 * the library can be flexible, with rich support for error handling, recursion,
 * and result transformations.
 */
public interface ResultBehavior {

	interface Operations<T> extends ResultBehavior {

		public interface ErrorHandling<T> extends Operations<T> {
			Result<T> mapFailure(String failureMessage, Exception exception);

			Result<T> mapFailure(String failureMessage);

			Result<T> mapFailure(Exception exception);

			Result<T> prependFailureMessage(String additionalFailureMessage);

			Result<T> prependMethodNameToFailureMessage(final String methodName);

			// TODO
			Result<T> removeCalledByMethodPrefixes();

			<F> Result<F> mapFailureForOtherObject();

			T getOrCreateFailureInstanceWithMessage(final Function<String, T> failureHandler);

			Result<T> extractOnlyUserError();
		}

		public interface Mapping<T> extends Operations<T> {
			<U> Result<U> map(Function<T, U> mapper);

			<U> Result<U> flatMap(Function<T, Result<U>> flatMapper);
		}

		public interface Filtering<T> extends Operations<T> {
			Result<T> validate(Function<T, Boolean> predicate);

			Result<T> validate(Function<T, Boolean> predicate, String failureMessage);

			Result<T> reject(Function<T, Boolean> predicate);

			Result<T> reject(Function<T, Boolean> predicate, String failureMessage);
		}

		public interface SpecialCase<T> extends Operations<T> {
			Result<Nothing> mapEmpty();
		}

		public interface SuccessFailureQueries<T> extends Operations<T> {
			boolean isSuccess();

			boolean isFailure();

			boolean isEmpty();
		}

		public interface RetrievingValues<T> extends Operations<T> {
			T getOrElse(final T defaultValue);

			T getOrElse(final Supplier<T> defaultValueSupplier);

			T successValue();

			RuntimeException failureValue();
		}



		interface Conditional<T> extends Operations<T> {
			Result<T> or(final Supplier<Result<T>> alternativeIfTrue);

			Result<T> and(final Supplier<Result<T>> next);

			Result<T> orElse(final Supplier<Result<T>> defaultValue);
		}

		// Equality and comparison-related operations
		interface Equality<T> extends Operations<T> {
			boolean isNotSuccess();

			boolean isEqualTo(final Object o);
		}
		// This is where advanced operations will be divided logically.

	}

}
