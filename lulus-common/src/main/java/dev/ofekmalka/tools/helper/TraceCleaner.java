package dev.ofekmalka.tools.helper;

import java.util.regex.Pattern;

public final class TraceCleaner {

	// Precompiled regex pattern — only compiled once when the class is loaded
	private static final Pattern TRACE_PREFIX_PATTERN = Pattern.compile("(?m)^Called by the method name '.*?' ->\\s*");

	private TraceCleaner() {
		// Private constructor to prevent instantiation
	}

	public static String removeCalledByMethodPrefixes(final String message) {
		return TRACE_PREFIX_PATTERN.matcher(message).replaceAll("");
	}
}
