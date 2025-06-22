package dev.ofekmalka.core.assertion;

import java.util.Objects;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.assertion.result.Result.ErrorMessages;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.Nothing;

//a comprehensive overview of your Java class `If<T>
public final class If<T> implements CheckFlow.HardCheckFlow.IfStep<T> {

	// Define error message constants

	public static final String MUST_NOT_THROW_EXCEPTION = "must not throw exception.";
	public static final String MUST_THROW_EXCEPTION = "must throw exception.";
	public static final String MUST_NOT_BE_NULL = "must not be null.";
	public static final String MUST_BE_NULL = "must be null.";
	public static final String VALUE_FITS_ALL_REQUIREMENTS = "The object fits all the requirements";

	private final Result<T> ifValue;

	private If(final T value) {
		this.ifValue = Result.success(value);
	}

	private If(final Result<T> value) {
		this.ifValue = value;
	}

	public static <T> If<T> givenObject(final T value) {
		return new If<>(value);
	}

	public static String formatErrorMessageForNullValue(final String name) {
		return formatMessage(name, MUST_NOT_BE_NULL);
	}

	private static String formatMessage(final String argumentName, final String message) {
		return argumentName + " " + message;
	}

	public static Condition<Boolean> isItTrue(final boolean value) {
		final var rValue = Result.success(value);
		return Condition.of(rValue, rValue.validate(Boolean.TRUE::equals)//

		);
	}

	public static Condition<Boolean> isItTrue(final boolean value, final String errorMessageOnFailure) {
		final var rValue = Result.success(value);
		return Condition.of(rValue, rValue.validate(Boolean.TRUE::equals, errorMessageOnFailure)

		);
	}

	public static Condition<Boolean> isItFalse(final boolean value) {
		final var rValue = Result.success(value);
		return Condition.of(rValue, rValue.validate(Boolean.FALSE::equals));
	}

	public static Condition<Boolean> isItFalse(final boolean value, final String errorMessageOnFailure) {
		final var rValue = Result.success(value);
		return Condition.of(rValue, rValue.validate(Boolean.FALSE::equals, errorMessageOnFailure));
	}

	// ==================================================

	@Override
	public Condition<T> isNull(final String name) {
		return Condition.of(ifValue, ifValue.validate(Objects::isNull, If.formatMessage(name, If.MUST_BE_NULL))

		);
	}

	@Override
	public Condition<T> isNull() {
		return this.isNull("value");
	}

	@Override
	public Condition<T> isNonNull(final String name) {
		return Condition.of(ifValue, ifValue.validate(Objects::nonNull, If.formatMessage(name, If.MUST_NOT_BE_NULL))

		);
	}

	@Override
	public Condition<T> isNonNullWithCustomErrorMessage(final String castumErrorMessage) {
		return Condition.of(ifValue, ifValue.validate(Objects::nonNull, castumErrorMessage));
	}

	@Override
	public Condition<T> isNonNull() {
		return this.isNonNull("value");
	}

	@Override
	public Condition<T> is(final Predicate<T> condition) {
		return Condition.of(ifValue, ifValue.validate(condition));
	}

	@Override
	public Condition<T> is(final Predicate<T> condition, final String errorMessage) {
		return Condition.of(ifValue, ifValue.validate(condition, errorMessage));
	}

	@Override
	public Condition<T> isNot(final Predicate<T> condition) {
		return Condition.of(ifValue, ifValue.reject(condition));
	}

	@Override
	public Condition<T> isNot(final Predicate<T> condition, final String errorMessage) {
		return Condition.of(ifValue, ifValue.reject(condition, errorMessage));
	}

	public final static class Condition<T> implements CheckFlow.HardCheckFlow.ConditionStep<T> {
		private final Result<T> originalValue;
		private final Result<T> conditionResult;

		public Condition(final Result<T> ifValue, final Result<T> obj) {
			this.originalValue = ifValue;
			this.conditionResult = obj;
		}

		private static <T> Condition<T> of(final Result<T> ifValue, final Result<T> obj) {
			return new Condition<>(ifValue, obj);
		}

		@Override
		public <U> Condition<U> andForOtherCondition(final Condition<U> condition) {
			return Condition.of(originalValue.flatMap(obj -> condition.originalValue),
					conditionResult.flatMap(obj -> condition.conditionResult));
		}

		@Override
		public <U> Condition<T> andOtherObjectIsNotNull(final U value) {
			return this.andOtherObjectIsNotNull(value, "value");
		}

