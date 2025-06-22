package dev.ofekmalka.core.data_structure.list;

import static dev.ofekmalka.core.data_structure.list.List.Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE;
import static dev.ofekmalka.core.data_structure.list.List.Errors.EmptyListMessage.ERROR_FIRST_ELEMENT_IN_EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List.Errors;
import dev.ofekmalka.core.data_structure.list.List.Errors.GeneralMessage;
import dev.ofekmalka.core.data_structure.list.List.ExtendedFactoryOperations;
import dev.ofekmalka.core.data_structure.list.behavior.ListTestBehavior;
import dev.ofekmalka.core.data_structure.list.behavior.provider.InvalidListCreationProvider;
import dev.ofekmalka.core.data_structure.list.behavior.provider.InvalidPrimitiveListCreationsProvider;
import dev.ofekmalka.core.data_structure.map.Map;
import dev.ofekmalka.core.data_structure.set.Set;
import dev.ofekmalka.core.error.IndexErrorMessage;
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.support.TestMulHelper;
import dev.ofekmalka.support.annotation.ArgumentName;
import dev.ofekmalka.support.annotation.CustomDisplayNameGenerator;
import dev.ofekmalka.support.annotation.MethodName;
import dev.ofekmalka.support.annotation.ThreeArgumentNames;
import dev.ofekmalka.support.annotation.TwoArgumentNames;
import dev.ofekmalka.support.general.providers.ArgumentNameProvider;
import dev.ofekmalka.support.general.providers.MethodNameProvider;
import dev.ofekmalka.support.general.providers.ThreeArgumentNamesProvider;
import dev.ofekmalka.support.general.providers.TwoArgumentNamesProvider;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.tuple.Tuple2;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SoftAssertionsExtension.class)
public class UsingListOperations {

	@Nested
	public class Invalid {

		@Nested
		public class Creation implements ListTestBehavior.Operations.Invalid.Creation {

			@Override
			@ParameterizedTest
			@ArgumentsSource(InvalidListCreationProvider.class)
			public void shouldHandleInvalidListCreationScenarios(final String actualErrorMessage, //
					/*                                         */ final String expectedErrorMessage) {//

				assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);//
			}
		}

		@Nested
		public class ExtendedFactory {
			public static TestMulHelper<ExtendedFactoryOperations> createTestListOperation(
					final BDDSoftAssertions softly) {
				return TestMulHelper.softAssertions(softly).<ExtendedFactoryOperations>//
						initializeCase(List.extendedFactoryOperations());

			}

			@Nested
			public class CollectionFactory implements //
					ListTestBehavior.Operations.Invalid.ExtendedFactoryOperationsHandler.CollectionFactory {

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "createFromCollection", argumentName = "collection")
				public void createFromCollection(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					createTestListOperation(softly).<Collection<String>>//
							givenNullArgument()//
							.givenSingleArgument(Arrays.asList("1", null))

							.performActionResult(
									collection -> List -> List.createFromCollection(collection).getListResult())//
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))

							.thenShouldHaveSameErrorMessage(NullValueMessages.singleNullIndex("[1]").track("list")
									.withPrependedMethod("map").prependAndFinalize(methodName))

