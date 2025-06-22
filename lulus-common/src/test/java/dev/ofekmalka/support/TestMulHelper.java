package dev.ofekmalka.support;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;

/**
 * A helper class for running clean and powerful tests with JUnit 5 and AssertJ.
 * This framework allows you to validate multiple aspects of method calls and
 * their input data in a clean, reusable way.
 *
 * **Important Usage Guidelines:**
 *
 * 1. **Immutable Objects Only**: The generic type parameter <T> must be an
 * immutable object. This is essential to ensure that the testing framework
 * operates correctly, preserving the principles of functional programming and
 * avoiding side effects during testing. - Mutable objects (e.g., lists, maps,
 * streams) that are modified within the test should not be used as arguments. -
 * Instead, create new instances of collections, streams, or other objects as
 * needed.
 *
 * 2. **Stream Handling**: Be cautious when using streams as arguments. Streams
 * are inherently consumed when processed, which can interfere with subsequent
 * tests. - Always provide **fresh instances** of streams to ensure that each
 * test operates on a valid, untouched stream. - Immutable collections or
 * results should be preferred as arguments.
 *
 * 3. **Test Data**: Ensure the test cases initialized with `initializeCases`
 * contain consistent, valid data. This ensures predictable and reliable
 * behavior during testing. - Each test case should reflect a distinct scenario,
 * ensuring comprehensive coverage of the logic under test.
 *
 * 4. **Avoid Shared State**: Since functional programming emphasizes
 * immutability, avoid modifying shared data within the test execution. Instead,
 * create isolated, independent instances for each test case. - This principle
 * helps in maintaining pure, predictable tests.
 *
 * 5. **Fluent API**: The class leverages AssertJ for fluent assertions. Follow
 * the chain of assertions to make your test results clear and expressive. - Use
 * methods like `thenShouldBeEqualTo`, `performActionResult`, and
 * `withoutAnyArgument` to express your test flow clearly. - When dealing with
 * collections or complex results, use AssertJ’s powerful matching functions to
 * assert against expected results.
 *
 * **Example Usage**: Below is a basic example of using `TestMulHelper` to test
 * a list creation method:
 *
 * <pre>
 * TestMulHelper.<List<String>>softAssertions(softly)
 * 		.initializeCases(List.createFromStreamAndRemoveNullsValues(Stream.of("A", "B", "C", "D")),
 * 				List.createFromStreamAndRemoveNullsValues(Stream.of("A", null, "C", "D")),
 * 				List.createFromStreamAndRemoveNullsValues(Stream.empty()))
 * 		.performActionResult(List::getListResult).thenShouldBeEqualTo(List.list("A", "B", "C", "D"))
 * 		.thenShouldBeEqualTo(List.list("A", "C", "D")).thenShouldBeEqualTo(List.<String>emptyList());
 * </pre>
 *
 * **Notes**: - Each case is initialized with different input data to test
 * multiple outcomes. - Assertions are chained using AssertJ’s fluent API to
 * clearly check each expected result.
 *
 * @author ofekm
 * @param <T> the type parameter must be an immutable object
 */
public final class TestMulHelper<T> {

	private final List<T> object; // Can be any object

	private final BDDSoftAssertions assertion;

	private TestMulHelper(final List<T> object, final BDDSoftAssertions softly) {
		this.object = object;
		this.assertion = softly;
	}

	public static TypeAssertions softAssertions(final BDDSoftAssertions softly) {
		return new TypeAssertions(softly);
	}

	public static class TypeAssertions {

		private final BDDSoftAssertions assertion;

		private TypeAssertions(final BDDSoftAssertions softly) {
			assertion = softly;
		}

		public <T> TestMulHelper<T> initializeCase(final T a) {

			return new TestMulHelper<>(List.list(a), assertion);
		}

		public <T> TestMulHelper<T> initializeCases(final T a, final T b) {

			return new TestMulHelper<>(List.list(a, b), assertion);
		}

		public <T> TestMulHelper<T> initializeCases(final T a, final T b, final T c) {
			return new TestMulHelper<>(List.list(a, b, c), assertion);
		}

