package dev.ofekmalka.core.data_structure.set;

import org.assertj.core.api.BDDSoftAssertions;

public interface SetTestBehavior {

	public sealed interface Operations permits //
	Operations.Invalid, //
	Operations.Basic, //
	Operations.Queries, //
	Operations.Relations, //
	Operations.Algebra, //
	Operations.Joins, //
	Operations.Creation

	{
		public sealed interface Invalid extends Operations

		permits

		Invalid.Basic, //
		Invalid.Queries, //
		Invalid.Relations, //
		Invalid.Algebra, //
		Invalid.Joins {//

			public non-sealed interface Basic extends Invalid {
				void insert(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void delete(final String methodName, final String argumentName, final BDDSoftAssertions softly);

			}

			public non-sealed interface Queries extends Invalid {
				void contains(final String methodName, final String argumentName, final BDDSoftAssertions softly);

			}

			public non-sealed interface Relations extends Invalid {

				void isSubsetOf(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void isSupersetOf(final String methodName, final String argumentName, final BDDSoftAssertions softly);

			}

			public non-sealed interface Algebra extends Invalid {
				void intersect(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void union(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void difference(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void symmetricDifference(final String methodName, final String argumentName,
						final BDDSoftAssertions softly);
			}

			public non-sealed interface Joins extends Invalid {
				void crossJoin(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void innerJoin(final String methodName, final String firstArgumentName, final String secondArgumentName,
						final BDDSoftAssertions softly);
			}

		}

		public non-sealed interface Basic extends Operations {
			void insert(final BDDSoftAssertions softly);

			void delete(final BDDSoftAssertions softly);

			void toList(final BDDSoftAssertions softly);

		}

		public non-sealed interface Queries extends Operations {
			void isEmpty(final BDDSoftAssertions softly);

			void cardinality(final BDDSoftAssertions softly);

			void contains(final BDDSoftAssertions softly);

		}

		public non-sealed interface Relations extends Operations {
			void isSubsetOf(final BDDSoftAssertions softly);

			void isSupersetOf(final BDDSoftAssertions softly);

		}

		public non-sealed interface Algebra extends Operations {
			void intersect(final BDDSoftAssertions softly);

			void union(final BDDSoftAssertions softly);

			void difference(final BDDSoftAssertions softly);

			void symmetricDifference(final BDDSoftAssertions softly);

		}

		public non-sealed interface Joins extends Operations {
			void crossJoin(final BDDSoftAssertions softly);

			void innerJoin(final BDDSoftAssertions softly);
		}

		public non-sealed interface Creation extends Operations {

			void shouldSuccessfullyCreateAnEmptySet();

			void shouldSuccessfullyCreateNonEmptySet();

		}

	}

}