					;

				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "createFromStreamAndRemoveNullsValues", argumentName = "stream")
				public void createFromStreamAndRemoveNullsValues(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Stream<String>>//
							givenNullArgument()//
							.performActionResult(
									stream -> List -> List.createFromStreamAndRemoveNullsValues(stream).getListResult())//
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName));

					//
				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(TwoArgumentNamesProvider.class)
				@TwoArgumentNames(methodName = "createFilledList", //
						firstArgumentName = "numberOfElements", secondArgumentName = "elementSupplier")
				public void createFilledList(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final BDDSoftAssertions softly) {

					final var validNumberOfElements = 1;
					final Supplier<String> validElementSupplier = () -> "hello";

					createTestListOperation(softly)//
							.<Integer, Supplier<String>>//
							givenTwoArguments(0, validElementSupplier)//
							.givenTwoArguments(Errors.Constants.MAX_LIST_SIZE + 1, validElementSupplier)//

							.givenTwoArguments(validNumberOfElements, null)//
							.givenTwoArguments(validNumberOfElements, () -> null)//
							.givenTwoArguments(validNumberOfElements, () -> {//
								throw new RuntimeException("Error");
							})//
							.performActionResult(numberOfElements -> elementSupplier -> List -> List
									.createFilledList(numberOfElements, elementSupplier).getListResult())//

							.thenShouldHaveSameErrorMessage(Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE
									.withArgumentName(firstArgumentName).trackAndFinalize(methodName))

							.thenShouldHaveSameErrorMessage(
									Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.trackAndFinalize(methodName))

							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName))

					;//
				}

			}

			@Nested
			public class MathFactory implements //
					ListTestBehavior.Operations.Invalid.ExtendedFactoryOperationsHandler.MathFactory {

				@Override
				@ParameterizedTest
				@ArgumentsSource(MethodNameProvider.class)
				@MethodName(name = "generateRange") //
				public void generateRange(final String methodName, final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Integer, Integer>//
							givenTwoArguments(30, 10).givenTwoArguments(Integer.MIN_VALUE, 10)
							.performActionResult(startInclusive -> endExclusive -> List -> List
									.generateRange(startInclusive, endExclusive).getListResult())//
							.thenShouldHaveSameErrorMessage(
									GeneralMessage.ERROR_INVALID_START_END_RANGE.trackAndFinalize(methodName))

							.thenShouldHaveSameErrorMessage(
									GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.trackAndFinalize(methodName))

					;//
				}

			}

			@Nested
			public class PrimitiveListFactory

					implements //
					ListTestBehavior.Operations.Invalid.ExtendedFactoryOperationsHandler.PrimitiveListFactory {

				@Override
				@ParameterizedTest
				@ArgumentsSource(InvalidPrimitiveListCreationsProvider.class)
				public void shouldHandleInvalidListCreationScenarios(final String actualErrorMessage, //
						/*                                         */ final String expectedErrorMessage) {//
					assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);//
				}
			}

			@Nested
			public class RecursiveFactory

					implements //
					ListTestBehavior.Operations.Invalid.ExtendedFactoryOperationsHandler.RecursiveFactory {

				@Override
				@ParameterizedTest
				@ArgumentsSource(ThreeArgumentNamesProvider.class)
				@ThreeArgumentNames(methodName = "iterateWithSeed", //
						firstArgumentName = "seed", //
						secondArgumentName = "transformationFunction", //
						thirdArgumentName = "iterations") //
				public void iterateWithSeed(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final String thirdArgumentName,
						final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Integer, Function<Integer, Integer>, Integer>givenThreeArguments(null, a -> a, 10)//
							.givenThreeArguments(0, null, 10)//
							.givenThreeArguments(0, a -> null, 10)//
							.givenThreeArguments(0, a -> {//
								throw new RuntimeException("Error");
							}, 100)//
							.givenThreeArguments(0, a -> a, 0)

							.givenThreeArguments(0, a -> a, List.Errors.Constants.MAX_LIST_SIZE + 1)

							.performActionResult(seed -> transformationFunction -> iterations ->

							List -> List.iterateWithSeed(seed, transformationFunction, iterations).getListResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(Errors.CastumArgumentMessage//
									.ERROR_NON_POSITIVE_VALUE//
									.withArgumentName(thirdArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(
									GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.trackAndFinalize(methodName));

				}

				@Disabled
				@Override
				@ParameterizedTest
				@ArgumentsSource(ThreeArgumentNamesProvider.class)
				@ThreeArgumentNames(methodName = "unfoldFromSeed", //
						firstArgumentName = "initialSeed", //
						secondArgumentName = "generatorFunction", //
						thirdArgumentName = "successOutcome")
				public void unfoldFromSeed(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final String thirdArgumentName,
						final BDDSoftAssertions softly) {

					final Function<Integer, Tuple2<String, Integer>> validSuccessOutcome = s -> Tuple2
							.of(s.toString(), s).getResult().successValue();
					final Function<If<Integer>, Condition<Integer>> validGeneratorFunction = validator -> validator
							.is(i -> true);
					final var validInitialSeed = 0;
					final var cases = createTestListOperation(softly)//
							.<Integer, Function<If<Integer>, Condition<Integer>>, Function<Integer, Tuple2<String, Integer>>>

							givenThreeArguments(null, validGeneratorFunction, validSuccessOutcome)//
							.givenThreeArguments(validInitialSeed, null, validSuccessOutcome)//
							.givenThreeArguments(validInitialSeed, validGeneratorFunction, null)//
							.givenThreeArguments(validInitialSeed, validator -> null, validSuccessOutcome)//

							.givenThreeArguments(validInitialSeed, validator -> {//
								throw new RuntimeException("Error");
							}, validSuccessOutcome)//
							.givenThreeArguments(99,
									validator -> validator.is(i -> i > 100, "number must be grater than 100"),
									validSuccessOutcome)//
							.givenThreeArguments(validInitialSeed, validGeneratorFunction, a -> null)//
							.givenThreeArguments(validInitialSeed, validGeneratorFunction, a -> {//
								throw new RuntimeException("Error");
							})

							.givenThreeArguments(1,
									validator -> validator.is(i -> i > 0, "number must be grater than 100"),
									number -> Tuple2.of(number + "", number + 1).getResult().successValue())

					;//

					final var action = cases.performActionResult(initialSeed -> generatorFunction -> successOutcome ->

					List -> List.unfoldFromSeed(initialSeed, generatorFunction, successOutcome).getListResult());//

					final var errorFirstArgumentIsNull = NullValueMessages.argument(firstArgumentName)
							.trackAndFinalize(methodName);
					final var errorSecondArgumentIsNull = NullValueMessages.argument(firstArgumentName)
							.trackAndFinalize(methodName);
					final var errorThirdArgumentIsNull = NullValueMessages.argument(firstArgumentName)
							.trackAndFinalize(methodName);
					final var errorFunctionReturnNullValue = CheckedOperation.ERROR_MESSAGE_NULL_RESULT
							.trackAndFinalize(methodName);
					final var errorFunctionThrowingRuntimeException = CheckedOperation.ERROR_MESSAGE_NULL_RESULT
							.trackAndFinalize(methodName);

					action.thenShouldHaveSameErrorMessage(errorFirstArgumentIsNull)
							.thenShouldHaveSameErrorMessage(errorSecondArgumentIsNull)
							.thenShouldHaveSameErrorMessage(errorThirdArgumentIsNull)
							.thenShouldHaveSameErrorMessage(errorFunctionReturnNullValue)
							.thenShouldHaveSameErrorMessage(errorFunctionThrowingRuntimeException)
							.thenShouldHaveSameErrorMessage(ErrorTracker.startTrackingFrom(methodName)
									.finalizeWith("number must be grater than 100"))
							.thenShouldHaveSameErrorMessage(errorFunctionReturnNullValue)
							.thenShouldHaveSameErrorMessage(errorFunctionThrowingRuntimeException)
							.thenShouldHaveSameErrorMessage(
									Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.trackAndFinalize(methodName));

				}

			}

			@Nested

			public class TupleFactory

					implements //
					ListTestBehavior.Operations.Invalid.ExtendedFactoryOperationsHandler.TupleFactory {

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "unzipListOfTuples", argumentName = "tupleList")
				public void unzipListOfTuples(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<List<Tuple2<Integer, String>>>//
							givenNullArgument()//
							.givenSingleArgument(List.failureInstance())//
							.performActionResult(nestedList -> List -> List.unzipListOfTuples(nestedList))//
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(Errors.CastumArgumentMessage//
									.ERROR_PROCESSING_JOINED_LIST//
									.withArgumentName(argumentName).trackAndFinalize(methodName));
				}

			}

		}

		@Nested
		public class OnEmptyListBehavior {

			final List<?> EMPTY_LIST = List.emptyList();

			public static TestMulHelper<List<Integer>> createTestEmptyListOperation(final BDDSoftAssertions softly) {
				return TestMulHelper.softAssertions(softly).<List<Integer>>//
						initializeCase(List.emptyList());

			}

			@Nested
			class Basic implements ListTestBehavior.Operations.Invalid.OnEmptyList.Basic {
				@Override
				public void shouldRemainEmpty(final BDDSoftAssertions softly) {
					final var actualIsEmpty = EMPTY_LIST.isEmpty().successValue();
					final var actualIsNotEmpty = EMPTY_LIST.isNotEmpty().successValue();

					softly.then(actualIsEmpty).isTrue();
					softly.then(actualIsNotEmpty).isFalse();
				}

				@Override
				@Test
				public void shouldContainNoElements() {
					final var actualSize = EMPTY_LIST.size().successValue();

					assertThat(actualSize).isEqualTo(0);
				}

				@Override
				@Test
				public void shouldReportFailureWhenAccessingTheFirstElement() {
					final var actualFirstElementError = EMPTY_LIST.firstElementOption().failureValue().getMessage();
					final var expectedError =

							ERROR_FIRST_ELEMENT_IN_EMPTY_LIST.trackAndFinalize("firstElementOption");

					assertThat(actualFirstElementError).isEqualTo(expectedError);
				}

				@Override
				@Test
				public void shouldDisplayTheCorrectRepresentationForEmptiness() {
					final var actualRepresentList = EMPTY_LIST.representList().successValue();

					assertThat(actualRepresentList).isEqualTo("[NIL]");
				}
			}

			@Nested
			class Mutation implements ListTestBehavior.Operations.Invalid.OnEmptyList.Mutation {

				@Override
				@ParameterizedTest
				@ArgumentsSource(MethodNameProvider.class)
				@MethodName(name = "setFirstElement")
				public void setFirstElement(final String methodName, final BDDSoftAssertions softly) {

					final var errorEmptyList = Errors.EmptyListMessage.ERROR_SET_FIRST_ELEMENT_IN_EMPTY_LIST
							.trackAndFinalize(methodName);
					createTestEmptyListOperation(softly)//
							.givenSingleArgument(99)
							.performActionResult(
									firstElement -> list -> list.setFirstElement(firstElement).getListResult())//
							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(MethodNameProvider.class)
				@MethodName(name = "restElements")
				public void restElements(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList = List.Errors.EmptyListMessage.ERROR_REST_ELEMENTS_IN_EMPTY_LIST
							.trackAndFinalize(methodName);
					createTestEmptyListOperation(softly)//
							.withoutAnyArgument()//
							.performActionResult(list -> list.restElements().getListResult())//
							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "setElementAtIndex")
				@ArgumentsSource(MethodNameProvider.class)
				public void setElementAtIndex(final String methodName, final BDDSoftAssertions softly) {
					final var errorNegativeValue =

							Errors.CastumArgumentMessage.ERROR_NEGATIVE_VALUE.withArgumentName("index")
									.trackAndFinalize(methodName);

					final var errorEmptyList =

							Errors.EmptyListMessage.ERROR_SET_ELEMENT_AT_INDEX_IN_EMPTY_LIST
									.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(List.emptyList())//
							.<Integer, String>//
							givenTwoArguments(-2, "Ofek")

							.givenTwoArguments(2, "Ofek")

							.performActionResult(
									index -> patch -> list -> list.setElementAtIndex(index, patch).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNegativeValue)

							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "removeByIndex")
				@ArgumentsSource(MethodNameProvider.class)
				public void removeByIndex(final String methodName, final BDDSoftAssertions softly) {

					final var errorEmptyList = Errors.EmptyListMessage.ERROR_REMOVE_BY_INDEX_IN_EMPTY_LIST
							.trackAndFinalize(methodName);//
					final var errorNegativeValue =

							Errors.CastumArgumentMessage.ERROR_NEGATIVE_VALUE.withArgumentName("index")
									.trackAndFinalize(methodName);

					createTestEmptyListOperation(softly)//
							.givenSingleArgument(-8)//

							.givenSingleArgument(8)//
							.performActionResult(index -> list -> list.removeByIndex(index).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNegativeValue)

							.thenShouldHaveSameErrorMessage(errorEmptyList);

				}

				@Override
				@ParameterizedTest
				@MethodName(name = "updatedAllBetween")
				@ArgumentsSource(MethodNameProvider.class)
				public void updatedAllBetween(final String methodName, final BDDSoftAssertions softly) {

					final var errorEmptyList = Errors.EmptyListMessage.ERROR_UPDATED_ALL_BETWEEN_IN_EMPTY_LIST
							.trackAndFinalize(methodName);

					createTestEmptyListOperation(softly)//

							.<Integer, Integer, Integer>//
							givenThreeArguments(2, 4, 99)

							.performActionResult(fromIndex -> untilIndex -> element -> list -> list
									.updatedAllBetween(fromIndex, untilIndex, element).getListResult())//

							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "updatedAllFrom")
				@ArgumentsSource(MethodNameProvider.class)
				public void updatedAllFrom(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList =

							Errors.EmptyListMessage.ERROR_UPDATED_ALL_BETWEEN_IN_EMPTY_LIST.track("updatedAllBetween")
									.prependAndFinalize(methodName);//
					createTestEmptyListOperation(softly)//

							.<Integer, Integer>//
							givenTwoArguments(2, 99)

							.performActionResult(fromIndex -> element -> list -> list.updatedAllFrom(fromIndex, element)
									.getListResult())//

							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "updatedAllUntil")
				@ArgumentsSource(MethodNameProvider.class)
				public void updatedAllUntil(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList =

							Errors.EmptyListMessage.ERROR_UPDATED_ALL_BETWEEN_IN_EMPTY_LIST.track("updatedAllBetween")
									.prependAndFinalize(methodName);//

					createTestEmptyListOperation(softly)//

							.<Integer, Integer>//
							givenTwoArguments(2, 99)

							.performActionResult(untilIndex -> element -> list -> list
									.updatedAllUntil(untilIndex, element).getListResult())//

							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

			}

			@Nested
			class ElementQuerying implements ListTestBehavior.Operations.Invalid.OnEmptyList.ElementQuerying {

				@Override
				@ParameterizedTest
				@MethodName(name = "first")
				@ArgumentsSource(MethodNameProvider.class)
				public void first(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList = Errors.EmptyListMessage.ERROR_FIRST_IN_EMPTY_LIST
							.trackAndFinalize(methodName);//

					createTestEmptyListOperation(softly)//

							.<Predicate<Integer>>//
							givenSingleArgument(number -> number < 2)//
							.performActionResult(predicate -> list -> //
							list.first(predicate))//
							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "indexWhere")
				@ArgumentsSource(MethodNameProvider.class)
				public void indexWhere(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList = Errors.EmptyListMessage.ERROR_INDEX_WHERE_IN_EMPTY_LIST
							.trackAndFinalize(methodName);//

					createTestEmptyListOperation(softly)//

							.<Predicate<Integer>>//
							givenSingleArgument(number -> number < 2)//
							.performActionResult(predicate -> list -> //
							list.indexWhere(predicate))//
							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "indexOf")
				@ArgumentsSource(MethodNameProvider.class)
				public void indexOf(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList =

							Errors.EmptyListMessage.ERROR_INDEX_OF_IN_EMPTY_LIST.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(List.emptyList())//
							.givenSingleArgument("Dan")//
							.performActionResult(element -> list -> //
							list.indexOf(element))//
							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

				// Result lastOption();
				@Override
				@ParameterizedTest
				@MethodName(name = "last")
				@ArgumentsSource(MethodNameProvider.class)
				public void last(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList = Errors.EmptyListMessage.ERROR_LAST_IN_EMPTY_LIST
							.trackAndFinalize(methodName);//

					createTestEmptyListOperation(softly)//
							.withoutAnyArgument().performActionResult(list -> list.last().getListResult())//
							.thenShouldHaveSameErrorMessage(errorEmptyList);
				}

			}

			@Nested
			class MonadicTransformation
					implements ListTestBehavior.Operations.Invalid.OnEmptyList.MonadicTransformation {

				@Override
				@ParameterizedTest
				@ArgumentsSource(MethodNameProvider.class)
				@MethodName(name = "reduce")
				public void reduce(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList = Errors.EmptyListMessage.ERROR_REDUCE_IN_EMPTY_LIST
							.trackAndFinalize(methodName);
					createTestEmptyListOperation(softly)//
							.<Function<Integer, Function<Integer, Integer>>>//
							givenSingleArgument(a -> b -> a + b)//
							.performActionResult(elementTransformer -> list -> list.reduce(elementTransformer))//
							.thenShouldHaveSameErrorMessage(errorEmptyList);//
				}

			}



			@Nested
			class Splitting implements ListTestBehavior.Operations.Invalid.OnEmptyList.Splitting {
				@Override
				@ParameterizedTest
				@ArgumentsSource(MethodNameProvider.class)
				@MethodName(name = "firstElementAndRestElementsOption")
				public void firstElementAndRestElementsOption(final String methodName, final BDDSoftAssertions softly) {
					final var errorEmptyList = Errors.EmptyListMessage.ERROR_FIRST_ELEMENT_AND_REST_ELEMENTS_IN_EMPTY_LIST
							.trackAndFinalize(methodName);
					createTestEmptyListOperation(softly)//
							.withoutAnyArgument()//
							.performActionResult(List::firstElementAndRestElementsOption)//
							.thenShouldHaveSameErrorMessage(errorEmptyList); //
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "splitAt")
				@ArgumentsSource(MethodNameProvider.class)
				public void splitAt(final String methodName, final BDDSoftAssertions softly) {

					final var errorEmptyList = Errors.EmptyListMessage.ERROR_SPLIT_AT_IN_EMPTY_LIST
							.trackAndFinalize(methodName);
					final var errorNegativeValue =

							Errors.CastumArgumentMessage.ERROR_NEGATIVE_VALUE.withArgumentName("index")
									.trackAndFinalize(methodName);

					createTestEmptyListOperation(softly)//

							.<Integer>//
							givenSingleArgument(-1)//
							.givenSingleArgument(2)//
							.performActionResult(index -> list -> list.splitAt(index))//

							.thenShouldHaveSameErrorMessage(errorNegativeValue)//

							.thenShouldHaveSameErrorMessage(errorEmptyList);

				}
			}

		}

		@Nested
		public class OnValidNonEmptyListBehavior {

			public static TestMulHelper<List<Integer>> createTestListOperation(final BDDSoftAssertions softly) {
				return TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.extendedFactoryOperations().generateRange(0, 5));

			}

			@Nested
			public class Aggregation implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Aggregation {

				@Override
				@ParameterizedTest
				@ArgumentsSource(TwoArgumentNamesProvider.class)
				@TwoArgumentNames(methodName = "scanLeft", //
						firstArgumentName = "zero", //
						secondArgumentName = "accumulatorOp") //
				public void scanLeft(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final BDDSoftAssertions softly) {

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<Integer, Function<Integer, Function<Integer, Integer>>>

							givenTwoArguments(null, a -> b -> a + b)//
							.givenTwoArguments(0, null)//
							.givenTwoArguments(0, a -> b -> null)//
							.givenTwoArguments(0, a -> b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(identity -> accumulator -> list -> list.scanLeft(identity, accumulator)
									.getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//

				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(TwoArgumentNamesProvider.class)
				@TwoArgumentNames(methodName = "scanRight", //
						firstArgumentName = "zero", //
						secondArgumentName = "accumulatorOp") //
				public void scanRight(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final BDDSoftAssertions softly) {
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<Integer, Function<Integer, Function<Integer, Integer>>>

							givenTwoArguments(null, a -> b -> a + b)//
							.givenTwoArguments(0, null)//
							.givenTwoArguments(0, a -> b -> null)//
							.givenTwoArguments(0, a -> b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(identity -> accumulator -> list -> list
									.scanRight(identity, accumulator).getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(TwoArgumentNamesProvider.class)
				@TwoArgumentNames(methodName = "foldLeft", //
						firstArgumentName = "identity", //
						secondArgumentName = "accumulator") //
				public void foldLeft(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Integer, Function<Integer, Function<Integer, Integer>>>

							givenTwoArguments(null, a -> b -> a + b)//
							.givenTwoArguments(0, null)//
							.givenTwoArguments(0, a -> b -> null)//
							.givenTwoArguments(0, a -> b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(
									identity -> accumulator -> list -> list.foldLeft(identity, accumulator))//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//

				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(TwoArgumentNamesProvider.class)
				@TwoArgumentNames(methodName = "foldRight", //
						firstArgumentName = "identity", //
						secondArgumentName = "accumulator") //
				public void foldRight(final String methodName, final String firstArgumentName,
						final String secondArgumentName, final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Integer, Function<Integer, Function<Integer, Integer>>>

							givenTwoArguments(null, a -> b -> a + b)//
							.givenTwoArguments(0, null)//

							.givenTwoArguments(0, a -> b -> null)//
							.givenTwoArguments(0, a -> b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(
									identity -> accumulator -> list -> list.foldRight(identity, accumulator))//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))//

							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

			}

			@Nested
			public class ElementQuerying
					implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.ElementQuerying {

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "first", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void first(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
					final var errorElementNotFound = Errors.GeneralMessage.ERROR_ELEMENT_NOT_FOUND
							.trackAndFinalize(methodName);//
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(//
									List.list("Elon", "David"))//
							.<Predicate<String>>//
							givenNullArgument()//
							.givenSingleArgument(name -> name.contains("A"))//
							.performActionResult(predicate -> list -> //
							list.first(predicate))//
							.thenShouldHaveSameErrorMessage(errorNullValue)
							.thenShouldHaveSameErrorMessage(errorElementNotFound);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "isEqualsWithoutConsiderationOrder", argumentName = "otherList")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void isEqualsWithoutConsiderationOrder(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					{

						final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
								.ERROR_PROCESSING_JOINED_LIST//
								.withArgumentName(argumentName)//
								.trackAndFinalize(methodName);//

						final var errorNullValue = NullValueMessages//
								.argument(argumentName)//
								.trackAndFinalize(methodName);//
						//
						TestMulHelper//
								.softAssertions(softly)//
								.<List<Integer>>//
								initializeCase(//
										List.list(0, 1, 2, 3, 4, 5, 6, 7))//
								.<List<Integer>>//

								givenNullArgument().givenSingleArgument(List.failureInstance())

								.performActionResult(
										otherList -> list -> list.isEqualsWithoutConsiderationOrder(otherList))//

								.thenShouldHaveSameErrorMessage(errorNullValue)//
								.thenShouldHaveSameErrorMessage(failedProcessErrorMessage);

					}
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "allEqualTo", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void allEqualTo(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(//
									List.list("Elon", "David"))//
							.<String>givenNullArgument()//
							.performActionResult(element -> list -> //
							list.allEqualTo(element))//
							.thenShouldHaveSameErrorMessage(errorNullValue);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "contains", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void contains(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(//
									List.list("Elon", "David"))//
							.<String>givenNullArgument()//
							.performActionResult(element -> list -> //
							list.contains(element))//
							.thenShouldHaveSameErrorMessage(errorNullValue);

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "endsWith", argumentName = "suffix")
				@ArgumentsSource(ArgumentNameProvider.class)

				public void endsWith(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName(argumentName)//
							.trackAndFinalize(methodName);//

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<List<Integer>>//

							givenNullArgument()//
							.givenSingleArgument(List.failureInstance())//
							.performActionResult(joinedList -> list -> list.endsWith(joinedList))//
							//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "mkStr", argumentName = "sep")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void mkStr(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(2, 3))//
							.<String>givenNullArgument()//
							.performActionResult(step -> list -> list.mkStr(step))//
							.thenShouldHaveSameErrorMessage(errorNullValue);//

				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "isEqualTo", argumentName = "other")
				public void isEqualTo(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
					final var errorInstanceOf = Errors.CastumArgumentMessage.ERROR_NOT_INSTANCE_OF_LIST
							.withArgumentName(argumentName)

							.trackAndFinalize(methodName);

					final var errorProccess = Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
							.withArgumentName(argumentName)

							.trackAndFinalize(methodName);

					createTestListOperation(softly)//
							.<Object>givenNullArgument()//
							.givenSingleArgument(123)//
							.givenSingleArgument(List.failureInstance())//
							.performActionResult(other -> list -> list.isEqualTo(other))//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(errorInstanceOf)//
							.thenShouldHaveSameErrorMessage(errorProccess);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "indexOf", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void indexOf(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					final var errorIndexOfElementNotFound = Errors.CastumValueMessage.ERROR_INDEX_OF_ELEMENT_NOT_FOUND
							.withValue("Dan").trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(//
									List.list("Elon", "David"))//
							.<String>//
							givenNullArgument()//
							.givenSingleArgument("Dan")//
							.performActionResult(element -> list -> list.indexOf(element))//
							.thenShouldHaveSameErrorMessage(errorNullValue)//

							.thenShouldHaveSameErrorMessage(errorIndexOfElementNotFound);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "indexWhere", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void indexWhere(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorElementNotFound = Errors.GeneralMessage.ERROR_ELEMENT_NOT_FOUND
							.trackAndFinalize(methodName);//
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(//
									List.list("Elon", "David"))//
							.<Predicate<String>>//
							givenNullArgument()//
							.givenSingleArgument(name -> name.contains("A"))//
							.performActionResult(predicate -> list -> //
							list.indexWhere(predicate))//
							.thenShouldHaveSameErrorMessage(errorNullValue)
							.thenShouldHaveSameErrorMessage(errorElementNotFound);
				}

			}

			@Nested
			public class Filtering implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Filtering {

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "filter", argumentName = "predicate")
				public void filtering(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<Predicate<Integer>>//

							givenNullArgument().givenSingleArgument(a -> null)//
							.givenSingleArgument(a -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.filter(predicate).getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//

							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//

				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "exclude", argumentName = "predicate")
				public void excluding(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<Predicate<Integer>>//

							givenNullArgument().givenSingleArgument(a -> null)//
							.givenSingleArgument(a -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.exclude(predicate).getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//

							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//

				}

			}

			@Nested
			public class Grouping implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Grouping {

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "groupBy", argumentName = "keyMapper")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void groupBy(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<Function<Integer, Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(keyMapper -> list -> list.groupBy(keyMapper).asResult())//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "groupByZippingValuesAsPossible", argumentName = "joinedList")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void groupByZippingValuesAsPossible(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages.argument(argumentName).track("zipAsPossible")
							.withPrependedMethod("foldLeft").prependAndFinalize(methodName);

					final var failedProcessErrorMessage = Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
							.withArgumentName(argumentName)

							.track("zipAsPossible").withPrependedMethod("foldLeft").prependAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<List<Integer>>//

							givenNullArgument().givenSingleArgument(List.failureInstance())//

							.performActionResult(
									joinedList -> list -> list.groupByZippingValuesAsPossible(joinedList).asResult())//
							.thenShouldHaveSameErrorMessage(errorNullValue)//

							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage)//
					;
				}

			}

			@Nested
			public class Mutation implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Mutation {

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "setFirstElement", argumentName = "firstElement")
				public void setFirstElement(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue = NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
					TestMulHelper.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(List.list(1, 2, 3, 4))//
							.<Integer>givenNullArgument()
							.performActionResult(
									firstElement -> list -> list.setFirstElement(firstElement).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNullValue);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "removeByIndex", argumentName = "index")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void removeByIndex(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final Function<Integer, String> errorIndexIsNotInRange = index -> //
					IndexErrorMessage//
							.forIndex(index)//
							.between(0)//
							.to(6).trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7))//
							.givenSingleArgument(8)//
							.givenSingleArgument(-2)//

							.performActionResult(index -> list -> list.removeByIndex(index).getListResult())//

							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(8))
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(-2));

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "updatedAllBetween", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void updatedAllBetween(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue = //
							NullValueMessages//
									.argument(argumentName)//
									.trackAndFinalize(methodName);//

					final var errorFromIndexIsNotLessThanUntilIndex = //
							Errors//
									.GeneralMessage//
									.ERROR_FROM_INDEX_IS_NOT_LESS_THAN_UNTIL_INDEX//
									.trackAndFinalize(methodName);//

					final var errorIndexIsNotInRangeForUntil = //
							IndexErrorMessage//
									//
									.forIndex(10)//
									.between(0)//
									.toExclusive(9)//
									.trackAndFinalize(methodName);//
					final var errorIndexIsNotInRangeForFrom = //
							IndexErrorMessage//
									//
									.forIndex(-1)//
									.between(0)//
									.toExclusive(9)//
									.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7, 8))//
							.<Integer, Integer, Integer>//
							givenThreeArguments(-1, 3, 99)//

							.givenThreeArguments(2, 11, 99).givenThreeArguments(2, 3, 99)//

							.givenThreeArguments(2, 2, 99).givenThreeArguments(2, 1, 99).givenThreeArguments(2, 3, null)
							.performActionResult(fromIndex -> untilIndex -> element -> list -> list
									.updatedAllBetween(fromIndex, untilIndex, element).getListResult())//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRangeForFrom)//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRangeForUntil)//
							.thenShouldHaveSameErrorMessage(errorFromIndexIsNotLessThanUntilIndex)//
							.thenShouldHaveSameErrorMessage(errorFromIndexIsNotLessThanUntilIndex)//
							.thenShouldHaveSameErrorMessage(errorFromIndexIsNotLessThanUntilIndex)//
							.thenShouldHaveSameErrorMessage(errorNullValue);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "updatedAllFrom", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void updatedAllFrom(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue =

							NullValueMessages.argument(argumentName).track("updatedAllBetween")
									.prependAndFinalize(methodName);// ;//

					final var errorIndexIsNotInRangeForFrom1 = IndexErrorMessage.forIndex(-1).between(0).toExclusive(9)
							.track("updatedAllBetween").prependAndFinalize(methodName);// ;
					final var errorIndexIsNotInRangeForFrom2 = IndexErrorMessage.forIndex(10).between(0).toExclusive(9)
							.track("updatedAllBetween").prependAndFinalize(methodName);// ;
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7, 8))//
							.<Integer, Integer>//
							givenTwoArguments(-1, 99)

							.givenTwoArguments(10, 99)

							.givenTwoArguments(2, null)
							.performActionResult(fromIndex -> element -> list -> list.updatedAllFrom(fromIndex, element)
									.getListResult())//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRangeForFrom1)//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRangeForFrom2)//

							.thenShouldHaveSameErrorMessage(errorNullValue);

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "updatedAllUntil", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void updatedAllUntil(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue =

							NullValueMessages.argument(argumentName).track("updatedAllBetween")
									.prependAndFinalize(methodName);//

					final var errorIndexIsNotInRangeForUntil1 = IndexErrorMessage.forIndex(-2).between(0).toExclusive(9)
							.track("updatedAllBetween").prependAndFinalize(methodName);//
					final var errorIndexIsNotInRangeForUntil2 = IndexErrorMessage.forIndex(9).between(0).toExclusive(9)
							.track("updatedAllBetween").prependAndFinalize(methodName);//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7, 8))//
							.<Integer, Integer>//
							givenTwoArguments(-1, 99)

							.givenTwoArguments(10, 99)

							.givenTwoArguments(2, null)
							.performActionResult(untilIndex -> element -> list -> list
									.updatedAllUntil(untilIndex, element).getListResult())//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRangeForUntil1)//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRangeForUntil2)//

							.thenShouldHaveSameErrorMessage(errorNullValue);

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "setElementAtIndex", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void setElementAtIndex(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final Function<Integer, String> errorIndexIsNotInRange = index -> //
					IndexErrorMessage//
							.forIndex(index)//
							.between(0)//
							.to(3)//
							.trackAndFinalize(methodName);//

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCase(//
									List.list("Odel", "Dani", "Yosi", "Gil"))//
							.<Integer, String>//
							givenTwoArguments(-2, "Ofek")

							.givenTwoArguments(20, "Ofek")

							.givenTwoArguments(2, null)//
							.performActionResult(
									index -> element -> list -> list.setElementAtIndex(index, element).getListResult())//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(-2))//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(20))//
							.thenShouldHaveSameErrorMessage(errorNullValue);
				}

			}

			@Nested
			public class Transformation
					implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Transformation {

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "unzip", argumentName = "unzipper")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void unzip(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<Function<Integer, Tuple2<Integer, Integer>>>//

							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//

							.performActionResult(unzipper -> list -> list.unzip(unzipper))//
							.thenShouldHaveSameErrorMessage(errorNullValue)

							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));
				}

				

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)

				@ArgumentName(methodName = "map", argumentName = "elementTransformer")
				public void mapping(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Function<Integer, String>>//
							givenNullArgument()

							.givenSingleArgument(a -> null)//

							.givenSingleArgument(a -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(
									elementTransformer -> list -> list.map(elementTransformer).getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//

							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

			}

			@Nested
			public class SubList implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.SubList {

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "startsWith", argumentName = "sub")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void startsWith(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName(argumentName)//
							.trackAndFinalize(methodName);//

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<List<Integer>>//

							givenNullArgument()//
							.givenSingleArgument(List.failureInstance())//
							.performActionResult(sub -> list -> list.startsWith(sub))//
							//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "hasSubList", argumentName = "sub")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void hasSubList(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName(argumentName)//
							.trackAndFinalize(methodName);//

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<List<Integer>>//

							givenNullArgument()//
							.givenSingleArgument(List.failureInstance())//
							.performActionResult(sub -> list -> list.hasSubList(sub))//
							//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage);
				}

			}

			@Nested
			public class PredicateMatching
					implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.PredicateMatching {

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "anyMatch", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void anyMatch(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<Predicate<Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.anyMatch(predicate))//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "allMatch", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void allMatch(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<Predicate<Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.allMatch(predicate))//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "noneMatch", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void noneMatch(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<Predicate<Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.noneMatch(predicate))//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

			}

			@Nested
			public class Padding implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Padding {

				@Override
				@ParameterizedTest
				@MethodName(name = "paddingRightWithEmptyResult")
				@ArgumentsSource(MethodNameProvider.class)
				public void paddingRightWithEmptyResult(final String methodName, final BDDSoftAssertions softly) {

					final var errorSizeLimitExceeded = Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED
							.trackAndFinalize(methodName);

					final var errorNegativeValue = Errors.CastumArgumentMessage.ERROR_NEGATIVE_VALUE
							.withArgumentName("targetLength").trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2))//
							.givenSingleArgument(Errors.Constants.MAX_LIST_SIZE + 1)//
							.givenSingleArgument(-1)//

							.performActionResult(targetLength -> list -> list.paddingRightWithEmptyResult(targetLength)
									.getListResult())//

							.thenShouldHaveSameErrorMessage(errorSizeLimitExceeded)
							.thenShouldHaveSameErrorMessage(errorNegativeValue);

				}

				@Override
				@ParameterizedTest
				@MethodName(name = "paddingLeftWithEmptyResult")
				@ArgumentsSource(MethodNameProvider.class)
				public void paddingLeftWithEmptyResult(final String methodName, final BDDSoftAssertions softly) {

					final var errorSizeLimitExceeded = Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED
							.trackAndFinalize(methodName);

					final var errorNegativeValue = Errors.CastumArgumentMessage.ERROR_NEGATIVE_VALUE
							.withArgumentName("targetLength").trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2))//
							.givenSingleArgument(Errors.Constants.MAX_LIST_SIZE + 1)//
							.givenSingleArgument(-1)//

							.performActionResult(targetLength -> list -> list.paddingLeftWithEmptyResult(targetLength)
									.getListResult())//

							.thenShouldHaveSameErrorMessage(errorSizeLimitExceeded)
							.thenShouldHaveSameErrorMessage(errorNegativeValue);
				}

			}

			@Nested
			public class Splitting implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Splitting {

				@Override
				@ParameterizedTest
				@MethodName(name = "splitAt")
				@ArgumentsSource(MethodNameProvider.class)
				public void splitAt(final String methodName, final BDDSoftAssertions softly) {

					final Function<Integer, String> errorIndexIsNotInRange = index -> //
					IndexErrorMessage//
							.forIndex(index)//
							.between(0)//
							.to(7).trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<Integer>//
							givenSingleArgument(-1)//
							.givenSingleArgument(20)//

							.performActionResult(index -> list -> list.splitAt(index))//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(-1))//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(20))//

					;
				}

			}

			@Nested
			public class Slicing implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Slicing {
				//
				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "takeAtMost", argumentName = "n")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void takeAtMost(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNonPositiveValue =

							ERROR_NON_POSITIVE_VALUE.withArgumentName(argumentName).trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7))//
							.givenSingleArgument(0)//
							.givenSingleArgument(-99)

							.performActionResult(n -> list -> list.takeAtMost(n).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNonPositiveValue)

							.thenShouldHaveSameErrorMessage(errorNonPositiveValue);

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "dropAtMost", argumentName = "n")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void dropAtMost(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNonPositiveValue = //
							ERROR_NON_POSITIVE_VALUE.withArgumentName(argumentName).trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7))//
							.givenSingleArgument(0)//
							.givenSingleArgument(-99)//
							.performActionResult(n -> list -> list.dropAtMost(n).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNonPositiveValue)//
							.thenShouldHaveSameErrorMessage(errorNonPositiveValue);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "takeRightAtMost", argumentName = "n")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void takeRightAtMost(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNonPositiveValue = //
							ERROR_NON_POSITIVE_VALUE.withArgumentName(argumentName).trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7))//
							.givenSingleArgument(0)//
							.givenSingleArgument(-99)//
							.performActionResult(n -> list -> list.takeRightAtMost(n).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNonPositiveValue)//
							.thenShouldHaveSameErrorMessage(errorNonPositiveValue);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "dropRightAtMost", argumentName = "n")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void dropRightAtMost(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNonPositiveValue = //
							ERROR_NON_POSITIVE_VALUE.withArgumentName(argumentName).trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7))//
							.givenSingleArgument(0)//
							.givenSingleArgument(-99)//
							.performActionResult(n -> list -> list.dropRightAtMost(n).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNonPositiveValue)//
							.thenShouldHaveSameErrorMessage(errorNonPositiveValue);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "getSublistInRange")
				@ArgumentsSource(MethodNameProvider.class)
				public void getSublistInRange(final String methodName, final BDDSoftAssertions softly) {

					final Function<Integer, String> errorIndexIsNotInRange = index -> //
					IndexErrorMessage//
							.forIndex(index)//
							.between(0)//
							.to(7).trackAndFinalize(methodName);
					final var errorFromIndexIsNotLessThanUntilIndex = //

							Errors.GeneralMessage.ERROR_FROM_INDEX_IS_NOT_LESS_THAN_UNTIL_INDEX
									.trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<Integer, Integer>//
							givenTwoArguments(-8, 40)//
							.givenTwoArguments(8, 40)//

							.givenTwoArguments(0, 11)//
							.givenTwoArguments(4, 2)//
							.performActionResult(
									from -> until -> list -> list.getSublistInRange(from, until).getListResult())//

							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(-8))//
							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(8))//

							.thenShouldHaveSameErrorMessage(errorIndexIsNotInRange.apply(10))//
							.thenShouldHaveSameErrorMessage(errorFromIndexIsNotLessThanUntilIndex)//

					;
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "takeWhile", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void takeWhile(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2))//
							.<Predicate<Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.takeWhile(predicate).getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "dropWhile", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void dropWhile(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2))//
							.<Predicate<Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.dropWhile(predicate).getListResult())//

							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "takeRightWhile", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void takeRightWhile(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2))//
							.<Predicate<Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> //
							list.takeRightWhile(predicate).getListResult())//

							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "dropWhile", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void dropRightWhile(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2))//
							.<Predicate<Integer>>//
							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(predicate -> list -> list.dropWhile(predicate).getListResult())//

							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));//
				}

			}

			@Nested
			public class MonadicTransformation
					implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.MonadicTransformation {

				private static final List<Integer> VALID_LIST_OF_NUMBERS_FROM_1_TO_6 =

						List.extendedFactoryOperations().generateRange(0, 7);

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "sequence", argumentName = "transformer")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void sequence(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, -4, 5, 6, 7, 8))//
							.<Function<Integer, Result<Integer>>>//

							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//

							.givenSingleArgument(b -> Result.empty())//

							.givenSingleArgument(number -> //
							If.givenObject(number)//
									.is(n -> n > 0, "At least one number is negative")//
									.will()//
									.getResult())//

							.performActionResult(transformer -> list -> list.sequence(transformer).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNullValue)

							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_EMPTY_RESULT.trackAndFinalize(methodName))

							.thenShouldHaveSameErrorMessage(ErrorTracker.startTrackingFrom(methodName)
									.finalizeWith("At least one number is negative")

							);

				}

				//
				@Test
				@Override
				public void combineNestedListsWithNullElementTransformer() {

					final var methodName = "combineNestedLists";
					final var argumentName = "elementTransformer";
					final Function<Integer, List<String>> elementTransformer = null;
					Assertions
							.assertThat(VALID_LIST_OF_NUMBERS_FROM_1_TO_6.combineNestedLists(elementTransformer)
									.getListResult().failureValue().getMessage())
							.isEqualTo(NullValueMessages.argument(argumentName).trackAndFinalize(methodName));

				}

				@Test
				@Override
				public void combineNestedListsWithNullElementTransformerProvider() {

					final var methodName = "combineNestedLists";
					final Function<Integer, List<String>> elementTransformer = i -> null;
					Assertions
							.assertThat(VALID_LIST_OF_NUMBERS_FROM_1_TO_6.combineNestedLists(elementTransformer)
									.getListResult().failureValue().getMessage())
							.isEqualTo(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName));

				}

				@Test
				@Override
				public void combineNestedListsWithRuntimeExceptionElementTransformerProvider() {

					final var methodName = "combineNestedLists";
					final Function<Integer, List<String>> elementTransformer = i -> {//
						throw new RuntimeException("bla bla bla");
					};
					Assertions
							.assertThat(VALID_LIST_OF_NUMBERS_FROM_1_TO_6.combineNestedLists(elementTransformer)
									.getListResult().failureValue().getMessage())
							.isEqualTo(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

				}

				@Test
				@Override
				public void combineNestedListsWithLimitExceededElementTransformerProvider() {

					final var methodName = "combineNestedLists";
					final Function<Integer, List<String>> elementTransformer = number -> List
							.extendedFactoryOperations()//
							.generateRange(0, Errors.Constants.HALF_OF_MAX_LIST_SIZE)//
							.map(i -> i + "");
					Assertions
							.assertThat(VALID_LIST_OF_NUMBERS_FROM_1_TO_6.combineNestedLists(elementTransformer)
									.getListResult().failureValue().getMessage())
							.isEqualTo(Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.trackAndFinalize(methodName));

				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)

				@ArgumentName(methodName = "reduce", argumentName = "elementTransformer")
				public void invokeReduction(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Function<Integer, Function<Integer, Integer>>>

							givenNullArgument()//
							.givenSingleArgument(a -> b -> null)//
							.givenSingleArgument(a -> b -> {//
								throw new RuntimeException("bla bla bla");
							})//
							.performActionResult(elementTransformer -> list -> list.reduce(elementTransformer))//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//

							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "partition", argumentName = "partitioner")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void partition(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<Predicate<Integer>>//

							givenNullArgument()//
							.givenSingleArgument(b -> null)//
							.givenSingleArgument(b -> {//
								throw new RuntimeException("bla bla bla");
							})//

							.performActionResult(partitioner -> list -> list.partition(partitioner))//
							.thenShouldHaveSameErrorMessage(errorNullValue)

							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));
				}

			}

			@Nested
			public class Zipping implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Zipping {

				@Override
				@ParameterizedTest
				@MethodName(name = "zipWithPosition")
				@ArgumentsSource(MethodNameProvider.class)
				public void zipWithPosition(final String methodName, final BDDSoftAssertions softly) {

					final var errorIndexWillBeTooLarge = Errors.GeneralMessage.ERROR_SIZE_INDEX_EXCEEDED
							.trackAndFinalize(methodName);
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2))//
							.givenSingleArgument(Errors.Constants.MAX_INDEX)//
							.performActionResult(index -> list -> list.zipWithPosition(index).getListResult())//
							.thenShouldHaveSameErrorMessage(errorIndexWillBeTooLarge);
				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)
				@ArgumentName(methodName = "zipAsPossible", argumentName = "joinedList")
				public void zipAsPossible(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<List<Integer>>//

							givenNullArgument().givenSingleArgument(List.failureInstance())//

							.performActionResult(joinedList -> list -> list.zipAsPossible(joinedList).getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName)

											.trackAndFinalize(methodName)

							)//

							.thenShouldHaveSameErrorMessage(//

									Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
											.withArgumentName(argumentName)

											.trackAndFinalize(methodName)

							)//
					;
				}

			}

			@Nested
			public class Reordering implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Reordering {

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "interleave", argumentName = "joinedList")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void interleave(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName(argumentName)//
							.track("zipAsPossible")//
							.withPrependedMethod("combineNestedLists")//
							.prependAndFinalize(methodName);//

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.track("zipAsPossible")//
							.withPrependedMethod("combineNestedLists")//
							.prependAndFinalize(methodName);//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//

							.<List<Integer>>//

							givenNullArgument().givenSingleArgument(List.failureInstance())//

							.performActionResult(joinedList -> list -> list.interleave(joinedList).getListResult())//
							.thenShouldHaveSameErrorMessage(errorNullValue)//

							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "intersperse", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void intersperse(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					Assertions.assertThat(List.list(1, 2, 3, 4, 5, 6, 7, 8)//
							.intersperse(null)//
							.getListResult()//
							.failureValue()//
							.getMessage())//
							.isEqualTo(errorNullValue);
				}

			}

			@Nested
			public class Conversion implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Conversion {

				@Override
				@ParameterizedTest
				@MethodName(name = "toStream")
				@ArgumentsSource(MethodNameProvider.class)
				public void toStream(final String methodName) {

					final var expectedErrorTypeNotComparable = NullValueMessages.singleNullIndex("[1]").track("list")
							.prependAndFinalize(methodName);

					final var actualErrorMessage = List.list(1, null).convert().toStream().getStreamResult()
							.failureValue().getMessage();//

					Assertions.assertThat(actualErrorMessage).isEqualTo(expectedErrorTypeNotComparable);

				}

				// <L extends java.util.List<A>> Result<L> toJavaList(Supplier<L> listSupplier);

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "toJavaList", argumentName = "listSupplier")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void toJavaList(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(1, 2, 3, 4, 5, 6, 7, 8))//
							.<Supplier<? extends java.util.List<Integer>>>//

							givenNullArgument()//
							.givenSingleArgument(() -> null)//
							.givenSingleArgument(() -> {//
								throw new RuntimeException("bla bla bla");
							})//

							.performActionResult(supplier -> list -> list.convert().toJavaList(supplier))//
							.thenShouldHaveSameErrorMessage(errorNullValue)

							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//
							.thenShouldHaveSameErrorMessage(//
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

				}

			}

			@Nested
			public class Appending implements ListTestBehavior.Operations.Invalid.OnValidNonEmptyList.Appending {

				@Override
				@ParameterizedTest
				@MethodName(name = "addArray")
				@ArgumentsSource(MethodNameProvider.class)
				public void addArray(final String methodName, final BDDSoftAssertions softly) {
					final var errorSizeLimitExceeded = GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.track("addList")
							.prependAndFinalize(methodName);//

					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName("joinedList")//
							.track("addList")

							.prependAndFinalize(methodName);//
															//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.extendedFactoryOperations()
											.createFilledList(Errors.Constants.MAX_LIST_SIZE - 2, () -> 1))//
							.<Integer[]>//

							givenSingleArgument(new Integer[] { 1, null })//
							.givenSingleArgument(new Integer[] { 1, 2, 3 })//
							.performActionResult(array -> list -> list.addArray(array).getListResult())//
							//
							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage)
							.thenShouldHaveSameErrorMessage(errorSizeLimitExceeded);
				}

				@Override
				@ParameterizedTest
				@MethodName(name = "addElement")
				@ArgumentsSource(MethodNameProvider.class)
				public void addElement(final String methodName, final BDDSoftAssertions softly) {

					final var errorSizeLimitExceeded = GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.track("addList")
							.prependAndFinalize(methodName);//

					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName("joinedList")//
							.track("addList")

							.prependAndFinalize(methodName);//
															//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.extendedFactoryOperations().createFilledList(Errors.Constants.MAX_LIST_SIZE,
											() -> 1))//
							.<Integer>//

							givenNullArgument().givenSingleArgument(1)//
							.performActionResult(array -> list -> list.addElement(array).getListResult())//
							//
							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage)
							.thenShouldHaveSameErrorMessage(errorSizeLimitExceeded);

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "addList", argumentName = "joinedList")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void addList(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorSizeLimitExceeded = GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED
							.trackAndFinalize(methodName);//

					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName(argumentName)//
							.trackAndFinalize(methodName);//

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<List<Integer>>//

							givenNullArgument().givenSingleArgument(List.failureInstance())
							.givenSingleArgument(List.extendedFactoryOperations()
									.createFilledList(Errors.Constants.MAX_LIST_SIZE, () -> 1))//
							.performActionResult(joinedList -> list -> list.addList(joinedList).getListResult())//
							//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage)
							.thenShouldHaveSameErrorMessage(errorSizeLimitExceeded);
				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "consList", argumentName = "joinedList")
				@ArgumentsSource(ArgumentNameProvider.class)

				public void consList(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					final var errorSizeLimitExceeded = GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED
							.trackAndFinalize(methodName);//

					final var failedProcessErrorMessage = Errors.CastumArgumentMessage//
							.ERROR_PROCESSING_JOINED_LIST//
							.withArgumentName(argumentName)//
							.trackAndFinalize(methodName);//

					final var errorNullValue = NullValueMessages//
							.argument(argumentName)//
							.trackAndFinalize(methodName);//
					//
					TestMulHelper//
							.softAssertions(softly)//
							.<List<Integer>>//
							initializeCase(//
									List.list(0, 1, 2, 3, 4, 5, 6, 7))//
							.<List<Integer>>//

							givenNullArgument()//
							.givenSingleArgument(List.failureInstance())//
							.givenSingleArgument(//
									List.extendedFactoryOperations().createFilledList(Errors.Constants.MAX_LIST_SIZE,
											() -> 1))//
							.performActionResult(joinedList -> list -> list.consList(joinedList).getListResult())//
							//
							.thenShouldHaveSameErrorMessage(errorNullValue)//
							.thenShouldHaveSameErrorMessage(failedProcessErrorMessage)
							.thenShouldHaveSameErrorMessage(errorSizeLimitExceeded);
				}

				@Override
				@ParameterizedTest
				@ArgumentsSource(ArgumentNameProvider.class)

				@ArgumentName(methodName = "cons", argumentName = "element")
				public void addElementToTheBeginningOfTheList(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Integer>givenNullArgument()//
							.performActionResult(element -> list -> list.cons(element).getListResult())//
							.thenShouldHaveSameErrorMessage(//
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName));//
				}

			}

		}

	}

	@Nested
	public class ShouldSuccessfully {

		@Nested
		public class OnExtendedFactory {
			public static TestMulHelper<ExtendedFactoryOperations> createTestListOperation(
					final BDDSoftAssertions softly) {
				return TestMulHelper.softAssertions(softly).<ExtendedFactoryOperations>//
						initializeCase(List.extendedFactoryOperations());

			}

			@Nested
			public class CollectionFactory implements //
					ListTestBehavior.ExtendedFactoryOperationsHandler.CollectionFactory {

				@Override
				@Test
				public void createFromCollection(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//

							.<Collection<String>>//
							givenSingleArgument(java.util.List.of("A", "B", "C", "D"))//
							.givenSingleArgument(java.util.List.of())//

							.performActionResult(
									collection -> List -> List.createFromCollection(collection).getListResult())//
							.thenShouldBeEqualTo(List.list("A", "B", "C", "D"))

							.thenShouldBeEqualTo(List.emptyList())//

					;//

				}

				@Override
				@Test
				public void createFromStreamAndRemoveNullsValues(final BDDSoftAssertions softly) {

					final Stream<String> stream1 = Stream.of("A", "B", "C", "D");//
					final Stream<String> stream2 = Stream.of("A", null, "C", "D");//
					final Stream<String> stream3 = Stream.empty();//

					TestMulHelper//
							.softAssertions(softly)//
							.<List<String>>//
							initializeCases(//
									List.extendedFactoryOperations().createFromStreamAndRemoveNullsValues(stream1), //
									List.extendedFactoryOperations().createFromStreamAndRemoveNullsValues(stream2), //
									List.extendedFactoryOperations().createFromStreamAndRemoveNullsValues(stream3))//
							.withoutAnyArgument()//
							.performActionResult(List::getListResult)//
							.thenShouldBeEqualTo(List.list("A", "B", "C", "D"))//
							.thenShouldBeEqualTo(List.list("A", "C", "D"))//
							.thenShouldBeEqualTo(List.<String>emptyList());

				}

				@Override
				@Test
				public void createFilledList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<Integer, Supplier<String>>//
							givenTwoArguments(2, () -> "hello")//
							.performActionResult(numberOfElements -> elementSupplier -> List -> List
									.createFilledList(numberOfElements, elementSupplier).getListResult())//
							.thenShouldBeEqualTo(List.list("hello", "hello"));

				}

			}

			@Nested
			public class MathFactory implements //
					ListTestBehavior.ExtendedFactoryOperationsHandler.MathFactory {
				@Override
				@Test
				public void generateRange(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<Integer, Integer>//
							givenTwoArguments(1, 10)//
							.givenTwoArguments(10, 20)//
							.performActionResult(

									startInclusive -> endExclusive -> List -> List
											.generateRange(startInclusive, endExclusive).getListResult())//
							.thenShouldBeEqualTo(List.list(1, 2, 3, 4, 5, 6, 7, 8, 9))//
							.thenShouldBeEqualTo(List.list(10, 11, 12, 13, 14, 15, 16, 17, 18, 19));//
				}
			}

			@Nested
			public class PrimitiveListFactory implements //
					ListTestBehavior.ExtendedFactoryOperationsHandler.PrimitiveListFactory {
				@Override

				@Test

				public void createBooleanList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<boolean[]>//
							givenSingleArgument(new boolean[] { true })//
							.givenSingleArgument(new boolean[] { true, false })//
							.givenSingleArgument(new boolean[] { true, false, false })//
							.performActionResult(booleanValues -> List ->

							List.createBooleanList(booleanValues).getListResult()).thenShouldBeEqualTo(List.list(true))//
							.thenShouldBeEqualTo(List.list(true, false))//
							.thenShouldBeEqualTo(List.list(true, false, false))//

					;//
				}

				@Override

				@Test
				public void createCharList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<char[]>//
							givenSingleArgument(new char[] { '1' })//
							.givenSingleArgument(new char[] { '1', '2' })//
							.givenSingleArgument(new char[] { '1', '3', '2' })//
							.performActionResult(charValues -> List -> List.createCharList(charValues).getListResult())
							.thenShouldBeEqualTo(List.list('1'))//
							.thenShouldBeEqualTo(List.list('1', '2'))//
							.thenShouldBeEqualTo(List.list('1', '3', '2'))//

					;//
				}

				@Override
				@Test
				public void createByteList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<byte[]>//
							givenSingleArgument(new byte[] { 1 })//
							.givenSingleArgument(new byte[] { 1, 2 })//
							.givenSingleArgument(new byte[] { 1, 3, 2 })//
							.performActionResult(byteValues -> List -> List.createByteList(byteValues).getListResult())
							.thenShouldBeEqualTo(List.list((byte) 1))//
							.thenShouldBeEqualTo(List.list((byte) 1, (byte) 2))//
							.thenShouldBeEqualTo(List.list((byte) 1, (byte) 3, (byte) 2))//

					;//
				}

				@Override

				@Test
				public void createShortList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<short[]>//
							givenSingleArgument(new short[] { 1 })//
							.givenSingleArgument(new short[] { 1, 2 })//
							.givenSingleArgument(new short[] { 1, 3, 2 })//
							.performActionResult(
									shortValues -> List -> List.createShortList(shortValues).getListResult())
							.thenShouldBeEqualTo(List.list((short) 1))//
							.thenShouldBeEqualTo(List.list((short) 1, (short) 2))//
							.thenShouldBeEqualTo(List.list((short) 1, (short) 3, (short) 2));//
				}

				@Override

				@Test
				public void createIntList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<int[]>//
							givenSingleArgument(new int[] { 1 })//
							.givenSingleArgument(new int[] { 1, 2 })//
							.givenSingleArgument(new int[] { 1, 3, 2 })//
							.performActionResult(intValues -> List -> List.createIntList(intValues).getListResult())
							.thenShouldBeEqualTo(List.list(1))//
							.thenShouldBeEqualTo(List.list(1, 2))//
							.thenShouldBeEqualTo(List.list(1, 3, 2));//
				}

				@Override

				@Test
				public void createLongList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<long[]>//
							givenSingleArgument(new long[] { 1L })//
							.givenSingleArgument(new long[] { 1L, 2L })//
							.givenSingleArgument(new long[] { 1L, 3L, 2L })//
							.performActionResult(longValues -> List -> List.createLongList(longValues).getListResult())
							.thenShouldBeEqualTo(List.list(1L))//
							.thenShouldBeEqualTo(List.list(1L, 2L))//
							.thenShouldBeEqualTo(List.list(1L, 3L, 2L));//
				}

				@Override

				@Test
				public void createFloatList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<float[]>//
							givenSingleArgument(new float[] { 1F })//
							.givenSingleArgument(new float[] { 1, 2 })//
							.givenSingleArgument(new float[] { 1, 3, 2 })//
							.performActionResult(
									floatValues -> List -> List.createFloatList(floatValues).getListResult())
							.thenShouldBeEqualTo(List.list(1F))//
							.thenShouldBeEqualTo(List.list(1F, 2F))//
							.thenShouldBeEqualTo(List.list(1F, 3F, 2F));//
				}

				@Override
				@Test
				public void createDoubleList(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<double[]>//
							givenSingleArgument(new double[] { 1.0 })//
							.givenSingleArgument(new double[] { 1.0, 2.0 })//
							.givenSingleArgument(new double[] { 1.0, 3.0, 2.0 })//
							.performActionResult(
									doubleValues -> List -> List.createDoubleList(doubleValues).getListResult())
							.thenShouldBeEqualTo(List.list(1.0))//
							.thenShouldBeEqualTo(List.list(1.0, 2.0))//
							.thenShouldBeEqualTo(List.list(1.0, 3.0, 2.0));//
				}
			}

			@Nested
			public class RecursiveFactory implements //
					ListTestBehavior.ExtendedFactoryOperationsHandler.RecursiveFactory {

				@Override
				@Test
				public void iterateWithSeed(final BDDSoftAssertions softly) {

					createTestListOperation(softly)//
							.<Integer, Function<Integer, Integer>, Integer>//
							givenThreeArguments(1, number -> number + 1, 10)//
							.givenThreeArguments(100, number -> number - 5, 10)//
							.performActionResult(

									seed -> transformationFunction -> iterations -> List -> List
											.iterateWithSeed(seed, transformationFunction, iterations).getListResult())//
							.thenShouldBeEqualTo(List.list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
							.thenShouldBeEqualTo(List.list(100, 95, 90, 85, 80, 75, 70, 65, 60, 55, 50));//

				}

				@Override
				@Test
				public void unfoldFromSeed(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//

							.<Integer, Function<If<Integer>, Condition<Integer>>, Function<Integer, Tuple2<Integer, Integer>>>

							givenThreeArguments(1, //
									validator -> validator.is(n -> n < 10), //
									number -> Tuple2.of(number, number + 1).getResult().successValue())//
							.performActionResult(initialSeed -> generatorFunction -> successOutcome -> List ->

							List.unfoldFromSeed(initialSeed, generatorFunction, successOutcome).getListResult())

							.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(1, 10));
				}
			}

			@Nested
			public class TupleFactory implements //
					ListTestBehavior.ExtendedFactoryOperationsHandler.TupleFactory {
				@Override
				@Test
				public void unzipListOfTuples(final BDDSoftAssertions softly) {
					createTestListOperation(softly)//
							.<List<Tuple2<Integer, String>>>//
							givenSingleArgument(List.list(Tuple2.of(1, "one").getResult().successValue()))//
							.performActionResult(nestedList -> List -> List.unzipListOfTuples(nestedList))//
							.thenShouldBeEqualTo(Tuple2.of(List.list(1), List.list("one")).getResult().successValue());
				}
			}

		}

		@Nested
		class Basic {

			@Nested
			class DuringCreation implements ListTestBehavior.Operations.Basic.DuringCreation {
				@Override
				@Test
				public void shouldSuccessfullyCreateAnEmptyList() {
					assertThat(List.emptyList())//
							.as("Empty list should successfully be created")//
							.matches(List::isProcessSuccess);
				}

				@Override
				@Test
				public void shouldSuccessfullyCreateNonEmptyList() {
					assertThat(List.list(1))//
							.as("Creation non empty list should be a success")//
							.matches(List::isProcessSuccess);

				}

			}

			@Nested
			class EmptyList implements ListTestBehavior.Operations.Basic.EmptyListBehavior {
				private final List<?> EMPTY_LIST = List.emptyList();

				@Override
				@Test
				public void shouldRemainEmpty(final BDDSoftAssertions softly) {
					final var actualIsEmpty = EMPTY_LIST.isEmpty().successValue();
					final var actualIsNotEmpty = EMPTY_LIST.isNotEmpty().successValue();

					softly.then(actualIsEmpty).isTrue();
					softly.then(actualIsNotEmpty).isFalse();
				}

				@Override
				@Test
				public void shouldContainNoElements() {
					final var actualSize = EMPTY_LIST.size().successValue();

					assertThat(actualSize).isEqualTo(0);
				}

				@Override
				@Test
				public void shouldReportFailureWhenAccessingTheFirstElement() {
					final var actualFirstElementError = EMPTY_LIST.firstElementOption().failureValue().getMessage();
					final var expectedError =

							ERROR_FIRST_ELEMENT_IN_EMPTY_LIST.trackAndFinalize("firstElementOption");

					assertThat(actualFirstElementError).isEqualTo(expectedError);
				}

				@Override
				@Test
				public void shouldDisplayTheCorrectRepresentationForEmptiness() {
					final var actualRepresentList = EMPTY_LIST.representList().successValue();

					assertThat(actualRepresentList).isEqualTo("[NIL]");
				}

			}

			@Nested
			class NonEmptyList implements ListTestBehavior.Operations.Basic.NonEmptyListBehavior {
				private final List<String> LIST_OF_1_ELEMENTS = List.list("a");
				private final String FIRST_ELEMENT = "a";

				@Override
				@Test
				public void shouldNotBeEmpty(final BDDSoftAssertions softly) {
					final var actualIsEmpty = LIST_OF_1_ELEMENTS.isEmpty().successValue();
					final var actualIsNotEmpty = LIST_OF_1_ELEMENTS.isNotEmpty().successValue();

					softly.then(actualIsEmpty)

							.isFalse();
					softly.then(actualIsNotEmpty).isTrue();
				}

				@Override
				@Test
				public void shouldHaveCorrectSize() {
					final var actualSize = LIST_OF_1_ELEMENTS.size().successValue();

					assertThat(actualSize).isEqualTo(1);
				}

				@Override
				@Test
				public void shouldContainTheCorrectFirstElement() {
					final var actualFirstElement = LIST_OF_1_ELEMENTS.firstElementOption().successValue();

					assertThat(actualFirstElement).isEqualTo(FIRST_ELEMENT);

				}

				@Override
				@Test
				public void shouldDisplayTheCorrectRepresentation() {
					final var actualRepresentList = LIST_OF_1_ELEMENTS.representList().successValue();

					assertThat(actualRepresentList).isEqualTo("[a,NIL]");
				}

			}

		}

		@Nested
		class Mutation implements ListTestBehavior.Operations.Mutation {

			@Override
			@Test
			public void setFirstElement(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(1, 2, 3, 4))//
						.givenSingleArgument(99)//
						.givenSingleArgument(-99)//
						.performActionResult(firstElement -> list -> //
						list.setFirstElement(firstElement).getListResult())//
						.thenShouldBeEqualTo(List.list(99, 2, 3, 4))//
						.thenShouldBeEqualTo(List.list(-99, 2, 3, 4));
			}

			@Override
			@Test
			public void restElements(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.list(1), List.list(1, 2, 3, 4))//
						.withoutAnyArgument()//
						.performActionResult(list -> list.restElements().getListResult()//

						)//
						.thenShouldBeEqualTo(List.<Integer>emptyList()).thenShouldBeEqualTo(List.list(2, 3, 4));
			}

			@Override
			@Test
			public void setElementAtIndex(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCase(List.list("Odel", "Dani", "Yosi", "Gil"))//
						.<Integer, String>//
						givenTwoArguments(0, "Ofek")//
						.givenTwoArguments(1, "Ofek")//
						.givenTwoArguments(2, "Ofek")//
						.givenTwoArguments(3, "Ofek")//
						.performActionResult(
								index -> patch -> list -> list.setElementAtIndex(index, patch).getListResult())//
						.thenShouldBeEqualTo(List.list("Ofek", "Dani", "Yosi", "Gil"))//
						.thenShouldBeEqualTo(List.list("Odel", "Ofek", "Yosi", "Gil"))//
						.thenShouldBeEqualTo(List.list("Odel", "Dani", "Ofek", "Gil"))
						.thenShouldBeEqualTo(List.list("Odel", "Dani", "Yosi", "Ofek"));//
			}

			@Override
			@Test
			public void removeByIndex(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(1, 2, 3, 4, 5, 6, 7))//
						.givenSingleArgument(2)//
						.performActionResult(index -> list -> list.removeByIndex(index).getListResult())//
						.thenShouldBeEqualTo(List.list(1, 2, 4, 5, 6, 7));//
			}

			@Override
			@Test
			public void trimLast(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.<Integer>emptyList(), //
								List.list(1), //
								List.list(1, 2), //
								List.list(1, 2, 3), //
								List.list(1, 2, 3, 4), //
								List.list(1, 2, 3, 4, 5), //
								List.list(1, 2, 3, 4, 5, 6))//
						.withoutAnyArgument()//
						.performActionResult(list -> Result.success(list.trimLast()))//
						.thenShouldBeEqualTo(List.<Integer>emptyList())//
						.thenShouldBeEqualTo(List.<Integer>emptyList())//
						.thenShouldBeEqualTo(List.list(1))//
						.thenShouldBeEqualTo(List.list(1, 2))//
						.thenShouldBeEqualTo(List.list(1, 2, 3))//
						.thenShouldBeEqualTo(List.list(1, 2, 3, 4))//
						.thenShouldBeEqualTo(List.list(1, 2, 3, 4, 5));//
			}

			@Override
			@Test
			public void updatedAllBetween(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>initializeCase(List.list(0, 1, 2, 3, 4, 5, 6, 7, 8))//
						.<Integer, Integer, Integer>//
						givenThreeArguments(2, 5, 99)
						.performActionResult(fromIndex -> untilIndex -> element -> list -> list
								.updatedAllBetween(fromIndex, untilIndex, element).getListResult())//
						.thenShouldBeEqualTo(List.list(0, 1, 99, 99, 99, 5, 6, 7, 8));
			}

			@Override
			@Test
			public void updatedAllFrom(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(0, 1, 2, 3, 4, 5, 6, 7, 8))//
						.<Integer, Integer>//

						givenTwoArguments(2, 99)
						.performActionResult(
								fromIndex -> element -> list -> list.updatedAllFrom(fromIndex, element).getListResult())//
						.thenShouldBeEqualTo(List.list(0, 1, 99, 99, 99, 99, 99, 99, 99));

			}

			@Override
			@Test
			public void updatedAllUntil(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(0, 1, 2, 3, 4, 5, 6, 7, 8))//
						.<Integer, Integer>//

						givenTwoArguments(2, 99)
						.performActionResult(untilIndex -> element -> list -> list.updatedAllUntil(untilIndex, element)
								.getListResult())//
						.thenShouldBeEqualTo(List.list(99, 99, 2, 3, 4, 5, 6, 7, 8));
			}

		}

		@Nested
		class ElementQuerying implements ListTestBehavior.Operations.ElementQuerying {

			@Override
			@Test
			public void first(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCase(List.list("Elon", "David"))//
						.<Predicate<String>>//
						givenSingleArgument(name -> name.contains("E"))//
						.givenSingleArgument(name -> name.contains("D"))//

						.performActionResult(predicate -> list -> //
						list.first(predicate))//
						.thenShouldBeEqualTo("Elon")//
						.thenShouldBeEqualTo("David");
			}

			@Override
			@Test
			public void indexOf(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCase(List.list("Elon", "David"))//
						.<String>//
						givenSingleArgument("Elon")//
						.givenSingleArgument("David")//
						.performActionResult(element -> list -> list.indexOf(element))//
						.thenShouldBeEqualTo(0)//
						.thenShouldBeEqualTo(1);
			}

			@Override
			@Test
			public void lastOption(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.<Integer>emptyList(), //
								List.list(1), //
								List.list(1, 2), //
								List.list(1, 2, 3), //
								List.list(1, 2, 3, 4), //
								List.list(1, 2, 3, 4, 5), //
								List.list(1, 2, 3, 4, 5, 6))//
						.withoutAnyArgument()//
						.performActionResult(list -> Result.success(list.lastOption()))//
						.thenShouldBeEqualTo(Result.<Integer>empty())//
						.thenShouldBeEqualTo(Result.success(1))//
						.thenShouldBeEqualTo(Result.success(2))//
						.thenShouldBeEqualTo(Result.success(3))//
						.thenShouldBeEqualTo(Result.success(4))//
						.thenShouldBeEqualTo(Result.success(5))//
						.thenShouldBeEqualTo(Result.success(6));//

			}

			@Override
			@Test
			public void indexWhere(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCase(List.list("Elon", "David"))//
						.<Predicate<String>>//
						givenSingleArgument(name -> name.contains("E"))//
						.givenSingleArgument(name -> name.contains("D"))//

						.performActionResult(predicate -> list -> //
						list.indexWhere(predicate))//
						.thenShouldBeEqualTo(0)//
						.thenShouldBeEqualTo(1);
			}

			@Override
			@Test
			public void hasDuplicatedElements(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.<Integer>emptyList(), //
								List.list(1, 2), //
								List.list(1, 2, 2))//
						.withoutAnyArgument()//
						.performActionResult(List::hasDuplicatedElements)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);//
			}

			@Override
			@Test
			public void isPalindrome(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.<Integer>emptyList(), //
								List.list(1, 2), //
								List.list(2, 2), //
								List.list(1, 2, 2), //
								List.list(1, 2, 1))//
						.withoutAnyArgument()//
						.performActionResult(List::isPalindrome)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);//
			}

			@Override
			@Test
			public void allEqual(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.<Integer>emptyList(), //
								List.list(1), // can be any one element
								List.list(1, 2), //
								List.list(2, 2))//
						.withoutAnyArgument()//
						.performActionResult(List::allEqual)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);//

			}

			@Override
			@Test
			public void contains(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCases(List.emptyList(), List.list("Elon", "David"))//
						.<String>givenSingleArgument("Elon")//
						.givenSingleArgument("David")//
						.givenSingleArgument("Ofek")//
						.performActionResult(element -> list -> //
						list.contains(element))//
						.thenShouldBeEqualTo(false)///
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);

			}

			@Disabled
			@Test
			@Override
			public void isEqualsWithoutConsiderationOrder(final BDDSoftAssertions softly) {
				final var listCase = List.list("Elon", "Dani", "Yohav");
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCase(listCase)//
						.<List<String>>//
						givenSingleArgument(listCase)//
						.givenSingleArgument(List.list("Elon", "Yohav", "Dani"))//
						.givenSingleArgument(List.list("Dani", "Yohav", "Elon"))//
						.givenSingleArgument(List.list("Dani", "Elon", "Yohav"))//
						.givenSingleArgument(List.list("Yohav", "Elon", "Dani"))//
						.givenSingleArgument(List.list("Yohav", "Dani", "Elon"))//
						.givenSingleArgument(List.list("Yohav", "Dani", "Elon"))//
						.givenSingleArgument(List.<String>emptyList())// empty list
						.givenSingleArgument(List.list("Yohav", "Dani", "Dani"))// different length
						.givenSingleArgument(List.list("Elon", "Elon", "Dani")) // different length
						.givenSingleArgument(List.list("Yohav", "Elon", "Dani", "Dani"))// different length
						.givenSingleArgument(List.list("Yohav", "Elon", "Dani", "Dani"))// different length
						.performActionResult(otherList -> list -> list.isEqualsWithoutConsiderationOrder(otherList))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)

				;

			}

			@Override
			@Test
			public void allEqualTo(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCases(List.emptyList(), List.list("Elon", "Elon", "Elon"))//
						.givenSingleArgument("Elon")//
						.givenSingleArgument("David")//
						.performActionResult(element -> list -> //
						list.allEqualTo(element))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);
			}

			@Override
			@Test
			public void isEqualTo(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.givenSingleArgument(List.emptyList())//
						.givenSingleArgument(List.list(1, 2, 3, 4))//
						.performActionResult(other -> list -> list.isEqualTo(other))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);//
			}

			@Override
			@Test
			public void endsWith(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(0, 1, 2, 3, 4, 5, 6, 7))//
						.<List<Integer>>//
						givenSingleArgument(List.list(0, 1, 2, 3))//
						.givenSingleArgument(List.list(2, 3, 4, 5, 6, 7))//
						.givenSingleArgument(List.emptyList())//
						.performActionResult(joinedList -> list -> list.endsWith(joinedList))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true);

			}

			@Override
			@Test
			public void mkStr(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(2, 3))//
						.<String>//
						givenSingleArgument("")//
						.givenSingleArgument("-")//

						.performActionResult(step -> list -> list.mkStr(step))//
						.thenShouldBeEqualTo("")//
						.thenShouldBeEqualTo("")//
						.thenShouldBeEqualTo("23")//
						.thenShouldBeEqualTo("2-3");
			}

		}

		@Nested
		class Filtering implements ListTestBehavior.Operations.Filtering {

			@Override
			@Test
			public void filter(final BDDSoftAssertions softly) {

				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.<Integer>emptyList(), List.list(1, 2, 3, 4))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number < 3)//
						.performActionResult(other -> list -> list.filter(other).getListResult())//
						.thenShouldBeEqualTo(List.<Integer>emptyList())//
						.thenShouldBeEqualTo(List.list(1, 2));
			}

			@Override
			@Test
			public void exclude(final BDDSoftAssertions softly) {

				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.<Integer>emptyList(), List.list(1, 2, 3, 4))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number < 3)//
						.performActionResult(other -> list -> list.exclude(other).getListResult())//
						.thenShouldBeEqualTo(List.<Integer>emptyList())//
						.thenShouldBeEqualTo(List.list(3, 4));
			}

		}

		@Nested
		class Aggregation implements ListTestBehavior.Operations.Aggregation {

			@Override
			@Test
			public void foldLeft(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.<String, Function<String, Function<Integer, String>>>//
						givenTwoArguments("0", a -> b -> "(" + a + "+" + b + ")")//
						.performActionResult(identity -> accumulator -> list -> list.foldLeft(identity, accumulator))//
						.thenShouldBeEqualTo("0")//
						.thenShouldBeEqualTo("((((0+1)+2)+3)+4)");

			}

			@Override
			@Test
			public void foldRight(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.<String, Function<Integer, Function<String, String>>>//
						givenTwoArguments("0", a -> b -> "(" + a + "+" + b + ")")//
						.performActionResult(identity -> accumulator -> list -> list.foldRight(identity, accumulator))//
						.thenShouldBeEqualTo("0")//
						.thenShouldBeEqualTo("(1+(2+(3+(4+0))))");
			}

			@Override
			@Test
			public void scanLeft(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5))//
						.<Integer, Function<Integer, Function<Integer, Integer>>>

						givenTwoArguments(0, a -> b -> a + b)//

						.performActionResult(
								identity -> accumulator -> list -> list.scanLeft(identity, accumulator).getListResult())//
						.thenShouldBeEqualTo(List.list(0))

						.thenShouldBeEqualTo(List.list(0,1, 3, 6, 10, 15));
			}

			@Override
			@Test
			public void scanRight(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5))//
						.<Integer, Function<Integer, Function<Integer, Integer>>>

						givenTwoArguments(0, a -> b -> a + b)//

						.performActionResult(identity -> accumulator -> list -> list.scanRight(identity, accumulator)
								.getListResult())//
						.thenShouldBeEqualTo(List.list(0))

						.thenShouldBeEqualTo(List.list(15, 14,12,9,5,0));

			}

		}

		@Nested
		class Transformation implements ListTestBehavior.Operations.Transformation {

			@Override
			@Test
			public void map(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.<Function<Integer, Integer>>//
						givenSingleArgument(a -> a + 1)//
						.performActionResult(elementTransformer -> list -> list.map(elementTransformer).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(2, 3, 4, 5));
			}



			@Override
			@Test
			public void unzip(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.emptyList(), //
								List.list(1, 2, 3)//
						)//
						.<Function<Integer, Tuple2<Integer, Integer>>>//

						givenSingleArgument(n -> Tuple2.of(n, n + 1).getResult().successValue())//

						.performActionResult(unzipper -> list -> list.unzip(unzipper))//
						.thenShouldBeEqualTo(Tuple2.of(List.<Integer>emptyList(), List.<Integer>emptyList()).getResult()
								.successValue())//
						.thenShouldBeEqualTo(
								Tuple2.of(List.list(1, 2, 3), List.list(2, 3, 4)).getResult().successValue());
			}

		}

		@Nested
		class MonadicTransformation implements ListTestBehavior.Operations.MonadicTransformation {

			@Override
			@Test
			public void sequence(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3))//
						.<Function<Integer, Result<Integer>>>//

						givenSingleArgument(number -> Result.success(number + 1))//
						.performActionResult(transformer -> list -> list.sequence(transformer).getListResult())//
						.thenShouldBeEqualTo(List.<Integer>emptyList()).thenShouldBeEqualTo(List.list(2, 3, 4));

			}

			@Override
			@Test
			public void combineNestedLists(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(2, 3))//
						.<Function<Integer, List<String>>>//
						givenSingleArgument(a -> List.list("1", a.toString(), "1"))//
						.performActionResult(elementTransformer -> list -> list.combineNestedLists(elementTransformer)
								.getListResult())//
						.thenShouldBeEqualTo(List.<String>emptyList())//
						.thenShouldBeEqualTo(List.list("1", "2", "1", "1", "3", "1"));
			}

			@Override
			@Test
			public void reduce(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(1, 2, 3, 4))//
						.<Function<Integer, Function<Integer, Integer>>>//
						givenSingleArgument(a -> b -> a + b)//
						.performActionResult(elementTransformer -> list -> list.reduce(elementTransformer))//
						.thenShouldBeEqualTo(10);
			}

			@Override
			@Test
			public void partition(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))//
						.<Predicate<Integer>>//

						givenSingleArgument(n -> n % 3 == 0)//

						.performActionResult(unzipper -> list -> list.partition(unzipper))//
						.thenShouldBeEqualTo(Tuple2.of(List.<Integer>emptyList(), List.<Integer>emptyList()).getResult()
								.successValue())//
						.thenShouldBeEqualTo(Tuple2.of(List.list(3, 6, 9), List.list(1, 2, 4, 5, 7, 8, 10)).getResult()
								.successValue());
			}

		}

		@Nested
		class Grouping implements ListTestBehavior.Operations.Grouping {

			@Override
			@Test
			public void groupBy(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCases(List.emptyList(), List.list("1", "10", "100"))//
						.<Function<String, Integer>>//
						givenSingleArgument(String::length)//

						.performActionResult(keyMapper -> list -> list.groupBy(keyMapper).asResult())//
						.thenShouldBeEqualTo(Map.emptyMap())//
						.thenShouldBeEqualTo(//
								Map.<Integer, List<String>>emptyMap()//
										.add(1, List.list("1"))//
										.add(3, List.list("100")).add(2, List.list("10")//
										)//

						)//
				;

			}

			@Override
			@Test
			public void groupByZippingValuesAsPossible(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(0, 1, 2))//
						.<List<Integer>>//
						givenSingleArgument(List.list(3, 4))//
						.performActionResult(
								joinedList -> list -> list.groupByZippingValuesAsPossible(joinedList).asResult())//
						.thenShouldBeEqualTo(Map.emptyMap().add(0, 3).add(1, 4));
			}

		}

		@Nested
		class Appending implements ListTestBehavior.Operations.Appending {

			@Override
			@Test
			public void addArray(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCases(List.emptyList(), List.list("Elon", "David"))//
						.<String[]>//
						givenSingleArgument(new String[] { "Galit", "Adir" })//

						.performActionResult(array -> list -> //
						list.addArray(array).getListResult())//
						.thenShouldBeEqualTo(List.list("Galit", "Adir"))//
						.thenShouldBeEqualTo(List.list("Elon", "David", "Galit", "Adir"));//
			}

			@Override
			@Test
			public void addElement(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCases(List.emptyList(), List.list("Elon", "David"))//
						.<String>//
						givenSingleArgument("Galit")//

						.performActionResult(element -> list -> //
						list.addElement(element).getListResult())//
						.thenShouldBeEqualTo(List.list("Galit"))//
						.thenShouldBeEqualTo(List.list("Elon", "David", "Galit"));//
			}

			@Override
			@Test
			public void addList(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCases(List.emptyList(), List.list("Elon", "David"))//
						.<List<String>>//
						givenSingleArgument(List.list("Galit"))//

						.performActionResult(joinedList -> list -> //
						list.addList(joinedList).getListResult())//
						.thenShouldBeEqualTo(List.list("Galit"))//
						.thenShouldBeEqualTo(List.list("Elon", "David", "Galit"));//
			}

			@Override
			@Test
			public void cons(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.givenSingleArgument(99)
						.performActionResult(firstElement -> list -> list.cons(firstElement).getListResult())//
						.thenShouldBeEqualTo(List.list(99))//
						.thenShouldBeEqualTo(List.list(99, 1, 2, 3, 4));
			}

			@Override
			@Test
			public void consList(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(0, 1, 2))//
						.<List<Integer>>//

						givenSingleArgument(List.list(3, 4, 5, 6, 7))//
						.performActionResult(joinedList -> list -> list.consList(joinedList).getListResult())//
						//
						.thenShouldBeEqualTo(List.list(3, 4, 5, 6, 7, 0, 1, 2));

			}

		}

		@Nested
		class Structural implements ListTestBehavior.Operations.Structural {

			@Override
			@Test
			public void duplicate(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.withoutAnyArgument()//
						.performActionResult(List::duplicate)//
						.thenShouldBeEqualTo(Tuple2.of(List.<Integer>emptyList(), List.<Integer>emptyList()).getResult()
								.successValue())//
						.thenShouldBeEqualTo(
								Tuple2.of(List.list(1, 2, 3, 4), List.list(1, 2, 3, 4)).getResult().successValue());
			}

			@Override
			@Test
			public void last(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.list(1), //
								List.list(1, 2), //
								List.list(1, 2, 3), //
								List.list(1, 2, 3, 4), //
								List.list(1, 2, 3, 4, 5), //
								List.list(1, 2, 3, 4, 5, 6))//
						.withoutAnyArgument().performActionResult(list -> list.last().getListResult())//

						.thenShouldBeEqualTo(List.list(1))//
						.thenShouldBeEqualTo(List.list(2))//
						.thenShouldBeEqualTo(List.list(3))//
						.thenShouldBeEqualTo(List.list(4))//
						.thenShouldBeEqualTo(List.list(5))//
						.thenShouldBeEqualTo(List.list(6));//

			}

		}

		@Nested
		class Slicing implements ListTestBehavior.Operations.Slicing {
			@Override
			@Test
			public void takeAtMost(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5, 6, 7))//
						.givenSingleArgument(2)//
						.givenSingleArgument(10)//
						.performActionResult(n -> list -> list.takeAtMost(n).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(1, 2))//
						.thenShouldBeEqualTo(List.list(1, 2, 3, 4, 5, 6, 7));

			}

			@Override
			@Test
			public void dropAtMost(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5, 6, 7))//
						.givenSingleArgument(2)//
						.givenSingleArgument(10)//
						.performActionResult(n -> list -> list.dropAtMost(n).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(3, 4, 5, 6, 7))//
						.thenShouldBeEqualTo(List.emptyList());
			}

			@Override
			@Test
			public void takeRightAtMost(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5, 6, 7))//
						.givenSingleArgument(2)//
						.givenSingleArgument(10)//
						.performActionResult(n -> list -> list.takeRightAtMost(n).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(6, 7))//
						.thenShouldBeEqualTo(List.list(1, 2, 3, 4, 5, 6, 7));
			}

			@Override
			@Test
			public void dropRightAtMost(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5, 6, 7))//
						.givenSingleArgument(2)//
						.givenSingleArgument(10)//
						.performActionResult(n -> list -> list.dropRightAtMost(n).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(1, 2, 3, 4, 5))//
						.thenShouldBeEqualTo(List.emptyList());
			}

			@Override
			@Test
			public void getSublistInRange(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(0, 1, 2, 3, 4, 5, 6, 7))//
						.<Integer, Integer>//
						givenTwoArguments(1, 6)//
						.givenTwoArguments(3, 5)//
						.performActionResult(
								from -> until -> list -> list.getSublistInRange(from, until).getListResult())//

						.thenShouldBeEqualTo(List.list(1, 2, 3, 4, 5))//
						.thenShouldBeEqualTo(List.list(3, 4))//

				;
			}

			@Override
			@Test
			public void takeWhile(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(0, 1, 2))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number < 2)//

						.performActionResult(predicate -> list -> list.takeWhile(predicate).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())

						.thenShouldBeEqualTo(List.list(0, 1));

			}

			@Override
			@Test
			public void dropWhile(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(0, 1, 2))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number < 2)//

						.performActionResult(predicate -> list -> list.dropWhile(predicate).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())

						.thenShouldBeEqualTo(List.list(2));
			}

			@Override
			@Test
			public void takeRightWhile(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(0, 1, 2))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number > 1)//

						.performActionResult(predicate -> list -> list.takeRightWhile(predicate).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())

						.thenShouldBeEqualTo(List.list(2));
			}

			@Override
			@Test
			public void dropRightWhile(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(0, 1, 2))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number > 1)//
						.performActionResult(predicate -> list -> list.dropRightWhile(predicate).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(0, 1));
			}
		}

		@Nested
		class Reordering implements ListTestBehavior.Operations.Reordering {


			@Override
			@Test
			public void reverse(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4, 5, 6))//
						.withoutAnyArgument()//

						.performActionResult(list -> list.reverse().getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(6, 5, 4, 3, 2, 1));

			}

			@Override
			@Test
			public void shuffle(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.extendedFactoryOperations().generateRange(1, 11))//
						.withoutAnyArgument()//
						.performActionResult(list -> list.shuffle().convert()

								.toJavaList(java.util.ArrayList::new)
						// .successValue()
						)//

						.thenShouldDoAllTheRequirementsHereFitForList(

								shuffledList -> {
									final var originalListImp = List.emptyList();
									final var originalList = originalListImp.convert()
											.toJavaList(java.util.ArrayList::new).successValue();
									final var anotherShuffledList = originalListImp.shuffle().convert()
											.toJavaList(java.util.ArrayList::new).successValue();
									return shuffledList.containsExactlyInAnyOrderElementsOf(originalList)//
											.isEqualTo(originalList)//
											.isEqualTo(anotherShuffledList); //
								}

						)

						.thenShouldDoAllTheRequirementsHereFitForList(shuffledList -> {
							final List<Integer> originalListImp = List.list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
							final var originalList = originalListImp.convert().toJavaList(java.util.ArrayList::new)
									.successValue();
							final var anotherShuffledList = originalListImp.shuffle().convert()
									.toJavaList(java.util.ArrayList::new).successValue();
							return shuffledList.containsExactlyInAnyOrderElementsOf(originalList) //
									.isNotEqualTo(originalList) //
									.isNotEqualTo(anotherShuffledList); //
						}

						);
			}

			@Override
			@Test
			public void interleave(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3))//

						.<List<Integer>>//

						givenSingleArgument(List.list(9, 8, 7))

						.performActionResult(joinedList -> list -> list.interleave(joinedList).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//

						.thenShouldBeEqualTo(List.list(1, 9, 2, 8, 3, 7));
			}

			@Override
			@Test
			public void intersperse(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3))//

						.<Integer>//

						givenSingleArgument(4)

						.performActionResult(element -> list -> list.intersperse(element).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//

						.thenShouldBeEqualTo(List.list(1, 4, 2, 4, 3));
			}

		}

		@Nested
		class Zipping implements ListTestBehavior.Operations.Zipping {

			@Override
			@Test

			public void zipWithPositionWithArgument(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<String>>//
						initializeCases(List.<String>emptyList(), List.list("0", "1", "2"))//
						.givenSingleArgument(0)//
						.performActionResult(index -> list -> list.zipWithPosition(index).getListResult())//

						.thenShouldBeEqualTo(List.<String>emptyList())

						.thenShouldBeEqualTo(List.list("0", "1", "2").zipAsPossible(List.list(0, 1, 2)));

			}

			@Override
			@Test
			public void zipAsPossible(final BDDSoftAssertions softly) {

				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.givenSingleArgument(List.list(4, 3, 2, 1))
						.performActionResult(joinedList -> list -> list.zipAsPossible(joinedList).getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(//
								Tuple2.of(1, 4).getResult().successValue()//
								, Tuple2.of(2, 3).getResult().successValue() //
								, Tuple2.of(3, 2).getResult().successValue() //
								, Tuple2.of(4, 1).getResult().successValue() //
						)//
						);//
			}

		}

		@Nested
		class Conversion implements ListTestBehavior.Operations.Conversion {

			@Override
			@Test
			public void toSet(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<Set<Integer>>//
						initializeCases(List.<Integer>emptyList().convert().toSet(), //
								List.list(1, 1).convert().toSet() //
						).withoutAnyArgument()//
						.performActionResult(Set::getSetResult)//
						.thenShouldBeEqualTo(Set.emptySet())//

						.thenShouldBeEqualTo(Set.emptySet().insert(1));//
			}

			@Override
			@Test
			public void toStream(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.emptyList(), //
								List.list(1, 2) //
						).withoutAnyArgument()//
						.performActionResult(

								l -> l.convert().toStream()

										.getStreamResult())//
						.thenShouldBeEqualTo(dev.ofekmalka.tools.stream.Stream.<Integer>emptyStream())//
						.thenShouldBeEqualTo(dev.ofekmalka.tools.stream.Stream.<Integer>emptyStream().cons(2).cons(1));
			}

	
			@Override
			@Test
			public void toJavaList(final BDDSoftAssertions softly) {

				final var expectedArrayList = new ArrayList<>();
				expectedArrayList.add(4);
				expectedArrayList.add(77);

				expectedArrayList.add(-14);

				final var expectedVector = new Vector<Integer>();
				expectedVector.add(4);
				expectedVector.add(77);

				expectedVector.add(-14);
				final var expectedLinkedList = new LinkedList<Integer>();
				expectedLinkedList.add(4);
				expectedLinkedList.add(77);

				expectedLinkedList.add(-14);

				final var expectedStack = new Stack<Integer>();
				expectedStack.add(4);
				expectedStack.add(77);

				expectedStack.add(-14);

				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(//
								List.list(4, 77, -14))//
						.<Supplier<? extends java.util.List<Integer>>>//

						givenSingleArgument(java.util.ArrayList::new)//
						.givenSingleArgument(java.util.Vector::new)//
						.givenSingleArgument(java.util.LinkedList::new)//
						.givenSingleArgument(java.util.Stack::new)//

						.performActionResult(supplier -> list -> list.convert().toJavaList(supplier))//
						.thenShouldBeEqualTo(expectedArrayList).thenShouldBeEqualTo(expectedVector)
						.thenShouldBeEqualTo(expectedLinkedList).thenShouldBeEqualTo(expectedStack)

				;
			}

		}

		@Nested
		class SubList implements ListTestBehavior.Operations.SubList {

			@Override
			@Test
			public void startsWith(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>initializeCases(List.emptyList(), List.list(0, 1, 2, 3, 4, 5, 6, 7))//
						.<List<Integer>>//
						givenSingleArgument(List.list(0, 1, 2, 3))//
						.givenSingleArgument(List.list(2, 3, 4, 5, 6, 7))//
						.givenSingleArgument(List.emptyList())//
						.performActionResult(sub -> list -> list.startsWith(sub))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);

			}

			@Override
			@Test
			public void hasSubList(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(1, 2, 3, 4))//
						.<List<Integer>>//
						givenSingleArgument(List.list(1, 2, 3, 4))//
						.givenSingleArgument(List.list(2, 4))//
						.givenSingleArgument(List.list(2, 3))//
						.givenSingleArgument(List.list(2))//
						.givenSingleArgument(List.emptyList())//
						.performActionResult(sub -> list -> list.hasSubList(sub))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true);
			}

		}

		@Nested
		class PredicateMatching implements ListTestBehavior.Operations.PredicateMatching {

			@Override
			@Test
			public void anyMatch(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(2, 3, 4, 5, 6, 7, 8))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number < 2)//
						.givenSingleArgument(number -> number <= 2)//

						.performActionResult(predicate -> list -> list.anyMatch(predicate))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);
			}

			@Override
			@Test
			public void allMatch(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(2, 3, 4, 5, 6, 7, 8))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number > 2)//
						.givenSingleArgument(number -> number >= 2)//

						.performActionResult(predicate -> list -> list.allMatch(predicate))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);
			}

			@Override
			@Test
			public void noneMatch(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(2, 3, 4, 5, 6, 7, 8))//
						.<Predicate<Integer>>//
						givenSingleArgument(number -> number < 2)//
						.givenSingleArgument(number -> number >= 2)//

						.performActionResult(predicate -> list -> list.noneMatch(predicate))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);
			}

		}

		@Nested
		class Padding implements ListTestBehavior.Operations.Padding {

			@Override
			@Test
			public void paddingRightWithEmptyResult(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(0, 1, 2))//
						.givenSingleArgument(5)//
						.givenSingleArgument(3)//

						.performActionResult(
								targetLength -> list -> list.paddingRightWithEmptyResult(targetLength).getListResult())//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().createFilledList(5, () -> Result.empty()))//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().createFilledList(3, () -> Result.empty()))//
						.thenShouldBeEqualTo(List.list(0, 1, 2).map(Result::success).addElement(Result.empty())
								.addElement(Result.empty()))
						.thenShouldBeEqualTo(List.list(0, 1, 2).map(Result::success));

			}

			@Override
			@Test
			public void paddingLeftWithEmptyResult(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(List.emptyList(), List.list(0, 1, 2))//
						.givenSingleArgument(5)//
						.givenSingleArgument(3)//

						.performActionResult(
								targetLength -> list -> list.paddingLeftWithEmptyResult(targetLength).getListResult())//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().createFilledList(5, () -> Result.empty()))//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().createFilledList(3, () -> Result.empty()))//
						
						.thenShouldBeEqualTo(
								List.list(0, 1, 2).map(Result::success).cons(Result.empty()).cons(Result.empty()))
						.thenShouldBeEqualTo(List.list(0, 1, 2).map(Result::success));
			}

		}

		@Nested
		class Splitting implements ListTestBehavior.Operations.Splitting {

			@Override
			@Test
			public void firstElementAndRestElementsOption(final BDDSoftAssertions softly) {
				TestMulHelper.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(1, 2, 3, 4))//
						.withoutAnyArgument()//
						.performActionResult(List::firstElementAndRestElementsOption)//

						.thenShouldBeEqualTo(Tuple2.of(1, List.list(2, 3, 4)).getResult().successValue());
			}

			@Override
			@Test
			public void splitAt(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCase(List.list(0, 1, 2, 3, 4, 5, 6, 7))//
						.<Integer>//
						givenSingleArgument(2)//
						.performActionResult(index -> list -> list.splitAt(index))//
						.thenShouldBeEqualTo(
								Tuple2.of(List.list(0, 1), List.list(2, 3, 4, 5, 6, 7)).getResult().successValue())//
				;
			}

		}

		@Nested
		class Distincting implements ListTestBehavior.Operations.Distincting {

			@Override
			@Test
			public void distinct(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<List<Integer>>//
						initializeCases(//
								List.emptyList(), //
								List.list(1), // can be any one element
								List.list(1, 2), //
								List.list(2, 2), //
								List.list(1, 2, 2, 2, 1, 2, 1))//

						.withoutAnyArgument()//
						.performActionResult(list -> list.distinct().getListResult())//
						.thenShouldBeEqualTo(List.emptyList())//
						.thenShouldBeEqualTo(List.list(1))//
						.thenShouldBeEqualTo(List.list(1, 2))//
						.thenShouldBeEqualTo(List.list(2))//
						.thenShouldBeEqualTo(List.list(1, 2));//

			}
		}

	}

}
