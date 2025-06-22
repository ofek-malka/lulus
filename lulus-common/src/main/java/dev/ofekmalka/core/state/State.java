package dev.ofekmalka.core.state;

import static dev.ofekmalka.core.assertion.PreconditionedProcess.from;
import static dev.ofekmalka.core.assertion.PreconditionedProcess.fromSupplier;
import static dev.ofekmalka.core.assertion.PreconditionedProcess.startProcess;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.data_structure.list.List.Errors.CastumArgumentMessage;
import dev.ofekmalka.core.error.ErrorOptions;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.tools.helper.Nothing;
import dev.ofekmalka.tools.tuple.Tuple2;

/**
 * Represents a stateful computation. A State<S, A> receives a state of type S
 * and returns a result of type A along with a new state.
 *
 * This class helps manage state changes in a purely functional way.
 *
 * <p>
 * Example:
 *
 * <pre>
 * {@code
 *   State<Integer, String> example = StateFactory
 *       .modify(s -> s + 1)
 *       .flatMap(_ -> StateFactory.get())
 *       .map(newState -> "Counter is now: " + newState);
 *
 *   String result = example.eval(41).successValue(); // "Counter is now: 42"
 * }
 * </pre>
 *
 * <p>
 * <b>WARNING:</b> This class assumes that all provided functions:
 * <ul>
 * <li>Are non-null</li>
 * <li>Do not throw exceptions</li>
 * <li>Are side-effect-free and deterministic (if relevant)</li>
 * </ul>
 *
 * Improper usage may cause unexpected behavior at runtime.
 *
 *
 * See README or docs for a beginner-friendly explanation.
 */
public class State<S, A> implements StateBehavior.Operations.Basic<S, A> {

	private final Result<StateImp<S, A>> source;

	private State(final Result<StateImp<S, A>> source) {
		this.source = source;
	}

	private StateImp<S, A> getStateImpSource() {
		return source.successValue();
	}

	@Override
	public String toString() {
		return If.isItTrue(source.isFailure())//
				.will()//
				.returnValue(() -> "State is in a failed state and cannot generate a valid representation for toString."
						+ "\nPlease check the process status for more details.")//
				.orGet(() -> source.map(StateImp::toString).successValue());
	}

	public static class StateFactory {//

		public static <S> State<S, Nothing> set(final S state) {

			return startProcess()//
					.checkCondition(() -> If.givenObject(state).isNonNull("state"))//
					.processOperationBySupplierObject(() -> StateImp.<S>set(state))//
					.andMakeStackTraceUnderTheName("set")//
					.mapTo(State::new);//

		}

		public static <S, A> State<S, A> unit(final A value) {

			return startProcess()//

					.checkCondition(() -> If.givenObject(value).isNonNull("value"))//
					.processOperationBySupplierObject(() -> StateImp.<S, A>unit(value))//
					.andMakeStackTraceUnderTheName("unit")//
					.mapTo(State::new);//

		}

		/**
		 * Accepts a function to be applied in a functional context.
		 *
		 * <p>
		 * <b>Important:</b> The provided function must not be {@code null} and must not
		 * throw exceptions. Violating this will cause runtime failures and undefined
		 * behavior.
		 * </p>
		 */
		public static <S> State<S, Nothing> sequence(final Function<S, S> effect) {

			return startProcess()//
					.checkCondition(() -> //
					If.givenObject(effect).isNonNull("effect"))//
					.processOperationBySupplierObject(() -> StateImp.sequence(effect))//
					.andMakeStackTraceUnderTheName("sequence")//
					.mapTo(State::new);//

		}

		public static <S> State<S, S> get() {

			return fromSupplier(StateImp::<S>get)//
					.andMakeStackTraceUnderTheName("get")//
					.mapTo(State::new);//

		}

