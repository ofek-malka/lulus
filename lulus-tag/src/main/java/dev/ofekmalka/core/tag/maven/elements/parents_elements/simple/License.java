package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.LicenseDistribution;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record License(MavenParentElement parent) {

	private static License of(final MavenParentElement parent) {
		return new License(parent);
	}

	public static final AddUrl ofName(final String name) {
		return (urlScheme, host) -> {

			final var createParent =

					MavenParentElement.name(Parent.LICENSE)//
							.addChild(Leaf.NAME, name)//
							.addChild(Leaf.URL, urlScheme.getValue() + host);
			return License.of(createParent);

		};//
	}

	public interface AddName {
		License.AddUrl addName(final String name);

	}

	public interface AddUrl {
		License addUrl(final UrlScheme urlScheme, final String host);
	}

	public License addComments(final String comments) {
		return License.of(parent.addChild(Leaf.COMMENTS, comments));
	}

	public License addDistribution(final LicenseDistribution distribution) {
		return License.of(parent.addChild(Leaf.DISTRIBUTION, distribution.getValue()));

	}

}
