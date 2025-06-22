package dev.ofekmalka.core.data_structure.set;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface SetBehavior<T> {//

	public interface Basic<T> extends SetBehavior<T> {
		Set<T> insert(T element);

		Set<T> delete(T element);

		List<T> toList();
	}

	public interface Queries<T> extends SetBehavior<T> {
		Result<Boolean> isEmpty();

		Result<Integer> cardinality();

		Result<Boolean> contains(T element);
	}

	public interface Relations<T> extends SetBehavior<T> {
		Result<Boolean> isSubsetOf(Set<T> other);

		Result<Boolean> isSupersetOf(Set<T> other);
	}

	public interface Algebra<T> extends SetBehavior<T> {
		Set<T> intersect(Set<T> other);

		Set<T> union(Set<T> other);

		Set<T> difference(Set<T> other);

		Set<T> symmetricDifference(Set<T> other);

	}

	public interface Joins<T> extends SetBehavior<T> {
		<B> Set<Tuple2<T, B>> crossJoin(Set<B> other);

		<B> Set<Tuple2<T, B>> innerJoin(Set<B> other, Function<T, Predicate<B>> matchCondition);
	}

	public interface ResultProvider<T> extends SetBehavior<T> {
		Result<Set<T>> getSetResult();
	}

}