		//////////////////////////////////////////////////////////////////////////////////
		// You should use these operations bellow only if you provide
		////////////////////////////////////////////////////////////////////////////////// withoutAnyArgument()
		////////////////////////////////////////////////////////////////////////////////// or
		// one the "given" state
		/////////////////////////////////////////////////////////////////////////////////////

		public <T> TestMulHelper<T> initializeCases(final T a, final T b, final T c, final T d) {
			return new TestMulHelper<>(List.list(a, b, c, d), assertion);
		}

		public <T> TestMulHelper<T> initializeCases(final T a, final T b, final T c, final T d, final T e) {
			return new TestMulHelper<>(List.list(a, b, c, d, e), assertion);
		}

		public <T> TestMulHelper<T> initializeCases(final T a, final T b, final T c, final T d, final T e, final T f) {
			return new TestMulHelper<>(List.list(a, b, c, d, e, f), assertion);
		}

		public <T> TestMulHelper<T> initializeCases(final T a, final T b, final T c, final T d, final T e, final T f,
				final T g) {
			return new TestMulHelper<>(List.list(a, b, c, d, e, f, g), assertion);
		}

		public <T> TestMulHelper<T> initializeCases(final T a, final T b, final T c, final T d, final T e, final T f,
				final T g, final T h) {
			return new TestMulHelper<>(List.list(a, b, c, d, e, f, g, h), assertion);
		}

		public <T> TestMulHelper<T> initializeCases(final T a, final T b, final T c, final T d, final T e, final T f,
				final T g, final T h, final T i) {
			return new TestMulHelper<>(List.list(a, b, c, d, e, f, g, h, i), assertion);
		}

		// and so on

	}

	// Class representing the "given" state with a method to provide the list

	public <A> WithoutAnyArgumentContext<T> withoutAnyArgument() {
		return new WithoutAnyArgumentContext<>(object, assertion);
	}

	public <A> SingleArgumentContext<T, A> givenSingleArgument(final A a) {
		return new SingleArgumentContext<>(object, assertion, a);
	}

	public <A> SingleArgumentContext<T, A> givenNullArgument() {
		return new SingleArgumentContext<>(object, assertion, (A) null);
	}

	public <A, B> TwoArgumentsContext<T, A, B> givenTwoArguments(final A a, final B b) {
		return new TwoArgumentsContext<>(object, assertion, a, b);
	}

	public <A, B, C> ThreeArgumentsContext<T, A, B, C> givenThreeArguments(final A a, final B b, final C c) {
		return new ThreeArgumentsContext<>(object, assertion, a, b, c);
	}

	public static final class WithoutAnyArgumentContext<T> {
		private final List<T> object;
		private final BDDSoftAssertions assertion;

		private WithoutAnyArgumentContext(final List<T> object, final BDDSoftAssertions assertion) {
			this.object = object;
			this.assertion = assertion;
		}

		public <W> OperationResultCollector<W> performActionResult(final Function<T, Result<W>> fun2) {
			return

			new OperationResultCollector<>(object.map(r ->

			new BDDSoftAssertionResult<>(assertion, fun2.apply(r))));

		}

	}

	public static final class SingleArgumentContext<T, A> {
		private final List<T> object;
		private final BDDSoftAssertions assertion;

		private final A a;
		private final List<SingleArgumentContext<T, A>> givenStatesList;

		private SingleArgumentContext(final List<T> object, final BDDSoftAssertions assertion, final A a) {
			this.object = object;
			this.assertion = assertion;
			this.a = a;
			this.givenStatesList = List.<SingleArgumentContext<T, A>>emptyList();

		}

		private SingleArgumentContext(final List<T> object, final BDDSoftAssertions assertion, final A a,
				final List<SingleArgumentContext<T, A>> givenStatesList) {
			this.object = object;
			this.assertion = assertion;
			this.a = a;
			this.givenStatesList = givenStatesList;
		}

		public SingleArgumentContext<T, A> givenSingleArgument(final A a) {
			return new SingleArgumentContext<>(object, assertion, a, givenStatesList.cons(this));
		}

