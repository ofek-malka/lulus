package dev.ofekmalka.core.application;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.data_structure.list.List.Errors.CastumArgumentMessage;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.tools.tuple.Tuple2;
import dev.ofekmalka.tools.utils.FileUtils;

public final class XmlReader {

	private final Result<Builder> source;

	private XmlReader(final Result<Builder> source) {
		this.source = source;
	}

	private static XmlReader createBase(final Result<Builder> source) {
		return new XmlReader(source);
	}

	public static dev.ofekmalka.core.application.XmlReader.Builder.AddRootElementName from(final String fileName) {
		return Builder.builder().addFileName(fileName);
	}

	public <T> List<T> constructWithOneField(

			final Function<String, T> converter

	) {

		return source.flatMap(B -> B.generatePojoContentForOneField(converter))

				.getOrCreateFailureInstanceWithMessage(List::failureWithMessage)

		;

	}

	public <T> List<T> constructWithTwoFields(

			final Function<String, Function<String, T>> converter

	) {

		return source.flatMap(B -> B.generatePojoContentForTwoFields(converter))

				.getOrCreateFailureInstanceWithMessage(List::failureWithMessage)

		;

	}

	public <T> List<T> constructWithThreeFields(

			final Function<String, Function<String, Function<String, T>>> converter

	) {

		return source.flatMap(B -> B.generatePojoContentForThreeFields(converter))

				.getOrCreateFailureInstanceWithMessage(List::failureWithMessage)

		;

	}

	public <T> List<T> constructWithFourFields(

			final Function<String, Function<String, Function<String, Function<String, T>>>> converter

	) {

		return source.flatMap(B -> B.generatePojoContentForFourFields(converter))

				.getOrCreateFailureInstanceWithMessage(List::failureWithMessage)

		;

	}

	public <T> List<T> constructWithFiveFields(

			final Function<String, Function<String, Function<String, Function<String, Function<String, T>>>>> converter

	) {

		return source.flatMap(B -> B.generatePojoContentForFiveFields(converter))

				.getOrCreateFailureInstanceWithMessage(List::failureWithMessage)

		;

	}

	public <T> List<T> constructWithSixFields(

			final Function<String, Function<String, Function<String, Function<String, Function<String, Function<String, T>>>>>> converter

	) {

		return source.flatMap(B -> B.generatePojoContentForSixFields(converter))

				.getOrCreateFailureInstanceWithMessage(List::failureWithMessage)

		;

	}

	static final class Builder {

		// Fields that define the configuration
		private final String fileName;
		private final String rootElementName;
		private final String childElementName;
		private final List<String> grandChildrenTags;
		private final String formatPropertyName = "%s.%s[%d].%s";

		private final int numberOfObjects;

		// Constructor to initialize configuration fields
		private Builder(final String fileName, final String rootElementName, final String childElementName,
				final List<String> grandChildrenTags, final int numberOfObjects) {
			this.fileName = fileName;
			this.rootElementName = rootElementName;
			this.childElementName = childElementName;
			this.grandChildrenTags = grandChildrenTags;
			this.numberOfObjects = numberOfObjects;
		}

		public static AddFileName builder() {
			return fileName -> //
			rootElementName -> //
			childElementName -> //
			grandChildrenTags -> //
			numberOfObjects -> {

				final var processes = List.list(validateStringField("fileName", fileName), //
						validateStringField("rootElementName", rootElementName), //
						validateStringField("childElementName", childElementName), //
						validateListFields("grandChildrenTags", grandChildrenTags), //

						Result.success(numberOfObjects).validate(n -> n > 0, "numberOfObjects must be positive")

				)//

						.filter(Result::isFailure);

				return If.isItTrue(processes.isNotEmpty().successValue()).will().//
				<XmlReader>//
						returnValue(() -> {//
							final var message = processes.map(r -> r.failureValue().getMessage()).mkStr("\n")
									.successValue();
							return XmlReader.createBase(

									Result.failure(message));

						})//

						.orGet(() -> XmlReader.createBase(Result.success(new Builder(fileName, //
								rootElementName, //
								childElementName, //
								grandChildrenTags, //
								numberOfObjects))));

			};

		}

