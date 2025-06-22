package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Subproject;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository.PluginRepository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Dependency;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.DependencyManagement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Properties;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Reporting;

sealed interface ModelBase permits Project, Profile {
	ModelBase addProperties(final Properties properties);

	ModelBase addDistributionManagement(final DistributionManagement distributionManagement);

	ModelBase addDependencyManagement(final DependencyManagement dependencyManagement);

	ModelBase addReporting(final Reporting reporting);

	ModelBase addDependencies(final Dependency... dependencies);

	ModelBase addPluginRepositories(final PluginRepository... pluginRepositories);

//	ModelBase addModules(final Module module, final Module... modules);
	ModelBase addSubprojects(final Subproject... subprojects);

//subproject
	ModelBase addRepositories(final Repository... repositories);
}
