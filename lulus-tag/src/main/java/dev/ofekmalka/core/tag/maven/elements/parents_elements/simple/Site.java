package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

/**
 *
 * <id>: While not strictly required, it's a good practice to include it as it
 * uniquely identifies the site configuration.
 *
 * <name>: This is also not mandatory, but including it provides a
 * human-readable name for the site, which can be helpful for documentation and
 * clarity.
 *
 * <url>: This element is typically required. It specifies the location where
 * the site will be deployed. Without a valid URL, Maven won't know where to
 * publish the site.
 *
 * In summary, while you might be able to omit <id> and <name>, it's advisable
 * to include them for clarity, while <url> is generally necessary for
 * deployment.
 */

public record Site(MavenParentElement parent) {

	private static Site of(final MavenParentElement parent) {
		return new Site(parent);
	}

	public static final AddId setToUrl(final UrlScheme urlScheme, final String host) {
		return id -> name -> {

			final var createParent =

					MavenParentElement.name(Parent.SITE)//
							.addChild(Leaf.ID, id)//
							.addChild(Leaf.NAME, name)//

							.addChild(Leaf.URL, urlScheme.getValue() + host);
			return Site.of(createParent);
		};

	}

	public interface AddUrl {
		Site.AddId addUrl(final UrlScheme urlScheme, final String host);
	}

	public interface AddId {
		Site.AddName addId(final String id);
	}

	public interface AddName {
		Site addName(final String name);

	}

	public Site addChildSiteUrlInheritAppendPath(final boolean childSiteUrlInheritAppendPath) {

		return Site.of(parent.addChild(Leaf.CHILD_SITE_URL_INHERIT_APPEND_PATH,
				String.valueOf(childSiteUrlInheritAppendPath)));

	}

}
