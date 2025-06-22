package dev.ofekmalka.core.error;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.helper.ErrorTracker.MessageDraft;

public interface ErrorOptions {

	default String trackAndFinalize(final String methodName) {
		return ErrorTracker.startTrackingFrom(methodName).finalizeWith(getMessage());
	}

	default MessageDraft track(final String methodName) {
		return ErrorTracker.withMessage(getMessage()).withPrependedMethod(methodName);
	}

	default <T> Result<T> asResult() {
		return Result.failure(getMessage());
	}

	String getMessage();

}
