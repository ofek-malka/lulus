package dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Configuration;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Dependency;

public record Plugin(MavenParentElement parent) implements ConfigurationContainer<Plugin> {

	private static Plugin of(final MavenParentElement parent) {
		return new Plugin(parent);
	}

	public static Plugin.AddArtifactId ofGroupId(final String groupId) {
		return artifactId -> {
			final var createParent =

					MavenParentElement.name(Parent.PLUGIN)//
							.addChild(Leaf.GROUP_ID, groupId)//
							.addChild(Leaf.ARTIFACT_ID, artifactId);
			return Plugin.of(createParent);
		};

	}

	public interface AddGroupId {
		Plugin.AddArtifactId addGroupId(final String groupId);
	}

	public interface AddArtifactId {
		Plugin addArtifactId(final String artifactId);

	}

	public Plugin addVersion(final String version) {

		return Plugin.of(parent.addChild(Leaf.VERSION, version));

	}

	@Override
	public Plugin addConfiguration(final Configuration configuration) {

		final var createParent = parent.addChild(Parent.CONFIGURATION, configuration.parent().getContent());
		return Plugin.of(createParent);

	}

	@Override
	public Plugin addInherited(final boolean inherited) {

		return Plugin.of(parent.addChild(Leaf.INHERITED, String.valueOf(inherited)));

	}

	public Plugin addExtensions(final boolean extensions) {
		return Plugin.of(parent.addChild(Leaf.EXTENSIONS, String.valueOf(extensions)));

	}

	public Plugin addAtLeastOneDependency(final Dependency... dependencies) {

		final var value = List.list(dependencies)//
				.map(dependency -> dependency.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addAtLeastOneDependency");//

		final var createParent = parent.

				addChild(Parent.DEPENDENCIES, value);

		return Plugin.of(createParent);

	}

	public Plugin addAtLeastOneExecution(final PluginExecution... executions) {

		final var value = List.list(executions)//
				.map(execution -> execution.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addAtLeastOneExecution");//

		final var createParent = parent.

				addChild(Parent.EXECUTIONS, value);

		return Plugin.of(createParent);

	}
}
