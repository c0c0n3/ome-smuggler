package util.lambda;

import java.util.function.Function;

/**
 * Lambda utilities.
 */
public class Functions {

    /**
     * The constant function {@code X → K: x ↦ c }.
     * It discards its input and always returns the given constant {@code c}.
     * @param c the constant value to return.
     * @return the constant function {@code x ↦ c }.
     */
    public static <X, K> Function<X, K> constant(K c) {
        return x -> c;
    }
    
}
