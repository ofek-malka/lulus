package dev.ofekmalka.core.application;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;
import java.util.Properties;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;

interface ConfigReader {

	Result<String> asString();

	<T extends Enum<?>> Result<T> asEnum(Class<T> enumClass);

	<T> List<T> asList(Function<PropertyReader, Result<T>> mapper);

	<T> Result<T> asType(Function<PropertyReader, Result<T>> mapper);

}

public final class PropertyReader {

	private final Result<Properties> properties;
	private final String source;

	private PropertyReader(final Result<Properties> properties, final String source) {
		this.properties = properties;
		this.source = source;
	}

	private enum OriginSource {
		FILE_NAME("fileName"), PROP_STRING("propString");

		private String origin;

		OriginSource(final String origin) {
			this.origin = origin;
		}

		public String getOrigin() {
			return origin;
		}

	}

	public static PropertyReader filePropertyReader(final String fileName) {
		return new PropertyReader(readFile2String(//

				OriginSource.FILE_NAME, //

				fileName, PropertyReader::readPropertiesFromFile), //
				String.format("File: %s", fileName));//
	}

	public static PropertyReader stringPropertyReader(final String propString) {
		return new PropertyReader(readFile2String(OriginSource.PROP_STRING, propString, //
				PropertyReader::readPropertiesFromString), //
				String.format("String: %s", propString));//
	}

	private static Result<Properties> readFile2String(final OriginSource originSource, final String path,
			final Function<String, Result<Properties>> m) {
		return If.givenObject(path)//
				.isNonNull(originSource.getOrigin())//
				.andIsNot(String::isEmpty, "The " + originSource.getOrigin() + " cannot be empty.")//
				.andIsNot(String::isBlank,
						"The " + originSource.getOrigin() + " cannot contains only white space codepoints.")

				.will()//
				.flatMapTo(__ -> m.apply(path))//
				.getResult();
	}

	private static Result<Properties> readPropertiesFromFile(final String configFileName) {
		try (var inputStream = PropertyReader.class.getClassLoader().getResourceAsStream(configFileName)) {
			final var properties = new Properties();
			properties.load(inputStream);
			return Result.of(properties);

		} catch (final NullPointerException e) {
			return Result.failure(String.format("File %s not found in classpath", configFileName));
		} catch (final IOException e) {
			return Result.failure(String.format("IOException reading classpath resource %s", configFileName));
		} catch (final Exception e) {
			return Result.failure(String.format("Exception reading classpath resource %s", configFileName), e);
		}

	}

	private static Result<Properties> readPropertiesFromString(final String propString) {
		try (Reader reader = new StringReader(propString)) {
			final var properties = new Properties();
			properties.load(reader);
			return Result.of(properties);
		} catch (final Exception e) {
			return Result.failure(String.format("Exception reading property string %s", propString), e);
		}
	}

	public GetAs readParameterName(final String propertyName) {
		return new GetAs(properties, propertyName);
	}

	public class GetAs implements ConfigReader {
		private final Result<Properties> properties;
		final String propertyName;

		private GetAs(final Result<Properties> properties, final String propertyName) {
			this.properties = properties;
			this.propertyName = propertyName;
		}

		@Override
		public Result<String> asString() {
			return properties.flatMap(props -> getProperty(props, propertyName));
		}

		@Override
		public <T> Result<T> asType(final Function<PropertyReader, Result<T>> mapper) {
			return

			asString()//
					.map(GetAs::toPropertyString)//

					.map(PropertyReader::stringPropertyReader)//
					.flatMap(mapper);//
		}

		@Override
		public <T> List<T> asList(final Function<PropertyReader, Result<T>> mapper) {
			return

			this.getAsList(i -> i)//

					.sequence(s -> //

					{//

						final var propertyString = GetAs.toPropertyString(s);//
						return mapper.apply(PropertyReader.stringPropertyReader(propertyString));//
					});
		}

		@Override
		public <T extends Enum<?>> Result<T> asEnum(final Class<T> enumClass) {

			final Function<String, Result<T>> f = t -> {
				try {
					final var constant = enumClass.getEnumConstants()[0];
					@SuppressWarnings("unchecked")
					final var value = (T) Enum.valueOf(constant.getClass(), t.toUpperCase());
					return Result.success(value);
				} catch (final Exception e) {
					return Result.failure(
							String.format("Parsing property /'%s/': value of property %s can't be parsed to %s.", t,
									propertyName, enumClass.getName()));
				}
			};

			return asString()//
					.flatMap(s -> {
						try {
							return f.apply(s);
						} catch (final Exception e) {
							return Result.failure(
									String.format("Invalid value while parsing property %s: %s", propertyName, s));
						}
					});
		}

		private <T> List<T> getAsList(final Function<String, T> f) {
			return asString()//

					.flatMap(s -> {
						try {
							final var elements = s.split(";");
							return List.list(elements).map(f).getListResult();
						} catch (final NumberFormatException e) {
							return Result.failure(
									String.format("Invalid value while parsing property %s: %s", propertyName, s));
						}
					})

					.getOrCreateFailureInstanceWithMessage(List::failureWithMessage)

			;
		}

		private Result<String> getProperty(final Properties properties, final String propertyName) {

			return If.givenObject(properties.getProperty(propertyName))
					.is(Objects::nonNull, String.format("Property \"%s\" no found in %s", propertyName, source))
					.andIsNot(String::isBlank, "Property \"" + propertyName + "\" must not be empty or blank")//
					.will()//
					.getResult();

		}

		private static String toPropertyString(final String propertyName) {
			return propertyName.replace(",", "\n");
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
