package dev.ofekmalka.core.assertion;

import java.util.Objects;

import dev.ofekmalka.core.assertion.If.Condition;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.assertion.result.Result.ErrorMessages;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.Nothing;
import dev.ofekmalka.tools.tuple.Tuple2;

public final class PreconditionedProcess<A> {

	private final Result<A> objectResult;

	private PreconditionedProcess(final Result<A> list) {
		this.objectResult = list;
	}

	public static PreconditionedProcess<String> startProcess() {
		return new PreconditionedProcess<>(Result.success("we are in successful starting point"));
	}

	public static <A> PreconditionedProcess<A> from(final Result<A> objectResult) {

		final var validatedObjectResult = //
				If.givenObject(objectResult)//
						.isNot(Result::isEmpty)//
						.will()//
						.getValueOrElseThrow(
								() -> new RuntimeException("objectResult in PreconditionedProcess is empty kind"));

		return new PreconditionedProcess<>(

				validatedObjectResult);
	}

	public static <A> PreconditionedProcess<A> fromValue(final A object) {
		return new PreconditionedProcess<>(Result.success(object));
	}

	public static <T> ResultStage<T> fromValidatedSupplier(final Supplier<Result<T>> supplierObject) {
		return convertToResultHandler(supplierObject.safeGet().flatMap(t -> t));//

	}

	public static <T> ResultStage<T> fromSupplier(final Supplier<T> supplierObject) {
		return convertToResultHandler(supplierObject.safeGet());//

	}

	private static <T> ResultStage<T> convertToResultHandler(final Result<T> re) {
		return new ResultStage<>(re);//

	}

	public <T> ResultStage<T> processOperation(final Function<A, T> f) {
		return convertToResultHandler(objectResult.map(f));

	}

	public <T> ResultStage<T> processOperationWithResult(final Function<A, Result<T>> f) {
		return convertToResultHandler(objectResult.flatMap(f));

	}

	public <T> PreconditionStage<A> checkCondition(final Function<A, Condition<T>> condition) {

		final var preconditionResultTuple = objectResult.flatMap(list -> {

			final var afterValidateArgs = condition.apply(list).will()//
					.returnValue(() -> Nothing.INSTANCE)//
					.getResult()//
			;

			return Tuple2.<Result<Nothing>, A>of(afterValidateArgs, list).getResult();
		});

		return new PreconditionStage<>(preconditionResultTuple);
	}

	public <T> PreconditionStage<A> checkCondition(final Supplier<Condition<T>> conditionSupplier) {

		final var preconditionResultTuple = objectResult.flatMap(list -> {

			final var afterValidateArgs = conditionSupplier//
					.get()//
					.will()//
					.returnValue(() -> Nothing.INSTANCE)

					.getResult()//
			;

			return Tuple2.<Result<Nothing>, A>of(afterValidateArgs, list).getResult();
		});

		return new PreconditionStage<>(preconditionResultTuple);

	}

	public <T> PreconditionStage<A> checkResult(final Supplier<Result<T>> conditionSupplier) {

		final var preconditionResultTuple = objectResult.flatMap(list -> {

			final var afterValidateArgs = conditionSupplier.get()

					.map(__ -> Nothing.INSTANCE);//

			return Tuple2.<Result<Nothing>, A>of(afterValidateArgs, list).getResult();
		});

		return new PreconditionStage<>(preconditionResultTuple);

	}

	public static class PreconditionStage<A> {

		private final Result<Tuple2<Result<Nothing>, A>> preconditionResult;

		private PreconditionStage(final Result<Tuple2<Result<Nothing>, A>> r) {
			this.preconditionResult = r;
		}

		public <T> ResultStage<T> processOperationBySupplierResult(final Supplier<Result<T>> f) {

			final var mappedResult = preconditionResult

					.map(Tuple2::state)
					.flatMap(afterArgValidation -> afterArgValidation.flatMap(ignore -> f.safeGet().flatMap(t -> t)));

			return convertToResultHandler(mappedResult);

		}

