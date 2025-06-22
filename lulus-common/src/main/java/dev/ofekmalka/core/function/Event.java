package dev.ofekmalka.core.function;

public interface Event<T, B> {
	Supplier<T> state();

	Supplier<B> value();

}