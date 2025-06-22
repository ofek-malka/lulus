package dev.ofekmalka.core.tag.maven.elements;

import dev.ofekmalka.core.tag.maven.elements.enums.InceptionYear;
import dev.ofekmalka.core.tag.maven.elements.enums.PackagingType;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Property;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Subproject;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.essence.Activation;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.essence.Build;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.essence.DistributionManagement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.essence.Profile;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.essence.ProfileBuild;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.essence.Project;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin.Plugin;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin.ReportPlugin;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.CiManagement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Configuration;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Contributor;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Dependency;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.DependencyManagement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Developer;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.License;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.MailingList;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Notifier;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Organization;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.ParentTag;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Properties;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Reporting;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Scm;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Site;

public class ExampleMavenPom {
	public static void main(final String[] args) {

		final var build = Build.addScriptSourceDirectory("src/main/scripts")//
				.addSourceDirectory("src/main/java")//
				.addTestSourceDirectory("src/test/java")//
				.addOutputDirectory("target/classes")//
				.addTestOutputDirectory("target/test-classes");

		Project.addDefaultModelVersion()//
				.addGroupId("com.ofek_malka")//
				.addArtifactId("my-project")//
				.addVersion("1.0-SNAPSHOT")//
				.addBuild(build)//
				.addCiManagement(CiManagement.ofSystem("Jenkins")//
						.addUrl("http://ci.example.com")//
						.addNotifiers(Notifier.addType("email")//
								.addAddress("builds@example.com")//
								.isSendOnError(true)//
								.isSendOnFailure(true)//
								.isSendOnSuccess(true)//
								.isSendOnWarning(false)//
								.addConfiguration(Configuration

										.isSetTo(Property.isSetTo("key").addValue("value")))//
						)//
				)//
				.addContributors(Contributor.ofName("Ofek Malka"))//
				.addDependencies(Dependency.ofGroupId("org.apache.commons")//
						.andArtifactId("commons-lang3")//
				)//
				.addDependencyManagement(DependencyManagement.ofAtleastOneDependency(Dependency.ofGroupId("org.slf4j")//
						.andArtifactId("slf4j-api")//
				))//
				.addDescription("My Maven Project for demonstrating usage.")//
				.addDevelopers(Developer.ofId("ofek_malka"))//
				.addDistributionManagement(DistributionManagement
						.addRepository(Repository.addId("r - id").addUrl(UrlScheme.HTTP, "bla.com"))//
						.addSnapshotRepository(
								Repository.addId("r - id").addUrl(UrlScheme.HTTP, "bla.com").asSnapshotRepository())//
						.addSite(Site.setToUrl(UrlScheme.HTTP, "myproject.example.com/site\")").addId("id")
								.addName("site name")))//
				.addInceptionYear(InceptionYear.YEAR_2023)//
				.addLicenses(License.ofName("Apache-2.0").addUrl(UrlScheme.HTTP,
						"https://www.apache.org/licenses/LICENSE-2.0"))//
				.addMailingLists(MailingList.ofName("my-project-dev")//
						.addPost("dev@myproject.example.com")//
						.addSubscribe("subscribe@myproject.example.com")//
						.addUnsubscribe("unsubscribe@myproject.example.com")//
						.addArchive("http://myproject.example.com/mailing-lists/")//
				)//
				.addName("My Project")//
				.addOrganization(Organization.addName("My Organization")//
						.addUrl(UrlScheme.HTTP, "http://myorganization.example.com")//
				)//
				.addPackaging(PackagingType.JAR)//
				.addParent(ParentTag.ofGroupId("com.ofek_malka.parent")//
						.addArtifactId("my-parent-pom")//
						.addVersion("1.0")//
				)//
				.addPluginRepositories(Repository.addId("my-plugins")
						.addUrl(UrlScheme.HTTP, "http://myproject.example.com/plugins").asPluginRepository())//
				.addProfiles(Profile.addId("dev").addActivation(Activation.isSetToActiveByDefault(false))
						.addBuild(ProfileBuild
								.addPlugins(Plugin.ofGroupId("Plugin.addGroupId").addArtifactId("Plugin.ArtifactId"))))//
				.addProperties(Properties.ofProperty(Property.isSetTo("java.version").addValue("1.8")))//
				.addReporting(Reporting.addAtLeastOneReportPlugin(ReportPlugin.ofGroupId("org.apache.maven.plugins")//
						.andArtifactId("maven-surefire-plugin"))//
				)//
				.addRepositories(
						Repository.addId("central").addUrl(UrlScheme.HTTP, "https://repo.maven.apache.org/maven2"))//
				.addScm(Scm.addConnection("scm:git:git@github.com:ofek_malka/my-project.git")//
						.addDeveloperConnection("scm:git:git@github.com:ofek_malka/my-project.git")//
						.addUrl(UrlScheme.HTTP, "https://github.com/ofek_malka/my-project")//
						.addTag("v1.0")//
				)//
				.addSubprojects(Subproject.isSetTo("my-subproject"))//
				.addUrl(UrlScheme.HTTP, "http://myproject.example.com")//
				.generateProcess().printlnToConsole()//

				.run();
	}
}