		@Override
		public <U> Condition<T> andOtherObjectIsNotNull(final U value, final String name) {
			return this.andSupplierIsNot(() -> Objects.isNull(value), If.formatMessage(name, If.MUST_NOT_BE_NULL));
		}

		@Override
		public <U> Condition<T> andOtherObjectIsNotNullWithErrorMessage(final U value, final String errorMessage) {
			return this.andSupplierIsNot(() -> Objects.isNull(value), errorMessage);
		}

		@Override
		public <U> Condition<T> orOtherObjectIsNotNull(final U value) {
			return this.orOtherObjectIsNotNull(value, "value");
		}

		@Override
		public <U> Condition<T> orOtherObjectIsNotNull(final U value, final String name) {
			return this.orSupplierIsNot(() -> Objects.isNull(value), If.formatMessage(name, If.MUST_NOT_BE_NULL));
		}

		@Override
		public Condition<T> andIs(final Predicate<T> condition) {
			return Condition.of(originalValue, conditionResult.validate(condition));
		}

		@Override
		public Condition<T> andIs(final Predicate<T> condition, final String errorMessage) {
			return Condition.of(originalValue, conditionResult.validate(condition, errorMessage));
		}

		@Override
		public Condition<T> andSupplierIs(final Supplier<Boolean> condition) {
			return this.andIs(o -> condition.get());
		}

		@Override
		public Condition<T> andSupplierIs(final Supplier<Boolean> condition, final String errorMessage) {
			return this.andIs(o -> condition.get(), errorMessage);
		}

//--------------------------------------------------------------------------------------------------------
		@Override
		public Condition<T> andIsNot(final Predicate<T> condition) {
			return Condition.of(originalValue, conditionResult.reject(condition));
		}

		@Override
		public Condition<T> andIsNot(final Predicate<T> condition, final String errorMessage) {
			return Condition.of(originalValue, conditionResult.reject(condition, errorMessage));
		}

		@Override
		public Condition<T> andSupplierIsNot(final Supplier<Boolean> condition) {
			return this.andIsNot(o -> condition.get());
		}

		@Override
		public Condition<T> andSupplierIsNot(final Supplier<Boolean> condition, final String errorMessage) {
			return this.andIsNot(o -> condition.get(), errorMessage);
		}

//--------------------------------------------------------------------------------------------------------
		@Override
		public Condition<T> orIs(final Predicate<T> condition) {
			return Condition.of(originalValue, this.conditionResult.or(() -> this.originalValue.validate(condition)));
		}

		@Override
		public Condition<T> orIs(final Predicate<T> condition, final String errorMessage) {

			return Condition.of(originalValue,

					this.conditionResult.or(() -> this.originalValue.validate(condition, errorMessage)));
		}

		@Override
		public Condition<T> orSupplierIs(final Supplier<Boolean> condition) {
			return this.orIs(o -> condition.get());
		}

		@Override
		public Condition<T> orSupplierIs(final Supplier<Boolean> condition, final String errorMessage) {

			return this.orIs(o -> condition.get(), errorMessage);
		}

//--------------------------------------------------------------------------------------------------------
		@Override
		public Condition<T> orIsNot(final Predicate<T> condition) {
			return Condition.of(originalValue, this.conditionResult.or(() -> this.originalValue.reject(condition)));
		}

		@Override
		public Condition<T> orIsNot(final Predicate<T> condition, final String errorMessage) {
			return Condition.of(originalValue,
					this.conditionResult.or(() -> this.originalValue.reject(condition, errorMessage)));
		}

		@Override
		public Condition<T> orSupplierIsNot(final Supplier<Boolean> condition) {
			return this.orIsNot(o -> condition.get());
		}

		@Override
		public Condition<T> orSupplierIsNot(final Supplier<Boolean> condition, final String errorMessage) {
			return this.orIsNot(o -> condition.get(), errorMessage);
		}

		@Override
		public WillCondition<T> will() {
			return new WillCondition<>(originalValue, conditionResult);
		}

		public final static class WillCondition<T> implements CheckFlow.HardCheckFlow.ActionStep<T> {
			protected final Result<T> originalValue;
			protected final Result<T> conditionResult;

			public WillCondition(final Result<T> originalValue, final Result<T> conditionResult) {
				this.originalValue = originalValue;
				this.conditionResult = conditionResult;
			}

