package dev.ofekmalka.core.data_structure.grid;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.grid.Grid.GridCoordinate;
import dev.ofekmalka.core.data_structure.grid.Grid.GridRegion;
import dev.ofekmalka.core.data_structure.grid.Grid.ValidatedDimension;
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.support.TestMulHelper;
import dev.ofekmalka.support.annotation.ArgumentName;
import dev.ofekmalka.support.annotation.CustomDisplayNameGenerator;
import dev.ofekmalka.support.annotation.TwoArgumentNames;
import dev.ofekmalka.support.general.providers.ArgumentNameProvider;
import dev.ofekmalka.support.general.providers.TwoArgumentNamesProvider;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SoftAssertionsExtension.class)
public class UsingGridOperations {

	@Nested
	class Invalid {
		final Grid<Integer> EMPTY_GRID_2_X_2 = Grid.<Integer>//
				withHeight(ValidatedDimension.of(2).successValue())//
				.withWidth(ValidatedDimension.of(2).successValue())

				.defaultUnassigned()//
		;

		final GridCoordinate GRID_POINT = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();

		@Nested
		class Basic implements GridTestBehavior.Operations.invalid.Basic {

			@Override
			@ParameterizedTest
			@ArgumentsSource(TwoArgumentNamesProvider.class)
			@TwoArgumentNames(methodName = "setAt", //
					firstArgumentName = "gridCoordinate", secondArgumentName = "value")

			public void setAt(final String methodName, final String firstArgumentName, final String secondArgumentName,
					final BDDSoftAssertions softly) {

				final var pointInBounds = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var pointOutsideBounds = GridCoordinate.withRowIndex(10).withColumnIndex(10).successValue();

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(EMPTY_GRID_2_X_2)//
						.<GridCoordinate, Integer>//
						givenTwoArguments(null, 3)//
						.givenTwoArguments(pointInBounds, null)

						.givenTwoArguments(pointOutsideBounds, 5)

						.performActionResult(gridCoordinate -> value -> grid -> //
						grid.setAt(gridCoordinate, value).asResult())//
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(firstArgumentName).trackAndFinalize(methodName))//
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(secondArgumentName).trackAndFinalize(methodName))

						.thenShouldHaveSameErrorMessage(
								Grid.Errors.GeneralMessage.ERROR_GRID_COORDINATE_IS_NOT_IN_BOUNDS
										.trackAndFinalize(methodName));//

			}

			@Override
			@ParameterizedTest
			@ArgumentsSource(ArgumentNameProvider.class)
			@ArgumentName(methodName = "getAt", argumentName = "gridCoordinate")
			public void getAt(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var pointOutsideBounds = GridCoordinate.withRowIndex(10).withColumnIndex(10).successValue();

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(EMPTY_GRID_2_X_2)//
						.<GridCoordinate>//

						givenNullArgument().givenSingleArgument(pointOutsideBounds)
						.performActionResult(gridCoordinate -> grid -> //
						grid.getAt(gridCoordinate))//
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//

						.thenShouldHaveSameErrorMessage(
								Grid.Errors.GeneralMessage.ERROR_GRID_COORDINATE_IS_NOT_IN_BOUNDS
										.trackAndFinalize(methodName));//

			}

