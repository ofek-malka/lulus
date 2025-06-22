package dev.ofekmalka.tools.tuple;

import java.util.Objects;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition.WillCondition.OrGet;

public final class Tuple4<A, B, C, D> {

	private final A value1;
	private final B value2;
	private final C value3;
	private final D value4;

	public Tuple4(final A value1, final B value2, final C value3, final D value4) {
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
	}

	public static <A, B, C, D> OrGet<Tuple4<A, B, C, D>> of(final A value1, final B value2, final C value3,
			final D value4) {
		return If.givenObject(value1)//
				.isNonNull("value1 argument in Tuple4").andOtherObjectIsNotNull(value2, "value2 argument in Tuple4")
				.andOtherObjectIsNotNull(value3, "value3 argument in Tuple4")
				.andOtherObjectIsNotNull(value4, "value4 argument in Tuple4")//
				.will()//
				.returnValue(() -> new Tuple4<>(value1, value2, value3, value4));
	}

	@Override
	public int hashCode() {
		return Objects.hash(value1, value2, value3, value4);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof final Tuple4 other))
			return false;
		return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2)
				&& Objects.equals(value3, other.value3) && Objects.equals(value4, other.value4);
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

	@Override
	public String toString() {
		return String.format("(%s, %s, %s, %s)", value1, value2, value3, value4);
	}
}
