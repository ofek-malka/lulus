package dev.ofekmalka.data_structure.list.behavior;


//public interface ListTestBehavior {
//	public interface ExtendedFactoryOperationsHandler {
//
//		public interface Creation extends ExtendedFactoryOperationsHandler {
//
//			List<Integer> list();
//
//			List<Integer> reversedList();
//
//			List<Integer> emptyList();
//
//		}
//
//		public interface PrimitiveListFactory extends ExtendedFactoryOperationsHandler {
//			List<Boolean> createBooleanList();
//
//			List<Byte> createByteList();
//
//			List<Integer> createIntList();
//
//			List<Character> createCharList();
//
//			List<Short> createShortList();
//
//			List<Float> createFloatList();
//
//			List<Double> createDoubleList();
//
//			List<Long> createLongList();
//		}
//
//		public interface CollectionFactory extends ExtendedFactoryOperationsHandler {
//			List<Integer> createFromCollection();
//
//			List<Integer> createFromStreamAndRemoveNullsValues();
//
//			List<Integer> createFilledList();
//		}
//
//		public interface RecursiveFactory extends ExtendedFactoryOperationsHandler {
//			List<Integer> iterateWithSeed();
//
//			List<Integer> unfoldFromSeed();
//		}
//
//		public interface MathFactory extends ExtendedFactoryOperationsHandler {
//			List<Integer> generateRange();
//
//		}
//
//		public interface TupleFactory extends ExtendedFactoryOperationsHandler {
//			Result<Tuple2<List<Integer>, List<Integer>>> unzipListOfTuples();
//		}
//
//	}
//
//	public interface Operations
//
//	{
//
//		public interface Basic extends Operations {
//			Result<Boolean> isEmpty();
//
//			Result<Boolean> isNotEmpty();
//
//			Result<Integer> size();
//
//			Result<Integer> firstElementOption();
//
//			Result<String> representList();
//
//		}
//
//		public interface Mutation extends Operations {
//			List<Integer> setFirstElement();
//
//			List<Integer> restElements();
//
//			List<Integer> setElementAtIndex();
//
//			List<Integer> removeByIndex();
//
//			List<Integer> trimLast();
//
//			List<Integer> updatedAllBetween();
//
//			List<Integer> updatedAllFrom();
//
//			List<Integer> updatedAllUntil();
//		}
//
//		public interface ElementQuerying extends Operations {
//			Result<Integer> first();
//
//			Result<Integer> indexOf();
//
//			Result<Integer> lastOption();
//
//			Result<Boolean> hasDuplicatedElements();
//
//			Result<Boolean> isPalindrome();
//
//			Result<Boolean> allEqual();
//
//			Result<Boolean> contains();
//
//			Result<Boolean> isEqualTo();
//
//			Result<Boolean> isEqualsWithoutConsiderationOrder();
//
//			Result<Boolean> allEqualTo();
//
//			Result<Boolean> endsWith();
//
//			Result<Integer> indexWhere();
//
//			Result<String> mkStr();
//		}
//
//		public interface Filtering extends Operations {
//			List<Integer> filter();
//
//			List<Integer> exclude();
//		}
//
//		public interface Aggregation extends Operations {
//			Result<String> foldLeft();
//
//			Result<String> foldRight();
//
//			List<Integer> scanLeft();
//
//			List<Integer> scanRight();
//		}
//
//		public interface Transformation extends Operations {
//			List<Integer> map();
//
//			List<String> updatedAllWithIndex();
//
//			Result<Tuple2<List<Integer>, List<Integer>>> unzip();
//		}
//
//		public interface MonadicTransformation extends Operations {
//			List<Integer> sequence();
//
//			List<String> combineNestedLists();
//
//			Result<Integer> reduce();
//
//			Result<Tuple2<List<Integer>, List<Integer>>> partition();
//		}
//
//		public interface Grouping extends Operations {
//			Map<Integer, List<Integer>> groupBy();
//
//			Map<Integer, Integer> groupByZippingValuesAsPossible();
//		}
//
//		public interface Appending extends Operations {
//			List<Integer> addArray(StaticListState state);
//
//			List<Integer> addElement(StaticListState state);
//
//			List<Integer> addList(StaticListState state);
//
//			List<Integer> cons(StaticListState state);
//
//			List<Integer> consList(StaticListState state);
//		}
//
//		public interface Structural extends Operations {
//			Result<Tuple2<List<Integer>, List<Integer>>> duplicate();
//
//			List<Integer> last();
//		}
//
//		public interface Slicing extends Operations {
//			List<Integer> takeAtMost();
//
//			List<Integer> dropAtMost();
//
//			List<Integer> takeRightAtMost();
//
//			List<Integer> dropRightAtMost();
//
//			List<Integer> getSublistInRange();
//
//			List<Integer> takeWhile();
//
//			List<Integer> dropWhile();
//
//			List<Integer> takeRightWhile();
//
//			List<Integer> dropRightWhile();
//		}
//
//		public interface Reordering extends Operations {
//			List<Integer> rotate();
//
//			List<Integer> reverse();
//
//			List<Integer> shuffle();
//
//			List<Integer> intersperse();
//
//			List<Integer> interleave();
//		}
//
//		public interface Zipping extends Operations {
//
//			List<Tuple2<Integer, Integer>> zipAsPossible();
//
//			List<Tuple2<Integer, Integer>> zipWithPositionWithArgument();
//		}
//
//		public interface Conversion extends Operations {
//
//			Result<ArrayList<Integer>> toJavaList(ArrayList<Integer> listSupplier);
//
//			Array<Integer> asArrayInstance();
//
//			dev.ofekmalka.core.data_structure.set.Set<Integer> toSet();
//
//			Queue<Integer> toQueue();
//
//			dev.ofekmalka.tools.stream.Stream<Integer> toStream();
//
//		}
//
//		public interface Advanced extends Operations {
//			List<List<Integer>> tailsLeft();
//
//			List<List<Integer>> initsLeft();
//
//		}
//
//		public interface SubList extends Operations {
//			Result<Boolean> startsWith();
//
//			Result<Boolean> hasSubList();
//		}
//
//		public interface PredicateMatching extends Operations {
//			Result<Boolean> anyMatch();
//
//			Result<Boolean> allMatch();
//
//			Result<Boolean> noneMatch();
//		}
//
//		public interface Padding extends Operations {
//			List<Result<Integer>> paddingRightWithEmptyResult();
//
//			List<Result<Integer>> paddingLeftWithEmptyResult();
//		}
//
//		public interface Splitting extends Operations {
//			Result<Tuple2<Integer, List<Integer>>> firstElementAndRestElementsOption();
//
//			Result<Tuple2<List<Integer>, List<Integer>>> splitAt();
//		}
//
//		public interface Distincting extends Operations {
//			List<Integer> distinct();
//		}
//
//	}
//
//}
