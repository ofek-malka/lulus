package dev.ofekmalka.core.state.data;

import dev.ofekmalka.core.assertion.result.Result;

public class Deposit implements Input {

	private final Result<Integer> amount;

	private Deposit(final Result<Integer> amount) {
		this.amount = amount;
	}

	private Deposit(final Integer amount) {
		this.amount = Result.success(amount);
	}

	public static Deposit amount(final Integer amount) {
		return new Deposit(amount);
	}

	@Override
	public Type type() {
		return Type.DEPOSIT;
	}

	@Override
	public boolean isDeposit() {
		return true;
	}

	@Override
	public boolean isWithdraw() {
		return false;
	}

	@Override
	public Result<Integer> getAmount() {
		return amount;
	}
}
