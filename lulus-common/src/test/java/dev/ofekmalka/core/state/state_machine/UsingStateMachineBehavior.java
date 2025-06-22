package dev.ofekmalka.core.state.state_machine;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.state.data.Atm;
import dev.ofekmalka.core.state.data.Input;
import dev.ofekmalka.core.state.data.Outcome;
import dev.ofekmalka.core.state.data.Withdraw;
import dev.ofekmalka.support.annotation.CustomDisplayNameGenerator;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SoftAssertionsExtension.class)
public class UsingStateMachineBehavior {

	@Test
	void demonstrateBankWithdrawRequests() {

		final List<Input> operations = List.list(Withdraw.amount(10), Withdraw.amount(100), Withdraw.amount(100));
		final var outcome = Atm.createMachine()//
				.processActionList(operations)//
				.eval(Outcome.account(10));

		assertThat(outcome.successValue().toString()).isEqualTo("""
				(0,\
				[Success(-10),\
				Failure(java.lang.IllegalStateException: Insufficient balance),\
				Failure(java.lang.IllegalStateException: Insufficient balance),NIL\
				]\
				)""");

	}
}