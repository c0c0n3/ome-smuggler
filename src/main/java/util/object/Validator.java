package util.object;

/**
 * A function that validates {@code T}-values.
 */
public interface Validator<T> {
    
    /**
     * Tells if the given value is a valid one.
     * @param value the value to test.
     * @return {@link ValidationOutcome#success() success} if validation passed,
     * {@link ValidationOutcome#error(String) error} if it failed.
     */
    ValidationOutcome validate(T value);
    
}
