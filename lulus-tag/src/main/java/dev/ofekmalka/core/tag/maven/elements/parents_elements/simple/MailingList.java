package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.leaf_elements.LeftElements.OtherArchive;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;

public record MailingList(MavenParentElement parent) {

	public static final MailingList.AddPost ofName(final String name) {
		return MailingList.builder().addName(name);
	}

	static MailingList.AddName builder() {
		return name -> post -> subscribe -> unsubscribe -> archive -> {

			final var createParent =

					MavenParentElement.name(Parent.MAILINGLIST)//
							.addChild(Leaf.NAME, name)//

							.addChild(Leaf.POST, post)//
							.addChild(Leaf.SUBSCRIBE, subscribe)//
							.addChild(Leaf.UNSUBSCRIBE, unsubscribe)

							.addChild(Leaf.ARCHIVE, archive);//

			return new MailingList(createParent);
		};//
	}

	public interface AddName {
		MailingList.AddPost addName(final String name);

	}

	public interface AddPost {
		MailingList.AddSubscribe addPost(final String post);

	}

	public interface AddSubscribe {
		MailingList.AddUnsubscribe addSubscribe(final String subscribe);

	}

	public interface AddUnsubscribe {
		MailingList.AddArchive addUnsubscribe(final String unsubscribe);

	}

	public interface AddArchive {
		MailingList addArchive(final String archive);

	}

	public MailingList addOtherArchives(final OtherArchive... otherArchives) {
		final var value = List.list(otherArchives)//
				.map(OtherArchive::toMaven)//
				.mkStr("\n")//
				.getOrElse("error in addExclusions");//
		return new MailingList(parent.addChild(Parent.OTHER_ARCHIVES, value));

	}

}
