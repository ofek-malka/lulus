package dev.ofekmalka.support.general.providers;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import dev.ofekmalka.support.annotation.ThreeArgumentNames;

public class ThreeArgumentNamesProvider implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {

		final var methodName = context.getRequiredTestMethod().getAnnotation(ThreeArgumentNames.class).methodName(); // Get
		final var firstArgumentName = context.getRequiredTestMethod().getAnnotation(ThreeArgumentNames.class)
				.firstArgumentName(); // Get

		final var secondArgumentName = context.getRequiredTestMethod().getAnnotation(ThreeArgumentNames.class)
				.secondArgumentName(); // Get

		final var thirdArgumentName = context.getRequiredTestMethod().getAnnotation(ThreeArgumentNames.class)
				.thirdArgumentName(); // Get

		return Stream.of(Arguments.arguments(methodName, firstArgumentName, secondArgumentName, thirdArgumentName));

	}

}
