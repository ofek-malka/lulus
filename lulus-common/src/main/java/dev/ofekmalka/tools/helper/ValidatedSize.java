package dev.ofekmalka.tools.helper;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;

public class ValidatedSize {
	private final int value;

	private ValidatedSize(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Result<ValidatedSize> of(final int number) {
		return of(number, "number");
	}

	public static Result<ValidatedSize> of(final int number, final String argumentName) {
		return If.givenObject(number)
				.is(n -> n > 0,
						List.Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE.withArgumentName(argumentName)
								.getMessage())
				.andIs(n -> n <= List.Errors.Constants.MAX_LIST_SIZE,
						List.Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage())

				.will().mapTo(ValidatedSize::new).getResult();
	}
}