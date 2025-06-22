package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.InceptionYear;
import dev.ofekmalka.core.tag.maven.elements.enums.PackagingType;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Subproject;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository.PluginRepository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.CiManagement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Contributor;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Dependency;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.DependencyManagement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Developer;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.License;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.MailingList;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Organization;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.ParentTag;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Properties;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Reporting;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Scm;

interface ProjectBehavior {

	Project addBuild(final Build build); //

	// need to check
//	Project addChildProjectUrlInheritAppendPath(final boolean childProjectUrlInheritAppendPath); //

	Project addCiManagement(final CiManagement ciManagement); //

	Project addContributors(final Contributor... contributors); //

	Project addDescription(final String description); //

	Project addDevelopers(final Developer... developers); //

	Project addInceptionYear(final InceptionYear inceptionYear);//

	Project addLicenses(final License... licenses); //

	Project addMailingLists(final MailingList... mailingLists); //

	Project addName(final String name); //

	Project addOrganization(final Organization organization); //

	Project addPackaging(final PackagingType packaging);

	Project addParent(final ParentTag parentTag); //

	Project addProfiles(final Profile... profiles); //

	Project addScm(final Scm scm); //

	Project addUrl(final UrlScheme urlScheme, final String host); //
}

public final class Project implements ModelBase, ProjectBehavior {

	private final MavenParentElement parent;

	private Project(final MavenParentElement parent) {
		this.parent = parent;
	}

	private static Project of(final MavenParentElement parent) {
		return new Project(parent);
	}

	public static AddGroupId addModelVersion(final String id) {
		return Project.builder().addModelVersion(id);
	}

	public static AddGroupId addDefaultModelVersion() {
		return Project.builder().addDefaultModelVersion();
	}

	public static AddModelVersion builder() {
		return modelVersion -> //
		groupId -> //
		artifactId -> //
		version -> {//

			final var createParent = MavenParentElement.name(Parent.PROJECT)//

					.addChild(Leaf.MODEL_VERSION, modelVersion)//
					.addChild(Leaf.GROUP_ID, groupId)//
					.addChild(Leaf.ARTIFACT_ID, artifactId)//
					.addChild(Leaf.VERSION, version);

			return Project.of(createParent);

		};//
	}

	public interface AddModelVersion {
		default Project.AddGroupId addDefaultModelVersion() {
			return addModelVersion("4.0.0");
		}

		Project.AddGroupId addModelVersion(final String modelVersion);

	}

	public interface AddGroupId {
		Project.AddArtifactId addGroupId(final String groupId);

	}

	public interface AddArtifactId {
		Project.AddVersion addArtifactId(final String artifactId);
	}

	public interface AddVersion {
		Project addVersion(final String version);
	}

	@Override
	public Project addProperties(final Properties properties) {
		return Project.of(parent

				.addChild(

						properties.parent().getName(),

						properties.parent().getContent())

		);
	}

	@Override
	public Project addDistributionManagement(final DistributionManagement distributionManagement) {

		return Project.of(parent

				.addChild(

						distributionManagement.parent().getName(),

						distributionManagement.parent().getContent())

		);

	}

	@Override
	public Project addDependencyManagement(final DependencyManagement dependencyManagement) {
		return Project.of(parent

				.addChild(

						dependencyManagement.parent().getName(),

						dependencyManagement.parent().getContent())

		);
	}

	@Override
	public Project addReporting(final Reporting reporting) {
		return Project.of(parent

				.addChild(

						reporting.parent().getName(),

						reporting.parent().getContent())

		);
	}

