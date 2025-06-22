package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.Status;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin.Plugin;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.repository.Repository.SnapshotRepository;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Relocation;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Site;

public record DistributionManagement(MavenParentElement parent) {

	private static DistributionManagement of(final MavenParentElement parent) {
		return new DistributionManagement(parent);
	}

	public static AddSnapshotRepository addRepository(final Repository repository) {
		return

		snapshotRepository -> site -> {

			final var createParent =

					MavenParentElement.name(Parent.DISTRIBUTION_MANAGEMENT)//
							.addChild(repository.parent().getName(), repository.parent().getContent())
							.addChild(snapshotRepository.parent().getName(), snapshotRepository.parent().getContent())
							.addChild(site.parent().getName(), site.parent().getContent());
			return DistributionManagement.of(createParent);

		};//
	}

	public interface AddRepository {
		DistributionManagement.AddSnapshotRepository addRepository(final Repository repository);
	}

	public interface AddSnapshotRepository {
		DistributionManagement.AddSite addSnapshotRepository(final SnapshotRepository snapshotRepository);

	}

	public interface AddSite {
		DistributionManagement addSite(final Site site);
	}

	public DistributionManagement addFinalName(final String finalName) {

		return DistributionManagement.of(parent.addChild(Leaf.FINAL_NAME, finalName));

	}

	public DistributionManagement addDownloadUrl(final String downloadUrl) {
		return DistributionManagement.of(parent.addChild(Leaf.DOWNLOAD_URL, downloadUrl));

	}

	public DistributionManagement addStatus(final Status status) {
		return DistributionManagement.of(parent.addChild(Leaf.STATUS, status.getStatus()));
	}

	public DistributionManagement addRelocation(final Relocation relocation) {
		return DistributionManagement
				.of(parent.addChild(relocation.parent().getName(), relocation.parent().getContent()));

	}

	public DistributionManagement addPlugins(final Plugin... plugins) {

		final var value = List.list(plugins)//
				.map(plugin -> plugin.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addPlugins");//

		final var createParent = parent.

				addChild(Parent.PLUGINS, value);

		return DistributionManagement.of(createParent);

	}

}
