package dev.ofekmalka.tools.stream;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Predicate;
import dev.ofekmalka.tools.helper.ValidatedSize;
import dev.ofekmalka.tools.tuple.Tuple2;

public interface StreamBehavior {
	public interface Operations<A> {

		Result<Stream<A>> getStreamResult();

		// Filtering Operations
		interface Filtering<A> extends Operations<A> {
			Stream<A> filter(Predicate<A> predicate);

			Stream<A> dropWhile(Predicate<A> predicate);

			Stream<A> takeWhile(Predicate<A> predicate);
		}

		// Mapping Operations
		interface Mapping<A> extends Operations<A> {
			<B> Stream<B> map(Function<A, B> elementTransformer);

			<B> Stream<B> flatMap(Function<A, Stream<B>> elementTransformer);

		}

		// Combining Operations
		interface Combining<A> extends Operations<A> {
			Stream<A> append(Stream<A> joinedStream);

			<B> Stream<Tuple2<A, B>> zipAsPossible(final Stream<B> joinedStream);
		}

		// Windowing Operations
		interface Windowing<A> extends Operations<A> {
			Stream<List<A>> windowFixed(ValidatedSize windowSize);

			Stream<List<A>> windowSliding(ValidatedSize windowSize);

			Stream<List<A>> windowFixedAtMost(ValidatedSize windowSize);
		}

		// Other Transformations
		interface OtherTransformations<A> extends Operations<A> {
			Stream<A> setFirstElement(A firstElement);

			Stream<A> cons(A element);

			Stream<A> takeAtMost(ValidatedSize n);

			Stream<A> dropAtMost(ValidatedSize n);

			List<A> toBoundedList(ValidatedSize size);
		}

	}
}
