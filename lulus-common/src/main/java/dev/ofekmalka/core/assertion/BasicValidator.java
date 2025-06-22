
package dev.ofekmalka.core.assertion;

import dev.ofekmalka.core.assertion.If.Condition;

public enum BasicValidator {

	MESSAGE("message"), //
	PACKAGE_NAME("package name");

	private final String name;

	BasicValidator(final String name) {
		this.name = name;
	}

	public static Condition<String> validationMethodName(final String methodName) {
		return If.givenObject(methodName)//
				.isNonNull("methodName")//
				.andIsNot(String::isEmpty, "methodName must not be empty")//
				.andIsNot(String::isBlank, "methodName must not be blank")//
				.andIsNot(s -> s.matches(".*\\s.*"), "methodName must not contain white space")//
				.andIs(s -> s.length() >= 2, "methodName must contain at least two letters");
	}

	public static Condition<String> validationGeneralMessage(final String message) {
		return If.givenObject(message)//
				.isNonNull("message")//
				.andIsNot(String::isEmpty, "message must not be empty")//
				.andIsNot(String::isBlank, "message must not be blank")//
				.andIs(s -> s.replaceAll("\\s+", "").length() >= 2, "message must contain at least two letters");// the
																													// word
																													// 'no'
																													// ha
																													// ha
																													// ha
	}

	public Condition<String> validate(final String actualName) {
		return If.givenObject(actualName)//
				.isNonNull(actualName)//
				.andIsNot(String::isEmpty, ErrorMessages.format(this, ErrorMessages.EMPTY))//
				.andIsNot(String::isBlank, ErrorMessages.format(this, ErrorMessages.BLANK))//
				.andIs(s -> s.length() >= 2, ErrorMessages.format(this, ErrorMessages.MIN_LENGTH));
	}

	public static class MethodNameValidator {
		private static final String METHOD_NAME = "method name";
		static final String INVALID_BEGGINING_LETTER_METHOD_NAME = "must start with a lower letter";

		public static Condition<String> validate(final String value) {
			return If.givenObject(value)//
					.isNonNull(value)//
					.andIsNot(String::isEmpty, ErrorMessages.format(METHOD_NAME, ErrorMessages.EMPTY))//
					.andIsNot(String::isBlank, ErrorMessages.format(METHOD_NAME, ErrorMessages.BLANK))//
					.andIs(s -> s.matches("^\\S+$"), ErrorMessages.format(METHOD_NAME, ErrorMessages.WHITESPACE))//
					.andIs(s -> s.length() >= 2, ErrorMessages.format(METHOD_NAME, ErrorMessages.MIN_LENGTH))//
					.andIs(s -> s.matches("^[a-z][a-zA-Z0-9_]*$"),
							ErrorMessages.format(METHOD_NAME, INVALID_BEGGINING_LETTER_METHOD_NAME));
		}
	}

	public static class ClassNameValidator {
		private static final String CLASS_NAME = "class name";
		static final String INVALID_BEGGINING_LETTER_CLASS_NAME = "must start with a CAPITAL letter";

		public static Condition<String> validate(final String value) {
			return If.givenObject(value)//
					.isNonNull(value)//
					.andIsNot(String::isEmpty, ErrorMessages.format(CLASS_NAME, ErrorMessages.EMPTY))//
					.andIsNot(String::isBlank, ErrorMessages.format(CLASS_NAME, ErrorMessages.BLANK))//
					.andIs(s -> s.matches("^\\S+$"), ErrorMessages.format(CLASS_NAME, ErrorMessages.WHITESPACE))//
					.andIs(s -> s.length() >= 2, ErrorMessages.format(CLASS_NAME, ErrorMessages.MIN_LENGTH))//
					.andIs(s -> s.matches("^[A-Z][A-Za-z0-9]*$"),
							ErrorMessages.format(CLASS_NAME, INVALID_BEGGINING_LETTER_CLASS_NAME))

			;
		}
	}

	// The new ErrorMessageValidator class
	public static class ErrorMessageValidator {

		public static Condition<String> validate(final String errorMessage) {
			return If.givenObject(errorMessage)//
					.isNonNull(errorMessage)//
					.andIsNot(String::isEmpty, ErrorMessages.format("Error message", ErrorMessages.EMPTY))//
					.andIsNot(String::isBlank, ErrorMessages.format("Error message", ErrorMessages.BLANK))//
					.andIs(s -> s.length() >= 2, ErrorMessages.format("Error message", ErrorMessages.MIN_LENGTH));//
		}
	}

	private static class ErrorMessages {
		static final String EMPTY = "must not be empty";
		static final String BLANK = "must not be blank";
		static final String WHITESPACE = "must not contain white space";
		static final String MIN_LENGTH = "must contain at least two characters";

		static String format(final BasicValidator validator, final String message) {
			return String.format("[%s] %s %s", validator.name(), validator.name, message);
		}

		static String format(final String validatorName, final String message) {
			return String.format("[%s] %s", validatorName, message);
		}
	}
}
