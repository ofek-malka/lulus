package dev.ofekmalka.core.tag.maven.elements.parents_elements.repository;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.LayoutOption;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record Repository(MavenParentElement parent) {

	private static Repository of(final MavenParentElement parent) {
		return new Repository(parent);
	}

	public static final AddUrl addId(final String id) {
		return (urlScheme, host) -> {

			final var createParent =

					MavenParentElement.name(Parent.REPOSITORY)//
							.addChild(Leaf.ID, id)//

							.addChild(Leaf.URL, urlScheme.getValue() + host);
			return Repository.of(createParent);
		};

	}

	public interface AddId {
		Repository.AddUrl addId(final String id);

	}

	public interface AddUrl {
		Repository addUrl(final UrlScheme urlScheme, final String host);

	}

	public Repository addReleases(final RepositoryPolicy releases) {

		return Repository.of(parent.

				addChild(Parent.RELEASES, releases.parent().getContent()));

	}

	public Repository addSnapshots(final RepositoryPolicy snapshots) {

		return Repository.of(parent.

				addChild(Parent.SNAPSHOTS, snapshots.parent().getContent()));

	}

	public Repository addName(final String name) {
		return Repository.of(parent.addChild(Leaf.NAME, name));
	}

	public Repository addLayoutTo(final LayoutOption option) {
		return Repository.of(parent.addChild(Leaf.LAYOUT, option.getValue()));
	}

	public DeploymentRepository asDeploymentRepositoryByAddUniqueVersion(final boolean uniqueVersion) {
		return DeploymentRepository.createInstance(parent.addChild(

				Leaf.UNIQUE_VERSION, String.valueOf(uniqueVersion)));
	}

	public SnapshotRepository asSnapshotRepository() {
		return SnapshotRepository.createInstance(

				parent.changeNameTo(Parent.SNAPSHOT_REPOSITORY));
	}

	public PluginRepository asPluginRepository() {
		return PluginRepository.createInstance(

				parent.changeNameTo(Parent.PLUGIN_REPOSITORY));
	}

	public static record DeploymentRepository(MavenParentElement parent) {

		static DeploymentRepository createInstance(final MavenParentElement parent) {
			return new DeploymentRepository(parent);
		}

	}

	public static record SnapshotRepository(MavenParentElement parent) {

		static SnapshotRepository createInstance(final MavenParentElement parent) {
			return new SnapshotRepository(parent);
		}

	}

	public static record PluginRepository(MavenParentElement parent) {

		static PluginRepository createInstance(final MavenParentElement parent) {
			return new PluginRepository(parent);
		}

	}

	public static void main(final String[] args) {
		final var releases = RepositoryPolicy.disabled()//
				.addChecksumPolicyToIgnore()//
				.addUpdatePolicyToNever()//
		;
		final var snapshots = RepositoryPolicy.disabled()//
				.addChecksumPolicyToIgnore()//
				.addUpdatePolicyToNever()//
		;
		final var result = Repository.addId("gggg")//
				.addUrl(UrlScheme.HTTP, "ggg")//
				.addReleases(releases)//
				.addSnapshots(snapshots)//
				.addLayoutTo(LayoutOption.DEFAULT)//
				.asDeploymentRepositoryByAddUniqueVersion(false);

		System.out.println(result.parent().toMaven());

	}

}
