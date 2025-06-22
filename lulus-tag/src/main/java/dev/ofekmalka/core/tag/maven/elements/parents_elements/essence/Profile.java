package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Subproject;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository.PluginRepository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Dependency;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.DependencyManagement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Properties;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Reporting;

public record Profile(MavenParentElement parent) implements ModelBase {

	private static Profile of(final MavenParentElement parent) {
		return new Profile(parent);
	}

	public static AddActivation addId(final String id) {
		return

		activation -> build ->

		{

			final var createParent =

					MavenParentElement.name(Parent.PROFILE)//
							.addChild(Leaf.ID, id)//
							.addChild(

									activation.parent().getName(),

									activation.parent().getContent())
							.addChild(

									build.parent().getName(),

									build.parent().getContent());
			return Profile.of(createParent);

		};//
	}

	public interface AddId {
		Profile.AddActivation addId(final String id);
	}

	public interface AddActivation {
		Profile.AddBuild addActivation(final Activation activation);

	}

	public interface AddBuild {
		Profile addBuild(final ProfileBuild build);
	}

	public Profile addSource(final String source) {

		return Profile.of(parent.addChild(Leaf.SOURCE, source));

	}

	@Override
	public Profile addProperties(final Properties properties) {
		return Profile.of(parent

				.addChild(

						properties.parent().getName(),

						properties.parent().getContent())

		);
	}

	@Override
	public Profile addDistributionManagement(final DistributionManagement distributionManagement) {

		return Profile.of(parent

				.addChild(

						distributionManagement.parent().getName(),

						distributionManagement.parent().getContent())

		);

	}

	@Override
	public Profile addDependencyManagement(final DependencyManagement dependencyManagement) {
		return Profile.of(parent

				.addChild(

						dependencyManagement.parent().getName(),

						dependencyManagement.parent().getContent())

		);
	}

	@Override
	public Profile addReporting(final Reporting reporting) {
		return Profile.of(parent

				.addChild(

						reporting.parent().getName(),

						reporting.parent().getContent())

		);
	}

	@Override
	public Profile addDependencies(final Dependency... dependencies) {

		final var value = List.list(dependencies)//
				.map(dependency -> dependency.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addDependencies");//

		final var createParent = parent.

				addChild(Parent.DEPENDENCIES, value);

		return Profile.of(createParent);

	}

	@Override
	public Profile addPluginRepositories(final PluginRepository... pluginRepositories) {

		final var value = List.list(pluginRepositories)//
				.map(pluginRepository -> pluginRepository.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addPluginRepositories");//

		final var createParent = parent.

				addChild(Parent.PLUGIN_REPOSITORIES, value);

		return Profile.of(createParent);

	}

	@Override
	public Profile addSubprojects(final Subproject... subprojects) {

		final var value = List.list(subprojects)//
				.map(Subproject::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addSubprojects");//

		final var createParent = parent.

				addChild(Parent.MODULES, value);

		return Profile.of(createParent);

	}

	@Override
	public Profile addRepositories(final Repository... repositories) {

		final var value = List.list(repositories)//
				.map(repository -> repository.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addRepositories");//

		final var createParent = parent.

				addChild(Parent.REPOSITORIES, value);

		return Profile.of(createParent);

	}

}
