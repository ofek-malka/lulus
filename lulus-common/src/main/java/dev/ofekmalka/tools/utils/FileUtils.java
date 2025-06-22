package dev.ofekmalka.tools.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;

public class FileUtils {

	/**
	 * Reads a file from the resources folder and returns its content as a string.
	 *
	 * @param path The path to the file in the classpath resources.
	 * @return A Result object containing the file content or an error message.
	 */
	public static Result<String> readFile2String(final String path) {
		return If.givenObject(path)//
				.isNonNull("The file path")//
				.andIsNot(String::isEmpty, "The file path cannot be empty.")//
				.andIsNot(String::isBlank, "The file path cannot contains only white space codepoints.")

				.will()//
				.flatMapTo(FileUtils::readFileContent)//
				.getResult();
	}

	private static Result<String> readFileContent(final String path) {
		// Attempt to read the file from resources
		try (var inputStream = FileUtils.class.getClassLoader().getResourceAsStream(path)) {
			return If.givenObject(inputStream)//
					.is(Objects::nonNull, "Resource '" + path + "' not found in classpath.")//
					.will()//
					.flatMapTo(inputS -> {
						try (var scanner = new Scanner(inputS, StandardCharsets.UTF_8)) {
							if (scanner.useDelimiter("\\A").hasNext())
								return Result.success(scanner.next());
							return Result.failure("empty content is invalid"); //

						}
					}).getResult();
		} catch (final IOException e) {
			return Result.failure("IO error while reading file: " + path, e);
		} catch (final SecurityException e) {
			return Result.failure("Security violation while accessing file: " + path, e);
		} catch (final Exception e) {
			return Result.failure("Unexpected error while reading file: " + path, e);
		}
	}
}
