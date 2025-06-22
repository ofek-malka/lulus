package dev.ofekmalka.core.state.data;

import dev.ofekmalka.core.assertion.result.Result;

public interface Input {

	Type type();

	boolean isDeposit();

	boolean isWithdraw();

	Result<Integer> getAmount();

	enum Type {
		DEPOSIT, WITHDRAW
	}
}
