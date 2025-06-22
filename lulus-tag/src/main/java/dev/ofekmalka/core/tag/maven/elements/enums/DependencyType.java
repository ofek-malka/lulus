package dev.ofekmalka.core.tag.maven.elements.enums;

public enum DependencyType {
	// New types can be defined by extensions, so this is not a complete list.

	JAR("jar"), // default
	WAR("war"), //
	POM("pom"), //
	EAR("ear"), //
	ZIP("zip"), //
	TAR_GZ("tar.gz"), //
	TEST_JAR("test-jar"), //
	SOURCES("sources"), //
	JAVADOC("javadoc"), //
	DLL("dll"), //
	OTHER("other"); // For any custom types not listed

	private final String type;

	DependencyType(final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
