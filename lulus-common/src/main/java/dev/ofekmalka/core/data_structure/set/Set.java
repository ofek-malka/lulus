package dev.ofekmalka.core.data_structure.set;

import static dev.ofekmalka.core.assertion.PreconditionedProcess.from;

import java.util.ArrayList;
import java.util.HashSet;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.PreconditionedProcess.PreconditionStage;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.error.ErrorOptions;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.tools.tuple.Tuple2;

public final class Set<T> implements //
		SetBehavior.Basic<T>, //
		SetBehavior.Queries<T>, //
		SetBehavior.Relations<T>, //
		SetBehavior.Algebra<T>, //
		SetBehavior.Joins<T>, //
		SetBehavior.ResultProvider<T> {

	private final Result<SetImp<T>> source;

	public Result<SetImp<T>> getSource() {
		return source;
	}

	private Set(final Result<SetImp<T>> source) {
		this.source = source;
	}

	@SuppressWarnings("rawtypes")
	private static Set EMPTY_SET = Set.makeTypeSafe(SetImp.emptySetImp());

	@SuppressWarnings("unchecked")
	public static <T> Set<T> emptySet() {
		return EMPTY_SET;
	}

	private static <T> Set<T> makeTypeSafe(final Result<SetImp<T>> source) {
		return new Set<>(source);
	}

	private static <T> Set<T> makeTypeSafe(final SetImp<T> source) {
		return new Set<>(Result.success(source));
	}

	private SetImp<T> getSetImpSource() {
		return source.successValue();
	}

	public static <T> Set<T> makeFailureInstance() {
		return Set.makeTypeSafe(Result.failure("failure instance"));
	}

	public static <T> Set<T> makeFailureInstanceWithMessage(final String message) {
		return Set.makeTypeSafe(Result.failure(message));
	}

	@Override
	public Result<Set<T>> getSetResult() {
		return source.map(Set::makeTypeSafe);
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
		final var errorProcessingMessage = Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_SET
				.withArgumentName(argName).getMessage();
		final var errorInstanceOf =

				Errors.CastumArgumentMessage.ERROR_NOT_INSTANCE_OF_SET.withArgumentName(argName).getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(other)//
						.isNonNull(argName)//
						.andIs(o -> o instanceof Set, errorInstanceOf)//
						.andIs(o -> ((Set<T>) o).isProcessSuccess(), errorProcessingMessage))
				.processOperation(l -> l.isEqualTo(((Set<T>) other).getSetImpSource()))
				.andMakeStackTraceUnderTheName("isEqualTo")//
				.getResultProccess();

	}

	@Override
	public int hashCode() {
		return source//
				.map(SetImp::hashCode)//
				.casesForProvidedHashCode()//
				.onSuccess(t -> t)//
				.onOtherOptionProvideClassName("Set")//
		;
	}

	@Override
	public String toString() {
		return If.isItTrue(source.isFailure())//
				.will()//
				.returnValue(() -> "Set is in a failed state and cannot generate a valid representation for toString."
						+ "\nPlease check the process status for more details.")//
				.orGet(() -> source.map(SetImp::toString).successValue());
	}

	@Override
	public Set<T> insert(final T element) {
		return from(source)//
				.checkCondition(() -> If.givenObject(element).isNonNull("element"))//
				.processOperation(s -> s.insert(element))//
				.andMakeStackTraceUnderTheName("insert")//
				.mapTo(Set::makeTypeSafe);//

	}

	@Override
	public Set<T> delete(final T element) {
		return from(source)//
				.checkCondition(() -> If.givenObject(element).isNonNull("element"))//
				.processOperation(s -> s.delete(element))//
				.andMakeStackTraceUnderTheName("delete")//
				.mapTo(Set::makeTypeSafe);//

	}

	@Override
	public Result<Integer> cardinality() {
		return from(source)//
				.processOperation(SetImp::cardinality)//
				.andMakeStackTraceUnderTheName("cardinality")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> isEmpty() {
		return from(source)//
				.processOperation(SetImp::isEmpty)//
				.andMakeStackTraceUnderTheName("isEmpty")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> contains(final T element) {
		return from(source)//
				.checkCondition(() -> If.givenObject(element).isNonNull("element"))
				.processOperation(set -> set.contains(element))//
				.andMakeStackTraceUnderTheName("contains")//
				.getResultProccess();
	}

	private <B> PreconditionStage<SetImp<T>> checkingNullAndProcessingOtherSet(final Set<B> other) {
		final var errorProcessingMessage = Errors.CastumArgumentMessage//
				.ERROR_PROCESSING_JOINED_SET//
				.withArgumentName("other")//
				.getMessage();//

		return from(source)//
				.checkCondition(() -> //
				If.givenObject(other)//
						.isNonNull("other")//
						.andIs(Set::isProcessSuccess, errorProcessingMessage));

	}

	@Override
	public Result<Boolean> isSubsetOf(final Set<T> other) {
		return checkingNullAndProcessingOtherSet(other).processOperation(set -> set.isSubsetOf(other.getSetImpSource()))//
				.andMakeStackTraceUnderTheName("isSubsetOf")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> isSupersetOf(final Set<T> other) {
		return checkingNullAndProcessingOtherSet(other)
				.processOperation(set -> set.isSupersetOf(other.getSetImpSource()))//
				.andMakeStackTraceUnderTheName("isSupersetOf")//
				.getResultProccess();
	}

	@Override
	public Set<T> intersect(final Set<T> other) {

		return checkingNullAndProcessingOtherSet(other)//
				.processOperation(set -> set.intersect(other.getSetImpSource()))//
				.andMakeStackTraceUnderTheName("intersect")//
				.mapTo(Set::makeTypeSafe);

	}

	@Override
	public Set<T> union(final Set<T> other) {
		return checkingNullAndProcessingOtherSet(other)//
				.processOperation(set -> set.union(other.getSetImpSource()))//
				.andMakeStackTraceUnderTheName("union")//
				.mapTo(Set::makeTypeSafe);

	}

	@Override
	public <B> Set<Tuple2<T, B>> crossJoin(final Set<B> other) {
		return checkingNullAndProcessingOtherSet(other)//
				.processOperationWithResult(//
						set -> set.crossJoin(other.getSetImpSource()))//
				.andMakeStackTraceUnderTheName("crossJoin")//
				.mapTo(Set::makeTypeSafe);//
	}

	@Override
	public <B> Set<Tuple2<T, B>> innerJoin(final Set<B> other, final Function<T, Predicate<B>> matchCondition) {
		final var errorProcessingMessage = Errors.CastumArgumentMessage//
				.ERROR_PROCESSING_JOINED_SET//
				.withArgumentName("other")//
				.getMessage();//

		return from(source)//
				.checkCondition(() -> //
				If.givenObject(other)//
						.isNonNull("other")//
						.andIs(Set::isProcessSuccess, errorProcessingMessage)
						.andOtherObjectIsNotNull(matchCondition, "matchCondition"))
				.processOperationWithResult(//
						set -> set.innerJoin(other.getSetImpSource(), matchCondition))//
				.andMakeStackTraceUnderTheName("innerJoin")//
				.mapTo(Set::makeTypeSafe);

		// .convertTo(Set::makeTypeSafe);//
	}

	@Override
	public Set<T> difference(final Set<T> other) {

		return checkingNullAndProcessingOtherSet(other)//
				.<Set<T>>processOperationBySupplierResult(() ->

				this.toList()//
						.filter(element -> !other.contains(element).successValue())//
						.foldLeft(Set.<T>emptySet(), set -> element -> set.insert(element))//

				)//
				.andMakeStackTraceUnderTheName("difference")//
				.getOrConvertToFailureState(Set::makeFailureInstanceWithMessage);

	}

	@Override
	public Set<T> symmetricDifference(final Set<T> other) {

		return checkingNullAndProcessingOtherSet(other).<Set<T>>processOperationBySupplierResult(() -> {

			final var onlyInThis = this.difference(other);
			final var onlyInOther = other.difference(this);
			return Result.success(onlyInThis.union(onlyInOther));
		})//
				.andMakeStackTraceUnderTheName("symmetricDifference")//
				.getOrConvertToFailureState(Set::makeFailureInstanceWithMessage);

	}

	@Override
	public List<T> toList() {
		return from(source)//
				.processOperation(SetImp::toList)//
				.andMakeStackTraceUnderTheName("toList")//
				.getOrConvertToFailureState(List::failureWithMessage);
	}

	private interface SetImpMethods<T> {

		SetImp<T> insert(final T element);

		List<T> toList();

		<B> Result<SetImp<Tuple2<T, B>>> crossJoin(SetImp<B> set);

		<B> Result<SetImp<Tuple2<T, B>>> innerJoin(SetImp<B> set, Function<T, Predicate<B>> f);

		boolean isEmpty();

		int cardinality();

		boolean contains(final T element);

		SetImp<T> delete(final T element);

		boolean isSubsetOf(SetImp<T> other);

		boolean isSupersetOf(SetImp<T> other);

		SetImp<T> intersect(final SetImp<T> other);

		SetImp<T> union(final SetImp<T> other);

	}

	private static class SetImp<T> implements SetImpMethods<T> {

		private final HashSet<T> delegate;

		private SetImp(final HashSet<T> delegate) {
			this.delegate = delegate;
		}

		@SuppressWarnings("unchecked")
		private boolean isEqualTo(final Object other) {
			return delegate.equals(((SetImp<T>) other).delegate);

		}

		@SuppressWarnings("rawtypes")
		private static final SetImp EMPTY_SET_IMP = new SetImp<>(new HashSet<>());

		@SuppressWarnings("unchecked")
		public static <T> SetImp<T> emptySetImp() {
			return EMPTY_SET_IMP;
		}

		@Override
		public SetImp<T> insert(final T element) {
			final var copy = new HashSet<>(delegate);
			copy.add(element);
			return new SetImp<>(copy);
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public boolean contains(final T element) {
			return delegate.contains(element);
		}

		@Override
		public SetImp<T> delete(final T element) {
			final var copy = new HashSet<>(delegate);
			copy.remove(element);
			return new SetImp<>(copy);
		}

		@Override
		public List<T> toList() {
			return List.extendedFactoryOperations().createFromCollection(delegate);
		}

		@Override
		public int cardinality() {
			return delegate.size();
		}

		@Override
		public boolean isSubsetOf(final SetImp<T> other) {
			return delegate.stream().allMatch(other.delegate::contains);
		}

		@Override
		public boolean isSupersetOf(final SetImp<T> other) {
			if (other.isEmpty())
				return true;
			return other.delegate.stream().allMatch(delegate::contains);
		}

		@Override
		public SetImp<T> intersect(final SetImp<T> other) {
			final var result = new HashSet<T>();
			for (final T e : delegate) {
				if (other.delegate.contains(e)) {
					result.add(e);
				}
			}
			return new SetImp<>(result);
		}

		@Override
		public SetImp<T> union(final SetImp<T> other) {
			final var result = new HashSet<>(delegate);
			result.addAll(other.delegate);
			return new SetImp<>(result);
		}

		@Override
		public <B> Result<SetImp<Tuple2<T, B>>> crossJoin(final SetImp<B> set) {
			final java.util.List<Tuple2<T, B>> list = new ArrayList<>();
			for (final T a : delegate) {
				for (final B b : set.delegate) {
					final var tuple = Tuple2.of(a, b).getResult().successValue();
					list.add(tuple);
				}
			}
			return Result.success(SetImp.of(list));
		}

		@Override
		public <B> Result<SetImp<Tuple2<T, B>>> innerJoin(final SetImp<B> set,
				final Function<T, Predicate<B>> matchCondition) {

			final java.util.List<Tuple2<T, B>> filtered = new ArrayList<>();

			for (final T a : delegate) {
				for (final B b : set.delegate) {

					final var funResult = matchCondition.safeApplyOn(a);
					if (funResult.isFailure())
						return funResult.removeCalledByMethodPrefixes().mapFailureForOtherObject();
					final var predicate = funResult.successValue();

					final var predicateResult = predicate.safeApplyOn(b);

					if (predicateResult.isFailure())
						return predicateResult.removeCalledByMethodPrefixes().mapFailureForOtherObject();
					if (predicateResult.successValue()) {
						filtered.add(Tuple2.of(a, b).getResult().successValue());
					}

				}
			}
			return Result.success(SetImp.of(filtered));

		}

		@Override
		public boolean equals(final Object other) {
			if (this == other)
				return true;
			if (!(other instanceof SetImp))
				return false;
			final SetImp<?> that = (SetImp<?>) other;
			return delegate.equals(that.delegate);
		}

		@Override
		public int hashCode() {
			return delegate.hashCode();
		}

		// Helper factory from your existing List<T> to SetImp<T>
		private static <T> SetImp<T> of(final java.util.List<T> list) {
			final var set = new HashSet<>(list);
			return new SetImp<>(set);
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

	}

	public static class Errors {
		public enum CastumArgumentMessage {
			ERROR_PROCESSING_JOINED_SET("The process of ###joinedSet### argument is NOT Success."),
			ERROR_NOT_INSTANCE_OF_SET("###object### argument is NOT an instance of Set.");

			String message;

			CastumArgumentMessage(final String message) {
				this.message = message;
			}

			public ErrorOptions withArgumentName(final String argumentName) {
				return () -> message.replaceAll("###.*###", argumentName);
			}

		}

	}

}
//private static class SetImp<T> implements SetImpMethods<T> {
//
//	private final Tree<MapEntry<Integer, List<T>>> delegate;
//
//	private SetImp(final Tree<MapEntry<Integer, List<T>>> delegate) {
//		this.delegate = delegate;
//	}
//
//	@SuppressWarnings("rawtypes")
//	private static SetImp EMPTY_SET_IMP = new SetImp<>(Tree.emptyTree());
//
//	@SuppressWarnings("unchecked")
//	public static <T> SetImp<T> emptySetImp() {
//		return EMPTY_SET_IMP;
//	}
//
//	@Override
//	public SetImp<T> insert(final T element) {
//		final var hashCode = element.hashCode();
//		final var elements = this.getAll(hashCode).getOrElse(List.<T>emptyList())
//
//				.filter(e -> !e.equals(element)).cons(element);
//		return SetImp.of(delegate.insertValue(MapEntry.mapEntry(hashCode, elements)));
//	}
//
//	private static <T> SetImp<T> of(final List<T> list) {
//		return list.foldLeft(SetImp.<T>emptySetImp(), set -> item -> set.insert(item)).successValue();
//	}
//
//	private static <T> SetImp<T> of(final Tree<MapEntry<Integer, List<T>>> delegate) {
//		return new SetImp<>(delegate);
//	}
//
//	/**
//	 * Returns all elements in the set with the given hash code.
//	 *
//	 * @param hashCode The hash code of the elements to retrieve.
//	 * @return A list of elements with the given hash code.
//	 */
//
//	private Result<List<T>> getAll(final int hashCode) {
//		return delegate.findValue(MapEntry.mapEntry(hashCode))
//
//				.flatMap(x -> x.value.map(t -> t));
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return delegate.isEmpty().successValue();
//	}
//
//	@Override
//	public boolean contains(final T element) {
//		return this.getAll(element.hashCode()).getOrElse(List.emptyList())//
//				.contains(element).successValue();
//	}
//
//	@Override
//	public SetImp<T> delete(final T element) {
//		final var hashCode = element.hashCode();
//		final var elements = this.getAll(hashCode)//
//				.getOrElse(List.<T>emptyList())//
//				.exclude(e -> e.equals(element));
//		return SetImp.of(delegate.insertValue(MapEntry.mapEntry(hashCode, elements)));
//	}
//
//	@Override
//	public List<T> toList() {
//		return this.delegate.inOrderTraversal()
//
//				.combineNestedLists(entry -> entry.value.getOrElse(List.<T>emptyList()))
//
//		;
//	}
//
//	@Override
//	public int cardinality() {
//		return this.delegate.size().successValue();
//	}
//
//	@Override
//	public boolean isSubsetOf(final SetImp<T> other) {
//		return this.toList().allMatch(other::contains).successValue();
//
//	}
//
//	@Override
//	public boolean isSupersetOf(final SetImp<T> other) {
//		return If.isItTrue(other.isEmpty())//
//				.will()//
//				.returnValue(() -> true).orGet(() -> other.toList().allMatch(this::contains).successValue());
//
//	}
//
//	@Override
//	public SetImp<T> intersect(final SetImp<T> other) {
//		return SetImp.of(this.toList().filter(e -> other.contains(e)));
//	}
//
//	@Override
//	public SetImp<T> union(final SetImp<T> other) {
//		return other.toList().foldLeft(this, acc -> element -> acc.insert(element)).successValue();
//	}
//
//	@Override
//	public <B> Result<SetImp<Tuple2<T, B>>> crossJoin(final SetImp<B> set) {
//		return crossJoin__(set).convert().toSet().getSource();
//	}
//
//	private <B> List<Tuple2<T, B>> crossJoin__(final SetImp<B> set) {
//		return this.toList()
//				.combineNestedLists(a -> set.toList().map(b -> Tuple2.<T, B>of(a, b).getResult().successValue()));
//	}
//
//	@Override
//	public <B> Result<SetImp<Tuple2<T, B>>> innerJoin(final SetImp<B> set,
//			final Function<T, Predicate<B>> matchCondition) {
//		return this.innerJoin__(set, matchCondition)//
//				.convert().toSet()//
//				.getSource();
//	}
//
//	private <B> List<Tuple2<T, B>> innerJoin__(final SetImp<B> set,
//			final Function<T, Predicate<B>> matchCondition) {
//		return this.crossJoin__(set)//
//				.safeApplySequence(t -> //
//				matchCondition.safeApplyOn(t.state())//
//						.flatMap(func -> func.safeApplyOn(t.value())))//
//				.filter(t -> matchCondition.apply(t.state()).apply(t.value()))//
//		;
//	}
//
//	@SuppressWarnings("unchecked")
//	private boolean isEqualTo(final Object o) {
//		final var other = (SetImp<T>) o;
//
//		return Tuple2.<SetImp<T>, SetImp<T>>of(this, other)//
//				.flatMapTo(t -> t.map(SetImp::toList, SetImp::toList))//
//				.mapTo(Tuple2::isStateAndValueEqual)//
//
//				.getResult()//
//				.successValue();
//	}
//
//	@Override
//	public boolean equals(final Object other) {
//		return isEqualTo(other);
//	}
//
//	@Override
//	public int hashCode() {
//		return delegate.hashCode();
//	}
//
//	@Override
//	public String toString() {
//		return String.format(this.toList().toString()).replaceFirst("[,]?NIL", "");
//	}
//
//}
