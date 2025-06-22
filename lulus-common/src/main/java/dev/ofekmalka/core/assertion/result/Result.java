package dev.ofekmalka.core.assertion.result;

import java.util.Objects;
import java.util.regex.Pattern;

import dev.ofekmalka.core.assertion.BasicValidator;
import dev.ofekmalka.core.assertion.result.behavior.ResultBehavior;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.ConsoleOutputEffect;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.helper.Nothing;

/**
 * The {@code Result<T>} class is a functional programming construct designed to
 * represent the outcome of a computation, allowing for a distinction between
 * successful results and failure cases.
 *
 * In functional programming, purity is often emphasized, but real-world
 * scenarios frequently require some pragmatic safety measures, such as
 * conditional checks or explicit exception handling. The {@code Result<T>}
 * class strikes a balance between these needs, ensuring correctness while
 * maintaining the elegance of functional design.
 *
 * This class serves as a robust solution for modeling outcomes without relying
 * on traditional exception handling. It encapsulates success and failure in a
 * controlled manner, allowing the calling code to deal with these outcomes in a
 * type-safe way, without cluttering the business logic with error handling.
 *
 * The primary goal of this class is not to eliminate all conditional checks or
 * exceptions but to contain them in safe, controlled areas where they enforce
 * correctness. By doing so, the design ensures that the most critical parts of
 * the program remain functional and easy to reason about, while still
 * safeguarding against potential errors.
 *
 * @param <T> the type of the value in the successful outcome
 */

