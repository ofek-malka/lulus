package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Scm(MavenParentElement parent) {

	private static Scm of(final MavenParentElement parent) {
		return new Scm(parent);
	}

	public static final Scm.AddDeveloperConnection addConnection(final String connection) {
		return developerConnection ->

		(urlScheme, host) -> {

			final var createParent =

					MavenParentElement.name(Parent.SCM)//
							.addChild(Leaf.CONNECTION, connection)//
							.addChild(Leaf.DEVELOPER_CONNECTION, developerConnection)//

							.addChild(Leaf.URL, urlScheme.getValue() + host);
			return Scm.of(createParent);
		};

	}

	public interface AddConnection {
		Scm.AddDeveloperConnection addConnection(final String connection);

	}

	public interface AddDeveloperConnection {
		Scm.AddUrl addDeveloperConnection(final String developerConnection);

	}

	public interface AddUrl {
		Scm addUrl(final UrlScheme urlScheme, final String host);

	}

	public Scm addTag(final String tag) {
		return Scm.of(parent.addChild(Leaf.TAG, tag));
	}

}