			@Override
			public <U> OrGet<U> returnValue(final Supplier<U> returnValue) {
				return new OrGet<>(conditionResult.map(i -> returnValue.get()));
			}

			@Override
			public boolean allTheRequirementsFit() {
				return conditionResult.isSuccess();
			}

			@Override
			public Result<T> getResult() {
				return conditionResult;
			}

			@Override
			public <W> Result<String> assertionsElementInSoftMode(
					final Function<GetSoft<T>, SoftCondition<W>> function) {

				return this.getResult()//
						.flatMap(elemenet -> {
							final GetSoft<T> softIntial = If.givenNonNullForSoftValidation(elemenet);
							return function.apply(softIntial)

									.will()//
									.generateResultErrorIfExists();
						});// ;
			}

			@Override
			public <W> OrGet<W> mapTo(final Function<T, W> f) {
				return new OrGet<>(this.conditionResult.map(f));
			}

			@Override
			public <W> OrGet<W> flatMapTo(final Function<T, Result<W>> f) {
				return new OrGet<>(this.conditionResult.flatMap(f));
			}

			@Override
			public T getValueOrElseThrow(final Supplier<RuntimeException> defaultValue) {

				if (conditionResult.isFailure())

					throw Result.of(defaultValue).flatMap(Result::failureFromRuntimeException).failureValue();
				return originalValue.successValue();

			}

			@Override
			public Nothing getOrElseThrow(final Supplier<RuntimeException> defaultValue) {

				if (conditionResult.isFailure())
					throw Result.of(defaultValue).flatMap(Result::failureFromRuntimeException).failureValue();
				return Nothing.INSTANCE;

			}

			@Override
			public Nothing thenUnapprovedOrElseThrowException(final Supplier<RuntimeException> defaultValue) {
				if (conditionResult.isSuccess())
					throw defaultValue.get();
				return Nothing.INSTANCE;

			}

			@Override
			public Nothing thenApprovedOrElseThrowException() {
				if (conditionResult.isFailure())
					throw conditionResult.failureValue();
				return Nothing.INSTANCE;

			}

			@Override
			public <W> Result<W> thenGetOrElseThrowException(final Supplier<W> defaultValue) {
				if (conditionResult.isFailure())
					throw conditionResult.failureValue();
				return Result.of(defaultValue);

			}

			@Override
			public Result<T> thenGetOrElseThrowException() {
				if (conditionResult.isFailure())
					throw conditionResult.failureValue();
				return originalValue;

			}

			public final static class OrGet<T> implements CheckFlow.HardCheckFlow.OrElseStep<T> {
				private final Result<T> value;

				public OrGet(final Result<T> value) {
					this.value = value;
				}

				public T orGet(final Supplier<T> defaultValue) {
					return value.getOrElse(defaultValue);
				}

				@Override
				public T orThrowException(final Supplier<RuntimeException> defaultValue) {

					if (value.mapFailure(defaultValue.get()).isFailure())
						throw defaultValue.get();
					return value.successValue();
				}

				@Override
				public Result<T> getResult() {
					return value;
				}

				@Override
				public If<T> andAfterThisTransformationCheckIfTransformedObject() {
					return new If<>(value);
				}

				@Override
				public <W> OrGet<W> mapTo(final Function<T, W> f) {
					return new OrGet<>(this.value.map(f));
				}

				@Override
				public <W> OrGet<W> flatMapTo(final Function<T, Result<W>> f) {
					return new OrGet<>(this.value.flatMap(f));
				}

			}

		}

	}

	// very rare situation!!!
	public static <T> GetSoft<T> givenNonNullForSoftValidation(final T value) {

		Objects.requireNonNull(value, If.MUST_NOT_BE_NULL);
		return GetSoft.of(Result.success(value), List.emptyList());
	}

	public final static class GetSoft<T> implements CheckFlow.SoftCheckFlow.SoftCheck<T> {
		private final Result<T> originalValue;
		private final List<Result<T>> errors;

		private GetSoft(final Result<T> originalValue, final List<Result<T>> errors) {
			this.originalValue = originalValue;
			this.errors = errors; // Initialize the error list
		}

		private static <T> GetSoft<T> of(final Result<T> originalValue, final List<Result<T>> errors) {
			return new GetSoft<>(originalValue, errors);
		}

