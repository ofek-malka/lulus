package dev.ofekmalka.core.data_structure.grid;

import static dev.ofekmalka.core.assertion.PreconditionedProcess.from;

import java.util.Objects;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.grid.Grid.Builder.WithWidth;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.data_structure.map.Map;
import dev.ofekmalka.core.error.ErrorOptions;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.ResultProvider;
import dev.ofekmalka.tools.tuple.Tuple2;

public class Grid<T> implements ResultProvider<Grid<T>>,

		GridBehavior.Operations.Queries<T>, //
		GridBehavior.Operations.Transformations<T>, //
		GridBehavior.Operations.Traversal<T>, //
		GridBehavior.Operations.Basic<T> {

	final Result<GridImp<T>> source;

	private Grid(final Result<GridImp<T>> source) {
		this.source = source;
	}

	private Grid(final GridImp<T> source) {
		this.source = Result.success(source);
	}

	static <T> Grid<T> makeTypeSafe(final Result<GridImp<T>> r) {
		return new Grid<>(r);
	}

	static <T> Grid<T> makeTypeSafe(final GridImp<T> r) {
		return new Grid<>(r);
	}

	public static <T> Grid<T> failureInstance() {
		return new Grid<>(Result.failure("failure instance"));
	}

	public static <T> Grid<T> failureWithMessage(final String message) {
		return new Grid<>(Result.failure(message));
	}

	@Override
	public int hashCode() {
		return source//
				.map(GridImp::hashCode)//
				.casesForProvidedHashCode()//
				.onSuccess(t -> t)//
				.onOtherOptionProvideClassName("Grid")//
		;

	}

	@SuppressWarnings("unchecked")

	public Result<Boolean> isEqualTo(final Object other) {

		final var argName = "other";
		final var errorProcessingMessage = "The process of other argument is NOT Success.";
		final var errorInstanceOf = "other argument is NOT an instance of Grid.";

		return from(source)//
				.checkCondition(() -> If.givenObject(other)//
						.isNonNull(argName)//
						.andIs(o -> o instanceof Grid, errorInstanceOf)//
						.andIs(o -> ((Grid<T>) o).source.isSuccess(), errorProcessingMessage))
				// we must compare only the content!
				.processOperation(g -> g.equals(((Grid<T>) other).source.successValue()))
				.andMakeStackTraceUnderTheName("isEqualTo")//
				.getResultProccess();//
	}

	@Override
	public boolean equals(final Object obj) {
		return this

				.isEqualTo(obj).getOrElse(false);

	}

	@Override
	public String toString() {
		return If.isItTrue(source.isNotSuccess())//
				.will()//

				.returnValue(() -> "It is in a failed state and cannot generate a valid representation for toString."
						+ "\nPlease check the process status for more details.")//
				.orGet(() -> source.successValue().toString());
	}

	@Override
	public Result<Grid<T>> asResult() {
		return source.map(__ -> this);
	}

	// MAX_GRID_DIMENSION = 10_000

	public static final class Constants {

		public static final int MAX_GRID_DIMENSION = 10_000;//

		public static final String DEFAULT_UNDEFINED_BY_USER = "Element is not defined here";

	}

	public static class Errors {

		public enum CastumArgumentMessage {
			ERROR_NON_POSITIVE_VALUE("The ###nameOfNumber### argument must be positive");

			String message;

			CastumArgumentMessage(final String message) {
				this.message = message;
			}

			public ErrorOptions withArgumentName(final String argumentName) {
				return () -> message.replaceAll("###.*###", argumentName);
			}

		}

		public enum GeneralMessage implements ErrorOptions {
			ERROR_MAX_GRID_DIMENSION_EXCEEDED(
					String.format("Input exceeds the maximum allowed limit of %d.", Constants.MAX_GRID_DIMENSION)),

			ERROR_GRID_COORDINATE_IS_NOT_IN_BOUNDS("grid coordinate is not in bounds");

			String message;

			GeneralMessage(final String message) {
				this.message = message;
			}

			@Override
			public String getMessage() {
				return message;
			}

		}

	}

	public static class GridRegion {
		private final GridCoordinate topLeft;
		private final GridCoordinate bottomRight;

		private GridRegion(final GridCoordinate topLeft, final GridCoordinate bottomRight) {
			this.topLeft = topLeft;
			this.bottomRight = bottomRight;
		}

		public static <T> WithBottomRight<T> withTopLeft(final GridCoordinate topLeft) {
			return bottomRight -> //

			If.givenObject(topLeft)//
					.isNot(tl -> tl.rowIndex() > bottomRight.rowIndex() //
							, "topLeft below bottomRight")//
					.andIsNot(tl -> tl.columnIndex() > bottomRight.columnIndex()//

							, "topLeft right of bottomRight")//
					.will()//
					.returnValue(() -> new GridRegion(topLeft, bottomRight))//
					.getResult();//

		}

		public interface WithTopLeft<T> {
			WithBottomRight<T> withTopLeft(final GridCoordinate topLeft);
		}

		public interface WithBottomRight<T> {
			Result<GridRegion> withBottomRight(final GridCoordinate bottomRight);

		}

		public GridCoordinate topLeft() {
			return topLeft;
		}

		public GridCoordinate bottomRight() {
			return bottomRight;
		}

		public boolean isWithinBounds(final GridCoordinate coordinate) {

			return coordinate.rowIndex() >= topLeft.rowIndex() && coordinate.rowIndex() <= bottomRight.rowIndex()
					&& coordinate.columnIndex() >= topLeft.columnIndex()
					&& coordinate.columnIndex() <= bottomRight.columnIndex();

		}

		public boolean contains(final GridRegion gridRegion) {

			return

			isWithinBounds(gridRegion.bottomRight()) && isWithinBounds(gridRegion.topLeft());//

		}

		public boolean isInside(final GridRegion container) {
			return container.contains(this);
		}

	}

	public static class ValidatedDimension {
		private final int value;

		private ValidatedDimension(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Result<ValidatedDimension> of(final int number) {
			return of(number, "number");
		}

		public static Result<ValidatedDimension> of(final int number, final String argumentName) {
			return If.givenObject(number)
					.is(n -> n > 0,
							Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE.withArgumentName(argumentName)
									.getMessage())
					.andIs(n -> n <= Constants.MAX_GRID_DIMENSION,
							Errors.GeneralMessage.ERROR_MAX_GRID_DIMENSION_EXCEEDED.getMessage())

					.will().mapTo(ValidatedDimension::new).getResult();
		}
	}

	public static class GridCoordinate {
		private final Tuple2<Integer, Integer> coordinate;

		public GridCoordinate(final Tuple2<Integer, Integer> coordinate) {
			this.coordinate = coordinate;
		}

		public static <T> WithColumnIndex<T> withRowIndex(final int rowIndex) {
			return columnIndex -> //

			If.givenObject(rowIndex)//
					.is(n -> n >= 0, "rowIndex is negative")//
					.andIs(n -> n <= Grid.Constants.MAX_GRID_DIMENSION, //
							Errors.GeneralMessage.ERROR_MAX_GRID_DIMENSION_EXCEEDED.getMessage())//
					.will()//
					.returnValue(() -> columnIndex).andAfterThisTransformationCheckIfTransformedObject()//

					.is(n -> n >= 0, "columnIndex is negative")//
					.andIs(n -> n <= Grid.Constants.MAX_GRID_DIMENSION, //
							Errors.GeneralMessage.ERROR_MAX_GRID_DIMENSION_EXCEEDED.getMessage())//

					.will()//

					.returnValue(() -> new GridCoordinate(

							Tuple2.of(rowIndex, columnIndex).getResult().successValue()))//
					.getResult();//

		}

		public interface WithRowIndex<T> {
			WithColumnIndex<T> withRowIndex(final int rowIndex);
		}

		public interface WithColumnIndex<T> {
			Result<GridCoordinate> withColumnIndex(final int columnIndex);

		}

		public int rowIndex() {
			return coordinate.state();
		}

		public int columnIndex() {
			return coordinate.value();
		}

		public GridCoordinate swap() {
			return new GridCoordinate(coordinate.swap());
		}

		public Result<GridCoordinate> changeRowIndexAndColumnIndex(final Function<Integer, Integer> f2,
				final Function<Integer, Integer> f1) {
			return

			coordinate.map(f2, f1).map(GridCoordinate::new)

			;
		}

		public Result<GridCoordinate> changeRowIndex(final Function<Integer, Integer> f2) {
			return

			coordinate.mapState(f2).map(GridCoordinate::new)

			;
		}

		public Result<GridCoordinate> changeColumnIndex(final Function<Integer, Integer> f2) {
			return

			coordinate.mapValue(f2).map(GridCoordinate::new)

			;
		}

		public boolean isCoordinatesEqual() {
			return coordinate.isStateAndValueEqual();
		}

		@Override
		public int hashCode() {
			return Objects.hash(coordinate);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof final GridCoordinate other))
				return false;
			return Objects.equals(coordinate, other.coordinate);
		}

		@Override
		public String toString() {
			return "GridCoordinate [coordinate=" + coordinate + "]";
		}

	}

	public static <T> WithWidth<T> withHeight(final ValidatedDimension height) {
		return Builder.withHeight(height);

	}

	static class Builder<T> {

		public interface WithHeight<T> {

			WithWidth<T> withHeight(ValidatedDimension height);

		}

		public interface WithWidth<T> {
			Creation<T> withWidth(ValidatedDimension width);
		}

		public interface Creation<T> {
			default Grid<T> defaultUnassigned() {
				return defaultUnassigned(Result.<T>failure(Constants.DEFAULT_UNDEFINED_BY_USER));
			}

			default Grid<T> defaultUnassigned(final T value) {
				return defaultUnassigned(Result.of(value, "value argument of defaultUnassigned is null"));

			}

			Grid<T> defaultUnassigned(Result<T> result);

		}

		public static <T> WithWidth<T> withHeight(final ValidatedDimension height) {
			return //
			width -> //
			defaultUnassigned -> {

				final var heightValue = height.getValue();

				final var widthValue = width.getValue();

				final var map = List.extendedFactoryOperations().generateRange(0, heightValue)

						.combineNestedLists(rowIndex -> List.extendedFactoryOperations().generateRange(0, widthValue)

								.map(columnIndex ->

								GridCoordinate.withRowIndex(rowIndex).withColumnIndex(columnIndex).successValue()

								))

						.foldLeft(Map.<GridCoordinate, Result<T>>emptyMap(), acc -> element -> //
				acc.add(element, defaultUnassigned))

						.successValue();

				return Grid.makeTypeSafe(new GridImp<>(heightValue, widthValue, defaultUnassigned, map));
			};

		}

	}

	interface GridImpBehavior<T> {

		boolean isEmpty();

		Result<Integer> sizeOnlySuccessfulValues();

		int size();

		<R> GridImp<R> mapWithPosition(Function<GridCoordinate, Function<T, R>> mapper);

		Result<GridImp<T>> subGrid(GridRegion gridRegion);

		Result<T> getAt(GridCoordinate gridCoordinate);

		Result<GridImp<T>> setAt(GridCoordinate gridCoordinate, T value);

		List<Tuple2<GridCoordinate, Result<T>>> indexedCells();

		GridImp<T> flipHorizontally();

		GridImp<T> flipVertically();

		GridImp<T> rotate270();

		GridImp<T> rotate180();

		GridImp<T> rotate90();

		GridImp<T> filter(Function<T, Boolean> predicate);

		<R> GridImp<R> map(Function<T, R> mapper);

		int countColumns();

		int countRows();

		Result<Boolean> isDiagonal();

		Result<Boolean> isSymmetric();

		boolean isInBounds(GridCoordinate gridCoordinate);

	}

	final static class GridImp<T> implements GridImpBehavior<T> {

		private final int height;// rows number
		private final int width;// columns number
		private final Result<T> defaultUnassigned;
		private final Map<GridCoordinate, Result<T>> gridStructure;

		private GridImp(final int height, final int width, final Result<T> defaultUnassigned,
				final Map<GridCoordinate, Result<T>> gridStructure) {
			this.width = width;
			this.height = height;
			this.defaultUnassigned = defaultUnassigned;
			this.gridStructure = gridStructure;
		}

		public <R> GridImp<R> createGridImp(final Result<R> defaultUnassigned,
				final Map<GridCoordinate, Result<R>> gridStructure) {
			return new GridImp<>(height, width, defaultUnassigned, gridStructure);
		}

		public <R> GridImp<R> createGridImp(final Map<GridCoordinate, Result<R>> gridStructure) {
			return new GridImp<>(height, width, Result.<R>failure("element is not defined here"), gridStructure);
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o)
				return true;
			if (!(o instanceof GridImp))
				return false;
			final var gridImp = (GridImp<?>) o;
			return

			width == gridImp.width //
					&& height == gridImp.height //
					&& Objects.equals(defaultUnassigned, gridImp.defaultUnassigned)

					&& Objects.equals(gridStructure, gridImp.gridStructure)

			;//
		}

		@Override
		public int hashCode() {
			return gridStructure.hashCode();
		}

		@Override
		public String toString() {
			return "size: " + height + "x" + width + ",\n" +

					gridStructure.toList().groupBy(t -> t.state().rowIndex())
							.mapValues(l -> l.map(t -> "position: (" + t.state().rowIndex() + ","
									+ t.state().columnIndex() + ") -> value: " + t.value()).mkStr(",\n").successValue())
							.toList().map(t -> "row: " + (t.state() + 1) + ")\n" + t.value()) //
							.mkStr(",\n").successValue();
		}

		@Override
		public boolean isEmpty() {
			return gridStructure//
					.toList()//
					.exclude(t -> t.value().equals(defaultUnassigned))//
					.isEmpty()//
					.successValue();

		}

		@Override
		public boolean isInBounds(final GridCoordinate gridCoordinate) {

			return

			If.givenObject(gridCoordinate)//
					.is(tl -> tl.rowIndex() < width //
							, "topLeft right  bottomRight")//
					.andIs(tl -> tl.columnIndex() < height//
							, "topLeft below of bottomRight")//
					.will()//
					.allTheRequirementsFit();//

		}

		@Override
		public Result<Boolean> isSymmetric() {
			return If.isItFalse(isGridSquare())//
					.will()//
					.returnValue(() -> Result.success(false))//
					.orGet(() -> //
					gridStructure//
							.toList()//
							.map(t -> {//

								final var transposedCoordinates = t.state().swap();//

								final var transposedValue = gridStructure.getOptionally(transposedCoordinates);//

								return t.value().equals(transposedValue);//
							})//
							.allEqualTo(true));//

		}

		@Override
		public Result<Boolean> isDiagonal() {
			return If.isItFalse(isGridSquare())//
					.will()//
					.returnValue(() -> Result.success(false))//
					.orGet(this::hasOnlyDiagonalValues);//
		}

		private Result<Boolean> hasOnlyDiagonalValues() {
			final var partition = gridStructure//
					.toList()//
					.partition(t -> t.state().isCoordinatesEqual())//
					.successValue();//

			final var elementsInDiagonal = partition.state();
			final var elementsOffDiagonal = partition.value();

			return elementsOffDiagonal//
					.map(Tuple2::value)//
					.allEqualTo(defaultUnassigned)//
					.and(() -> //
					elementsInDiagonal//
							.map(t -> t.value().isSuccess())//
							.allEqualTo(true)//
					);//
		}

		private boolean isGridSquare() {
			return width == height;
		}

		@Override
		public int countRows() {
			return height;
		}

		@Override
		public int countColumns() {
			return width;
		}

		@Override
		public int size() {
			return height * width;
		}

		@Override
		public Result<Integer> sizeOnlySuccessfulValues() {
			return gridStructure.toList().filter(t -> t.value().isSuccess()).size();
		}

		@Override
		public <R> GridImp<R> map(final Function<T, R> mapper) {
			final var mapValues = gridStructure.mapValues(r -> r.map(mapper));
			return createGridImp(mapValues);
		}

		@Override
		public <R> GridImp<R> mapWithPosition(final Function<GridCoordinate, Function<T, R>> mapper) {

			final var identity = Map.<GridCoordinate, Result<R>>emptyMap();
			final var afterMapperValuesWithPosition = gridStructure.toList()

					.foldLeft(identity, acc -> element -> {

						final var position = element.state();
						final var rowIndex = position.rowIndex();
						final var columnIndex = position.columnIndex();

						final var gridCoordinate = GridCoordinate//
								.withRowIndex(rowIndex)//
								.withColumnIndex(columnIndex)//
								.successValue();

						final var positionValue = element.value();

						final var newValue = mapper.safeApplyOn(gridCoordinate).flatMap(fun ->

					positionValue.flatMap(t ->

					fun.safeApplyOn(t)));
						return acc.add(position, newValue);

					}).successValue();

			return createGridImp(afterMapperValuesWithPosition);

		}

		@Override
		public GridImp<T> filter(final Function<T, Boolean> predicate) {
			final var mapValues = gridStructure.mapValues(result -> filterResult(result, predicate));
			return createGridImp(mapValues);
		}

		private Result<T> filterResult(final Result<T> result, final Function<T, Boolean> predicate) {
			return If.givenObject(result)//
					.is(Result::isSuccess)//
					.andIs(r -> r.validate(predicate).isNotSuccess())//
					.will()//
					.returnValue(() -> extractPredicateFailure(result, predicate))//
					.orGet(() -> keepIfPredicatePassesOrMarkAsFiltered(result, predicate));//
		}

		private Result<T> extractPredicateFailure(final Result<T> result, final Function<T, Boolean> predicate) {
			return result.validate(predicate).extractOnlyUserError();
		}

		private Result<T> keepIfPredicatePassesOrMarkAsFiltered(final Result<T> result,
				final Function<T, Boolean> predicate) {
			return If.givenObject(result)//
					.is(Result::isSuccess)//
					.andIs(r -> r.map(predicate).successValue())//
					.will()//
					.returnValue(() -> result)//
					.orGet(() -> Result.failure("element is not defined here after removed by filter"));//
		}

		@Override
		public GridImp<T> rotate90() {
			final var newWidth = height;
			final var newHeight = width;
			final var identity = Map.<GridCoordinate, Result<T>>emptyMap();

			final var rotatedMap = gridStructure

					.toList().foldLeft(identity, acc -> element -> {

						final var position = element.state();
						final var positionValue = element.value();
						final var rowIndex = position.rowIndex();
						final var columnIndex = position.columnIndex();

						// Swap coordinates for 90-degree rotation
						final var rotatedCoordinates =

								position.changeRowIndexAndColumnIndex(//

										__ -> columnIndex, __ -> height - 1 - rowIndex //
						)//
										.successValue();//

						return acc.add(rotatedCoordinates, positionValue);

					}).successValue();

			return new GridImp<>(newHeight, newWidth, defaultUnassigned, rotatedMap);
		}

		@Override
		public GridImp<T> rotate180() {

			final var identity = Map.<GridCoordinate, Result<T>>emptyMap();

			final var rotatedMap = gridStructure

					.toList().foldLeft(identity, acc -> element -> {

						final var position = element.state();
						final var positionValue = element.value();
						final var rowIndex = position.rowIndex();
						final var columnIndex = position.columnIndex();

						final var rotatedCoordinates =

								position.changeRowIndexAndColumnIndex(//
										__ -> height - 1 - rowIndex, //
										__ -> width - 1 - columnIndex//
						)//
										.successValue();//

						return acc.add(rotatedCoordinates, positionValue);

					}).successValue();

			return createGridImp(rotatedMap);

		}

		@Override
		public GridImp<T> rotate270() {
			final var newWidth = height;
			final var newHeight = width;
			final var identity = Map.<GridCoordinate, Result<T>>emptyMap();

			final var rotatedMap = gridStructure

					.toList().foldLeft(identity, acc -> element -> {

						final var position = element.state();
						final var positionValue = element.value();
						final var rowIndex = position.rowIndex();
						final var columnIndex = position.columnIndex();

						final var rotatedCoordinates =

								position.changeRowIndexAndColumnIndex(//
										__ -> width - 1 - columnIndex, //
										__ -> rowIndex //
						)//
										.successValue();//

						return acc.add(rotatedCoordinates, positionValue);

					}).successValue();

			return new GridImp<>(newHeight, newWidth, defaultUnassigned, rotatedMap);
		}

		@Override
		public GridImp<T> flipVertically() {

			final var identity = Map.<GridCoordinate, Result<T>>emptyMap();

			final var rotatedMap = gridStructure

					.toList().foldLeft(identity, acc -> element -> {

						final var position = element.state();
						final var positionValue = element.value();
						final var rowIndex = position.rowIndex();

						final var rotatedCoordinates =

								position.changeRowIndex(//
										x -> height - 1 - rowIndex//
						)//
										.successValue();//

						return acc.add(rotatedCoordinates, positionValue);

					}).successValue();

			return createGridImp(rotatedMap);

		}

		@Override
		public GridImp<T> flipHorizontally() {

			final var identity = Map.<GridCoordinate, Result<T>>emptyMap();

			final var rotatedMap = gridStructure

					.toList().foldLeft(identity, acc -> element -> {

						final var position = element.state();
						final var positionValue = element.value();
						final var columnIndex = position.columnIndex();

						final var rotatedCoordinates =

								position.changeColumnIndex(//
										y -> width - 1 - columnIndex//
						)//
										.successValue();//

						return acc.add(rotatedCoordinates, positionValue);

					}).successValue();

			return createGridImp(rotatedMap);
		}

		@Override
		public List<Tuple2<GridCoordinate, Result<T>>> indexedCells() {
			return gridStructure.toList();
		}

		@Override
		public Result<GridImp<T>> setAt(final GridCoordinate gridCoordinate, final T value) {
			return

			If.givenObject(gridCoordinate)//
					.is(gc -> gc.rowIndex() >= height)//
					.andIs(gc -> gc.columnIndex() >= width)//
					.will().<Result<GridImp<T>>>returnValue(
							() -> Errors.GeneralMessage.ERROR_GRID_COORDINATE_IS_NOT_IN_BOUNDS.asResult())
					.orGet(() ->

					Result.success(createGridImp(gridStructure.add(gridCoordinate, Result.success(value)))))//

			;
		}

		@Override
		public Result<T> getAt(final GridCoordinate gridCoordinate) {
			return gridStructure.getOptionally(gridCoordinate).mapFailure(

					Errors.GeneralMessage.ERROR_GRID_COORDINATE_IS_NOT_IN_BOUNDS.getMessage()

			)//
					.flatMap(t -> t)//
					.removeCalledByMethodPrefixes();
		}

		@Override
		public Result<GridImp<T>> subGrid(final GridRegion gridRegion) {
			final var topLeft = gridRegion.topLeft();
			final var bottomRight = gridRegion.bottomRight();

			return If.givenObject(bottomRight)//
					.is(br -> br.rowIndex() > height)//
					.andIs(br -> br.columnIndex() > width)//
					.will()//
					.<Result<GridImp<T>>>returnValue(//
							() -> Errors.GeneralMessage.ERROR_GRID_COORDINATE_IS_NOT_IN_BOUNDS.asResult())//
//
					.orGet(() -> {//
						final var identity = Map.<GridCoordinate, Result<T>>emptyMap();//

						final var shiftedMap = gridStructure.toList()
								.filter(element -> gridRegion.isWithinBounds(element.state()))
								.foldLeft(identity, acc -> element -> {//
									final var original = element.state();//
									final var shifted = GridCoordinate//
											.withRowIndex(original.rowIndex() - topLeft.rowIndex())//
											.withColumnIndex(original.columnIndex() - topLeft.columnIndex())//
											.successValue();//

									return acc.add(shifted, element.value());//
								})//
								.successValue();//

						final var newWidth = bottomRight.columnIndex() - topLeft.columnIndex() + 1;
						final var newHeight = bottomRight.rowIndex() - topLeft.rowIndex() + 1;
						return Result.success(new GridImp<>(newHeight, newWidth, defaultUnassigned, shiftedMap));
					});
		}

	}

	@Override
	public Result<Boolean> isEmpty() {
		return from(source)//
				.processOperation(GridImp::isEmpty)//
				.andMakeStackTraceUnderTheName("isEmpty")//
				.getResultProccess();//
	}

	@Override
	public Result<Boolean> isInBounds(final GridCoordinate gridCoordinate) {
		return from(source)//
				.checkCondition(() -> If.givenObject(gridCoordinate).isNonNull("gridCoordinate"))
				.processOperation(grid -> grid.isInBounds(gridCoordinate))//
				.andMakeStackTraceUnderTheName("isInBounds")//
				.getResultProccess();//
	}

	@Override
	public Result<Boolean> isSymmetric() {
		return from(source)//
				.processOperationWithResult(GridImp::isSymmetric)//
				.andMakeStackTraceUnderTheName("isSymmetric")//
				.getResultProccess();//
	}

	@Override
	public Result<Boolean> isDiagonal() {
		return from(source)//
				.processOperationWithResult(GridImp::isDiagonal)//
				.andMakeStackTraceUnderTheName("isDiagonal")//
				.getResultProccess();//
	}

	@Override
	public Result<Integer> countRows() {
		return from(source)//
				.processOperation(GridImp::countRows)//
				.andMakeStackTraceUnderTheName("countRows")//
				.getResultProccess();//
	}

	@Override
	public Result<Integer> countColumns() {
		return from(source)//
				.processOperation(GridImp::countColumns)//
				.andMakeStackTraceUnderTheName("countColumns")//
				.getResultProccess();//
	}

	@Override
	public Result<Integer> size() {
		return from(source)//
				.processOperation(GridImp::size)//
				.andMakeStackTraceUnderTheName("size")//
				.getResultProccess();//
	}

	@Override
	public Result<Integer> sizeOnlySuccessfulValues() {
		return from(source)//
				.processOperationWithResult(GridImp::sizeOnlySuccessfulValues)//
				.andMakeStackTraceUnderTheName("sizeOnlySuccessfulValues")//
				.getResultProccess();//
	}

	@Override
	public <R> Grid<R> map(final Function<T, R> mapper) {
		final var r = from(source)//

				.checkCondition(() -> If.givenObject(mapper).isNonNull("mapper"))

				.processOperation(grid -> grid.map(mapper))//
				.andMakeStackTraceUnderTheName("map")//
				.getResultProccess();

		return Grid.makeTypeSafe(r);
	}

	@Override
	public <R> Grid<R> mapWithPosition(final Function<GridCoordinate, Function<T, R>> mapper) {
		final var r = from(source)//

				.checkCondition(() -> If.givenObject(mapper).isNonNull("mapper"))

				.processOperation(grid -> grid.mapWithPosition(mapper))//
				.andMakeStackTraceUnderTheName("mapWithPosition")//
				.getResultProccess();

		return Grid.makeTypeSafe(r);
	}

	@Override
	public Grid<T> filter(final Function<T, Boolean> predicate) {
		final var r = from(source)//
				.checkCondition(() -> If.givenObject(predicate).isNonNull("predicate"))//
				.processOperation(grid -> grid.filter(predicate))//
				.andMakeStackTraceUnderTheName("filter")//
				.getResultProccess();//

		return Grid.makeTypeSafe(r);
	}

	@Override
	public Grid<T> rotate90() {
		final var r = from(source)//

				.processOperation(GridImp::rotate90)//
				.andMakeStackTraceUnderTheName("rotate90")//
				.getResultProccess();

		return Grid.makeTypeSafe(r);
	}

	@Override
	public Grid<T> rotate180() {
		final var r = from(source)//
				.processOperation(GridImp::rotate180)//
				.andMakeStackTraceUnderTheName("rotate180")//
				.getResultProccess();

		return Grid.makeTypeSafe(r);
	}

	@Override
	public Grid<T> rotate270() {
		final var r = from(source)//
				.processOperation(GridImp::rotate270)//
				.andMakeStackTraceUnderTheName("rotate270")//
				.getResultProccess();

		return Grid.makeTypeSafe(r);
	}

	@Override
	public Grid<T> flipHorizontally() {
		final var r = from(source)//

				.processOperation(GridImp::flipHorizontally)//
				.andMakeStackTraceUnderTheName("flipHorizontally")//
				.getResultProccess();

		return Grid.makeTypeSafe(r);
	}

	@Override
	public Grid<T> flipVertically() {
		final var r = from(source)//

				.processOperation(GridImp::flipVertically)//
				.andMakeStackTraceUnderTheName("flipVertically")//
				.getResultProccess();

		return Grid.makeTypeSafe(r);

	}

	@Override
	public List<Tuple2<GridCoordinate, Result<T>>> indexedCells() {
		return from(source)//

				.processOperation(GridImp::indexedCells)//
				.andMakeStackTraceUnderTheName("indexedCells")//
				.getOrConvertToFailureState(List::failureWithMessage);
	}

	@Override
	public Grid<T> setAt(final GridCoordinate gridCoordinate, final T value) {
		return from(source)//

				.checkCondition(() -> If.givenObject(gridCoordinate).isNonNull("gridCoordinate")
						.andOtherObjectIsNotNull(value, "value"))

				.processOperationWithResult(grid -> grid.setAt(gridCoordinate, value))//
				.andMakeStackTraceUnderTheName("setAt")//
				.mapTo(Grid::makeTypeSafe);

	}

	@Override
	public Result<T> getAt(final GridCoordinate gridCoordinate) {
		return from(source)//

				.checkCondition(() -> If.givenObject(gridCoordinate).isNonNull("gridCoordinate"))

				.processOperationWithResult(grid -> grid.getAt(gridCoordinate))//
				.andMakeStackTraceUnderTheName("getAt")//
				.getResultProccess();

	}

	@Override
	public Grid<T> subGrid(final GridRegion gridRegion) {
		return from(source)//
				.checkCondition(() -> If.givenObject(gridRegion).isNonNull("gridRegion"))
				.processOperationWithResult(grid -> grid.subGrid(gridRegion))//
				.andMakeStackTraceUnderTheName("subGrid")//
				.mapTo(Grid::makeTypeSafe);

	}

}
