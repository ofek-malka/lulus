package dev.ofekmalka.core.function;

import dev.ofekmalka.core.io.IO;
import dev.ofekmalka.tools.helper.Nothing;

public interface ConsoleOutputEffect {
	default IO<Nothing> printlnToConsole() {
		return IO.printlnToConsole(this);
	}

	default IO<Nothing> printToConsole() {
		return IO.printToConsole(this);
	}

}
