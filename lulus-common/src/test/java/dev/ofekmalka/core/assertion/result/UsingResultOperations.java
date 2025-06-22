package dev.ofekmalka.core.assertion.result;

import static dev.ofekmalka.support.TestMulHelper.softAssertions;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import dev.ofekmalka.core.assertion.result.Result.ErrorMessages;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.support.TestMulHelper;
import dev.ofekmalka.support.annotation.ArgumentName;
import dev.ofekmalka.support.annotation.CustomDisplayNameGenerator;
import dev.ofekmalka.support.annotation.MethodName;
import dev.ofekmalka.support.annotation.TwoArgumentNames;
import dev.ofekmalka.support.general.providers.ArgumentNameProvider;
import dev.ofekmalka.support.general.providers.MethodNameProvider;
import dev.ofekmalka.support.general.providers.TwoArgumentNamesProvider;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.helper.Nothing;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SoftAssertionsExtension.class)
public class UsingResultOperations {

	public static <T> Function<T, Result<T>> createSuccessResult() {
		return Result::success;
	}

	public static <T> Function<String, Result<T>> createFailureResultFromErrorMessage() {
		return Result::failure;
	}

	public static <T> Function<Exception, Result<T>> createFailureResultFromException() {
		return Result::failure;
	}

	public static <T> Function<RuntimeException, Result<T>> createFailureResultFromRuntimeException() {
		return Result::failureFromRuntimeException;
	}

	public static <T> Function<String, Function<Exception, Result<T>>> createFailureResultFromErrorMessageAndException() {
		return errorMessage -> exception -> Result.failure(errorMessage, exception);
	}

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class ShouldHandleInvalidScenariosWhenInvoke {

		@Nested
		@Order(1)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Creation implements//
				ResultTestBehavior.Operations.Invalid.Creation {
			/**
			 * Since this method Result.of(final T value) internally calls <br>
			 * Result.of(final T value, final String failureMessageForNullValue)<br>
			 * ,which is already tested, we only test its unique behavior in the
			 * error-handling tests.
			 */
			@Order(1)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "of", argumentName = "value")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void generalCreationWithNullValue(final String methodName, final String argumentName) {
				final var expectedFailure = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.<String>asFailure();

				assertThat(Result.of((String) null))//
						.as("Empty result should successfully be created")//
						.isEqualTo(expectedFailure);
			}

			/**
			 * Since this method Result.of(final Supplier<T> supplier) internally calls <br>
			 * of(final Supplier<T> supplier,final String
			 * errorMessageIfSupplierProduceNullValue)<br>
			 * ,which is already tested, we only test its unique behavior in the
			 * error-handling tests.
			 */

			@Order(2)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "of", argumentName = "supplier")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void generalCreationWithSupplierProduceNull(final String methodName, final String argumentName) {

				final var expectedFailure = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNot("provide null value")//
						.<String>asFailure();

				assertThat(Result.of(() -> null))//
						.as("Empty result should successfully be created")//
						.isEqualTo(expectedFailure);
			}

