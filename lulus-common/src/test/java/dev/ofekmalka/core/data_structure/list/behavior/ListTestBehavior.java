package dev.ofekmalka.core.data_structure.list.behavior;

import org.assertj.core.api.BDDSoftAssertions;

public interface ListTestBehavior {
	public sealed interface ExtendedFactoryOperationsHandler

	permits

	ExtendedFactoryOperationsHandler.PrimitiveListFactory, //
	ExtendedFactoryOperationsHandler.CollectionFactory, //
	ExtendedFactoryOperationsHandler.RecursiveFactory, //
	ExtendedFactoryOperationsHandler.MathFactory, //
	ExtendedFactoryOperationsHandler.TupleFactory {

		public non-sealed interface PrimitiveListFactory extends ExtendedFactoryOperationsHandler {
			void createBooleanList(final BDDSoftAssertions softly);

			void createByteList(final BDDSoftAssertions softly);

			void createIntList(final BDDSoftAssertions softly);

			void createCharList(final BDDSoftAssertions softly);

			void createShortList(final BDDSoftAssertions softly);

			void createFloatList(final BDDSoftAssertions softly);

			void createDoubleList(final BDDSoftAssertions softly);

			void createLongList(final BDDSoftAssertions softly);
		}

		public non-sealed interface CollectionFactory extends ExtendedFactoryOperationsHandler {
			void createFromCollection(BDDSoftAssertions softly);

			void createFromStreamAndRemoveNullsValues(BDDSoftAssertions softly);

			void createFilledList(BDDSoftAssertions softly);
		}

		public non-sealed interface RecursiveFactory extends ExtendedFactoryOperationsHandler {
			void iterateWithSeed(BDDSoftAssertions softly);

			void unfoldFromSeed(BDDSoftAssertions softly);
		}

		public non-sealed interface MathFactory extends ExtendedFactoryOperationsHandler {
			void generateRange(BDDSoftAssertions softly);

		}

		public non-sealed interface TupleFactory extends ExtendedFactoryOperationsHandler {
			void unzipListOfTuples(final BDDSoftAssertions softly);
		}

	}

	public sealed interface Operations permits //
	Operations.Invalid, //
	Operations.Aggregation, //
	Operations.Basic, //
	Operations.Conversion, //
	Operations.ElementQuerying, //
	Operations.Filtering, //
	Operations.Grouping, //
	Operations.Appending, //
	Operations.Structural, //
	Operations.Mutation, //
	Operations.Transformation, //
	Operations.SubList, //
	Operations.PredicateMatching, //
	Operations.Padding, //
	Operations.Splitting, //
	Operations.Distincting, //
	Operations.Slicing, //
	Operations.Reordering, //
	Operations.Zipping, //
	Operations.MonadicTransformation// , //
	// Operations.ExtendedFactoryOperationsHandler