public sealed abstract class Result<T> implements //
		ResultBehavior.Operations.Filtering<T>, //
		ResultBehavior.Operations.Mapping<T>, //
		ResultBehavior.Operations.SpecialCase<T>, //
		ResultBehavior.Operations.SuccessFailureQueries<T>, //
		ResultBehavior.Operations.RetrievingValues<T>, //
		ResultBehavior.Operations.ErrorHandling<T>, //
		ResultBehavior.Operations.Conditional<T>, //
		ResultBehavior.Operations.Equality<T>, //
		ConsoleOutputEffect {

	public Result<Result<T>> lift() {
		return Result.success(this);

	}

	public String getFailureMessageOrDefault() {
		return this.lift().map(r -> r.failureValue().getMessage())//
				.getOrElse("state of Result value is successful");

	}

	public Result<Result<T>> mapToNested() {
		return this.map(i -> this);
	}

	public static <T> Result<T> of(final T value) {
		final var defaultFailureMessageForNullValue = ErrorMessages//
				.inClass("Result")//
				.forArgumentName("value")//
				.inStaticMethod("of")//
				.shouldNotBeNull()//
				.asErrorMessage();

		return Result.of(value, defaultFailureMessageForNullValue);//

	}

	public static <T> Result<T> of(final T value, final String failureMessageForNullValue) {

		if (Objects.isNull(failureMessageForNullValue))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("failureMessageForNullValue")//
					.inStaticMethod("of")//
					.shouldNotBeNull()//
					.asFailure();//

		if (failureMessageForNullValue.isBlank())
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("failureMessageForNullValue")//
					.inStaticMethod("of")//
					.shouldNotBe("blank")//
					.asFailure();//

		if (Objects.isNull(value))
			return new Failure<>(failureMessageForNullValue);
		return success(value);
	}

	public static <T> Result<T> of(final Supplier<T> supplier) {
		final var defaultErrorMessageIfSupplierProduceNullValue = ErrorMessages//
				.inClass("Result")//
				.forArgumentName("supplier")//
				.inStaticMethod("of")//
				.shouldNot("provide null value")//
				.asErrorMessage();
		return of(supplier, defaultErrorMessageIfSupplierProduceNullValue);
	}

	public static <T> Result<T> of(final Supplier<T> supplier, //
			final String errorMessageIfSupplierProduceNullValue) {//
		final var errorSupplierCannotBeNullFailure = ErrorMessages//
				.inClass("Result")//
				.forArgumentName("supplier")//
				.inStaticMethod("of")//
				.shouldNotBeNull()//
				.<T>asFailure();

		final var errorMessageCannotBeNullFailure = ErrorMessages//
				.inClass("Result")//
				.forArgumentName("errorMessageIfCallableProduceNullValue")//
				.inStaticMethod("of")//
				.shouldNotBeNull()//
				.<T>asFailure();

		final var errorMessageCannotBeBlankFailure = ErrorMessages//
				.inClass("Result")//
				.forArgumentName("errorMessageIfCallableProduceNullValue")//
				.inStaticMethod("of")//
				.shouldNotBe("blank")//
				.<T>asFailure();

		final var errorSupplierProduceRuntimeExceptionExplanation = ErrorMessages//
				.inClass("Result")//
				.forArgumentName("supplier")//
				.inStaticMethod("of")//
				.withThisErrorExplanation("A runtime exception was thrown from the callable arg, which is undesirable!")//
				.asErrorMessage();

		if (Objects.isNull(errorMessageIfSupplierProduceNullValue))
			return errorMessageCannotBeNullFailure;

		if (errorMessageIfSupplierProduceNullValue.isBlank())
			return errorMessageCannotBeBlankFailure;

		if (Objects.isNull(supplier))
			return errorSupplierCannotBeNullFailure;

		try {
			final var value = supplier.get();
			return Objects.isNull(value)//
					? new Failure<>(errorMessageIfSupplierProduceNullValue) //
					: success(value);
		} catch (final Exception e) {
			return Result.failure(errorSupplierProduceRuntimeExceptionExplanation);
		}

	}

	public static <T> Result<T> failure(final String message) {

		if (Objects.isNull(message))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("message")//
					.inStaticMethod("failure")//
					.shouldNotBeNull()//
					.asFailure();//
		if (message.isBlank())
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("message")//
					.inStaticMethod("failure")//
					.shouldNotBe("blank")//
					.asFailure();//
		return new Failure<>(message);

	}

	public static <T> Result<T> failure(final String message, final Exception exception) {
		if (Objects.isNull(message))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("message")//
					.inStaticMethod("failure")//
					.shouldNotBeNull()//
					.asFailure();//
		if (message.isBlank())
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("message")//
					.inStaticMethod("failure")//
					.shouldNotBe("blank")//
					.asFailure();//

		if (Objects.isNull(exception))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("exception")//
					.inStaticMethod("failure")//
					.shouldNotBeNull()//
					.asFailure();//

		return new Failure<>(new IllegalStateException(message, exception));//

	}

	public static <T> Result<T> failure(final Exception exception) {

		if (Objects.isNull(exception))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("exception")//
					.inStaticMethod("failure")//
					.shouldNotBeNull()//
					.asFailure();//
		return new Failure<>(exception);

	}

	public static <T> Result<T> failureFromRuntimeException(final RuntimeException runtimeException) {

		if (Objects.isNull(runtimeException))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("runtimeException")//
					.inStaticMethod("failure")//
					.shouldNotBeNull()//
					.asFailure();//
		return new Failure<>(runtimeException);

	}

	public static <T> Result<T> success(final T value) {
		return new Success<>(value);

	}

	public static <T> Result<T> empty() {
		return new Empty<>();
	}

	public static <A, B> Function<Result<A>, Result<B>> lift(final Function<A, B> mapper) {
		final var providedResultArgumentCannotBeNullFailure = ErrorMessages//
				.inClass("Result")//
				.forArgumentWhich("be invoked by apply")//
				.inStaticMethod("lift")//
				.shouldNotBeNull()//
				.<B>asFailure();

		return result -> {//
			if (Objects.isNull(result))
				return providedResultArgumentCannotBeNullFailure;
			return result.map(mapper);//
		};//
	}

	public static <A, B, C> Function<Result<A>, Function<Result<B>, Result<C>>> lift2(//
			final Function<A, Function<B, C>> combiner) {//
		return firstResult -> secondResult -> {
			if (Objects.isNull(firstResult))
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentWhich("be invoked first by apply")//
						.inStaticMethod("lift2")//
						.shouldNotBeNull()//
						.<C>asFailure();
			if (Objects.isNull(secondResult))
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentWhich("be invoked second by apply")//
						.inStaticMethod("lift2")//
						.shouldNotBeNull()//
						.<C>asFailure();

			return firstResult.map(combiner).flatMap(secondResult::map);

		};
	}

	public static <A, B, C, D> Function<Result<A>, Function<Result<B>, Function<Result<C>, Result<D>>>>//
			lift3(final Function<A, Function<B, Function<C, D>>> combiner) {//

		return firstResult -> secondResult -> thirdResult -> {//
			if (Objects.isNull(thirdResult))
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentWhich("be invoked third by apply")//
						.inStaticMethod("lift3")//
						.shouldNotBeNull()//
						.<D>asFailure();

			return lift2(combiner)//
					.apply(firstResult)//
					.apply(secondResult)// Result<Function<C, D>>
					.flatMap(thirdResult::map);
		};

	}



	@Override
	public Result<T> or(final Supplier<Result<T>> alternativeIfTrue) {
		if (Objects.isNull(alternativeIfTrue))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("alternativeIfTrue")//
					.inMethod("or")//
					.shouldNotBeNull()//
					.asFailure();

		final var result = alternativeIfTrue.safeGet();
		if (result.isFailure())
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("alternativeIfTrue")//
					.inMethod("or")//

					.withThisErrorExplanation(result.failureValue().getMessage())//
					.asFailure();

		return isFailure() && alternativeIfTrue.get().isSuccess() ? alternativeIfTrue.get() : this;
	}

	@Override
	public Result<T> and(final Supplier<Result<T>> next) {
		if (Objects.isNull(next))
			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("next")//
					.inMethod("and")//
					.shouldNotBeNull()//
					.asFailure();

		final var result = next.safeGet();
		if (result.isFailure())

			return ErrorMessages//
					.inClass("Result")//
					.forArgumentName("next")//
					.inMethod("and")//

					.withThisErrorExplanation(result.failureValue().getMessage())//
					.asFailure();

		return isSuccess() ? next.get() : this;
	}

	@Override
	public Result<T> orElse(final Supplier<Result<T>> defaultValue) {
		return this.map(x -> this).getOrElse(defaultValue);
	}

	@Override
	public Result<T> prependMethodNameToFailureMessage(final String methodName) {
		return prependFailureMessage(String.format(ErrorTracker.CALL_FORMAT, methodName));
	}

	/**
	 * ⚠️ Use with caution! This method should only be called when you are certain
	 * that the result is a failure. Calling it on a success result will likely
	 * cause unintended behavior.
	 */
	@Override
	public <F> Result<F> mapFailureForOtherObject() {
		return Result.<F>failureFromRuntimeException(failureValue());

	}

	@Override
	public boolean isNotSuccess() {
		return Boolean.logicalOr(isFailure(), isEmpty());
	}

	// TODO: UNSAFE!!!!!!
	@Override
	public T getOrCreateFailureInstanceWithMessage(final Function<String, T> failureHandler) {

		if (Objects.isNull(failureHandler))
			throw ErrorMessages//
					.inClass("Result")//
					.forArgumentName("failureHandler")//
					.inMethod("getOrCreateFailureInstanceWithMessage")//
					.shouldNotBeNull()//
					.asFailure()//
					.failureValue();

		final var result = failureHandler.safeApplyOn("dummyMessage");
		if (result.isFailure())
			throw ErrorMessages//
					.inClass("Result")//
					.forArgumentName("failureHandler")//
					.inMethod("getOrCreateFailureInstanceWithMessage")//
					.withThisErrorExplanation(result.failureValue().getMessage())//
					.asFailure()//
					.failureValue();

		if (isEmpty())
			throw ErrorMessages//
					.inClass("Result")//
					.forArgumentName("failureHandler")//
					.inMethod("getOrCreateFailureInstanceWithMessage")//
					.withThisErrorExplanation("Called on a Empty instance")//
					.asFailure()//
					.failureValue();

		return isFailure() ? failureHandler.apply(failureValue().getMessage())

				: successValue();

	}

	@Override
	public boolean isEqualTo(final Object o) {
		return equals(o);
	}

	// TODO: UNSAFE!!!!!!
	public OnSuccess<T> casesForProvidedHashCode() {
		return fun -> className -> //
		{

			final var validationClassName = BasicValidator.ClassNameValidator.validate(className)

					.will().getResult();

			if (validationClassName.isFailure())
				throw ErrorMessages//
						.inClass("Result")//
						.forArgumentName("caseSensitiveClassName")//
						.inMethod("onOtherOptionProvideClassName")//
						.withThisErrorExplanation(validationClassName.failureValue().getMessage()).asFailure()//
						.failureValue();//

			return isSuccess() //
					? this.map(fun).successValue() //
					: isFailure() //
							? Objects.hash(className, failureValue().getMessage()) //
							: Objects.hash(className, 0); //
		};

	}

	public interface OnSuccess<T> {

		default OnOtherOption<T> onSuccess(final Supplier<Integer> fun) {
			return onSuccess(ignore -> fun.get());

		}

		default OnOtherOption<T> onSuccess(final int hashCode) {
			return onSuccess(ignore -> hashCode);
		}

		OnOtherOption<T> onSuccess(Function<T, Integer> fun);
	}

	public interface OnOtherOption<T> {
		int onOtherOptionProvideClassName(String caseSensitiveClassName);
	}

	// =============================================================================================
	// =============================================================================================
	// =============================================================================================

	public final static class Failure<T> extends Result<T> {
		/**
		 *
		 * The first question you might ask is, “What type should I use?” Obviously, two
		 * different types come to mind: String and RuntimeException. A string can hold
		 * an error message, as an exception does, but many error situations will
		 * produce an exception. Using a String as the type carried by the Left value
		 * will force you to ignore the relevant information in the exception and use
		 * only the included message. It’s thus better to use RuntimeException as the
		 * Left value. That way, if you only have a message, you can wrap it into an
		 * exception.
		 */
		private final RuntimeException exception;

		/**
		 *
		 * Constructors are private. If a Failure is constructed with a message, it’s
		 * wrapped into a RuntimeException (more specifically, the IllegalStateException
		 * subclass)
		 *
		 */
		private Failure(final String message) {
			this.exception = new IllegalStateException(message);

		}

		private Failure(final RuntimeException e) {
			this.exception = e;
		}

		private Failure(final Exception e) {
			this.exception = new IllegalStateException(e);
		}

		@Override
		public Result<T> validate(final Function<T, Boolean> predicate) {
			return this;
		}

		@Override
		public Result<T> validate(final Function<T, Boolean> predicate, final String failureMessage) {
			return this;
		}

		@Override
		public Result<T> reject(final Function<T, Boolean> predicate) {
			return this;
		}

		@Override
		public Result<T> reject(final Function<T, Boolean> predicate, final String failureMessage) {
			return this;
		}

		@Override
		public <U> Result<U> map(final Function<T, U> mapper) {
			return new Failure<>(this.exception);
		}

		@Override
		public <U> Result<U> flatMap(final Function<T, Result<U>> flatMapper) {
			return new Failure<>(this.exception);
		}

		@Override
		public Result<T> mapFailure(final String failureMessage, final Exception exception) {
			return failure(failureMessage, exception);
		}

		@Override
		public Result<T> mapFailure(final String failureMessage) {
			return failure(failureMessage);
		}

		@Override
		public Result<T> mapFailure(final Exception exception) {
			return failure(exception);

		}

		@Override
		public Result<T> prependFailureMessage(final String additionalFailureMessage) {
			if (Objects.isNull(additionalFailureMessage))
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentName("additionalFailureMessage")//
						.inMethod("prependFailureMessage")//
						.shouldNotBeNull()//
						.<T>asFailure();

			if (additionalFailureMessage.isBlank())
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentName("additionalFailureMessage")//
						.inMethod("prependFailureMessage")//
						.shouldNotBe("blank")//
						.<T>asFailure();

			final var originalMessage = this.exception.getMessage();

			return new Failure<>(additionalFailureMessage + "\n" + originalMessage);

		}

		@Override
		public Result<Nothing> mapEmpty() {
			return this.mapFailureForOtherObject();
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isFailure() {
			return true;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public T getOrElse(final T defaultValue) {
			return defaultValue;
		}

		@Override
		public T getOrElse(final Supplier<T> defaultValueSupplier) {

			if (Objects.isNull(defaultValueSupplier))
				throw ErrorMessages//
						.inClass("Result")//
						.forArgumentName("defaultValueSupplier")//
						.inMethod("getOrElse")//
						.shouldNotBeNull()//
						.asFailure()//
						.failureValue();//

			final var result = defaultValueSupplier.safeGet();
			if (result.isFailure())
				throw ErrorMessages//
						.inClass("Result")//
						.forArgumentName("defaultValueSupplier")//
						.inMethod("getOrElse")//
						.withThisErrorExplanation(result.failureValue().getMessage())//
						.<T>asFailure()//
						.failureValue();

			return defaultValueSupplier.get();
		}

		@Override
		public T successValue() {
			throw ErrorMessages//
					.inClass("Result")//
					.inMethod("successValue")//
					.withThisErrorExplanation(
							"Called on a Failure instance.\nThe failure message:\n " + this.exception.getMessage())//
					.<T>asFailure()//
					.failureValue();
		}

		@Override
		public RuntimeException failureValue() {
			return this.exception;
		}

		@Override
		public boolean equals(final Object obj) {
			// Check if the references are the same
			if (this == obj)
				return true;

			// Check if obj is an instance of Failure
			if (!(obj instanceof Failure))
				return false;

			// Cast obj to Failure<T> (using wildcard to allow any generic type)
			final Failure<?> other = (Failure<?>) obj;

			// Check if the exception messages are the same
			return getClass() == other.getClass()//
					&& Objects.equals(exception.getMessage(), other.exception.getMessage());//
		}

		@Override
		public int hashCode() {
			// Return a hash code based on the class and exception message
			return Objects.hash(getClass(), exception.getMessage());
		}

		@Override
		public String toString() {
			return String.format("Failure(%s)", failureValue());
		}

		@Override
		public Result<T> removeCalledByMethodPrefixes() {
			final var TRACE_PREFIX_PATTERN = Pattern.compile("(?m)^Called by the method name '.*?' ->\\s*");

			return new Failure<>(TRACE_PREFIX_PATTERN.matcher(exception.getMessage()).replaceAll(""));

		}

		@Override
		public Result<T> extractOnlyUserError() {
			return new Failure<>(extractOnlyUserError(this.failureValue().getMessage()));

		}

		private static String extractOnlyUserError(

				final String fullErrorMessage) {

			final var SYSTEM_TRACE_PATTERN = Pattern
					.compile("(?m)^(In class|For argument|In method|In static method|For argument which).*?->\\s*\\n?")

			;

			return SYSTEM_TRACE_PATTERN.matcher(fullErrorMessage).replaceAll("")

					.replace("With this error explanation:\n", "")
					// Remove system trace lines
					.strip(); // Clean leading/trailing spaces
		}

		// extractOnlyUserError(final String fullErrorMessage)
	}

	final static class Empty<T> extends Result<T> {

		public Empty() {
		}

		@Override
		public Result<T> validate(final Function<T, Boolean> predicate) {
			return this;
		}

		@Override
		public Result<T> validate(final Function<T, Boolean> predicate, final String failureMessage) {
			return this;
		}

		@Override
		public Result<T> reject(final Function<T, Boolean> predicate) {
			return this;
		}

		@Override
		public Result<T> reject(final Function<T, Boolean> predicate, final String failureMessage) {
			return this;
		}

		@Override
		public <U> Result<U> map(final Function<T, U> mapper) {
			return empty();
		}

		@Override
		public <U> Result<U> flatMap(final Function<T, Result<U>> flatMapper) {
			return empty();
		}

		@Override
		public Result<T> mapFailure(final String failureMessage, final Exception exception) {
			return failure(failureMessage, exception);
		}

		@Override
		public Result<T> mapFailure(final String failureMessage) {
			return failure(failureMessage);

		}

		@Override
		public Result<T> mapFailure(final Exception exception) {
			return failure(exception);

		}

		@Override
		public Result<T> prependFailureMessage(final String additionalFailureMessage) {
			return this;
		}

		@Override
		public Result<Nothing> mapEmpty() {
			return success(Nothing.INSTANCE);
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isFailure() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public T getOrElse(final T defaultValue) {
			return defaultValue;
		}

		@Override
		public T getOrElse(final Supplier<T> defaultValueSupplier) {

			if (Objects.isNull(defaultValueSupplier))
				throw ErrorMessages//
						.inClass("Result")//
						.forArgumentName("defaultValueSupplier")//
						.inMethod("getOrElse")//
						.shouldNotBeNull()//
						.asFailure()//
						.failureValue();//

			final var result = defaultValueSupplier.safeGet();
			if (result.isFailure())
				throw ErrorMessages//
						.inClass("Result")//
						.forArgumentName("defaultValueSupplier")//
						.inMethod("getOrElse")//
						.withThisErrorExplanation(result.failureValue().getMessage())//
						.<T>asFailure()//
						.failureValue();

			return defaultValueSupplier.get();
		}

		@Override
		public T successValue() {
			throw ErrorMessages//
					.inClass("Result")//
					.inMethod("successValue")//
					.withThisErrorExplanation("Called on a Empty instance")//
					.<T>asFailure()//
					.failureValue();

		}

		@Override
		public RuntimeException failureValue() {
			throw ErrorMessages//
					.inClass("Result")//
					.inMethod("failureValue")//
					.withThisErrorExplanation("Called on a Empty instance")//
					.<T>asFailure()//
					.failureValue();
		}

		@Override
		public boolean equals(final Object obj) {
			// Check if the objects are the same reference
			if (this == obj)
				return true;

			// Check if obj is an instance of Empty
			if (!(obj instanceof Empty))
				return false;

			// Ensure that the generic types match
			final Empty<?> other = (Empty<?>) obj;
			return getClass() == other.getClass(); // Ensure same class and same type parameter
		}

		@Override
		public int hashCode() {
			// Use the class to generate a consistent hash code
			return Objects.hash(getClass());
		}

		@Override
		public String toString() {
			return "Empty()";
		}

		@Override
		public Result<T> removeCalledByMethodPrefixes() {
			return this;
		}

		@Override
		public Result<T> extractOnlyUserError() {
			return this;

		}

	}

	final static class Success<T> extends Result<T> {

		private final T value;

		public Success(final T value) {
			this.value = value;
		}

		@Override
		public Result<T> validate(final Function<T, Boolean> predicate) {
			return this.validate(predicate, GeneralMessage.UNMATCHED_PREDICATE.asErrorMessage());
		}

		@Override
		public Result<T> validate(final Function<T, Boolean> predicate, final String failureMessage) {

			final var predicateErrorMessage = ErrorMessages//
					.inClass("Result")//
					.forArgumentName("predicate")//
					.inMethod("validate");

			if (Objects.isNull(predicate))
				return predicateErrorMessage//
						.shouldNotBeNull()//
						.<T>asFailure();

			if (Objects.isNull(failureMessage))
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentName("failureMessage")//
						.inMethod("validate")//
						.shouldNotBeNull()//
						.<T>asFailure();

			if (failureMessage.isBlank())
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentName("failureMessage")//
						.inMethod("validate")//
						.shouldNotBe("blank")//
						.<T>asFailure();

			try {
				final var isCoonditionTrue = predicate.apply(value);//
				return Objects.isNull(isCoonditionTrue)//
						? predicateErrorMessage//
								.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
								.<T>asFailure()//
						: isCoonditionTrue //
								? this//
								: failure(failureMessage);//
			} catch (final Exception e) {
				return Result.failure(predicateErrorMessage//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage());
			}

		}

		@Override
		public Result<T> reject(final Function<T, Boolean> predicate) {
			return this.reject(predicate, GeneralMessage.UNMATCHED_PREDICATE.asErrorMessage());
		}

		@Override
		public Result<T> reject(final Function<T, Boolean> predicate, final String failureMessage) {

			final var predicateErrorMessage = ErrorMessages//
					.inClass("Result")//
					.forArgumentName("predicate")//
					.inMethod("reject");

			if (Objects.isNull(predicate))
				return predicateErrorMessage//
						.shouldNotBeNull()//
						.<T>asFailure();

			if (Objects.isNull(failureMessage))
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentName("failureMessage")//
						.inMethod("reject")//
						.shouldNotBeNull()//
						.<T>asFailure();

			if (failureMessage.isBlank())
				return ErrorMessages//
						.inClass("Result")//
						.forArgumentName("failureMessage")//
						.inMethod("reject")//
						.shouldNotBe("blank")//
						.<T>asFailure();

			try {
				final var isCoonditionTrue = predicate.apply(value);//
				return Objects.isNull(isCoonditionTrue)//
						? predicateErrorMessage//
								.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
								.<T>asFailure()//
						: isCoonditionTrue //
								? failure(failureMessage) //
								: this;//
			} catch (final Exception e) {
				return Result.failure(predicateErrorMessage//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage());
			}

		}

		@Override
		public <U> Result<U> map(final Function<T, U> mapper) {

			final var mapperErrorMessage = ErrorMessages//
					.inClass("Result")//
					.forArgumentName("mapper")//
					.inMethod("map");

			if (Objects.isNull(mapper))
				return mapperErrorMessage//
						.shouldNotBeNull()//
						.<U>asFailure();

			final var errorMapperProduceNullValueFailure = mapperErrorMessage//
					.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
					.<U>asFailure();

			final var errorMapperProduceRuntimeExceptionMessage = mapperErrorMessage//
					.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
					.asErrorMessage();

			try {
				final var rValue = mapper.apply(value);//
				return Objects.isNull(rValue)//
						? errorMapperProduceNullValueFailure//
						: success(rValue);//
			} catch (final Exception e) {
				return Result.failure(errorMapperProduceRuntimeExceptionMessage);
			}

		}

		@Override
		public <U> Result<U> flatMap(final Function<T, Result<U>> flatMapper) {
			final var flatMapperErrorMessage = ErrorMessages//
					.inClass("Result")//
					.forArgumentName("flatMapper")//
					.inMethod("flatMap");

			if (Objects.isNull(flatMapper))
				return flatMapperErrorMessage//
						.shouldNotBeNull()//
						.<U>asFailure();

			final var errorFlatMapperProduceRuntimeExceptionMessage = flatMapperErrorMessage//
					.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
					.asErrorMessage();
			final var errorFlatMapperProduceNullValueFailure = flatMapperErrorMessage//
					.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
					.<U>asFailure();

			try {
				final var rValue = flatMapper.apply(value);//

				return Objects.isNull(rValue)//
						? errorFlatMapperProduceNullValueFailure//
						: rValue;//
			} catch (final Exception e) {
				return Result.failure(errorFlatMapperProduceRuntimeExceptionMessage);
			}

		}

		@Override
		public Result<T> mapFailure(final String failureMessage, final Exception exception) {
			return this;
		}

		@Override
		public Result<T> mapFailure(final String failureMessage) {
			return this;

		}

		@Override
		public Result<T> mapFailure(final Exception exception) {
			return this;

		}

		@Override
		public Result<T> prependFailureMessage(final String additionalFailureMessage) {
			return this;

		}

		@Override
		public Result<Nothing> mapEmpty() {
			return Result.<Nothing>failure("Not empty");
		}

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public boolean isFailure() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public T getOrElse(final T defaultValue) {
			return successValue();
		}

		@Override
		public T getOrElse(final Supplier<T> defaultValueSupplier) {
			return successValue();
		}

		@Override
		public T successValue() {
			return this.value;
		}

		@Override
		public RuntimeException failureValue() {
			throw ErrorMessages//
					.inClass("Result")//
					.inMethod("failureValue")//
					.withThisErrorExplanation("Called on a Success instance")//
					.asFailure()//
					.failureValue();

		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;

			if (!(obj instanceof Success))
				return false;

			final Success<?> other = (Success<?>) obj;

			return getClass() == other.getClass() && Objects.equals(value, other.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(getClass(), value);
		}

		@Override
		public String toString() {
			return String.format("Success(%s)", this.successValue().toString());
		}

		@Override
		public Result<T> removeCalledByMethodPrefixes() {
			return this;

		}

		@Override
		public Result<T> extractOnlyUserError() {
			return this;

		}

	}

	public enum GeneralMessage {
		UNMATCHED_PREDICATE("Unmatched predicate with no error message provided.");

		private final String errorMessage;

		GeneralMessage(final String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public String asErrorMessage() {
			return errorMessage;
		}

		public Result<String> asFailure() {
			return Result.failure(errorMessage);
		}
	}

	public final static class ErrorMessages {

		public static InClass inClass(final String className) {
			return new InClass(String.format("In class '%s' ->\n", className));
		}

		public static class InClass {
			private final String className;

			public InClass(final String className) {
				this.className = className;
			}

			public ArgumentError forArgumentName(final String argumentName) {
				return new ArgumentError(String.format("%sFor argument '%s' ->\n", className, argumentName));
			}

			public ArgumentError.ContextError inMethod(final String methodName) {
				return new ArgumentError.ContextError(String.format("%sIn method '%s' ->\n", className, methodName));
			}

			public ArgumentError.ContextError inStaticMethod(final String methodName) {
				return new ArgumentError.ContextError(
						String.format("%sIn static method '%s' ->\n", className, methodName));
			}

			public ArgumentError forArgumentWhich(final String descriptionOfArgument) {
				return new ArgumentError(
						String.format("%sFor argument which '%s' ->\n", className, descriptionOfArgument));
			}

			String getClassName() {
				return className;
			}

			public static class ArgumentError {
				private final String baseMessage;

				public ArgumentError(final String baseMessage) {
					this.baseMessage = baseMessage;
				}

				public ContextError inMethod(final String methodName) {
					return new ContextError(String.format("%sIn method '%s' ->\n", baseMessage, methodName));
				}

				public ContextError inStaticMethod(final String methodName) {
					return new ContextError(String.format("%sIn static method '%s' ->\n", baseMessage, methodName));
				}

				public static class ContextError {
					private final String formattedMessage;

					public ContextError(final String formattedMessage) {
						this.formattedMessage = formattedMessage;
					}

					public ErrorResult shouldNotBeNull() {
						return new ErrorResult(formattedMessage + "It should not be null\n");
					}

					public ErrorResult shouldNot(final String explanation) {
						return new ErrorResult(formattedMessage + "It should not " + explanation + "\n");
					}

					public ErrorResult shouldNotBe(final String explanation) {
						return new ErrorResult(formattedMessage + "It should not be " + explanation + "\n");
					}

					public ErrorResult withThisErrorExplanation(final String errorExplanation) {
						return new ErrorResult(formattedMessage + "With this error explanation:\n"
								+ String.format("%s\n", errorExplanation));
					}

					public static class ErrorResult {
						private final String errorMessage;

						public ErrorResult(final String errorMessage) {
							this.errorMessage = errorMessage;
						}

						public String asErrorMessage() {
							return errorMessage;
						}

						public <W> Result<W> asFailure() {
							return Result.<W>failure(errorMessage);
						}

						@Override
						public String toString() {
							throw new UnsupportedOperationException(
									"toString() is not supported. Use 'asErrorMessage()' instead.");
						}
					}
				}
			}
		}
	}

}
