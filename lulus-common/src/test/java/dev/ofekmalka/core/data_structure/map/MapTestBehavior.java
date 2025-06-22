package dev.ofekmalka.core.data_structure.map;

import org.assertj.core.api.BDDSoftAssertions;

public interface MapTestBehavior {
	public sealed interface Operations permits //
	Operations.Invalid, //
	Operations.Basic, //
	Operations.Queries, //
	Operations.Transformations, //
	Operations.Creation

	{

		public sealed interface Invalid extends Operations permits //
		Invalid.Basic, //
		Invalid.Queries, //
		Invalid.Transformations {

			public non-sealed interface Transformations extends Invalid {
				void mapValues(final String methodName, final String argumentName, final BDDSoftAssertions softly);

			}

			public non-sealed interface Queries extends Invalid {
				void containsKey(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void containsValue(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void getOptionallyWithMessage(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final BDDSoftAssertions softly);

				void keysForValue(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void getOptionally(final String methodName, final BDDSoftAssertions softly);

				void getOptionallyWithKeyObjectNotOverrideToString(String methodName);
			}

			public non-sealed interface Basic extends Invalid {
				void add(final String methodName, final String firstArgumentName, final String secondArgumentName,
						final BDDSoftAssertions softly);

				void remove(final String methodName, final String argumentName, final BDDSoftAssertions softly);
			}

		}

		public non-sealed interface Creation extends Operations {

			void shouldSuccessfullyCreateAnEmptyMap();

			void shouldSuccessfullyCreateNonEmptyMap();

		}

		public non-sealed interface Transformations extends Operations {
			void mapValues(final BDDSoftAssertions softly);

		}

		public non-sealed interface Queries extends Operations {
			void isEmpty(final BDDSoftAssertions softly);

			void size(final BDDSoftAssertions softly);

			void containsKey(final BDDSoftAssertions softly);

			void containsValue(final BDDSoftAssertions softly);

			void getOptionally(final BDDSoftAssertions softly);

			void getOptionallyWithMessage(final BDDSoftAssertions softly);

			void keys(final BDDSoftAssertions softly);

			void values(final BDDSoftAssertions softly);

			void keysForValue(final BDDSoftAssertions softly);
		}

		public non-sealed interface Basic extends Operations {
			void add(final BDDSoftAssertions softly);

			void remove(final BDDSoftAssertions softly);

			void toList(final BDDSoftAssertions softly);
		}

	}
}
//
//
//
//