			@Override
			@ParameterizedTest
			@ArgumentsSource(ArgumentNameProvider.class)
			@ArgumentName(methodName = "subGrid", argumentName = "gridRegion")
			public void subGrid(final String methodName, final String argumentName, final BDDSoftAssertions softly) {
				final var pointOutsideBounds = GridCoordinate.withRowIndex(10).withColumnIndex(10).successValue();

				final var pointInBounds = GridCoordinate.withRowIndex(10).withColumnIndex(10).successValue();
				final var invalidGridRegion = GridRegion.withTopLeft(pointInBounds).withBottomRight(pointOutsideBounds)
						.successValue();

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(EMPTY_GRID_2_X_2)//
						.<GridRegion>//

						givenNullArgument().givenSingleArgument(invalidGridRegion)
						.performActionResult(gridCoordinate -> grid -> //
						grid.subGrid(gridCoordinate).asResult())//
						.thenShouldHaveSameErrorMessage(
								NullValueMessages.argument(argumentName).trackAndFinalize(methodName))//

						.thenShouldHaveSameErrorMessage(
								Grid.Errors.GeneralMessage.ERROR_GRID_COORDINATE_IS_NOT_IN_BOUNDS
										.trackAndFinalize(methodName));//

			}

		}

		@Nested
		class Queries implements GridTestBehavior.Operations.invalid.Queries {

			@Override
			@ParameterizedTest
			@ArgumentsSource(ArgumentNameProvider.class)
			@ArgumentName(methodName = "isInBounds", argumentName = "gridCoordinate")
			public void isInBounds(final String methodName, final String argumentName) {
				final var actualErrorMessage = EMPTY_GRID_2_X_2.isInBounds(null).failureValue().getMessage();

				final var expectedErrorMessage = NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
				assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);

			}

		}

		@Nested
		class Transformations implements GridTestBehavior.Operations.invalid.Transformations {

			@Override
			@ParameterizedTest
			@ArgumentsSource(ArgumentNameProvider.class)
			@ArgumentName(methodName = "map", argumentName = "mapper")
			public void map(final String methodName, final String argumentName) {
				final var actualErrorMessage = EMPTY_GRID_2_X_2.map(null).asResult().failureValue().getMessage();

				final var expectedErrorMessage = NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
				assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);

			}

			@Override
			@ParameterizedTest
			@ArgumentsSource(ArgumentNameProvider.class)
			@ArgumentName(methodName = "mapWithPosition", argumentName = "mapper")
			public void mapWithPosition(final String methodName, final String argumentName) {
				final var actualErrorMessage = EMPTY_GRID_2_X_2.mapWithPosition(null).asResult().failureValue()
						.getMessage();

				final var expectedErrorMessage = NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
				assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);

			}

			@Override
			@ParameterizedTest
			@ArgumentsSource(ArgumentNameProvider.class)
			@ArgumentName(methodName = "filter", argumentName = "predicate")
			public void filter(final String methodName, final String argumentName) {
				final var actualErrorMessage = EMPTY_GRID_2_X_2.filter(null).asResult().failureValue().getMessage();

				final var expectedErrorMessage = NullValueMessages.argument(argumentName).trackAndFinalize(methodName);
				assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);

			}

		}

	}

	@Nested
	class ShouldSuccessfully {

		@Nested
		class DuringCreation {

			@Test
			public void shouldSuccessfullyCreateAnEmptyGrid() {
				final var validFive = ValidatedDimension.of(5).successValue();
				final var validTwo = ValidatedDimension.of(2).successValue();
				final var firstInstance = Grid.<Integer>withHeight(validFive)//
						.withWidth(validTwo)//
						.defaultUnassigned();

				assertThat(firstInstance.asResult())//
						.as("Empty grid should successfully be created")//
						.matches(Result::isSuccess);
			}

		}

		@Nested
		class Basic implements GridTestBehavior.Operations.Basic {

			final GridCoordinate GRID_POINT = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();

			@Override
			@Test
			public void size(final BDDSoftAssertions softly) {

				final var gridContainsFourElements = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())

						.defaultUnassigned()//
				;

				final var gridContainsSixElements = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(3).successValue())

						.defaultUnassigned()//
				;

				final var gridContainsTwelveElements = Grid.<Integer>//
						withHeight(ValidatedDimension.of(3).successValue())//
						.withWidth(ValidatedDimension.of(4).successValue())

						.defaultUnassigned()//
				;

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCases(gridContainsFourElements,

								gridContainsSixElements,

								gridContainsTwelveElements//
						)//

						.withoutAnyArgument()//
						.performActionResult(Grid::sizeOnlySuccessfulValues)//
						.thenShouldBeEqualTo(0)//
						.thenShouldBeEqualTo(0)//
						.thenShouldBeEqualTo(0);//

			}

			@Override
			@Test
			public void setAt(final BDDSoftAssertions softly) {

				final var EMPTY_GRID_2_X_2 = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())

						.defaultUnassigned()//
				;

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(EMPTY_GRID_2_X_2)//

						.<GridCoordinate, Integer>//
						givenTwoArguments(GRID_POINT, 3)//
						.givenTwoArguments(GRID_POINT, 4)

						.performActionResult(gridCoordinate -> value -> grid -> //
						grid.setAt(gridCoordinate, value).getAt(gridCoordinate))//
						.thenShouldBeEqualTo(3)//
						.thenShouldBeEqualTo(4);//

			}

			@Override
			@Test
			public void getAt(final BDDSoftAssertions softly) {
				// same test as above

			}

			// TODO NEED FIX THE ISSUE WHY IT'S NOT PASS without .indexedCells() ot toString
			@Override
			@Test
			public void subGrid(final BDDSoftAssertions softly) {

				final var topLeft = GridCoordinate//
						.withRowIndex(0)//
						.withColumnIndex(0)//
						.successValue();
				final var bottomLeft = GridCoordinate//
						.withRowIndex(1)//
						.withColumnIndex(0)//
						.successValue();//
				final var bottomRight = GridCoordinate//
						.withRowIndex(0)//
						.withColumnIndex(1)//
						.successValue();//

				final var topRight = GridCoordinate//
						.withRowIndex(1)//
						.withColumnIndex(1)//
						.successValue();//

				final var GRID_2_X_2 = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())

						.defaultUnassigned()//
						.setAt(topLeft, 1)//
						.setAt(bottomLeft, 2)//
						.setAt(topRight, 3)//
						.setAt(bottomRight, 4)

				;

				final var gr = GridRegion//
						.withTopLeft(topLeft)//
						.withBottomRight(bottomLeft)//
						.successValue();

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(GRID_2_X_2)//
						.<GridRegion, GridCoordinate>//
						givenTwoArguments(gr, topLeft)//
						.givenTwoArguments(gr, bottomLeft)//
						.givenTwoArguments(gr, topRight)//
						.givenTwoArguments(gr, bottomRight)//
						.performActionResult(gridRegion -> gridCoordinate -> grid -> //

						grid.subGrid(gridRegion)

								.indexedCells().getListResult())//
						.thenShouldBeEqualTo(Grid.<Integer>//
								withHeight(ValidatedDimension.of(2).successValue())//
								.withWidth(ValidatedDimension.of(1).successValue())

								.defaultUnassigned()//
								.setAt(topLeft, 1)//
								.setAt(bottomLeft, 2).indexedCells()//
						)

				;//

			}

		}

		@Nested
		class Queries implements GridTestBehavior.Operations.Queries {

			@Override
			@Test
			public void isEmpty(final BDDSoftAssertions softly) {
				final var emptyGrid = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())

						.defaultUnassigned()//
				;

				final var topLeft = GridCoordinate//
						.withRowIndex(0)//
						.withColumnIndex(0)//
						.successValue();
				final var nonEmptyGrid = Grid.<Integer>//
						withHeight(ValidatedDimension.of(3).successValue())//
						.withWidth(ValidatedDimension.of(3).successValue())

						.defaultUnassigned()//
						.setAt(topLeft, 1);

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCases(emptyGrid,

								nonEmptyGrid

						)//

						.withoutAnyArgument().performActionResult(Grid::isEmpty)//
						.thenShouldBeEqualTo(true)//
						.thenShouldBeEqualTo(false)//
				;//

			}

			@Override
			@Test
			public void countRows(final BDDSoftAssertions softly) {
				final var gridWithTwoRows = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(3).successValue())

						.defaultUnassigned()//
				;

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(gridWithTwoRows)//
						.withoutAnyArgument().performActionResult(Grid::countRows)//
						.thenShouldBeEqualTo(2)//
				;//

			}

			@Override
			@Test
			public void countColumns(final BDDSoftAssertions softly) {
				final var gridWithThreeColumn = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(3).successValue())

						.defaultUnassigned()//
				;

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(gridWithThreeColumn)//
						.withoutAnyArgument().performActionResult(Grid::countColumns)//
						.thenShouldBeEqualTo(3)//
				;//

			}

			@Override
			public void isInBounds(final BDDSoftAssertions softly) {
				final var grid = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(3).successValue())//
						.defaultUnassigned();

				final var inside1 = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var inside2 = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();
				final var outsideRow = GridCoordinate.withRowIndex(2).withColumnIndex(0).successValue();
				final var outsideCol = GridCoordinate.withRowIndex(0).withColumnIndex(2).successValue();
				final var negative = GridCoordinate.withRowIndex(-1).withColumnIndex(0).successValue();

				softly.then(grid.isInBounds(inside1).successValue()).isTrue();
				softly.then(grid.isInBounds(inside2).successValue()).isTrue();
				softly.then(grid.isInBounds(outsideRow).successValue()).isFalse();
				softly.then(grid.isInBounds(outsideCol).successValue()).isFalse();
				softly.then(grid.isInBounds(negative).successValue()).isFalse();
			}

			@Override
			public void isSymmetric(final BDDSoftAssertions softly) {
				final var coord00 = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var coord01 = GridCoordinate.withRowIndex(0).withColumnIndex(1).successValue();
				final var coord10 = GridCoordinate.withRowIndex(1).withColumnIndex(0).successValue();
				final var coord11 = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();

				final var grid = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())//
						.defaultUnassigned();

				final var symmetric = grid.setAt(coord00, 1).setAt(coord01, 2).setAt(coord10, 2).setAt(coord11, 1);

				final var notSymmetric = grid.setAt(coord00, 1).setAt(coord01, 2).setAt(coord10, 3).setAt(coord11, 4);

				softly.then(symmetric.isSymmetric().successValue()).isTrue();
				softly.then(notSymmetric.isSymmetric().successValue()).isFalse();
			}

			@Override
			public void isDiagonal(final BDDSoftAssertions softly) {
				final var coord00 = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var coord01 = GridCoordinate.withRowIndex(0).withColumnIndex(1).successValue();
				// final var coord10 =
				// GridCoordinate.withRowIndex(1).withColumnIndex(0).successValue();
				final var coord11 = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();
				final var grid = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())//
						.defaultUnassigned();

				final var diagonal = grid.setAt(coord00, 1).setAt(coord11, 2); // off-diagonal left unassigned

				final var notDiagonal = grid.setAt(coord00, 1).setAt(coord01, 2) // non-diagonal cell has value
						.setAt(coord11, 3);

				softly.then(diagonal.isDiagonal().successValue()).isTrue();
				softly.then(notDiagonal.isDiagonal().successValue()).isFalse();
			}

		}

		@Nested
		class Transformations implements GridTestBehavior.Operations.Transformations {

			@Override
			@Test
			public void map(final BDDSoftAssertions softly) {

				final var position = GridCoordinate//
						.withRowIndex(0)//
						.withColumnIndex(0)//
						.successValue();

				final var GRID_2_X_2 = Grid.<Integer>//
						withHeight(ValidatedDimension.of(1).successValue())//
						.withWidth(ValidatedDimension.of(1).successValue())

						.defaultUnassigned()//
						.setAt(position, 4)//

				;

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(GRID_2_X_2)//
						.<Function<Integer, String>>//
						givenSingleArgument(i -> "#" + i)//
						.performActionResult(mapper -> grid -> //

						grid.map(mapper).getAt(position))//
						.thenShouldBeEqualTo("#4")

				;//

			}

			@Override
			@Test
			public void mapWithPosition(final BDDSoftAssertions softly) {

				final var position = GridCoordinate//
						.withRowIndex(0)//
						.withColumnIndex(2)//
						.successValue();

				final var GRID_2_X_2 = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())

						.defaultUnassigned()//
						.setAt(position, 4)//

				;

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(GRID_2_X_2)//
						.<Function<GridCoordinate, Function<Integer, String>>>//
						givenSingleArgument(gridCoordinate -> element -> //
						"the sum of the indexes  of gridCoordinate plus its value is "
								+ (element + gridCoordinate.columnIndex()

										+ gridCoordinate.rowIndex()))//
						.performActionResult(mapper -> grid -> //

						grid.mapWithPosition(mapper).getAt(position))//
						.thenShouldBeEqualTo("the sum of the indexes  of gridCoordinate plus its value is 6")

				;//

			}

			@Override
			@Test
			public void filter(final BDDSoftAssertions softly) {
				final var topLeft = GridCoordinate//
						.withRowIndex(0)//
						.withColumnIndex(0)//
						.successValue();
				final var bottomLeft = GridCoordinate//
						.withRowIndex(1)//
						.withColumnIndex(0)//
						.successValue();//
				final var bottomRight = GridCoordinate//
						.withRowIndex(0)//
						.withColumnIndex(1)//
						.successValue();//

				final var topRight = GridCoordinate//
						.withRowIndex(1)//
						.withColumnIndex(1)//
						.successValue();//

				final var GRID_2_X_2 = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())

						.defaultUnassigned()//
						.setAt(topLeft, 1)//
						.setAt(bottomLeft, 2)//
						.setAt(topRight, 3)//
						.setAt(bottomRight, 4)

				;

				TestMulHelper.softAssertions(softly)//
						.<Grid<Integer>>//
						initializeCase(GRID_2_X_2)//
						.<Function<Integer, Boolean>>//
						givenSingleArgument(i -> i < 2)//
						.givenSingleArgument(i -> i > 2)//

						.performActionResult(predicate -> grid -> //

						grid.filter(predicate).sizeOnlySuccessfulValues())//
						.thenShouldBeEqualTo(1).thenShouldBeEqualTo(2)

				;//

			}

			@Override
			@Test
			public void rotate90(final BDDSoftAssertions softly) {
				final var topLeft = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var topRight = GridCoordinate.withRowIndex(0).withColumnIndex(1).successValue();
				final var bottomLeft = GridCoordinate.withRowIndex(1).withColumnIndex(0).successValue();
				final var bottomRight = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();

				final var base = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())//
						.defaultUnassigned().setAt(topLeft, 1).setAt(topRight, 2).setAt(bottomLeft, 3)
						.setAt(bottomRight, 4);

				final var expected = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())//
						.defaultUnassigned()//
						.setAt(topLeft, 3)//
						.setAt(topRight, 1)//
						.setAt(bottomLeft, 4)//
						.setAt(bottomRight, 2);

				softly.then(base.rotate90()).isEqualTo(expected);
			}

			@Override
			@Test
			public void rotate180(final BDDSoftAssertions softly) {
				final var topLeft = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var topRight = GridCoordinate.withRowIndex(0).withColumnIndex(1).successValue();
				final var bottomLeft = GridCoordinate.withRowIndex(1).withColumnIndex(0).successValue();
				final var bottomRight = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();

				final var base = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())//
						.defaultUnassigned().setAt(topLeft, 1).setAt(topRight, 2).setAt(bottomLeft, 3)
						.setAt(bottomRight, 4);

				final var expected = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())//
						.defaultUnassigned().setAt(topLeft, 4).setAt(topRight, 3).setAt(bottomLeft, 2)
						.setAt(bottomRight, 1);

				softly.then(base.rotate180()).isEqualTo(expected);

			}

			@Override
			@Test
			public void rotate270(final BDDSoftAssertions softly) {
				final var topLeft = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var topRight = GridCoordinate.withRowIndex(0).withColumnIndex(1).successValue();
				final var bottomLeft = GridCoordinate.withRowIndex(1).withColumnIndex(0).successValue();
				final var bottomRight = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();

				final var base = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())//
						.defaultUnassigned().setAt(topLeft, 1).setAt(topRight, 2).setAt(bottomLeft, 3)
						.setAt(bottomRight, 4);

				final var expected = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())// )
						.defaultUnassigned().setAt(topLeft, 2).setAt(topRight, 4).setAt(bottomLeft, 1)
						.setAt(bottomRight, 3);

				softly.then(base.rotate270()).isEqualTo(expected);

			}

			@Override
			@Test
			public void flipHorizontally(final BDDSoftAssertions softly) {
				final var topLeft = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var topRight = GridCoordinate.withRowIndex(0).withColumnIndex(1).successValue();
				final var bottomLeft = GridCoordinate.withRowIndex(1).withColumnIndex(0).successValue();
				final var bottomRight = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();

				final var base = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())// )
						.defaultUnassigned().setAt(topLeft, 1).setAt(topRight, 2).setAt(bottomLeft, 3)
						.setAt(bottomRight, 4);

				final var expected = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())// )
						.defaultUnassigned().setAt(topLeft, 2).setAt(topRight, 1).setAt(bottomLeft, 4)
						.setAt(bottomRight, 3);

				softly.then(base.flipHorizontally()).isEqualTo(expected);

			}

			@Override
			@Test
			public void flipVertically(final BDDSoftAssertions softly) {
				final var topLeft = GridCoordinate.withRowIndex(0).withColumnIndex(0).successValue();
				final var topRight = GridCoordinate.withRowIndex(0).withColumnIndex(1).successValue();
				final var bottomLeft = GridCoordinate.withRowIndex(1).withColumnIndex(0).successValue();
				final var bottomRight = GridCoordinate.withRowIndex(1).withColumnIndex(1).successValue();

				final var base = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())// )
						.defaultUnassigned().setAt(topLeft, 1).setAt(topRight, 2).setAt(bottomLeft, 3)
						.setAt(bottomRight, 4);

				final var expected = Grid.<Integer>//
						withHeight(ValidatedDimension.of(2).successValue())//
						.withWidth(ValidatedDimension.of(2).successValue())// )
						.defaultUnassigned().setAt(topLeft, 3).setAt(topRight, 4).setAt(bottomLeft, 1)
						.setAt(bottomRight, 2);

				softly.then(base.flipVertically()).isEqualTo(expected);
			}

		}

	}
}