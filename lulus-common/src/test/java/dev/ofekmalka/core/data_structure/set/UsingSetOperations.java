package dev.ofekmalka.core.data_structure.set;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.data_structure.set.Set.Errors;
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.support.TestMulHelper;
import dev.ofekmalka.support.annotation.ArgumentName;
import dev.ofekmalka.support.annotation.CustomDisplayNameGenerator;
import dev.ofekmalka.support.annotation.TwoArgumentNames;
import dev.ofekmalka.support.general.providers.ArgumentNameProvider;
import dev.ofekmalka.support.general.providers.TwoArgumentNamesProvider;
import dev.ofekmalka.tools.tuple.Tuple2;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SoftAssertionsExtension.class)
public class UsingSetOperations {

	@Nested
	class Invalid {
		private TestMulHelper<Set<Integer>> createSetTest(final BDDSoftAssertions softly) {
			return TestMulHelper.softAssertions(softly)//
					.<Set<Integer>>//
					initializeCase(List.list(0, 1, 2, 3, 4, 5, 6, 7, 8).convert().toSet())//
			;
		}

		private String errorFunctionThrowingException(final String methodName) {
			return CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION//
					.trackAndFinalize(methodName);

		}

		private String errorFunctionProducesNull(final String methodName) {
			return CheckedOperation.ERROR_MESSAGE_NULL_RESULT//

					.trackAndFinalize(methodName);
		}

		private String errorProcessingMessage(final String methodName) {
			return Errors.CastumArgumentMessage//
					.ERROR_PROCESSING_JOINED_SET//
					.withArgumentName("other")//
					.trackAndFinalize(methodName);
		}

		private String errorArgumentNullValue(final String methodName, final String argumentName) {
			return NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
		}

		private <B> void checkingNullAndProcessingOtherSetForFunction(//
				final String methodName, //
				final String argumentName, //
				final BDDSoftAssertions softly, //
				final Function<Set<Integer>, Function<Set<Integer>, Result<Set<B>>>> f) {//
			final var errorNullValue = errorArgumentNullValue(methodName, argumentName);

			final var errorProcessingMessage = errorProcessingMessage(methodName);//

			createSetTest(softly)//
					.<Set<Integer>>givenNullArgument()//
					.givenSingleArgument(Set.makeFailureInstance())//
					.performActionResult(f)//
					.thenShouldHaveSameErrorMessage(errorNullValue)//
					.thenShouldHaveSameErrorMessage(errorProcessingMessage);

		}

		@Nested
		class Algebra implements SetTestBehavior.Operations.Invalid.Algebra {

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "intersect", argumentName = "other")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void intersect(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				checkingNullAndProcessingOtherSetForFunction(methodName, argumentName, softly,
						other -> set -> set.intersect(other).getSetResult());

			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "union", argumentName = "other")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void union(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				checkingNullAndProcessingOtherSetForFunction(methodName, argumentName, softly,
						other -> set -> set.union(other).getSetResult());

			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "difference", argumentName = "other")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void difference(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				checkingNullAndProcessingOtherSetForFunction(methodName, argumentName, softly,
						other -> set -> set.difference(other).getSetResult());
			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "symmetricDifference", argumentName = "other")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void symmetricDifference(final String methodName, final String argumentName,
					final BDDSoftAssertions softly) {
				checkingNullAndProcessingOtherSetForFunction(methodName, argumentName, softly,
						other -> set -> set.symmetricDifference(other).getSetResult());
			}

		}

