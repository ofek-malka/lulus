package dev.ofekmalka.tools.tuple;

import java.util.Objects;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition.WillCondition.OrGet;

public final class Tuple5<A, B, C, D, E> {

	private final A value1;
	private final B value2;
	private final C value3;
	private final D value4;
	private final E value5;

	public Tuple5(final A value1, final B value2, final C value3, final D value4, final E value5) {
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.value5 = value5;
	}

	public static <A, B, C, D, E> OrGet<Tuple5<A, B, C, D, E>> of(final A value1, final B value2, final C value3,
			final D value4, final E value5) {
		return If.givenObject(value1)//
				.isNonNull("value1 argument in Tuple5").andOtherObjectIsNotNull(value2, "value2 argument in Tuple5")
				.andOtherObjectIsNotNull(value3, "value3 argument in Tuple5")
				.andOtherObjectIsNotNull(value4, "value4 argument in Tuple5")
				.andOtherObjectIsNotNull(value5, "value5 argument in Tuple5")//
				.will()//
				.returnValue(() -> new Tuple5<>(value1, value2, value3, value4, value5));
	}

	@Override
	public int hashCode() {
		return Objects.hash(value1, value2, value3, value4, value5);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof final Tuple5 other))
			return false;
		return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2)
				&& Objects.equals(value3, other.value3) && Objects.equals(value4, other.value4)
				&& Objects.equals(value5, other.value5);
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

	public D value4() {
		return value4;
	}

	public E value5() {
		return value5;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s, %s, %s)", value1, value2, value3, value4, value5);
	}
}
