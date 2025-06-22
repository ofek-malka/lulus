package dev.ofekmalka.core.data_structure.list.behavior.provider;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.tuple.Tuple2;;

public class InvalidPrimitiveListCreationsProvider implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {

		final var factoryList = List.extendedFactoryOperations();
		final var emptyArrayErrorMessage = List.Errors

				.CastumArgumentMessage.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY;

		// Error case when the argument is null (stateCase1 to stateCase8)
		final var stateCase1 = factoryList.createBooleanList(null).getErrorMessageIfProcessFail();
		final var valueCase1 = ErrorTracker.startTrackingFrom("createBooleanList")
				.finalizeWith(NullValueMessages.argument("booleanValues").getMessage());

		final var stateCase2 = factoryList.createByteList(null).getErrorMessageIfProcessFail();
		final var valueCase2 = ErrorTracker.startTrackingFrom("createByteList")
				.finalizeWith(NullValueMessages.argument("byteValues").getMessage());

		final var stateCase3 = factoryList.createCharList(null).getErrorMessageIfProcessFail();
		final var valueCase3 = ErrorTracker.startTrackingFrom("createCharList")
				.finalizeWith(NullValueMessages.argument("charValues").getMessage());

		final var stateCase4 = factoryList.createDoubleList(null).getErrorMessageIfProcessFail();
		final var valueCase4 = ErrorTracker.startTrackingFrom("createDoubleList")
				.finalizeWith(NullValueMessages.argument("doubleValues").getMessage());

		final var stateCase5 = factoryList.createFloatList(null).getErrorMessageIfProcessFail();
		final var valueCase5 = ErrorTracker.startTrackingFrom("createFloatList")
				.finalizeWith(NullValueMessages.argument("floatValues").getMessage());

		final var stateCase6 = factoryList.createIntList(null).getErrorMessageIfProcessFail();
		final var valueCase6 = ErrorTracker.startTrackingFrom("createIntList")
				.finalizeWith(NullValueMessages.argument("intValues").getMessage());

		final var stateCase7 = factoryList.createLongList(null).getErrorMessageIfProcessFail();
		final var valueCase7 = ErrorTracker.startTrackingFrom("createLongList")
				.finalizeWith(NullValueMessages.argument("longValues").getMessage());

		final var stateCase8 = factoryList.createShortList(null).getErrorMessageIfProcessFail();
		final var valueCase8 = ErrorTracker.startTrackingFrom("createShortList")
				.finalizeWith(NullValueMessages.argument("shortValues").getMessage());

		// Error case when the argument is an empty array (stateCase9 to stateCase16)
		final var stateCase9 = factoryList.createBooleanList().getErrorMessageIfProcessFail();
		final var valueCase9 = ErrorTracker.startTrackingFrom("createBooleanList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("booleanValues").getMessage());

		final var stateCase10 = factoryList.createByteList().getErrorMessageIfProcessFail();
		final var valueCase10 = ErrorTracker.startTrackingFrom("createByteList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("byteValues").getMessage());

		final var stateCase11 = factoryList.createCharList().getErrorMessageIfProcessFail();
		final var valueCase11 = ErrorTracker.startTrackingFrom("createCharList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("charValues").getMessage());

		final var stateCase12 = factoryList.createDoubleList().getErrorMessageIfProcessFail();
		final var valueCase12 = ErrorTracker.startTrackingFrom("createDoubleList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("doubleValues").getMessage());

		final var stateCase13 = factoryList.createFloatList().getErrorMessageIfProcessFail();
		final var valueCase13 = ErrorTracker.startTrackingFrom("createFloatList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("floatValues").getMessage());

		final var stateCase14 = factoryList.createIntList().getErrorMessageIfProcessFail();
		final var valueCase14 = ErrorTracker.startTrackingFrom("createIntList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("intValues").getMessage());

		final var stateCase15 = factoryList.createLongList().getErrorMessageIfProcessFail();
		final var valueCase15 = ErrorTracker.startTrackingFrom("createLongList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("longValues").getMessage());

		final var stateCase16 = factoryList.createShortList().getErrorMessageIfProcessFail();
		final var valueCase16 = ErrorTracker.startTrackingFrom("createShortList")
				.finalizeWith(emptyArrayErrorMessage.withArgumentName("shortValues").getMessage());

		// Returning the arguments stream
		return Stream.of(Tuple2.of(stateCase1, valueCase1), //
				Tuple2.of(stateCase2, valueCase2), //
				Tuple2.of(stateCase3, valueCase3), //
				Tuple2.of(stateCase4, valueCase4), //
				Tuple2.of(stateCase5, valueCase5), //
				Tuple2.of(stateCase6, valueCase6), //
				Tuple2.of(stateCase7, valueCase7), //
				Tuple2.of(stateCase8, valueCase8), //
				Tuple2.of(stateCase9, valueCase9), //
				Tuple2.of(stateCase10, valueCase10), //
				Tuple2.of(stateCase11, valueCase11), //
				Tuple2.of(stateCase12, valueCase12), //
				Tuple2.of(stateCase13, valueCase13), //
				Tuple2.of(stateCase14, valueCase14), //
				Tuple2.of(stateCase15, valueCase15), //
				Tuple2.of(stateCase16, valueCase16)//
		).map(t -> {
			final var tuple = t.getResult().successValue();
			final var actualFailMessage = tuple.state();
			final var expectedErrorMessage = tuple.value();
			return Arguments.arguments(actualFailMessage, expectedErrorMessage);
		});

	}
}
