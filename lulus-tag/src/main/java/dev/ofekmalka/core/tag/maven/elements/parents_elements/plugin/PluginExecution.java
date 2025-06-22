package dev.ofekmalka.core.tag.maven.elements.parents_elements.plugin;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.enums.MavenPhase;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Goal;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Configuration;

public record PluginExecution(MavenParentElement parent) implements ConfigurationContainer<PluginExecution> {

	private static PluginExecution of(final MavenParentElement parent) {

		return new PluginExecution(parent);
	}

	public static final AddPhase addId(final String id) {
		return phase -> goals -> {//

			final var value = List.list(goals)//
					.map(Goal::toMaven)//
					.mkStr("\n")//
					.getOrElse("error in addGoals");//

			final var createParent =

					MavenParentElement.name(Parent.PLUGIN_EXECUTION)//
							.addChild(Leaf.ID, id)//
							.addChild(Leaf.PHASE, phase.getPhase())//

							.addChild(Parent.GOALS, value)//
			;
			return PluginExecution.of(createParent);

		};//
	}

	public interface AddId {
		PluginExecution.AddPhase addId(final String id);
	}

	public interface AddPhase {
		PluginExecution.AddGoals addPhase(final MavenPhase phase);

	}

	public interface AddGoals {
		PluginExecution addGoals(final Goal... goals);
	}

	@Override
	public PluginExecution addConfiguration(final Configuration configuration) {
		final var createParent = parent.addChild(Parent.CONFIGURATION, configuration.parent().getContent());
		return PluginExecution.of(createParent);

	}

	@Override
	public PluginExecution addInherited(final boolean inherited) {
		return PluginExecution.of(parent.addChild(Leaf.INHERITED, String.valueOf(inherited)));

	}

}
