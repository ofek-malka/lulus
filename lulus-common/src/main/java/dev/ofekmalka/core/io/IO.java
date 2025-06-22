package dev.ofekmalka.core.io;

import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.Nothing;

/**
 * The {@code IO} class encapsulates side-effecting operations (such as printing
 * to the console) in a functional programming style. The key idea is that side
 * effects, instead of being executed directly, are represented as values that
 * can be executed (or "run") later in a controlled manner.
 *
 * <p>
 * This class allows you to compose side-effecting operations (like printing or
 * reading from a file) while keeping the core computation logic pure and free
 * of side effects. The `IO` class provides a way to represent a computation
 * that can be run to produce an effect, encapsulating the logic for producing
 * side effects in a domain-specific language (DSL).
 * </p>
 *
 * <p>
 * When you construct an {@code IO} value, you're effectively constructing a
 * program that, when run, will execute the desired side effect. For example,
 * {@code IO.printlnToConsole("Hello World")} represents a program that, when
 * run, will print "Hello World" to the console.
 * </p>
 *
 * <p>
 * While this class is functional in nature, the resulting program that gets
 * executed (by calling the {@link #run()} method) will ultimately be a
 * side-effecting operation, but you gain the benefit of composing pure
 * functions that are separate from the side effects themselves.
 * </p>
 *
 * <p>
 * This class is useful for functional programming in Java where side effects
 * (e.g., I/O operations) are handled in a structured and controlled manner,
 * ensuring that the functional purity of the core logic is maintained.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * IO.printlnToConsole("Hello World").run();
 * </pre>
 *
 * This will print "Hello World" to the console, but the side effect is only
 * executed when the {@code run()} method is called.
 *
 * <p>
 * Note that while the IO class itself encapsulates side effects, computations
 * should still be done using pure functional programming principles, keeping
 * side-effecting operations isolated and explicit.
 * </p>
 *
 * @param <A> the type of the result that the IO computation produces (or
 *            nothing if there is no result)
 */

//It's certainly
//better to use the IO type only for input and output,
//doing all the computations in
//functional programming.
public final class IO<A> {

	private final Supplier<Nothing> s;

	private IO(final Supplier<Nothing> s) {
		this.s = s;
	}

	public static IO<Nothing> empty = new IO<>(() -> Nothing.INSTANCE);

	public static <A> IO<Nothing> printlnToConsole(final A value) {

		return new IO<>(() -> {
			System.out.println(value);
			return Nothing.INSTANCE;
		});

	}

	public static <A> IO<Nothing> printToConsole(final A value) {
		return new IO<>(() -> {
			System.out.print(value);
			return Nothing.INSTANCE;
		});

	}

	public Nothing run() {
		return s.get();
	}

}
