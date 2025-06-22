package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import java.util.ArrayList;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Filter;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin.Plugin;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Extension;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.PluginManagement;

public record Build(MavenParentElement parent) {

	private static Build of(final MavenParentElement parent) {
		return new Build(parent);
	}

	public static final AddSourceDirectory addScriptSourceDirectory(final String scriptSourceDirectory) {
		return sourceDirectory -> testSourceDirectory ->

		outputDirectory -> testOutputDirectory ->

		{

			final var createParent =

					MavenParentElement.name(Parent.BUILD)//
							.addChild(Leaf.SCRIPT_SOURCE_DIRECTORY, scriptSourceDirectory)//
							.addChild(Leaf.SOURCE_DIRECTORY, sourceDirectory)//
							.addChild(Leaf.TEST_SOURCE_DIRECTORY, testSourceDirectory)//
							.addChild(Leaf.OUTPUT_DIRECTORY, outputDirectory)//
							.addChild(Leaf.TEST_OUTPUT_DIRECTORY, testOutputDirectory);
			return Build.of(createParent);

		};//
	}

	public interface AddScriptSourceDirectory {
		Build.AddSourceDirectory addScriptSourceDirectory(final String scriptSourceDirectory);

		default Build.AddSourceDirectory addDefaultScriptSourceDirectory() {
			return addScriptSourceDirectory("${project.basedir}/src/main/scripts");
		}
	}

	public interface AddSourceDirectory {
		Build.AddTestSourceDirectory addSourceDirectory(final String sourceDirectory);

		default Build.AddTestSourceDirectory addDefaultSourceDirectory() {
			return addSourceDirectory("${project.basedir}/src/main/java");
		}
	}

	public interface AddTestSourceDirectory {
		Build.AddOutputDirectory addTestSourceDirectory(final String testSourceDirectory);

		default Build.AddOutputDirectory addDefaultTestSourceDirectory() {
			return addTestSourceDirectory("${project.basedir}/src/test/java");
		}
	}

	public interface AddOutputDirectory {
		Build.AddTestOutputDirectory addOutputDirectory(final String outputDirectory);

		default Build.AddTestOutputDirectory addDefaultOutputDirectory() {
			return addOutputDirectory("${project.build.directory}");
		}
	}

	public interface AddTestOutputDirectory {
		Build addTestOutputDirectory(final String testOutputDirectory);

		default Build addDefaultTestOutputDirectory() {
			return addTestOutputDirectory("${project.build.directory}/test-classes");
		}
	}

	public Build addFinalName(final String finalName) {

		return Build.of(parent.addChild(Leaf.FINAL_NAME, finalName));

	}

	public Build addDefaultGoal(final String defaultGoal) {

		return Build.of(parent.addChild(Leaf.DEFAULT_GOAL, defaultGoal));

	}

	public Build addDirectory(final String directory) {

		return Build.of(parent.addChild(Leaf.DIRECTORY, directory));

	}

	public Build addFilters(final Filter... filters) {

		final var value = List.list(filters)//
				.map(Filter::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addFilters");//

		final var createParent = parent.

				addChild(Parent.FILTERS, value);

		return Build.of(createParent);

	}

	public Build addResources(final Resource... resources) {

		final var value = List.list(resources)//
				.map(resource -> resource.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addResources");//

		final var createParent = parent.

				addChild(Parent.RESOURCES, value);

		return Build.of(createParent);

	}

	public Build addTestResources(final Resource... resources) {

		final var value = List.list(resources)//
				.map(resource -> resource

						.parent()//

						.changeNameTo(Parent.TEST_RESOURCE)//

						.toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addTestResources");//

		final var createParent = parent.

				addChild(Parent.TEST_RESOURCES, value);

		return Build.of(createParent);

	}

	public Build addPlugins(final Plugin... plugins) {

		final var value = List.list(plugins)//
				.map(plugin -> plugin.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addPlugins");//

		final var createParent = parent.

				addChild(Parent.PLUGINS, value);

		return Build.of(createParent);

	}

	public Build addExtensions(final Extension... extensions) {
		// return
		// Build.of(parent.addChild(ParentElement.EXTENSIONS.addChildren(extensions)));

		final var value = List.list(extensions)//
				.map(extension -> extension.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addExtensions");//

		final var createParent = parent.

				addChild(Parent.EXTENSIONS, value);

		return Build.of(createParent);

	}

	public Build addPluginManagement(final PluginManagement pluginManagement) {
		return Build.of(parent.addChild(pluginManagement.parent().getName(), pluginManagement.parent().getContent()));

	}

	public static void main(final String[] args) {

		final String[] groupIds = { "group1", "group2", "group3" };

		final String[] artifactIds = { "ArtifactId1", "ArtifactId2", "ArtifactId3" };
		final String[] versions = { "version1", "version2", "version3" };

		System.out.println(List.extendedFactoryOperations() //

				.generateRange(0, 3)//

				.map(number -> Plugin.ofGroupId(groupIds[number]).addArtifactId(artifactIds[number])
						.addVersion(versions[number]))

				.convert().toJavaList(ArrayList::new).map(s -> s.toArray(new Plugin[0])));
		final var plugins = List.extendedFactoryOperations() //

				.generateRange(0, 3)//

				.map(number -> Plugin.ofGroupId(groupIds[number]).addArtifactId(artifactIds[number])
						.addVersion(versions[number]))

				.convert().toJavaList(ArrayList::new)

				.map(s -> // (Plugin[])
				s.toArray(new Plugin[0])).successValue();

		final var extension = Extension.addGroupId(null).addArtifactId(null).addVersion(null);
		final var plugin = Plugin.ofGroupId(null).addArtifactId(null).addVersion(null);

		final var extensions = List.extendedFactoryOperations()

				.createFilledList(3, () -> extension).convert()

				.toJavaList(ArrayList::new).map(s -> s.toArray(new Extension[0])).successValue();

		final var pluginManagement = PluginManagement.addAtleastOnePlugin(plugin);

		final var result = Build.addScriptSourceDirectory(null)//
				.addSourceDirectory(null)//
				.addTestSourceDirectory(null)//
				.addOutputDirectory(null)//
				.addTestOutputDirectory(null)//
				.addFinalName(null)//
				.addDefaultGoal(null)//
				.addDirectory(null)//
				.addExtensions(extensions[0], extensions[1], extensions[2])//
				.addFilters(Filter.isSetTo(null))//
				.addPluginManagement(pluginManagement)//
				.addPlugins(plugins[0], plugins[1], plugins[2])//
				.addResources(Resource.defineDirectory(null))//
				.addTestResources(Resource.defineDirectory(null))//
		;//

		System.out.println(result.parent().toMaven());

	}

}
