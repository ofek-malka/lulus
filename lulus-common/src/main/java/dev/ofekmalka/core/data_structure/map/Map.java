
package dev.ofekmalka.core.data_structure.map;

import static dev.ofekmalka.core.assertion.PreconditionedProcess.from;

import java.util.HashMap;
import java.util.Objects;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.error.ErrorOptions;
import dev.ofekmalka.core.function.ConsoleOutputEffect;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.ResultProvider;
import dev.ofekmalka.tools.tuple.Tuple2;

public final class Map<K, V> implements //
		MapBehavior.Basic<K, V>, //
		MapBehavior.Queries<K, V>, //
		MapBehavior.Transformations<K, V>, //
		ResultProvider<Map<K, V>>, //
		ConsoleOutputEffect {
	private final Result<MapImp<K, V>> source;

	private Map(final Result<MapImp<K, V>> source) {
		this.source = source;
	}

	@SuppressWarnings("rawtypes")
	private static Map EMPTY_MAP = Map.makeTypeSafe(MapImp.emptyMapImp());

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> emptyMap() {
		return EMPTY_MAP;
	}

	private static <K, V> Map<K, V> makeTypeSafe(final Result<MapImp<K, V>> source) {
		return new Map<>(source);
	}

	private static <K, V> Map<K, V> makeTypeSafe(final MapImp<K, V> source) {
		return new Map<>(Result.success(source));
	}

	private MapImp<K, V> getMapImpSource() {
		return source.successValue();
	}

	public static <K, V> Map<K, V> makeFailureInstance() {
		return Map.makeTypeSafe(Result.failure("failure instance"));
	}

	public static <K, V> Map<K, V> makeFailureInstanceWithMessage(final String message) {
		return Map.makeTypeSafe(Result.failure(message));
	}

	@Override
	public Result<Map<K, V>> asResult() {
		return source.map(Map::makeTypeSafe);
	}

	private boolean isProcessSuccess() {
		return source.isSuccess();
	}

	@Override
	public boolean equals(final Object obj) {
		return isEqualTo(obj).getOrElse(false);

	}

	@SuppressWarnings("unchecked")
	private Result<Boolean> isEqualTo(final Object other) {
		final var argName = "other";
		final var errorProcessingMessage = Errors.CastumArgumentNameMessage.ERROR_PROCESSING_JOINED_MAP
				.withArgumentName(argName).getMessage();
		final var errorInstanceOf =

				Errors.CastumArgumentNameMessage.ERROR_NOT_INSTANCE_OF_MAP.withArgumentName(argName).getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(other)//
						.isNonNull(argName)//
						.andIs(o -> o instanceof Map, errorInstanceOf)//
						.andIs(o -> ((Map<K, V>) o).isProcessSuccess(), errorProcessingMessage))
				.processOperation(l -> l.isEqualTo(((Map<K, V>) other).getMapImpSource()))
				.andMakeStackTraceUnderTheName("isEqualTo")//
				.getResultProccess();

	}

	@Override
	public int hashCode() {
		return source//
				.map(MapImp::hashCode)//
				.casesForProvidedHashCode()//
				.onSuccess(t -> t)//
				.onOtherOptionProvideClassName("Map")//
		;

	}

	@Override
	public String toString() {
		return If.isItTrue(source.isFailure())//
				.will()//
				.returnValue(() -> "Map is in a failed state and cannot generate a valid representation for toString."
						+ "\nPlease check the process status for more details.")//
				.orGet(() -> source.map(MapImp::toString).successValue());
	}

	@Override
	public Map<K, V> add(final K mapKey, final V mapValue) {
		return from(source)//
				.checkCondition(() -> If.givenObject(mapKey).isNonNull("mapKey")

						.andOtherObjectIsNotNull(mapValue, "mapValue"))//
				.processOperation(map -> map.add(mapKey, mapValue))//
				.andMakeStackTraceUnderTheName("add")//
				.mapTo(Map::makeTypeSafe);//
	}

	@Override
	public Result<Boolean> isEmpty() {
		return from(source)//

				.processOperation(MapImp::isEmpty)//
				.andMakeStackTraceUnderTheName("isEmpty")////
				.getResultProccess()//
		;
	}

	@Override
	public Result<Integer> size() {
		return from(source)//
				.processOperation(MapImp::size)//
				.andMakeStackTraceUnderTheName("size")////
				.getResultProccess()//
		;
	}

	@Override
	public Result<Boolean> containsKey(final K searchKey) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(searchKey)//
						.isNonNull("searchKey"))//

				.processOperation(map -> map.containsKey(searchKey))//
				.andMakeStackTraceUnderTheName("containsKey")////
				.getResultProccess()//
		;//
	}

	@Override
	public Result<Boolean> containsValue(final V searchValue) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(searchValue)//
						.isNonNull("searchValue"))//
				.processOperation(map -> map.containsValue(searchValue))//
				.andMakeStackTraceUnderTheName("containsValue")////
				.getResultProccess()//
		;//
	}

	@Override
	public Result<V> getOptionally(final K searchKey) {
		return from(source)//
				.checkCondition(() -> If.givenObject(searchKey)//
						.isNonNull("searchKey")//

						.andIsNot(key -> key.toString().matches(".*@\\p{XDigit}+$"),
								"Key must override toString() with meaningful output"))

				.processOperationWithResult(map -> map.getOptionally(searchKey))//
				.andMakeStackTraceUnderTheName("getOptionally")//
				.getResultProccess()//
		;//
	}

	@Override
	public Result<V> getOptionallyWithMessage(final K searchKey, final String keyNotFoundMessage) {
		return from(source)//
				.checkCondition(() -> If.givenObject(searchKey)//
						.isNonNull("searchKey")//
						.andForOtherCondition(//
								If.givenObject(keyNotFoundMessage)//
										.isNonNull("keyNotFoundMessage")//
										.andIsNot(String::isEmpty, "keyNotFoundMessage must not be empty")
										.andIsNot(String::isBlank, "keyNotFoundMessage must not be blank")
										.andIsNot(key -> key.toString().matches(".*@\\p{XDigit}+$"),
												"Key must override toString() with meaningful output: " + searchKey)

						))
				.processOperationWithResult(map -> map.getOptionallyWithMessage(searchKey, keyNotFoundMessage))//
				.andMakeStackTraceUnderTheName("getOptionallyWithMessage")//
				.getResultProccess();
	}

	@Override
	public Map<K, V> remove(final K mapKey) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(mapKey)//
						.isNonNull("mapKey"))//
				.processOperation(map -> map.remove(mapKey))//
				.andMakeStackTraceUnderTheName("remove")//
				.mapTo(Map::makeTypeSafe);//
	}

	// TODO NOT EFFICENT
	@Override
	public <B> Map<K, B> mapValues(final Function<V, B> valueMapper) {
		return from(source)//
				.checkCondition(() -> If.givenObject(valueMapper).isNonNull("valueMapper"))

				.processOperationWithResult(map -> map.mapValues(valueMapper))//
				.andMakeStackTraceUnderTheName("mapValues")//
				.mapTo(Map::makeTypeSafe)//
		;//
	}

	@Override
	public List<Tuple2<K, V>> toList() {
		return from(source)//
				.processOperation(MapImp::toList)//
				.andMakeStackTraceUnderTheName("toList")//
				.getOrConvertToFailureState(List::failureWithMessage);
	}

	@Override
	public List<K> keys() {
		return from(source)//
				.processOperation(MapImp::keys)//
				.andMakeStackTraceUnderTheName("keys")//
				.getOrConvertToFailureState(List::failureWithMessage);
	}

	public List<K> keysList() {
		return from(source)//
				.processOperation(MapImp::keys)//
				.andMakeStackTraceUnderTheName("keysList")//
				.getOrConvertToFailureState(List::failureWithMessage);
	}

	@Override
	public List<V> values() {
		return from(source)//
				.processOperation(MapImp::values)//
				.andMakeStackTraceUnderTheName("values")//
				.getOrConvertToFailureState(List::failureWithMessage);
	}

	@Override
	public List<K> keysForValue(final V searchValue) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(searchValue)//
						.isNonNull("searchValue"))//
				.processOperation(map -> map.keysForValue(searchValue))//
				.andMakeStackTraceUnderTheName("keysForValue")//
				.getOrConvertToFailureState(List::failureWithMessage);
	}

	private interface MapImpMethods<K, V> {

		MapImp<K, V> add(final K key, final V value);

		boolean isEmpty();

		int size();

		boolean containsKey(final K key);

		boolean containsValue(final V value);

		Result<V> getOptionally(final K key);

		Result<V> getOptionallyWithMessage(final K key, final String messageWhenKeyNotFound);

		MapImp<K, V> remove(final K key);

		<B> Result<MapImp<K, B>> mapValues(final Function<V, B> f);

		List<Tuple2<K, V>> toList();

		List<K> keys();

		List<V> values();

		List<K> keysForValue(final V value);

	}

	private static class MapImp<K, V> implements MapImpMethods<K, V> {

		private final HashMap<K, V> delegate;

		private MapImp(final HashMap<K, V> delegate) {
			this.delegate = delegate;
		}

		@SuppressWarnings("rawtypes")
		private static final MapImp EMPTY_MAP_IMP = new MapImp<>(new HashMap<>());

		@SuppressWarnings("unchecked")
		public static <K, V> MapImp<K, V> emptyMapImp() {
			return EMPTY_MAP_IMP;
		}

		@Override
		public MapImp<K, V> add(final K key, final V value) {
			final var copy = new HashMap<>(delegate);
			copy.put(key, value);
			return new MapImp<>(copy);
		}

		@Override
		public MapImp<K, V> remove(final K key) {
			if (!delegate.containsKey(key))
				return this;
			final var copy = new HashMap<>(delegate);
			copy.remove(key);
			return new MapImp<>(copy);
		}

		@Override
		public boolean containsKey(final K key) {
			return delegate.containsKey(key);
		}

		@SuppressWarnings("unchecked")
		private boolean isEqualTo(final Object other) {
			return delegate.equals(((MapImp<K, V>) other).delegate);

		}

		@Override
		public Result<V> getOptionally(final K key) {

			final var r = delegate.get(key);

			if (r == null)
				return Map.Errors//
						.CastumArgumentMessage//
						.ERROR_DEFAULT_GET_OPTIONALLY//
						.withArgument(key)//
						.asResult();

			return Result.success(r);

		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public int size() {
			return delegate.size();
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o)
				return true;
			if (!(o instanceof MapImp))
				return false;
			final MapImp<?, ?> other = (MapImp<?, ?>) o;
			return delegate.equals(other.delegate);
		}

		@Override
		public int hashCode() {
			return delegate.hashCode();
		}

		@Override
		public boolean containsValue(final V value) {
			return delegate.containsValue(value);
		}

		@Override
		public Result<V> getOptionallyWithMessage(final K key, final String messageWhenKeyNotFound) {
			if (delegate.containsKey(key))
				return Result.success(delegate.get(key));
			return Result.failure(messageWhenKeyNotFound);

		}

		@Override
		public <B> Result<MapImp<K, B>> mapValues(final Function<V, B> f) {
			try {

				final var mapped = new HashMap<K, B>();
				for (final var entry : delegate.entrySet()) {
					final var enteryValue = f.safeApplyOn(entry.getValue());
					if (enteryValue.isFailure())
						return enteryValue.mapFailureForOtherObject();

					mapped.put(entry.getKey(), f.apply(entry.getValue()));
				}
				final var newMapImp = new MapImp<>(mapped);
				return Result.success(newMapImp);
			} catch (final Exception e) {
				return Result.failure("Mapping function failed: " + e.getMessage());
			}
		}

		@Override
		public List<Tuple2<K, V>> toList() {
			// Assuming List is your custom immutable list, build from entries
			var builder = List.<Tuple2<K, V>>emptyList();
			for (final var entry : delegate.entrySet()) {
				builder = builder.cons(Tuple2.of(entry.getKey(), entry.getValue()).getResult().successValue());
			}
			return builder.reverse(); // Because cons adds at front

		}

		@Override
		public List<K> keys() {
			var builder = List.<K>emptyList();
			for (final K key : delegate.keySet()) {
				builder = builder.cons(key);
			}
			return builder.reverse();
		}

		@Override
		public List<V> values() {
			var builder = List.<V>emptyList();
			for (final V value : delegate.values()) {
				builder = builder.cons(value);
			}
			return builder.reverse();
		}

		@Override
		public List<K> keysForValue(final V value) {
			var builder = List.<K>emptyList();
			for (final var entry : delegate.entrySet()) {
				if (Objects.equals(entry.getValue(), value)) {
					builder = builder.cons(entry.getKey());
				}
			}
			return builder.reverse();
		}

	}

	public static class Errors {

		public enum GeneralMessage implements ErrorOptions {
			ERROR_ELEMENT_NOT_FOUND("No element satisfying the function in list\n(Element not found)"),
			ERROR_KEY_MUST_OVERRIDE_TO_STRING("Key must override toString() with meaningful output");

			String message;

			GeneralMessage(final String message) {
				this.message = message;
			}

			@Override
			public String getMessage() {
				return message;
			}

		}

		public enum CastumArgumentMessage {
			ERROR_DEFAULT_GET_OPTIONALLY("the key ###object### was not found.");

			String message;

			CastumArgumentMessage(final String message) {
				this.message = message;
			}

			public <Q> ErrorOptions withArgument(final Q argumentName) {
				return () -> message.replaceAll("###.*###", argumentName.toString());
			}

		}

		private enum CastumArgumentNameMessage {
			ERROR_PROCESSING_JOINED_MAP("The process of ###joinedSet### argument is NOT Success."),
			ERROR_NOT_INSTANCE_OF_MAP("###object### argument is NOT an instance of Set.");

			String message;

			CastumArgumentNameMessage(final String message) {
				this.message = message;
			}

			public ErrorOptions withArgumentName(final String argumentName) {
				return () -> message.replaceAll("###.*###", argumentName);
			}

		}

	}

}