	@Override
	public Project addDependencies(final Dependency... dependencies) {

		final var value = List.list(dependencies)//
				.map(dependency -> dependency.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addDependencies");//

		final var createParent = parent.

				addChild(Parent.DEPENDENCIES, value);

		return Project.of(createParent);

	}

	@Override
	public Project addPluginRepositories(final PluginRepository... pluginRepositories) {

		final var value = List.list(pluginRepositories)//
				.map(pluginRepository -> pluginRepository.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addPluginRepositories");//

		final var createParent = parent.

				addChild(Parent.PLUGIN_REPOSITORIES, value);

		return Project.of(createParent);

	}

	@Override
	public Project addSubprojects(final Subproject... subprojects) {

		final var value = List.list(subprojects)//
				.map(Subproject::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addSubprojects");//

		final var createParent = parent.

				addChild(Parent.MODULES, value);

		return Project.of(createParent);

	}

	@Override
	public Project addRepositories(final Repository... repositories) {

		final var value = List.list(repositories)//
				.map(repository -> repository.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addRepositories");//

		final var createParent = parent.

				addChild(Parent.REPOSITORIES, value);

		return Project.of(createParent);

	}

	// ====================================================================================================
	// ==========================================================================================
	@Override
	public Project addBuild(final Build build) {
		return Project.of(parent.addChild(

				build.parent().getName(),

				build.parent().getContent()));
	}

	@Override
	public Project addCiManagement(final CiManagement ciManagement) {
		return Project.of(parent.addChild(

				ciManagement.parent().getName(),

				ciManagement.parent().getContent()));
	}

	@Override
	public Project addParent(final ParentTag parentTag) {
		return Project.of(parent.addChild(

				parentTag.parent().getName(),

				parentTag.parent().getContent()));
	}

	@Override
	public Project addOrganization(final Organization organization) {
		return Project.of(parent.addChild(

				organization.parent().getName(),

				organization.parent().getContent()));
	}

	@Override
	public Project addScm(final Scm scm) {
		return Project.of(parent.addChild(

				scm.parent().getName(),

				scm.parent().getContent()));
	}

	@Override
	public Project addDescription(final String description) {
		return Project.of(parent.addChild(Leaf.DESCRIPTION, description));
	}

	@Override
	public Project addInceptionYear(final InceptionYear inceptionYear) {
		return Project.of(parent.addChild(Leaf.INCEPTION_YEAR, String.valueOf(inceptionYear.getYear())));
	}

	@Override
	public Project addName(final String name) {
		return Project.of(parent.addChild(Leaf.NAME, name));

	}

	@Override
	public Project addPackaging(final PackagingType packaging) {
		return Project.of(parent.addChild(Leaf.PACKAGING, packaging.getType()));
	}

	@Override
	public Project addUrl(final UrlScheme urlScheme, final String host) {

		return Project.of(parent.addChild(Leaf.URL, urlScheme.getValue() + host));

	}

//	@Override
//	public Project addChildProjectUrlInheritAppendPath(final boolean childProjectUrlInheritAppendPath) {
//
//		return Project.of(parent.addChild(Leaf.CHILD_PROJECT_URL_INHERIT_APPEND_PATH,
//				String.valueOf(childProjectUrlInheritAppendPath)));
//
//	}

	@Override
	public Project addContributors(final Contributor... contributors) {
		final var value = List.list(contributors)//
				.map(contributor -> contributor.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addContributors");//

		final var createParent = parent.

				addChild(Parent.CONTRIBUTORS, value);

		return Project.of(createParent);

	}

	@Override
	public Project addDevelopers(final Developer... developers) {

		final var value = List.list(developers)//
				.map(developer -> developer.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addDevelopers");//

		final var createParent = parent.

				addChild(Parent.DEVELOPERS, value);

		return Project.of(createParent);

	}

	@Override
	public Project addLicenses(final License... licenses) {

		final var value = List.list(licenses)//
				.map(license -> license.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addLicenses");//

		final var createParent = parent.

				addChild(Parent.LICENSES, value);

		return Project.of(createParent);

	}

	@Override
	public Project addMailingLists(final MailingList... mailingLists) {

		final var value = List.list(mailingLists)//
				.map(mailingList -> mailingList.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addMailingLists");//

		final var createParent = parent.

				addChild(Parent.MAILING_LISTS, value);

		return Project.of(createParent);

	}

	@Override
	public Project addProfiles(final Profile... profiles) {

		final var value = List.list(profiles)//
				.map(profile -> profile.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addProfiles");//

		final var createParent = parent.

				addChild(Parent.PROFILES, value);

		return Project.of(createParent);

	}

	public Result<String> generateProcess() {

		final var preTagProjectOpening = """
				<?xml version="1.0" encoding="UTF-8"?>
				<project xmlns="http://maven.apache.org/POM/4.0.0"
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
					xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">""";

		return Result.success(parent.toMaven().replace("<" + Parent.PROJECT.getTagName() + ">", preTagProjectOpening))

		;

	}

}
