package dev.ofekmalka.core.data_structure.map;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface MapBehavior<K, V> {

	public interface Basic<K, V> extends MapBehavior<K, V> {
		Map<K, V> add(K mapKey, V mapValue);

		Map<K, V> remove(K mapKey);

		List<Tuple2<K, V>> toList();
	}

	public interface Queries<K, V> extends MapBehavior<K, V> {
		Result<Boolean> isEmpty();

		Result<Integer> size();

		Result<Boolean> containsKey(K searchKey);

		Result<Boolean> containsValue(V searchValue);

		Result<V> getOptionally(K searchKey);

		Result<V> getOptionallyWithMessage(K searchKey, String keyNotFoundMessage);

		List<K> keys();

		List<V> values();

		List<K> keysForValue(V searchValue);

	}

	public interface Transformations<K, V> extends MapBehavior<K, V> {
		<B> Map<K, B> mapValues(Function<V, B> valueMapper);
	}

}
