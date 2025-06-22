package dev.ofekmalka.core.tag.maven.elements.enums;

public enum PackagingType {
	JAR("jar"), //
	WAR("war"), //
	EAR("ear"), //
	POM("pom");//

	private final String type;

	PackagingType(final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
