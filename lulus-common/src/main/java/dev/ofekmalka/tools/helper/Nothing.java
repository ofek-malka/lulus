package dev.ofekmalka.tools.helper;

/**
 * Represents an absence of a meaningful return value. </br>
 * Typically used in contexts where only the modified state is of interest See
 * {@link Effect}.</br>
 * Similar to `Void`, but avoids instantiation issues. </br>
 * Use `Nothing.INSTANCE` to represent a "no value" state.</br>
 */
public enum Nothing {
	INSTANCE;
}