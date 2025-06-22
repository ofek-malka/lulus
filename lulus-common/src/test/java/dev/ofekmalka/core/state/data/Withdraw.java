package dev.ofekmalka.core.state.data;

import dev.ofekmalka.core.assertion.result.Result;

public class Withdraw implements Input {

	private final Result<Integer> amount;

	private Withdraw(final Result<Integer> amount) {
		this.amount = amount;
	}

	private Withdraw(final Integer amount) {
		this.amount = Result.success(amount);
	}

	public static Withdraw amount(final Integer amount) {
		return new Withdraw(amount);
	}

	@Override
	public Type type() {
		return Type.WITHDRAW;
	}

	@Override
	public boolean isDeposit() {
		return false;
	}

	@Override
	public boolean isWithdraw() {
		return true;
	}

	@Override
	public Result<Integer> getAmount() {
		return amount;
	}
}
