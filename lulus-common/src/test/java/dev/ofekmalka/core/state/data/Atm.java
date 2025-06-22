package dev.ofekmalka.core.state.data;

import dev.ofekmalka.core.state.state_machine.Condition;
import dev.ofekmalka.core.state.state_machine.StateMachine;
import dev.ofekmalka.core.state.state_machine.Transition;
import dev.ofekmalka.tools.tuple.Tuple2;

public class Atm {

//	The createMachine implementation must first construct a list of tuples of conditions
//	 and corresponding transitions. These tuples will have to be ordered, with the more
//	 specific coming first. The last tuple will need a catch-all condition. This is like the
//	 default case in a switch structure .
//	 This catch-all condition isn’t always needed, but it’s safer to always have one. The list of
//	 tuples will be used as the argument to the StateMachine constructor.
//	 You’ll have to run the resulting state machine to get an observable result.

	public static StateMachine<Outcome, Input> createMachine() {

		final Condition<Outcome, Input> predicate1 = t -> t.value().isDeposit();
		final Transition<Outcome, Input> transition1 = Outcome::add;

		final Condition<Outcome, Input> predicate2 = t -> t.value().isWithdraw()
				&& t.value().getAmount().map(w -> w <= t.state().getAccount()).getOrElse(false);
		final Transition<Outcome, Input> transition2 = Outcome::sub;

		/*
		 * Without the commented checking, this condition must come after the previous
		 * one in the list. With the commented checking, the order is not significant
		 */
		final Condition<Outcome, Input> predicate3 = t -> t.value().isWithdraw();
		final Transition<Outcome, Input> transition3 = Outcome::err;

		final Condition<Outcome, Input> catchAllCondition = t -> true;
		final Transition<Outcome, Input> catchAllTransition = Tuple2::state;

		return StateMachine//
				.addRule(predicate1, transition1)//
				.addRule(predicate2, transition2)//
				.addRule(predicate3, transition3)//
				.addRule(catchAllCondition, catchAllTransition)//
				.createMachine()

		;
	}

}
