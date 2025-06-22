package dev.ofekmalka.core.tag.maven.elements.enums;

public enum JdkVersion {
	JAVA_6("1.6"), //
	JAVA_7("1.7"), //
	JAVA_8("1.8"), //
	JAVA_9("9"), //
	JAVA_10("10"), //
	JAVA_11("11"), // // LTS
	JAVA_12("12"), //
	JAVA_13("13"), //
	JAVA_14("14"), //
	JAVA_15("15"), //
	JAVA_16("16"), //
	JAVA_17("17"), // LTS
	JAVA_18("18"), //
	JAVA_19("19"), //
	JAVA_20("20"), //
	JAVA_21("21"), // LTS
	JAVA_22("22"), //
	JAVA_23("23");//
	// JAVA_24("24"),
	// JAVA_25("25"); // Expected next LTS version

	private final String version;

	JdkVersion(final String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

}
