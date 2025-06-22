package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record DependencyManagement(MavenParentElement parent) {

	public static DependencyManagement ofAtleastOneDependency(final Dependency... dependencies) {

		final var value = List.list(dependencies)//
				.map(dependency -> dependency.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in ofAtleastOneDependency");//

		final var createParent =

				MavenParentElement.name(Parent.DEPENDENCY_MANAGEMENT)

						.addChild(Parent.DEPENDENCIES, value);

		return new DependencyManagement(createParent);
	}

}
