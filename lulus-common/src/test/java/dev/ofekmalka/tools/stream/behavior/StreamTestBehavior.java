package dev.ofekmalka.tools.stream.behavior;

import org.assertj.core.api.BDDSoftAssertions;

/**
 *
 * This design is clean, easy to navigate, and allows for future-proof
 * expansion, ensuring that as your library or application grows, your tests can
 * grow seamlessly with it. This approach aligns well with functional
 * programming principles in Java and stream processing concepts.
 *
 */
public interface StreamTestBehavior {

	public sealed interface Laziness permits //
	Laziness.General, //
	Laziness.Power {

		non-sealed interface General extends Laziness {

			void shouldApplySideEffectUntilFirstOccurrence();

			void shouldApplySideEffectUntilFirstOccurrenceWithDifferentCondition();

			void shouldProcessStreamOnce();

			void shouldReturnTheRequiredResult();

		}

		non-sealed interface Power extends Laziness {

			void shouldGenerateFirstNPrimesLazily();

			void shouldStopInfiniteStreamWhenConditionMet();

		}

	}

	public sealed interface Operations permits //
	Operations.Invalid, //
	Operations.Creation, //
	Operations.Filtering, //
	Operations.Mapping, //
	Operations.Combining, //
	Operations.Windowing, //
	Operations.OtherTransformations {

		// Creation Status
		non-sealed interface Creation extends Operations {
			void emptyStream(final BDDSoftAssertions softly);

			void from(final BDDSoftAssertions softly);

			void fibs(final BDDSoftAssertions softly);

			void repeat(final BDDSoftAssertions softly);

			void iterate(final BDDSoftAssertions softly);

			void unfold(final BDDSoftAssertions softly);
		}

		// Filtering Operations
		non-sealed interface Filtering extends Operations {
			void filter(final BDDSoftAssertions softly);

			void dropWhile(final BDDSoftAssertions softly);

			void takeWhile(final BDDSoftAssertions softly);
		}

		// Mapping Operations
		non-sealed interface Mapping extends Operations {
			void map(final BDDSoftAssertions softly);

			void flatMap(final BDDSoftAssertions softly);
		}

		// Combining Operations
		non-sealed interface Combining extends Operations {
			void append(final BDDSoftAssertions softly);

			void zipAsPossible(final BDDSoftAssertions softly);
		}

		// Windowing Operations
		non-sealed interface Windowing extends Operations {
			void windowFixed(final BDDSoftAssertions softly);

			void windowSliding(final BDDSoftAssertions softly);

			void windowFixedAtMost(BDDSoftAssertions softly);
		}

		// Other Transformations
		non-sealed interface OtherTransformations extends Operations {
			void setFirstElement(final BDDSoftAssertions softly);

			void cons(final BDDSoftAssertions softly);

			void dropAtMost(final BDDSoftAssertions softly);

			void takeAtMost(final BDDSoftAssertions softly);

			void toBoundedList(final BDDSoftAssertions softly);
		}

		// Invalid operations.
		sealed interface Invalid extends Operations {

			non-sealed interface Creation extends Invalid {

				void repeat(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void iterate(final String methodName, final String firstArgumentName, final String secondArgumentName,
						final BDDSoftAssertions softly);

				void unfold(final String methodName, final String firstArgumentName, final String secondArgumentName,
						final BDDSoftAssertions softly);
			}

			// Filtering Operations
			non-sealed interface Filtering extends Invalid {
				void filter(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void dropWhile(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void takeWhile(final String methodName, final String argumentName, final BDDSoftAssertions softly);
			}

			// Mapping Operations
			non-sealed interface Mapping extends Invalid {
				void map(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void flatMap(final String methodName, final String argumentName, final BDDSoftAssertions softly);
			}

			// Combining Operations
			non-sealed interface Combining extends Invalid {
				void append(final String methodName, final String argumentName, final BDDSoftAssertions softly);

				void zipAsPossible(final String methodName, final String argumentName, final BDDSoftAssertions softly);
			}

			// Other Transformations
			non-sealed interface OtherTransformations extends Invalid {
				void setFirstElement(final String methodName, final String argumentName,
						final BDDSoftAssertions softly);

				void cons(final String methodName, final String argumentName);

			}

		}
	}
}