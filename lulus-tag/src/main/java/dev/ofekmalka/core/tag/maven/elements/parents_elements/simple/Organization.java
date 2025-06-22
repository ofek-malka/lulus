package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Organization(MavenParentElement parent) {

	public static final Organization.AddUrl addName(final String name) {
		return (urlScheme, host) -> {

			final var createParent =

					MavenParentElement.name(Parent.ORGANIZATION)//
							.addChild(Leaf.NAME, name)//
							.addChild(Leaf.URL, urlScheme.getValue() + host);
			return new Organization(createParent);
		};
	}

	public interface AddName {
		Organization.AddUrl addName(final String name);

	}

	public interface AddUrl {
		Organization addUrl(final UrlScheme urlScheme, final String host);

	}

}
