package dev.ofekmalka.core.function;

import dev.ofekmalka.core.assertion.result.Result;

public interface ResultProvider<T> {
	Result<T> asResult();
}