		public <T> ResultStage<T> processOperationBySupplierObject(final Supplier<T> f) {

			final var mappedResult = preconditionResult

					.map(Tuple2::state)
					.flatMap(afterArgValidation -> afterArgValidation.flatMap(ignore -> f.safeGet()));

			return convertToResultHandler(mappedResult);

		}

		public <T> ResultStage<T> processOperationWithResult(final Function<A, Result<T>> f) {

			final var mappedResult = preconditionResult.flatMap(t -> {

				final var afterArgValidation = t.state();
				final var list = t.value();

				return afterArgValidation.flatMap(ignore -> f.apply(list));
			});

			return convertToResultHandler(mappedResult);

		}

		public <T> ResultStage<T> processOperation(final Function<A, T> f) {

			final var mappedResult = preconditionResult.flatMap(t -> {

				final var afterArgValidation = t.state();
				final var list = t.value();
				return afterArgValidation.map(ignore -> f.apply(list));
			});

			return convertToResultHandler(mappedResult);

		}

	}

	public static class ResultStage<A> {

		private final Result<A> r;

		private ResultStage(final Result<A> r) {
			this.r = r;
		}

		public static <A> ResultStage<A> from(final Result<A> r) {
			return new ResultStage<>(r);
		}

		public ResultWrapper<A> andMakeStackTraceUnderTheName(final String methodName) {
			return

			new ResultWrapper<>(r.prependMethodNameToFailureMessage(methodName));

		}

		public ResultWrapper<A> withoutStackTrace() {
			return new ResultWrapper<>(r);

		}

	}

	public static class ResultWrapper<A> {

		private final Result<A> r;

		private ResultWrapper(final Result<A> r) {
			this.r = r;
		}

		// TODO it's unsafe!!
		public <Q> Q mapTo(final Function<Result<A>, Q> mapperToObjectThatContainResultAsField) {

			if (Objects.isNull(mapperToObjectThatContainResultAsField))
				throw ErrorMessages//
						.inClass("PreconditionedProcess.ResultWrapper")//
						.forArgumentName("mapperToObjectThatContainResultAsField")//
						.inMethod("convertTo")//
						.shouldNotBeNull()//
						.asFailure()//
						.failureValue();

			final var result = mapperToObjectThatContainResultAsField.safeApplyOn(r);
			if (result.isFailure())
				throw ErrorMessages//
						.inClass("PreconditionedProcess.ResultWrapper")//
						.forArgumentName("mapperToObjectThatContainResultAsField")//
						.inMethod("convertTo")//
						.withThisErrorExplanation(result.failureValue().getMessage()).asFailure()//
						.failureValue();

			return result.successValue();

		}

		public A getOrConvertToFailureState(final Function<String, A> mapperErrorMessageInFailureState) {

			if (Objects.isNull(mapperErrorMessageInFailureState))
				throw ErrorMessages//
						.inClass("PreconditionedProcess.ResultWrapper")//
						.forArgumentName("mapperErrorMessageInFailureState")//
						.inMethod("getOrConvertToFailureState")//
						.shouldNotBeNull()//
						.asFailure()//
						.failureValue();

			final var result = mapperErrorMessageInFailureState.safeApplyOn("");// dummy string
			if (result.isFailure())
				throw ErrorMessages//
						.inClass("PreconditionedProcess.ResultWrapper")//
						.forArgumentName("mapperErrorMessageInFailureState")//
						.inMethod("getOrConvertToFailureState")//
						.withThisErrorExplanation(result.failureValue().getMessage()).asFailure()//
						.failureValue();

			return r.getOrCreateFailureInstanceWithMessage(mapperErrorMessageInFailureState);
		}

		public Result<A> getResultProccess() {
			return r;

		}

	}

}
