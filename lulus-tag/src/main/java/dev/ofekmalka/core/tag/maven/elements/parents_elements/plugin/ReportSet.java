package dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Report;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Configuration;

public record ReportSet(MavenParentElement parent) implements ConfigurationContainer<ReportSet> {

	private static ReportSet of(final MavenParentElement parent) {
		return new ReportSet(parent);
	}

	public static final AddReports addId(final String id) {
		return reports -> {//
			final var value = List.list(reports)//
					.map(Report::toMaven)//
					.mkStr("\n")//
					.getOrElse("error in addReports");//

			final var createParent =

					MavenParentElement.name(Parent.REPORT_SET)//
							.addChild(Leaf.ID, id)//
							.addChild(Parent.REPORTS, value);
			return ReportSet.of(createParent);

		};//
	}

	public interface AddId {
		ReportSet.AddReports addGroupId(final String id);
	}

	public interface AddReports {
		ReportSet addReports(final LeftElements.Report... reports);

	}

	public ReportSet addVersion(final String version) {
		return ReportSet.of(parent.addChild(Leaf.VERSION, version));
	}

	@Override
	public ReportSet addConfiguration(final Configuration configuration) {
		return ReportSet.of(parent.addChild(Parent.CONFIGURATION, configuration.parent().getContent()));

	}//

	@Override
	public ReportSet addInherited(final boolean inherited) {
		return ReportSet.of(parent.addChild(Leaf.INHERITED, String.valueOf(inherited)));

	}

}
