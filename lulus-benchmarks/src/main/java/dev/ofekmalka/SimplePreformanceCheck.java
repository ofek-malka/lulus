package dev.ofekmalka;
//
//import org.openjdk.jmh.annotations.Scope;
//import org.openjdk.jmh.annotations.State;
//
//import dev.ofekmalka.core.data_structure.list.List;
//
//@State(Scope.Thread)
//public class SimplePreformanceCheck {
//
//	public static void main(final String[] args) {
//		List<Integer> tree = List.emptyList();
//		var i = 0;
//
//		final var startTime = System.nanoTime(); // Start timer
//
//		while (i < 100000) {
//			tree = tree.addElement(i);
//			i += 1;
//		}
//
//		final var endTime = System.nanoTime(); // End timer
//
//		final var duration = endTime - startTime;
//		final var milliseconds = duration / 1_000_000.0;
//
//		System.out.printf("Insertion took %.3f milliseconds%n", milliseconds);
//
//	}
//}