		public static Result<String> validateStringField(final String name, final String value) {
			return If.givenObject(value)//
					.isNonNull(name)//
					.andIsNot(String::isEmpty, name + " cannot be empty.")//
					.andIsNot(String::isBlank, name + " cannot contain only white space codepoints.")//
					.will().getResult();
		}

		public static Result<List<String>> validateListFields(final String name, final List<String> grandChildrenTags) {

			final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST.withArgumentName(name)//
					.getMessage();

			return If.givenObject(grandChildrenTags)//
					.isNonNull(name)//
					.andIsNot(List::isNotSuccess, errorProcessingMessage)//

					.andIsNot(l -> l.isEmpty().successValue(), name + " cannot be empty.")//
					.will().getResult();
		}

		public interface AddFileName {
			Builder.AddRootElementName addFileName(String fileName);
		}

		public interface AddRootElementName {
			Builder.AddChildElementName root(final String rootElementName);
		}

		public interface AddChildElementName {
			Builder.AddGrandChildrenTags child(final String childElementName);
		}

		public interface AddGrandChildrenTags {
			Builder.AddNumberOfObjects tags(final List<String> grandChildrenTags);
		}

		public interface AddNumberOfObjects {
			XmlReader takeValidatedUpTo(int numberOfObjects);
		}

		public <T> Result<List<T>> generatePojoContentForOneField(

				final Function<String, T> converter

		) {
			final var proccess = generatePojoContentFor(array -> //

			converter.apply(array.get(0).successValue())//

			);

			return validateCurringNumberFit(proccess, 1);

		}//

		public <T> Result<List<T>> validateCurringNumberFit(

				final Result<List<T>> r, final int number

		) {

			final var size = grandChildrenTags.size().successValue();
			return If.isItTrue(size==number//
					,
					"Not using the appropriate function choise. this use unmatches the actual number of tags which is btw: "
							+ grandChildrenTags.size().successValue())

					.will()//
					.flatMapTo(__ -> r)//
					.getResult();
		}

		public <T> Result<List<T>> generatePojoContentForTwoFields(

				final Function<String, Function<String, T>> converter

		) {

			final var proccess = generatePojoContentFor(array -> //

			converter.apply(array.get(0).successValue())//
					.apply(array.get(1).successValue())//

			);

			return validateCurringNumberFit(proccess, 2);
		}//

		public <T> Result<List<T>> generatePojoContentForThreeFields(

				final Function<String, Function<String, Function<String, T>>> converter

		) {
			final var proccess = generatePojoContentFor(array -> //

			converter.apply(array.get(0).successValue())//
					.apply(array.get(1).successValue())//
					.apply(array.get(2).successValue())//

			);

			return validateCurringNumberFit(proccess, 3);

		}//

		public <T> Result<List<T>> generatePojoContentForFourFields(

				final Function<String, Function<String, Function<String, Function<String, T>>>> converter

		) {
			final var proccess = generatePojoContentFor(array -> //

			converter.apply(array.get(0).successValue())//
					.apply(array.get(1).successValue())//
					.apply(array.get(2).successValue())//
					.apply(array.get(3).successValue())//

			);

			return validateCurringNumberFit(proccess, 4);

		}//

		public <T> Result<List<T>> generatePojoContentForFiveFields(

				final Function<String, Function<String, Function<String, Function<String, Function<String, T>>>>> converter

		) {
			final var proccess = generatePojoContentFor(array -> //

			converter.apply(array.get(0).successValue())//
					.apply(array.get(1).successValue())//
					.apply(array.get(2).successValue())//
					.apply(array.get(3).successValue())//
					.apply(array.get(4).successValue())//

			);

			return validateCurringNumberFit(proccess, 5);

		}//

		public <T> Result<List<T>> generatePojoContentForSixFields(

				final Function<String, Function<String, Function<String, Function<String, Function<String, Function<String, T>>>>>> converter

		) {
			final var proccess = generatePojoContentFor(array -> //

			converter.apply(array.get(0).successValue())//
					.apply(array.get(1).successValue())//
					.apply(array.get(2).successValue())//
					.apply(array.get(3).successValue())//
					.apply(array.get(4).successValue())//
					.apply(array.get(5).successValue())//

			);

			return validateCurringNumberFit(proccess, 6);

		}//

