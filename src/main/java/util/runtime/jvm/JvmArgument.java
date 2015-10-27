package util.runtime.jvm;

/**
 * A typed JVM argument.
 */
public interface JvmArgument<T> {
    
    /**
     * Sets this argument's payload.
     * @param arg the value to set.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the argument is not suitable.
     */
    void set(T arg);
    
    /**
     * @return a string representation of this argument as required for a JVM
     * invocation.
     * @throws IllegalStateException if the argument hasn't been set yet.
     */
    String build();
    
}
