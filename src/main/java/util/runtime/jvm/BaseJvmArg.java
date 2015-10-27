package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static util.object.Either.right;

import java.util.Optional;

import util.object.Either;

/**
 * Base class to enforce the set/build protocol required by the {@link 
 * JvmArgument} interface.
 */
public class BaseJvmArg<T> implements JvmArgument<T> {

    private Optional<T> maybeArg;
    
    protected BaseJvmArg() {
        maybeArg = Optional.empty();
    }

    @Override
    public void set(T arg) {
        requireNonNull(arg);
        maybeArg = validate(arg).either(this::throwIfNotValid, Optional::of); 
    }
    
    private Optional<T> throwIfNotValid(String errorMessage) {
        throw new IllegalArgumentException(errorMessage);
    }
    
    /**
     * Override this method to do additional validation of the argument to set.
     * @param argToSet the value to validate.
     * @return either an error message if validation fails or the argument 
     * itself if validation succeeds.
     */
    protected Either<String, T> validate(T argToSet) {
        return right(argToSet);
    }

    @Override
    public String build() {
        if (!maybeArg.isPresent()) {
            throw new IllegalStateException("argument not set yet");
        }
        return toString(maybeArg.get());
    }
    
    /**
     * Override this method to convert the argument into a JVM string argument
     * if the default of calling the argument's {@code toString} method and
     * escaping spaces is not suitable.
     * @param arg the argument to convert to string.
     * @return the string representation of the argument as required by the
     * {@link #build() build} method.
     */
    protected String toString(T arg) {
        return escape(arg.toString());
    }
    
    protected String escape(String convertedArg) {
        return convertedArg.replace(' ', '\u0020');
        // or
        // String.format("\\\"%s\\\"", convertedArg); ???
    }
    /* TODO figure out what is the *right* thing to do here, taking into account
     * that the stringified args will have to be digested by ProcessBuilder.
     * See:
     * - http://stackoverflow.com/questions/12124935/processbuilder-adds-extra-quotes-to-command-line
     * - http://stackoverflow.com/questions/18099499/how-to-start-a-process-from-java-with-arguments-which-contain-double-quotes 
     * - http://stackoverflow.com/questions/2108103/can-the-key-in-a-java-property-include-a-blank-character
     * 
     * NB escaping spaces means we don't need to quote, which avoids trouble 
     * with ProcessBuilder; but then what should we do with other whitespace
     * chars, e.g. tabs, new lines? And double quotes themselves?! Ouch!
     */
}
