package dev.ofekmalka.core.tag.maven;

public class MavenXmlTags {

	public enum Parent {

		RELEASES("releases"), //
		ACTIVATION_FILE("file"), //
		ACTIVATION_OS("os"), //
		ACTIVATION_PROPERTY("property"), //
		CI_MANAGEMENT("ciManagement"), //
		NOTIFIERS("notifiers"), //
		NOTIFIER("notifier"), //
		CONFIGURATION("configuration"), //
		CONTRIBUTOR("contributor"), //
		ROLES("roles"), //
		PROPERTIES("properties"), //
		DEPENDENCY("dependency"), //
		DEPENDENCY_EXCLUSION("exclusion"), //
		DEPENDENCY_EXCLUSIONS("exclusions"), //
		DEPENDENCIES("dependencies"), //
		DEPENDENCY_MANAGEMENT("dependencyManagement"), //
		DEVELOPER("developer"), //
		EXTENSION("extension"), //
		LICENSE("license"), //
		MAILINGLIST("mailingList"), //
		OTHER_ARCHIVES("otherArchives"), //
		ORGANIZATION("organization"), //
		PARENT_TAG("parent"), //
		PLUGIN_MANAGEMENT("pluginManagement"), //
		PLUGINS("plugins"), //
		EXECUTIONS("executions"), //
		PLUGIN("plugin"), //
		RELOCATION("relocation"), //
		REPORTING("reporting"), //
		SITE("site"), //
		SCM("scm"), //
		PLUGIN_EXECUTION("execution"), //
		GOALS("goals"), //
		REPORT_SETS("reportSets"), //
		REPORT_SET("reportSet"), //
		REPORTS("reports"), //
		REPOSITORY_POLICY("RepositoryPolicy"), //
		REPOSITORY("repository"), //
		SNAPSHOT_REPOSITORY("snapshotRepository"), //
		PLUGIN_REPOSITORY("pluginRepository"), //

		RESOURCE("resource"), //
		TEST_RESOURCE("testResource"), //

		INCLUDES("includes"), //
		EXCLUDES("excludes"), //
		ACTIVATION("activation"), //
		BUILD("build"), //
		FILTERS("filters"), //
		RESOURCES("resources"), //
		TEST_RESOURCES("testResources"), //
		EXTENSIONS("extensions"), //
		DISTRIBUTION_MANAGEMENT("distributionManagement"), //
		PROFILE("profile"), //
		PLUGIN_REPOSITORIES("pluginRepositories"), //
		MODULES("modules"), //
		REPOSITORIES("repositories"), //
		PROJECT("project"), //
		PROFILES("profiles"), //
		MAILING_LISTS("mailingLists"), //
		LICENSES("licenses"), //
		DEVELOPERS("developers"), //
		CONTRIBUTORS("contributors"), //
		SNAPSHOTS("snapshots");

		final String tagName;

		Parent(final String tagName) {
			this.tagName = tagName;
		}

		public String getTagName() {
			return tagName;
		}

	}

	public enum Leaf {
		FILTER("filter"), //
		GOAL("goal"), //
		OTHER_ARCHIVE("otherArchive"), //
		REPORT("report"), //
		RESOURCE_EXCLUSION("exclude"), //
		RESOURCE_INCLUSION("include"), //
		ROLE("role"), //
		SUBPROJECT("module"), //
		EXISTS("exists"), //
		MISSING("missing"), //
		ARCH("arch"), //
		FAMILY("family"), //
		VERSION("version"), //
		NAME("name"), //
		VALUE("value"), //
		SYSTEM("system"), //
		URL("url"), //
		TYPE("type"), //
		ADDRESS("address"), //
		SEND_ON_ERROR("sendOnError"), //
		SEND_ON_FAILURE("sendOnFailure"), //
		SEND_ON_SUCCESS("sendOnSuccess"), //
		SEND_ON_WARNING("sendOnWarning"), //
		EMAIL("email"), //
		ORGANIZATION("organization"), //
		ORGANIZATION_URL("organizationUrl"), //
		TIMEZONE("timezone"), //
		GROUP_ID("groupId"), //
		ARTIFACT_ID("artifactId"), //
		SCOPE("scope"), //
		CLASSIFIER("classifier"), //
		SYSTEM_PATH("systemPath"), //
		OPTIONAL("optional"), //
		ID("id"), //
		COMMENTS("comments"), //
		DISTRIBUTION("distribution"), //
		POST("post"), //
		SUBSCRIBE("subscribe"), //
		UNSUBSCRIBE("unsubscribe"), //
		ARCHIVE("archive"), //
		INHERITED("inherited"), //
		EXTENSIONS("extensions"), //
		MESSAGE("message"), //
		EXCLUDE_DEFAULTS("excludeDefaults"), //
		OUTPUT_DIRECTORY("outputDirectory"), //
		CHILD_SITE_URL_INHERIT_APPEND_PATH("childSiteUrlInheritAppendPath"), //
		CONNECTION("connection"), //
		DEVELOPER_CONNECTION("developerConnection"), //
		TAG("tag"), //
		MAVEN_PHASE("phase"), //
		ENABLED("enabled"), //
		CHECKSUM_POLICY("checksumPolicy"), //
		UPDATE_POLICY("updatePolicy"), //
		LAYOUT("layout"), //
		UNIQUE_VERSION("uniqueVersion"), //
		DIRECTORY("directory"), //
		FILTERING("filtering"), //
		TARGET_PATH("targetPath"), //
		JDK("jdk"), //
		ACTIVE_BY_DEFAULT("activeByDefault"), //
		SCRIPT_SOURCE_DIRECTORY("scriptSourceDirectory"), //
		SOURCE_DIRECTORY("sourceDirectory"), //
		TEST_SOURCE_DIRECTORY("testSourceDirectory"), //
		TEST_OUTPUT_DIRECTORY("testOutputDirectory"), //
		FINAL_NAME("finalName"), //
		DEFAULT_GOAL("defaultGoal"), //
		DOWNLOAD_URL("downloadUrl"), //
		STATUS("status"), //
		SOURCE("source"), //
		MODEL_VERSION("modelVersion"), //
		DESCRIPTION("description"), //
		INCEPTION_YEAR("inceptionYear"), //
		PACKAGING("packaging"), //
		CHILD_PROJECT_URL_INHERIT_APPEND_PATH("childProjectUrlInheritAppendPath"), //
		PHASE("phase");

		private final String tagName;

		Leaf(final String tagName) {
			this.tagName = tagName;
		}

		public String getTagName() {
			return tagName;
		}

	}

}