		/**
		 * Accepts a function to be applied in a functional context.
		 *
		 * <p>
		 * <b>Important:</b> The provided function must not be {@code null} and must not
		 * throw exceptions. Violating this will cause runtime failures and undefined
		 * behavior.
		 * </p>
		 */
		public static <S> State<S, Nothing> modify(final Function<S, S> stateTransformer) {

			return startProcess()//
					.checkCondition(() -> //
					If.givenObject(stateTransformer).isNonNull("stateTransformer"))//
					.processOperationBySupplierObject(() -> StateImp.modify(stateTransformer))//
					.andMakeStackTraceUnderTheName("modify")//
					.mapTo(State::new);//

		}

		public static <S, A> State<S, List<A>> sequence(final List<State<S, A>> stateList) {

			final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
					.withArgumentName("stateList").getMessage();

			return startProcess()//
					.checkCondition(() -> //
					If.givenObject(stateList).isNonNull("stateList")//
							.andIs(List::isProcessSuccess, errorProcessingMessage))//
					.processOperationBySupplierObject(() -> StateImp.sequence(stateList.map(State::getStateImpSource)))//
					.andMakeStackTraceUnderTheName("sequence")//
					.mapTo(State::new);//

		}

	}

	/**
	 * Accepts a function to be applied in a functional context.
	 *
	 * <p>
	 * <b>Important:</b> The provided function must not be {@code null} and must not
	 * throw exceptions. Violating this will cause runtime failures and undefined
	 * behavior.
	 * </p>
	 */
	@Override
	public <B> State<S, B> map(final Function<A, B> mapper) {
		return from(source)//
				.checkCondition(() -> If.givenObject(mapper).isNonNull("mapper")

				)//
				.processOperation(state -> state.map(mapper))//
				.andMakeStackTraceUnderTheName("map")//
				.mapTo(State::new);//

	}

	/**
	 * Accepts a function to be applied in a functional context.
	 *
	 * <p>
	 * <b>Important:</b> The provided function must not be {@code null} and must not
	 * throw exceptions. Violating this will cause runtime failures and undefined
	 * behavior.
	 * </p>
	 */
	@Override
	public <B> State<S, B> flatMap(final Function<A, State<S, B>> binder) {
		return from(source)//
				.checkCondition(() -> If.givenObject(binder).isNonNull("binder"))//
				.processOperation(state ->

				state.flatMap(binder.andThen(State::getStateImpSource)))//
				.andMakeStackTraceUnderTheName("flatMap")//
				.mapTo(State::new);//
	}

	static class Errors {

		public enum StateMessage implements ErrorOptions {

			ERROR_EVALUATION(
					"""
							An error occurred during the evaluation of the State monad. \
							Unlike Result, State is lazy (like a lazy list or Java Stream), so exceptions are deferred. \
							This means errors are usually the result of invalid function arguments passed earlier, \
							even though null checks were in place. Please inspect the function logic provided to map, flatMap, etc.""");

			private final String message;

			StateMessage(final String message) {
				this.message = message;
			}

			@Override
			public String getMessage() {
				return message;

			}

		}

	}

