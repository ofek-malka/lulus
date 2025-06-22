package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Relocation(MavenParentElement parent) {

	public static Relocation.AddArtifactId ofGroupId(final String groupId) {
		return artifactId -> version -> {
			final var createParent =

					MavenParentElement.name(Parent.RELOCATION)//
							.addChild(Leaf.GROUP_ID, groupId)//
							.addChild(Leaf.ARTIFACT_ID, artifactId)

							.addChild(Leaf.VERSION, version)

			;
			return new Relocation(createParent);
		};

	}

	public interface AddArtifactId {
		Relocation.AddVersion addArtifactId(final String artifactId);

	}

	public interface AddVersion {
		Relocation addVersion(final String version);
	}

	public Relocation addMessage(final String message) {
		return new Relocation(parent.addChild(Leaf.MESSAGE, message));
	}

}
