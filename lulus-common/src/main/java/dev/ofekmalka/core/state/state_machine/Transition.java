package dev.ofekmalka.core.state.state_machine;

import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface Transition<S, A> extends Function<Tuple2<S, A>, S> {

}
