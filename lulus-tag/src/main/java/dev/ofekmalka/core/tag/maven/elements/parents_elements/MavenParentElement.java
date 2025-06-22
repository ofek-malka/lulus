package dev.ofekmalka.core.tag.maven.elements.parents_elements;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.tag.maven.MavenXmlTags;
import dev.ofekmalka.tools.tuple.Tuple2;

public class MavenParentElement {

	private final MavenXmlTags.Parent name;

	private final List<Tuple2<String, String>> parent;

	public MavenParentElement(final MavenXmlTags.Parent name, final List<Tuple2<String, String>> parent) {
		this.name = name;
		this.parent = parent;
	}

	public static MavenParentElement name(final MavenXmlTags.Parent name) {
		return new MavenParentElement(name, List.emptyList());
	}

	public MavenParentElement changeNameTo(final MavenXmlTags.Parent nameAlternative) {
		return new MavenParentElement(nameAlternative, parent);
	}

	public MavenParentElement addChild(final String tagName, final String value) {
		return new MavenParentElement(name, parent.addElement(
				Tuple2.of(tagName, Result.of(value).getOrElse("<!-- missing value -->")).getResult().successValue()));
	}

	public MavenParentElement addChild(final MavenXmlTags.Leaf le, final String value) {
		return new MavenParentElement(name, parent.addElement(Tuple2
				.of(le.getTagName(), Result.of(value).getOrElse("<!-- missing value -->")).getResult().successValue()));
	}

	public MavenParentElement addChild(final MavenXmlTags.Parent le, final String value) {
		return new MavenParentElement(name, parent.addElement(Tuple2
				.of(le.getTagName(), Result.of(value).getOrElse("<!-- missing value -->")).getResult().successValue()));
	}

	public String getName() {
		return name.getTagName();
	}

	public String toMaven() {
		return parent.map(t -> "<" + t.state() + ">" + t.value() + "</" + t.state() + ">").mkStr("\n")

				.map(s -> "<" + getName() + ">\n" + s + "\n</" + getName() + ">").successValue()

		;
	}

	public String getContent() {
		return parent.map(t -> "<" + t.state() + ">" + t.value() + "</" + t.state() + ">").mkStr("\n")

				.successValue()

		;
	}

}
