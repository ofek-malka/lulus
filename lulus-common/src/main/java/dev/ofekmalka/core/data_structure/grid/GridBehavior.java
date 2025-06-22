package dev.ofekmalka.core.data_structure.grid;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.grid.Grid.GridCoordinate;
import dev.ofekmalka.core.data_structure.grid.Grid.GridRegion;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface GridBehavior<T> {

	public interface Operations<T>

	{

		public interface Basic<T> extends Operations<T> {
			// Set a value at a specific row and column (immutable)
			Grid<T> setAt(GridCoordinate gridCoordinate, T value);

			// Get the value at a specific row and column
			Result<T> getAt(GridCoordinate gridCoordinate);

			// Extract a subgrid (extract rectangular block)
			Grid<T> subGrid(GridRegion gridRegion);

			Result<Integer> size();

			Result<Integer> sizeOnlySuccessfulValues();

		}

		public interface Queries<T> extends Operations<T> {
			// Check if the grid is empty
			Result<Boolean> isEmpty();

			// Get number of rows (height)
			Result<Integer> countRows();

			// Get number of columns (width)
			Result<Integer> countColumns();

			// Check if a given (row, col) is in bounds
			Result<Boolean> isInBounds(GridCoordinate gridCoordinate);

			// Check if the grid is symmetric (for square grids)
			Result<Boolean> isSymmetric();

			// Check if the grid is diagonal (for square grids)
			Result<Boolean> isDiagonal();

		}

		public interface Transformations<T> extends Operations<T> {
			// Map function to every cell, potentially changing its value
			<R> Grid<R> map(Function<T, R> mapper);

			// Map with awareness of (row, col) position
			<R> Grid<R> mapWithPosition(Function<GridCoordinate, Function<T, R>> mapper);

			// Filter the grid's cells by predicate
			Grid<T> filter(Function<T, Boolean> predicate);

			// Rotate the grid 90 degrees clockwise
			Grid<T> rotate90();

			// Rotate the grid 180 degrees
			Grid<T> rotate180();

			// Rotate the grid 270 degrees
			Grid<T> rotate270();

			// Flip the grid horizontally (mirror along vertical axis)
			Grid<T> flipHorizontally();

			// Flip the grid vertically (mirror along horizontal axis)
			Grid<T> flipVertically();

		}

		public interface Traversal<T> extends Operations<T> {

			List<Tuple2<GridCoordinate, Result<T>>> indexedCells();

		}
	}
}
