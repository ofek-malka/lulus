package dev.ofekmalka.core.state.state_machine;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.state.State;
import dev.ofekmalka.core.state.State.StateFactory;
import dev.ofekmalka.tools.helper.Nothing;
import dev.ofekmalka.tools.tuple.Tuple2;

/**
 * One of the most common tools for composing state mutations is the state
 * machine. A state machine is a piece of code that processes inputs by
 * conditionally switching from one state to another. Many business problems can
 * be represented by such conditional state mutations. By creating a
 * parameterized state machine, you can abstract all the details about state
 * handling. That way, you’ll be able to handle any such problem by simply
 * listing the condition/transition pairs, and then feeding in the list of
 * inputs to get the resulting state. The machine will handle the composition of
 * the various transitions transparently.
 *
 * <p>
 * Represents a functional state machine for processing state transitions.
 *
 * <p>
 * A StateMachine models the core idea of conditional state transitions, where
 * each transition is defined by a condition and a transformation function. The
 * user defines a list of (Condition, Transition) pairs, and then feeds the
 * machine a sequence of actions. The machine processes these actions in order,
 * applying only the first transition whose condition matches.
 *
 * <p>
 * Internally, this class is built entirely using functional tools (e.g.
 * {@link List}, {@link Tuple2}, {@link Function}, {@link State}). It composes
 * logic in a purely functional way with zero mutation and no side effects.
 *
 * <p>
 * Example:
 *
 * <pre>
 * {
 * 	&#64;code
 * 	enum Action {
 * 		OPEN, CLOSE
 * 	}
 *
 * 	var machine = StateMachine
 * 			.addRule((state, action) -> state.equals("closed") && action == Action.OPEN, (state, action) -> "open")
 * 			.addRule((state, action) -> state.equals("open") && action == Action.CLOSE, (state, action) -> "closed")
 * 			.createMachine();
 *
 * 	var result = machine.addAction(Action.OPEN).addAction(Action.CLOSE).process().eval("closed").successValue(); // =>
 * 																													// "closed"
 * }
 * </pre>
 *
 * <p>
 * <b>⚠ WARNING – NOT A SAFE CLASS</b>
 * </p>
 * <ul>
 * <li>This class assumes that <b>all values</b> passed to it (conditions,
 * transitions, inputs, state) are <b>valid</b>.</li>
 * <li>It does <b>not</b> validate for nulls, duplicate transitions, unreachable
 * states, or invalid inputs.</li>
 * <li>If conditions are badly written (e.g. overlapping or none-matching),
 * results may be <b>unpredictable</b>.</li>
 * <li>Any thrown exceptions inside the user-provided functions will propagate
 * and potentially fail silently inside State.</li>
 * </ul>
 *
 * <p>
 * If you want safety (e.g. guaranteed matching transitions, validated inputs),
 * it is your responsibility to build wrappers or additional validation layers
 * around this machine.
 *
 * <p>
 * <b>Use this class only if:</b>
 * <ul>
 * <li>You fully control the transition logic.</li>
 * <li>You are sure no nulls or exceptions can occur.</li>
 * <li>You understand that invalid logic leads to unexpected runtime
 * behavior.</li>
 * </ul>
 *
 * <p>
 * This class is primarily designed for internal business rule processing in
 * FP-style domain code where all inputs and behavior are guaranteed ahead of
 * time.
 */

public final class StateMachine<CurrentState, Action> {

	private final Function<Action, State<CurrentState, Nothing>> function;
	private final List<Tuple2<Condition<CurrentState, Action>, Transition<CurrentState, Action>>>//
	conditionTransitionPairs;

	private StateMachine(//
			final List<Tuple2<Condition<CurrentState, Action>, Transition<CurrentState, Action>>>//
			conditionTransitionPairs) {//

		this.conditionTransitionPairs = conditionTransitionPairs;
		function = action -> //
		StateFactory.sequence(currentState -> {//
			final var actionTuple = Tuple2.of(currentState, action).getResult().successValue();
			return conditionTransitionPairs.filter(transitionPair -> transitionPair.state().apply(actionTuple))//
					.firstElementOption()//
					.map(transitionPair -> transitionPair.value().apply(actionTuple))//
					.getOrElse(currentState);

		});

	}

	public static <CurrentState, Action> Builder<CurrentState, Action> addRule(final Condition<CurrentState, Action> c, //
			final Transition<CurrentState, Action> t) {
		return Builder.<CurrentState, Action>build().addRule(c, t);

	}

	public static class Builder<CurrentState, Action> {

		final List<Tuple2<Condition<CurrentState, Action>, Transition<CurrentState, Action>>>//
		conditionTransitionPairs;

		private Builder(final List<Tuple2<Condition<CurrentState, Action>, Transition<CurrentState, Action>>>//
		conditionTransitionPairs) {
			this.conditionTransitionPairs = conditionTransitionPairs;
		}

		static <CurrentState, Action> Builder<CurrentState, Action> build() {
			return new Builder<>(List.emptyList());

		}

		public Builder<CurrentState, Action> addRule(final Condition<CurrentState, Action> c,
				final Transition<CurrentState, Action> t) {
			final var actionTuple = Tuple2.of(c, t).getResult().successValue();
			return new Builder<>(conditionTransitionPairs.addElement(actionTuple));
		}

		public StateMachine<CurrentState, Action> createMachine() {
			return new StateMachine<>(conditionTransitionPairs);
		}

	}

	public static class ActionSequence<CurrentState, Action> {
		private final StateMachine<CurrentState, Action> createMachine;
		private final List<Action> actions;

		private ActionSequence(final StateMachine<CurrentState, Action> createMachine, final List<Action> actions) {
			this.createMachine = createMachine;
			this.actions = actions;

		}

		public ActionSequence<CurrentState, Action> addAction(final Action action) {
			return new ActionSequence<>(createMachine, actions.addElement(action));

		}

		public StateProcessingResult<CurrentState> process() {
			return createMachine.processActionList(actions);
		}

	}

	public StateProcessingResult<CurrentState> processActionList(final List<Action> inputs) {

		final var state = StateFactory.sequence(inputs.map(function)).flatMap(x -> StateFactory.get());
		return new StateProcessingResult<>(state);
	}

	@SuppressWarnings("unchecked")
	public StateProcessingResult<CurrentState> processActionArray(final Action... inputs) {
		final var state = StateFactory.sequence(List.list(inputs).map(function)).flatMap(x -> StateFactory.get());

		return new StateProcessingResult<>(state);

	}

	public ActionSequence<CurrentState, Action> addAction(final Action action) {
		return new ActionSequence<>(this, List.<Action>emptyList().addElement(action));
	}

			List<Tuple2<Condition<CurrentState, Action>, Transition<CurrentState, Action>>>//
			getConditionTransitionPairs() {
		return conditionTransitionPairs;
	}

	public static class StateProcessingResult<CurrentState> {

		private final State<CurrentState, CurrentState> state;

		private StateProcessingResult(final State<CurrentState, CurrentState> result) {
			this.state = result;
		}

		public Result<CurrentState> eval(final CurrentState s) {
			return state.eval(s);
		}

	}

}