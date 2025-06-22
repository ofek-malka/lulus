package dev.ofekmalka.core.tag.maven.elements.enums;

public enum LicenseDistribution {
	REPO("repo"), MANUAL("manual");

	private final String value;

	LicenseDistribution(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}