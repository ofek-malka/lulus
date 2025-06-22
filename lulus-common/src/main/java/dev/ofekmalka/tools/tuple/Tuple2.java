package dev.ofekmalka.tools.tuple;

import java.util.Objects;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition.WillCondition.OrGet;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.assertion.result.Result.ErrorMessages;
import dev.ofekmalka.core.function.Function;

/**
 *
 * You're absolutely right! The concept of a **state** makes sense in `Tuple2`
 * when dealing with accumulators, but in `Tuple3` and above, the first value
 * should just be called `value1`, followed by `value2`, `value3`, etc. This
 * aligns with the idea that larger tuples are primarily used for grouping
 * multiple values rather than maintaining a stateful accumulator.
 *
 *
 * This makes the API more intuitive. Let me know if you want me to implement
 * `Tuple3` and `Tuple4` with this approach! 🚀
 */
public final class Tuple2<S, A> {

	private final S state;
	private final A value;

	private Tuple2(final S state, final A value) {
		this.state = state;//
		this.value = value;//
	}

	private static <S, A> Tuple2<S, A> unsafeCreate(final S state, final A value) {
		return new Tuple2<>(state, value);
	}

	public static <S, A> OrGet<Tuple2<S, A>> of(final S state, final A value) {
		return If.givenObject(state)//
				.isNonNull("state argument in Tuple2")//
				.andOtherObjectIsNotNull(value, "value argument in Tuple2")//
				.will()//
				.returnValue(() -> Tuple2.<S, A>unsafeCreate(state, value))

		;

	}

	public static <S> OrGet<Tuple2<S, S>> pairBothAsStateAndValue(final S element) {
		return Tuple2.<S, S>of(element, element);

	}

	public S state() {
		return state;
	}

	public A value() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", state, value);
	}

	public Tuple2<A, S> swap() {
		return unsafeCreate(value, state);
	}

	public boolean isStateAndValueEqual() {
		return this.equals(this.swap());
	}

	public boolean isEqualState(final S otherstate) {
		return state.equals(otherstate);
	}

	public boolean isEqualValue(final A otherValue) {
		return value.equals(otherValue);
	}

	public <A2, S2> Result<Tuple2<S2, A2>> map(final Function<S, S2> f2, final Function<A, A2> f1) {
		final var newState = Result.success(state).map(f2)//
				.mapToNested()//
				.getOrCreateFailureInstanceWithMessage(errorMessage -> //
				ErrorMessages//
						.inClass("Tuple2")//
						.forArgumentName("f2")//
						.inMethod("map")//
						.withThisErrorExplanation(errorMessage)//
						.<S2>asFailure()//

				);

		final var newValue = Result.success(value).map(f1)//
				.mapToNested()//
				.getOrCreateFailureInstanceWithMessage(errorMessage -> //
				ErrorMessages//
						.inClass("Tuple2")//
						.forArgumentName("f1")//
						.inMethod("map")//
						.withThisErrorExplanation(errorMessage)//
						.<A2>asFailure()//

				);

		return newState.flatMap(state -> newValue.map(value -> unsafeCreate(state, value)));

	}

	public <A2> Result<Tuple2<S, A2>> mapValue(final Function<A, A2> f) {
		final var newValue = Result.success(value).map(f)//
				.mapToNested()//
				.getOrCreateFailureInstanceWithMessage(errorMessage -> //
				ErrorMessages//
						.inClass("Tuple2")//
						.forArgumentName("f")//
						.inMethod("mapValue")//
						.withThisErrorExplanation(errorMessage)//
						.<A2>asFailure()//

				);

		return newValue.map(v -> unsafeCreate(state, v));
	}

	public <S2> Result<Tuple2<S2, A>> mapState(final Function<S, S2> f) {

		final var newState = Result.success(state).map(f)//
				.mapToNested()//
				.getOrCreateFailureInstanceWithMessage(errorMessage -> //
				ErrorMessages//
						.inClass("Tuple2")//
						.forArgumentName("f")//
						.inMethod("mapState")//
						.withThisErrorExplanation(errorMessage)//
						.<S2>asFailure()//

				);

		return newState.map(s -> unsafeCreate(s, value));
	}

	@Override
	public int hashCode() {
		return Objects.hash(state, value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof final Tuple2 other))
			return false;
		return Objects.equals(state, other.state)

				&& Objects.equals(value, other.value);
	}

}
