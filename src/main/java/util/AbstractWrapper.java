package util;

import java.util.function.Supplier;

/**
 * A {@link Supplier} to wrap a value in such a way that the wrapper behaves 
 * just like the wrapped value in terms of {@link #equals(Object) equality}, 
 * {@link #hashCode() hashing}, and {@link #toString() string conversion}.
 */
public abstract class AbstractWrapper<T> implements Supplier<T> {

    /**
     * Implemented by delegating to the wrapped value except when {@code null},
     * in which case the value of {@code other == null} is returned.
     */
    @Override 
    public boolean equals(Object other) {
        T wrappedValue = get();
        if (wrappedValue != null) {
            return wrappedValue.equals(other);
        }
        return other == null;
    }
    
    /**
     * Implemented by delegating to the wrapped value except when {@code null},
     * in which case {@code 0} is returned.
     */
    @Override 
    public int hashCode() {
        T wrappedValue = get();
        return wrappedValue == null ? 0 : wrappedValue.hashCode();
    }
    
    /**
     * Implemented by delegating to the wrapped value except when {@code null},
     * in which case the string "null" is returned.
     */
    @Override
    public String toString() {
        T wrappedValue = get();
        return String.valueOf(wrappedValue);  // caters for null
    }

}