		public <W> OperationResultCollector<W> performActionResult(final Function<A, Function<T, Result<W>>> fun2) {
			final List<AssertionResultHandler<W>> combineNestedLists =

					object.combineNestedLists(e -> givenStatesList.cons(this).reverse().<AssertionResultHandler<W>>map(

							given1 -> new BDDSoftAssertionResult<>(assertion, fun2.apply(given1.a).apply(e)))

					);
			return new OperationResultCollector<>(combineNestedLists);
		}

	}

	public static class TwoArgumentsContext<T, A, B> {
		private final List<T> object;
		private final BDDSoftAssertions assertion;

		private final A a;
		private final B b;
		private final List<TwoArgumentsContext<T, A, B>> givenStatesList;

		private TwoArgumentsContext(final List<T> object, final BDDSoftAssertions assertion, final A a, final B b) {
			this.object = object;
			this.assertion = assertion;
			this.a = a;
			this.b = b;
			this.givenStatesList = List.<TwoArgumentsContext<T, A, B>>emptyList();

		}

		private TwoArgumentsContext(final List<T> object, final BDDSoftAssertions assertion, final A a, final B b,
				final List<TwoArgumentsContext<T, A, B>> givenStatesList) {
			this.object = object;
			this.assertion = assertion;
			this.a = a;
			this.b = b;
			this.givenStatesList = givenStatesList;
		}

		public TwoArgumentsContext<T, A, B> givenTwoArguments(final A a, final B b) {
			return new TwoArgumentsContext<>(object, assertion, a, b, givenStatesList.cons(this));
		}

		public <W> OperationResultCollector<W> performActionResult(
				final Function<A, Function<B, Function<T, Result<W>>>> fun2) {
			final List<AssertionResultHandler<W>> combineNestedLists = object
					.combineNestedLists(e -> givenStatesList.cons(this).reverse().<AssertionResultHandler<W>>map(

							given2 ->

							new BDDSoftAssertionResult<>(assertion, fun2.apply(given2.a).apply(given2.b).apply(e)))

					);
			return new OperationResultCollector<>(combineNestedLists);
		}

	}

	public static class ThreeArgumentsContext<T, A, B, C> {
		private final List<T> object;
		private final A a;
		private final B b;
		private final C c;
		private final BDDSoftAssertions assertion;

		private final List<ThreeArgumentsContext<T, A, B, C>> givenStatesList;

		private ThreeArgumentsContext(final List<T> object, final BDDSoftAssertions assertion, final A a, final B b,
				final C c) {
			this.object = object;
			this.assertion = assertion;

			this.a = a;
			this.b = b;
			this.c = c;
			this.givenStatesList = List.<ThreeArgumentsContext<T, A, B, C>>emptyList();

		}

		private ThreeArgumentsContext(final List<T> object, final BDDSoftAssertions assertion, final A a, final B b,
				final C c, final List<ThreeArgumentsContext<T, A, B, C>> givenStatesList) {
			this.object = object;
			this.assertion = assertion;

			this.a = a;
			this.b = b;
			this.c = c;
			this.givenStatesList = givenStatesList;
		}

		public ThreeArgumentsContext<T, A, B, C> givenThreeArguments(final A a, final B b, final C c) {
			return new ThreeArgumentsContext<>(object, assertion, a, b, c, givenStatesList.cons(this));
		}

		public <W> OperationResultCollector<W> performActionResult(
				final Function<A, Function<B, Function<C, Function<T, Result<W>>>>> fun2) {
			final List<AssertionResultHandler<W>> combineNestedLists = object
					.combineNestedLists(e -> givenStatesList.cons(this).reverse().<AssertionResultHandler<W>>map(

							given3 ->

							new BDDSoftAssertionResult<>(assertion,
									fun2.apply(given3.a).apply(given3.b).apply(given3.c).apply(e)))

					);

			return new OperationResultCollector<>(combineNestedLists);
		}
	}

	public interface AssertionResultHandler<W> {

		AssertionResultHandler<W> thenShouldHaveSameErrorMessage(String forMethodName);

