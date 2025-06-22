package dev.ofekmalka.tools.test;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.SoftCondition;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;

public final class TestScenarioBuilder<T> {

	private final List<T> object; // Can be any object

	private TestScenarioBuilder(final List<T> object) {
		this.object = object;
	}

	public static class TypeAssertions {

		public <T> TestScenarioBuilder<T> initializeCase(final T a) {

			return new TestScenarioBuilder<>(List.list(a));
		}

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b) {

			return new TestScenarioBuilder<>(List.list(a, b));
		}

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b, final T c) {
			return new TestScenarioBuilder<>(List.list(a, b, c));
		}

		//////////////////////////////////////////////////////////////////////////////////
		// You should use these operations bellow only if you provide
		////////////////////////////////////////////////////////////////////////////////// withoutAnyArgument()
		////////////////////////////////////////////////////////////////////////////////// or
		// one the "given" state
		/////////////////////////////////////////////////////////////////////////////////////

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b, final T c, final T d) {
			return new TestScenarioBuilder<>(List.list(a, b, c, d));
		}

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b, final T c, final T d, final T e) {
			return new TestScenarioBuilder<>(List.list(a, b, c, d, e));
		}

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b, final T c, final T d, final T e,
				final T f) {
			return new TestScenarioBuilder<>(List.list(a, b, c, d, e, f));
		}

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b, final T c, final T d, final T e, final T f,
				final T g) {
			return new TestScenarioBuilder<>(List.list(a, b, c, d, e, f, g));
		}

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b, final T c, final T d, final T e, final T f,
				final T g, final T h) {
			return new TestScenarioBuilder<>(List.list(a, b, c, d, e, f, g, h));
		}

		public <T> TestScenarioBuilder<T> initializeCases(final T a, final T b, final T c, final T d, final T e, final T f,
				final T g, final T h, final T i) {
			return new TestScenarioBuilder<>(List.list(a, b, c, d, e, f, g, h, i));
		}

		// and so on

	}

	// Class representing the "given" state with a method to provide the list

	public <A> WithoutAnyArgumentContext<T> withoutAnyArgument() {
		return new WithoutAnyArgumentContext<>(object);
	}

	public <A> SingleArgumentContext<T, A> givenSingleArgument(final A a) {
		return new SingleArgumentContext<>(object, a);
	}

	public <A> SingleArgumentContext<T, A> givenNullArgument() {
		return new SingleArgumentContext<>(object, (A) null);
	}

	public <A, B> TwoArgumentsContext<T, A, B> givenTwoArguments(final A a, final B b) {
		return new TwoArgumentsContext<>(object, a, b);
	}

	public <A, B, C> ThreeArgumentsContext<T, A, B, C> givenThreeArguments(final A a, final B b, final C c) {
		return new ThreeArgumentsContext<>(object, a, b, c);
	}

	public static final class WithoutAnyArgumentContext<T> {
		private final List<T> object;

		private WithoutAnyArgumentContext(final List<T> object) {
			this.object = object;
		}

		public <W> OperationResultCollector<W> performActionResult(final Function<T, Result<W>> fun2) {
			return

			new OperationResultCollector<>(object.map(fun2).map(BDDSoftAssertionResult::new));

		}

	}

	public static final class SingleArgumentContext<T, A> {
		private final List<T> object;

		private final A a;
		private final List<SingleArgumentContext<T, A>> givenStatesList;

		private SingleArgumentContext(final List<T> object, final A a) {
			this.object = object;
			this.a = a;
			this.givenStatesList = List.<SingleArgumentContext<T, A>>emptyList();

		}

		private SingleArgumentContext(final List<T> object, final A a,
				final List<SingleArgumentContext<T, A>> givenStatesList) {
			this.object = object;
			this.a = a;
			this.givenStatesList = givenStatesList;
		}

		public SingleArgumentContext<T, A> givenSingleArgument(final A a) {
			return new SingleArgumentContext<>(object, a, givenStatesList.cons(this));
		}

		public <W> OperationResultCollector<W> performActionResult(final Function<A, Function<T, Result<W>>> fun2) {
			final List<AssertionResultHandler<W>> combineNestedLists =

					object.combineNestedLists(e -> givenStatesList.cons(this).reverse().<AssertionResultHandler<W>>map(

							given1 -> new BDDSoftAssertionResult<>(fun2.apply(given1.a).apply(e)))

					);
			return new OperationResultCollector<>(combineNestedLists);
		}

	}

	public static class TwoArgumentsContext<T, A, B> {
		private final List<T> object;

		private final A a;
		private final B b;
		private final List<TwoArgumentsContext<T, A, B>> givenStatesList;

		private TwoArgumentsContext(final List<T> object, final A a, final B b) {
			this.object = object;
			this.a = a;
			this.b = b;
			this.givenStatesList = List.<TwoArgumentsContext<T, A, B>>emptyList();

		}

		private TwoArgumentsContext(final List<T> object, final A a, final B b,
				final List<TwoArgumentsContext<T, A, B>> givenStatesList) {
			this.object = object;
			this.a = a;
			this.b = b;
			this.givenStatesList = givenStatesList;
		}

		public TwoArgumentsContext<T, A, B> givenTwoArguments(final A a, final B b) {
			return new TwoArgumentsContext<>(object, a, b, givenStatesList.cons(this));
		}

		public <W> OperationResultCollector<W> performActionResult(
				final Function<A, Function<B, Function<T, Result<W>>>> fun2) {
			final List<AssertionResultHandler<W>> combineNestedLists = object
					.combineNestedLists(e -> givenStatesList.cons(this).reverse().<AssertionResultHandler<W>>map(

							given2 ->

							new BDDSoftAssertionResult<>(fun2.apply(given2.a).apply(given2.b).apply(e)))

					);
			return new OperationResultCollector<>(combineNestedLists);
		}

	}

	public static class ThreeArgumentsContext<T, A, B, C> {
		private final List<T> object;
		private final A a;
		private final B b;
		private final C c;

		private final List<ThreeArgumentsContext<T, A, B, C>> givenStatesList;

		private ThreeArgumentsContext(final List<T> object, final A a, final B b, final C c) {
			this.object = object;

			this.a = a;
			this.b = b;
			this.c = c;
			this.givenStatesList = List.<ThreeArgumentsContext<T, A, B, C>>emptyList();

		}

		private ThreeArgumentsContext(final List<T> object, final A a, final B b, final C c,
				final List<ThreeArgumentsContext<T, A, B, C>> givenStatesList) {
			this.object = object;

			this.a = a;
			this.b = b;
			this.c = c;
			this.givenStatesList = givenStatesList;
		}

		public ThreeArgumentsContext<T, A, B, C> givenThreeArguments(final A a, final B b, final C c) {
			return new ThreeArgumentsContext<>(object, a, b, c, givenStatesList.cons(this));
		}

		public <W> OperationResultCollector<W> performActionResult(
				final Function<A, Function<B, Function<C, Function<T, Result<W>>>>> fun2) {
			final List<AssertionResultHandler<W>> combineNestedLists = object
					.combineNestedLists(e -> givenStatesList.cons(this).reverse().<AssertionResultHandler<W>>map(

							given3 ->

							new BDDSoftAssertionResult<>(fun2.apply(given3.a).apply(given3.b).apply(given3.c).apply(e)))

					);

			return new OperationResultCollector<>(combineNestedLists);
		}
	}

	public interface AssertionResultHandler<W> {

		AssertionResultHandler<W> thenShouldHaveSameErrorMessage(String forMethodName);

		AssertionResultHandler<W> thenShouldBeEqualTo(Object object);

		<E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForObject(final

		Function<If.SoftCondition<W>, If.SoftCondition<W>> assertionFunction);

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
				final Function<SoftCondition<W>, SoftCondition<W>> assertionFunction) {
			assertionResults.firstElementOption().successValue()
					.thenShouldDoAllTheRequirementsHereFitForObject(assertionFunction);
			return new OperationResultCollector<>(assertionResults.restElements());
		}

	}

	public static class BDDSoftAssertionResult<W> implements AssertionResultHandler<W> {
		final Result<W> result;

		public BDDSoftAssertionResult(final Result<W> result) {
			this.result = result;
		}

		@Override
		public BDDSoftAssertionResult<W> thenShouldHaveSameErrorMessage(final String expectedErrorMessage) {

			final var actualErrorMessage = result.failureValue().getMessage();
			if (!actualErrorMessage.equals(expectedErrorMessage)) {//
				Result.failure(//
						String.format(//
								"Actual error message :\n%s\nBut expected error message is :\n%s", //
								actualErrorMessage, expectedErrorMessage//
						)//
				)//
						.printlnToConsole()//
						.run();//
			}

			return this;

		}

		@Override
		public AssertionResultHandler<W> thenShouldBeEqualTo(final Object object) {

			final var actualValue = result.successValue();
			if (!actualValue.equals(object)) {//
				Result.failure(//
						String.format(//
								"Actual value :\n%s\nBut expected value is :\n%s", //
								actualValue, object//
						)//
				)//
						.printlnToConsole()//
						.run();//
			}

			return this;

		}

		@Override
		public <E> AssertionResultHandler<W> thenShouldDoAllTheRequirementsHereFitForObject(final

		Function<If.SoftCondition<W>, If.SoftCondition<W>> assertionFunction) {

			final var actualErrorMessage = assertionFunction
					.apply(If.givenNonNullForSoftValidation(result).is(Result::isSuccess).mapTo(Result::successValue))

					.will().thenGetOrErrorMessage()

			;

			if (actualErrorMessage.isFailure()) {
				actualErrorMessage.printlnToConsole().run();//
			}

			return this;
		}

	}

}