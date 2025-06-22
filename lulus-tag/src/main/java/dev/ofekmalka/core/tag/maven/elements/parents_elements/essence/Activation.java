package dev.ofekmalka.core.tag.maven.elements.parents_elements.essence;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.JdkVersion;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.ActivationFile;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.ActivationOS;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.ActivationProperty;

public record Activation(MavenParentElement parent) {

	private static Activation of(final MavenParentElement parent) {
		return new Activation(parent);
	}

	public static Activation isSetToActiveByDefault(final boolean activeByDefault) {

		return new Activation(MavenParentElement.name(Parent.ACTIVATION).addChild(Leaf.ACTIVE_BY_DEFAULT,
				String.valueOf(activeByDefault)));

	}

	public static Activation isSetToJdkVersion(final JdkVersion version) {

		return new Activation(MavenParentElement.name(Parent.ACTIVATION).addChild(Leaf.VERSION, version.getVersion()));

	}

	public static Activation isSetToFile(final ActivationFile file) {
		// return Activation.of(ParentElement.ACTIVATION.addChild("file", file));

		return new Activation(MavenParentElement.name(Parent.ACTIVATION).addChild(file.parent().getName(),
				file.parent().getContent()));

	}

	public static Activation isSetToOs(final ActivationOS os) {
		return new Activation(
				MavenParentElement.name(Parent.ACTIVATION).addChild(os.parent().getName(), os.parent().getContent()));

	}

	public static Activation isSetToProperty(final ActivationProperty property) {
		return new Activation(MavenParentElement.name(Parent.ACTIVATION).addChild(property.parent().getName(),
				property.parent().getContent()));

	}

	public Activation addActiveByDefault(final boolean activeByDefault) {

		return Activation.of(parent.addChild(Leaf.ACTIVE_BY_DEFAULT, String.valueOf(activeByDefault)));

	}

	public Activation addJdk(final JdkVersion version) {
		return Activation.of(parent.addChild(Leaf.VERSION, version.getVersion()));
	}

	public Activation addFile(final ActivationFile file) {
		return Activation.of(parent.addChild(file.parent().getName(), file.parent().getContent()));
	}

	public Activation addOs(final ActivationOS os) {
		return Activation.of(parent.addChild(os.parent().getName(), os.parent().getContent()));

	}

	public Activation addProperty(final ActivationProperty property) {
		return Activation.of(parent.addChild(property.parent().getName(), property.parent().getContent()));

	}

}