	{

		sealed interface Invalid extends Operations

		permits //

		Invalid.Creation, //
		Invalid.OnEmptyList,

		Invalid.OnValidNonEmptyList //
		, Invalid.ExtendedFactoryOperationsHandler

		{

			public non-sealed interface Creation extends Invalid {

				void shouldHandleInvalidListCreationScenarios(final String actualErrorMessage, //
						/*                                         */ final String expectedErrorMessage);
			}

			public sealed interface ExtendedFactoryOperationsHandler extends Invalid

			permits

			ExtendedFactoryOperationsHandler.PrimitiveListFactory, //
			ExtendedFactoryOperationsHandler.CollectionFactory, //
			ExtendedFactoryOperationsHandler.RecursiveFactory, //
			ExtendedFactoryOperationsHandler.MathFactory, //
			ExtendedFactoryOperationsHandler.TupleFactory {

				public non-sealed interface PrimitiveListFactory extends ExtendedFactoryOperationsHandler {
					void shouldHandleInvalidListCreationScenarios(final String actualErrorMessage, //
							/*                                         */ final String expectedErrorMessage);
				}

				public non-sealed interface CollectionFactory extends ExtendedFactoryOperationsHandler {
					void createFromCollection(final String methodName, final String argumentName,
							final BDDSoftAssertions softly);

					void createFromStreamAndRemoveNullsValues(final String methodName, final String argumentName,
							final BDDSoftAssertions softly);

					void createFilledList(final String methodName, final String firstArgumentName,
							final String secondArgumentName, final BDDSoftAssertions softly);
				}

				public non-sealed interface RecursiveFactory extends ExtendedFactoryOperationsHandler {
					void iterateWithSeed(final String methodName, final String firstArgumentName,
							final String secondArgumentName, final String thirdArgumentName,
							final BDDSoftAssertions softly);

					void unfoldFromSeed(final String methodName, final String firstArgumentName,
							final String secondArgumentName, final String thirdArgumentName,
							final BDDSoftAssertions softly);
				}

				public non-sealed interface MathFactory extends ExtendedFactoryOperationsHandler {
					void generateRange(final String methodName, final BDDSoftAssertions softly);

				}

				public non-sealed interface TupleFactory extends ExtendedFactoryOperationsHandler {
					void unzipListOfTuples(final String methodName, final String argumentName,
							final BDDSoftAssertions softly);
				}

			}

			sealed interface OnEmptyList extends Invalid

			permits//
			OnEmptyList.Basic, //
			OnEmptyList.Mutation, //
			OnEmptyList.ElementQuerying, //
			OnEmptyList.Splitting//
			, OnEmptyList.MonadicTransformation

			{

				public non-sealed interface Basic extends OnEmptyList {
					// void isEmpty();
					void shouldRemainEmpty(BDDSoftAssertions softly);

					void shouldDisplayTheCorrectRepresentationForEmptiness();

					void shouldReportFailureWhenAccessingTheFirstElement();

					void shouldContainNoElements();
				}

				public non-sealed interface Mutation extends OnEmptyList {

					void setElementAtIndex(String methodName, BDDSoftAssertions softly);

					void restElements(String methodName, BDDSoftAssertions softly);

					void setFirstElement(String methodName, BDDSoftAssertions softly);

					void removeByIndex(String methodName, BDDSoftAssertions softly);

					void updatedAllBetween(String methodName, BDDSoftAssertions softly);

					void updatedAllFrom(String methodName, BDDSoftAssertions softly);

					void updatedAllUntil(String methodName, BDDSoftAssertions softly);

				}

				public non-sealed interface ElementQuerying extends OnEmptyList {

					void first(String methodName, BDDSoftAssertions softly);

					void indexOf(String methodName, BDDSoftAssertions softly);

					void last(String methodName, BDDSoftAssertions softly);

					void indexWhere(String methodName, BDDSoftAssertions softly);

				}

	

				public non-sealed interface MonadicTransformation extends OnEmptyList {
					void reduce(String methodName, BDDSoftAssertions softly);
				}

				public non-sealed interface Splitting extends OnEmptyList {
					void firstElementAndRestElementsOption(String methodName, BDDSoftAssertions softly);

					void splitAt(String methodName, BDDSoftAssertions softly);
				}

			}

			sealed interface OnValidNonEmptyList extends Invalid permits//
			OnValidNonEmptyList.Aggregation, //
			OnValidNonEmptyList.ElementQuerying, //
			OnValidNonEmptyList.Filtering, //
			OnValidNonEmptyList.Grouping, //
			OnValidNonEmptyList.Mutation, //
			OnValidNonEmptyList.Transformation, //
			OnValidNonEmptyList.SubList, //
			OnValidNonEmptyList.PredicateMatching, //
			OnValidNonEmptyList.Padding, //
			OnValidNonEmptyList.Splitting, //
			OnValidNonEmptyList.Slicing, //
			OnValidNonEmptyList.MonadicTransformation, //
			OnValidNonEmptyList.Zipping, //
			OnValidNonEmptyList.Reordering, //
			OnValidNonEmptyList.Appending//
			, OnValidNonEmptyList.Conversion {

				public non-sealed interface Mutation extends OnValidNonEmptyList {

					void setFirstElement(String methodName, String argumentName, BDDSoftAssertions softly);

					void removeByIndex(String methodName, String argumentName, BDDSoftAssertions softly);

					void updatedAllBetween(String methodName, String argumentName, BDDSoftAssertions softly);

					void updatedAllFrom(String methodName, String argumentName, BDDSoftAssertions softly);

					void updatedAllUntil(String methodName, String argumentName, BDDSoftAssertions softly);

					void setElementAtIndex(String methodName, String argumentName, BDDSoftAssertions softly);
				}

				public non-sealed interface ElementQuerying extends OnValidNonEmptyList {
					void first(String methodName, String argumentName, BDDSoftAssertions softly);

					void isEqualsWithoutConsiderationOrder(String methodName, String argumentName,
							BDDSoftAssertions softly);

					void allEqualTo(String methodName, String argumentName, BDDSoftAssertions softly);

					void contains(String methodName, String argumentName, BDDSoftAssertions softly);

					void endsWith(String methodName, String argumentName, BDDSoftAssertions softly);

					void mkStr(String methodName, String argumentName, BDDSoftAssertions softly);

					void isEqualTo(String methodName, String argumentName, BDDSoftAssertions softly);

					void indexOf(String methodName, String argumentName, BDDSoftAssertions softly);

					void indexWhere(String methodName, String argumentName, BDDSoftAssertions softly);
				}

				public non-sealed interface Filtering extends OnValidNonEmptyList {

					void filtering(String methodName, String argumentName, BDDSoftAssertions softly);

					void excluding(String methodName, String argumentName, BDDSoftAssertions softly);

				}

				public non-sealed interface Aggregation extends OnValidNonEmptyList {

					void scanLeft(String methodName, String firstArgumentName, String secondArgumentName,
							BDDSoftAssertions softly);

					void scanRight(String methodName, String firstArgumentName, String secondArgumentName,
							BDDSoftAssertions softly);

					void foldLeft(String methodName, String firstArgumentName, String secondArgumentName,
							BDDSoftAssertions softly);

					void foldRight(String methodName, String firstArgumentName, String secondArgumentName,
							BDDSoftAssertions softly);
				}

				public non-sealed interface Transformation extends OnValidNonEmptyList {

					void unzip(String methodName, String argumentName, BDDSoftAssertions softly);


					void mapping(String methodName, String argumentName, BDDSoftAssertions softly);

				}

				public non-sealed interface MonadicTransformation extends OnValidNonEmptyList {
					void sequence(String methodName, String argumentName, BDDSoftAssertions softly);

					void combineNestedListsWithNullElementTransformer();

					void combineNestedListsWithNullElementTransformerProvider();

					void combineNestedListsWithRuntimeExceptionElementTransformerProvider();

					void combineNestedListsWithLimitExceededElementTransformerProvider();

					void invokeReduction(String methodName, String argumentName, BDDSoftAssertions softly);

					void partition(String methodName, String argumentName, BDDSoftAssertions softly);

				}

				public non-sealed interface Grouping extends OnValidNonEmptyList {

					void groupBy(String methodName, String argumentName, BDDSoftAssertions softly);

					void groupByZippingValuesAsPossible(String methodName, String argumentName,
							BDDSoftAssertions softly);

				}

				public non-sealed interface Zipping extends OnValidNonEmptyList {
					void zipWithPosition(String methodName, BDDSoftAssertions softly);

					void zipAsPossible(String methodName, String argumentName, BDDSoftAssertions softly);
				}

				public non-sealed interface Slicing extends OnValidNonEmptyList {

					void takeAtMost(String methodName, String argumentName, BDDSoftAssertions softly);

					void dropAtMost(String methodName, String argumentName, BDDSoftAssertions softly);

					void takeRightAtMost(String methodName, String argumentName, BDDSoftAssertions softly);

					void dropRightAtMost(String methodName, String argumentName, BDDSoftAssertions softly);

					void takeWhile(String methodName, String argumentName, BDDSoftAssertions softly);

					void dropWhile(String methodName, String argumentName, BDDSoftAssertions softly);

					void takeRightWhile(String methodName, String argumentName, BDDSoftAssertions softly);

					void dropRightWhile(String methodName, String argumentName, BDDSoftAssertions softly);

					void getSublistInRange(String methodName, BDDSoftAssertions softly);
				}

				public non-sealed interface Appending extends OnValidNonEmptyList {
					void addArray(String methodName, BDDSoftAssertions softly);

					void addElement(String methodName, BDDSoftAssertions softly);

					void addList(String methodName, String argumentName, BDDSoftAssertions softly);

					void consList(String methodName, String argumentName, BDDSoftAssertions softly);

					void addElementToTheBeginningOfTheList(String methodName, String argumentName,
							BDDSoftAssertions softly);
				}

				public non-sealed interface Conversion extends OnValidNonEmptyList {

					void toJavaList(final String methodName, final String argumentName, final BDDSoftAssertions softly);

					void toStream(final String methodName);

				}

				public non-sealed interface Reordering extends OnValidNonEmptyList {

					void interleave(String methodName, String argumentName, BDDSoftAssertions softly);

					void intersperse(String methodName, String argumentName, BDDSoftAssertions softly);

				}

				public non-sealed interface SubList extends OnValidNonEmptyList {
					void startsWith(String methodName, String argumentName, BDDSoftAssertions softly);

					void hasSubList(String methodName, String argumentName, BDDSoftAssertions softly);
				}

				public non-sealed interface PredicateMatching extends OnValidNonEmptyList {
					void anyMatch(String methodName, String argumentName, BDDSoftAssertions softly);

					void allMatch(String methodName, String argumentName, BDDSoftAssertions softly);

					void noneMatch(String methodName, String argumentName, BDDSoftAssertions softly);
				}

				public non-sealed interface Padding extends OnValidNonEmptyList {

					void paddingRightWithEmptyResult(String methodName, BDDSoftAssertions softly);

					void paddingLeftWithEmptyResult(String methodName, BDDSoftAssertions softly);
				}

				public non-sealed interface Splitting extends OnValidNonEmptyList {

					void splitAt(String methodName, BDDSoftAssertions softly);
				}

			}

		}

		public sealed interface Basic extends Operations

		permits

		Basic.EmptyListBehavior, //
		Basic.NonEmptyListBehavior, //
		Basic.DuringCreation//

		{
			public non-sealed interface DuringCreation extends Basic {
				void shouldSuccessfullyCreateAnEmptyList();

				void shouldSuccessfullyCreateNonEmptyList();
			}

			public non-sealed interface NonEmptyListBehavior extends Basic {
				void shouldNotBeEmpty(final BDDSoftAssertions softly);

				void shouldHaveCorrectSize();

				void shouldContainTheCorrectFirstElement();

				void shouldDisplayTheCorrectRepresentation();

			}

			public non-sealed interface EmptyListBehavior extends Basic {
				void shouldRemainEmpty(final BDDSoftAssertions softly);

				void shouldContainNoElements();

				void shouldReportFailureWhenAccessingTheFirstElement();

				void shouldDisplayTheCorrectRepresentationForEmptiness();

			}

		}

		public non-sealed interface Mutation extends Operations {
			void setFirstElement(BDDSoftAssertions softly);

			void restElements(BDDSoftAssertions softly);

			void setElementAtIndex(BDDSoftAssertions softly);

			void removeByIndex(BDDSoftAssertions softly);

			void trimLast(BDDSoftAssertions softly);

			void updatedAllBetween(BDDSoftAssertions softly);

			void updatedAllFrom(BDDSoftAssertions softly);

			void updatedAllUntil(BDDSoftAssertions softlyt);
		}

		public non-sealed interface ElementQuerying extends Operations {
			void first(BDDSoftAssertions softly);

			void indexOf(BDDSoftAssertions softly);

			void lastOption(BDDSoftAssertions softly);

			void hasDuplicatedElements(BDDSoftAssertions softly);

			void isPalindrome(BDDSoftAssertions softly);

			void allEqual(BDDSoftAssertions softly);

			void contains(BDDSoftAssertions softly);

			void isEqualTo(BDDSoftAssertions softly);

			void isEqualsWithoutConsiderationOrder(BDDSoftAssertions softly);

			void allEqualTo(BDDSoftAssertions softly);

			void endsWith(BDDSoftAssertions softly);

			void indexWhere(BDDSoftAssertions softly);

			void mkStr(BDDSoftAssertions softly);
		}

		public non-sealed interface Filtering extends Operations {
			void filter(BDDSoftAssertions softly);

			void exclude(BDDSoftAssertions softly);
		}

		public non-sealed interface Aggregation extends Operations {
			void foldLeft(BDDSoftAssertions softly);

			void foldRight(BDDSoftAssertions softly);

			void scanLeft(BDDSoftAssertions softly);

			void scanRight(BDDSoftAssertions softly);
		}

		public non-sealed interface Transformation extends Operations {
			void map(BDDSoftAssertions softly);


			void unzip(BDDSoftAssertions softly);
		}

		public non-sealed interface MonadicTransformation extends Operations {
			void sequence(BDDSoftAssertions softly);

			void combineNestedLists(BDDSoftAssertions softly);

			void reduce(BDDSoftAssertions softly);

			void partition(BDDSoftAssertions softly);
		}

		public non-sealed interface Grouping extends Operations {
			void groupBy(BDDSoftAssertions softly);

			void groupByZippingValuesAsPossible(BDDSoftAssertions softly);
		}

		public non-sealed interface Appending extends Operations {
			void addArray(BDDSoftAssertions softly);

			void addElement(BDDSoftAssertions softly);

			void addList(BDDSoftAssertions softly);

			void cons(BDDSoftAssertions softly);

			void consList(BDDSoftAssertions softly);
		}

		public non-sealed interface Structural extends Operations {
			void duplicate(BDDSoftAssertions softly);

			void last(BDDSoftAssertions softly);
		}

		public non-sealed interface Slicing extends Operations {
			void takeAtMost(BDDSoftAssertions softly);

			void dropAtMost(BDDSoftAssertions softly);

			void takeRightAtMost(BDDSoftAssertions softly);

			void dropRightAtMost(BDDSoftAssertions softly);

			void getSublistInRange(BDDSoftAssertions softly);

			void takeWhile(BDDSoftAssertions softly);

			void dropWhile(BDDSoftAssertions softly);

			void takeRightWhile(BDDSoftAssertions softly);

			void dropRightWhile(BDDSoftAssertions softly);
		}

		public non-sealed interface Reordering extends Operations {

			void reverse(BDDSoftAssertions softly);

			void shuffle(BDDSoftAssertions softly);

			void intersperse(BDDSoftAssertions softly);

			void interleave(BDDSoftAssertions softly);
		}

		public non-sealed interface Zipping extends Operations {
			void zipWithPositionWithArgument(BDDSoftAssertions softly);

			void zipAsPossible(BDDSoftAssertions softly);
		}

		public non-sealed interface Conversion extends Operations {


			void toSet(BDDSoftAssertions softly);


			void toStream(BDDSoftAssertions softly);

			void toJavaList(BDDSoftAssertions softly);
		}

		public non-sealed interface SubList extends Operations {
			void startsWith(BDDSoftAssertions softly);

			void hasSubList(BDDSoftAssertions softly);
		}

		public non-sealed interface PredicateMatching extends Operations {
			void anyMatch(BDDSoftAssertions softly);

			void allMatch(BDDSoftAssertions softly);

			void noneMatch(BDDSoftAssertions softly);
		}

		public non-sealed interface Padding extends Operations {
			void paddingRightWithEmptyResult(BDDSoftAssertions softly);

			void paddingLeftWithEmptyResult(BDDSoftAssertions softly);
		}

		public non-sealed interface Splitting extends Operations {
			void firstElementAndRestElementsOption(BDDSoftAssertions softly);

			void splitAt(BDDSoftAssertions softly);
		}

		public non-sealed interface Distincting extends Operations {
			void distinct(BDDSoftAssertions softly);
		}

	}

}
