package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Property;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Configuration(MavenParentElement parent) {

	public static Configuration isSetTo(final Property property) {
		final var createParent =

				MavenParentElement.name(Parent.CONFIGURATION)

						.addChild(property.tagName(), property.propertyValue());
		return new Configuration(createParent);

	}

	public Configuration addProperty(final Property property) {
		final var createParent = parent.addChild(property.tagName(), property.propertyValue());
		return new Configuration(createParent);

	}

}
