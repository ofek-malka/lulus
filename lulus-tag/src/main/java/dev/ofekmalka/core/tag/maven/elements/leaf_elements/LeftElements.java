package dev.ofekmalka.core.tag.maven.elements.leaf_elements;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;

public interface LeftElements {

	String toMaven();

	public static record Filter(String tagName, String propertiesFileName) implements LeftElements {
//		/*
//		 * filter: Defines *.properties files that contain a list of properties that
//		 * apply to resources which accept their settings (covered below). In other
//		 * words, the "name=value" pairs defined within the filter files replace ${name}
//		 * strings within resources on build. The example above defines the
//		 * filter1.properties file under the filters/ directory. Maven's default filter
//		 * directory is ${project.basedir}/src/main/filters/.
//		 */
		public static Filter isSetTo(final String propertiesFileName) {
			return new Filter(Leaf.FILTER.getTagName(), propertiesFileName);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + propertiesFileName + "</" + tagName + ">";
		}

	}

	public static record Goal(String tagName, String goalName) implements LeftElements {

		public static Goal isSetTo(final String goalName) {
			return new Goal(Leaf.GOAL.getTagName(), goalName);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + goalName + "</" + tagName + ">";
		}

	}

	public static record OtherArchive(String tagName, String archiveName) implements LeftElements {

		public static OtherArchive isSetTo(final String archiveName) {
			return new OtherArchive(Leaf.OTHER_ARCHIVE.getTagName(), archiveName);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + archiveName + "</" + tagName + ">";
		}

	}

	public static record Property(String tagName, String propertyValue) implements LeftElements {

		public interface AddValue {
			Property addValue(final String propertyValue);

			default Property withReferance(final Property property) {

				return addValue("${" + property.tagName + "}");
			}

		}

		public static AddValue isSetTo(final String tagName) {
			return propertyValue -> new Property(tagName, propertyValue);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + propertyValue + "</" + tagName + ">";
		}

	}

	public static record Report(String tagName, String reportName) implements LeftElements {

		public static Report isSetTo(final String reportName) {
			return new Report(Leaf.REPORT.getTagName(), reportName);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + reportName + "</" + tagName + ">";
		}

	}

	public static record ResourceExclusion(String tagName, String excludedResourcePattern) implements LeftElements {

		public static ResourceExclusion isSetTo(final String excludedResourcePattern) {
			return new ResourceExclusion(Leaf.RESOURCE_EXCLUSION.getTagName(), excludedResourcePattern);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + excludedResourcePattern + "</" + tagName + ">";
		}

	}

	public static record ResourceInclusion(String tagName, String includedResourcePattern) implements LeftElements {

		public static ResourceInclusion isSetTo(final String includedResourcePattern) {
			return new ResourceInclusion(Leaf.RESOURCE_INCLUSION.getTagName(), includedResourcePattern);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + includedResourcePattern + "</" + tagName + ">";
		}

	}

	public static record Role(String tagName, String roleName) implements LeftElements {

		public static Role isSetTo(final String roleName) {
			return new Role(Leaf.ROLE.getTagName(), roleName);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + roleName + "</" + tagName + ">";
		}

	}

	public static record Subproject(String tagName, String moduleName) implements LeftElements {

		public static Subproject isSetTo(final String moduleName) {
			return new Subproject(Leaf.SUBPROJECT.getTagName(), moduleName);

		}

		@Override
		public String toMaven() {
			return "<" + tagName + ">" + moduleName + "</" + tagName + ">";
		}

	}

}
