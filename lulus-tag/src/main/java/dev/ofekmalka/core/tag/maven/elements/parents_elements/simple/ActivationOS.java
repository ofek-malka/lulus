package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record ActivationOS(MavenParentElement parent) {

	public static ActivationOS ofArch(final String arch) {
		return new ActivationOS(MavenParentElement.name(Parent.ACTIVATION_OS).addChild(Leaf.ARCH, arch));

	}

	public static ActivationOS ofFamily(final String family) {
		return new ActivationOS(MavenParentElement.name(Parent.ACTIVATION_OS).addChild(Leaf.FAMILY, family));

	}

	public static ActivationOS ofVersion(final String version) {
		return new ActivationOS(MavenParentElement.name(Parent.ACTIVATION_OS).addChild(Leaf.VERSION, version));

	}

	public static ActivationOS ofName(final String name) {
		return new ActivationOS(MavenParentElement.name(Parent.ACTIVATION_OS).addChild(Leaf.NAME, name));

	}

	public ActivationOS addArch(final String arch) {
		return new ActivationOS(parent.addChild(Leaf.ARCH, arch));

	}

	public ActivationOS addFamily(final String family) {
		return new ActivationOS(parent.addChild(Leaf.FAMILY, family));

	}

	public ActivationOS addVersion(final String version) {
		return new ActivationOS(parent.addChild(Leaf.VERSION, version));

	}

	public ActivationOS addName(final String name) {
		return new ActivationOS(parent.addChild(Leaf.NAME, name));

	}

}
