package dev.ofekmalka.core.data_structure.map;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.ClassOrderer;
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
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.support.TestMulHelper;
import dev.ofekmalka.support.annotation.ArgumentName;
import dev.ofekmalka.support.annotation.CustomDisplayNameGenerator;
import dev.ofekmalka.support.annotation.MethodName;
import dev.ofekmalka.support.annotation.TwoArgumentNames;
import dev.ofekmalka.support.general.providers.ArgumentNameProvider;
import dev.ofekmalka.support.general.providers.MethodNameProvider;
import dev.ofekmalka.support.general.providers.TwoArgumentNamesProvider;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.tuple.Tuple2;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SoftAssertionsExtension.class)
public class UsingMapOperations {

	@Nested
	class Invalid {

		private TestMulHelper<Map<Integer, Integer>> createMapTest(final BDDSoftAssertions softly) {
			return TestMulHelper.softAssertions(softly)//
					.<Map<Integer, Integer>>//
					initializeCase(List.list(0, 1, 2, 3, 4, 5, 6, 7, 8)
							.groupByZippingValuesAsPossible(List.list(0, 1, 2, 3, 4, 5, 6, 7, 8)))//
			;
		}

		private String errorArgumentNullValue(final String methodName, final String argumentName) {
			return NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
		}

		@Nested
		class Basic implements MapTestBehavior.Operations.Invalid.Basic {
			@Override
			@ParameterizedTest
			@TwoArgumentNames(methodName = "add", //
					firstArgumentName = "mapKey", //
					secondArgumentName = "mapValue") //
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			public void add(final String methodName, final String firstArgumentName, final String secondArgumentName,
					final BDDSoftAssertions softly) {
				createMapTest(softly)//
						.<Integer, Integer>givenTwoArguments(null, 1)//
						.givenTwoArguments(1, null)
						.performActionResult(mapKey -> mapValue -> map -> map.add(mapKey, mapValue).asResult())//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, firstArgumentName))//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, secondArgumentName));
			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "remove", argumentName = "mapKey")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void remove(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				createMapTest(softly)//
						.<Integer>givenNullArgument()//
						.performActionResult(mapKey -> map -> map.remove(mapKey).asResult())//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, argumentName))//
				;
			}

		}

		@Nested
		class Transformations implements MapTestBehavior.Operations.Invalid.Transformations {

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "mapValues", argumentName = "valueMapper")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void mapValues(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				createMapTest(softly)//
						.<Function<Integer, Integer>>//
						givenNullArgument()//
						.givenSingleArgument(i -> null)//
						.givenSingleArgument(i -> {

							throw new RuntimeException();
						}).performActionResult(valueMapper -> map -> map.mapValues(valueMapper).asResult())//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, argumentName))//
						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName))//

