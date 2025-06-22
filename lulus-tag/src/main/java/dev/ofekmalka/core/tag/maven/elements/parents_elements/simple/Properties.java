package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Property;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Properties(MavenParentElement parent) {

	private static Properties of(final MavenParentElement parent) {
		return new Properties(parent);
	}

	public static Properties ofProperty(final LeftElements.Property property) {

		final var createParent = MavenParentElement.name(Parent.PROPERTIES)//
				.addChild(property.tagName(), property.propertyValue());
		return Properties.of(createParent);//

	}

	public Properties addProperty(final Property property) {

		final var createParent = parent.addChild(property.tagName(), property.propertyValue());
		return Properties.of(createParent);//
	}

}
