package util.object;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

/**
 * Common functionality to manipulate {@link Either} values.
 */
public class Eithers {

    /**
     * Collects all the left values in encounter order.
     * Any {@code null} or right value is filtered out.
     * @param xs the values to collect.
     * @return all the left values in the input stream.
     */
    public static <L> Stream<L> collectLeft(Stream<Either<L, ?>> xs) {
        requireNonNull(xs, "xs");
        return xs.filter(x -> x != null && x.isLeft())
                 .map(Either::getLeft);
    }
    
    /**
     * Collects all the right values in encounter order.
     * Any {@code null} or left value is filtered out.
     * @param xs the values to collect.
     * @return all the right values in the input stream.
     */
    public static <R> Stream<R> collectRight(Stream<Either<?, R>> xs) {
        requireNonNull(xs, "xs");
        return xs.filter(x -> x != null && x.isRight())
                 .map(Either::getRight);
    }
    
}
