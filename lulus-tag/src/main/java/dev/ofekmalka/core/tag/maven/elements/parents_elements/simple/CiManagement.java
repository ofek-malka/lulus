package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.Property;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record CiManagement(MavenParentElement parent) {

	public static final Builder.AddUrl ofSystem(final String system) {
		return Builder.builder().addSystem(system);
	}

	public static final class Builder {

		public static AddSystem builder() {
			return system -> url -> notifiers -> {
				final var createParent =

						MavenParentElement

								.name(Parent.CI_MANAGEMENT)//
								.addChild(Leaf.SYSTEM, system)//
								.addChild(Leaf.URL, url)//
								.addChild(Parent.NOTIFIERS,

										List.list(notifiers)//
												.map(notifier -> notifier.parent().toMaven())//
												.mkStr("\n")//
												.getOrElse("error in AddNotifiers")

								);

				return new CiManagement(createParent);
			};

		}

		public interface AddSystem {
			Builder.AddUrl addSystem(String system);
		}

		public interface AddUrl {
			Builder.AddNotifiers addUrl(final String url);

		}

		public interface AddNotifiers {
			CiManagement addNotifiers(final Notifier... notifiers);
		}

	}

	public static void main(final String[] args) {

		// final Notifier ddd = null;
		final var t = CiManagement.ofSystem("ddd")//
				.addUrl("dd")//
				.addNotifiers(Notifier.addType("s")//
						.addAddress("s")//
						.isSendOnError(false)//
						.isSendOnFailure(false)//
						.isSendOnSuccess(false)//
						.isSendOnWarning(false)//
						.addConfiguration(Configuration.isSetTo(Property.isSetTo("w").addValue("e")))//

				);

		System.out.println(t.parent.toMaven());
	}

}
