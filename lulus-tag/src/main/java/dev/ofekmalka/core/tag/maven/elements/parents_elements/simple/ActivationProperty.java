package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record ActivationProperty(MavenParentElement parent) {

	interface AddValue {
		ActivationProperty addValue(final String propertyValue);
	}

	public static AddValue isSetTo(final String name) {
		return value -> {
			final var createParent = //
					MavenParentElement.name(Parent.ACTIVATION_PROPERTY)//
							.addChild(Leaf.NAME, name)//
							.addChild(Leaf.VALUE, value);
			return new ActivationProperty(createParent);
		};

	}

}
