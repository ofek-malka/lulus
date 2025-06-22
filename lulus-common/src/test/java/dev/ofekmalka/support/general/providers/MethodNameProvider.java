package dev.ofekmalka.support.general.providers;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import dev.ofekmalka.support.annotation.MethodName;

public class MethodNameProvider implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
		final var methodName = context.getRequiredTestMethod().getAnnotation(MethodName.class).name(); // Get
		return Stream.of(Arguments.arguments(methodName));

	}

}
