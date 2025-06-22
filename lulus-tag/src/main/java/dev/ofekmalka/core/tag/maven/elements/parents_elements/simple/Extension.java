package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Extension(MavenParentElement parent) {

	public static final Extension.AddArtifactId addGroupId(final String groupId) {
		return artifactId -> version -> {

			final var createParent =

					MavenParentElement.name(Parent.EXTENSION)//
							.addChild(Leaf.GROUP_ID, groupId)//
							.addChild(Leaf.ARTIFACT_ID, artifactId)//
							.addChild(Leaf.VERSION, version);

			return new Extension(createParent);
		};

	}

	public interface AddGroupId {
		Extension.AddArtifactId addGroupId(final String groupId);
	}

	public interface AddArtifactId {
		Extension.AddVersion addArtifactId(final String artifactId);
	}

	public interface AddVersion {
		Extension addVersion(final String version);

	}

}
