package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.EmailSuffix;
import dev.ofekmalka.core.tag.maven.elements.enums.TimezoneUtil;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

interface CreationContributor {
	Contributor addRoles(final LeftElements.Role... roles);

	Contributor addEmail(final String emailPrefix, final EmailSuffix emailSuffix);

	Contributor addOrganization(final String organization);

	Contributor addOrganizationUrl(final UrlScheme urlScheme, final String organizationUrlhost);

	Contributor addTimezone(final TimezoneUtil timezone);

	Contributor addUrl(final UrlScheme urlScheme, final String host);

	Contributor addProperties(final Properties properties);

}

public record Contributor(MavenParentElement parent) implements CreationContributor {

	private static Contributor of(final MavenParentElement parent) {
		return new Contributor(parent);
	}

	public static Contributor ofName(final String name) {
		final var createParent =

				MavenParentElement.name(Parent.CONTRIBUTOR).addChild(Leaf.NAME, name);
		return Contributor.of(createParent);
	}

	@Override
	public Contributor addRoles(final LeftElements.Role... roles) {

		final var value = List.list(roles)//
				.map(LeftElements.Role::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addExclusions");//

		final var createParent = parent.

				addChild(Parent.ROLES, value);

		return Contributor.of(createParent);

	}

	@Override
	public Contributor addEmail(final String emailPrefix, final EmailSuffix emailSuffix) {

		final var createParent = parent.addChild(Leaf.EMAIL, emailPrefix + emailSuffix.getValue());

		return Contributor.of(createParent);
	}

	@Override
	public Contributor addOrganization(final String organization) {

		final var createParent = parent.addChild(Leaf.ORGANIZATION, organization);

		return Contributor.of(createParent);
	}

	@Override
	public Contributor addOrganizationUrl(final UrlScheme urlScheme, final String organizationUrlhost) {

		final var createParent = parent.addChild(Leaf.EMAIL, urlScheme.getValue() + organizationUrlhost);

		return Contributor.of(createParent);
	}

	@Override
	public Contributor addTimezone(final TimezoneUtil timezone) {

		final var result = parent.addChild(Leaf.TIMEZONE, timezone.toString());
		return Contributor.of(result);
	}

	@Override
	public Contributor addUrl(final UrlScheme urlScheme, final String host) {

		final var result = parent.addChild(Leaf.URL, urlScheme.getValue() + host);
		return Contributor.of(result);

	}

	@Override
	public Contributor addProperties(final Properties properties) {
		return Contributor.of(parent.addChild(Parent.PROPERTIES, properties.parent().getContent()));
	}

}
