package dev.ofekmalka.core.state;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface StateBehavior<S, A> {

	interface Operations<S, A> //

	{

		interface Basic<S, A> extends Operations<S, A> {
			<B> State<S, B> map(Function<A, B> mapper);

			<B> State<S, B> flatMap(Function<A, State<S, B>> binder);

			Result<A> eval(S initialState);

			Result<Tuple2<S, A>> runWith(S initialState);
		}

	}
}
