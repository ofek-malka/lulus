package dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Configuration;

public record ReportPlugin(MavenParentElement parent) implements ConfigurationContainer<ReportPlugin> {

	private static ReportPlugin of(final MavenParentElement parent) {
		return new ReportPlugin(parent);
	}

	public static ReportPlugin.AddArtifactId ofGroupId(final String groupId) {
		return artifactId -> {
			final var createParent =

					MavenParentElement.name(Parent.PLUGIN)//
							.addChild(Leaf.GROUP_ID, groupId)//
							.addChild(Leaf.ARTIFACT_ID, artifactId);
			return ReportPlugin.of(createParent);
		};

	}

	public interface AddGroupId {
		ReportPlugin.AddArtifactId addGroupId(final String groupId);
	}

	public interface AddArtifactId {
		ReportPlugin andArtifactId(final String artifactId);

	}

	public ReportPlugin addVersion(final String version) {
		return ReportPlugin.of(parent.addChild(Leaf.VERSION, version));
	}

	public ReportPlugin addAtLeastOneReportSet(final ReportSet... reportSets) {

		final var value = List.list(reportSets)//
				.map(report -> report.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addAtLeastOneReportSet");//

		return ReportPlugin.of(parent.addChild(Parent.REPORT_SETS, value));
	}

	@Override
	public ReportPlugin addConfiguration(final Configuration configuration) {
		return ReportPlugin.of(parent.addChild(Parent.CONFIGURATION, configuration.parent().getContent()));

	}//

	@Override
	public ReportPlugin addInherited(final boolean inherited) {
		return ReportPlugin.of(parent.addChild(Leaf.INHERITED, String.valueOf(inherited)));

	}

}