	@Override
	public Result<A> eval(final S initialState) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(initialState)//
						.isNonNull("initialState"))//
				.processOperationWithResult(state -> state.eval(initialState))//
				.andMakeStackTraceUnderTheName("eval")//
				.getResultProccess()//
				.mapToNested()//
				.getOrCreateFailureInstanceWithMessage(st -> //
				Result.failure(//
						st.replace(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage(), //
								Errors.StateMessage.ERROR_EVALUATION.getMessage()//
						).replace(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage(), //
								Errors.StateMessage.ERROR_EVALUATION.getMessage())));//

	}

	@Override
	public Result<Tuple2<S, A>> runWith(final S initialState) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(initialState)//
						.isNonNull("initialState"))//
				.processOperationWithResult(state -> state.runWith(initialState))//
				.andMakeStackTraceUnderTheName("eval")//
				.getResultProccess()//
				.mapToNested()//
				.getOrCreateFailureInstanceWithMessage(st -> //
				Result.failure(//
						st.replace(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage(), //
								Errors.StateMessage.ERROR_EVALUATION.getMessage()//
						).replace(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage(), //
								Errors.StateMessage.ERROR_EVALUATION.getMessage())));//

	}

	private static class StateImp<S, A> {

		final Function<S, Result<Tuple2<S, A>>> run;

		private StateImp(final Function<S, Result<Tuple2<S, A>>> run) {
			this.run = run;
		}

		private final Function<S, Result<Tuple2<S, A>>> getRun() {
			return run;
		}

		public static <S, A> StateImp<S, A> unit(final A a) {
			return new StateImp<>(s -> Tuple2.of(s, a).getResult());
		}

		public <B> StateImp<S, B> map(final Function<A, B> f) {
			return this.flatMapResult(a -> f.safeApplyOn(a).map(StateImp::unit));
		}

		public <B> StateImp<S, B> flatMap(final Function<A, StateImp<S, B>> f) {
			return this.flatMapResult(a -> f.safeApplyOn(a));
		}

		private <B> StateImp<S, B> flatMapResult(final Function<A, Result<StateImp<S, B>>> f) {

			return new StateImp<>(s -> {
				final var tuple = this.getRun().safeApplyOn(s).flatMap(x -> x);

				return tuple.flatMap(t -> f.safeApplyOn(t.value())

						.flatMap(d -> d).map(StateImp::getRun).flatMap(r -> r.safeApplyOn(t.state()))).flatMap(d -> d);

			});
		}

		public static <S, A> StateImp<S, List<A>> sequence(final List<StateImp<S, A>> fs) {
			final StateImp<S, List<A>> identity = StateImp.unit(List.<A>emptyList());//
			return fs.foldRight(identity, stateElement -> acc -> stateElement.map2(acc, //
					stateElementValue -> accValue -> accValue.cons(stateElementValue))).successValue()

			;//
		}

		private final <B, C> StateImp<S, C> map2(final StateImp<S, B> sb, final Function<A, Function<B, C>> f) {
			return this.flatMap(a -> sb.map(b ->

			f.apply(a).apply(b)
			// f.safeApplyOn(a).flatMap(fun -> fun
			// .safeApplyOn(b)).prependFailureMessage("")

			));
		}

		public static <S> StateImp<S, Nothing> sequence(final Function<S, S> f) {
			return sequence(f, Nothing.INSTANCE);
		}

		private static <S, A> StateImp<S, A> sequence(final Function<S, S> f, final A value) {
			return new StateImp<>(s -> f.safeApplyOn(s)

					.flatMap(r -> Tuple2.of(r, value).getResult()));
		}

		public final static <S> StateImp<S, S> get() {
			return new StateImp<>(s -> Tuple2.of(s, s).getResult());
		}

		public final static <S> StateImp<S, Nothing> set(final S s) {
			return new StateImp<>(x -> Tuple2.of(s, Nothing.INSTANCE).getResult());
		}

		public static <S> StateImp<S, Nothing> modify(final Function<S, S> f) {
			return StateImp.<S>get().flatMapResult(s ->

			f.safeApplyOn(s)

					.map(StateImp::set));
		}

		public Result<Tuple2<S, A>> runWith(final S s) {
			return this.getRun().safeApplyOn(s).flatMap(x -> x);

		}

		public Result<A> eval(final S s) {
			return this.getRun().safeApplyOn(s).flatMap(x -> x).map(Tuple2::value);

		}
	}

	public static void main(final String[] args) {

		final var incrementWithMessage = StateFactory.<Integer>modify(state -> state + 1) // increment the state
				// increment the state
				.flatMap(nothing -> null) // get the new state
				.map(newState -> null); // return a string
		final var result = incrementWithMessage.eval(41);
		System.out.println("e: " + result); // Should print: "New state is: 42"

	}// throw new RuntimeException("");

}
