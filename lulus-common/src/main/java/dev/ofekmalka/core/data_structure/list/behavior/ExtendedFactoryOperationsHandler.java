package dev.ofekmalka.core.data_structure.list.behavior;

import java.util.Collection;
import java.util.stream.Stream;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.assertion.If.Condition;
import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;
import dev.ofekmalka.core.function.Function;
import dev.ofekmalka.core.function.Supplier;
import dev.ofekmalka.tools.tuple.Tuple2;

public sealed interface ExtendedFactoryOperationsHandler

permits

ExtendedFactoryOperationsHandler.PrimitiveListFactory, //
ExtendedFactoryOperationsHandler.CollectionFactory, //
ExtendedFactoryOperationsHandler.RecursiveFactory, //
ExtendedFactoryOperationsHandler.MathFactory, //
ExtendedFactoryOperationsHandler.TupleFactory {

	public non-sealed interface PrimitiveListFactory extends ExtendedFactoryOperationsHandler {
		List<Boolean> createBooleanList(boolean... booleanValues);

		List<Byte> createByteList(byte... byteValues);

		List<Integer> createIntList(int... intValues);

		List<Character> createCharList(char... charValues);

		List<Short> createShortList(short... shortValues);

		List<Float> createFloatList(float... floatValues);

		List<Double> createDoubleList(double... doublesValues);

		List<Long> createLongList(long... longsValues);
	}

	public non-sealed interface CollectionFactory extends ExtendedFactoryOperationsHandler {
		<A> List<A> createFromCollection(Collection<A> collection);

		<A> List<A> createFromStreamAndRemoveNullsValues(Stream<A> stream);

		<A> List<A> createFilledList(int numberOfElements, Supplier<A> elementSupplier);
	}

	public non-sealed interface RecursiveFactory extends ExtendedFactoryOperationsHandler {
		<A> List<A> iterateWithSeed(A seed, Function<A, A> transformationFunction, int iterations);

		<A, S> List<A> unfoldFromSeed(S initialSeed, Function<If<S>, Condition<S>> generatorFunction,
				Function<S, Tuple2<A, S>> successOutcome);
	}

	public non-sealed interface MathFactory extends ExtendedFactoryOperationsHandler {
		List<Integer> generateRange(int startInclusive, int endExclusive);

	}

	public non-sealed interface TupleFactory extends ExtendedFactoryOperationsHandler {
		<A1, A2> Result<Tuple2<List<A1>, List<A2>>> unzipListOfTuples(List<Tuple2<A1, A2>> tupleList);
	}

}
