package dev.ofekmalka.core.data_structure.grid;

import org.assertj.core.api.BDDSoftAssertions;

public interface GridTestBehavior {
	sealed interface Operations permits //
	Operations.invalid, //
	Operations.Basic, //
	Operations.Queries, //
	Operations.Transformations, //
	Operations.Traversal//

	{

		sealed interface invalid extends Operations

		permits //
		invalid.Basic, //
		invalid.Queries, //
		invalid.Transformations {

			public non-sealed interface Basic extends invalid {
				void setAt(final String methodName, final String firstArgumentName, final String secondArgumentName,
						final BDDSoftAssertions softly);

				void getAt(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void subGrid(final String methodName, final String argumentName, final BDDSoftAssertions softly);

			}

			public non-sealed interface Queries extends invalid {

				void isInBounds(final String methodName, final String argumentName);

			}

			public non-sealed interface Transformations extends invalid {
				void map(final String methodName, final String argumentName);

				void mapWithPosition(final String methodName, final String argumentName);

				void filter(final String methodName, final String argumentName);

			}

		}

		public non-sealed interface Basic extends Operations {
			void setAt(final BDDSoftAssertions softly);

			void getAt(final BDDSoftAssertions softly);

			void subGrid(final BDDSoftAssertions softly);

			void size(BDDSoftAssertions softly);

		}

		public non-sealed interface Queries extends Operations {
			void isEmpty(final BDDSoftAssertions softly);

			void countRows(final BDDSoftAssertions softly);

			void countColumns(final BDDSoftAssertions softly);

			void isInBounds(final BDDSoftAssertions softly);

			void isSymmetric(final BDDSoftAssertions softly);

			void isDiagonal(final BDDSoftAssertions softly);

		}

		public non-sealed interface Transformations extends Operations {
			void map(final BDDSoftAssertions softly);

			void mapWithPosition(final BDDSoftAssertions softly);

			void filter(final BDDSoftAssertions softly);

			void rotate90(final BDDSoftAssertions softly);

			void rotate180(final BDDSoftAssertions softly);

			void rotate270(final BDDSoftAssertions softly);

			void flipHorizontally(final BDDSoftAssertions softly);

			void flipVertically(final BDDSoftAssertions softly);
		}

		public non-sealed interface Traversal extends Operations {

			void indexedCells(final BDDSoftAssertions softly);

		}

	}

}
