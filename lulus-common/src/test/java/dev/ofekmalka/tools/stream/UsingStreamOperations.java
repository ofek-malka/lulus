package dev.ofekmalka.tools.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.support.TestMulHelper;
import dev.ofekmalka.support.annotation.ArgumentName;
import dev.ofekmalka.support.annotation.CustomDisplayNameGenerator;
import dev.ofekmalka.support.annotation.TwoArgumentNames;
import dev.ofekmalka.support.general.providers.ArgumentNameProvider;
import dev.ofekmalka.support.general.providers.TwoArgumentNamesProvider;
import dev.ofekmalka.tools.helper.ValidatedSize;
import dev.ofekmalka.tools.stream.Stream.UnsafeLazyList;
import dev.ofekmalka.tools.stream.behavior.StreamTestBehavior;
import dev.ofekmalka.tools.tuple.Tuple2;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SoftAssertionsExtension.class)
public class UsingStreamOperations {

	@Nested
	final class Invalid {// GeneralEdgeCaseErrorsHandeler {
		@Nested
		final class DuringCreation

				implements//
				StreamTestBehavior.Operations.Invalid.Creation {

			public static <T> Function<Supplier<T>, Stream<T>> repeat() {
				return Stream::repeat;
			}

			public static <T> Function<T, Function<Function<T, T>, Stream<T>>> iterate() {
				return initialValue -> generator -> Stream.iterate(initialValue, generator);
			}

			public static <S, A> Function<S, Function<Function<S, Tuple2<A, S>>, Stream<A>>> unfold() {
				return initialState -> generator -> Stream.unfold(initialState, generator);
			}

			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "repeat", argumentName = "supplier")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void repeat(final String methodName, final String argumentName, final BDDSoftAssertions softly) {

				final var errorNullSupplier = //
						NullValueMessages.argument(argumentName).trackAndFinalize(methodName);

				TestMulHelper//
						.softAssertions(softly)//
						.<Function<Supplier<Integer>, Stream<Integer>>>//
						initializeCase(//
								repeat())//

						.<Supplier<Integer>>//
						givenNullArgument()//
						.givenSingleArgument(() -> null)//
						.givenSingleArgument(() -> {//
							throw new RuntimeException("Error");
						})//
						.performActionResult(supplier -> stream -> stream.apply(supplier).getStreamResult())//
						.thenShouldHaveSameErrorMessage(errorNullSupplier)//
						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))//

						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

			}

			@Override
			@ParameterizedTest
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			@TwoArgumentNames(methodName = "iterate", //
					firstArgumentName = "initialValue", secondArgumentName = "generator")
			public void iterate(final String methodName, final String firstArgumentName,
					final String secondArgumentName, final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Function<Integer, Function<Function<Integer, Integer>, Stream<Integer>>>>//
						initializeCase(//
								iterate())//

						.<Integer, Function<Integer, Integer>>givenTwoArguments(null, a -> a)//
						.givenTwoArguments(0, null)//
						.givenTwoArguments(0, a -> null)//
						.givenTwoArguments(0, a -> {//
							throw new RuntimeException("Error");
						})//

						.performActionResult(

								initialValue -> generator -> stream -> stream.apply(initialValue).apply(generator)
										.getStreamResult())
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))
						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

			}

			@Override
			@ParameterizedTest
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			@TwoArgumentNames(methodName = "unfold", //
					firstArgumentName = "initialState", secondArgumentName = "generator")
			public void unfold(final String methodName, final String firstArgumentName, final String secondArgumentName,
					final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<Function<Integer, Function<Function<Integer, Tuple2<String, Integer>>, Stream<String>>>>//
						initializeCase(//
								unfold())//

						.<Integer, Function<Integer, Tuple2<String, Integer>>>givenTwoArguments(null,
								a -> Tuple2.of(a.toString(), ++a).getResult().successValue())//
					
						

						.givenTwoArguments(0, null)//
						.givenTwoArguments(0, a -> null)//
						.givenTwoArguments(0, a -> {//
							throw new RuntimeException("Error");
						})//

						.performActionResult(

								initialState -> generator -> stream -> stream.apply(initialState).apply(generator)
										.getStreamResult())
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))
						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName))
						.thenShouldHaveSameErrorMessage(
								CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

			}

		}

		@Nested
		final class DuringTransformation {
			private TestMulHelper<Stream<Integer>> errorTestCase(final BDDSoftAssertions softly) {
				return TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(//
								List.extendedFactoryOperations().generateRange(1, 10)

										.convert()//
										.toStream())//
				;
			}

			@Nested
			final class Filtering //

					implements//
					StreamTestBehavior.Operations.Invalid.Filtering { //
				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "filter", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void filter(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
					errorTestCase(softly)//

							.<Predicate<Integer>>//
							givenNullArgument()//

							.givenSingleArgument(a -> null)//
							.givenSingleArgument(a -> {//
								throw new RuntimeException("Error");
							})

							.performActionResult(

									predicate -> stream -> stream.filter(predicate).getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "dropWhile", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void dropWhile(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					errorTestCase(softly)

							.<Predicate<Integer>>//
							givenNullArgument()//

							.givenSingleArgument(a -> null)//
							.givenSingleArgument(a -> {//
								throw new RuntimeException("Error");
							})

							.performActionResult(

									predicate -> stream -> stream.dropWhile(predicate).getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "takeWhile", argumentName = "predicate")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void takeWhile(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					errorTestCase(softly)//

							.<Predicate<Integer>>//
							givenNullArgument()//

							.givenSingleArgument(a -> null)//
							.givenSingleArgument(a -> {//
								throw new RuntimeException("Error");//
							})//

							.performActionResult(

									predicate -> stream -> stream.takeWhile(predicate).getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

				}

			}

			@Nested
			final class Mapping //

					implements//
					StreamTestBehavior.Operations.Invalid.Mapping { //
				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "map", argumentName = "elementTransformer")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void map(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
					errorTestCase(softly)//
							.<Function<Integer, Object>>givenNullArgument().givenSingleArgument(a -> null)
							.givenSingleArgument(a -> {//
								throw new RuntimeException("Error");
							})

							.performActionResult(

									elementTransformer -> stream -> stream.map(elementTransformer).getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "flatMap", argumentName = "elementTransformer")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void flatMap(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					errorTestCase(softly)//
							.<Function<Integer, Stream<Integer>>>givenNullArgument().givenSingleArgument(a -> null)
							.givenSingleArgument(a -> {//
								throw new RuntimeException("Error");
							})

							.performActionResult(

									elementTransformer -> stream -> stream.flatMap(elementTransformer)
											.getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_NULL_RESULT.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.trackAndFinalize(methodName));
				}

			}

			@Nested
			final class Combining //

					implements//
					StreamTestBehavior.Operations.Invalid.Combining { //
				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "append", argumentName = "joinedStream")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void append(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
					errorTestCase(softly)//
							.<Stream<Integer>>givenNullArgument().givenSingleArgument(Stream.makeFailureInstance())

							.performActionResult(

									joinedStream -> stream -> stream.append(joinedStream).getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									Stream.Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_STREAM
											.withArgumentName(argumentName)

											.trackAndFinalize(methodName));

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "zipAsPossible", argumentName = "joinedStream")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void zipAsPossible(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {
					errorTestCase(softly)//
							.<Stream<Integer>>givenNullArgument().givenSingleArgument(Stream.makeFailureInstance())

							.performActionResult(

									joinedStream -> stream -> stream.zipAsPossible(joinedStream).getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									Stream.Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_STREAM
											.withArgumentName(argumentName)

											.trackAndFinalize(methodName));

				}

			}

			@Nested
			final class OtherTransformations //

					implements//
					StreamTestBehavior.Operations.Invalid.OtherTransformations { //

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "setFirstElement", argumentName = "firstElement")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void setFirstElement(final String methodName, final String argumentName,
						final BDDSoftAssertions softly) {

					TestMulHelper//
							.softAssertions(softly)//
							.<Stream<Integer>>//
							initializeCases(Stream.<Integer>emptyStream(), //
									List.extendedFactoryOperations().generateRange(1, 10).convert().toStream())//
							.<Integer>//
							givenNullArgument()//
							.givenSingleArgument(99)//

							.performActionResult(

									firstElement -> stream -> stream.setFirstElement(firstElement).getStreamResult())
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(Stream.Errors

									.StreamEmptyErrorMessage.ERROR_SET_FIRST_ELEMENT_IN_EMPTY_STREAM
									.trackAndFinalize(methodName))
							.thenShouldHaveSameErrorMessage(
									NullValueMessages.argument(argumentName).trackAndFinalize(methodName))

							.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(2, 10).cons(99)
									.convert().toStream());

				}

				@Override
				@ParameterizedTest
				@ArgumentName(methodName = "cons", argumentName = "element")
				@ArgumentsSource(ArgumentNameProvider.class)
				public void cons(final String methodName, final String argumentName) {
					final var expectedErrorMessageNull = NullValueMessages.argument(argumentName)
							.trackAndFinalize(methodName);

					final var stream = List.extendedFactoryOperations().generateRange(1, 10).convert().toStream();

					final var actualErrorMessage = stream.cons(null).getStreamResult().failureValue().getMessage();

					assertThat(actualErrorMessage).isEqualTo(expectedErrorMessageNull);
				}
			}

		}

	}

	@Nested
	class ShouldSeccessfully {

		@Nested
		final class OtherTransformations //

				implements//
				StreamTestBehavior.Operations.OtherTransformations {
			@Override
			@Test
			public void setFirstElement(final BDDSoftAssertions softly) {

				final var numberOfElements = 3;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(1)

						)//
						.<Integer>//
						givenSingleArgument(3)//
						.givenSingleArgument(2)//

						.performActionResult(//

								firstElement -> stream -> stream.setFirstElement(firstElement)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.list(3, 2, 3))//
						.thenShouldBeEqualTo(List.list(2, 2, 3))//

				//

				;

			}

			@Override
			@Test
			public void cons(final BDDSoftAssertions softly) {
				final var numberOfElements = 4;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(1)

						)//
						.<Integer>//
						givenSingleArgument(3)//
						.givenSingleArgument(2)//

						.performActionResult(//

								element -> stream -> stream.cons(element)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.list(3, 1, 2, 3))//
						.thenShouldBeEqualTo(List.list(2, 1, 2, 3))//

				//

				;

			}

			@Override
			@Test
			public void dropAtMost(final BDDSoftAssertions softly) {
				final var numberOfElements = 4;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(1)

						)//
						.<ValidatedSize>//
						givenSingleArgument(ValidatedSize.of(1).successValue())//
						.givenSingleArgument(ValidatedSize.of(2).successValue())//

						.givenSingleArgument(ValidatedSize.of(3).successValue())//

						.performActionResult(//

								n -> stream -> stream.dropAtMost(n)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.list(2, 3, 4, 5))//
						.thenShouldBeEqualTo(List.list(3, 4, 5, 6))//
						.thenShouldBeEqualTo(List.list(4, 5, 6, 7))//

				//

				;

			}

			@Override
			@Test
			public void takeAtMost(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(1)

						)//
						.<ValidatedSize>//
						givenSingleArgument(ValidatedSize.of(1).successValue())//
						.givenSingleArgument(ValidatedSize.of(2).successValue())//

						.givenSingleArgument(ValidatedSize.of(3).successValue())//

						.performActionResult(//

								n -> stream -> stream.takeAtMost(n)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.list(1))//
						.thenShouldBeEqualTo(List.list(1, 2))//
						.thenShouldBeEqualTo(List.list(1, 2, 3))//

				//

				;

			}

			@Override
			@Test
			public void toBoundedList(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(1)

						)//
						.<ValidatedSize>//
						givenSingleArgument(ValidatedSize.of(1).successValue())//
						.givenSingleArgument(ValidatedSize.of(2).successValue())//

						.givenSingleArgument(ValidatedSize.of(3).successValue())//

						.performActionResult(//

								size -> stream -> stream

										.toBoundedList(size)//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.list(1))//
						.thenShouldBeEqualTo(List.list(1, 2))//
						.thenShouldBeEqualTo(List.list(1, 2, 3))//

				//

				;

			}

		}

		@Nested
		final class Windowing //

				implements//
				StreamTestBehavior.Operations.Windowing {
			@Override
			@Test
			public void windowFixed(final BDDSoftAssertions softly) {

				final var numberOfElements = 3;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCases(//

								Stream.emptyStream(), //
								Stream.from(1), //
								List.extendedFactoryOperations().generateRange(1, 3).convert().toStream(),

								List.extendedFactoryOperations().generateRange(1, 9).convert().toStream()//

						)//
						.<ValidatedSize>//
						givenSingleArgument(ValidatedSize.of(3).successValue())//

						.performActionResult(//

								windowSize -> stream -> stream.windowFixed(windowSize)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(

								List.emptyList())//
						.thenShouldBeEqualTo(

								List.list(List.list(1, 2, 3), List.list(4, 5, 6), List.list(7, 8, 9)))//

						.thenShouldBeEqualTo(

								List.emptyList())//
						.thenShouldBeEqualTo(//
								List.list(List.list(1, 2, 3), List.list(4, 5, 6)))//
				//

				;

			}

			@Override
			@Test
			public void windowSliding(final BDDSoftAssertions softly) {
				final var numberOfElements = 3;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCases(//

								Stream.emptyStream(), //
								Stream.from(1), //
								List.extendedFactoryOperations().generateRange(1, 3).convert().toStream(),

								List.extendedFactoryOperations().generateRange(1, 11).convert().toStream()//

						)//
						.<ValidatedSize>//
						givenSingleArgument(ValidatedSize.of(3).successValue())//

						.performActionResult(//

								windowSize -> stream -> stream.windowSliding(windowSize)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(

								List.emptyList())//
						.thenShouldBeEqualTo(

								List.list(List.list(1, 2, 3), List.list(2, 3, 4), List.list(3, 4, 5)))//

						.thenShouldBeEqualTo(

								List.list(List.list(1, 2)))//
						.thenShouldBeEqualTo(//
								List.list(List.list(1, 2, 3), List.list(2, 3, 4), List.list(3, 4, 5)))//
				//

				;

			}

			@Override
			@Test
			public void windowFixedAtMost(final BDDSoftAssertions softly) {

				final var numberOfElements = 3;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCases(//

								Stream.emptyStream(), //
								Stream.from(1), //
								List.extendedFactoryOperations().generateRange(1, 3).convert().toStream(),

								List.extendedFactoryOperations().generateRange(1, 11).convert().toStream()//

						)//
						.<ValidatedSize>//
						givenSingleArgument(ValidatedSize.of(3).successValue())//

						.performActionResult(//

								windowSize -> stream -> stream.windowFixedAtMost(windowSize)

										.toBoundedList(ValidatedSize.of(numberOfElements + 1).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(

								List.emptyList())//
						.thenShouldBeEqualTo(

								List.list(List.list(1, 2, 3), List.list(4, 5, 6), List.list(7, 8, 9),
										List.list(10, 11, 12)))//

						.thenShouldBeEqualTo(

								List.list(List.list(1, 2)))//
						.thenShouldBeEqualTo(//
								List.list(List.list(1, 2, 3), List.list(4, 5, 6), List.list(7, 8, 9), List.list(10)))//
				//

				;

			}

		}

		@Nested
		final class Combining //

				implements//
				StreamTestBehavior.Operations.Combining {
			@Override
			@Test
			public void append(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				final var list = List.list(2, 1, 4, 2, 6, 3);

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(list.convert().toStream()

						)//
						.<Stream<Integer>>//
						givenSingleArgument(Stream.from(1)).performActionResult(//

								joinedStream -> stream -> stream.append(joinedStream)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(list.addList(List.list(1, 2, 3, 4)))//
				//

				;

			}

			@Override
			@Test
			public void zipAsPossible(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				final var list = List.list(2, 1, 4, 2, 6, 3);

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(list.convert().toStream()

						)//
						.<Stream<Integer>>//
						givenSingleArgument(Stream.from(1))//
						.givenSingleArgument(list.convert().toStream())//

						.performActionResult(//

								joinedStream -> stream -> stream.zipAsPossible(joinedStream)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(

								list.zipAsPossible(List.list(1, 2, 3, 4, 5, 6)))//
						.thenShouldBeEqualTo(

								list.zipAsPossible(list))//
				//

				;

			}
		}

		@Nested
		final class Mapping //

				implements//
				StreamTestBehavior.Operations.Mapping {
			@Override
			@Test
			public void map(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(0)

						)//
						.<Function<Integer, Integer>>//
						givenSingleArgument(n -> n * 2).performActionResult(//

								elementTransformer -> stream -> stream.map(elementTransformer)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(
								List.extendedFactoryOperations().generateRange(0, numberOfElements).map(n -> n * 2))//
				//

				;

			}

			@Override
			@Test
			public void flatMap(final BDDSoftAssertions softly) {
				final var numberOfElements = 6;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(1)

						)//
						.<Function<Integer, Stream<Integer>>>//
						givenSingleArgument(n ->

						List.<Integer>emptyList().cons(n).cons(n * 2).convert().toStream()).performActionResult(//

								elementTransformer -> stream -> stream.flatMap(elementTransformer)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.list(2, 1, 4, 2, 6, 3))//
				//

				;

			}
		}

		@Nested
		final class Filtering //

				implements//
				StreamTestBehavior.Operations.Filtering {
			@Override
			@Test
			public void filter(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				final var infinteNumbersFromOne = Stream.from(1);
				final var infinteEvenNumbersFromTwo = Stream.unfold(2, number -> {
					final var newValue = number + 2;
					return Tuple2.of(number, newValue).getResult().successValue();
				});
				final var infinteOddNumbersFromOne = Stream.unfold(1, number -> {

					final var newValue = number + 2;
					return Tuple2.of(number, newValue).getResult().successValue();
				});

				final Predicate<Integer> isNumberDividedByThreeOrFive = n -> If.givenObject(n).is(iu -> iu % 3 == 0)
						.orIs(iu -> iu % 5 == 0).will().allTheRequirementsFit();

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCases(//
								infinteNumbersFromOne, //
								infinteEvenNumbersFromTwo, //
								infinteOddNumbersFromOne//

						)//
						.<Predicate<Integer>>//
						givenSingleArgument(isNumberDividedByThreeOrFive).performActionResult(//

								predicate -> stream -> stream.filter(predicate)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.list(3, 5, 6, 9, 10, 12, 15, 18, 20, 21))//
						.thenShouldBeEqualTo(List.list(6, 10, 12, 18, 20, 24, 30, 36, 40, 42))//
						.thenShouldBeEqualTo(List.list(3, 5, 9, 15, 21, 25, 27, 33, 35, 39))//

				;

			}

			@Override
			@Test
			public void dropWhile(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(0)

						)//
						.<Predicate<Integer>>//
						givenSingleArgument(n -> n < 10).performActionResult(//

								predicate -> stream -> stream.dropWhile(predicate)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(10, 20))//
				;

			}

			@Override
			@Test
			public void takeWhile(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.from(0)

						)//
						.<Predicate<Integer>>//
						givenSingleArgument(n -> n < 10).performActionResult(//

								predicate -> stream -> stream.takeWhile(predicate)

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(0, numberOfElements))//
				//

				;

			}
		}

		@Nested
		final class Creation //

				implements//
				StreamTestBehavior.Operations.Creation {
			@Override

			@Test
			public void emptyStream(final BDDSoftAssertions softly) {

				final var size = ValidatedSize.of(6).successValue();
				softly.then(Stream.emptyStream())//
						.satisfies(r -> {
							softly.then(r).isNotNull();
							softly.then(r.toBoundedList(size).isEmpty().successValue()).isTrue();
						});

			}

			@Override
			@Test
			public void from(final BDDSoftAssertions softly) {
				final var size = 6;
				final var start = 1;
				final var endExclusive = size + 1;

				final var actualList = Stream.from(start).toBoundedList(ValidatedSize.of(size).successValue());
				final var expectedList = List.extendedFactoryOperations().generateRange(start, endExclusive);
				softly.then(actualList).isEqualTo(expectedList);

			}

			@Override
			@Test
			public void fibs(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Stream<Integer>>//
						initializeCase(Stream.fibs())//
						.<Integer>//
						givenSingleArgument(6)//
						.givenSingleArgument(10)//
						.givenSingleArgument(15)//
						.givenSingleArgument(20)//

						.performActionResult(

								size -> stream -> stream.toBoundedList(ValidatedSize.of(size).successValue())
										.getListResult())
						.thenShouldBeEqualTo(List.list(1, 1, 2, 3, 5, 8))//
						.thenShouldBeEqualTo(List.list(1, 1, 2, 3, 5, 8, 13, 21, 34, 55))//
						.thenShouldBeEqualTo(List.list(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610))//
						.thenShouldBeEqualTo(List.list(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987//
								, 1597, 2584, 4181, 6765))//

				;
			}

			@Override
			@Test
			public void repeat(final BDDSoftAssertions softly) {

				final var numberOfElements = 10;
				final var listOfTenElements = List.extendedFactoryOperations()

						.generateRange(0, numberOfElements);
				TestMulHelper//
						.softAssertions(softly)//
						.<Integer>//
						initializeCases(1, 2, 3, 4, 5)//
						.withoutAnyArgument()

						.performActionResult(

								n -> Stream.repeat(() -> n)
										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())
										.getListResult())
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 1))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 2))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 3))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 4))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 5))//

				;

			}

			@Override
			@Test
			public void iterate(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;
				final var listOfTenElements = List.extendedFactoryOperations()

						.generateRange(0, numberOfElements);
				TestMulHelper//
						.softAssertions(softly)//
						.<Integer>//
						initializeCases(1, 2, 3, 4, 5)//
						.withoutAnyArgument()

						.performActionResult(

								n -> Stream.iterate(n, a -> a)
										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())
										.getListResult())
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 1))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 2))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 3))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 4))//
						.thenShouldBeEqualTo(listOfTenElements.map(i -> 5))//

				;

			}

			@Override
			@Test
			public void unfold(final BDDSoftAssertions softly) {
				final var numberOfElements = 10;

				TestMulHelper//
						.softAssertions(softly)//
						.<Integer>//
						initializeCases(1, 2, 3, 4, 5)//
						.withoutAnyArgument().performActionResult(//

								n -> Stream.unfold(n, number -> {

									final var newValue = number + 1;
									final var newState = number - 1;
									return Tuple2.of(newState, newValue).getResult().successValue();
								})//

										.toBoundedList(ValidatedSize.of(numberOfElements).successValue())//
										.getListResult()//
						)//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(0, numberOfElements))//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(1, numberOfElements + 1))//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(2, numberOfElements + 2))//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(3, numberOfElements + 3))//
						.thenShouldBeEqualTo(List.extendedFactoryOperations().generateRange(4, numberOfElements + 4))//

				;

			}

		}

		@Nested
		class StreamLazinessPower implements StreamTestBehavior.Laziness.Power {

			@Override

			@Test
			public void shouldStopInfiniteStreamWhenConditionMet() {
				final var actualFirstThirtyPrimes = primeNumbers()//

						.dropWhile(x -> x < 100)//
						.takeWhile(x -> x < 115)//
						.toBoundedList(ValidatedSize.of(30).successValue());
				assertThat(actualFirstThirtyPrimes).isEqualTo(List.list(101, 103, 107, 109, 113));

			}

			@Override

			@Test
			public void shouldGenerateFirstNPrimesLazily() {

				final var expectedFirstThirtyPrimes = //
						List.list(//
								2, 3, 5, 7, 11, 13, 17, 19, 23, 29, //
								31, 37, 41, 43, 47, 53, 59, 61, 67, //
								71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113);//

				final var actualFirstThirtyPrimes = primeNumbers().toBoundedList(ValidatedSize.of(30).successValue());
				assertThat(actualFirstThirtyPrimes).isEqualTo(expectedFirstThirtyPrimes);

			}

			public Stream<Integer> primeNumbers() {
				return Stream.from(2) //
						.filter(this::isPrime); //
			}

			// Check if a number is prime (lazily applied)
			private boolean isPrime(final int number) {
				if (number == 2)
					return true;

				return number > 1 //
						&& Stream.iterate(2, n -> n + 1)

								.toBoundedList(ValidatedSize.of((int) Math.sqrt(number)).successValue())
								.noneMatch(i -> number % i == 0) // No divisor means it's prime
								.successValue(); // No divisor means it's prime
			}

		}

		static class StreamLazinessEffect implements StreamTestBehavior.Laziness.General {

			private static UnsafeLazyList<Integer> streamNumbersFrom1To5 = Stream//
					.UnsafeLazyList//
					.<Integer>staticMethod()//
					.from(1) //
					.takeAtMost(5); //

			private static Function<Integer, Integer> multiplyByThree = x -> {
				System.out.println("Mapping " + x);
				return x * 3;
			};

			private static Predicate<Integer> isEven = x -> {
				System.out.println("Filtering " + x);
				return x % 2 == 0;
			};

			private static Predicate<Integer> isOdd = x -> {
				System.out.println("Filtering " + x);
				return x % 2 != 0;
			};

			@Override
			@Test
			public void shouldApplySideEffectUntilFirstOccurrence() {
				// Capture system output
				final var out = new ByteArrayOutputStream();
				System.setOut(new PrintStream(out));

				// Apply map and filter, but evaluation happens only when we call toList()
				streamNumbersFrom1To5.map(multiplyByThree).filterForTestingOnly(isEven);

				// Reset System.out
				System.setOut(System.out);

				final var expectedOutput = String.join(System.lineSeparator(), //
						"Mapping 1", "Filtering 3", //
						"Mapping 2", "Filtering 6") //
						+ System.lineSeparator();

				assertThat(out.toString()).isEqualTo(expectedOutput);
			}

			@Override
			@Test
			public void shouldApplySideEffectUntilFirstOccurrenceWithDifferentCondition() {
				final var out = new ByteArrayOutputStream();
				System.setOut(new PrintStream(out));

				// Apply map and filter, but evaluation happens only when we call toList()
				streamNumbersFrom1To5.map(multiplyByThree).filterForTestingOnly(isOdd);

				// Reset System.out
				System.setOut(System.out);

				final var expectedOutput = String.join(System.lineSeparator(), //
						"Mapping 1", "Filtering 3") //
						+ System.lineSeparator();

				assertThat(out.toString()).isEqualTo(expectedOutput);
			}

			@Override
			@Test
			public void shouldProcessStreamOnce() {
				final var out = new ByteArrayOutputStream();
				System.setOut(new PrintStream(out));

				streamNumbersFrom1To5.map(multiplyByThree).filterForTestingOnly(isEven).takeAtMostBeforeTrigger(5);

				System.setOut(System.out);

				final var expectedOutput = String.join(System.lineSeparator(), //
						"Mapping 1", "Filtering 3", //
						"Mapping 2", "Filtering 6", //
						"Mapping 3", "Filtering 9", //
						"Mapping 4", "Filtering 12", //
						"Mapping 5", "Filtering 15") //
						+ System.lineSeparator();

				assertThat(out.toString()).isEqualTo(expectedOutput);
			}

			@Override
			@Test
			public void shouldReturnTheRequiredResult() {
				final var out = new ByteArrayOutputStream();
				System.setOut(new PrintStream(out));

				final var lazyEvaluationResult = //
						streamNumbersFrom1To5.map(multiplyByThree)//
								.filterForTestingOnly(isEven)//
								.takeAtMostBeforeTrigger(5); // Only now is the stream evaluated

				assertThat(lazyEvaluationResult).isEqualTo(List.list(6, 12));
			}

		}

	}

}
