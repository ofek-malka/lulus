package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin.ReportPlugin;

public record Reporting(MavenParentElement parent) {

	private static Reporting of(final MavenParentElement parent) {
		return new Reporting(parent);
	}

	public static Reporting addAtLeastOneReportPlugin(final ReportPlugin... reportPlugins) {

		final var value = List.list(reportPlugins)//
				.map(report -> report.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addExclusions");//

		final var createParent = MavenParentElement.name(Parent.REPORTING)//
				.addChild(Parent.PLUGINS, value)//
		;

		return Reporting.of(createParent);

	}

	public Reporting addExcludeDefaults(final boolean excludeDefaults) {
		return Reporting.of(parent.addChild(Leaf.EXCLUDE_DEFAULTS, String.valueOf(excludeDefaults)));
	}

	public Reporting addOutputDirectory(final String outputDirectory) {
		return Reporting.of(parent.addChild(Leaf.OUTPUT_DIRECTORY, outputDirectory));
	}

}