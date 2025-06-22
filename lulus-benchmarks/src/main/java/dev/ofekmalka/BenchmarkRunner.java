package dev.ofekmalka;

//import java.io.File;
//import java.util.concurrent.TimeUnit;
//
//import org.openjdk.jmh.annotations.Mode;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.RunnerException;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//import org.openjdk.jmh.runner.options.TimeValue;
//
//import dev.ofekmalka.data_structure.list.ListAggregationFilteringTransformationAppendingBenchmark;
//
//public class BenchmarkRunner {
//
//	public static void main(final String[] args) throws RunnerException {
//		final var directory = "target/data_structure/list/benchmark-results/operations/";
//		final var fileName = "aggregation-filtering-transformation-appending";
//
//		final var fileType = "-quick.txt";//
//
//		new File(directory).mkdirs();//
//
//		final var opt = new OptionsBuilder()//
//				.include(ListAggregationFilteringTransformationAppendingBenchmark.class.getName())//
//				.forks(3)
//				// Good balance for GC & JIT variability
//				.jvmArgsAppend("-Xms2G", "-Xmx2G") // Reduce GC pressure variability
//				.threads(1) // Single-threaded for pure FP benchmarks
//				.warmupIterations(10) // More warmup = better JIT optimization for recursive methods
//				.measurementIterations(10) // Better sampling stability
//				.warmupTime(TimeValue.seconds(1)) // Optional: can add explicit timing
//				.measurementTime(TimeValue.seconds(1))//
//				.mode(Mode.AverageTime) // Still good for FP style
//				.timeUnit(TimeUnit.MILLISECONDS) // Switch to MICROSECONDS or NANOSECONDS for very small ops
//				.output(directory + fileName + fileType)//
//
//				.build();
//
//		new Runner(opt).run();
//
//	}
//}
