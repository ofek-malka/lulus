package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record DependencyExclusion(MavenParentElement parent) {

	private static DependencyExclusion of(final MavenParentElement parent) {
		return new DependencyExclusion(parent);
	}

	public static AddArtifactId ofGroupId(final String groupId) {
		return builder().addGroupId(groupId);
	}

	static AddGroupId builder() {
		return groupId -> artifactId -> {
			final var createParent =

					MavenParentElement.name(Parent.EXTENSION)//
							.addChild(Leaf.GROUP_ID, groupId)//
							.addChild(Leaf.ARTIFACT_ID, artifactId);
			return DependencyExclusion.of(createParent);
		};
	}

	public interface AddGroupId {
		DependencyExclusion.AddArtifactId addGroupId(final String groupId);
	}

	public interface AddArtifactId {
		DependencyExclusion andArtifactId(final String artifactId);

	}

}