				;
			}

		}

		@Nested
		class Queries implements MapTestBehavior.Operations.Invalid.Queries {

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "containsKey", argumentName = "searchKey")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void containsKey(final String methodName, final String argumentName,
					final BDDSoftAssertions softly) {
				createMapTest(softly)//
						.<Integer>givenNullArgument()//
						.performActionResult(searchKey -> map -> map.containsKey(searchKey))//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, argumentName))//
				;

			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "containsValue", argumentName = "searchValue")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void containsValue(final String methodName, final String argumentName,
					final BDDSoftAssertions softly) {
				createMapTest(softly)//
						.<Integer>givenNullArgument()//
						.performActionResult(searchValue -> map -> map.containsValue(searchValue))//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, argumentName))//
				;

			}

			@Override
			@ParameterizedTest
			@TwoArgumentNames(methodName = "getOptionallyWithMessage", //
					firstArgumentName = "searchKey", //
					secondArgumentName = "keyNotFoundMessage") //
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			public void getOptionallyWithMessage(final String methodName, final String firstArgumentName,
					final String secondArgumentName, final BDDSoftAssertions softly) {
				final var trackMethodCall = ErrorTracker.startTrackingFrom(methodName);
				createMapTest(softly)//
						.<Integer, String>//
						givenTwoArguments(null, "")//
						.givenTwoArguments(1, null)//
						.givenTwoArguments(1, "")//
						.givenTwoArguments(1, "       ")//
						// .givenTwoArguments(1, "12 45 xx")// message without a valid word (no 3+
						// letter word)
						.givenTwoArguments(990, "this key doesn't exist in our api")// message without a valid word (no
																					// 3+ letter word)

						.performActionResult(searchKey -> keyNotFoundMessage -> map -> //
						map.getOptionallyWithMessage(searchKey, keyNotFoundMessage))//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, firstArgumentName))//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, secondArgumentName))//

						.thenShouldHaveSameErrorMessage(

								trackMethodCall.finalizeWith(

										secondArgumentName + " must not be empty"))//
						.thenShouldHaveSameErrorMessage(
								trackMethodCall.finalizeWith(secondArgumentName + " must not be blank"))//
//						.thenShouldHaveSameErrorMessage(trackMethodCall.finalizeWith(
//								secondArgumentName + " must contain at least one valid word (3+ letters)"))

						.thenShouldHaveSameErrorMessage(
								trackMethodCall.finalizeWith("this key doesn't exist in our api"))//

				;
			}

			@Override
			@ParameterizedTest
			@MethodName(name = "getOptionally")
			@ArgumentsSource(MethodNameProvider.class)

			public void getOptionally(final String methodName, final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, String>emptyMap();

				final var key = 80;
				final var error = Map.Errors//
						.CastumArgumentMessage//
						.ERROR_DEFAULT_GET_OPTIONALLY//
						.withArgument(key)//

						.trackAndFinalize(methodName);
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, String>>//
						initializeCases(//
								emptyMap, //
								emptyMap.add(1, "Dog"))//
						.<Integer>givenSingleArgument(key)
						.performActionResult(searchKey -> map -> map.getOptionally(searchKey))//
						.thenShouldHaveSameErrorMessage(error)//
						.thenShouldHaveSameErrorMessage(error);

			}

			private static class W {
				private final int number;

				public W(final int number) {
					this.number = number;
				}

//			    public int getNumber() {
//			        return number;
//			    }

				@Override
				public int hashCode() {
					return number;
				}

				@Override
				public boolean equals(final Object obj) {
					if (this == obj)
						return true;
					if (!(obj instanceof final W other))
						return false;
					return number == other.number;
				}

				// intentionally no toString override
			}

			@Override
			@ParameterizedTest
			@MethodName(name = "getOptionally")
			@ArgumentsSource(MethodNameProvider.class)
			public void getOptionallyWithKeyObjectNotOverrideToString(final String methodName) {
				final var emptyMap = Map.<W, String>emptyMap();
				final var key = new W(80); // key without toString override

				final var expectedErrorMessage = Map.Errors.GeneralMessage.ERROR_KEY_MUST_OVERRIDE_TO_STRING
						.trackAndFinalize(methodName);

				final var result = emptyMap.getOptionally(key);

				assertThat(result.isFailure()).isTrue();
				assertThat(result.failureValue().getMessage()).isEqualTo(expectedErrorMessage);
			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "keysForValue", argumentName = "searchValue")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void keysForValue(final String methodName, final String argumentName,
					final BDDSoftAssertions softly) {
				createMapTest(softly)//
						.<Integer>givenNullArgument()//
						.performActionResult(searchValue -> map -> map.keysForValue(searchValue).getListResult())//
						.thenShouldHaveSameErrorMessage(errorArgumentNullValue(methodName, argumentName))//
				;

			}

		}

	}

	@Nested
	class ShouldSuccessfully {

		@Nested
		class Creation implements MapTestBehavior.Operations.Creation {

			@Override
			@Test
			public void shouldSuccessfullyCreateAnEmptyMap() {
				assertThat(Map.emptyMap().asResult())//
						.as("Empty map should successfully be created")//
						.matches(Result::isSuccess);
			}

			@Override
			@Test
			public void shouldSuccessfullyCreateNonEmptyMap() {
				assertThat(List.list(1).groupByZippingValuesAsPossible(List.list(1)).asResult())//
						.as("Creation non emptyMap map should be a success")//
						.matches(Result::isSuccess);

//				assertThat(List.list(1).groupBy(a -> a).asResult())//
//				.as("Creation non emptyMap map should be a success")//
//				.matches(Result::isSuccess);

			}

		}

		@Nested
		class Basic implements MapTestBehavior.Operations.Basic {

			@Override
			@Test
			public void add(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(Map.emptyMap(),
								List.list(0, 1, 2).groupByZippingValuesAsPossible(List.list(0, 1, 2)))//
						.<Integer, Integer>//
						givenTwoArguments(0, 99)// updated key-value
						.givenTwoArguments(3, 0)//
						.performActionResult(mapKey -> mapValue -> map -> map.add(mapKey, mapValue).asResult())//
						.thenShouldBeEqualTo(List.list(0).groupByZippingValuesAsPossible(List.list(99)))//
						.thenShouldBeEqualTo(List.list(3).groupByZippingValuesAsPossible(List.list(0)))//
						.thenShouldBeEqualTo(List.list(0, 1, 2).groupByZippingValuesAsPossible(List.list(99, 1, 2)))//
						.thenShouldBeEqualTo(
								List.list(0, 1, 2, 3).groupByZippingValuesAsPossible(List.list(0, 1, 2, 0)));
			}

			@Override
			@Test
			public void remove(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(Map.emptyMap(),
								List.list(0, 1, 2).groupByZippingValuesAsPossible(List.list(0, 1, 2)))//
						.givenSingleArgument(99)//
						.givenSingleArgument(2)//
						.performActionResult(mapKey -> map -> map.remove(mapKey).asResult())//
						.thenShouldBeEqualTo(Map.emptyMap())//
						.thenShouldBeEqualTo(Map.emptyMap())//
						.thenShouldBeEqualTo(List.list(0, 1, 2).groupByZippingValuesAsPossible(List.list(0, 1, 2)))//
						.thenShouldBeEqualTo(List.list(0, 1).groupByZippingValuesAsPossible(List.list(0, 1)));
			}

			@Override
			@Test
			public void toList(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, Integer>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(//
								emptyMap, //
								emptyMap.add(1, 1), //
								emptyMap.add(10, 10)//
										.add(20, 20)//
										.add(30, 30)//
						)//
						.withoutAnyArgument()//
						.performActionResult(map -> map.toList().convert().toJavaList(ArrayList::new)
								.map(list -> list.stream().sorted(Comparator.comparing(Tuple2::state)) // or .value if
																										// needed
										.toList()))//
						.thenShouldBeEqualTo(java.util.List.of())//
						.thenShouldBeEqualTo(
								java.util.List.of(Tuple2.pairBothAsStateAndValue(1).getResult().successValue()))//
						// Guaranteed because Integer is comparable and underground Map uses a Tree
						.thenShouldBeEqualTo(
								java.util.List.of(Tuple2.pairBothAsStateAndValue(10).getResult().successValue(), //
										Tuple2.pairBothAsStateAndValue(20).getResult().successValue(), //
										Tuple2.pairBothAsStateAndValue(30).getResult().successValue()//
								));
				// Assert that set with multiple elements returns list in order
			}

		}

		@Nested
		class Transformations implements MapTestBehavior.Operations.Transformations {

			@Override
			@Test
			public void mapValues(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(Map.emptyMap(),
								List.list(0, 1, 2).groupByZippingValuesAsPossible(List.list(0, 1, 2)))//
						.<Function<Integer, Integer>>//
						givenSingleArgument(i -> i * 2)
						.performActionResult(valueMapper -> map -> map.mapValues(valueMapper).asResult())//
						.thenShouldBeEqualTo(Map.emptyMap())//
						.thenShouldBeEqualTo(

								List.list(0, 1, 2).groupByZippingValuesAsPossible(List.list(0, 2, 4)

								))//

				;

			}

		}

		@Nested
		class Queries implements MapTestBehavior.Operations.Queries {

			@Override
			@Test
			public void isEmpty(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, Integer>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(//
								emptyMap, //
								emptyMap.add(1, 1))//
						.withoutAnyArgument()//
						.performActionResult(Map::isEmpty)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);

			}

			@Override
			@Test
			public void size(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, Integer>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(//
								emptyMap, //
								emptyMap.add(1, 1))//
						.withoutAnyArgument()//
						.performActionResult(Map::size)//
						.thenShouldBeEqualTo(0)//
						.thenShouldBeEqualTo(1);

			}

			@Override
			@Test
			public void containsValue(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, Integer>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(//
								emptyMap, //
								emptyMap.add(1, 1), emptyMap.add(1, 2))//
						.<Integer>givenSingleArgument(1)
						.performActionResult(searchValue -> map -> map.containsValue(searchValue))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);
			}

			@Override
			@Test
			public void containsKey(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, Integer>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, Integer>>//
						initializeCases(//
								emptyMap, //
								emptyMap.add(1, 1), //
								emptyMap.add(2, 1))//
						.<Integer>givenSingleArgument(1)
						.performActionResult(searchValue -> map -> map.containsKey(searchValue))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);
			}

			@Override
			@Test
			public void getOptionally(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, String>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, String>>//
						initializeCase(//
								// emptyMap
								emptyMap.add(1, "Dog")//
										.add(2, "Cat")//
										.add(3, "Pig")//
						)//
						.<Integer>givenSingleArgument(1)
						.performActionResult(searchValue -> map -> map.getOptionally(searchValue))//
						.thenShouldBeEqualTo("Dog")//
				;
			}

			@Override
			@Test
			public void getOptionallyWithMessage(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, String>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, String>>//
						initializeCase(//
								// emptyMap
								emptyMap.add(1, "Dog")//
										.add(2, "Cat")//
										.add(3, "Pig")//
						)//
						.<Integer>givenSingleArgument(1)
						.performActionResult(searchValue -> map -> map.getOptionallyWithMessage(searchValue, "error"))//
						.thenShouldBeEqualTo("Dog")//
				;

			}

			@Override
			@Test
			public void keys(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, String>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, String>>//
						initializeCases(//
								emptyMap, emptyMap.add(1, "Dog")//
										.add(2, "Cat")//
										.add(3, "Pig")//
						)//
						.withoutAnyArgument().performActionResult(map -> map.keysList().getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(1, 2, 3))//

				;

			}

			@Override
			@Test
			public void values(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, String>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, String>>//
						initializeCases(//
								emptyMap, emptyMap.add(1, "Dog")//
										.add(2, "Cat")//
										.add(3, "Pig")//
						)//
						.withoutAnyArgument().performActionResult(map -> map.values().getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list("Dog", "Cat", "Pig"))//

				;

			}

			@Override
			@Test
			public void keysForValue(final BDDSoftAssertions softly) {
				final var emptyMap = Map.<Integer, String>emptyMap();
				TestMulHelper//
						.softAssertions(softly)//
						.<Map<Integer, String>>//
						initializeCases(//
								emptyMap, emptyMap.add(1, "Dog")//
										.add(2, "Cat")//
										.add(3, "Dog"),
								emptyMap.add(1, "Dog")//
										.add(2, "Cat")//
										.add(3, "Pig")//
						)//
						.withoutAnyArgument().performActionResult(map -> map.keysForValue("Dog").getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(1, 3))//
						.thenShouldBeEqualTo(List.list(1))//

				;

			}

		}

	}
}
