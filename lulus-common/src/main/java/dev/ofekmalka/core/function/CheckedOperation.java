package dev.ofekmalka.core.function;

import dev.ofekmalka.core.error.ErrorOptions;

public interface CheckedOperation {
	ErrorOptions ERROR_MESSAGE_RUNTIME_EXCEPTION = () -> """
			A runtime exception was thrown from the checked operation (can be Function,Supplier or Predicate), which is undesirable!
			Please ensure that the function provided does not produce runtime exception results
			when applied to its arguments.""";

	ErrorOptions ERROR_MESSAGE_NULL_RESULT = () -> """
			Null value encountered during operation (can be Function,Supplier or Predicate).
			Please ensure that the function provided does not produce null results
			when applied to its arguments.""";

	ErrorOptions ERROR_MESSAGE_EMPTY_RESULT =

			() -> """
					Result.empty() value encountered during operation (can be Function,Supplier or Predicate).\
					Please ensure that the function provided does not produce Result.empty() \
					results when applied to its arguments.""";

}
