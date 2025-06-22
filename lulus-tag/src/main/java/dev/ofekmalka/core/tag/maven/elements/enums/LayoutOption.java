package dev.ofekmalka.core.tag.maven.elements.enums;

public enum LayoutOption {
	DEFAULT("default"), LEGACY("legacy");

	private String option;

	LayoutOption(final String option) {
		this.option = option;
	}

	public String getValue() {
		return option;
	}

}
