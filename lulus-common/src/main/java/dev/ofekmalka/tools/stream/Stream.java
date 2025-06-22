package dev.ofekmalka.tools.stream;

import java.util.Objects;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.PreconditionedProcess;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.error.ErrorOptions;
import dev.ofekmalka.core.function.CheckedOperation;
import dev.ofekmalka.core.function.ConsoleOutputEffect;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.helper.Nothing;
import dev.ofekmalka.tools.helper.ValidatedSize;
import dev.ofekmalka.tools.tuple.Tuple2;

/**
 *
 * The Stream API is a great tool, but it’s not a silver bullet. For many cases
 * in functional programming, using eager collections (like List) and applying
 * higher-order functions or simple loops can be more effective, especially when
 * the data doesn't need lazy evaluation or parallelism.
 *
 * For most real-world applications, lists and collections with eager operations
 * are sufficient and can be more predictable in terms of performance and
 * readability. Stream should be used selectively, keeping in mind its strengths
 * and limitations, and often after gathering data into a collection.
 */
public final class Stream<A> implements //
		StreamBehavior.Operations.Combining<A>, //
		StreamBehavior.Operations.Filtering<A>, //
		StreamBehavior.Operations.Mapping<A>, //
		StreamBehavior.Operations.Windowing<A>, //
		StreamBehavior.Operations.OtherTransformations<A>, //
		ConsoleOutputEffect {//

	private final Result<Nothing> validator;
	private final UnsafeLazyList<A> unsafeLazyList;
	private static int DUMMY_POSITIVE_LIMIT = 2;

	private Stream(final Result<Nothing> validator, final UnsafeLazyList<A> unsafeLazyList) {
		this.validator = validator;
		this.unsafeLazyList = unsafeLazyList;
	}

	private Stream(final UnsafeLazyList<A> unsafeLazyList) {
		this.validator = Result.success(Nothing.INSTANCE);
		this.unsafeLazyList = unsafeLazyList;
	}

	/**
	 * @return the validator
	 */
	public Result<Nothing> getValidator() {
		return validator;
	}

	@SuppressWarnings("rawtypes")
	private static final Stream EMPTY_STREAM = new Stream<>(Result.success(Nothing.INSTANCE), UnsafeLazyList.empty());

	@SuppressWarnings("unchecked")
	public static <A> Stream<A> emptyStream() {
		return EMPTY_STREAM;
	}

	@Override
	public Result<Stream<A>> getStreamResult() {
		return validator.map(ignore -> this);

	}

	public static <A> Stream<A> makeFailureInstance() {
		return new Stream<>(Result.failure("failure instance"), UnsafeLazyList.empty());
	}

	public static <A> Stream<A> makeFailureInstanceWithMessage(final String message) {
		return new Stream<>(Result.failure(message), UnsafeLazyList.empty());
	}

	@Override
	public int hashCode() {
		return Objects.hash(unsafeLazyList, validator.hashCode());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof final Stream other))
			return false;
		return Objects.equals(unsafeLazyList, other.unsafeLazyList);
	}

	public static Stream<Integer> from(final int start) {
		return new Stream<>(UnsafeLazyList.staticMethod().from(start));
	}

	public static <A> Stream<A> repeat(final Supplier<A> supplier) {

		return PreconditionedProcess.from(Result.success(Nothing.INSTANCE))//

				.checkCondition(() -> //
				If.givenObject(supplier)//
						.isNonNull("supplier")

				).<Stream<A>>//
				processOperationBySupplierResult(() -> //

				supplier.safeGet().map(e ->

				new Stream<>(UnsafeLazyList.<A>staticMethod().repeat(e))

				)).andMakeStackTraceUnderTheName("repeat")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	public static <A> Stream<A> iterate(final A initialValue, final Function<A, A> generator) {
		final var validState = List.extendedFactoryOperations()
				.iterateWithSeed(initialValue, generator, DUMMY_POSITIVE_LIMIT)//
				.getListResult()//

				.getOrCreateFailureInstanceWithMessage(//
						s -> List.failureWithMessage(//
								s.replace("iterateWithSeed", "iterate")//
										.replace("seed", "initialValue")//
										.replace("transformationFunction", "generator")//

						))

				.mapListToResultOf(() -> Nothing.INSTANCE)

		;
		return new Stream<>(validState, //
				UnsafeLazyList.<A>staticMethod().iterate(initialValue, generator));
	}

	public static <S, A> Stream<A> unfold(final S initialState, final Function<S, Tuple2<A, S>> generator) {

		return PreconditionedProcess

				.from(Result.success(Nothing.INSTANCE))//
				.checkCondition(() -> //
				If.givenObject(initialState)//
						.isNonNull("initialState")

						.andOtherObjectIsNotNull(generator, "generator"))
				.<Stream<A>>//
				processOperationBySupplierObject(() -> {//
					final var validJustTheFirstTenInteractions = List.extendedFactoryOperations()//
							.generateRange(0, 100)//

							.foldRight(Tuple2.of(List.<A>emptyList(), initialState)

									.getResult().successValue(), element -> acc ->

							{
										final var apply = generator.apply(acc.value());
//										return If.givenObject(apply)//
//												.isNonNull()//
//												.will().<Tuple2<List<A>, S>>
//
//												returnValue(() -> {//
													final var newElementInList = apply.state();//
													final var newState = apply.value();//
													return acc.mapState(l -> l.cons(newElementInList))//
															.successValue()//
															.mapValue(ignore -> newState)//
															.successValue();//
//												})//
//
//												.orGet(() -> null)//

										//
										//;
									})
							.map(Tuple2::state)//

							.getOrCreateFailureInstanceWithMessage(//
									s -> List.failureWithMessage(s.replace("foldRight", "unfold")))

							.mapListToResultOf(() -> Nothing.INSTANCE);

					return new Stream<>(validJustTheFirstTenInteractions, //
							UnsafeLazyList.<A>staticMethod().unfold(initialState, generator));
				}).andMakeStackTraceUnderTheName("unfold")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	public static Stream<Integer> fibs() {
		return new Stream<>(UnsafeLazyList.<Integer>staticMethod().fibs());
	}

	@Override
	public Stream<A> setFirstElement(final A firstElement) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(firstElement)//
						.isNonNull("firstElement"))
				.<Stream<A>>//
				processOperationBySupplierResult(() -> //
				{

				try{ return Result.success(new Stream<A>(unsafeLazyList.setFirstElement(firstElement))  );} 
					
				catch (Exception e) {
					return Result.failure(e.getMessage());
				}
				})
				.andMakeStackTraceUnderTheName("setFirstElement")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<A> cons(final A element) {
		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(element)//
						.isNonNull("element"))//
				.processOperationBySupplierObject(() -> //

				new Stream<>(unsafeLazyList.cons(element))).andMakeStackTraceUnderTheName("cons")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<A> dropAtMost(final ValidatedSize n) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(n)//
						.isNonNull("n")//

				)//
				.processOperationBySupplierObject(() -> //

				new Stream<>(unsafeLazyList.dropAtMost(n.getValue()))).andMakeStackTraceUnderTheName("dropAtMost")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<A> dropWhile(final Predicate<A> predicate) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(predicate)//
						.isNonNull("predicate"))//

				.<Stream<A>>processOperationBySupplierResult(() -> //
				
				
				
				{

					try{ return Result.success(new Stream<A>(unsafeLazyList.dropWhile(predicate))  );} 
						
					catch (Exception e) {
						return Result.failure(e.getMessage());
					}
					}
				
				
				
				
			)

				.andMakeStackTraceUnderTheName("dropWhile")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<A> takeAtMost(final ValidatedSize n) {
		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(n)//
						.isNonNull("n")//

				)//
				.processOperationBySupplierObject(() ->

				new Stream<>(unsafeLazyList.takeAtMost(n.getValue()))).andMakeStackTraceUnderTheName("takeAtMost")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<A> takeWhile(final Predicate<A> predicate) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(predicate)//
						.isNonNull("predicate"))//

				.<Stream<A>>processOperationBySupplierResult(() -> //
				
				
				
				
				{

					try{ return Result.success(new Stream<A>(unsafeLazyList.takeWhile(predicate))  );} 
						
					catch (Exception e) {
						return Result.failure(e.getMessage());
					}
					}
				
				
				
				
		)
				.andMakeStackTraceUnderTheName("takeWhile")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	// TODO
	public <B> Stream<B> flatMap(final Function<A, Stream<B>> elementTransformer) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(elementTransformer)//
						.isNonNull("elementTransformer"))//

				.<Stream<B>>processOperationBySupplierResult(() -> {

					final var map = this.mapWithoutStackTraceUnderTheName(elementTransformer);
					final var unEvalueted = map.unsafeLazyList.flatMap(s -> s.unsafeLazyList);

					final var listResult = unEvalueted.takeAtMostBeforeTrigger(4).getListResult();
					final var validState = map.validator.flatMap(nothing -> listResult.map(ignore -> nothing));

					return new Stream<>(validState, unEvalueted).getStreamResult();
				})//

				.andMakeStackTraceUnderTheName("flatMap")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<A> filter(final Predicate<A> predicate) {

		return PreconditionedProcess//
				.from(validator)//
				.checkCondition(() -> //
				If.givenObject(predicate)//
						.isNonNull("predicate"))//

				.<Stream<A>>processOperationBySupplierResult(() ->

			
				{

					try{ return Result.success(new Stream<A>(unsafeLazyList.filter(predicate))  );} 
						
					catch (Exception e) {
						return Result.failure(e.getMessage());
					}
					}
				
				
				
				
		)

				.andMakeStackTraceUnderTheName("filter")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage)

		;//

	}

	@Override
	public <B> Stream<B> map(final Function<A, B> elementTransformer) {
		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(elementTransformer)//
						.isNonNull("elementTransformer"))//
				.processOperationBySupplierResult(() -> {

					final var unEvalueted = unsafeLazyList.map(elementTransformer);

					final var listResult = unEvalueted.takeAtMostBeforeTrigger(4).getListResult();
					final var validState = listResult.map(ignore -> Nothing.INSTANCE);

					return new Stream<>(validState, unEvalueted).getStreamResult();
				})//

				.andMakeStackTraceUnderTheName("map")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	private <B> Stream<B> mapWithoutStackTraceUnderTheName(final Function<A, B> elementTransformer) {
		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(elementTransformer)//
						.isNonNull("elementTransformer"))//
				.processOperationBySupplierResult(() -> {

					final var unEvalueted = unsafeLazyList.map(elementTransformer);

					final var listResult = unEvalueted.takeAtMostBeforeTrigger(4).getListResult();
					final var validState = listResult.map(ignore -> Nothing.INSTANCE);

					return new Stream<>(validState, unEvalueted).getStreamResult();
				})//

				.withoutStackTrace().getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<A> append(final Stream<A> joinedStream) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(joinedStream)//
						.isNonNull("joinedStream")//

						.andIs(stream -> stream.validator.isSuccess(), //

								Stream.Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_STREAM
										.withArgumentName("joinedStream")

										.getMessage())//
				).processOperationBySupplierObject(() ->

				new Stream<>(unsafeLazyList.append(() -> joinedStream.unsafeLazyList)))//
				.andMakeStackTraceUnderTheName("append")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<List<A>> windowFixed(final ValidatedSize windowSize) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(windowSize)//
						.isNonNull("windowSize")//

				)//
				.processOperationBySupplierObject(() ->

				new Stream<>(unsafeLazyList.windowFixed(windowSize.getValue())))
				.andMakeStackTraceUnderTheName("windowFixed")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<List<A>> windowSliding(final ValidatedSize windowSize) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(windowSize)//
						.isNonNull("windowSize")//

				)//
				.processOperationBySupplierObject(() ->

				new Stream<>(unsafeLazyList.windowSliding(windowSize.getValue())))
				.andMakeStackTraceUnderTheName("windowSliding")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public Stream<List<A>> windowFixedAtMost(final ValidatedSize windowSize) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(windowSize)//
						.isNonNull("windowSize")//

				)//
				.processOperationBySupplierObject(() ->

				new Stream<>(unsafeLazyList.windowFixedAtMost(windowSize.getValue())))
				.andMakeStackTraceUnderTheName("windowFixedAtMost")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public <B> Stream<Tuple2<A, B>> zipAsPossible(final Stream<B> joinedStream) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() ->

				If.givenObject(joinedStream)//
						.isNonNull("joinedStream")//
						.andIs(stream -> stream.validator.isSuccess()

								,
								Errors.CastumArgumentMessage.ERROR_PROCESSING_JOINED_STREAM
										.withArgumentName("joinedStream").getMessage()))//
				.<Stream<Tuple2<A, B>>>processOperationBySupplierObject(() ->

				new Stream<>(unsafeLazyList.zipAsPossible(joinedStream.unsafeLazyList)))
				.andMakeStackTraceUnderTheName("zipAsPossible")//
				.getOrConvertToFailureState(Stream::makeFailureInstanceWithMessage);//

	}

	@Override
	public List<A> toBoundedList(final ValidatedSize size) {

		return PreconditionedProcess.from(validator)//
				.checkCondition(() -> //
				If.givenObject(size)//
						.isNonNull("size")//

				)//
				.processOperationBySupplierResult(() -> //


				
				{

					try{ return Result.success(unsafeLazyList.takeAtMostBeforeTrigger(size.getValue()));} 
						
					catch (Exception e) {
						return Result.failure(Errors.ERROR_STREAM_GENERAL_MESSAGE.getMessage());
					}
					}
				
				
				
				
		)
				
				
				.andMakeStackTraceUnderTheName("toBoundedList")//
				.getOrConvertToFailureState(List::failureWithMessage);//

	}

	public static class Errors {

		public static ErrorOptions ERROR_STREAM_GENERAL_MESSAGE = () -> """
				A runtime exception was thrown from the checked operation, which is undesirable!
				or Null value encountered during operation.
				Please ensure that the function provided does not produce null results or runtime exception results
				when applied to its arguments.""";

		enum StreamEmptyErrorMessage implements ErrorOptions {
			ERROR_SET_FIRST_ELEMENT_IN_EMPTY_STREAM("Failed to set the firstElement") //
			;

			private final String message;
			private final String DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST;

			StreamEmptyErrorMessage(final String message) {
				this.message = message;
				DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST = this.message
						+ ":\nCannot perform the operation on an empty UnsafeLazyList.";
			}

			@Override
			public String getMessage() {
				return DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST;
			}

		}

		public enum CastumArgumentMessage {
			ERROR_PROCESSING_JOINED_STREAM("The process of ###joinedStream### argument is NOT Success.");

			String message;

			CastumArgumentMessage(final String message) {
				this.message = message;
			}

			public ErrorOptions withArgumentName(final String argumentName) {
				return () -> message.replaceAll("###.*###", argumentName);
			}

		}

		public enum GeneralMessage implements ErrorOptions {
			ERROR_STREAM_IS_IN_INFINITE_ISSUE("The stream operation appears to be in an infinite state.");

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

	public interface UnsafeLazyListBehavior {
		interface Creation<A> {

			UnsafeLazyList<Integer> from(final int i);

			UnsafeLazyList<A> repeat(final A a);

			UnsafeLazyList<A> iterate(final A seed, final Function<A, A> f);

			<S> UnsafeLazyList<A> unfold(final S z, final Function<S, Tuple2<A, S>> f);

			UnsafeLazyList<Integer> fibs();

		}

		interface Base<A> {
			A firstElement();

			Result<A> firstElementOption();

			UnsafeLazyList<A> restElements();

			boolean isEmpty();

			<B> B foldRight(Supplier<B> identity, Function<A, Function<Supplier<B>, B>> f);

		}

		interface Intermediate<A> {

			UnsafeLazyList<A> setFirstElement(A firstElement);

			UnsafeLazyList<List<A>> windowFixedAtMost(int windowSize);

			UnsafeLazyList<A> takeAtMost(int n);

			UnsafeLazyList<A> append(Supplier<UnsafeLazyList<A>> s);

			UnsafeLazyList<List<A>> windowFixed(int windowSize);

			UnsafeLazyList<List<A>> windowSliding(int windowSize);

			<B> UnsafeLazyList<Tuple2<A, B>> zipAsPossible(UnsafeLazyList<B> that);

			UnsafeLazyList<A> cons(A element);

			UnsafeLazyList<A> dropAtMost(int n);

			UnsafeLazyList<A> takeWhile(Predicate<A> f);

			UnsafeLazyList<A> dropWhile(Predicate<A> f);

			<B> UnsafeLazyList<B> flatMap(Function<A, UnsafeLazyList<B>> f);

			UnsafeLazyList<A> filter(Predicate<A> p);

			<B> UnsafeLazyList<B> map(Function<A, B> f);

		}

		interface Terminal<A> {

			List<A> takeAtMostBeforeTrigger(int n);

			enum StreamEmptyErrorMessage {
				ERROR_FIRST_ELEMENT_IN_EMPTY_STREAM("Failed to get the firstElement"), //
				ERROR_REST_ELEMENTS_IN_EMPTY_STREAM("Failed to get the restElements");

				private final String message;
				private final String DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST;

				StreamEmptyErrorMessage(final String message) {
					this.message = message;
					DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST = this.message
							+ ":\nCannot perform the operation on an empty UnsafeLazyList.";
				}

				public <T> Result<T> getResultErrorDescription() {
					return Result.failure(DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST);
				}

				public String getMessage() {
					return DEFAULT_ERROR_MESSAGE_IN_EMPTY_LIST;
				}

			}

		}

	}

	public static abstract class UnsafeLazyList<A>

			implements UnsafeLazyListBehavior.Base<A>, //
			UnsafeLazyListBehavior.Intermediate<A>, //
			UnsafeLazyListBehavior.Terminal<A>

	{//

		/*
		 *
		 * You're correct that using toList() for equality comparison in a potentially
		 * infinite stream context is not appropriate, as toList() would attempt to
		 * materialize the entire stream, which could lead to infinite processing and is
		 * against the nature of streams.
		 *
		 * For streams that can be infinite or very large, equality should typically be
		 * based on a subset of elements or some other identifiable characteristic that
		 * doesn't require traversing the entire stream. Here’s a revised approach to
		 * implementing equals() and hashCode() for your UnsafeLazyList<A> class,
		 * considering the constraints:
		 *
		 * Implementing equals() Method Since direct element-by-element comparison (like
		 * with toList()) isn't feasible for infinite streams, you might consider
		 * comparing streams up to a certain limit or based on another characteristic:
		 *
		 *
		 */
		private static final int MAX_COMPARISON_SIZE = List.Errors.Constants.MAX_LIST_SIZE;

		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null || this.getClass() != obj.getClass())
				return false;

			final var other = (UnsafeLazyList<A>) obj;

			// Compare streams up to a certain limit or based on another characteristic
			// For example, compare the first 100 elements or based on some unique
			// identifier
			// Implement this based on the specific characteristics of your streams
			// Here's a hypothetical example comparing the first 100 elements:
			return this.takeAtMostBeforeTrigger(MAX_COMPARISON_SIZE)
					.equals(other.takeAtMostBeforeTrigger(MAX_COMPARISON_SIZE));
		}

		// The constructor of the UnsafeLazyList class is private to prevent direct
		// instantiation.
		private UnsafeLazyList() {
		}

		@SuppressWarnings("rawtypes")
		private static UnsafeLazyList EMPTY = new Empty();

		@SuppressWarnings("unchecked")
		public static <A> UnsafeLazyList<A> empty() {
			return EMPTY;
		}

		// A convenience method to simplify stream creation
		static <A> UnsafeLazyList<A> cons(final Supplier<A> hd, final Supplier<UnsafeLazyList<A>> tl) {
			return new Cons<>(hd, tl);
		}

		// A convenience method to simplify stream creation
		static <A> UnsafeLazyList<A> cons(final Supplier<A> hd, final UnsafeLazyList<A> tl) {
			return new Cons<>(hd, () -> tl);
		}

		public static <A> StaticMethods<A> staticMethod() {
			return new StaticMethods<>();
		}

		public static class StaticMethods<A> implements UnsafeLazyListBehavior.Creation<A> {

			@Override
			public UnsafeLazyList<Integer> from(final int i) {
				return cons(() -> i, () -> this.from(i + 1));
			}

			@Override
			public UnsafeLazyList<A> repeat(final A a) {
				return cons(() -> a, () -> this.repeat(a));

			}

			@Override
			public UnsafeLazyList<A> iterate(final A seed, final Function<A, A> f) {
				return cons(() -> seed, () -> iterate(f.apply(seed), f));
			}

			@Override
			public <S> UnsafeLazyList<A> unfold(final S z, final Function<S, Tuple2<A, S>> f) {

				return cons(() -> f.apply(z).state(), () -> unfold(f.apply(z).value(), f));

			}

			@Override
			public UnsafeLazyList<Integer> fibs() {
				return UnsafeLazyList.<Integer>staticMethod().unfold(Tuple2.of(1, 1).getResult().successValue(), //
						x -> Tuple2
								.of(x.state(), Tuple2.of(x.value(), x.state() + x.value()).getResult().successValue())//
								.getResult().successValue());//

			}

		}

		private static class Empty<A> extends UnsafeLazyList<A> {
			@Override
			public UnsafeLazyList<A> restElements() {
				throw new IllegalStateException(
						StreamEmptyErrorMessage.ERROR_REST_ELEMENTS_IN_EMPTY_STREAM.getMessage());

			}

			@Override
			public A firstElement() {
				throw new IllegalStateException(
						StreamEmptyErrorMessage.ERROR_FIRST_ELEMENT_IN_EMPTY_STREAM.getMessage());
			}

			@Override
			public UnsafeLazyList<A> setFirstElement(final A firstElement) {
				throw new IllegalStateException(

						Errors.StreamEmptyErrorMessage.ERROR_SET_FIRST_ELEMENT_IN_EMPTY_STREAM.getMessage());
			}

			@Override
			public Result<A> firstElementOption() {
				return Result.empty();
			}

			@Override
			public boolean isEmpty() {
				return true;
			}

			@Override
			public <B> B foldRight(final Supplier<B> z, final Function<A, Function<Supplier<B>, B>> f) {
				return z.get();
			}

		}

		private static class Cons<A> extends UnsafeLazyList<A> {

			private final Supplier<A> firstElement;
			private final Result<A> h;
			private final Supplier<UnsafeLazyList<A>> restElements;

			private Cons(final Supplier<A> h, final Supplier<UnsafeLazyList<A>> t) {
				firstElement = h;
				restElements = t;
				this.h = Result.empty();
			}

			private Cons(final A h, final Supplier<UnsafeLazyList<A>> t) {
				firstElement = () -> h;
				restElements = t;
				this.h = Result.success(h);
			}

			@Override
			public UnsafeLazyList<A> restElements() {
				return restElements.get();
			}

			@Override
			public A firstElement() {
				return h.getOrElse(firstElement.get());
			}

			@Override
			public Result<A> firstElementOption() {
				return Result.success(firstElement());
			}

			@Override
			public UnsafeLazyList<A> setFirstElement(final A firstElement) {
				return cons(() -> firstElement, () -> restElements());
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public <B> B foldRight(final Supplier<B> identity, final Function<A, Function<Supplier<B>, B>> f) {
				return f.apply(firstElement()).apply(() -> restElements().foldRight(identity, f));

			}

		}

		@Override
		public UnsafeLazyList<A> dropAtMost(final int n) {
			if (n <= 0 || isEmpty())
				return this;

			var current = this;
			var toDrop = n;

			while (toDrop > 0 && !current.isEmpty()) {
				current = current.restElements();
				toDrop--;
			}

			return current;
		}

		@Override
		public UnsafeLazyList<A> takeWhile(final Predicate<A> f) {
			return foldRight(UnsafeLazyList::empty, a -> b -> {

				final var safeApplyOn = f.safeApplyOn(a);
				if (safeApplyOn.isFailure())

					throw safeApplyOn.failureValue();

				return safeApplyOn.successValue() ? cons(() -> a, b) : empty();
			}

			);
		}

		@Override
		public UnsafeLazyList<A> dropWhile(final Predicate<A> f) {
			var current = this;

			while (!current.isEmpty()) {
				try {

					final var safeApplyOn = f.safeApplyOn(current.firstElement());

					if (safeApplyOn.isFailure())
						throw safeApplyOn.failureValue();

					if (!f.apply(current.firstElement())) {
						break;
					}
				} catch (final Exception e) {
					throw new RuntimeException(e.getMessage());
				}
				current = current.restElements();
			}

			return current;
		}

		@Override
		public <B> UnsafeLazyList<B> flatMap(final Function<A, UnsafeLazyList<B>> f) {
			return foldRight(UnsafeLazyList::empty, a -> b -> f.apply(a).append(b));
		}

		@Override
		public UnsafeLazyList<A> append(final Supplier<UnsafeLazyList<A>> s) {
			return foldRight(s, a -> b -> cons(() -> a, b));
		}

		@Override
		public UnsafeLazyList<A> filter(final Predicate<A> p) {
			validationPredicateOrThrowException(p);
			final var stream = this.dropWhile(x -> !p.apply(x));

			return stream.isEmpty() //
					? stream //
					: cons(() -> stream.firstElement(), () -> stream.restElements().filter(p));

		}

		private Nothing validationPredicateOrThrowException(final Predicate<A> f) {
			this.dropWhile(x -> f.apply(x));
			return Nothing.INSTANCE;
		}

		/**
		 *
		 * Using foldRight to implement various methods is a smart technique.
		 * Unfortunately, it doesn’t really work for filter. If you test this method
		 * with a predicate that’s not matched by more than 1,000 or 2,000 consecutive
		 * elements, it will overflow the stack.
		 *
		 *
		 */

		public UnsafeLazyList<A> filterForTestingOnly(final Predicate<A> p) {
			return foldRight(UnsafeLazyList::empty, a -> b -> p.apply(a) ? cons(() -> a, b) : b.get());
		}

		@Override
		public <B> UnsafeLazyList<B> map(final Function<A, B> f) {
			return foldRight(UnsafeLazyList::empty, a -> b -> cons(() -> f.apply(a), b)

			);
		}

		@Override
		public List<A> takeAtMostBeforeTrigger(final int n) {
			try {
				var current = this.takeAtMost(n);
				List<A> acc = List.emptyList();
				var count = n;

				while (!current.isEmpty() && count > 0) {
					acc = acc.cons(current.firstElement());
					current = current.restElements();
					count--;
				}

				return acc.reverse().getListResult()
						.getOrElse(List.<A>failureWithMessage(CheckedOperation.ERROR_MESSAGE_NULL_RESULT.getMessage()))

				;
			} catch (final Exception e) {
				return List.failureWithMessage(CheckedOperation.ERROR_MESSAGE_RUNTIME_EXCEPTION.getMessage());
			}
		}

		@Override
		public UnsafeLazyList<A> takeAtMost(final int n) {
			if (n <= 0 || isEmpty())
				return UnsafeLazyList.empty();

			// Use an array to mutate inside lambda (Java closure workaround)
			return cons(this::firstElement, () -> restElements().takeAtMost(n - 1));
		}

		@Override
		public UnsafeLazyList<List<A>> windowFixed(final int windowSize) {

			return windowSlidingHelper(windowSize)//
					.takeWhile(e -> e.size().successValue() == windowSize)
					.zipAsPossible(UnsafeLazyList.staticMethod().from(0))//
					.filter(t -> t.value() % windowSize == 0)//
					.map(Tuple2::state)

			;

		}

		@Override
		public UnsafeLazyList<List<A>> windowFixedAtMost(final int windowSize) {

			return windowFixedAtMostHelper(windowSize);// .takeWhile(e -> e.size().successValue() == windowSize);

		}

		private UnsafeLazyList<List<A>> windowFixedAtMostHelper(final int windowSize) {

			final Function<UnsafeLazyList<A>, List<A>> f = t -> t.takeAtMostBeforeTrigger(windowSize);
			return

			isEmpty() ? UnsafeLazyList.<List<A>>empty() :

					this.dropAtMost(windowSize).isEmpty() ?

							UnsafeLazyList.<List<A>>empty().cons(f.apply(this)) :

							UnsafeLazyList.<List<A>>cons(() -> f.apply(this), //
									() ->

									this.dropAtMost(windowSize).windowFixedAtMostHelper(windowSize));

		}

		@Override
		public UnsafeLazyList<List<A>> windowSliding(final int windowSize) {

			return windowSlidingHelper(windowSize);// .takeWhile(e -> e.size().successValue() == windowSize);

		}

		private UnsafeLazyList<List<A>> windowSlidingHelper(final int windowSize) {

			final Function<UnsafeLazyList<A>, List<A>> f = t -> t.takeAtMostBeforeTrigger(windowSize);
			return

			isEmpty() ?

					UnsafeLazyList.<List<A>>empty() :

					this.dropAtMost(windowSize).isEmpty() ?

							UnsafeLazyList.<List<A>>empty().cons(f.apply(this)) :

							UnsafeLazyList.<List<A>>cons(() -> f.apply(this), //
									() -> restElements().windowSlidingHelper(windowSize));

		}

		@Override
		public <B> UnsafeLazyList<Tuple2<A, B>> zipAsPossible(final UnsafeLazyList<B> that) {
			return this
					.<B, Tuple2<Result<A>, Result<B>>>zipAllGeneric(that,
							x -> y -> Tuple2.of(x, y).getResult().successValue())

					.takeWhile(t -> t.state().isSuccess() && t.state().isSuccess())

					.map(t -> t.map(Result::successValue, Result::successValue).successValue());
		}

		private <B, C> UnsafeLazyList<C> zipAllGeneric(final UnsafeLazyList<B> that,
				final Function<Result<A>, Function<Result<B>, C>> f) {
			final UnsafeLazyList<Result<A>> a = infinite(this);
			final UnsafeLazyList<Result<B>> b = infinite(that);
			final var empty = Result.<Tuple2<C, Tuple2<UnsafeLazyList<Result<A>>, UnsafeLazyList<Result<B>>>>>empty();

			return unfoldProvidedResult(Tuple2.of(a, b).getResult().successValue(),

					x -> (x.state().isEmpty() && x.value().isEmpty() ? empty
							: x.state().firstElement().isEmpty() && x.value().firstElement().isEmpty() ? empty :
							////////
									Result.success(Tuple2.of(//
											f.apply(x.state().firstElement()).apply(x.value().firstElement()),

											Tuple2.of(x.state().restElements(), x.value().restElements()).getResult()
													.successValue()

									//////////
									).getResult().successValue())));

		}

		private static <A, S> UnsafeLazyList<A> unfoldProvidedResult(final S z,
				final Function<S, Result<Tuple2<A, S>>> f) {
			return f.apply(z).map(x -> cons(() -> x.state(), () -> unfoldProvidedResult(x.value(), f)))
					.getOrElse(UnsafeLazyList.empty());
		}

		private static <A> UnsafeLazyList<Result<A>> infinite(final UnsafeLazyList<A> s) {
			return s.<Result<A>>map(Result::success)
					.append(() -> UnsafeLazyList.<Result<A>>staticMethod().repeat(Result.<A>empty()));
		}

		@Override
		public UnsafeLazyList<A> cons(final A element) {
			return cons(() -> element, () -> this);
		}

	}
}
