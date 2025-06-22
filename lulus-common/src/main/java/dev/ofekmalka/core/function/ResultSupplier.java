package dev.ofekmalka.core.function;

@FunctionalInterface
public interface ResultSupplier<ResultType> {
    /**
     * Supplies a result, or throws an exception if unable to do so.
     *
     * @return supplied result
     * @throws Exception if unable to supply a result
     */
    ResultType supply() throws Exception;
}
