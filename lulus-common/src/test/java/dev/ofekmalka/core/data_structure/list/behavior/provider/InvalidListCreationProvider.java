package dev.ofekmalka.core.data_structure.list.behavior.provider;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.tuple.Tuple2;

public class InvalidListCreationProvider implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
		final String[] nullElements = null;

		final var erorrMessageArgElementsIsNull = NullValueMessages.argument("elements").getMessage();
		final var nullValueInArrayAtIndexZero = NullValueMessages.singleNullIndex("[0]").getMessage();
		final var nullValueInArrayAtIndexZeroAndOne = NullValueMessages.multipleNullIndexes("[0, 1]").getMessage();

		final var listMethodTracker = ErrorTracker.startTrackingFrom("list");
		final var reverseListMethodTracker = ErrorTracker.startTrackingFrom("reverseList");
		final var case1 = List.list(nullElements);
		final var pair1 = Tuple2.of(case1, listMethodTracker

				.finalizeWith(erorrMessageArgElementsIsNull));

		final var case2 = List.list(null, "");
		final var pair2 = Tuple2.of(case2, listMethodTracker.finalizeWith(nullValueInArrayAtIndexZero));
		final var case3 = List.list(null, null);
		final var pair3 = Tuple2.of(case3, listMethodTracker.finalizeWith(nullValueInArrayAtIndexZeroAndOne));

		final var case4 = List.reverseList(nullElements);
		final var pair4 = Tuple2.of(case4, reverseListMethodTracker

				.finalizeWith(listMethodTracker.finalizeWith(erorrMessageArgElementsIsNull))

		);

		final var case5 = List.reverseList(null, "");
		final var pair5 = Tuple2.of(case5,
				reverseListMethodTracker.finalizeWith(listMethodTracker.finalizeWith(nullValueInArrayAtIndexZero)));
		final var case6 = List.reverseList(null, null);
		final var pair6 = Tuple2.of(case6, reverseListMethodTracker
				.finalizeWith(listMethodTracker.finalizeWith(nullValueInArrayAtIndexZeroAndOne)));

		return Stream.of(pair1, pair2, pair3, pair4, pair5, pair6).map(t -> {

			final var typle = t.getResult().successValue();
			final var actualFailMessage = typle.state().getErrorMessageIfProcessFail();
			final var expectedErrorMessage = typle.value();
			return Arguments.arguments(actualFailMessage, expectedErrorMessage);
		});

	}
}