		@Nested
		class Basic implements SetTestBehavior.Operations.Invalid.Basic {
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "insert", argumentName = "element")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void insert(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				createSetTest(softly)//
						.<Integer>givenNullArgument()//
						.performActionResult(e -> set -> set.insert(e).getSetResult())//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, argumentName));
			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "delete", argumentName = "element")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void delete(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				createSetTest(softly)//
						.<Integer>givenNullArgument()//
						.performActionResult(e -> set -> set.delete(e).getSetResult())//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, argumentName));
			}

		}

		@Nested
		class Joins implements SetTestBehavior.Operations.Invalid.Joins {
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "crossJoin", argumentName = "other")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void crossJoin(//
					final String methodName, //
					final String argumentName, //
					final BDDSoftAssertions softly) {
				checkingNullAndProcessingOtherSetForFunction(methodName, argumentName, softly,
						other -> set -> set.crossJoin(other).getSetResult());
			}

			@Override
			@ParameterizedTest
			@TwoArgumentNames(methodName = "innerJoin", //
					firstArgumentName = "other", //
					secondArgumentName = "matchCondition") //
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			public void innerJoin(final String methodName, final String firstArgumentName,
					final String secondArgumentName, final BDDSoftAssertions softly) {
				final var currentSet = Set.<Integer>emptySet().insert(187);

				createSetTest(softly)//
						.<Set<Integer>, Function<Integer, Predicate<Integer>>>//
						givenTwoArguments(null, a -> b -> a == b)//
						.givenTwoArguments(Set.makeFailureInstance(), a -> b -> a == b)//
						.givenTwoArguments(List.list(1, 2, 3).convert().toSet(), null)//
						.givenTwoArguments(currentSet, a -> null)//
						.givenTwoArguments(currentSet, a -> b -> null)//
						.givenTwoArguments(currentSet, a -> {//
							throw new RuntimeException("bla bla bla");
						}).givenTwoArguments(currentSet, a -> b -> {//
							throw new RuntimeException("bla bla bla");
						})//
						.performActionResult(other -> matchCondition -> set -> //
						set.innerJoin(other, matchCondition).getSetResult()//
						)//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, firstArgumentName))//
						.thenShouldHaveSameErrorMessage(errorProcessingMessage(methodName))//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, secondArgumentName))//
						.thenShouldHaveSameErrorMessage(errorFunctionProducesNull(methodName))//
						.thenShouldHaveSameErrorMessage(errorFunctionProducesNull(methodName))//
						.thenShouldHaveSameErrorMessage(errorFunctionThrowingException(methodName))//
						.thenShouldHaveSameErrorMessage(errorFunctionThrowingException(methodName));

			}

		}

		@Nested
		class Queries implements SetTestBehavior.Operations.Invalid.Queries {
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "contains", argumentName = "element")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void contains(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var errorNullValue = errorArgumentNullValue(methodName, argumentName);
				createSetTest(softly)//
						.<Integer>givenNullArgument()//
						.performActionResult(e -> set -> set.contains(e))//
						.thenShouldHaveSameErrorMessage(errorNullValue);
			}

		}

		@Nested
		class Relations implements SetTestBehavior.Operations.Invalid.Relations {
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "isSubsetOf", argumentName = "other")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void isSubsetOf(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var errorNullValue = errorArgumentNullValue(methodName, argumentName);

				final var errorProcessingMessage = errorProcessingMessage(methodName);//

				createSetTest(softly)//
						.<Set<Integer>>givenNullArgument()//
						.givenSingleArgument(Set.makeFailureInstance())//
						.performActionResult(other -> set -> set.isSubsetOf(other))//
						.thenShouldHaveSameErrorMessage(errorNullValue)//
						.thenShouldHaveSameErrorMessage(errorProcessingMessage);
			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "isSupersetOf", argumentName = "other")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void isSupersetOf(final String methodName, final String argumentName,
					final BDDSoftAssertions softly) {
				final var errorNullValue = errorArgumentNullValue(methodName, argumentName);

				final var errorProcessingMessage = errorProcessingMessage(methodName);//

				createSetTest(softly)//
						.<Set<Integer>>givenNullArgument()//
						.givenSingleArgument(Set.makeFailureInstance())//
						.performActionResult(other -> set -> set.isSupersetOf(other))//
						.thenShouldHaveSameErrorMessage(errorNullValue)//
						.thenShouldHaveSameErrorMessage(errorProcessingMessage);
			}

		}

	}

	@Nested
	class ShouldSuccessfully {

		@Nested
		class DuringCreation implements SetTestBehavior.Operations.Creation {

			@Override
			@Test
			public void shouldSuccessfullyCreateAnEmptySet() {
				assertThat(Set.emptySet().getSetResult())//
						.as("Empty set should successfully be created")//
						.matches(Result::isSuccess);
			}

			@Override
			@Test
			public void shouldSuccessfullyCreateNonEmptySet() {
				assertThat(List.list(1).convert().toSet().getSetResult())//
						.as("Creation non emptySet set should be a success")//
						.matches(Result::isSuccess);

			}

		}

		@Nested
		class Algebra implements SetTestBehavior.Operations.Algebra {

			@Override
			@Test
			public void intersect(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.<Set<Integer>>//
						givenSingleArgument(Set.emptySet())//
						.givenSingleArgument(List.list(0, 1, 4, 5).convert().toSet())//
						.performActionResult(other -> set -> set.intersect(other).getSetResult())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(List.list(0, 1).convert().toSet());
			}

			@Override
			@Test
			public void union(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.<Set<Integer>>//
						givenSingleArgument(Set.emptySet())//
						.givenSingleArgument(List.list(0, 1, 4, 5).convert().toSet())//
						.performActionResult(other -> set -> set.union(other).getSetResult())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(List.list(0, 1, 4, 5).convert().toSet())//
						.thenShouldBeEqualTo(List.list(0, 1, 2, 3).convert().toSet())//
						.thenShouldBeEqualTo(List.list(5, 4, 0, 1, 2, 3).convert().toSet());
			}

			@Override
			@Test
			public void difference(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.<Set<Integer>>//
						givenSingleArgument(Set.emptySet())//
						.givenSingleArgument(List.list(0, 1, 4, 5).convert().toSet())//
						.performActionResult(other -> set -> set.difference(other).getSetResult())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(List.list(0, 1, 2, 3).convert().toSet())//
						.thenShouldBeEqualTo(List.list(2, 3).convert().toSet());
			}

			@Override
			@Test
			public void symmetricDifference(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.<Set<Integer>>//
						givenSingleArgument(Set.emptySet())//
						.givenSingleArgument(List.list(0, 1, 4, 5).convert().toSet())//
						.performActionResult(other -> set -> set.symmetricDifference(other).getSetResult())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(List.list(0, 1, 4, 5).convert().toSet())//
						.thenShouldBeEqualTo(List.list(0, 1, 2, 3).convert().toSet())//
						.thenShouldBeEqualTo(List.list(5, 4, 2, 3).convert().toSet());
			}

		}

		@Nested
		class Basic implements SetTestBehavior.Operations.Basic {
			@Override
			@Test
			public void insert(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.givenSingleArgument(-1)//
						.givenSingleArgument(2)//
						.performActionResult(e -> set -> set.insert(e).getSetResult())//
						.thenShouldBeEqualTo(List.list(-1).convert().toSet())//
						.thenShouldBeEqualTo(List.list(2).convert().toSet())//
						.thenShouldBeEqualTo(List.list(2, 0, 1, -1, 3).convert().toSet())//
						.thenShouldBeEqualTo(List.list(0, 1, 2, 3).convert().toSet());
			}

			@Override
			@Test
			public void delete(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.givenSingleArgument(99)//
						.givenSingleArgument(2)//
						.performActionResult(element -> set -> set.delete(element).getSetResult())//
						.thenShouldBeEqualTo(Set.<Integer>emptySet())//
						.thenShouldBeEqualTo(Set.<Integer>emptySet())//
						.thenShouldBeEqualTo(List.list(0, 1, 2, 3).convert().toSet())//
						.thenShouldBeEqualTo(List.list(0, 1, 3).convert().toSet());
			}

			@Override
			@Test
			public void toList(final BDDSoftAssertions softly) {
				final var emptySet = Set.<Integer>emptySet();
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(//
								emptySet, //
								emptySet.insert(1), //
								emptySet.insert(10).insert(20).insert(30)//
						)//
						.withoutAnyArgument()//
						.performActionResult(set -> set.toList().convert().toJavaList(ArrayList::new)
								.map(list -> list.stream().sorted().toList()))//
						.thenShouldBeEqualTo(java.util.List.of())//
						.thenShouldBeEqualTo(java.util.List.of(1))//
						// Guaranteed because Integer is comparable and underground Set uses a Tree
						.thenShouldBeEqualTo(java.util.List.of(10, 20, 30));
				// Assert that set with multiple elements returns list in order
			}

		}

		@Nested
		class Joins implements SetTestBehavior.Operations.Joins {

			@Override
			@Test
			public void crossJoin(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(1, 2).convert().toSet())//
						.<Set<Integer>>//

						givenSingleArgument(Set.emptySet())//
						.givenSingleArgument(List.list(1, 2).convert().toSet())//

						.performActionResult(other -> set -> //
						set.crossJoin(other).getSetResult()//
						)//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.<Tuple2<Integer, Integer>>emptySet()//
								.insert(Tuple2.of(1, 1).getResult().successValue())//
								.insert(Tuple2.of(1, 2).getResult().successValue())//
								.insert(Tuple2.of(2, 1).getResult().successValue())//
								.insert(Tuple2.of(2, 2).getResult().successValue())//

						);//

			}

			@Override
			@Test
			public void innerJoin(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(//
								Set.emptySet(), //
								List.list(1, 2, 3, 4).convert().toSet()//
						)//
						.<Set<Integer>, Function<Integer, Predicate<Integer>>>//

						givenTwoArguments(Set.emptySet(), a -> b -> a > b)//
						.givenTwoArguments(List.list(1, 2, 3).convert().toSet(), a -> b -> a > b)//

						.performActionResult(other -> matchCondition -> list -> //
						list.innerJoin(other, matchCondition).getSetResult()//
						)//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.<Tuple2<Integer, Integer>>emptySet()//
								.insert(Tuple2.of(2, 1).getResult().successValue())//
								.insert(Tuple2.of(3, 1).getResult().successValue())//
								.insert(Tuple2.of(3, 2).getResult().successValue())//
								.insert(Tuple2.of(4, 1).getResult().successValue())//
								.insert(Tuple2.of(4, 2).getResult().successValue())//
								.insert(Tuple2.of(4, 3).getResult().successValue())//
						);//

			}

			@Test
			@DisplayName("check join methods success produce empty set for empty set argument"
					+ "even when function throwing exception or return null")
			public void handle(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCase(Set.emptySet())//
						.<Set<Integer>, Function<Integer, Predicate<Integer>>>//

						givenTwoArguments(Set.emptySet(), a -> null)//
						.givenTwoArguments(Set.emptySet(), a -> b -> null)//
						.givenTwoArguments(Set.emptySet(), a -> {//
							throw new RuntimeException("bla bla bla");
						}).givenTwoArguments(Set.emptySet(), a -> b -> {//
							throw new RuntimeException("bla bla bla");
						})//
						.performActionResult(other -> matchCondition -> set -> //
						set.innerJoin(other, matchCondition).getSetResult()//
						)//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet())//
						.thenShouldBeEqualTo(Set.emptySet());//

			}

		}

		@Nested
		class Queries implements SetTestBehavior.Operations.Queries {

			@Override
			@Test
			public void isEmpty(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.withoutAnyArgument()//
						.performActionResult(Set::isEmpty)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);
			}

			@Override
			@Test
			public void cardinality(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.withoutAnyArgument()//
						.performActionResult(Set::cardinality)//
						.thenShouldBeEqualTo(0)//
						.thenShouldBeEqualTo(4);
			}

			@Override
			@Test
			public void contains(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.givenSingleArgument(99)//
						.givenSingleArgument(2)//

						.performActionResult(element -> set -> set.contains(element))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);
			}

		}

		@Nested
		class Relations implements SetTestBehavior.Operations.Relations {

			@Override
			@Test
			public void isSubsetOf(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1).convert().toSet())//
						.<Set<Integer>>//
						givenSingleArgument(Set.emptySet())//
						.givenSingleArgument(List.list(0, 1, 4, 5).convert().toSet())//
						.performActionResult(other -> set -> set.isSubsetOf(other))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);
			}

			@Override
			@Test
			public void isSupersetOf(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(Set.emptySet(), List.list(0, 1, 2, 3).convert().toSet())//
						.<Set<Integer>>//
						givenSingleArgument(Set.emptySet())//
						.givenSingleArgument(List.list(0, 1).convert().toSet())//
						.performActionResult(other -> set -> set.isSupersetOf(other))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true);
			}

		}

	}

}
