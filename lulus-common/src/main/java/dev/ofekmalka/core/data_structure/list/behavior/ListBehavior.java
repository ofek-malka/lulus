package dev.ofekmalka.core.data_structure.list.behavior;

import java.util.Collection;
import java.util.stream.Stream;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.data_structure.map.Map;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface ListBehavior<A> {
	public interface ExtendedFactoryOperationsHandler

	{

		public interface PrimitiveListFactory extends ExtendedFactoryOperationsHandler {
			List<Boolean> createBooleanList(boolean... booleanValues);

			List<Byte> createByteList(byte... byteValues);

			List<Integer> createIntList(int... intValues);

			List<Character> createCharList(char... charValues);

			List<Short> createShortList(short... shortValues);

			List<Float> createFloatList(float... floatValues);

			List<Double> createDoubleList(double... doublesValues);

			List<Long> createLongList(long... longsValues);
		}

		public interface CollectionFactory extends ExtendedFactoryOperationsHandler {
			<A> List<A> createFromCollection(Collection<A> collection);

			<A> List<A> createFromStreamAndRemoveNullsValues(Stream<A> stream);

			<A> List<A> createFilledList(int numberOfElements, Supplier<A> elementSupplier);
		}

		public interface RecursiveFactory extends ExtendedFactoryOperationsHandler {
			<A> List<A> iterateWithSeed(A seed, Function<A, A> transformationFunction, int iterations);

			<A, S> List<A> unfoldFromSeed(S initialSeed, Function<If<S>, Condition<S>> generatorFunction,
					Function<S, Tuple2<A, S>> successOutcome);
		}

		public interface MathFactory extends ExtendedFactoryOperationsHandler {
			List<Integer> generateRange(int startInclusive, int endExclusive);

		}

		public interface TupleFactory extends ExtendedFactoryOperationsHandler {
			<A1, A2> Result<Tuple2<List<A1>, List<A2>>> unzipListOfTuples(List<Tuple2<A1, A2>> tupleList);
		}

	}

	public interface Operations<A>

	{

		public interface Basic<A> extends Operations<A> {
			Result<Boolean> isEmpty();

			Result<Boolean> isNotEmpty();

			Result<Integer> size();

			Result<A> firstElementOption();

			Result<String> representList();
		}

		public interface Mutation<A> extends Operations<A> {
			List<A> setFirstElement(A firstElement);

			List<A> restElements();

			List<A> setElementAtIndex(int index, A element);

			List<A> removeByIndex(int index);

			List<A> trimLast();

			List<A> updatedAllBetween(int fromIndex, int untilIndex, A element);

			List<A> updatedAllFrom(int index, A element);

			List<A> updatedAllUntil(int untilIndex, A element);
		}

		public interface ElementQuerying<A> extends Operations<A> {
			Result<A> first(Predicate<A> predicate);

			Result<A> first(Predicate<A> predicate, String messageWhenElementNotFound);

			Result<Integer> indexOf(A element);

			Result<A> lastOption();

			Result<Boolean> hasDuplicatedElements();

			Result<Boolean> isPalindrome();

			Result<Boolean> allEqual();

			Result<Boolean> contains(A element);

			Result<Boolean> isEqualTo(Object other);

			Result<Boolean> isEqualsWithoutConsiderationOrder(List<A> otherList);

			Result<Boolean> allEqualTo(A element);

			Result<Boolean> endsWith(List<A> suffix);

			Result<Integer> indexWhere(Predicate<A> predicate);

			Result<String> mkStr(String sep);
		}

		public interface Filtering<A> extends Operations<A> {
			List<A> filter(Predicate<A> predicate);

			List<A> exclude(Predicate<A> predicate);
		}

		public interface Aggregation<A> extends Operations<A> {
			<B> Result<B> foldLeft(B identity, Function<B, Function<A, B>> accumulator);

			<B> Result<B> foldRight(B identity, Function<A, Function<B, B>> accumulator);

			<B> List<B> scanLeft(B zero, Function<B, Function<A, B>> accumulatorOp);

			<B> List<B> scanRight(B zero, Function<A, Function<B, B>> accumulatorOp);
		}

		public interface Transformation<A> extends Operations<A> {
			<B> List<B> map(Function<A, B> elementTransformer);


			<A1, A2> Result<Tuple2<List<A1>, List<A2>>> unzip(Function<A, Tuple2<A1, A2>> unzipper);
		}

		public interface MonadicTransformation<A> extends Operations<A> {
			<B> List<B> sequence(Function<A, Result<B>> transformer);

			<B> List<B> combineNestedLists(Function<A, List<B>> elementTransformer);

			Result<A> reduce(Function<A, Function<A, A>> elementTransformer);

			Result<Tuple2<List<A>, List<A>>> partition(Predicate<A> partitioner);
		}

		public interface Grouping<A> extends Operations<A> {
			<B> Map<B, List<A>> groupBy(Function<A, B> keyMapper);

			<B> Map<A, B> groupByZippingValuesAsPossible(List<B> joinedList);
		}

		public interface Appending<A> extends Operations<A> {
			List<A> addArray(A... array);

			List<A> addElement(A element);

			List<A> addList(List<A> joinedList);

			List<A> cons(A element);

			List<A> consList(List<A> joinedList);
		}

		public interface Structural<A> extends Operations<A> {
			Result<Tuple2<List<A>, List<A>>> duplicate();

			List<A> last();
		}

		public interface Slicing<A> extends Operations<A> {
			List<A> takeAtMost(int n);

			List<A> dropAtMost(int n);

			List<A> takeRightAtMost(int n);

			List<A> dropRightAtMost(int n);

			List<A> getSublistInRange(int from, int until);

			List<A> takeWhile(Predicate<A> predicate);

			List<A> dropWhile(Predicate<A> predicate);

			List<A> takeRightWhile(Predicate<A> predicate);

			List<A> dropRightWhile(Predicate<A> predicate);
		}

		public interface Reordering<A> extends Operations<A> {

			List<A> reverse();

			List<A> shuffle();

			List<A> intersperse(A element);

			List<A> interleave(List<A> joinedList);
		}

		public interface Zipping<A> extends Operations<A> {
			List<Tuple2<A, Integer>> zipWithPosition();

			List<Tuple2<A, Integer>> zipWithPosition(int startIndex);

			<B> List<Tuple2<A, B>> zipAsPossible(List<B> joinedList);
		}

		public interface Conversion<A> extends Operations<A> {


			dev.ofekmalka.core.data_structure.set.Set<A> toSet();


			dev.ofekmalka.tools.stream.Stream<A> toStream();


			<L extends java.util.List<A>> Result<L> toJavaList(Supplier<L> listSupplier);

		}

		public interface ErrorHandling<A> extends Operations<A> {
			List<A> getOrElse(List<A> alternative);

			List<A> IfProcessFailGetEmptyList();

			Result<List<A>> getListResult();

			String getErrorMessageIfProcessFail();

			boolean isProcessSuccess();

			boolean isProcessFail();

			boolean isNotSuccess();

			<T> Result<T> mapListToResultOf(final Supplier<T> f);

			<T> Result<T> mapListToResultOf(final Function<List<A>, T> f);

			<T> Result<T> flatMapListToResultOf(final Supplier<Result<T>> f);

			<T> Result<T> flatMapListToResultOf(final Function<List<A>, Result<T>> f);
		}

		public interface SubList<A> extends Operations<A> {
			Result<Boolean> startsWith(List<A> sub);

			Result<Boolean> hasSubList(List<A> sub);
		}

		public interface PredicateMatching<A> extends Operations<A> {
			Result<Boolean> anyMatch(Predicate<A> predicate);

			Result<Boolean> allMatch(Predicate<A> predicate);

			Result<Boolean> noneMatch(Predicate<A> predicate);
		}

		public interface Padding<A> extends Operations<A> {
			List<Result<A>> paddingRightWithEmptyResult(int targetLength);

			List<Result<A>> paddingLeftWithEmptyResult(int targetLength);
		}

		public interface Splitting<A> extends Operations<A> {
			Result<Tuple2<A, List<A>>> firstElementAndRestElementsOption();

			Result<Tuple2<List<A>, List<A>>> splitAt(int index);
		}

		public interface Distincting<A> extends Operations<A> {
			List<A> distinct();
		}

	}

}
