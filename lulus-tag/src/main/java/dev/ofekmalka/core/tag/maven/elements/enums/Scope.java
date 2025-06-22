package dev.ofekmalka.core.tag.maven.elements.enums;

public enum Scope {
	/**
	 * Compile only.
	 */
	COMPILE_ONLY("compile-only"),

	/**
	 * Compile, runtime and test.
	 */
	COMPILE("compile"),

	/**
	 * Runtime and test.
	 */
	RUNTIME("runtime"),

	/**
	 * Provided.
	 */
	PROVIDED("provided"),

	/**
	 * Test compile only.
	 */
	TEST_ONLY("test-only"),

	/**
	 * Test compile and test runtime.
	 */
	TEST("test"),

	/**
	 * Test runtime.
	 */
	TEST_RUNTIME("test-runtime"),

	/**
	 * System scope.
	 */
	SYSTEM("system");

	String scopeName;

	Scope(final String scopeName) {
		this.scopeName = scopeName;
	}

	public String getScopeName() {
		return scopeName;
	}

}