			@Order(3)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "of", argumentName = "failureMessageForNullValue")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void generalCreationWithNullInvalidValueOrFailureMessageForNullValue(final String methodName,
					final String argumentName, final BDDSoftAssertions softly) {//
				final Function<Integer, Function<String, Result<Integer>>> createResult = //
						value -> failureMessageForNullValue -> //
						Result.of(value, failureMessageForNullValue);//

				final var errorMessageCannotBeNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();
				final var errorMessageCannotBeBlank = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBe("blank")//
						.asErrorMessage();

				final var validValue = 1;
				softAssertions(softly)//
						.<Function<Integer, Function<String, Result<Integer>>>>//
						initializeCase(createResult)//
						.<Integer, String>//
						givenTwoArguments(null, null)//
						.givenTwoArguments(validValue, null)//
						.givenTwoArguments(null, "")//
						.givenTwoArguments(null, "valid failureMessageForNullValue")//

						.performActionResult(value -> failureMessageForNullValue -> creator -> //
						creator.apply(value).apply(failureMessageForNullValue))//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeNull)//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeNull)//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeBlank)//
						.thenShouldHaveSameErrorMessage("valid failureMessageForNullValue");//
			}

			@Order(4)
			@Override
			@ParameterizedTest
			@TwoArgumentNames(//
					methodName = "of", //
					firstArgumentName = "supplier", //
					secondArgumentName = "errorMessageIfCallableProduceNullValue") //
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			public void generalCreationWithInvalidSupplierOrFailureMessageForNullValue(//
					final String methodName, //
					final String firstArgumentName, //
					final String secondArgumentName, //
					final BDDSoftAssertions softly) {//
				final Function<Supplier<Integer>, Function<String, Result<Integer>>> createResult = //
						supplier -> errorMessageIfSupplierProduceNullValue -> //
						Result.of(supplier, errorMessageIfSupplierProduceNullValue);

				final var errorSupplierCannotBeNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();

				final var errorSupplierProduceRuntimeExceptionExplanation = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inStaticMethod(methodName)//
						.withThisErrorExplanation(
								"A runtime exception was thrown from the callable arg, which is undesirable!")//
						.asErrorMessage();

				final var errorMessageCannotBeNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(secondArgumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();

				final var errorMessageCannotBeBlank = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(secondArgumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBe("blank")//
						.asErrorMessage();

				final Supplier<Integer> validSupplier = () -> 1;
				softAssertions(softly)//
						.<Function<Supplier<Integer>, Function<String, Result<Integer>>>>//
						initializeCase(createResult)//
						.<Supplier<Integer>, String>//
						givenTwoArguments(null, null)//
						.givenTwoArguments(validSupplier, null)//
						.givenTwoArguments(null, "")//
						.givenTwoArguments(null, "valid failureMessageForNullValue")//
						.givenTwoArguments(() -> null, "valid failureMessageForNullValue")//
						.givenTwoArguments(() -> {

							throw new RuntimeException();
						}, "valid failureMessageForNullValue")//

						.performActionResult(supplier -> errorMessageIfSupplierProduceNullValue -> creator -> //
						creator.apply(supplier).apply(errorMessageIfSupplierProduceNullValue))//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeNull)//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeNull)//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeBlank)//
						.thenShouldHaveSameErrorMessage(errorSupplierCannotBeNull)//
						.thenShouldHaveSameErrorMessage("valid failureMessageForNullValue")//
						.thenShouldHaveSameErrorMessage(errorSupplierProduceRuntimeExceptionExplanation)//

				;
			}

			@Order(5)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "failure", //
					argumentName = "message")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void failureResultCreationFromErrorMessage(//
					final String methodName, //
					final String argumentName, //
					final BDDSoftAssertions softly) {//
				final var errorMessageCannotBeNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();

				final var errorMessageCannotBeBlank = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBe("blank")//
						.asErrorMessage();

				softAssertions(softly)//
						.<Function<String, Result<Object>>>//
						initializeCase(createFailureResultFromErrorMessage())//
						.<String>givenNullArgument().givenSingleArgument("")//
						.performActionResult(errorMessage -> creator -> creator.apply(errorMessage))//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeNull)//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeBlank)//
				;

			}

			@Order(6)
			@Override
			@ParameterizedTest
			@TwoArgumentNames(//
					methodName = "failure", //
					firstArgumentName = "message", //
					secondArgumentName = "exception") //
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			public void failureResultCreationFromErrorMessageAndException(//
					final String methodName, //
					final String firstArgumentName, //
					final String secondArgumentName, //
					final BDDSoftAssertions softly) {//

				final var errorMessageCannotBeNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();
				final var errorMessageCannotBeBlank = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBe("blank")//
						.asErrorMessage();

				final var errorExceptionCannotBeNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(secondArgumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();

				softAssertions(softly)//

						.<Function<String, Function<Exception, Result<String>>>>//
						initializeCase(createFailureResultFromErrorMessageAndException())//
						.<String, Exception>//
						givenTwoArguments(null, null)//
						.givenTwoArguments(null, new Exception())//

						.givenTwoArguments("", null)//
						.givenTwoArguments("valid failureMessageForNullValue", null)//

						.performActionResult(
								errorMessage -> exception -> creator -> creator.apply(errorMessage).apply(exception))//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeNull)//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeNull)//
						.thenShouldHaveSameErrorMessage(errorMessageCannotBeBlank)//
						.thenShouldHaveSameErrorMessage(errorExceptionCannotBeNull)//
				;

			}

			@Order(7)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "failure", //
					argumentName = "exception")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void failureResultCreationFromException(//
					final String methodName, //
					final String argumentName) {//

				final var actualResultFromException = Result.failure((Exception) null);
				final var expectedResultFromExceptionMessage = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();

				assertThat(actualResultFromException.failureValue().getMessage())
						.isEqualTo(expectedResultFromExceptionMessage);

			}

			@Order(8)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "failure", //
					argumentName = "runtimeException")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void failureResultCreationFromRuntimeException(//
					final String methodName, //
					final String argumentName) {

				final var actualResultFromException = Result.failureFromRuntimeException(null);
				final var expectedResultFromExceptionMessage = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inStaticMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();

				assertThat(actualResultFromException.failureValue().getMessage())
						.isEqualTo(expectedResultFromExceptionMessage);

			}

			/**
			 * Since this method lift(final Function<A, B> mapper) internally calls <br>
			 * map(Function<T, U> mapper)<br>
			 * ,which is already tested, we only test its unique behavior in the
			 * error-handling tests.
			 */
			@Order(9)
			@Override
			@ParameterizedTest
			@MethodName(name = "lift")
			@ArgumentsSource(MethodNameProvider.class)
			public void lift(final String methodName) {
				final var excpectedError = ErrorMessages//
						.inClass("Result")//
						.forArgumentWhich("be invoked by apply")//
						.inStaticMethod("lift")//
						.shouldNotBeNull()//
						.asErrorMessage();

				final var actual = Result.<Integer, String>lift(i -> i.toString()).apply(null)//
						.failureValue()//
						.getMessage();
				assertThat(actual).isEqualTo(excpectedError);
			}

			/**
			 * Since this method lift2(final Function<A, Function<B, C>> combiner)
			 * internally calls <br>
			 * map(Function<T, U> mapper) and flatMap(Function<T, Result<U>> flatMapper)
			 * <br>
			 * ,which are already tested, we only test its unique behavior in the
			 * error-handling tests.
			 */
			@Order(10)
			@Override
			@ParameterizedTest
			@MethodName(name = "lift2")
			@ArgumentsSource(MethodNameProvider.class)
			public void lift2(final String methodName, final BDDSoftAssertions softly) {
				//
				softAssertions(softly)//
						.<Function<Result<String>, Function<Result<String>, Result<String>>>>initializeCase(
								Result.lift2(a -> b -> a + b))//
						.<Result<String>, Result<String>>//
						givenTwoArguments(null, Result.success(""))//
						.givenTwoArguments(Result.success(""), null)//
						.performActionResult(firstResult -> secondResult -> lift2WithCombiner -> lift2WithCombiner
								.apply(firstResult).apply(secondResult))//
						.thenShouldHaveSameErrorMessage(ErrorMessages//
								.inClass("Result")//
								.forArgumentWhich("be invoked first by apply")//
								.inStaticMethod(methodName)//
								.shouldNotBeNull()//
								.asErrorMessage())//
						.thenShouldHaveSameErrorMessage(ErrorMessages//
								.inClass("Result")//
								.forArgumentWhich("be invoked second by apply")//
								.inStaticMethod(methodName)//
								.shouldNotBeNull()//
								.asErrorMessage());

			}

			/**
			 * Since this method lift3(final Function<A, Function<B, Function<C, D>>>
			 * combiner) internally calls <br>
			 * lift2(final Function<A, Function<B, C>> combiner) <br>
			 * ,which are already tested its unique behavior above , we only test lift3's
			 * unique behavior in the error-handling tests.
			 */
			@Order(11)
			@Override
			@ParameterizedTest
			@MethodName(name = "lift3")
			@ArgumentsSource(MethodNameProvider.class)
			public void lift3(final String methodName) {
				final var excpectedError = ErrorMessages//
						.inClass("Result")//
						.forArgumentWhich("be invoked third by apply")//
						.inStaticMethod("lift3")//
						.shouldNotBeNull()//
						.asErrorMessage();

				final var actual = Result.<Integer, Integer, Integer, String>lift3(a -> b -> c -> a.toString())
						.apply(Result.success(1)).apply(Result.success(2)).apply(null)//
						.failureValue()//
						.getMessage();
				assertThat(actual).isEqualTo(excpectedError);
			}

		}

		@Nested
		@Order(2)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Filtering implements//
				ResultTestBehavior.Operations.Invalid.Filtering {
			@Order(12)
			@Override
			@ParameterizedTest
			@TwoArgumentNames(methodName = "validate", //
					firstArgumentName = "predicate", //
					secondArgumentName = "failureMessage") //
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			public void validate(final String methodName, //
					final String firstArgumentName, //
					final String secondArgumentName, //
					final BDDSoftAssertions softly) {//

				final var errorMessagePredicateNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();//

				final var errorMessageFailureMessageNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(secondArgumentName)//
						.inMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();//

				final var errorMessageFailureMessageBlank = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(secondArgumentName)//
						.inMethod(methodName)//
						.shouldNotBe("blank")//
						.asErrorMessage();//

				final var errorMessageFunctionNullResult = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
						.asErrorMessage();//

				final var errorMessageFunctionRuntimeException = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage();//

				final var errorMessageLessThan100 = "Error: 42 is less than 100";

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCase(Result.success(42))//
						.<Function<Integer, Boolean>, String>//
						givenTwoArguments(null, "valid error message")//
						.givenTwoArguments(a -> true, null)//
						.givenTwoArguments(a -> true, "")//
						.givenTwoArguments(a -> null, "valid error message")//
						.givenTwoArguments(a -> {//
							throw new RuntimeException("bla bla bla");//
						}, "valid message")//
						.givenTwoArguments(zero -> zero > 100, errorMessageLessThan100)//
						.performActionResult(predicate -> failureMessage -> result -> //
						result.validate(predicate, failureMessage))//
						.thenShouldHaveSameErrorMessage(errorMessagePredicateNull)//
						.thenShouldHaveSameErrorMessage(errorMessageFailureMessageNull)//
						.thenShouldHaveSameErrorMessage(errorMessageFailureMessageBlank)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionNullResult)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionRuntimeException)//
						.thenShouldHaveSameErrorMessage(errorMessageLessThan100);//
			}

			@Order(13)
			@Override
			@ParameterizedTest
			@TwoArgumentNames(methodName = "reject", //
					firstArgumentName = "predicate", //
					secondArgumentName = "failureMessage") //
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			public void reject(final String methodName, final String firstArgumentName, final String secondArgumentName,
					final BDDSoftAssertions softly) {

				final var errorMessagePredicateNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();//

				final var errorMessageFailureMessageNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(secondArgumentName)//
						.inMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();//

				final var errorMessageFailureMessageBlank = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(secondArgumentName)//
						.inMethod(methodName)//
						.shouldNotBe("blank")//
						.asErrorMessage();//

				final var errorMessageFunctionNullResult = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
						.asErrorMessage();//

				final var errorMessageFunctionRuntimeException = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(firstArgumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage();//

				final var errorMessageLessThan100 = "Error: 42 is less than 0";

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCase(Result.success(42))//
						.<Function<Integer, Boolean>, String>//
						givenTwoArguments(null, "valid error message")//
						.givenTwoArguments(a -> true, null)//
						.givenTwoArguments(a -> true, "")//
						.givenTwoArguments(a -> null, "valid error message")//
						.givenTwoArguments(a -> {//
							throw new RuntimeException("bla bla bla");//
						}, "valid message")//
						.givenTwoArguments(zero -> zero > 0, errorMessageLessThan100)//
						.performActionResult(predicate -> failureMessage -> result -> //
						result.reject(predicate, failureMessage))//
						.thenShouldHaveSameErrorMessage(errorMessagePredicateNull)//
						.thenShouldHaveSameErrorMessage(errorMessageFailureMessageNull)//
						.thenShouldHaveSameErrorMessage(errorMessageFailureMessageBlank)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionNullResult)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionRuntimeException)//
						.thenShouldHaveSameErrorMessage(errorMessageLessThan100);//
			}

		}

		@Nested
		@Order(3)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Mapping implements//
				ResultTestBehavior.Operations.Invalid.Mapping {
			@Order(14)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "map", argumentName = "mapper")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void map(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var errorMessagePredicateNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();//

				final var errorMessageFunctionNullResult = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
						.asErrorMessage();//

				final var errorMessageFunctionRuntimeException = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage();//

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCase(Result.success(42))//
						.<Function<Integer, String>>//
						givenNullArgument().givenSingleArgument(a -> null)//
						.givenSingleArgument(a -> {//
							throw new RuntimeException("bla bla bla");//
						})//
						.performActionResult(mapper -> result -> //
						result.map(mapper))//
						.thenShouldHaveSameErrorMessage(errorMessagePredicateNull)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionNullResult)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionRuntimeException);//
			}

			@Order(15)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "flatMap", argumentName = "flatMapper")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void flatMap(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var errorMessagePredicateNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName)//
						.shouldNotBeNull()//
						.asErrorMessage();//

				final var errorMessageFunctionNullResult = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
						.asErrorMessage();//

				final var errorMessageFunctionRuntimeException = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName)//
						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage();//

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCase(Result.success(42))//
						.<Function<Integer, Result<String>>>//
						givenNullArgument()//
						.givenSingleArgument(a -> null)//
						.givenSingleArgument(a -> {//
							throw new RuntimeException("bla bla bla");//
						})//
						.performActionResult(flatMapper -> result -> //
						result.flatMap(flatMapper))//
						.thenShouldHaveSameErrorMessage(errorMessagePredicateNull)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionNullResult)//
						.thenShouldHaveSameErrorMessage(errorMessageFunctionRuntimeException);//
			}

		}

		@Nested
		@Order(4)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class SpecialCases implements//
				ResultTestBehavior.Operations.Invalid.SpecialCase {

			@Override
			@Order(17)

			@Test
			public void mapEmpty() {
				assertThat(Result.success(10).mapEmpty().failureValue().getMessage()).isEqualTo("Not empty");

			}

		}

		@Nested
		@Order(5)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class ErrorHandling implements//

				ResultTestBehavior.Operations.Invalid.ErrorHandling {

			@Order(16)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "prependFailureMessage", argumentName = "additionalFailureMessage")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void prependFailureMessage(final String methodName, final String argumentName,
					final BDDSoftAssertions softly) {

				final var errorAdditionalFailureMessageNull = ErrorMessages//
						.inClass("Result")//
						.forArgumentName("additionalFailureMessage")//
						.inMethod("prependFailureMessage")//
						.shouldNotBeNull()//
						.asErrorMessage();//

				final var errorAdditionalFailureMessageBlank = ErrorMessages//
						.inClass("Result")//
						.forArgumentName("additionalFailureMessage")//
						.inMethod("prependFailureMessage")//
						.shouldNotBe("blank")//
						.asErrorMessage();//

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCase(Result.failure("first line of error message"))//
						.<String>//
						givenNullArgument().givenSingleArgument("")//

						.performActionResult(flatMapper -> result -> //
						result.prependFailureMessage(flatMapper))//
						.thenShouldHaveSameErrorMessage(errorAdditionalFailureMessageNull)//
						.thenShouldHaveSameErrorMessage(errorAdditionalFailureMessageBlank);//
			}

			@SuppressWarnings("unused")
			@Order(18)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "getOrCreateFailureInstanceWithMessage", //
					argumentName = "failureHandler")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void getOrCreateFailureInstanceWithMessage(//
					final String methodName, //
					final String argumentName, //
					final BDDSoftAssertions softly) {

				final var errorArgument = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName);
				final var errorArgumentIsNull = //
						errorArgument//
								.shouldNotBeNull()//
								.asErrorMessage();//

				final var errorFunctionReturnNull = //
						errorArgument//
								.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
								.asErrorMessage();//

				final var errorFunctionThrowingRuntimeException = //
						errorArgument//
								.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
								.asErrorMessage();//

				final var errorCalledOnEmptyInstance = //
						errorArgument//
								.withThisErrorExplanation("Called on a Empty instance")//
								.asErrorMessage();//

				softAssertions(softly)//
						.<Result<List<Integer>>>//
						initializeCases(//
								Result.success(List.list(42)), //
								Result.empty()//
						)//
						.<Function<String, List<Integer>>>//
						givenNullArgument()//
						.givenSingleArgument(errorMessage -> null)//
						.givenSingleArgument(errorMessage -> {//
							throw new RuntimeException("bla bla bla");//
						})//

						.givenSingleArgument(List::failureWithMessage)//
						.performActionResult(//
								failureHandler -> //
								result -> {//
									try {//
										return //
										result.<List<Integer>>getOrCreateFailureInstanceWithMessage(failureHandler)//
												.getListResult();//
									} catch (final Exception e) {
										return List.<Integer>failureWithMessage(e.getMessage()).getListResult();
									}

								}
//								TryRaw.of(() -> result.getOrCreateFailureInstanceWithMessage(failureHandler))//
//										.evaluate()//
						)//
						.thenShouldHaveSameErrorMessage(errorArgumentIsNull)//
						.thenShouldHaveSameErrorMessage(errorFunctionReturnNull)//
						.thenShouldHaveSameErrorMessage(errorFunctionThrowingRuntimeException)
						.thenShouldBeEqualTo(List.list(42))//
						.thenShouldHaveSameErrorMessage(errorArgumentIsNull)//
						.thenShouldHaveSameErrorMessage(errorFunctionReturnNull)//
						.thenShouldHaveSameErrorMessage(errorFunctionThrowingRuntimeException)
						.thenShouldHaveSameErrorMessage(errorCalledOnEmptyInstance)

				;

			}
		}

		

		@Nested
		@Order(7)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Conditional implements//

				ResultTestBehavior.Operations.Invalid.Conditional {
			@Order(21)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "or", //
					argumentName = "alternativeIfTrue")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void or(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var errorArgument = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName);
				final var errorAlternativeIfTrueIsNull = errorArgument//
						.shouldNotBeNull()//
						.asErrorMessage();

				final var errorAlternativeIfTrueReturnNull = errorArgument//

						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
						.asErrorMessage();

				final var errorAlternativeIfTrueThrowRuntimeException = errorArgument//

						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage();

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCase(// no matter the kind of Result
								Result.success(42))//
						.<Supplier<Result<Integer>>>//
						givenNullArgument()//
						.givenSingleArgument(() -> null)//
						.givenSingleArgument(() -> {//
							throw new RuntimeException("bla bla bla");//
						})//
						.performActionResult(alternativeIfTrue -> result -> //
						result.or(alternativeIfTrue))//
						.thenShouldHaveSameErrorMessage(errorAlternativeIfTrueIsNull)//
						.thenShouldHaveSameErrorMessage(errorAlternativeIfTrueReturnNull)//
						.thenShouldHaveSameErrorMessage(errorAlternativeIfTrueThrowRuntimeException);//

			}

			@Order(22)
			@Override
			@ParameterizedTest
			@ArgumentName(methodName = "and", //
					argumentName = "next")
			@ArgumentsSource(ArgumentNameProvider.class)
			public void and(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var errorArgument = ErrorMessages//
						.inClass("Result")//
						.forArgumentName(argumentName)//
						.inMethod(methodName);
				final var errorAlternativeIfTrueIsNull = errorArgument//
						.shouldNotBeNull()//
						.asErrorMessage();

				final var errorAlternativeIfTrueReturnNull = errorArgument//

						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())//
						.asErrorMessage();

				final var errorAlternativeIfTrueThrowRuntimeException = errorArgument//

						.withThisErrorExplanation(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage())//
						.asErrorMessage();

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCase(// no matter the kind of Result
								Result.success(42))//
						.<Supplier<Result<Integer>>>//
						givenNullArgument()//
						.givenSingleArgument(() -> null)//
						.givenSingleArgument(() -> {//
							throw new RuntimeException("bla bla bla");//
						})//
						.performActionResult(next -> result -> //
						result.and(next))//
						.thenShouldHaveSameErrorMessage(errorAlternativeIfTrueIsNull)//
						.thenShouldHaveSameErrorMessage(errorAlternativeIfTrueReturnNull)//
						.thenShouldHaveSameErrorMessage(errorAlternativeIfTrueThrowRuntimeException);//
			}

		}

	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class ShouldSuccessfully {

		@Nested
		@Order(1)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Creating implements ResultTestBehavior.Operations.Creation //
		{

			@Override
			@Order(1)
			@Test
			public void emptyResult() {
				assertThat(Result.empty())//
						.as("Empty result should successfully be created")//
						.matches(Result::isEmpty);
			}

			@Override
			@Order(2)
			@Test
			public void successResult(final BDDSoftAssertions softly) {
				final var expectedSuccessContent = "simple string";
				softAssertions(softly)//
						.<Function<String, Result<String>>>//
						initializeCase(createSuccessResult())//
						.givenSingleArgument(expectedSuccessContent)//
						.performActionResult(content -> creator -> creator.apply(content).lift())//
						.thenShouldDoAllTheRequirementsHereFitForObject(tester -> element -> tester.then(element)//
								.satisfies(r -> {
									tester.then(r.isSuccess()).isTrue();
									tester.then(r.successValue()).isEqualTo(expectedSuccessContent);
								}));
			}

			@Override
			@Order(3)
			@Test
			public void failureResultFromErrorMessage(final BDDSoftAssertions softly) {

				final var expectedContentOfErrorMessage = "simple error";

				softAssertions(softly)//
						.<Function<String, Result<Object>>>//
						initializeCase(createFailureResultFromErrorMessage())//
						.givenSingleArgument(expectedContentOfErrorMessage)//
						.performActionResult(errorMessage -> creator -> creator.apply(errorMessage).lift())//
						.thenShouldDoAllTheRequirementsHereFitForObject(tester -> element -> tester.then(element)//
								.satisfies(r -> {
									tester.then(r.isFailure()).isTrue();
									tester.then(r.failureValue().getMessage()).isEqualTo(expectedContentOfErrorMessage);
								}
								//
								));

			}

			@Override
			@Order(4)
			@Test
			public void failureResultFromErrorMessageAndException(final BDDSoftAssertions softly) {

				final var expectedContentOfErrorMessage = "simple error";

				final var expectedExceptionMessage = "hi, it's a dummy exception";
				final var expectedException = new Exception(expectedExceptionMessage);

				softAssertions(softly)//
						.<Function<String, Function<Exception, Result<Object>>>>//
						initializeCase(createFailureResultFromErrorMessageAndException())//
						.<String, Exception>givenTwoArguments(expectedContentOfErrorMessage, expectedException)
						.performActionResult(errorMessage -> exception -> creator -> creator.apply(errorMessage)
								.apply(exception).lift())//
						.thenShouldDoAllTheRequirementsHereFitForObject(tester -> element -> tester.then(element)//
								.matches(Result::isFailure)//
								.satisfies(r -> {

									tester.then(r.failureValue().getMessage()).isEqualTo(expectedContentOfErrorMessage);
									tester.then(r.failureValue().getCause()).isInstanceOf(Exception.class);
									tester.then(r.failureValue().getCause().getMessage())
											.isEqualTo(expectedExceptionMessage);

								}

								));

			}

			@Override
			@Order(5)
			@Test
			public void failureResultFromException(final BDDSoftAssertions softly) {

				final var expectedExceptionMessage = "hi, it's a dummy exception";
				final var expectedException = new Exception(expectedExceptionMessage);

				softAssertions(softly)//
						.<Function<Exception, Result<Object>>>//
						initializeCase(createFailureResultFromException())//
						.<Exception>givenSingleArgument(expectedException)//

						.performActionResult(exception -> creator -> creator.apply(exception).lift())//
						.thenShouldDoAllTheRequirementsHereFitForObject(tester -> element -> tester.then(element)//
								.matches(Result::isFailure)//
								.satisfies(r -> {
									tester.then(r.failureValue().getCause()).isInstanceOf(Exception.class);
									tester.then(r.failureValue().getCause().getMessage())
											.isEqualTo(expectedExceptionMessage);

								}

								));

			}

			@Override
			@Order(6)
			@Test
			public void failureResultFromRuntimeException(final BDDSoftAssertions softly) {

				final var expectedExceptionMessage = "hi, it's a dummy exception";
				final var expectedException = new RuntimeException(expectedExceptionMessage);

				softAssertions(softly)//
						.<Function<RuntimeException, Result<Object>>>//
						initializeCase(createFailureResultFromRuntimeException())//
						.<RuntimeException>givenSingleArgument(expectedException)//

						.performActionResult(exception -> creator -> creator.apply(exception).lift())//
						.thenShouldDoAllTheRequirementsHereFitForObject(tester -> element -> tester.then(element)//
								.matches(Result::isFailure)//
								.satisfies(r -> {
									tester.then(r.failureValue()).isInstanceOf(RuntimeException.class);
									tester.then(r.failureValue().getMessage()).isEqualTo(expectedExceptionMessage);

								}

								));

			}

		}

		@Nested
		@Order(2)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Filtering implements ResultTestBehavior.Operations.Filtering //
		{
			@Override
			@Order(1)
			@Test
			public void validate(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(Result.success(42), Result.failure("error message"),
								Result.empty())//
						.<Function<Integer, Boolean>, String>//
						givenTwoArguments(zero -> zero < 100, "valid error message")//
						.performActionResult(predicate -> failureMessage -> result -> //
						result.validate(predicate, failureMessage).lift())//
						.thenShouldBeEqualTo(Result.success(42))
						.thenShouldBeEqualTo(Result.<Integer>failure("error message"))//
						.thenShouldBeEqualTo(Result.<Integer>empty());
			}

			@Override
			@Order(2)
			@Test
			public void reject(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(Result.success(42), Result.failure("error message"),
								Result.empty())//
						.<Function<Integer, Boolean>, String>//
						givenTwoArguments(zero -> zero > 100, "valid error message")//
						.performActionResult(predicate -> failureMessage -> result -> //
						result.reject(predicate, failureMessage).lift())//
						.thenShouldBeEqualTo(Result.success(42))
						.thenShouldBeEqualTo(Result.<Integer>failure("error message"))//
						.thenShouldBeEqualTo(Result.<Integer>empty());

			}
		}

		@Nested
		@Order(3)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Mapping implements ResultTestBehavior.Operations.Mapping //
		{
			@Override
			@Order(3)
			@Test
			public void map(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(Result.success(42), Result.failure("error message"),
								Result.empty())//
						.<Function<Integer, String>>//
						givenSingleArgument(number -> number + "$")//
						.performActionResult(mapper -> result -> //
						result.map(mapper).lift())//
						.thenShouldBeEqualTo(Result.success("42$"))
						.thenShouldBeEqualTo(Result.<String>failure("error message"))//
						.thenShouldBeEqualTo(Result.<String>empty());

			}

			@Override
			@Order(4)
			@Test
			public void flatMap(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(Result.success(42), Result.failure("error message"),
								Result.empty())//
						.<Function<Integer, Result<String>>>//
						givenSingleArgument(number -> Result.success(number + "$"))//
						.performActionResult(flatMapper -> result -> //
						result.flatMap(flatMapper).lift())//
						.thenShouldBeEqualTo(Result.success("42$"))
						.thenShouldBeEqualTo(Result.<String>failure("error message"))//
						.thenShouldBeEqualTo(Result.<String>empty());
			}

		}

		@Nested
		@Order(4)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class ErrorHandling implements ResultTestBehavior.Operations.ErrorHandling //
		{

			@Override
			@Order(5)
			@Test
			public void prependFailureMessage(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("second line of error message"), //
								Result.empty()//
						)//
						.<String>//
						givenSingleArgument("first line of error message")//
						.performActionResult(additionalFailureMessage -> result -> //
						result.prependFailureMessage(additionalFailureMessage).lift())//
						.thenShouldBeEqualTo(Result.success(42)).thenShouldBeEqualTo(Result.<String>failure("""
								first line of error message\

								second line of error message"""))//
						.thenShouldBeEqualTo(Result.<Integer>empty());
			}

			@Override
			@Order(19)
			@Test
			public void prependMethodNameToFailureMessage(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("second line of error message"), //
								Result.empty()//
						)//
						.<String>//
						givenSingleArgument("methodName")//
						.performActionResult(methodName -> result -> //
						result.prependMethodNameToFailureMessage(methodName).lift())//
						.thenShouldBeEqualTo(Result.success(42))
						.thenShouldBeEqualTo(
								Result.<String>failure(String.format(ErrorTracker.CALL_FORMAT, "methodName") + "\n"
										+ "second line of error message"))//
						.thenShouldBeEqualTo(Result.<Integer>empty());

			}

			@Override
			@Order(20)
			@Test
			public void mapFailureForOtherObject() {
				assertThat(Result.<Integer>failure("ERROR MESSAGE IN RESULT INTEGER"))
						.extracting(Result::mapFailureForOtherObject)
						.isEqualTo(Result.<String>failure("ERROR MESSAGE IN RESULT INTEGER"));
			}

			@Override
			@Order(21)
			@Test
			public void getOrCreateFailureInstanceWithMessage(final BDDSoftAssertions softly) {
				softAssertions(softly)//
						.<Result<List<Integer>>>initializeCases(// no matter the kind of Result
								Result.success(List.list(42)), //
								Result.failure("ERROR MESSAGE IN RESULT INTEGER")//
						)//
						.<Function<String, List<Integer>>>givenSingleArgument(List::failureWithMessage)//

						.performActionResult(//
								failureHandler -> //
								result -> //
								result.getOrCreateFailureInstanceWithMessage(failureHandler).getListResult()//

						)//
						.thenShouldBeEqualTo(List.list(42))

						.thenShouldHaveSameErrorMessage("ERROR MESSAGE IN RESULT INTEGER");

			}

			@Override
			@Test
			public void removeCalledByMethodPrefixes(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.<Integer>failure("second line of error message")
										.prependMethodNameToFailureMessage("methodName1")
										.prependMethodNameToFailureMessage("methodName2"), //
								Result.<Integer>empty()//
						)//

						.withoutAnyArgument().performActionResult(result -> //
						result.removeCalledByMethodPrefixes().lift())//
						.thenShouldBeEqualTo(Result.success(42))
						.thenShouldBeEqualTo(Result.failure("second line of error message"))//
						.thenShouldBeEqualTo(Result.<Integer>empty());

			}

			@Override
			@Test
			public void extractOnlyUserError(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure((Exception) null)//
								, Result.<Integer>empty()//
						)//

						.withoutAnyArgument().performActionResult(result -> //
						result.extractOnlyUserError().lift())//
						.thenShouldBeEqualTo(Result.success(42))//
						.thenShouldBeEqualTo(

								Result.failure("It should not be null"))//
						.thenShouldBeEqualTo(Result.<Integer>empty());

			}

		}

		@Nested
		@Order(5)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class SpecialCases implements ResultTestBehavior.Operations.SpecialCase //
		{
			@Override
			@Order(6)
			@Test
			public void mapEmpty(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(Result.empty(), Result.failure("error message"))//
						.withoutAnyArgument()//
						.performActionResult(result -> result.mapEmpty().lift())//
						.thenShouldBeEqualTo(Result.success(Nothing.INSTANCE))
						.thenShouldBeEqualTo(Result.<Nothing>failure("error message"));
			}

		}

		

		@Nested
		@Order(7)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Equality implements ResultTestBehavior.Operations.Equality //
		{
			@Override
			@Order(22)
			@Test
			public void isNotSuccess(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.withoutAnyArgument()//
						.performActionResult(result -> result.lift().map(Result::isNotSuccess))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(true);

			}

			@Override
			@Order(23)
			@Test
			public void isEqualTo(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.<Result<Integer>>givenSingleArgument(Result.success(42))//
						.givenSingleArgument(Result.failure("Error message"))//
						.givenSingleArgument(Result.empty())//
						.performActionResult(obj -> result -> Result.success(result.isEqualTo(obj)))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//

				;

			}

		}

		@Nested
		@Order(8)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class RetrievingValues implements ResultTestBehavior.Operations.RetrievingValues //
		{
			@Override
			@Order(10)
			@Test
			public void getOrElse(final BDDSoftAssertions softly) {

				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.<Integer>givenSingleArgument(100)
						.performActionResult(
								defaultValue -> result -> result.lift().map(r -> r.getOrElse(defaultValue)))//
						.thenShouldBeEqualTo(42)//
						.thenShouldBeEqualTo(100)//
						.thenShouldBeEqualTo(100);

			}

			@Override
			@Order(11)
			@Test
			public void getOrElseForSupplierArgument(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.<Supplier<Integer>>givenSingleArgument(() -> 100)
						.performActionResult(defaultValueSupplier -> result -> result.lift()
								.map(r -> r.getOrElse(defaultValueSupplier)))//
						.thenShouldBeEqualTo(42)//
						.thenShouldBeEqualTo(100)//
						.thenShouldBeEqualTo(100);

			}

			@Override
			@Order(12)
			@Test
			public void successValue(final BDDSoftAssertions softly) {

				assertThat(Result.success(42).successValue()).isEqualTo(42);

			}

			@Override
			@Order(13)
			@Test
			public void failureValue(final BDDSoftAssertions softly) {
				final var expectedContentOfErrorMessage = "simple error";
				softly.then(Result.failure(expectedContentOfErrorMessage))//
						.satisfies(r -> softly.then(r.failureValue())//
								.isInstanceOf(IllegalStateException.class)//
								.hasMessage(expectedContentOfErrorMessage));

			}
		}

		@Nested
		@Order(9)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class SuccessFailureQueries implements ResultTestBehavior.Operations.SuccessFailureQueries //
		{

			@Override
			@Order(7)
			@Test
			public void isSuccess(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.withoutAnyArgument()//
						.performActionResult(result -> result.lift().map(Result::isSuccess))//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false);
			}

			@Override
			@Order(8)
			@Test
			public void isFailure(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.withoutAnyArgument()//
						.performActionResult(result -> result.lift().map(Result::isFailure))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false);
			}

			@Override
			@Order(9)
			@Test
			public void isEmpty(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.withoutAnyArgument()//
						.performActionResult(result -> result.lift().map(Result::isEmpty))//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(false)//
						.thenShouldBeEqualTo(true);
			}

		}

		@Nested
		@Order(10)
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		class Conditional implements ResultTestBehavior.Operations.Conditional //
		{

			@Override
			@Order(16)
			@Test
			public void or(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.<Supplier<Result<Integer>>>//
						givenSingleArgument(() -> Result.success(100))//
						.givenSingleArgument(Result::empty)//
						.givenSingleArgument(() -> Result.failure("Diffrent error message"))
						.performActionResult(alternativeIfTrue -> result -> result.or(alternativeIfTrue).lift())//
						.thenShouldBeEqualTo(Result.success(42))//
						.thenShouldBeEqualTo(Result.success(42))//
						.thenShouldBeEqualTo(Result.success(42))//
						.thenShouldBeEqualTo(Result.success(100))// the only diffrent state
						.thenShouldBeEqualTo(Result.<Integer>failure("Error message"))//
						.thenShouldBeEqualTo(Result.<Integer>failure("Error message"))//
						.thenShouldBeEqualTo(Result.<Integer>empty())//
						.thenShouldBeEqualTo(Result.<Integer>empty())//
						.thenShouldBeEqualTo(Result.<Integer>empty());

			}

			@Override
			@Order(17)
			@Test
			public void and(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.<Supplier<Result<Integer>>>//
						givenSingleArgument(() -> Result.success(100))//
						.givenSingleArgument(Result::empty)//
						.givenSingleArgument(() -> Result.failure("Diffrent error message"))
						.performActionResult(next -> result -> result.and(next).lift())//
						.thenShouldBeEqualTo(Result.success(100))// return next
						.thenShouldBeEqualTo(Result.<Integer>empty())// return next
						.thenShouldBeEqualTo(Result.<Integer>failure("Diffrent error message"))// return next
						.thenShouldBeEqualTo(Result.<Integer>failure("Error message"))//
						.thenShouldBeEqualTo(Result.<Integer>failure("Error message"))//
						.thenShouldBeEqualTo(Result.<Integer>failure("Error message"))//
						.thenShouldBeEqualTo(Result.<Integer>empty())//
						.thenShouldBeEqualTo(Result.<Integer>empty())//
						.thenShouldBeEqualTo(Result.<Integer>empty());
			}

			@Override
			@Order(18)
			@Test
			public void orElse(final BDDSoftAssertions softly) {
				TestMulHelper//
						.softAssertions(softly)//
						.<Result<Integer>>initializeCases(//
								Result.success(42), //
								Result.failure("Error message"), //
								Result.empty())//
						.<Supplier<Result<Integer>>>//
						givenSingleArgument(() -> Result.success(100))//
						.givenSingleArgument(Result::empty)//
						.givenSingleArgument(() -> Result.failure("Diffrent error message"))
						.performActionResult(defaultValue -> result -> result.orElse(defaultValue).lift())//
						.thenShouldBeEqualTo(Result.success(42))//
						.thenShouldBeEqualTo(Result.success(42))//
						.thenShouldBeEqualTo(Result.success(42))//
						.thenShouldBeEqualTo(Result.success(100))// return next
						.thenShouldBeEqualTo(Result.<Integer>empty())// return next
						.thenShouldBeEqualTo(Result.<Integer>failure("Diffrent error message"))// return next
						.thenShouldBeEqualTo(Result.success(100))// return next
						.thenShouldBeEqualTo(Result.<Integer>empty())// return next
						.thenShouldBeEqualTo(Result.<Integer>failure("Diffrent error message"))// return next
				;
			}

		}

	}
}
