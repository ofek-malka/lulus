package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.DependencyType;
import dev.ofekmalka.core.tag.maven.elements.enums.Scope;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Dependency(MavenParentElement parent) {

	private static Dependency of(final MavenParentElement parent) {
		return new Dependency(parent);
	}

	public static Dependency.AddArtifactId ofGroupId(final String groupId) {
		return artifactId -> {
			final var createParent =

					MavenParentElement.name(Parent.DEPENDENCY)//
							.addChild(Leaf.GROUP_ID, groupId)//
							.addChild(Leaf.ARTIFACT_ID, artifactId);
			return Dependency.of(createParent);
		};

	}

	public interface AddGroupId {
		Dependency.AddArtifactId addGroupId(final String groupId);
	}

	public interface AddArtifactId {
		Dependency andArtifactId(final String artifactId);

	}

	public Dependency addVersion(final String version) {
		return Dependency.of(parent.addChild(Leaf.VERSION, version));
	}

	public Dependency addScope(final Scope scope) {
		final var result = parent.addChild(Leaf.SCOPE,

				Result.of(scope.getScopeName()).getOrElse((String) null)

		);
		return Dependency.of(result);
	}

	public Dependency addType(final DependencyType type) {
		final var result = parent.addChild(Leaf.TYPE, Result.of(type.getType()).getOrElse((String) null));
		return Dependency.of(result);
	}

	public Dependency addClassifier(final String classifier) {
		return Dependency.of(parent.addChild(Leaf.CLASSIFIER, classifier));
	}

	public Dependency addSystemPath(final String systemPath) {
		return Dependency.of(parent.addChild(Leaf.SYSTEM_PATH, systemPath));

	}

	public Dependency addOptional(final boolean optional) {
		return Dependency.of(parent.addChild(Leaf.OPTIONAL, String.valueOf(optional)));
	}

	public Dependency addExclusions(final DependencyExclusion... exclusions) {

		final var value = List.list(exclusions)//
				.map(exclusion -> exclusion.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addExclusions");//

		final var createParent = parent.

				addChild(Parent.DEPENDENCY_EXCLUSIONS, value);

		return Dependency.of(createParent);

	}

}
