package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin.Plugin;

public record PluginManagement(MavenParentElement parent) {

	public static PluginManagement addAtleastOnePlugin(final Plugin... plugins) {

		final var value = List.list(plugins)//
				.map(plugin -> plugin.parent().toMaven())//
				.mkStr("\n")//
				.getOrElse("error in addAtleastOnePlugin");//

		final var createParent =

				MavenParentElement.name(Parent.PLUGIN_MANAGEMENT)

						.addChild(Parent.PLUGINS, value);

		return new PluginManagement(createParent);
	}

}
