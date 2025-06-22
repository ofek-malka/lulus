package dev.ofekmalka.core.data_structure.list;

import static dev.ofekmalka.core.assertion.PreconditionedProcess.from;
import static dev.ofekmalka.core.assertion.PreconditionedProcess.fromValidatedSupplier;
import static dev.ofekmalka.core.assertion.result.Result.success;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition;
import dev.ofekmalka.core.assertion.If.Condition.WillCondition.OrGet;
import dev.ofekmalka.core.assertion.PreconditionedProcess;
import dev.ofekmalka.core.assertion.PreconditionedProcess.PreconditionStage;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List.Errors.CastumArgumentMessage;
import dev.ofekmalka.core.data_structure.list.List.Errors.GeneralMessage;
import dev.ofekmalka.core.data_structure.list.behavior.ListBehavior;
import dev.ofekmalka.core.data_structure.map.Map;
import dev.ofekmalka.core.data_structure.set.Set;
import dev.ofekmalka.core.error.ErrorOptions;
import dev.ofekmalka.core.error.IndexErrorMessage;
import dev.ofekmalka.core.error.NullValueMessages;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.ConsoleOutputEffect;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.ErrorTracker;
import dev.ofekmalka.tools.helper.Nothing;
import dev.ofekmalka.tools.tuple.Tuple2;

