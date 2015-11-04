package util.object;

import static util.object.Either.left;
import static util.string.Strings.isNullOrEmpty;

/**
 * Turns a textual representation of a {@code T}-value into an instance of 
 * {@code T}.
 */
public interface ValueParser<T> {

    /**
     * Parses a textual representation of a {@code T}-value. 
     * @param value the input to parse; it is assumed to be a string of length
     * at least 1.
     * @return either the parsed value (right) or a parse error message (left).
     */
    Either<String, T> parseNonEmpty(String value);
    
    /**
     * Parses a textual representation of a {@code T}-value. 
     * @param value the input to parse, may be {@code null} or empty.
     * @return either the parsed value (right) or a parse error message (left).
     */
    default Either<String, T> parse(String value) {
        if (isNullOrEmpty(value)) {
            return left("no input");
        }
        return parseNonEmpty(value);
    }
    
}
/* NOTE. A poor man interface for parsing.
 * Moderately useful for validation, but seriously hampered by the lack of 
 * composability. Use it if it fits your needs but be aware that there are 
 * way better options out there, most notably parser combinator libraries.
 */  