		@Override
		public SoftCondition<T> is(final Predicate<T> condition) {
			final var result = originalValue.validate(condition);
			final var addedAtMostOneMoreError = result.map(ifThereIsNotErrorThen -> errors)
					.getOrElse(errors.cons(result));

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);
		}

		@Override
		public SoftCondition<T> is(final Predicate<T> condition, final String errorMessage) {

			final var result = originalValue.validate(condition, errorMessage);
			final var addedAtMostOneMoreError = result.map(ifThereIsNotErrorThen -> errors)
					.getOrElse(errors.cons(result));

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);
		}

		@Override
		public SoftCondition<T> isNot(final Predicate<T> condition) {

			final var result = originalValue.reject(condition);
			final var addedAtMostOneMoreError = result.map(ifThereIsNotErrorThen -> errors)
					.getOrElse(errors.cons(result));

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);
		}

		@Override
		public SoftCondition<T> isNot(final Predicate<T> condition, final String errorMessage) {

			final var result = originalValue.reject(condition, errorMessage);
			final var addedAtMostOneMoreError = result.map(ifThereIsNotErrorThen -> errors)
					.getOrElse(errors.cons(result));

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);

		}

	}

	public final static class SoftCondition<T> implements CheckFlow.SoftCheckFlow.SoftConditionStep<T> {
		private final Result<T> originalValue;
		private final List<Result<T>> errors;

		private SoftCondition(final Result<T> ifValue, final List<Result<T>> errors) {
			this.originalValue = ifValue;
			this.errors = errors; // Initialize the error list
		}

		@Override
		public List<Result<T>> getErrors() {
			return errors.reverse();
		}

		private static <T> SoftCondition<T> of(final Result<T> ifValue, final List<Result<T>> errors) {
			return new SoftCondition<>(ifValue, errors);
		}

		@Override
		public GetSoft<T> andAfterThisTransformationCheckIfTransformedObject() {
			return new GetSoft<>(originalValue, errors);
		}

		@Override
		public <W> SoftCondition<W> returnValue(final Supplier<W> f) {
			return mapTo(i -> f.get());
		}

		@Override
		public <W> SoftCondition<W> mapTo(final Function<T, W> f) {
			final var result = originalValue.map(f);

			If.isItTrue(result.isFailure())

					.will().getOrElseThrow(() -> //
					ErrorMessages.inClass("SoftCondition")//
							.forArgumentName("f")//
							.inMethod("mapTo")//
							.withThisErrorExplanation(result.failureValue().getMessage())//
							.asFailure()//
							.failureValue()//
					);

			return new SoftCondition<>(result, errors.map(Result::mapFailureForOtherObject));
		}

		@Override
		public <W> SoftCondition<W> flatMapTo(final Function<T, Result<W>> f) {
			final var result = originalValue.flatMap(f);

			If.isItTrue(result.isNotSuccess())// maybe Result.empty
					.will().getOrElseThrow(() -> //
					ErrorMessages.inClass("SoftCondition")//
							.forArgumentName("f")//
							.inMethod("flatMapTo")//
							.withThisErrorExplanation("Argument 'f' maybe return or just create \n"
									+ "failure situation by throwing exception, returning null or failure result or Result.empty.")//
							.asFailure()//
							.failureValue()//
					);

			return new SoftCondition<>(result, errors.map(Result::mapFailureForOtherObject));

		}

		@Override
		public <U> SoftCondition<T> andForOtherCondition(final Condition<U> condition) {

			final var result = condition.will().getResult();//

			final var mergedErrors = //
					If.isItTrue(result.isFailure())//
							.will().returnValue(() -> this.errors.addElement(result.mapFailureForOtherObject()))//
							.orGet(() -> this.errors);//

			return SoftCondition.of(originalValue, mergedErrors);
		}

		@Override
		public <U> SoftCondition<T> andForOtherSoftCondition(final SoftCondition<U> condition) {
			final var mergedErrors = condition.errors.<Result<T>>map(Result::mapFailureForOtherObject)
					.addList(this.errors);
			return SoftCondition.of(originalValue, mergedErrors);
		}

		@Override
		public <U> SoftCondition<T> andOtherObjectIsNotNull(final U value) {

			return this.andOtherObjectIsNotNull(value, "value");
		}

		@Override
		public <U> SoftCondition<T> andOtherObjectIsNotNull(final U value, final String name) {

			return this.andSupplierIsNot(() -> Objects.isNull(value), If.formatMessage(name, If.MUST_NOT_BE_NULL));
		}

		@Override
		public SoftCondition<T> andIs(final Predicate<T> condition) {

			final var result = originalValue.validate(condition);
			final var addedAtMostOneMoreError = result.map(ifThereIsNotErrorThen -> errors)
					.getOrElse(errors.cons(result));

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);
		}

		@Override
		public SoftCondition<T> andIs(final Predicate<T> condition, final String errorMessage) {

			final var result = originalValue.validate(condition, errorMessage)

			;
			final var addedAtMostOneMoreError = result

					.map(ifThereIsNotErrorThen -> errors).getOrElse(errors.cons(result.extractOnlyUserError()

							.prependMethodNameToFailureMessage("andIs"))

					);

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);
		}

		@Override
		public SoftCondition<T> andSupplierIs(final Supplier<Boolean> condition) {
			return this.andIs(o -> condition.get());
		}

		@Override
		public SoftCondition<T> andSupplierIs(final Supplier<Boolean> condition, final String errorMessage) {
			return this.andIs(o -> condition.get(), errorMessage);
		}

