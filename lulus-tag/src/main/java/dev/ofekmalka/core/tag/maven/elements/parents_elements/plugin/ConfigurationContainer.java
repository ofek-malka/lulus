package dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin;

import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Configuration;

public interface ConfigurationContainer<T> {
	T addConfiguration(Configuration configuration);

	T addInherited(boolean inherited);
}
