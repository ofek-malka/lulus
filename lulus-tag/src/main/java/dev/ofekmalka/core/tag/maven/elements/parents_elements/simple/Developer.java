package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.EmailSuffix;
import dev.ofekmalka.core.tag.maven.elements.enums.TimezoneUtil;
import dev.ofekmalka.core.tag.maven.elements.enums.UrlScheme;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

interface CreationDeveloper {
	Developer addRoles(final LeftElements.Role... otherRoles);

	Developer addEmail(final String emailPrefix, final EmailSuffix emailSuffix);

	Developer addName(final String name);

	Developer addOrganization(final String organization);

	Developer addOrganizationUrl(final UrlScheme urlScheme, final String organizationUrlhost);

	Developer addTimezone(final TimezoneUtil timezone);

	Developer addUrl(final UrlScheme urlScheme, final String host);

	Developer addProperties(final Properties properties);

}

public record Developer(MavenParentElement parent) implements CreationDeveloper {

	private static Developer of(final MavenParentElement parent) {
		return new Developer(parent);
	}

	public static Developer ofId(final String id) {
		final var createParent =

				MavenParentElement.name(Parent.DEVELOPER).addChild(Leaf.ID, id);
		return Developer.of(createParent);
	}

	@Override
	public Developer addName(final String name) {
		final var createParent =

				parent.addChild(Leaf.NAME, name);
		return Developer.of(createParent);
	}

	@Override
	public Developer addRoles(final LeftElements.Role... roles) {

		final var value = List.list(roles)//
				.map(LeftElements.Role::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addExclusions");//

		final var createParent = parent.

				addChild(Parent.ROLES, value);

		return Developer.of(createParent);

	}

	@Override
	public Developer addEmail(final String emailPrefix, final EmailSuffix emailSuffix) {

		final var createParent = parent.addChild(Leaf.EMAIL, emailPrefix + emailSuffix.getValue());

		return Developer.of(createParent);
	}

	@Override
	public Developer addOrganization(final String organization) {

		final var createParent = parent.addChild(Leaf.ORGANIZATION, organization);

		return Developer.of(createParent);
	}

	@Override
	public Developer addOrganizationUrl(final UrlScheme urlScheme, final String organizationUrlhost) {

		final var createParent = parent.addChild(Leaf.EMAIL, urlScheme.getValue() + organizationUrlhost);

		return Developer.of(createParent);
	}

	@Override
	public Developer addTimezone(final TimezoneUtil timezone) {

		final var result = parent.addChild(Leaf.TIMEZONE, timezone.toString());
		return Developer.of(result);
	}

	@Override
	public Developer addUrl(final UrlScheme urlScheme, final String host) {

		final var result = parent.addChild(Leaf.URL, urlScheme.getValue() + host);
		return Developer.of(result);

	}

	@Override
	public Developer addProperties(final Properties properties) {
		return Developer.of(parent.addChild(Parent.PROPERTIES, properties.parent().getContent()));
	}

}