//--------------------------------------------------------------------------------------------------------
		@Override
		public SoftCondition<T> andIsNot(final Predicate<T> condition) {

			final var result = originalValue.reject(condition);
			final var addedAtMostOneMoreError = result.map(ifThereIsNotErrorThen -> errors)
					.getOrElse(errors.cons(result));

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);
		}

		@Override
		public SoftCondition<T> andIsNot(final Predicate<T> condition, final String errorMessage) {
			final var result = originalValue.reject(condition, errorMessage);
			final var addedAtMostOneMoreError = result.map(ifThereIsNotErrorThen -> errors)
					.getOrElse(errors.cons(result));

			return SoftCondition.of(originalValue, addedAtMostOneMoreError);
		}

		@Override
		public SoftCondition<T> andSupplierIsNot(final Supplier<Boolean> condition) {
			return this.andIsNot(o -> condition.get());
		}

		@Override
		public SoftCondition<T> andSupplierIsNot(final Supplier<Boolean> condition, final String errorMessage) {
			return this.andIsNot(o -> condition.get(), errorMessage);
		}

//--------------------------------------------------------------------------------------------------------
		@Override
		public WillCondition<T> will() {
			return new WillCondition<>(originalValue, errors.reverse());
		}

		public final static class WillCondition<T> implements CheckFlow.SoftCheckFlow.SoftResult<T> {
			private final Result<T> originalValue;
			private final List<Result<T>> errors;

			private WillCondition(final Result<T> ifValue, final List<Result<T>> errors) {
				this.originalValue = ifValue;
				this.errors = errors; // Initialize the error list
			}

			@Override
			public List<Result<T>> getErrors() {
				return errors.reverse();
			}

			@Override
			public T thenGetOrElse(final Supplier<T> defaultValue) {
				return originalValue.getOrElse(defaultValue);
			}

			@Override
			public Result<T> thenGetOrErrorMessage() {
				return thenGetDefaultResultValueOrErrorMessage(originalValue);//

			}

			@Override
			public <W> Result<W> thenGetDefaultValueOrErrorMessage(final W defaultValue) {
				return thenGetDefaultResultValueOrErrorMessage(//
						Result.of(defaultValue, "defaultValue in 'thenGetDefaultValueOrErrorMessage' is null"));//
			}

			@Override
			public Result<String> generateResultErrorIfExists() {
				return thenGetDefaultResultValueOrErrorMessage(//
						Result.<String>success(If.VALUE_FITS_ALL_REQUIREMENTS));//

			}

			private <W> Result<W> thenGetDefaultResultValueOrErrorMessage(final Result<W> defaultValue) {
				final var orderedErrors = this.getErrors().map(error -> error.failureValue().getMessage());
				return If.givenObject(orderedErrors)//
						.is(l -> l.isEmpty().successValue())//
						.will().returnValue(() -> defaultValue)//
						.orGet(() -> Result.<W>failure(

								orderedErrors.size().successValue() == 1
										? "This is the error message:\n" + orderedErrors.mkStr("\n").successValue()
												+ "\n"
										: "Those are the error messages:\n" + orderedErrors.zipWithPosition(1)
												.map(t -> t.value() + ") " + t.state()).mkStr("\n").successValue()
												+ "\n"));

			}

			@Override
			public boolean isThereAnyError() {
				return errors.isNotEmpty().successValue();
			}

		}

	}

}