//private static class MapImp<K, V> implements MapImpMethods<K, V> {
//
//	private final Tree<MapEntry<Integer, List<Tuple2<K, V>>>> delegate;
//
//	private MapImp() {
//		this.delegate = Tree.<MapEntry<Integer, List<Tuple2<K, V>>>>emptyTree();
//	}
//
//	private MapImp(final Tree<MapEntry<Integer, List<Tuple2<K, V>>>> delegate) {
//		this.delegate = delegate;
//	}
//
//	@SuppressWarnings("rawtypes")
//	private static MapImp EMPTY_MAP_IMP = new MapImp<>(Tree.emptyTree());
//
//	@SuppressWarnings("unchecked")
//	public static <K, V> MapImp<K, V> emptyMapImp() {
//		return EMPTY_MAP_IMP;
//	}
//
//	public static <K, V> MapImp<K, V> of(final Tree<MapEntry<Integer, List<Tuple2<K, V>>>> delegate) {
//		return new MapImp<>(delegate);
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return delegate.isEmpty().successValue();
//	}
//
//	@Override
//	public int size() {
//		return delegate.size().successValue();
//	}
//
//	@Override
//	public int hashCode() {
//		return delegate.hashCode();
//	}
//
//	// TODO find other alternative for using Java HashSet
//	@SuppressWarnings("unchecked")
//	private boolean isEqualTo(final Object other) {
//		final var otherSet = (MapImp<K, V>) other;
//
//		return Tuple2.<MapImp<K, V>, MapImp<K, V>>of(this, otherSet)//
//
//				.flatMapTo(t -> t.map(MapImp::toList, MapImp::toList))//
//				.mapTo(Tuple2::isStateAndValueEqual)//
//				.getResult()//
//				.successValue();
//
//	}
//
//	@Override
//	public boolean equals(final Object other) {
//		return isEqualTo(other);
//	}
//
//	@Override
//	public String toString() {//
//		return String.format("Map[%s]", this.toList()//
//
//				.map(t -> "key: " + t.state() + " -> value: " + t.value())//
//				.mkStr(",\n")//
//				.successValue());//
//	}
//
//	@Override
//	public List<Tuple2<K, V>> toList() {
//
//		final List<List<Tuple2<K, V>>> result =
//
//				this.delegate.inOrderTraversal().<List<Tuple2<K, V>>>
//
//						map(entry ->
//
//						entry.value.getOrElse(List.<Tuple2<K, V>>emptyList()));//
//		return result.combineNestedLists(t -> t); // flattenListOfLists;
//
//	}
//
//	@Override
//	public List<K> keys() {
//		return this.toList().map(Tuple2::state);
//	}
//
//	@Override
//	public List<V> values() {
//		return this.toList().map(Tuple2::value);
//	}
//
//	@Override
//	public List<K> keysForValue(final V value) {
//		return this.toList()//
//				.filter(tuple -> tuple.value().equals(value))//
//				.map(Tuple2::state);
//	}
//
//	private List<Tuple2<K, V>> getAll(final K key) {
//		final var result = delegate.findValue(MapEntry.mapEntry(key.hashCode()))//
//
//				.flatMap(x -> x.value)
//
//		;//
//		return result
//
//				.getOrCreateFailureInstanceWithMessage(
//
//						s -> List.failureWithMessage("error find value: " + key));
//
//	}
//
//	/**
//	 *
//	 *
//	 * @see Map#contains()
//	 */
//	@Override
//	public boolean containsKey(final K key) {
//		return //
//
//		keys().filter(t -> t.equals(key)).isNotEmpty().successValue();
//
//	}
//
//	@Override
//	public boolean containsValue(final V value) {
//		return values() //
//				.filter(t -> t.equals(value)).isNotEmpty().successValue();
//
//	}
//
//	/*
//	 * the add method in the Map class not only adds a new key-value pair but also
//	 * serves as an update method if the key already exists. If the key already
//	 * exists in the map, the method updates the value associated with that key. If
//	 * the key does not exist, a new key-value pair is added.
//	 *
//	 */
//
//	@Override
//	public MapImp<K, V> add(final K key, final V value) {
//		final var keyAndValue = Tuple2.<K, V>of(key, value)
//
//				.getResult().successValue();
//
//		final var identity = List.<Tuple2<K, V>>list(keyAndValue);
//		final var ltkv =
//
//				this.getAll(key)//
//						.getOrElse(List.emptyList())//
//						.<List<Tuple2<K, V>>>foldLeft(identity, l -> t ->
//
//						If.isItTrue(t.state().equals(key))//
//								.will()//
//								.returnValue(() -> l)//
//								.orGet(() -> l.cons(t))
//
//						).successValue();
//
//		final var mapEntry = MapEntry.mapEntry(key.hashCode(), ltkv);
//		return MapImp.of(delegate.insertValue(mapEntry));
//	}
//
//	@Override
//	public MapImp<K, V> remove(final K key) {
//		final var ltkv = this.getAll(key)//
//				.getOrElse(List.emptyList())//
//				.<List<Tuple2<K, V>>>foldLeft(List.<Tuple2<K, V>>emptyList(),
//						l -> t -> If.isItTrue(t.state().equals(key))//
//								.will()//
//								.returnValue(() -> l)//
//								.orGet(() -> l.cons(t))//
//
//				).successValue();
//
//		return If.isItTrue(ltkv.isEmpty().successValue())//
//				.will()//
//				.returnValue(() -> new MapImp<>(delegate.deleteValue(MapEntry.mapEntry(key.hashCode()))))//
//				.orGet(() -> new MapImp<>(delegate.insertValue(MapEntry.mapEntry(key.hashCode(), ltkv))));//
//	}
//
//	@Override
//	public Result<V> getOptionally(final K key) {
//		return getOptionallyWithMessage(key,
//				Errors.CastumArgumentMessage.ERROR_DEFAULT_GET_OPTIONALLY.withArgument(key).getMessage()
//
//		);
//	}
//
//	@Override
//	public Result<V> getOptionallyWithMessage(final K key, final String messageWhenKeyNotFound) {
//		return this.getAll(key)//
//				.getOrElse(List.emptyList())
//				.flatMapListToResultOf(lt -> lt.first(t -> t.state().equals(key), messageWhenKeyNotFound)
//
//						.removeCalledByMethodPrefixes())//
//				.map(Tuple2::value)//
//		;
//	}
//
//	@Override
//	public <B> Result<MapImp<K, B>> mapValues(final Function<V, B> f) {
//
//		final var valuesMapped = this.values().map(f);
//
//		return If.givenObject(valuesMapped.getListResult()).is(Result::isFailure).will()//
//				.<Result<MapImp<K, B>>>//
//				returnValue(() ->
//
//				valuesMapped.getListResult().removeCalledByMethodPrefixes().mapFailureForOtherObject()
//
//				).orGet(() ->
//
//				this.keys().zipAsPossible(valuesMapped)
//
//						.foldLeft(MapImp.<K, B>emptyMapImp(), //
//								acc -> entry -> acc.add(entry.state(), entry.value())));
//
//	}
//
//}
