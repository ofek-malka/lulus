package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record ParentTag(MavenParentElement parent) {

	public static ParentTag.AddArtifactId ofGroupId(final String groupId) {
		return artifactId -> version -> {
			final var createParent =

					MavenParentElement.name(Parent.PARENT_TAG)//
							.addChild(Leaf.GROUP_ID, groupId)//
							.addChild(Leaf.ARTIFACT_ID, artifactId)

							.addChild(Leaf.VERSION, version)

			;
			return new ParentTag(createParent);
		};

	}

	public interface AddArtifactId {
		ParentTag.AddVersion addArtifactId(final String artifactId);

	}

	public interface AddVersion {
		ParentTag addVersion(final String version);
	}

}
