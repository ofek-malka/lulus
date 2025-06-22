package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.ActivationFile.Builder.AddMissing;

public record ActivationFile(MavenParentElement parent) {

	public static AddMissing addExists(final String exists) {
		return Builder.builder().addExists(exists);

	}

	public static class Builder {

		public static AddExists builder() {
			return exists -> missing -> {

				final var createMavenParentElement = //
						MavenParentElement.name(Parent.ACTIVATION_FILE)//
								.addChild(Leaf.EXISTS, exists)//
								.addChild(Leaf.MISSING, missing);//
				return new ActivationFile(createMavenParentElement);

			};

		}

		public interface AddExists {
			Builder.AddMissing addExists(final String exists);
		}

		public interface AddMissing {
			ActivationFile addMissing(final String missing);

		}

	}

}
