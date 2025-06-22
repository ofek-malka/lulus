package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Filter;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin.Plugin;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.PluginManagement;

interface BaseBuild {
	ProfileBuild addDefaultGoal(String defaultGoal);

	ProfileBuild addDirectory(String directory);

	ProfileBuild addFinalName(String finalName);

	ProfileBuild addFilters(final Filter... filters);

	// ProfileBuild addPluginManagement(PluginManagement pluginManagement);

	ProfileBuild addResources(final Resource... resources);

	ProfileBuild addTestResources(final Resource... resources);
}

public record ProfileBuild(MavenParentElement parent) implements BaseBuild {

	private static ProfileBuild of(final MavenParentElement parent) {
		return new ProfileBuild(parent);
	}

	public static final ProfileBuild addPlugins(final Plugin... plugins) {
		final var value = List.list(plugins)//
				.map(plugin -> plugin.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addPlugins");//

		final var createParent = MavenParentElement.name(Parent.BUILD).

				addChild(Parent.PLUGINS, value);

		return ProfileBuild.of(createParent);

	}

//	@Override
	public static ProfileBuild addPluginManagement(final PluginManagement pluginManagement) {

		final var createParent = MavenParentElement.name(Parent.BUILD)

				.addChild(

						pluginManagement.parent().getName(),

						pluginManagement.parent().getContent());
		return ProfileBuild.of(

				createParent);

	}

	@Override
	public ProfileBuild addFinalName(final String finalName) {

		return ProfileBuild.of(parent.addChild(Leaf.FINAL_NAME, finalName));

	}

	@Override
	public ProfileBuild addDefaultGoal(final String defaultGoal) {

		return ProfileBuild.of(parent.addChild(Leaf.DEFAULT_GOAL, defaultGoal));

	}

	@Override
	public ProfileBuild addDirectory(final String directory) {

		return ProfileBuild.of(parent.addChild(Leaf.DIRECTORY, directory));

	}

	@Override
	public ProfileBuild addFilters(final Filter... filters) {

		final var value = List.list(filters)//
				.map(Filter::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addFilters");//

		final var createParent = parent.

				addChild(Parent.FILTERS, value);

		return ProfileBuild.of(createParent);

	}

	@Override
	public ProfileBuild addResources(final Resource... resources) {

		final var value = List.list(resources)//
				.map(resource -> resource.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addFilters");//

		final var createParent = parent.

				addChild(Parent.RESOURCES, value);

		return ProfileBuild.of(createParent);

	}

	@Override
	public ProfileBuild addTestResources(final Resource... resources) {

		final var value = List.list(resources)//
				.map(resource -> resource.parent()

						.changeNameTo(Parent.TEST_RESOURCE)

						.toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addFilters");//

		final var createParent = parent.

				addChild(Parent.TEST_RESOURCES, value);

		return ProfileBuild.of(createParent);

	}

}
