package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.ResourceExclusion;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.ResourceInclusion;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Resource(MavenParentElement parent) {

	private static Resource of(final MavenParentElement parent) {
		return new Resource(parent);
	}

	public static final Resource defineDirectory(final String directory) {

		final var createParent = MavenParentElement.name(Parent.RESOURCE)//
				.addChild(Leaf.DIRECTORY, directory);
		return Resource.of(createParent);

	}

	public Resource addFiltering(final boolean filtering) {
		return Resource.of(parent.addChild(Leaf.FILTERING, String.valueOf(filtering)));//

	}

	public Resource addTargetPath(final String targetPath) {
		return Resource.of(parent.addChild(Leaf.TARGET_PATH, targetPath));//

	}

	public Resource addInclusions(final LeftElements.ResourceInclusion... inclusions) {

		final var value = List.list(inclusions)//
				.map(ResourceInclusion::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addInclusions");//

		final var createParent = parent.

				addChild(Parent.INCLUDES, value);

		return Resource.of(createParent);//

	}

	public Resource addExclusions(final LeftElements.ResourceExclusion... exclusions) {

		final var value = List.list(exclusions)//
				.map(ResourceExclusion::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addExclusions");//

		final var createParent = parent.

				addChild(Parent.EXCLUDES, value);

		return Resource.of(createParent);//

	}

}
