package dev.ofekmalka.core.state.state_machine;

import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface Condition<S, I> extends Function<Tuple2<S, I>, Boolean> {
}
