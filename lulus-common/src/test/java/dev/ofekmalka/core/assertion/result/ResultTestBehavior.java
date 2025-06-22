package dev.ofekmalka.core.assertion.result;

import org.assertj.core.api.BDDSoftAssertions;

public interface ResultTestBehavior {

	sealed interface Operations permits //
	Operations.Invalid, //
	Operations.Creation, //
	Operations.Filtering, //
	Operations.Mapping, //
	Operations.ErrorHandling, //
	Operations.SpecialCase, //
	Operations.SuccessFailureQueries, //
	Operations.RetrievingValues, //
	Operations.Conditional, //
	Operations.Equality {

		sealed interface Invalid extends Operations

		permits //
		Invalid.Creation, //

		Invalid.Mapping, //
		Invalid.Filtering, //
		Invalid.ErrorHandling, //
		Invalid.SpecialCase, //
		Invalid.RetrievingValues, //
		Invalid.Conditional //

		{

			non-sealed interface Creation extends Invalid {

				void lift(String methodName);

				void lift2(String methodName, BDDSoftAssertions softly);

				void lift3(final String methodName);

				void generalCreationWithNullValue(String methodName, String argumentName);

				void generalCreationWithSupplierProduceNull(String methodName, String argumentName);

				void generalCreationWithNullInvalidValueOrFailureMessageForNullValue(String methodName,
						String argumentName, BDDSoftAssertions softly);

				void generalCreationWithInvalidSupplierOrFailureMessageForNullValue(String methodName,
						String firstArgumentName, String secondArgumentName, BDDSoftAssertions softly);

				void failureResultCreationFromErrorMessage(String methodName, String argumentName,
						BDDSoftAssertions softly);

				void failureResultCreationFromErrorMessageAndException(String methodName, String firstArgumentName,
						String secondArgumentName, BDDSoftAssertions softly);

				void failureResultCreationFromException(String methodName, String argumentName);

				void failureResultCreationFromRuntimeException(String methodName, String argumentName);

			}

			public non-sealed interface Mapping extends Invalid {
				void map(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void flatMap(final String methodName, final String argumentName, final BDDSoftAssertions softly);

			}

			public non-sealed interface Filtering extends Invalid {
				void validate(final String methodName, //
						final String firstArgumentName, //
						final String secondArgumentName, final BDDSoftAssertions softly);

				void reject(final String methodName, //
						final String firstArgumentName, //
						final String secondArgumentName, final BDDSoftAssertions softly);

			}

			public non-sealed interface ErrorHandling extends Invalid {

				void prependFailureMessage(final String methodName, String argumentName,
						final BDDSoftAssertions softly);

				void getOrCreateFailureInstanceWithMessage(String methodName, String argumentName,
						BDDSoftAssertions softly);

			}

			public non-sealed interface SpecialCase extends Invalid {
				void mapEmpty();
			}

			public non-sealed interface RetrievingValues extends Invalid {
				void getOrElseForSupplierArgument(final String methodName, final String argumentName,
						final BDDSoftAssertions softly);

				void successValue(final String methodName, final BDDSoftAssertions softly);

				void failureValue(final String methodName, final BDDSoftAssertions softly);
			}

	

			non-sealed interface Conditional extends Invalid {
				void or(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void and(final String methodName, final String argumentName, final BDDSoftAssertions softly);

			}

		}

		public non-sealed interface Creation extends Operations {

			void emptyResult();

			void successResult(BDDSoftAssertions softly);

			void failureResultFromErrorMessage(BDDSoftAssertions softly);

			void failureResultFromErrorMessageAndException(BDDSoftAssertions softly);

			void failureResultFromException(BDDSoftAssertions softly);

			void failureResultFromRuntimeException(BDDSoftAssertions softly);

		}

		public non-sealed interface ErrorHandling extends Operations {
			void prependMethodNameToFailureMessage(final BDDSoftAssertions softly);

			void mapFailureForOtherObject();

			void getOrCreateFailureInstanceWithMessage(final BDDSoftAssertions softly);

			void prependFailureMessage(final BDDSoftAssertions softly);

			void removeCalledByMethodPrefixes(final BDDSoftAssertions softly);

			void extractOnlyUserError(final BDDSoftAssertions softly);
		}

		public non-sealed interface Mapping extends Operations {
			void map(final BDDSoftAssertions softly);

			void flatMap(final BDDSoftAssertions softly);
		}

		public non-sealed interface Filtering extends Operations {
			void validate(final BDDSoftAssertions softly);

			void reject(final BDDSoftAssertions softly);

		}

		public non-sealed interface SpecialCase extends Operations {
			void mapEmpty(final BDDSoftAssertions softly);
		}

		public non-sealed interface SuccessFailureQueries extends Operations {
			void isSuccess(final BDDSoftAssertions softly);

			void isFailure(final BDDSoftAssertions softly);

			void isEmpty(final BDDSoftAssertions softly);
		}

		public non-sealed interface RetrievingValues extends Operations {
			void getOrElse(final BDDSoftAssertions softly);

			void successValue(final BDDSoftAssertions softly);

			void failureValue(final BDDSoftAssertions softly);

			void getOrElseForSupplierArgument(final BDDSoftAssertions softly);
		}



		non-sealed interface Conditional extends Operations {
			void or(final BDDSoftAssertions softly);

			void and(final BDDSoftAssertions softly);

			void orElse(final BDDSoftAssertions softly);
		}

		// Equality and comparison-related operations
		non-sealed interface Equality extends Operations {
			void isNotSuccess(final BDDSoftAssertions softly);

			void isEqualTo(final BDDSoftAssertions softly);
		}
		// This is where advanced operations will be divided logically.

	}

}
