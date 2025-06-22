package dev.ofekmalka.core.assertion;

import dev.ofekmalka.core.assertion.If.Condition;
import dev.ofekmalka.core.assertion.If.Condition.WillCondition.OrGet;
import dev.ofekmalka.core.assertion.If.GetSoft;
import dev.ofekmalka.core.assertion.If.SoftCondition;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.Nothing;

public interface CheckFlow {

	public interface HardCheckFlow extends CheckFlow { //

		interface IfStep<T> extends HardCheckFlow {
			Condition<T> isNull();

			Condition<T> isNull(String name);

			Condition<T> isNonNull();

			Condition<T> isNonNull(String name);

			Condition<T> is(Predicate<T> condition);

			Condition<T> is(Predicate<T> condition, String errorMessage);

			Condition<T> isNot(Predicate<T> condition);

			Condition<T> isNot(Predicate<T> condition, String errorMessage);

			Condition<T> isNonNullWithCustomErrorMessage(String castumErrorMessage);

		}

		interface ConditionStep<T> extends HardCheckFlow {
			Condition<T> andIs(Predicate<T> condition);

			Condition<T> andIs(Predicate<T> condition, String errorMessage);

			Condition<T> andIsNot(Predicate<T> condition);

			Condition<T> andIsNot(Predicate<T> condition, String errorMessage);

			Condition<T> andSupplierIs(Supplier<Boolean> condition);

			Condition<T> andSupplierIs(Supplier<Boolean> condition, String errorMessage);

			Condition<T> andSupplierIsNot(Supplier<Boolean> condition);

			Condition<T> andSupplierIsNot(Supplier<Boolean> condition, String errorMessage);

			Condition<T> orIs(Predicate<T> condition);

			Condition<T> orIs(Predicate<T> condition, String errorMessage);

			Condition<T> orIsNot(Predicate<T> condition);

			Condition<T> orIsNot(Predicate<T> condition, String errorMessage);

			Condition<T> orSupplierIs(Supplier<Boolean> condition);

			Condition<T> orSupplierIs(Supplier<Boolean> condition, String errorMessage);

			Condition<T> orSupplierIsNot(Supplier<Boolean> condition);

			Condition<T> orSupplierIsNot(Supplier<Boolean> condition, String errorMessage);

			<U> Condition<T> orOtherObjectIsNotNull(U value);

			<U> Condition<T> orOtherObjectIsNotNull(U value, String name);

			<U> Condition<T> andOtherObjectIsNotNull(U value);

			<U> Condition<T> andOtherObjectIsNotNull(U value, String name);

			// =====
			<U> Condition<T> andOtherObjectIsNotNullWithErrorMessage(U value, String errorMessage);

			<U> Condition<U> andForOtherCondition(Condition<U> condition);

			dev.ofekmalka.core.assertion.If.Condition.WillCondition<T> will();

		}

		interface ActionStep<T> extends HardCheckFlow {
			Result<T> getResult();

			<U> OrGet<U> returnValue(Supplier<U> returnValue);

			<W> OrGet<W> mapTo(Function<T, W> f);

			<W> OrGet<W> flatMapTo(Function<T, Result<W>> f);

			<W> Result<W> thenGetOrElseThrowException(Supplier<W> defaultValue);

			<W> Result<String> assertionsElementInSoftMode(Function<GetSoft<T>, SoftCondition<W>> function);

			Nothing thenApprovedOrElseThrowException();

			Nothing thenUnapprovedOrElseThrowException(Supplier<RuntimeException> defaultValue);

			Nothing getOrElseThrow(Supplier<RuntimeException> defaultValue);

			T getValueOrElseThrow(Supplier<RuntimeException> defaultValue);

			boolean allTheRequirementsFit();

			Result<T> thenGetOrElseThrowException();

		}

		interface OrElseStep<T> extends HardCheckFlow {

			Result<T> getResult();

			<W> OrGet<W> mapTo(Function<T, W> f);

			<W> OrGet<W> flatMapTo(Function<T, Result<W>> f);

			T orThrowException(Supplier<RuntimeException> defaultValue);

			If<T> andAfterThisTransformationCheckIfTransformedObject();

		}
	}

	public interface SoftCheckFlow extends CheckFlow {

		interface SoftCheck<T> extends SoftCheckFlow {

			SoftCondition<T> is(Predicate<T> condition);

			SoftCondition<T> is(Predicate<T> condition, String errorMessage);

			SoftCondition<T> isNot(Predicate<T> condition);

			SoftCondition<T> isNot(Predicate<T> condition, String errorMessage);

		}

		interface SoftConditionStep<T> extends SoftCheckFlow {

			GetSoft<T> andAfterThisTransformationCheckIfTransformedObject();

			<W> SoftCondition<W> returnValue(Supplier<W> f);

			<W> SoftCondition<W> mapTo(Function<T, W> f);

			<W> SoftCondition<W> flatMapTo(Function<T, Result<W>> f);

			<U> SoftCondition<T> andForOtherCondition(Condition<U> condition);

			<U> SoftCondition<T> andForOtherSoftCondition(SoftCondition<U> condition);

			<U> SoftCondition<T> andOtherObjectIsNotNull(U value);

			<U> SoftCondition<T> andOtherObjectIsNotNull(U value, String name);

			SoftCondition<T> andIs(Predicate<T> condition);

			SoftCondition<T> andIs(Predicate<T> condition, String errorMessage);

			SoftCondition<T> andSupplierIs(Supplier<Boolean> condition);

			SoftCondition<T> andSupplierIs(Supplier<Boolean> condition, String errorMessage);

			SoftCondition<T> andIsNot(Predicate<T> condition);

			SoftCondition<T> andIsNot(Predicate<T> condition, String errorMessage);

			SoftCondition<T> andSupplierIsNot(Supplier<Boolean> condition);

			SoftCondition<T> andSupplierIsNot(Supplier<Boolean> condition, String errorMessage);

			List<Result<T>> getErrors();

			dev.ofekmalka.core.assertion.If.SoftCondition.WillCondition<T> will();

		}

		interface SoftResult<T> extends SoftCheckFlow {

			boolean isThereAnyError();

			List<Result<T>> getErrors();

			T thenGetOrElse(Supplier<T> defaultValue);

			Result<T> thenGetOrErrorMessage();

			<W> Result<W> thenGetDefaultValueOrErrorMessage(W defaultValue);

			Result<String> generateResultErrorIfExists();

		}

	}

}
