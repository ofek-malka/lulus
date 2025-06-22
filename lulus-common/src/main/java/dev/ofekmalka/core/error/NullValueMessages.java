package dev.ofekmalka.core.error;

import dev.ofekmalka.core.assertion.If;

public class NullValueMessages {

	// Standard null message for any argument
	public static ErrorOptions argument(final String argName) {
		return () -> If.formatErrorMessageForNullValue(argName);
	}

	// Standard null message for any variable
	public static ErrorOptions variable(final String variableName) {
		return () -> If.formatErrorMessageForNullValue(variableName);
	}

	// Error message for multiple null indexes in a list/array
	public static ErrorOptions multipleNullIndexes(final String nullIndexes) {
		return () -> "Array contains null elements at indexes: " + nullIndexes;
	}

	// Error message for a single null index in a list/array
	public static ErrorOptions singleNullIndex(final String nullIndex) {
		return () -> "Array contains one null element at index: " + nullIndex;
	}

	// Additional methods for other `null` checks can be added here as needed

}
