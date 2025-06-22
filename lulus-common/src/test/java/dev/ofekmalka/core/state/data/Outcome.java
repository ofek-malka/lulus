package dev.ofekmalka.core.state.data;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.tools.tuple.Tuple2;

/**
 * This class represent the result that is returned by the value() machine. It
 * is not mandatory to use such a class, but it is much clearer than using
 * tuples.
 */
public final class Outcome {

	private final Integer account;
	private final List<Result<Integer>> operations;

	private Outcome(final Integer account, final List<Result<Integer>> operations) {
		this.account = account;
		this.operations = operations;
	}

	public static Outcome account(final Integer account) {
		return new Outcome(account, List.emptyList());
	}

	public static Outcome of(final Integer account, final List<Result<Integer>> operations) {
		return new Outcome(account, operations);
	}

	public static Outcome add(final Tuple2<Outcome, Input> t) {
		final var newAccount = t.value().getAmount().map(a -> t.state().account + a).getOrElse(t.state().account);

		final var newOperations = t.state().operations.cons(t.value().getAmount());
		return Outcome.of(newAccount, newOperations);
	}

	public static Outcome sub(final Tuple2<Outcome, Input> t) {
		final var newAccount = t.value().getAmount().map(a -> t.state().account - a).getOrElse(t.state().account);

		final var newOperations = t.state().operations.cons(t.value().getAmount().map(a -> -a));

		return Outcome.of(newAccount, newOperations);

	}

	public static Outcome err(final Tuple2<Outcome, Input> t) {

		final var newAccount = t.state().account;

		final var newOperations = t.state().operations.cons(Result.failure("Insufficient balance"));

		return Outcome.of(newAccount, newOperations);
	}

	public Integer getAccount() {
		return account;
	}

	public List<Result<Integer>> getOperations() {
		return operations.reverse();
	}

	@Override
	public String toString() {
		return "(" + account.toString() + "," + getOperations().toString() + ")";
	}

}