		private <T> Result<List<T>> generatePojoContentFor(final Function<ArrayList<Result<String>>, T> mapper) {

			return generatePropertiesFileContent()//
					.<List<T>>map(content ->

					List.extendedFactoryOperations().generateRange(0, numberOfObjects)

							.filter(isObjectValid(content))

							.map(indexOfStuff ->

							grandChildrenTags.map(grandChildTag -> PropertyReader.stringPropertyReader(content)

									.readParameterName(String.format("%s.%s[%d].%s", rootElementName, childElementName,
											indexOfStuff, grandChildTag)

									)//
									.asString()//
							)

									.filter(Result::isSuccess)//

									.convert()//

									.toJavaList(ArrayList::new)//
									.map(mapper)//

									.successValue()//
							)

					);

		}

		private Predicate<Integer> isObjectValid(final String content) {
			return indexOfStuff ->

			PropertyReader.stringPropertyReader(content)

					.readParameterName(

							String.format("%s.%s[%d].%s", rootElementName, childElementName, indexOfStuff,
									grandChildrenTags.firstElementOption().successValue()))//
					.asString().isSuccess();
		}

		private Result<String> generatePropertiesFileContent() {
			return FileUtils.readFile2String(fileName)//
					.flatMap(this::readDocument)//
					.flatMap(this::toStringList);//

		}//

		Result<List<Element>> readDocument(final String docName) {
			final var builder = new SAXBuilder();
			try {
				final var rootElement = builder.build(new StringReader(docName))//
						.getRootElement();
				final var children = rootElement//
						.getChildren(childElementName)

				;

				return If.givenObject(rootElement)//
						.is(s -> s.getName().equals(rootElementName), "root error name")
						.andIsNot(s -> children.isEmpty(),
								"rootElement tag must contain at least one child tag. check if the childElementName is actually '"
										+ childElementName + "'")

						.andIs(s ->

						List.extendedFactoryOperations().createFromCollection(children).map(Element::getName)
								.allEqualTo(childElementName).successValue()

								,
								"childElementNames are not the same as should be the only name: '" + childElementName
										+ "'")

						.andIs(s -> List.extendedFactoryOperations().generateRange(0, children.size())//

								.map(i -> //

								List.extendedFactoryOperations().createFromCollection(children.get(i).getChildren())//

										.map(Element::getName)//

										.convert().toSet()//

										.equals(grandChildrenTags.convert().toSet())//

								).allEqualTo(true).successValue(),
								"All the grandChildrenTags you provided are mandatory in the file. Please check if you missed one of them or if there was simply a spelling mistake.")
						.will()//
						.returnValue(() -> List.extendedFactoryOperations().createFromCollection(children))

						.getResult();
			} catch (IOException | JDOMException io) {
				return Result.failure(
						String.format("Invalid XML data or root element name '%s': %s", rootElementName, docName), io);
			} catch (final Exception e) {
				return Result.failure(String.format("Unexpected error while reading XML data: %s", docName), e);
			}
		}

		Result<String> toStringList(final List<Element> list) {
			final Function<Tuple2<Element, Integer>, List<String>> elementTransformer = //
					element -> grandChildrenTags.map(namedChildElement -> processElement(element, namedChildElement));

			return list.zipWithPosition()//
					.combineNestedLists(elementTransformer)//
					.mkStr("\n");
		}

		String processElement(final Tuple2<Element, Integer> element, final String namedChildElement) {
			final var index = element.value();
			final var currentElement = element.state();

			final var propertyKey = String.format(formatPropertyName, rootElementName, childElementName, index,
					namedChildElement);

			final var childValue = currentElement.getChildText(namedChildElement);

			return String.format("%s=%s", propertyKey, childValue);
		}

	}

	public static class Errors {

		public enum ErrorMessage {
			UNMATCHED_CURRY_ARGUMENTS("Not using the appropriate function choice.\n"
					+ "This use unmatches the actual number of tags which is: %d");

			private final String template;

			ErrorMessage(final String template) {
				this.template = template;
			}

			public String format(final Object... args) {
				return String.format(template, args);
			}
		}

	}

}
