package dev.ofekmalka.core.error;

public final class IndexErrorMessage {

	public static Builder.Between forIndex(final int index) {
		return Builder.builder().forIndex(String.valueOf(index));
	}

	// Builder class to support fluent interface
	public static class Builder {

		public static Builder.ForIndex builder() {
			return index -> lowerBound -> upperBound -> {
				final var MESSAGE_TEMPLATE = //
						"Index %s is out of range. Valid index range is between %d and %d(included).";

				return () -> String.format(MESSAGE_TEMPLATE, index, lowerBound, upperBound);
			};

		}

		public interface ForIndex {
			Between forIndex(String index);

		}

		public interface Between {
			To between(int lowerBound);
		}

		public interface To {
			ErrorOptions to(final int upperBound);

			default ErrorOptions toExclusive(final int upperBound) {

				return to(upperBound - 1);
			}

		}

	}

}
