package dev.ofekmalka.tools.tuple;

import java.util.Objects;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition.WillCondition.OrGet;

public final class Tuple3<A, B, C> {

	private final A value1;
	private final B value2;
	private final C value3;

	public Tuple3(final A value1, final B value2, final C value3) {
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}

	public static <A, B, C> OrGet<Tuple3<A, B, C>> of(final A value1, final B value2, final C value3) {
		return If.givenObject(value1)//
				.isNonNull("value1 argument in Tuple3")//
				.andOtherObjectIsNotNull(value2, "value2 argument in Tuple3")//
				.andOtherObjectIsNotNull(value3, "value3 argument in Tuple3")//

				.will()//
				.returnValue(() -> new Tuple3<>(value1, value2, value3));
	}

	@Override
	public int hashCode() {
		return Objects.hash(value1, value2, value3);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof final Tuple3 other))
			return false;
		return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2)
				&& Objects.equals(value3, other.value3);
	}

	// Getters
	public A value1() {
		return value1;
	}

	public B value2() {
		return value2;
	}

	public C value3() {
		return value3;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", value1, value2, value3);
	}

}
