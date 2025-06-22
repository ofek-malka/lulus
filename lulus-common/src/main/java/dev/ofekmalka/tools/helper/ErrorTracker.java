package dev.ofekmalka.tools.helper;

import static dev.ofekmalka.core.assertion.BasicValidator.MESSAGE;

import dev.ofekmalka.core.assertion.BasicValidator.MethodNameValidator;

public final class ErrorTracker {

	public static final String CALL_FORMAT = "Called by the method name '%s' ->";

	private static Nothing validateCallerName(final String methodName) {
		return MethodNameValidator//

				.validate(methodName).will()

				.thenApprovedOrElseThrowException();//
	}

	private static Nothing validateErrorMessage(final String message) {
		return MESSAGE//
				.validate(message)//
				.will().thenApprovedOrElseThrowException();//

	}

	public static MessageAppender startTrackingFrom(final String methodName) {
		validateCallerName(methodName);
		return new MessageAppender(String.format(CALL_FORMAT, methodName));
	}

	public static MessageDraft withMessage(final String message) {
		validateErrorMessage(message);
		return new MessageDraft(message);
	}

	public static class MessageDraft {

		private final String content;

		private MessageDraft(final String content) {
			this.content = content;
		}

		public MessageDraft withPrependedMethod(final String methodName) {
			validateCallerName(methodName);
			return new MessageDraft(String.format(CALL_FORMAT, methodName) + "\n" + content);
		}

		public String prependAndFinalize(final String methodName) {
			validateCallerName(methodName);
			return String.format(CALL_FORMAT, methodName) + "\n" + content;
		}

	}

	public static class MessageAppender {

		private final String content;

		private MessageAppender(final String content) {
			this.content = content;
		}

		public MessageAppender addCallFrom(final String methodName) {
			validateCallerName(methodName);
			return new MessageAppender(content + "\n" + String.format(CALL_FORMAT, methodName));
		}

		public MessageAppender withPrependedMethod(final String methodName) {
			validateCallerName(methodName);
			return new MessageAppender(String.format(CALL_FORMAT, methodName) + "\n" + content);
		}

		public String finalizeWith(final String finalMessage) {
			validateErrorMessage(finalMessage);
			return content + "\n" + finalMessage;
		}

	}

}