public final class List<A> implements //

		ListBehavior.Operations.Aggregation<A>, //
		ListBehavior.Operations.Basic<A>, //
		ListBehavior.Operations.ElementQuerying<A>, //
		ListBehavior.Operations.ErrorHandling<A>, //
		ListBehavior.Operations.Filtering<A>, //
		ListBehavior.Operations.Grouping<A>, //
		ListBehavior.Operations.Appending<A>, //
		ListBehavior.Operations.Structural<A>, //
		ListBehavior.Operations.Mutation<A>, //
		ListBehavior.Operations.Transformation<A>, //
		ListBehavior.Operations.SubList<A>, //
		ListBehavior.Operations.PredicateMatching<A>, //
		ListBehavior.Operations.Padding<A>, //
		ListBehavior.Operations.Splitting<A>, //
		ListBehavior.Operations.Distincting<A>, //
		ListBehavior.Operations.Slicing<A>, //
		ListBehavior.Operations.Reordering<A>, //
		ListBehavior.Operations.Zipping<A>, //
		ListBehavior.Operations.MonadicTransformation<A>, //
		ConsoleOutputEffect //
{//

	final private Result<ListImp<A>> source;

	private List(final Result<ListImp<A>> source) {
		this.source = source;
	}

	private List(final ListImp<A> source) {
		this.source = Result.success(source);
	}

	private static <A> List<A> makeTypeSafe(final Result<ListImp<A>> r) {
		return new List<>(r);
	}

	private static <A> List<A> makeTypeSafe(final ListImp<A> r) {
		return new List<>(r);
	}

	public static <A> List<A> failureInstance() {
		return new List<>(Result.failure("failure instance"));
	}

	public static <A> List<A> failureWithMessage(final String message) {
		return new List<>(Result.failure(message));
	}

	@Override
	public boolean isProcessSuccess() {
		return source.isSuccess();
	}

	@Override
	public boolean isProcessFail() {
		return source.isFailure();
	}

	@Override
	public boolean isNotSuccess() {
		return source.isNotSuccess();
	}

	// very important it is default method
	private ListImp<A> getListImpSource() {
		return source.successValue();
	}

	@Override
	public Result<List<A>> getListResult() {
		return source.map(List::makeTypeSafe);
	}

	@Override
	public String getErrorMessageIfProcessFail() {
		return source.map(l -> "List process was successed")

				.getOrCreateFailureInstanceWithMessage(s -> s);

	}

	@Override
	public List<A> getOrElse(final List<A> alternative) {
		return If.isItTrue(this.isProcessSuccess()).//
				will()//
				.returnValue(() -> this)//
				.orGet(() -> alternative);

	}

	@Override
	public List<A> IfProcessFailGetEmptyList() {
		return If.isItTrue(this.isProcessFail()).will()//
				.<List<A>>returnValue(List::emptyList)//
				.orGet(() -> this);

	}

	@Override
	public <T> Result<T> mapListToResultOf(final Supplier<T> f) {
		return source.map(l -> f.get());
	}

	@Override
	public <T> Result<T> mapListToResultOf(final Function<List<A>, T> f) {
		return source.map(l -> f.apply(this));
	}

	@Override
	public <T> Result<T> flatMapListToResultOf(final Supplier<Result<T>> f) {
		return source.flatMap(l -> f.get());
	}

	@Override
	public <T> Result<T> flatMapListToResultOf(final Function<List<A>, Result<T>> f) {
		return source.flatMap(l -> f.apply(this));
	}

	// TODO need to include '.andIsNot(array -> array.length > List.' in the test
	@SafeVarargs
	public static <A> List<A> list(final A... elements) {
		final var result = If.givenObject(elements)//
				.isNonNull("elements")//
				.andIs(array -> array.length > 0, GeneralMessage.ERROR_ZERO_LENGTH_ELEMENTS.getMessage())//
				.andIsNot(array -> array.length > List.Errors.Constants.MAX_LIST_SIZE,
						Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage())//

				.will()//
				.flatMapTo(List::validateAndCreateListOrFail)//
				.getResult()//
				.prependMethodNameToFailureMessage("list");

		return List.makeTypeSafe(result);
	}

	@SafeVarargs
	private static <A> Result<ListImp<A>> validateAndCreateListOrFail(final A... elements) {
		final var nullIndexes = new ArrayList<Integer>(List.Errors.Constants.MAX_LIST_SIZE);

		for (var i = 0; i < elements.length; i++) {
			if (elements[i] == null) {
				nullIndexes.add(i);
			}
		}

		final var count = nullIndexes.size();

		return If.givenObject(count)//
				.isNot(c -> c >= 2, NullValueMessages.multipleNullIndexes(nullIndexes.toString()).getMessage())//
				.andIsNot(c -> c == 1, NullValueMessages.singleNullIndex(nullIndexes.toString()).getMessage())//
				.will()//
				.returnValue(() -> List.ListImp.list(elements)).getResult();
	}

	@SafeVarargs
	public static <A> List<A> reverseList(final A... elements) {

		final var result = List.list(elements).getListResult();//

		return If.isItTrue(result.isFailure())//
				.will()//

				.returnValue(() ->

				result.getOrCreateFailureInstanceWithMessage(
						errorMessage -> List.failureWithMessage(ErrorTracker.startTrackingFrom("reverseList")

								.finalizeWith(errorMessage)))

				)//
				.orGet(() -> List.makeTypeSafe(ListImp.reverseList(elements)));//

	}

	@SuppressWarnings("rawtypes")
	private static final List EMPTY_LIST = List.makeTypeSafe(Result.success(ListImp.emptyList()));

	@SuppressWarnings("unchecked")
	public static <A> List<A> emptyList() {
		return EMPTY_LIST;
	}

	@Override
	public int hashCode() {
		return source//
				.flatMap(ListImp::generateHashCode)//
				.casesForProvidedHashCode()//
				.onSuccess(t -> t)//
				.onOtherOptionProvideClassName("List")//
		;

	}

	@Override
	public boolean equals(final Object obj) {
		return this.isEqualTo(obj).getOrElse(false);

	}

	@Override
	public String toString() {
		return If.isItTrue(this.isNotSuccess())//
				.will()//

				.returnValue(() -> "It is in a failed state and cannot generate a valid representation for toString."
						+ "\nPlease check the process status for more details.")//
				.orGet(() -> this.representList().successValue());
	}

	@Override
	public Result<String> representList() {
		return source.map(ListImp::toString);
	}

	// ------------------------------------------------------------------------------------------------------------
//  non-static

	@Override
	public Result<Boolean> isEmpty() {
		return from(source)//
				.processOperation(ListImp::isEmpty)//
				.andMakeStackTraceUnderTheName("isEmpty")//
				.getResultProccess();//

	}

	@Override
	public Result<Boolean> isNotEmpty() {
		return from(source)//
				.processOperation(ListImp::isNotEmpty)//
				.andMakeStackTraceUnderTheName("isNotEmpty")//
				.getResultProccess();//
	}

	@Override
	public Result<Integer> size() {
		return from(source)//
				.processOperation(ListImp::size)//
				.andMakeStackTraceUnderTheName("size")//
				.getResultProccess();//
	}

	@Override
	public Result<A> firstElementOption() {
		return from(source)//
				.processOperationWithResult(ListImp::firstElementOption)//
				.andMakeStackTraceUnderTheName("firstElementOption")//
				.getResultProccess();//
	}

	// -------------------------------------------------------------------------------------
	// End Basic
	// -------------------------------------------------------------------------------------
	// Start Intermediate

	@Override
	public List<A> cons(final A element) {
		return from(source)//
				.checkCondition(l -> If.givenObject(element)//
						.isNonNull("element")//
						.andSupplierIsNot(() -> l.size() == Errors.Constants.MAX_LIST_SIZE, //
								Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage()))//
				.processOperation(l -> l.cons(element))//
				.andMakeStackTraceUnderTheName("cons")//
				.mapTo(List::makeTypeSafe);//

	}

	@Override
	public Result<Tuple2<List<A>, List<A>>> duplicate() {
		return from(source)//
				.processOperation(ListImp::duplicate)//
				.andMakeStackTraceUnderTheName("duplicate")//
				.getResultProccess()//

				.map(t -> t.map(List::makeTypeSafe, List::makeTypeSafe).successValue());

	}

	@Override
	public <B> List<Tuple2<A, B>> zipAsPossible(final List<B> joinedList) {

		final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
				.withArgumentName("joinedList")//
				.getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(joinedList).isNonNull("joinedList").andIs(List::isProcessSuccess,
						errorProcessingMessage))
				.processOperation(l -> l.zipAsPossible(joinedList.getListImpSource()))//
				.andMakeStackTraceUnderTheName("zipAsPossible")//
				.mapTo(List::makeTypeSafe);//

	}

	@Override
	public <B> Result<B> foldLeft(final B identity, final Function<B, Function<A, B>> accumulator) {
		return from(source)//
				.checkCondition(() -> If.givenObject(identity).isNonNull("identity")
						.andOtherObjectIsNotNull(accumulator, "accumulator")

				).processOperationWithResult(l -> l.foldLeft(identity, accumulator))
				.andMakeStackTraceUnderTheName("foldLeft")//
				.getResultProccess();//
	}

	@Override
	public Result<A> reduce(final Function<A, Function<A, A>> elementTransformer)

	{
		final var provideNullValue = "provide null value";
		final var checkErrorOfPredicate = Result.of(() -> elementTransformer.apply(null).apply(null), provideNullValue);

		final var isElementTransformerProvideNull = CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage()
				.equals(Result.success(checkErrorOfPredicate).map(r -> r.failureValue().getMessage())
						.getOrElse(() -> If.VALUE_FITS_ALL_REQUIREMENTS));

		return from(source)//
				.checkCondition(() -> //
				If.givenObject(elementTransformer)//
						.isNonNull("elementTransformer").andSupplierIsNot(() -> isElementTransformerProvideNull,
								CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage())

				).processOperationWithResult(l -> l.reduce(elementTransformer)).andMakeStackTraceUnderTheName("reduce")//
				.getResultProccess();//
	}

	@Override
	public List<A> setFirstElement(final A firstElement) {
		return from(source)//
				.checkCondition(() -> If.givenObject(firstElement).isNonNull("firstElement"))//
				.processOperationWithResult(l -> l.setFirstElement(firstElement))//
				.andMakeStackTraceUnderTheName("setFirstElement")

				.mapTo(List::makeTypeSafe)//
		;

	}

	@Override
	public List<A> reverse() {
		return from(source)//
				.processOperation(ListImp::reverse)//
				.andMakeStackTraceUnderTheName("reverse")//
				.mapTo(List::makeTypeSafe);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result<Boolean> isEqualTo(final Object other) {

		final var argName = "other";
		final var errorProcessingMessage = Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
				.withArgumentName(argName).getMessage();
		final var errorInstanceOf =

				Errors.CastumArgumentMessage.ERROR_NOT_INSTANCE_OF_LIST.withArgumentName(argName).getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(other)//
						.isNonNull(argName)//
						.andIs(o -> o instanceof List, errorInstanceOf)//
						.andIs(o -> ((List<A>) o).isProcessSuccess(), errorProcessingMessage))
				.processOperation(l -> l.isEqualTo(((List<A>) other).getListImpSource()))
				.andMakeStackTraceUnderTheName("isEqualTo")//
				.getResultProccess();//
	}

	@Override
	public List<A> restElements() {

		return from(source)//
				.processOperationWithResult(ListImp::restElementsOption)//
				.andMakeStackTraceUnderTheName("restElements")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public Result<Tuple2<A, List<A>>> firstElementAndRestElementsOption() {
		return from(source)//
				.processOperationWithResult(ListImp::firstElementAndRestElementsOption)//
				.andMakeStackTraceUnderTheName("firstElementAndRestElementsOption")//
				.getResultProccess()//
				.map(t -> t.mapValue(List::makeTypeSafe).successValue());

	}

	@Override
	public <B> Result<B> foldRight(final B identity, final Function<A, Function<B, B>> accumulator) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(identity)//
						.isNonNull("identity")//
						.andOtherObjectIsNotNull(accumulator, "accumulator")

				)//
				.processOperationWithResult(l -> l.foldRight(identity, accumulator))//
				.andMakeStackTraceUnderTheName("foldRight")//
				.getResultProccess();//

	}

	@Override
	public List<A> filter(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(predicate)//
						.isNonNull("predicate")

				).processOperationWithResult(l -> l.filter(predicate))//
				.andMakeStackTraceUnderTheName("filter")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public List<A> exclude(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(predicate)//
						.isNonNull("predicate")

				).processOperationWithResult(l -> l.exclude(predicate))//
				.andMakeStackTraceUnderTheName("exclude")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public <B> List<B> map(final Function<A, B> elementTransformer) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(elementTransformer)//
						.isNonNull("elementTransformer")

				).processOperationWithResult(l -> l.map(elementTransformer))//
				.andMakeStackTraceUnderTheName("map")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public <B> List<B> combineNestedLists(final Function<A, List<B>> elementTransformer) {

		return from(source)//
				.checkCondition(() -> //
				If.givenObject(elementTransformer)//
						.isNonNull("elementTransformer")

				)//
				.processOperationWithResult(l -> //
				l.combineNestedLists(elementTransformer))//
				.andMakeStackTraceUnderTheName("combineNestedLists")//
				.mapTo(List::makeTypeSafe);
	}

	// -------------------------------------------------------------------------------------
	// End Intermediate
	// -------------------------------------------------------------------------------------
	// Start advanced
	public static ExtendedFactoryOperations extendedFactoryOperations() {
		return new ExtendedFactoryOperations();
	}

	public final static class ExtendedFactoryOperations
			implements ListBehavior.ExtendedFactoryOperationsHandler.PrimitiveListFactory, //
			ListBehavior.ExtendedFactoryOperationsHandler.CollectionFactory, //
			ListBehavior.ExtendedFactoryOperationsHandler.RecursiveFactory, //
			ListBehavior.ExtendedFactoryOperationsHandler.MathFactory, //
			ListBehavior.ExtendedFactoryOperationsHandler.TupleFactory {

		private <T> PreconditionStage<dev.ofekmalka.core.data_structure.list.List.ListImp.StaticMethod> verifyingSupplierPreconditionsForStaticMethod(
				final Supplier<Condition<T>> supplier) {//
			return PreconditionedProcess//
					.fromValue(List.ListImp.staticMethod())//
					.checkCondition(supplier);//

		}

		@Override
		public List<Boolean> createBooleanList(final boolean... booleanValues) {
			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("booleanValues").getMessage();//

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(booleanValues)//
					.isNonNull("booleanValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))//
					.processOperation(l -> l.booleanList(booleanValues))//
					.andMakeStackTraceUnderTheName("createBooleanList")//
					.mapTo(List::makeTypeSafe);//

		}

		@Override
		public List<Byte> createByteList(final byte... byteValues) {

			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("byteValues").getMessage();//

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(byteValues)//
					.isNonNull("byteValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))
					.processOperation(l -> l.byteList(byteValues))//
					.andMakeStackTraceUnderTheName("createByteList")//
					.mapTo(List::makeTypeSafe);//

		}

		@Override
		public List<Integer> createIntList(final int... intValues) {

			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("intValues").getMessage();//
			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(intValues)//
					.isNonNull("intValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))
					.processOperation(l -> l.intList(intValues))//
					.andMakeStackTraceUnderTheName("createIntList")//
					.mapTo(List::makeTypeSafe);//
		}

		@Override
		public List<Character> createCharList(final char... charValues) {
			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("charValues").getMessage();//

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(charValues)//
					.isNonNull("charValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))
					.processOperation(l -> l.charList(charValues))//
					.andMakeStackTraceUnderTheName("createCharList")//
					.mapTo(List::makeTypeSafe);//
		}

		@Override
		public List<Short> createShortList(final short... shortValues) {
			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("shortValues").getMessage();//

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(shortValues)//
					.isNonNull("shortValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))
					.processOperation(l -> l.shortList(shortValues))//
					.andMakeStackTraceUnderTheName("createShortList")//
					.mapTo(List::makeTypeSafe);//
		}

		@Override
		public List<Float> createFloatList(final float... floatValues) {
			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("floatValues").getMessage();//

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(floatValues)//
					.isNonNull("floatValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))
					.processOperation(l -> l.floatList(floatValues))//
					.andMakeStackTraceUnderTheName("createFloatList")//
					.mapTo(List::makeTypeSafe);//
		}

		@Override
		public List<Double> createDoubleList(final double... doublesValues) {
			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("doubleValues").getMessage();//

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(doublesValues)//
					.isNonNull("doubleValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))
					.processOperation(l -> l.doubleList(doublesValues))//
					.andMakeStackTraceUnderTheName("createDoubleList")//
					.mapTo(List::makeTypeSafe);//
		}

		@Override
		public List<Long> createLongList(final long... longsValues) {
			final var emptyArrayErrorMessage = Errors.CastumArgumentMessage//
					.ERROR_ZERO_LENGTH_ARGUMENT_ARRAY//
					.withArgumentName("longValues").getMessage();//

			return

			verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(longsValues)//
					.isNonNull("longValues")//
					.andIs(array -> array.length > 0, emptyArrayErrorMessage))
					.processOperation(l -> l.longList(longsValues))//
					.andMakeStackTraceUnderTheName("createLongList")//
					.mapTo(List::makeTypeSafe);//
		}

		@SuppressWarnings("unchecked")
		@Override
		public <A> List<A> createFromCollection(final Collection<A> collection) {

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(collection).isNonNull("collection"))//
					.processOperationBySupplierResult(() -> If.givenObject(collection)//
							.is(Collection::isEmpty)//
							.will()//

							.<Result<ListImp<A>>>returnValue(ListImp::emptyResultList)//
							.orGet(() -> List.list(collection.toArray()).map(o -> (A) o).source)//
					)

					.andMakeStackTraceUnderTheName("createFromCollection")//
					.mapTo(List::makeTypeSafe);//

		}

		@Override
		public <A> List<A> createFromStreamAndRemoveNullsValues(final Stream<A> stream) {
			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(stream)//
					.isNonNull("stream")//
			)//
					.processOperation(l -> {
						final var list = stream.filter(Objects::nonNull)

								.limit(Errors.Constants.MAX_LIST_SIZE).toList();
						return l.fromCollection(list);
					})//
					.andMakeStackTraceUnderTheName("createFromStreamAndRemoveNullsValues")//
					.mapTo(List::makeTypeSafe);//

		}

		@Override
		public <A> List<A> iterateWithSeed(final A seed, final Function<A, A> transformationFunction,
				final int iterations) {

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(seed)//
					.isNonNull("seed")//
					.andOtherObjectIsNotNull(transformationFunction, "transformationFunction")//
					.andSupplierIs(() -> iterations > 0, //
							Errors.CastumArgumentMessage//
									.ERROR_NON_POSITIVE_VALUE//
									.withArgumentName("iterations").getMessage())//
					.andSupplierIs(() -> iterations <= Errors.Constants.MAX_LIST_SIZE, //
							Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED//
									.getMessage()))//
					.processOperationWithResult(l -> l.iterate(seed, transformationFunction, iterations))//
					.andMakeStackTraceUnderTheName("iterateWithSeed")//
					.mapTo(List::makeTypeSafe);//
		}

		@Override
		public List<Integer> generateRange(final int startInclusive, final int endExclusive) {

			final var preconditionsForRangeMethod = If.isItTrue(startInclusive < endExclusive//
					, GeneralMessage.ERROR_INVALID_START_END_RANGE.getMessage())//
					// Using start and end in DoubleUtil to prevent unexpected behavior during
					// subtraction of two integers
					.andSupplierIs(() -> //
					Math.abs(endExclusive - startInclusive)//
							<= Errors.Constants.MAX_LIST_SIZE//
							, GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage());//

			return verifyingSupplierPreconditionsForStaticMethod(() -> preconditionsForRangeMethod)//
					.processOperation(l -> l.generateRange(startInclusive, endExclusive))//
					.andMakeStackTraceUnderTheName("generateRange")//
					.mapTo(List::makeTypeSafe);
		}

		@Override
		public <A, S> List<A> unfoldFromSeed(final S initialSeed, //

				final Function<If<S>, Condition<S>> generatorFunction, //
				final Function<S, Tuple2<A, S>> successOutcome) {

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(initialSeed)//
					.isNonNull("initialSeed")//
					.andOtherObjectIsNotNull(generatorFunction, "generatorFunction")//
					.andOtherObjectIsNotNull(successOutcome, "successOutcome"))//

					.processOperationWithResult(//
							l -> {
								final Function<S, Result<Tuple2<A, S>>> generator = s -> generatorFunction
										.safeApplyOn(If.givenObject(s))//
										.flatMap(ifConditionPassed -> ifConditionPassed.will()
												.flatMapTo(i -> successOutcome.safeApplyOn(i)).getResult());

								return If.isItTrue(generator.apply(initialSeed).isFailure())

										.will()//
										.returnValue(() -> generator.apply(initialSeed)
												.<ListImp<A>>mapFailureForOtherObject())
										.orGet(() -> l.unfold(initialSeed, //

												generator))

							;
							}//

					)//
					.andMakeStackTraceUnderTheName("unfoldFromSeed")//
					.mapTo(List::makeTypeSafe);//
		}

		@Override
		public <A1, A2> Result<Tuple2<List<A1>, List<A2>>> unzipListOfTuples(final List<Tuple2<A1, A2>> tupleList) {

			final var errorProcessingMessage = Errors.CastumArgumentMessage//
					.ERROR_PROCESSING_JOINED_LIST//
					.withArgumentName("tupleList").getMessage();//

			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(tupleList)//
					.isNonNull("tupleList")//
					.andIs(List::isProcessSuccess, errorProcessingMessage)//
			)//
					.processOperation(l -> l.unzip(tupleList.getListImpSource()))//
					.andMakeStackTraceUnderTheName("unzipListOfTuples")//
					.getResultProccess()//
					.map(tuple -> tuple.map(List::makeTypeSafe, List::makeTypeSafe).successValue());//
		}

//		@Override
//		public List<String> splitStringIntoWords(final String inputString) {
//			return verifyingSupplierPreconditionsForStaticMethod(() -> //
//			If.givenObject(inputString)//
//					.isNonNull("inputString"))//
//					.processOperation(l -> l.words(inputString))//
//					.andMakeStackTraceUnderTheName("splitStringIntoWords")//
//					.mapTo(List::makeTypeSafe);//
//		}
//
//		@Override
//		public Result<BigDecimal> calculateFactorial(final int number) {
//			return verifyingSupplierPreconditionsForStaticMethod(() -> //
//			If.givenObject(number))
//					.is(IntegerUtil::isNotNegative, Errors.GeneralMessage.ERROR_NEGATIVE_FACTORIAL_INPUT.getMessage())
//					.andIs(iu -> iu<=Errors.Constants.FACTORIAL_INPUT_LIMIT),
//							Errors.GeneralMessage.ERROR_FACTORIAL_INPUT_LIMIT_EXCEEDED.getMessage()))
//					.processOperation(l -> l.factorial(number))//
//					.andMakeStackTraceUnderTheName("calculateFactorial")//
//					.getResultProccess();
//
//		}

		@Override
		public <A> List<A> createFilledList(final int numberOfElements, final Supplier<A> elementSupplier) {
			return verifyingSupplierPreconditionsForStaticMethod(() -> //
			If.givenObject(numberOfElements)//

					.is(i -> i > 0,
							Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE
									.withArgumentName("numberOfElements").getMessage())
					.andIs(iu -> iu <= Errors.Constants.MAX_LIST_SIZE, //
							Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage())//

					.andOtherObjectIsNotNull(elementSupplier, "elementSupplier"))//
					.processOperationWithResult(l -> l.fill(numberOfElements, elementSupplier))//
					.andMakeStackTraceUnderTheName("createFilledList")//
					.mapTo(List::makeTypeSafe);//
		}

	}

////////////////////////////////////////
//////no args operations
/////////////////////////////////////

	@Override
	public List<A> last() {
		return from(source)//
				.processOperationWithResult(ListImp::last)//
				.andMakeStackTraceUnderTheName("last")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public Result<A> lastOption() {
		return from(source)//
				.processOperationWithResult(ListImp::lastOption)//
				.andMakeStackTraceUnderTheName("lastOption")//
				.getResultProccess();//

	}

	@Override
	public List<A> trimLast() {
		return from(source)//
				.processOperation(ListImp::trimLast)//
				.andMakeStackTraceUnderTheName("trimLast")//
				.mapTo(List::makeTypeSafe);//
	}

	public Convertor convert() {

		return new Convertor(this);
	}

	public class Convertor implements ListBehavior.Operations.Conversion<A> {
		List<A> list;

		public Convertor(final List<A> list) {
			this.list = list;
		}

	

		

		@Override
		public dev.ofekmalka.core.data_structure.set.Set<A> toSet() {
			return from(source)//
					.processOperation(ListImp::toSet)//
					.andMakeStackTraceUnderTheName("toSet")

					.getOrConvertToFailureState(Set::makeFailureInstanceWithMessage)

			;//
		}

		@Override
		// TODO NEED TO ADD TO TESTS
		public <L extends java.util.List<A>> Result<L> toJavaList(final Supplier<L> listSupplier) {
			return from(source)//
					.checkCondition(

							() -> //
							If.givenObject(listSupplier)//
									.isNonNull("listSupplier"))//
					.processOperationWithResult(l -> l.toJavaList(listSupplier))//
					.andMakeStackTraceUnderTheName("toJavaList")//
					.getResultProccess();// ;//

		}

		@Override
		public dev.ofekmalka.tools.stream.Stream<A> toStream() {
			return from(source)//
					.processOperation(ListImp::toStream)//
					.andMakeStackTraceUnderTheName("toStream")//
					.getOrConvertToFailureState(//
							dev.ofekmalka.tools.stream.Stream//
							::makeFailureInstanceWithMessage);//
		}

	}

	/// ===============================================
	@Override
	public Result<Boolean> hasDuplicatedElements() {
		return from(source)//
				.processOperation(ListImp::hasDuplicatedElements)//
				.andMakeStackTraceUnderTheName("hasDuplicatedElements")//
				.getResultProccess();//
	}

	@Override
	public Result<Boolean> isPalindrome() {
		return from(source)//
				.processOperation(ListImp::isPalindrome)//
				.andMakeStackTraceUnderTheName("hasDuplicatedElements")//
				.getResultProccess();//
	}

	@Override
	public Result<Boolean> allEqual() {
		return from(source)//
				.processOperation(ListImp::allEqual)//
				.andMakeStackTraceUnderTheName("allEqual")//
				.getResultProccess();//
	}

	@Override
	public List<A> distinct() {
		return from(source)//
				.processOperation(ListImp::distinct)//
				.andMakeStackTraceUnderTheName("distinct")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public List<A> shuffle() {
		return from(source)//
				.processOperation(ListImp::shuffle)//
				.andMakeStackTraceUnderTheName("shuffle")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public List<Tuple2<A, Integer>> zipWithPosition() {
		return from(source)//
				.processOperation(ListImp::zipWithPosition)//
				.andMakeStackTraceUnderTheName("zipWithPosition")//
				.mapTo(List::makeTypeSafe);

	}

///////////////////////////////////////////////////
//all args operations
///////////////////////////////////////////////////



	@Override
	public List<A> takeAtMost(final int n) {
		return from(source)//
				.checkCondition(() -> If.givenObject(n)//
						.is(i -> i > 0,
								Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE.withArgumentName("n")
										.getMessage()))//
				.processOperation(l -> l.takeAtMost(n))//
				.andMakeStackTraceUnderTheName("takeAtMost")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public List<A> dropAtMost(final int n) {
		return from(source)//
				.checkCondition(() -> If.givenObject(n)//
						.is(i -> i > 0,
								Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE.withArgumentName("n")
										.getMessage()))//
				.processOperation(l -> l.dropAtMost(n))//
				.andMakeStackTraceUnderTheName("dropAtMost")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public List<A> takeRightAtMost(final int n) {
		return from(source)//
				.checkCondition(() -> If.givenObject(n)//
						.is(i -> i > 0,
								Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE.withArgumentName("n")
										.getMessage()))//
				.processOperation(l -> l.takeRightAtMost(n))//
				.andMakeStackTraceUnderTheName("takeRightAtMost")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public List<A> dropRightAtMost(final int n) {
		return from(source)//
				.checkCondition(() -> If.givenObject(n)//
						.is(i -> i > 0,
								Errors.CastumArgumentMessage.ERROR_NON_POSITIVE_VALUE.withArgumentName("n")
										.getMessage()))//
				.processOperation(l -> l.dropRightAtMost(n))//
				.andMakeStackTraceUnderTheName("dropRightAtMost")//
				.mapTo(List::makeTypeSafe);
	}

	private Condition<Boolean> isIndexIsInRange(final int index, final int size) {
		final var condition = index >= 0 && index < size;
		return If.isItTrue(condition, //
				IndexErrorMessage//
						.forIndex(index)//
						.between(0)//
						.toExclusive(size).getMessage());
	}

	private boolean isListEmptyAndValueNegative(final int index, final ListImp<A> list) {
		return list.isEmpty()

				&& index < 0;
	}

	private Condition<Boolean> checkIndexValidation(final int index, final ListImp<A> list) {
		return If.isItFalse(isListEmptyAndValueNegative(index, list)//
				, CastumArgumentMessage.ERROR_NEGATIVE_VALUE.withArgumentName("index").getMessage())//
				.andForOtherCondition(isIndexIsInRange(index, list.size()).orSupplierIs(() -> list.isEmpty()));

	}

	private Condition<Boolean> checkFromAndUntilIndicesValidation(final int from, final int until,
			final ListImp<A> list) {

		final var toExclusive = until - 1;

		final var size = list.size();

		return isIndexIsInRange(from, size)//
				.andForOtherCondition(isIndexIsInRange(toExclusive, size))
				.andSupplierIs(() -> toExclusive > from,
						Errors.GeneralMessage.ERROR_FROM_INDEX_IS_NOT_LESS_THAN_UNTIL_INDEX.getMessage())

				.orSupplierIs(() -> list.isEmpty());
	}

	@Override
	public List<A> removeByIndex(final int index) {
		return from(source)//
				.checkCondition(list -> checkIndexValidation(index, list))//
				.processOperationWithResult(l -> l.removeByIndex(index))//
				.andMakeStackTraceUnderTheName("removeByIndex")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public List<A> getSublistInRange(final int from, final int until) {

		return from(source)//
				.checkCondition(list -> checkFromAndUntilIndicesValidation(from, until, list)

				)//
				.processOperation(l -> l.getSublistInRange(from, until))//
				.andMakeStackTraceUnderTheName("getSublistInRange")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public Result<Tuple2<List<A>, List<A>>> splitAt(final int index) {
		return from(source)//
				.checkCondition(list -> checkIndexValidation(index, list))//
				.processOperationWithResult(l -> l.splitAt(index)//
						.map(t -> t.map(List::makeTypeSafe, List::makeTypeSafe).successValue())//
				)//
				.andMakeStackTraceUnderTheName("splitAt")//
				.getResultProccess();
	}

	private int leftElementsFor(final int size) {
		return Errors.Constants.MAX_INDEX - size + 1;
	}

	@Override
	public List<Tuple2<A, Integer>> zipWithPosition(final int startIndex) {

		return from(source)//

				.checkCondition(l -> //

				If.givenObject(l.size())//
						.is(size -> leftElementsFor(size)//
								>= startIndex//
								, Errors.GeneralMessage.ERROR_SIZE_INDEX_EXCEEDED.getMessage()))

				.processOperation(
						l -> l.zipWithPosition().unsafeMap(t -> t.mapValue(v -> v + startIndex).successValue()))//
				.andMakeStackTraceUnderTheName("zipWithPosition")//
				.mapTo(List::makeTypeSafe);

	}

	@Override
	public List<Result<A>> paddingRightWithEmptyResult(final int targetLength) {
		return from(source)//
				.checkCondition(() ->

				If.givenObject(targetLength).isNot(iu -> iu > Errors.Constants.MAX_LIST_SIZE, //
						Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage()).andIsNot(i -> i < 0, //
								Errors.CastumArgumentMessage.ERROR_NEGATIVE_VALUE.withArgumentName("targetLength")
										.getMessage()))
				.processOperation(l -> l.paddingRightWithEmptyResult(targetLength))//
				.andMakeStackTraceUnderTheName("paddingRightWithEmptyResult")//
				.mapTo(List::makeTypeSafe);//

	}

	@Override
	public List<Result<A>> paddingLeftWithEmptyResult(final int targetLength) {
		return from(source)//
				.checkCondition(() ->

				If.givenObject(targetLength).isNot(iu -> iu > Errors.Constants.MAX_LIST_SIZE, //
						Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage()).andIsNot(i -> i < 0, //
								Errors.CastumArgumentMessage.ERROR_NEGATIVE_VALUE.withArgumentName("targetLength")
										.getMessage()))
				.processOperation(l -> l.paddingLeftWithEmptyResult(targetLength))//
				.andMakeStackTraceUnderTheName("paddingLeftWithEmptyResult")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public List<A> takeWhile(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() ->

				If.givenObject(predicate).isNonNull("predicate"))//
				.processOperationWithResult(l -> l.takeWhile(predicate))//
				.andMakeStackTraceUnderTheName("takeWhile")//
				.mapTo(List::makeTypeSafe);//

	}

	@Override
	public List<A> dropWhile(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() ->

				If.givenObject(predicate).isNonNull("predicate"))//
				.processOperationWithResult(l -> l.dropWhile(predicate))//
				.andMakeStackTraceUnderTheName("dropWhile")//
				.mapTo(List::makeTypeSafe);//

	}

	@Override
	public List<A> takeRightWhile(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() ->

				If.givenObject(predicate).isNonNull("predicate"))//
				.processOperationWithResult(l -> l.takeRightWhile(predicate))//
				.andMakeStackTraceUnderTheName("takeRightWhile")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public List<A> dropRightWhile(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() ->

				If.givenObject(predicate).isNonNull("predicate"))//
				.processOperationWithResult(l -> l.dropRightWhile(predicate))//
				.andMakeStackTraceUnderTheName("dropRightWhile")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public List<A> updatedAllBetween(final int fromIndex, final int untilIndex, final A element) {
		return from(source)//
				.checkCondition(list -> //
				If.givenObject(element).isNonNull("element")//
						.andForOtherCondition(checkFromAndUntilIndicesValidation(fromIndex, untilIndex, list))//
				)//
				.processOperationWithResult(l -> l.updatedAllBetween(fromIndex, untilIndex, element))//
				.andMakeStackTraceUnderTheName("updatedAllBetween")//
				.mapTo(List::makeTypeSafe);

	}

	@Override
	public List<A> updatedAllFrom(final int fromIndex, final A element) {
		return PreconditionedProcess//
				.from(source)//
				.processOperationWithResult(l ->

				this.updatedAllBetween(fromIndex, l.size(), element)

						.source)//
				.andMakeStackTraceUnderTheName("updatedAllFrom")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public List<A> updatedAllUntil(final int untilIndex, final A element) {
		return

		PreconditionedProcess//
				.from(source)//

				.processOperationWithResult(l ->

				this.updatedAllBetween(0, untilIndex, element).source)//

				.andMakeStackTraceUnderTheName("updatedAllUntil")//

				.mapTo(List::makeTypeSafe);
	}



	@Override
	public List<A> setElementAtIndex(final int index, final A element) {
		return from(source)//
				.checkCondition(list ->

				checkIndexValidation(index, list)//
						.andForOtherCondition(If.givenObject(element).isNonNull("element"))//
				)//
				.processOperationWithResult(l -> l.setElementAtIndex(index, element))//
				.andMakeStackTraceUnderTheName("setElementAtIndex")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public Result<Integer> indexOf(final A element) {
		return PreconditionedProcess//
				.from(source)//
				.checkCondition(() -> If.givenObject(element).isNonNull("element"))//
				.processOperationWithResult(l ->

				If.givenObject(l)//
						.is(ListImp::isEmpty)//

						.will()//
						.returnValue(() -> l.indexOf(element))//
						.orGet(() -> l.indexOf(element)
								.mapFailure(Errors.CastumValueMessage.ERROR_INDEX_OF_ELEMENT_NOT_FOUND
										.withValue(element).getMessage()

								)))//
				.andMakeStackTraceUnderTheName("indexOf")//
				.getResultProccess();//

	}

	@Override
	public Result<Integer> indexWhere(final Predicate<A> predicate) {
		return PreconditionedProcess//
				.from(source)//
				.checkCondition(() -> If.givenObject(predicate).isNonNull("predicate"))//
				.processOperationWithResult(l -> l.indexWhere(predicate))//
				.andMakeStackTraceUnderTheName("indexWhere")//
				.getResultProccess();//
	}

	@Override
	public Result<A> first(final Predicate<A> predicate) {
		return PreconditionedProcess//
				.from(source)//
				.checkCondition(() -> If.givenObject(predicate).isNonNull("predicate"))//
				.processOperationWithResult(l -> l.first(predicate))//
				.andMakeStackTraceUnderTheName("first")//
				.getResultProccess();//
	}

	@Override
	public Result<A> first(final Predicate<A> predicate, final String messageWhenElementNotFound) {
		return PreconditionedProcess//
				.from(source)//
				.checkCondition(() -> If.givenObject(predicate).isNonNull("predicate")

						.andOtherObjectIsNotNull(messageWhenElementNotFound, "messageWhenElementNotFound")
						.andSupplierIsNot(() -> messageWhenElementNotFound.isEmpty(),
								"messageWhenElementNotFound must not be empty")
						.andSupplierIsNot(() -> messageWhenElementNotFound.isBlank(),
								"messageWhenElementNotFound must not be blank")

				)//
				.processOperationWithResult(l -> l.first(predicate, messageWhenElementNotFound))//
				.andMakeStackTraceUnderTheName("first")

				.getResultProccess();//
	}

	///////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	@Override
	public List<A> addArray(final A... array) {
		return fromValidatedSupplier(() ->

		this.addList(List.list(array)).getListResult())//
				.andMakeStackTraceUnderTheName("addArray")//
				.getOrConvertToFailureState(List::failureWithMessage);//
	}

	@Override
	public List<A> addElement(final A element) {
		return fromValidatedSupplier(() ->

		this.addList(List.list(element)).getListResult())//
				.andMakeStackTraceUnderTheName("addElement")//
				.getOrConvertToFailureState(List::failureWithMessage);//
	}

	@Override
	public List<A> addList(final List<A> joinedList) {
		final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
				.withArgumentName("joinedList").getMessage();

		return from(source)//
				.checkCondition(l -> If.givenObject(joinedList)//
						.isNonNull("joinedList")//
						.andIs(List::isProcessSuccess, errorProcessingMessage)
						// Using start and end in DoubleUtil to prevent unexpected behavior during
						// addition of two integers
						.andIs(jl -> //
						l.size() + jl.size().successValue()//
								<= Errors.Constants.MAX_LIST_SIZE//
								, GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage()))

				//
				.processOperation(l -> l.add(joinedList.getListImpSource()))//
				.andMakeStackTraceUnderTheName("addList")//
				.mapTo(List::makeTypeSafe);//

	}

	@Override
	public Result<Boolean> contains(final A element) {
		return from(source)//
				.checkCondition(() -> If.givenObject(element)//
						.isNonNull("element")//
				).processOperation(l -> l.contains(element))//
				.andMakeStackTraceUnderTheName("contains")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> isEqualsWithoutConsiderationOrder(final List<A> otherList) {
		final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
				.withArgumentName("otherList").getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(otherList)//
						.isNonNull("otherList")//
						.andIs(List::isProcessSuccess, errorProcessingMessage))
				.processOperation(l -> l.isEqualsWithoutConsiderationOrder(otherList.getListImpSource()))//
				.andMakeStackTraceUnderTheName("isEqualsWithoutConsiderationOrder")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> allEqualTo(final A element) {
		return from(source)//
				.checkCondition(() -> If.givenObject(element)//
						.isNonNull("element"))
				.processOperation(l -> l.allEqualTo(element))//
				.andMakeStackTraceUnderTheName("allEqualTo")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> anyMatch(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() -> If.givenObject(predicate)//
						.isNonNull("predicate"))
				.processOperationWithResult(l -> l.anyMatch(predicate))//
				.andMakeStackTraceUnderTheName("anyMatch")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> allMatch(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() -> If.givenObject(predicate)//
						.isNonNull("predicate"))
				.processOperationWithResult(l -> l.allMatch(predicate))//
				.andMakeStackTraceUnderTheName("allMatch")//
				.getResultProccess();
	}

	@Override
	public Result<Boolean> noneMatch(final Predicate<A> predicate) {
		return from(source)//
				.checkCondition(() -> If.givenObject(predicate)//
						.isNonNull("predicate"))
				.processOperationWithResult(l -> l.n1Match(predicate))//
				.andMakeStackTraceUnderTheName("noneMatch")//
				.getResultProccess();
	}

	@Override
	public Result<String> mkStr(final String sep) {
		return from(source)//
				.checkCondition(() -> If.givenObject(sep)//
						.isNonNull("sep"))
				.processOperation(l -> l.mkStr(sep))//
				.andMakeStackTraceUnderTheName("mkStr")//
				.getResultProccess();
	}

	@Override
	public <B> Map<B, List<A>> groupBy(final Function<A, B> keyMapper) {

		return from(source)//
				.checkCondition(() -> If.givenObject(keyMapper)//
						.isNonNull("keyMapper"))

				.processOperationWithResult(l -> l.advancedGroupBy(keyMapper)//

				)//

				.andMakeStackTraceUnderTheName("groupBy")
				.getOrConvertToFailureState(Map::makeFailureInstanceWithMessage);

	}

	@Override
	public <B> Map<A, B> groupByZippingValuesAsPossible(final List<B> joinedList) {
		return PreconditionedProcess.fromValidatedSupplier(//
				() -> this.zipAsPossible(joinedList)//
						.foldLeft(Map.<A, B>emptyMap(), acc -> element -> acc.add(element.state(), element.value())))
				.andMakeStackTraceUnderTheName("groupByZippingValuesAsPossible")

				.getOrConvertToFailureState(Map::makeFailureInstanceWithMessage);

	}

	@Override
	public List<A> consList(final List<A> joinedList) {
		final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST
				.withArgumentName("joinedList").getMessage();

		return from(source)//
				.checkCondition(l -> If.givenObject(joinedList)//
						.isNonNull("joinedList")//
						.andIs(List::isProcessSuccess, errorProcessingMessage)
						// Using start and end in DoubleUtil to prevent unexpected behavior during
						// addition of two integers
						.andIs(j -> //
						l.size() + j.size().successValue()//
								<= Errors.Constants.MAX_LIST_SIZE//
								, GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.getMessage()))

				//
				.processOperation(l -> l.cons(joinedList.getListImpSource()))//
				.andMakeStackTraceUnderTheName("consList")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public <B> List<B> scanLeft(final B zero, final Function<B, Function<A, B>> accumulatorOp) {
		return from(source)//
				.checkCondition(() -> If.givenObject(zero)//
						.isNonNull("zero")//
						.andOtherObjectIsNotNull(accumulatorOp, "accumulatorOp"))
				.processOperationWithResult(l -> l.scanLeft(zero, accumulatorOp))//
				.andMakeStackTraceUnderTheName("scanLeft")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public <B> List<B> scanRight(final B zero, final Function<A, Function<B, B>> accumulatorOp) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(zero)//
						.isNonNull("zero")//
						.andOtherObjectIsNotNull(accumulatorOp, "accumulatorOp"))//
				.processOperationWithResult(l -> l.scanRight(zero, accumulatorOp))//
				.andMakeStackTraceUnderTheName("scanRight")//
				.mapTo(List::makeTypeSafe);//
	}

	@Override
	public Result<Boolean> endsWith(final List<A> suffix) {
		final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST.withArgumentName("suffix")
				.getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(suffix)//
						.isNonNull("suffix")//
						.andIs(List::isProcessSuccess, errorProcessingMessage))

				.processOperation(l -> l.endsWith(suffix.getListImpSource()))//
				.andMakeStackTraceUnderTheName("endsWith").getResultProccess();//
	}

	@Override
	public <B> List<B> sequence(final Function<A, Result<B>> transformer) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(transformer).isNonNull("transformer"))//
				.processOperationWithResult(l -> l.sequence(transformer))//
				.andMakeStackTraceUnderTheName("sequence")//
				.mapTo(List::makeTypeSafe);//

	}

	@Override
	public <A1, A2> Result<Tuple2<List<A1>, List<A2>>> unzip(final Function<A, Tuple2<A1, A2>> unzipper) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(unzipper).isNonNull("unzipper"))//
				.processOperationWithResult(l -> l.unzip(unzipper))//
				.andMakeStackTraceUnderTheName("unzip")//

				.getResultProccess()//
				.map(t -> t.map(List::makeTypeSafe, List::makeTypeSafe).successValue());
	}

	@Override
	public Result<Tuple2<List<A>, List<A>>> partition(final Predicate<A> partitioner) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(partitioner).isNonNull("partitioner"))//
				.processOperationWithResult(l -> l.partition(partitioner))//
				.andMakeStackTraceUnderTheName("partition")//
				.getResultProccess()//
				.map(t -> t.map(List::makeTypeSafe, List::makeTypeSafe).successValue());
	}

	@Override
	public List<A> interleave(final List<A> joinedList) {
		return PreconditionedProcess//
				.fromValidatedSupplier(//
						() -> this.zipAsPossible(joinedList)//
								.combineNestedLists(t -> list(t.state(), t.value()))//
								.getListResult())
				.andMakeStackTraceUnderTheName("interleave")//
				.getOrConvertToFailureState(List::failureWithMessage);//
	}

	@Override
	public List<A> intersperse(final A element) {
		return from(source)//
				.checkCondition(() -> //
				If.givenObject(element).isNonNull("element"))//
				.processOperation(l -> l.intersperse(element))//
				.andMakeStackTraceUnderTheName("intersperse")//
				.mapTo(List::makeTypeSafe);
	}

	@Override
	public Result<Boolean> startsWith(final List<A> sub) {

		final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST.withArgumentName("sub")
				.getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(sub)//
						.isNonNull("sub")//
						.andIs(List::isProcessSuccess, errorProcessingMessage))//
				.processOperation(l -> l.startsWith(sub.getListImpSource()))//
				.andMakeStackTraceUnderTheName("startsWith")//
				.getResultProccess();//

	}

	@Override
	public Result<Boolean> hasSubList(final List<A> sub) {
		final var errorProcessingMessage = CastumArgumentMessage.ERROR_PROCESSING_JOINED_LIST.withArgumentName("sub")
				.getMessage();

		return from(source)//
				.checkCondition(() -> If.givenObject(sub)//
						.isNonNull("sub")//
						.andIs(List::isProcessSuccess, errorProcessingMessage))//
				.processOperation(l -> l.hasSubList(sub.getListImpSource()))//
				.andMakeStackTraceUnderTheName("hasSubList")//
				.getResultProccess();//

	}

	public <B> List<A> safeApplySequence(

			final Function<A, Result<B>> f) {
		return PreconditionedProcess//
				.fromValidatedSupplier(//
						() -> this.sequence(a -> f.safeApplyOn(a)//
								.flatMap(x -> x)//
								.map(mc -> a)

						// Preserve the tuple if successful
						).getListResult())//
				.andMakeStackTraceUnderTheName("safeApplySequence")//
				.getOrConvertToFailureState(List::failureWithMessage);//
	}

	// -------------------------------------------------------------------------------------
	// End advanced
	// -------------------------------------------------------------------------------------

	public static class Errors {

		public static final class Constants {

			// A reasonable practical limit for most in-memory lists before performance and
			// memory issues become significant.
			// This value allows for a million elements, aligning with the upper end of your
			// assumed range.
			public static final int MAX_LIST_SIZE = 300_000;

			public static final int HALF_OF_MAX_LIST_SIZE = MAX_LIST_SIZE / 2;//

			public static final int MAX_INDEX = Integer.MAX_VALUE - 7;//

		}

		public enum EmptyListMessage implements ErrorOptions {
			ERROR_FIRST_ELEMENT_IN_EMPTY_LIST("Failed to get the first element"),
			ERROR_REST_ELEMENTS_IN_EMPTY_LIST("Failed to get the rest of elements"),
			ERROR_FIRST_ELEMENT_AND_REST_ELEMENTS_IN_EMPTY_LIST(
					"Failed to get the first element and the rest of elements"),
			ERROR_SET_FIRST_ELEMENT_IN_EMPTY_LIST("Failed to set the first element"),
			ERROR_REDUCE_IN_EMPTY_LIST("Failed to reduce (Can't reduce without a 0)"),
			ERROR_LAST_IN_EMPTY_LIST("Failed to get the list with only the last element"),
			ERROR_REMOVE_BY_INDEX_IN_EMPTY_LIST("Failed to remove element with certain index"),
			ERROR_SPLIT_AT_IN_EMPTY_LIST("Failed to make split at certain index"),
			ERROR_UPDATED_ALL_BETWEEN_IN_EMPTY_LIST("Failed to update all elements between certain indexes"),
			ERROR_SET_ELEMENT_AT_INDEX_IN_EMPTY_LIST("Failed to set an element at a certain index"),

			ERROR_INDEX_OF_IN_EMPTY_LIST("Failed to find index of an element"),
			ERROR_GET_ELEMENTS_AT_INDEXES_IN_EMPTY_LIST("Failed to get elements at certain indexes"),
			ERROR_FIRST_IN_EMPTY_LIST("Cannot retrieve the first element from an empty list."),
			ERROR_INDEX_WHERE_IN_EMPTY_LIST("Cannot find the index where a condition matches in an empty list.");

			private final String message;
			private final String DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST = ".\nCannot perform the operation on an empty list.";

			EmptyListMessage(final String message) {
				this.message = message + DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST;

			}

			@Override
			public String getMessage() {
				return message;
			}

		}

		public enum GeneralMessage implements ErrorOptions {
			ERROR_EMPTY_ARRAY("array size is 0 instead of being positive."),
			ERROR_FOUND_TRUE_VALUE("At least 1 boolean value in conditions is true."),
			ERROR_ZERO_LENGTH_ELEMENTS("The 'elements' argument has 0 length."),
			ERROR_INVALID_START_END_RANGE("Start value must be less than end value to ensure a positive range size."),

			ERROR_SIZE_LIMIT_EXCEEDED(String.format(
					"The size limit must not exceed (%,d). This limit safeguards against creating impractically large structures that could impact performance or memory. This applies to ranges, fills, and indices.",
					Constants.MAX_LIST_SIZE)),
			ERROR_SIZE_INDEX_EXCEEDED(String.format(
					"The index value cannot exceed (%,d), which is slightly less than the maximum allowed value for an index in a list or array.",
					Constants.MAX_INDEX)),
			ERROR_PROCESS_FAILURE_IN_NESTED_LIST(
					"At least 1 element in the provided list of lists has a failed process."),
			ERROR_NEGATIVE_FACTORIAL_INPUT("Factorial is not defined for negative numbers."),
			// ERROR_FACTORIAL_INPUT_LIMIT_EXCEEDED(
			// String.format("Input exceeds the maximum allowed limit of %d.",
			// Constants.FACTORIAL_INPUT_LIMIT)),
			ERROR_FROM_INDEX_IS_NOT_LESS_THAN_UNTIL_INDEX(
					"From index is not less than until index at least in 2 numbers"),
			ERROR_STEP_SIZE_TOO_LARGE("The range between 'from' and 'until' is too small for the specified step size"),
			ERROR_ELEMENT_NOT_FOUND("No element satisfying the function in list\n(Element not found)"),
			ERROR_TYPE_NOT_COMPARABLE("""
					Error constructing tree:
					The elements in the list are of type which does not implement Comparable.
					The type of the elements must implement Comparable to be inserted into a tree.
					""")

			;

			String message;

			GeneralMessage(final String message) {
				this.message = message;
			}

			@Override
			public String getMessage() {
				return message;
			}

		}

		public enum CastumValueMessage {
			ERROR_INDEXES_ARE_OUT_OF_RANGE("Those indexes are out of range:\n ###value###"),
			ERROR_INDEX_OF_ELEMENT_NOT_FOUND("""
					The element with value '###element###' could not be found in the list.
					Please verify that the element exists in the list before attempting to find its index.
					This error occurs if the element does not match any entry in the list,
					meaning it is not identical to any of the existing items in the collection.
					Ensure that the element you are searching for is correctly represented and exists in the list.
					""")

			;

			String message;

			CastumValueMessage(final String message) {
				this.message = message;
			}

			public <Q> ErrorOptions withValue(final Q value) {
				return () -> message.replaceAll("###.*###", value.toString());

			}

			public ErrorOptions withValue(final String value) {
				return () -> message.replaceAll("###.*###", value);

			}

		}

		public enum CastumArgumentMessage {
			ERROR_PROCESSING_JOINED_LIST("The process of ###joinedList### argument is NOT Success."),
			ERROR_NOT_INSTANCE_OF_LIST("###object### argument is NOT an instance of List."),
			ERROR_ZERO_LENGTH_ARGUMENT_ARRAY("The ###nameOfArray### argument has 0 length."),
			ERROR_NON_POSITIVE_VALUE("The ###nameOfNumber### argument must be positive"),

			ERROR_NEGATIVE_VALUE("The ###nameOfNumber### argument must be non negative");

			String message;

			CastumArgumentMessage(final String message) {
				this.message = message;
			}

			public ErrorOptions withArgumentName(final String argumentName) {
				return () -> message.replaceAll("###.*###", argumentName);
			}

		}

	}

	interface ListImpBehavior<A> {
		Result<Integer> generateHashCode();

		/**
		 * 🚀 Why This Works No mutation: Instead of modifying list, a new list is
		 * created at each step. Immutable approach: The previous state (list) is copied
		 * before adding a new element. No casting issues: The Supplier<L> ensures the
		 * correct list type is used.
		 */
		<L extends java.util.List<A>> Result<L> toJavaList(Supplier<L> listSupplier);

		Result<ListImp<A>> last();

		Result<A> lastOption();

		ListImp<A> trimLast();

		dev.ofekmalka.tools.stream.Stream<A> toStream();

		Set<A> toSet();

		boolean hasDuplicatedElements();

		boolean isPalindrome();

		boolean allEqual();

		ListImp<A> distinct();

		ListImp<ListImp<A>> subLists();

		ListImp<A> shuffle();

		ListImp<Tuple2<A, Integer>> zipWithPosition();


		ListImp<A> takeAtMost(int n);

		ListImp<A> dropAtMost(int n);

		Result<ListImp<A>> takeWhile(Predicate<A> predicate);

		Result<ListImp<A>> dropWhile(Predicate<A> predicate);

		ListImp<A> takeRightAtMost(int n);

		ListImp<A> dropRightAtMost(int n);

		Result<ListImp<A>> takeRightWhile(Predicate<A> predicate);

		Result<ListImp<A>> dropRightWhile(Predicate<A> predicate);

		Result<ListImp<A>> removeByIndex(int index);

		ListImp<A> getSublistInRange(int from, int until);

		Result<Tuple2<ListImp<A>, ListImp<A>>> splitAt(int index);

		Result<ListImp<A>> updatedAllBetween(int startIndex, int endIndex, A element);


		Result<ListImp<A>> setElementAtIndex(int index, A element);

		Result<Integer> indexOf(A element);

		ListImp<A> add(ListImp<A> other);

		ListImp<A> cons(ListImp<A> other);

		<B> Result<ListImp<B>> scanLeft(B zero, Function<B, Function<A, B>> f);

		<B> Result<ListImp<B>> scanRight(B zero, Function<A, Function<B, B>> f);

		boolean contains(A elem);

		boolean isEqualsWithoutConsiderationOrder(ListImp<A> otherList);

		boolean allEqualTo(A element);

		boolean endsWith(ListImp<A> suffix);

		Result<Integer> indexWhere(Predicate<A> predicate);

		Result<A> first(Predicate<A> predicate);

		Result<A> first(Predicate<A> predicate, String messageWhenElementNotFound);

		String mkStr(String sep);

		<B> Result<ListImp<B>> sequence(Function<A, Result<B>> f);

		<A1, A2> Result<Tuple2<ListImp<A1>, ListImp<A2>>> unzip(Function<A, Tuple2<A1, A2>> f);

		// <B> Result<Map<B, ListImp<A>>> groupBy(Function<A, B> f);

		Result<Tuple2<ListImp<A>, ListImp<A>>> partition(Predicate<A> predicate);

		ListImp<A> intersperse(A element);

		Result<Boolean> anyMatch(Predicate<A> predicate);

		Result<Boolean> allMatch(Predicate<A> predicate);

		Result<Boolean> n1Match(Predicate<A> predicate);

		ListImp<Result<A>> paddingRightWithEmptyResult(int targetLength);

		ListImp<Result<A>> paddingLeftWithEmptyResult(int targetLength);

		boolean startsWith(ListImp<A> sub);

		boolean hasSubList(ListImp<A> sub);

		Result<ListImp<A>> filter(Predicate<A> predicate);

		<B> Result<ListImp<B>> map(Function<A, B> elementTransformer);

		<B> Result<ListImp<B>> combineNestedLists(Function<A, List<B>> elementTransformer);

		Result<ListImp<A>> exclude(Predicate<A> predicate);

		<B> Result<B> foldRight(B identity, Function<A, Function<B, B>> accumulator);

		Result<A> reduce(Function<A, Function<A, A>> elementTransformer);

		ListImp<A> cons(A element);

		Tuple2<ListImp<A>, ListImp<A>> duplicate();

		<B> ListImp<Tuple2<A, B>> zipAsPossible(ListImp<B> joinedList);

		<B> Result<B> foldLeft(B identity, Function<B, Function<A, B>> accumulator);

		ListImp<A> reverse();

		boolean isEqualTo(Object other);

		Result<ListImp<A>> setFirstElement(A firstElement);

		Result<ListImp<A>> restElementsOption();

		Result<Tuple2<A, ListImp<A>>> firstElementAndRestElementsOption();

		boolean isEmpty();

		int size();

		boolean isNotEmpty();

		Result<A> firstElementOption();

		String representList();

		public interface UnsafeOperations<A> {
			////////////////////////////////////////
			// WE CREATE HER PRIVATE METHODS THAT HELP US HERE ONLY INTERNAL!!
			// SOME OF THEM IS FOR ASSUMPTIONS AND SAFE AND CLEAR VIEW
			// inner helpful methods only inner this class!!
			A firstElement();// QueryNoArgsOperations

			ListImp<A> restElements();// QueryNoArgsOperations

			ListImp<A> unsafeFilter(final Predicate<A> predicate);

			// inner validation cause return Result
			<B> ListImp<B> unsafeMap(final Function<A, B> elementTransformer);

			// inner validation cause return Result
			<B> ListImp<B> unsafeCombineNestedLists(final Function<A, List<B>> elementTransformer);

			<B> B unsafeFoldRight(final B identity, final Function<A, Function<B, B>> accumulator);

			<B> B unsafeFoldLeft(final B identity, final Function<B, Function<A, B>> accumulator);

		}

		public interface ExtendedFactoryOperations {

			// List Creation
			ListImp<Boolean> booleanList(final boolean... booleans);

			ListImp<Byte> byteList(final byte... bytes);

			ListImp<Integer> intList(final int... ints);

			ListImp<Character> charList(final char... chars);

			ListImp<Short> shortList(final short... shorts);

			ListImp<Float> floatList(final float... floats);

			ListImp<Double> doubleList(final double... doubles);

			ListImp<Long> longList(final long... longs);

			// Operations on Lists

			<T> ListImp<T> fromCollection(final Collection<T> ct);

			<T> ListImp<T> fromJavaStreamUtil(final java.util.stream.Stream<T> stream);

			<A> Result<ListImp<A>> iterate(final A seed, final Function<A, A> f, final int n);

			ListImp<Integer> generateRange(final int start, final int end);

			<A, S> Result<ListImp<A>> unfold(final S z, final Function<S, Result<Tuple2<A, S>>> generator);

			<A1, A2> Tuple2<ListImp<A1>, ListImp<A2>> unzip(final ListImp<Tuple2<A1, A2>> list);

			<A> Result<ListImp<A>> fill(final int n, final Supplier<A> s);

		}

	}

	 static abstract class ListImp<A> implements//
			ListImpBehavior<A>,

			ListImpBehavior.UnsafeOperations<A>//
	 {

		private ListImp() {
		}

		final static <A> ListImp<A> list(final Collection<A> collection) {

			return ListImp.staticMethod().fromCollection(collection);

		}
		// inner validation cause return Result

		@Override
		public ListImp<A> unsafeFilter(final Predicate<A> predicate) {
			return this.filter(predicate).successValue();

		}

		// inner validation cause return Result
		@Override
		public <B> ListImp<B> unsafeMap(final Function<A, B> elementTransformer) {
			return this.map(elementTransformer).successValue();

		}

		// inner validation cause return Result
		@Override
		public <B> ListImp<B> unsafeCombineNestedLists(final Function<A, List<B>> elementTransformer) {
			return this.combineNestedLists(elementTransformer).successValue();

		}

		// inner validation cause return Result
		@Override
		public <B> B unsafeFoldRight(final B identity, final Function<A, Function<B, B>> accumulator) {
			return this.foldRight(identity, accumulator).successValue();

		}

		// inner validation cause return Result
		@Override
		public <B> B unsafeFoldLeft(final B identity, final Function<B, Function<A, B>> accumulator) {
			return this.foldLeft(identity, accumulator).successValue();

		}

///////////////////////////////////////

		@SuppressWarnings("rawtypes")
		private static final ListImp NIL = new Nil();

		@SuppressWarnings("unchecked")
		static <A> ListImp<A> emptyList() {
			return NIL;
		}

		@SuppressWarnings("unchecked")
		static <A> Result<ListImp<A>> emptyResultList() {
			return Result.success(NIL);
		}

		/*
		 * Note that the list(A ... a) method is annotated with @SafeVarargs to indicate
		 * that the method doesn’t do anything that could lead to heap pollution. This
		 * method uses an imperative implementation based on a for loop. This isn’t very
		 * “functional,” but it’s a trade-off for simplicity and performance. If you
		 * insist on implementing it in a functional way, you can do so. All you need is
		 * a function taking an array as its argument and returning its last element,
		 * and another 1 to return the array without its last element. Here’s 1 possible
		 * solution:
		 *
		 * @SafeVarargs public static <A> List<A> list(A... as) { return list_(list(),
		 * as).eval(); } public static <A> TailCall<List<A>> list_(List<A> acc, A[] as)
		 * { return as.length == 0 ? ret(acc) : sus(()-> list_(new
		 * Cons<>(as[as.length-1], acc), Arrays.copyOfRange(as, 0, as.length- 1))); } Be
		 * sure, however, not to use this implementation, because it’s 10,000 times
		 * slower than the imperative 1. This is a good example of when not to be
		 * blindly functional. The imperative version has a functional interface, and
		 * this is what you need. Note that recursion isn’t the problem. Recursion using
		 * TailCall is nearly as fast as iteration. The problem here is the copyOfRange
		 * method, which is very slow.
		 *
		 *
		 */

		@SafeVarargs
		final static <A> ListImp<A> list(final A... a) {
			ListImp<A> list = emptyList();
			for (var i = a.length - 1; i >= 0; i--) {
				list = list.cons(a[i]);
			}
			return list;
		}

		@SafeVarargs
		final static <A> ListImp<A> reverseList(final A... a) {
			ListImp<A> list = emptyList();
			for (final A element : a) {
				list = list.cons(element);
			}
			return list;
		}

		@Override
		public String toString() {
			return this.representList();
		}

		@Override
		public String representList() {
			return isEmpty() ? "[NIL]"
					: "[" + this
							.<StringBuilder>unsafeFoldLeft(new StringBuilder(), acc -> el -> acc.append(el).append(","))
							.toString() + "NIL]";
		}

		final static class Nil<A> extends ListImp<A> {

			@Override
			public Result<Integer> generateHashCode() {

				return Result.<Integer>empty();
			}

			@Override
			public boolean isEqualTo(final Object other) {
				return other instanceof Nil;

			}

			@Override
			public int hashCode() {
				return 0; // All empty lists hash the same
			}

			@Override
			public boolean equals(final Object obj) {
				return this.isEqualTo(obj);
			}

			@Override
			public A firstElement() {
				throw new IllegalStateException("firstElement called on empty list");
			}

			@Override
			public ListImp<A> restElements() {
				throw new IllegalStateException("restElements called on empty list");
			}

			@Override
			public boolean isEmpty() {
				return TRUE;
			}

			@Override
			public int size() {
				return 0;
			}

			@Override
			public boolean isNotEmpty() {
				return FALSE;
			}

			@Override
			public Result<A> firstElementOption() {
				return

				Errors.EmptyListMessage.ERROR_FIRST_ELEMENT_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<ListImp<A>> restElementsOption() {
				return Errors.EmptyListMessage.ERROR_REST_ELEMENTS_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<Tuple2<A, ListImp<A>>> firstElementAndRestElementsOption() {
				return Errors.EmptyListMessage.ERROR_FIRST_ELEMENT_AND_REST_ELEMENTS_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<ListImp<A>> setFirstElement(final A firstElement) {
				return Errors.EmptyListMessage.ERROR_SET_FIRST_ELEMENT_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<A> reduce(final Function<A, Function<A, A>> elementTransformer) {
				return Errors.EmptyListMessage.ERROR_REDUCE_IN_EMPTY_LIST.asResult();
			}

			@Override
			public <B> Result<B> foldLeft(final B identity, final Function<B, Function<A, B>> accumulator) {
				return success(identity);
			}

			@Override
			public Result<ListImp<A>> last() {
				return Errors.EmptyListMessage.ERROR_LAST_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<ListImp<A>> removeByIndex(final int index) {
				return Errors.EmptyListMessage.ERROR_REMOVE_BY_INDEX_IN_EMPTY_LIST.asResult();

			}

			@Override
			public Result<Tuple2<ListImp<A>, ListImp<A>>> splitAt(final int index) {
				return Errors.EmptyListMessage.ERROR_SPLIT_AT_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<ListImp<A>> updatedAllBetween(final int fromIndex, final int untilIndex, final A element) {
				return Errors.EmptyListMessage.ERROR_UPDATED_ALL_BETWEEN_IN_EMPTY_LIST.asResult();
			}


			@Override
			public Result<ListImp<A>> setElementAtIndex(final int index, final A element) {
				return Errors.EmptyListMessage.ERROR_SET_ELEMENT_AT_INDEX_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<Integer> indexOf(final A element) {
				return Errors.EmptyListMessage.ERROR_INDEX_OF_IN_EMPTY_LIST.asResult();
			}

			@Override
			public Result<A> first(final Predicate<A> predicate) {

				return Errors.EmptyListMessage.ERROR_FIRST_IN_EMPTY_LIST.asResult();

			}

			@Override
			public Result<Integer> indexWhere(final Predicate<A> predicate) {
				return Errors.EmptyListMessage.ERROR_INDEX_WHERE_IN_EMPTY_LIST.asResult();
			}

		}

//-------------------------------------------------------------------
		final static class Cons<A> extends ListImp<A> {
			private final A firstElement;
			private final ListImp<A> restElements;
			private final int length;
			private final int hashCodeMemo;

			private Cons(final A firstElement, final ListImp<A> restElements) {
				this.firstElement = firstElement;
				this.restElements = restElements;
				this.length =

						restElements.size() + 1;
				// Memoize hashCode once, so it's O(1) after the first calculation
				this.hashCodeMemo = computeHashCode(firstElement, restElements);
			}

			private int computeHashCode(final A firstElement, final ListImp<A> restElements) {
				return Objects.hash(firstElement, restElements);
			}

			/**
			 * It’s up to you to decide whether you want to use memoization in your data
			 * structures. It may be a valid option for functions that are often called and
			 * don’t create new
			 *
			 * objects for their results. For example, the length and hashCode functions
			 * return integers, and the max and min functions return references to already
			 * existing objects, so they may be good candidates. On the other hand, the
			 * toString function creates new strings that would have to be memoized, so that
			 * would probably be a huge waste of memory space. The other factor to take into
			 * consideration is how often the function is used. The length function may be
			 * used more often than hashCode, because using lists as map keys is not a
			 * common practice.
			 *
			 *
			 */
			@Override
			public Result<Integer> generateHashCode() {
				return Result.success(hashCodeMemo); // Return precomputed hash code (O(1))
			}

			@Override
			public boolean isEqualTo(final Object o) {

				if (!(o instanceof final Cons<?> other) || this.size() != other.size())
					return false;

				ListImp<A> list1 = this;
				ListImp<?> list2 = other;

				while (!list1.isEmpty() && !list2.isEmpty()) {
					if (!Objects.equals(list1.firstElement(), list2.firstElement()))
						return false; // early exit on mismatch
					list1 = list1.restElements();
					list2 = list2.restElements();
				}

				// Both lists should be empty here, sizes were equal, so return true
				return true;

			}

			@Override
			public boolean equals(final Object obj) {
				return this.isEqualTo(obj);
			}

			@Override
			public A firstElement() {
				return firstElement;
			}

			@Override
			public ListImp<A> restElements() {
				return restElements;
			}

			static <A> ListImp<A> cons(final A a, final ListImp<A> restElements) {
				return new Cons<>(a, restElements);

			}

			@Override
			public boolean isEmpty() {
				return FALSE;
			}

			@Override
			public int size() {
				return length;
			}

			@Override
			public boolean isNotEmpty() {
				return TRUE;
			}

			@Override
			public Result<A> firstElementOption() {
				return success(this.firstElement());
			}

			@Override
			public Result<ListImp<A>> restElementsOption() {
				return success(this.restElements());
			}

			@Override
			public Result<Tuple2<A, ListImp<A>>> firstElementAndRestElementsOption() {
				return Tuple2.of(this.firstElement(), this.restElements()).getResult();
			}

			@Override
			public Result<ListImp<A>> setFirstElement(final A firstElement) {
				return success(Cons.<A>cons(firstElement, this.restElements()));

			}

			private <B> Result<B> foldLeftUnchecked(final B identity, final Function<B, Function<A, B>> accumulator) {
				var result = identity;
				ListImp<A> current = this;

				while (!current.isEmpty()) {
					final var fn = accumulator.apply(result);
					if (fn == null)
						return Result.failure(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage());
					final var nextResult = fn.apply(current.firstElement());
					if (nextResult == null)
						return Result.failure(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage());
					result = nextResult;
					current = current.restElements();
				}
				return Result.success(result);
			}
			// Then wrap with try-catch only where you call foldLeftUnchecked if you want:

			@Override
			public <B> Result<B> foldLeft(final B identity, final Function<B, Function<A, B>> accumulator) {
				try {
					return foldLeftUnchecked(identity, accumulator);
				} catch (final Exception e) {
					return Result.failure(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage());
				}
			}

			@Override
			public Result<A> reduce(final Function<A, Function<A, A>> elementTransformer) {
				return this.restElements().foldLeft(this.firstElement(), elementTransformer);
			}

			@Override
			public Result<ListImp<A>> last() {
				ListImp<A> current = this;
				while (!current.restElements().isEmpty()) {
					current = current.restElements();
				}
				return Result.success(current);
			}

			@Override
			public Result<ListImp<A>> removeByIndex(final int index) {

				ListImp<A> result = ListImp.emptyList();
				ListImp<A> current = this;
				var position = 0;
				while (!current.isEmpty()) {
					if (position != index) {
						result = result.cons(current.firstElement());
					}
					current = current.restElements();
					position++;
				}

				// If index was greater than or equal to size, it's effectively a no-op
				return Result.success(result.reverse()); // Reverse to restore original order

			}

			@Override
			public Result<Tuple2<ListImp<A>, ListImp<A>>> splitAt(final int index) {

				ListImp<A> left = ListImp.emptyList();
				ListImp<A> right = this;

				var i = 0;
				while (!right.isEmpty() && i < index) {
					left = left.cons(right.firstElement());
					right = right.restElements();
					i++;
				}

				return Tuple2.of(left.reverse(), right).getResult();

			}

			@Override
			public Result<ListImp<A>> updatedAllBetween(final int fromIndex, final int untilIndex, final A newElement) {
				return this.zipWithPosition().map( element ->

				element.value()   >= fromIndex && element.value() < untilIndex ? newElement : element.state()

				);//

			}



			@Override
			public Result<ListImp<A>> setElementAtIndex(final int indexPosition, final A newElement) {
				ListImp<A> current = this;
				ListImp<A> result = ListImp.emptyList();
				var index = 0;

				while (!current.isEmpty()) {
					final var updated = index == indexPosition ? newElement : current.firstElement();
					result = result.cons(updated);
					current = current.restElements();
					index++;
				}

				return Result.success(result.reverse());

			}

			@Override
			public Result<Integer> indexOf(final A element) {
				return indexWhere(e -> e.equals(element));
			}

		}

//START
//SameNilAndConsImpl<A>

		@Override
		public ListImp<A> cons(final A element) {
			return Cons.cons(element, this);
		}

		@Override
		public Tuple2<ListImp<A>, ListImp<A>> duplicate() {
			return Tuple2.of(this, this).getResult().successValue();
		}

		@Override
		public <B> ListImp<Tuple2<A, B>> zipAsPossible(final ListImp<B> joinedList) {

			var list1 = this;
			var list2 = joinedList;
			final var minElements = list1.size() > list2.size() ? list2.size() : list1.size();
			ListImp<Tuple2<A, B>> result = emptyList();

			Tuple2<A, B> newElement;
			for (var i = 0; i < minElements; i++) {

				newElement = Tuple2.of(list1.firstElement(), list2.firstElement()).getResult().successValue();

				list1 = list1.restElements();
				list2 = list2.restElements();

				result = result.cons(newElement);

			}

			return result.reverse();

		}

		@Override
		public ListImp<A> reverse() {
			return this.<ListImp<A>>unsafeFoldLeft(emptyList(), acc -> element -> acc.cons(element));
		}

		@Override
		public <B> Result<B> foldRight(final B identity, final Function<A, Function<B, B>> accumulator) {
			return //

			this.<ListImp<A>>foldLeft(emptyList(), acc -> element -> acc.cons(element))
					.flatMap(reverseList -> reverseList.foldLeft(identity,
							acc -> element -> accumulator.apply(element).apply(acc)))

			;
		}

		/**
		 *
		 *
		 * we can make this
		 *
		 * @Override public Result<List<A>> filter(final Predicate<A> predicate) {
		 *           return this.<A>flatMap(a -> //
		 *
		 *           If.isItTrue(predicate.matches(a))// maybe predicate throw exception
		 *           and thats why filter return Result<List<A>> .then(() ->
		 *           List.list(a).successValue())
		 *
		 *           .orGet(List::emptyList)// );//
		 *
		 *
		 *           }
		 *
		 *
		 *           but we give up for cases : list(1, 2, 3, 4, 5).filter(a -> a > 3 ?
		 *           null : true) not identifay as null error provided but Runtime
		 *           exception
		 *
		 */

		@Override
		public Result<ListImp<A>> filter(final Predicate<A> predicate) {
			return this.foldRight(emptyList(), element -> acc -> {
				final var keep = predicate.apply(element);
				if (keep == null)
					return null; // Consider failing fast earlier
				return keep ? acc.cons(element) : acc;
			});
		}

		@Override
		public Result<ListImp<A>> exclude(final Predicate<A> predicate) {
			return this.foldRight(emptyList(), element -> acc -> {
				final var keep = predicate.apply(element);
				if (keep == null)
					return null; // Same caveat here
				return keep ? acc : acc.cons(element);
			});
		}

		@Override
		public <B> Result<ListImp<B>> map(final Function<A, B> elementTransformer) {
			return this.foldRight(emptyList(), element -> acc -> {
				final var transformed = elementTransformer.apply(element);
				if (transformed == null)
					return null; // Same caveat here
				return acc.cons(transformed);
			});

		}

		@Override
		public <B> Result<ListImp<B>> combineNestedLists(final Function<A, List<B>> elementTransformer) {
			return this.foldRight(Result.success(ListImp.<B>emptyList()), element -> accResult -> {
				if (accResult.isFailure())
					return accResult;

				final var transformed = elementTransformer.apply(element);
				if (transformed == null) //
					return null; // Same caveat here

				if (transformed.getListResult().isFailure())
					return Result.failure("elementTransformer in combineNestedLists produced a failure List instance.");//

				return accResult.map(l -> l.cons(transformed.getListImpSource()));

			}).flatMap(flatList -> {
				final var totalSize = flatList.successValue().<Double>unsafeFoldLeft(0.0, acc -> x -> acc + 1);
				if (totalSize > List.Errors.Constants.MAX_LIST_SIZE)
					return List.Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.asResult();

				return Result.success(flatList.successValue()); // Because we cons'ed in reverse order
			});
		}

		// we always must keep the modifier "private"
		private static StaticMethod staticMethod() {
			return new StaticMethod();
		}

		private static class StaticMethod implements ListImpBehavior.ExtendedFactoryOperations {

			@Override
			public ListImp<Boolean> booleanList(final boolean... booleans) {
				return generateRange(0, booleans.length).unsafeMap(i -> booleans[i]);
			}

			@Override
			public ListImp<Byte> byteList(final byte... bytes) {
				return generateRange(0, bytes.length).unsafeMap(i -> bytes[i]);
			}

			@Override
			public ListImp<Integer> intList(final int... ints) {
				return generateRange(0, ints.length).unsafeMap(i -> ints[i]);
			}

			@Override
			public ListImp<Character> charList(final char... chars) {
				return generateRange(0, chars.length).unsafeMap(i -> chars[i]);

			}

			@Override
			public ListImp<Short> shortList(final short... shorts) {
				return generateRange(0, shorts.length).unsafeMap(i -> shorts[i]);

			}

			@Override
			public ListImp<Float> floatList(final float... floats) {
				return generateRange(0, floats.length).unsafeMap(i -> floats[i]);

			}

			@Override
			public ListImp<Double> doubleList(final double... doubles) {
				return generateRange(0, doubles.length).unsafeMap(i -> doubles[i]);

			}

			@Override
			public ListImp<Long> longList(final long... longs) {
				return generateRange(0, longs.length).unsafeMap(i -> longs[i]);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T> ListImp<T> fromCollection(final Collection<T> ct) {

				return If.givenObject(ct)//
						.is(Collection::isEmpty)//
						.will()//
						.returnValue(ListImp::emptyList)//
						.orGet(() -> ListImp.list(ct.toArray()))//
						.unsafeMap(o -> (T) o);//
			}

			@Override
			public <T> ListImp<T> fromJavaStreamUtil(final Stream<T> stream) {
				return this.fromCollection(stream.toList());
			}

			@Override
			public <A> Result<ListImp<A>> iterate(final A seed, final Function<A, A> f, final int n) {

				return generateRange(0, n)

						.<ListImp<A>>foldLeft(list(seed), acc -> elementIgnore -> 

						{
							final var resultFunc = f.apply(acc.firstElement());
							if (resultFunc == null)
								return null;
							return acc.cons(resultFunc);//

						}

						).map(ListImp::reverse);

			}

			@Override
			public ListImp<Integer> generateRange(final int start, final int end) {

				ListImp<Integer> acc = emptyList();
				for (var i = end - 1; i >= start; i--) { // Build in reverse order
					acc = acc.cons(i);
				}
				return acc;

			}

			@Override
			public <A, S> Result<ListImp<A>> unfold(final S seed, final Function<S, Result<Tuple2<A, S>>> generator) {

				try {
					return unfoldUnchecked(seed, generator);
				} catch (final Exception e) {
					return Result.failure(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage());
				}
			}

			private <A, S> Result<ListImp<A>> unfoldUnchecked(final S seed,
					final Function<S, Result<Tuple2<A, S>>> generator) {
				ListImp<A> result = emptyList();
				var state = seed;
				var count = 0;

				while (true) {
					final var tupleResult = generator.apply(state);

					if (tupleResult == null)
						return Result.failure(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage());

					if (tupleResult.isNotSuccess()) {
						break;
					}

					result = result.cons(tupleResult.successValue().state());
					state = tupleResult.successValue().value();

					count++;
					if (count > List.Errors.Constants.MAX_LIST_SIZE)
						return List.Errors.GeneralMessage.ERROR_SIZE_LIMIT_EXCEEDED.asResult();
				}

				return Result.success(result.reverse());
			}

			@Override
			public <A1, A2> Tuple2<ListImp<A1>, ListImp<A2>> unzip(final ListImp<Tuple2<A1, A2>> list) {
				ListImp<A1> acc1 = ListImp.emptyList();
				ListImp<A2> acc2 = ListImp.emptyList();

				var current = list;
				while (!current.isEmpty()) {
					final var head = current.firstElement();
					acc1 = acc1.cons(head.state());
					acc2 = acc2.cons(head.value());
					current = current.restElements();
				}

				return Tuple2.of(acc1.reverse(), acc2.reverse()).getResult().successValue();
			}

			@Override
			public <A> Result<ListImp<A>> fill(final int n, final Supplier<A> s) {
				return generateRange(0, n).map(ignore -> s.get());
			}

		}

////////////////////////////////////////
//////no args operations
/////////////////////////////////////

		@Override
		public Result<A> lastOption() {
			return this.unsafeFoldLeft(Result.<A>empty(), x -> Result::success);
		}

		@Override
		public ListImp<A> trimLast() {
			return this.dropRightAtMost(1);
		}

		/**
		 * 🚀 Why This Works No mutation: Instead of modifying list, a new list is
		 * created at each step. Immutable approach: The previous state (list) is copied
		 * before adding a new element. No casting issues: The Supplier<L> ensures the
		 * correct list type is used.
		 */
		@Override
		public <L extends java.util.List<A>> Result<L> toJavaList(final Supplier<L> listSupplier) {

			final var collectionResult = listSupplier.safeGet();

			if (collectionResult.isFailure())
				return collectionResult.mapFailureForOtherObject();

			// .map(collection -> this.unsafeMap(a -> collection.add(a) /* Add new element
			// */))

			// ; // Create a new list

			// ;

			final var collection = collectionResult.successValue();

			this.forEach(a -> collection.add(a));
			return Result.success(collection);

		}

		@Override
		public dev.ofekmalka.tools.stream.Stream<A> toStream() {
			return unsafeFoldRight(dev.ofekmalka.tools.stream.Stream.emptyStream(),
					item -> stream -> stream.cons(item));
		}

		@Override
		public dev.ofekmalka.core.data_structure.set.Set<A> toSet() {
			return unsafeFoldLeft(Set.<A>emptySet(), set -> item -> set.insert(item));
		}

		@Override
		public boolean hasDuplicatedElements() {
			return size() > this.toSet().cardinality().successValue();
		}

		@Override
		public boolean isPalindrome() {
			return equals(this.reverse());
		}

		@Override
		public boolean allEqual() {
			return this.toSet().cardinality().successValue() <= 1;
		}

///**
//* The {@code distinct()} method is designed to provide a new list that contains
//* distinct elements while preserving their original order as they appear in the
//* source list. This method ensures that no duplicates exist within the
//* resulting list, maintaining the uniqueness of elements. This is achieved
//* using a set-based approach without altering the original order of elements.
//*
//* @return A new list containing distinct elements in the same order as they
//*         appear in the source list.
//*
//* @implNote The method employs a set-based approach to efficiently eliminate
//*           duplicate elements. It utilizes the {@code Set.of(this)} construct
//*           to create an immutable set from the current list. By doing so, it
//*           leverages the set's inherent property of containing only distinct
//*           elements. The {@code filter()} function is then applied to the
//*           original list, retaining only those elements that are contained
//*           within the constructed set.
//*
//* @example Consider the following code: {@code
//* List<String> colors = List.of("red", "green", "blue", "green", "red");
//* List<String> distinctColors = colors.distinct(); // Result: ["red", "green", "blue"]
//* }
//*
//* @implSpec The {@code distinct()} method ensures uniqueness while preserving
//*           the original order of elements. If maintaining the order is a
//*           crucial requirement, this method is well-suited for achieving that
//*           goal.
//*/
//
		@Override
		public ListImp<A> distinct() {
			final var identity = Tuple2.of(ListImp.<A>emptyList(), Set.<String>emptySet());
			return this.unsafeFoldLeft(identity, acc -> element -> this.distinctOperation(acc, element))
					.mapTo(Tuple2::state)//
					.mapTo(ListImp::reverse)//
					.getResult().successValue();//

		}

		private OrGet<Tuple2<ListImp<A>, Set<String>>> distinctOperation(
				final OrGet<Tuple2<ListImp<A>, Set<String>>> accumulator, final A element) {
			final var elementRepresentation = element.toString();
			final var condition = accumulator

					.flatMapTo(t -> t.value().contains(elementRepresentation))//
					.getResult().successValue();//
			final var returnValue = accumulator;//
			final var defaultValue = accumulator

					.flatMapTo(t -> t.mapState(list -> list.cons(element)))//
					.flatMapTo(t -> t.mapValue(set -> set.insert(elementRepresentation)))//
			;//

			return If.isItTrue(condition)//
					.will()//
					.returnValue(() -> returnValue)//
					.orGet(() -> defaultValue);//
		}//

		/**
		 * The power set of a set S is the set of all subsets of S. [28] The empty set
		 * and S itself are elements of the power set of S, because these are both
		 * subsets of S. For example, the power set of {1, 2, 3} is {∅, {1}, {2}, {3},
		 * {1, 2}, {1, 3}, {2, 3}, {1, 2, 3}}. The power set of a set S is commonly
		 * written as P(S) or 2S. If S has n elements, then P(S) has 2^n elements. For
		 * example, {1, 2, 3} has three elements, and its power set has 2^3 = 8
		 * elements, as shown above.
		 *
		 */

		@Override
		@SuppressWarnings("unchecked")
		public ListImp<ListImp<A>> subLists() {

			final var distinctArray = (A[]) this

					.distinct()

					.toJavaList(java.util.ArrayList::new).successValue()

					.toArray();

			final var numberOfSubsets = 1 << distinctArray.length;// 2^n

			return staticMethod()//
					.generateRange(0, numberOfSubsets)//
					.<ListImp<A>>unsafeMap(i -> staticMethod()//
							.generateRange(0, distinctArray.length)//
							.unsafeFilter(j -> (i & 1 << j) != 0)//
							.unsafeMap(index -> distinctArray[index])//
							.reverse()//

					)

			;//

		}

		@Override
		@SuppressWarnings("unchecked")
		public ListImp<A> shuffle() {
			// Convert the list to an array for shuffling
			final var array = (A[]) this.toJavaList(java.util.ArrayList::new).successValue().toArray();

			// Create a new RNG for generating random indices
			final var rng = new SecureRandom();

			return this.zipWithPosition().map(tuple -> {
				// Generate a random index between 0 and i (inclusive)

				final var index = tuple.value();
				final var j = rng.nextInt(index + 1);

				// Swap array[i] and array[j]
				final var temp = array[index];
				array[index] = array[j];
				array[j] = temp;

				return array[index];
			}).successValue();
		}

		@Override
		public ListImp<Tuple2<A, Integer>> zipWithPosition() {

			ListImp<Tuple2<A, Integer>> result = emptyList();
			var current = this;
			var index = 0;

			while (!current.isEmpty()) {
				result = result.cons(Tuple2.of(current.firstElement(), index).getResult().successValue());
				current = current.restElements();
				index++;
			}

			return result.reverse();

		}

//		static <A> State<Integer, Tuple2<A, Integer>> assignId(final A a) {
//			return StateFactory.<Integer>get()
//					.flatMap(id -> StateFactory.set(id + 1).map(__ -> Tuple2.of(a, id).getResult().successValue()));
//
//		}

///////////////////////////////////////////////////
//all args operations
///////////////////////////////////////////////////



		@Override
		public ListImp<A> takeAtMost(final int n) {
			if (n >= size())
				return this;

			return take(n);
		}

		@Override
		public ListImp<A> dropAtMost(final int n) {
			if (n >= size())
				return emptyList();

			return drop(n);
		}

		private ListImp<A> take(final int n) {

			ListImp<A> result = ListImp.emptyList();
			var current = this;

			var count = n;

			while (count > 0) {

				result = result.cons(current.firstElement());
				current = current.restElements();
				count = count - 1;
			}

			return result.reverse(); // Reverse to restore original order
		}

		private ListImp<A> drop(final int n) {
			var current = this;

			var count = n;

			while (count > 0) {

				current = current.restElements();
				count = count - 1;
			}

			return current;
		}

		@Override
		public Result<ListImp<A>> takeWhile(final Predicate<A> predicate) {

			ListImp<A> result = ListImp.emptyList();
			var current = this;

			while (!current.isEmpty()) {
				final var firstElement = current.firstElement();

				final var predicateResult = predicate.safeApplyOn(firstElement);
				if (predicateResult.isFailure())
					return predicateResult.mapFailureForOtherObject();

				if (!predicateResult.successValue()) {
					break;
				}

				result = result.cons(firstElement);
				current = current.restElements();
			}

			return Result.success(result.reverse()); // Reverse to restore original order
		}

		@Override
		public Result<ListImp<A>> dropWhile(final Predicate<A> predicate) {

			var current = this;

			while (!current.isEmpty()) {
				final var firstElement = current.firstElement();

				final var predicateResult = predicate.safeApplyOn(firstElement);
				if (predicateResult.isFailure())
					return predicateResult.mapFailureForOtherObject();

				if (!predicateResult.successValue()) {
					break;
				}

				current = current.restElements();
			}

			return Result.success(current); // Reverse to restore original order

		}

		@Override
		public ListImp<A> takeRightAtMost(final int n) {
			return this.dropAtMost(size() - n);
		}

		@Override
		public ListImp<A> dropRightAtMost(final int n) {
			return this.takeAtMost(size() - n);
		}

		@Override
		public Result<ListImp<A>> takeRightWhile(final Predicate<A> predicate) {
			return this.reverse()//
					.takeWhile(predicate)//
					.map(ListImp::reverse);

		}

		@Override
		public Result<ListImp<A>> dropRightWhile(final Predicate<A> predicate) {

			final var optionIndex = this.takeRightWhile(predicate).map(ListImp::size);//

			if (optionIndex.isNotSuccess())//

				return optionIndex.mapFailureForOtherObject();//
			return success(this.dropRightAtMost(optionIndex.successValue()));//

		}

		@Override
		public ListImp<A> getSublistInRange(final int from, final int until) {
			final var diff = until - from;
			return dropAtMost(from).takeAtMost(diff);
		}

		@Override
		public ListImp<A> add(final ListImp<A> other) {
			return this.unsafeFoldRight(other, ele -> acc -> acc.cons(ele));
		}

		@Override
		public ListImp<A> cons(final ListImp<A> other) {
			return other.unsafeFoldRight(this, ele -> acc -> acc.cons(ele));

		}

		private <B> Result<ListImp<B>> scanLeftUnchecked(final B identity,
				final Function<B, Function<A, B>> accumulator) {

			var result = ListImp.<B>emptyList().cons(identity);
			var currentAcc = identity;
			var current = this;

			while (!current.isEmpty()) {
				final var fn = accumulator.apply(currentAcc);
				if (fn == null)
					return Result.failure(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage());

				final var newAcc = fn.apply(current.firstElement());
				if (newAcc == null)
					return Result.failure(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage());

				result = result.cons(newAcc);
				currentAcc = newAcc;
				current = current.restElements();
			}

			return Result.success(result.reverse());
		}

		@Override
		public <B> Result<ListImp<B>> scanLeft(final B identity, final Function<B, Function<A, B>> accumulator) {
			try {
				return scanLeftUnchecked(identity, accumulator);
			} catch (final Exception e) {
				return Result.failure(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage());
			}
		}

		private <B> Result<ListImp<B>> scanRightUnchecked(final B identity,
				final Function<A, Function<B, B>> accumulator) {

			final var reversed = this.reverse();
			var result = ListImp.<B>emptyList().cons(identity);
			var currentAcc = identity;
			var current = reversed;

			while (!current.isEmpty()) {
				final var fn = accumulator.apply(current.firstElement());
				if (fn == null)
					return Result.failure(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage());

				final var newAcc = fn.apply(currentAcc);
				if (newAcc == null)
					return Result.failure(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage());

				result = result.cons(newAcc);
				currentAcc = newAcc;
				current = current.restElements();
			}

			return Result.success(result);
		}

		@Override
		public <B> Result<ListImp<B>> scanRight(final B identity, final Function<A, Function<B, B>> accumulator) {
			try {
				return scanRightUnchecked(identity, accumulator);
			} catch (final Exception e) {
				return Result.failure(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage());
			}
		}

		@Override
		public boolean contains(final A elem) {
			return this.anyMatch(x -> x.equals(elem)).successValue();

		}

		@Override
		public boolean isEqualsWithoutConsiderationOrder(final ListImp<A> otherList) {
			// TODO find other alternative for using Java HashSet

			if (otherList.size() != size())
				return false;

			if (otherList == this)
				return true;

			return this.groupBy(s -> s)

					.map(map -> map.mapValues(ListImp::size)).successValue()

					.equals(otherList.groupBy(s -> s).map(map -> map.mapValues(ListImp::size)).successValue())//
			;//

		}

		// I want to consider an empty list as trivially satisfying the condition!.
		@Override
		public boolean allEqualTo(final A element) {
			var current = this;
			while (!current.isEmpty()) {
				if (!element.equals(current.firstElement()))
					return false;
				current = current.restElements();
			}
			return true; // Empty list or all matched
		}

		@Override
		public boolean endsWith(final ListImp<A> suffix) {

			final var thisSize = size();
			final var suffixSize = suffix.size();

			if (suffixSize > thisSize)
				return false;

			var currentThis = this;
			// Drop prefix to align with suffix start
			for (var i = 0; i < thisSize - suffixSize; i++) {
				currentThis = currentThis.restElements();
			}

			var currentSuffix = suffix;
			while (!currentSuffix.isEmpty()) {
				if (!currentThis.firstElement().equals(currentSuffix.firstElement()))
					return false;

				currentThis = currentThis.restElements();
				currentSuffix = currentSuffix.restElements();
			}
			return true;

		}

		private Result<Tuple2<A, Integer>> findFirstElementWithItsIndex(final Predicate<A> predicate) {
			var current = this;
			var index = 0;
			while (!current.isEmpty()) {
				final var element = current.firstElement();

				final var predicateResult = predicate.safeApplyOn(element);
				if (predicateResult.isFailure())
					return predicateResult.mapFailureForOtherObject();

				if (predicate.matches(element))
					return Tuple2.of(element, index).getResult();
				current = current.restElements();
				index++;
			}
			return Errors.GeneralMessage.ERROR_ELEMENT_NOT_FOUND.asResult();
		}

		@Override
		public Result<Integer> indexWhere(final Predicate<A> predicate) {
			return this.findFirstElementWithItsIndex(predicate).map(Tuple2::value);
		}

		@Override
		public Result<A> first(final Predicate<A> predicate, final String messageWhenElementNotFound) {

			var current = this;
			while (!current.isEmpty()) {
				final var element = current.firstElement();

				final var predicateResult = predicate.safeApplyOn(element);
				if (predicateResult.isFailure())
					return predicateResult.mapFailureForOtherObject();

				if (predicate.matches(element))
					return Result.success(element);
				current = current.restElements();
			}
			return Errors.GeneralMessage.ERROR_ELEMENT_NOT_FOUND.asResult();

		}

		@Override
		public Result<A> first(final Predicate<A> predicate) {
			return this.findFirstElementWithItsIndex(predicate).map(Tuple2::state);
		}

		@Override
		public String mkStr(final String sep) {

			final var sb = new StringBuilder();

			this.forEach(element -> {
				if (sb.length() > 0) {
					sb.append(sep);
				}
				sb.append(element);

			});

			return sb.toString();

		}

		private Nothing forEach(final Consumer<A> action) {
			var current = this;
			while (!current.isEmpty()) {
				action.accept(current.firstElement());
				current = current.restElements();
			}

			return Nothing.INSTANCE;
		}

		@Override
		public <B> Result<ListImp<B>> sequence(final Function<A, Result<B>> f) {

			ListImp<B> resultList = ListImp.emptyList();
			var current = this.reverse(); // So we can cons in order
			while (!current.isEmpty()) {
				final var element = current.firstElement();
				final Result<B> result = f.safeApplyOn(element).flatMap(t -> t);

				if (result.isFailure())
					return result.mapFailureForOtherObject();

				if (result.isEmpty())
					return Result.failure(CheckedOperation.ERROR_MESSAGE_EMPTY_RESULT.getMessage());

				resultList = resultList.cons(result.successValue());
				current = current.restElements();
			}

			return Result.success(resultList);
		}

		@Override
		public <A1, A2> Result<Tuple2<ListImp<A1>, ListImp<A2>>> unzip(final Function<A, Tuple2<A1, A2>> f) {
			ListImp<A1> list1 = ListImp.emptyList();
			ListImp<A2> list2 = ListImp.emptyList();

			var current = this.reverse(); // so we can cons in correct order

			while (!current.isEmpty()) {
				final var element = current.firstElement();
				final var result = f.safeApplyOn(element);

				if (result.isFailure())
					return result.mapFailureForOtherObject();

				final var pair = result.successValue();
				list1 = list1.cons(pair.state());
				list2 = list2.cons(pair.value());

				current = current.restElements();
			}

			return Tuple2.of(list1, list2).getResult();
		}

		public <B> Result<Map<B, List<A>>> advancedGroupBy(final Function<A, B> f) {
			Map<B, List<A>> grouped = Map.emptyMap();

			var current = this;

			while (!current.isEmpty()) {
				final var element = current.firstElement();
				final var result = f.safeApplyOn(element);

				if (result.isFailure())
					return result.mapFailureForOtherObject();

				final var key = result.successValue();
				final var group = grouped.getOptionally(key).getOrElse(List.emptyList());
				grouped = grouped.add(key, group.cons(element));

				current = current.restElements();
			}

			return Result.success(grouped);
		}

		private <B> Result<Map<B, ListImp<A>>> groupBy(final Function<A, B> f) {

			Map<B, ListImp<A>> grouped = Map.emptyMap();

			var current = this;

			while (!current.isEmpty()) {
				final var element = current.firstElement();
				final var result = f.safeApplyOn(element);

				if (result.isFailure())
					return result.mapFailureForOtherObject();

				final var key = result.successValue();
				final var group = grouped.getOptionally(key).getOrElse(ListImp.emptyList());
				grouped = grouped.add(key, group.cons(element));

				current = current.restElements();
			}

			return Result.success(grouped);

		}

		@Override
		public Result<Tuple2<ListImp<A>, ListImp<A>>> partition(final Predicate<A> predicate) {
			ListImp<A> matches = ListImp.emptyList();
			ListImp<A> nonMatches = ListImp.emptyList();

			var current = this.reverse(); // for consing in correct order

			while (!current.isEmpty()) {
				final var element = current.firstElement();
				final var predicateResult = predicate.safeApplyOn(element);

				if (predicateResult.isFailure())
					return predicateResult.mapFailureForOtherObject();

				if (predicateResult.successValue()) {
					matches = matches.cons(element);
				} else {
					nonMatches = nonMatches.cons(element);
				}

				current = current.restElements();
			}

			return Tuple2.of(matches, nonMatches).getResult();

		}

		@Override
		public ListImp<A> intersperse(final A element) {

			if (isEmpty() || size() == 1)
				return this;

			ListImp<A> result = emptyList();
			var current = this.reverse(); // reverse to preserve order when consing

			var first = true;
			while (!current.isEmpty()) {
				final var val = current.firstElement();
				if (!first) {
					result = result.cons(element);
				}
				result = result.cons(val);
				current = current.restElements();
				first = false;
			}

			return result;

		}

		@Override
		public Result<Boolean> anyMatch(final Predicate<A> predicate) {
			var current = this;
			while (!current.isEmpty()) {
				final var result = predicate.safeApplyOn(current.firstElement());
				if (result.isFailure())
					return result.mapFailureForOtherObject();
				if (result.successValue())
					return Result.success(true);
				current = current.restElements();
			}
			return Result.success(false);
		}

		@Override
		public Result<Boolean> n1Match(final Predicate<A> predicate) {
			return this.anyMatch(predicate).map(b -> !b);
		}

		@Override
		public Result<Boolean> allMatch(final Predicate<A> predicate) {
			var current = this;
			while (!current.isEmpty()) {
				final var result = predicate.safeApplyOn(current.firstElement());
				if (result.isFailure())
					return result.mapFailureForOtherObject();
				if (Boolean.FALSE.equals(result.successValue()))
					return Result.success(false);
				current = current.restElements();
			}
			return Result.success(true);
		}

		@Override
		public ListImp<Result<A>> paddingRightWithEmptyResult(final int targetLength) {
			return paddingWithEmptyResultHelper(targetLength, //
					paddedList -> emptyPadding -> paddedList.add(emptyPadding));
		}

		@Override
		public ListImp<Result<A>> paddingLeftWithEmptyResult(final int targetLength) {
			return paddingWithEmptyResultHelper(targetLength, //
					paddedList -> emptyPadding -> paddedList.cons(emptyPadding));

		}

		private ListImp<Result<A>> paddingWithEmptyResultHelper(final int targetLength, //
				final Function<ListImp<Result<A>>, Function<ListImp<Result<A>>, ListImp<Result<A>>>> paddingAction) {//

			final var currentSize = size();

			// No padding needed
			if (currentSize >= targetLength)
				return this.unsafeMap(Result::success);

			final var paddingCount = targetLength - currentSize;

			// Create the empty padding only once
			final var emptyPadding = ListImp.staticMethod().<Result<A>>fill(paddingCount, Result::empty).successValue();

			final ListImp<Result<A>> paddedList = this.unsafeMap(Result::success);

			return paddingAction.apply(paddedList).apply(emptyPadding);

		}

		@Override
		public boolean startsWith(final ListImp<A> sub) {
			if (size() < sub.size())
				return false;

			var currentThis = this;
			var currentSub = sub;

			while (!currentSub.isEmpty()) {
				if (!currentThis.firstElement().equals(currentSub.firstElement()))
					return false;
				currentThis = currentThis.restElements();
				currentSub = currentSub.restElements();
			}

			return true;
		}

		@Override
		public boolean hasSubList(final ListImp<A> sub) {
			if (sub.isEmpty())
				return true;
			if (size() < sub.size())
				return false;

			var current = this;
			while (!current.isEmpty()) {
				if (current.startsWith(sub))
					return true;
				current = current.restElements();
			}

			return false;
		}

	}

}