		AssertionResultHandler<W> thenShouldBeEqualTo(Object object);

		// Use ObjectAssert<W> consistently
		<E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForObject(
				final Function<BDDSoftAssertions, Function<W, ObjectAssert<E>>> assertionFunction);

		// Use ObjectAssert<W> consistently
		<E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForList(
				Function<ListAssert<E>, ListAssert<E>> assertionFunction);

	}

	public static class OperationResultCollector<W> implements AssertionResultHandler<W> {

		private final List<AssertionResultHandler<W>> assertionResults;

		public OperationResultCollector(final List<AssertionResultHandler<W>> assertionResults) {

			this.assertionResults = assertionResults;

		}

		@Override
		public OperationResultCollector<W> thenShouldHaveSameErrorMessage(final String forMethodName) {

			assertionResults.firstElementOption().successValue().thenShouldHaveSameErrorMessage(forMethodName);
			return new OperationResultCollector<>(assertionResults.restElements());

		}

		@Override
		public AssertionResultHandler<W> thenShouldBeEqualTo(final Object object) {
			assertionResults.firstElementOption().successValue().thenShouldBeEqualTo(object);
			return new OperationResultCollector<>(assertionResults.restElements());
		}

		@Override
		public <E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForObject(
				final Function<BDDSoftAssertions, Function<W, ObjectAssert<E>>> assertionFunction) {
			assertionResults.firstElementOption().successValue()
					.thenShouldDoAllTheRequirementsHereFitForObject(assertionFunction);
			return new OperationResultCollector<>(assertionResults.restElements());
		}

		@Override
		public <E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForList(
				final Function<ListAssert<E>, ListAssert<E>> assertionFunction) {
			assertionResults.firstElementOption().successValue()
					.thenShouldDoAllTheRequirementsHereFitForList(assertionFunction);
			return new OperationResultCollector<>(assertionResults.restElements());
		}

	}

	public static class BDDSoftAssertionResult<W> implements AssertionResultHandler<W> {
		final BDDSoftAssertions assertion;
		final Result<W> result;

		public BDDSoftAssertionResult(final BDDSoftAssertions softy, final Result<W> result) {
			this.assertion = softy;
			this.result = result;
		}

		@Override
		public BDDSoftAssertionResult<W> thenShouldHaveSameErrorMessage(final String expectedErrorMessage) {
			assertion.then(result.failureValue().getMessage()).isEqualTo(expectedErrorMessage);
			return this;

		}

		@Override
		public AssertionResultHandler<W> thenShouldBeEqualTo(final Object object) {

			assertion.then(result.successValue())
					.as("The value should be equal to the object which is not the case here.").isEqualTo(object);
			return this;
		}

		@Override
		public <E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForObject(
				final Function<BDDSoftAssertions, Function<W, ObjectAssert<E>>> assertionFunction) {
			try {
				assertionFunction.apply(assertion).apply(result.successValue());
			} catch (final RuntimeException e) {
				throw new RuntimeException("Assertion failed: " + e.getMessage(), e);
			} catch (final Throwable t) {
				throw new RuntimeException("Unexpected error occurred during assertion: " + t.getMessage(), t);
			}

			return this;
		}

		// Handling ListAssert<W> where W can be a List<T>
		@SuppressWarnings("unchecked")
		@Override
		public <E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForList(
				final Function<ListAssert<E>, ListAssert<E>> assertionFunction) {
			try {
				// Ensure result.successValue() is a ListAssert<W>
				final Object value = result.successValue();

				ListAssert<E> listAssert;

				// If value is a List<T>, we need to convert it into ListAssert<W>
				if (!(value instanceof java.util.List))
					throw new IllegalStateException("The result is neither a List nor a ListAssert.");
				listAssert = assertion.then((java.util.List<E>) value);
				assertionFunction.apply(listAssert);

			} catch (final RuntimeException e) {
				throw new RuntimeException("Assertion failed: " + e.getMessage(), e);
			} catch (final Throwable t) {
				throw new RuntimeException("Unexpected error occurred during assertion: " + t.getMessage(), t);
			}

			return this;
		}

	}

}
