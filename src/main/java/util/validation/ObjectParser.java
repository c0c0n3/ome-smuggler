package util.validation;

import static java.util.Objects.requireNonNull;
import static util.object.Either.right;

import java.util.stream.Stream;

import util.object.Either;

/**
 * Turns a textual representation of a {@code T}-value into an instance of 
 * {@code T}.
 */
public interface ObjectParser<T> {
    
    /**
     * Builds a parser that returns the input stream as is, provided it is not
     * {@code null}, in which case a {@link NullPointerException} is thrown.
     * @return a no-op parser.
     */
    static ObjectParser<Stream<String>> identityParser() { 
        return tokens -> {
            requireNonNull(tokens, "tokens");
            return right(tokens);
        };
    }
    
    /**
     * Parses a textual representation of a {@code T}-value. 
     * @param value the input to parse, may be {@code null} or empty.
     * @return either the parsed value (right) or a parse error message (left).
     * @param tokens the tokens that make up the state of a {@code T}-value,
     * typically {@code T}'s fields. Tokens are allowed to be {@code null} or 
     * empty.
     * @return either the parsed value (right) or a parse error message (left).
     * @throws NullPointerException if the argument is {@code null}.
     */
    Either<String, T> parse(Stream<String> tokens);
    
    /**
     * Combines this parser with a validator.
     * First applies this parser to turn tokens into a {@code T}-value,
     * then the given validator to decide if the parsed value is legit. 
     * If this parser fails, then the process stops there and the parse error
     * is returned; otherwise the parsed value is given to the validator.
     * If validation fails, then the validation error is returned; otherwise
     * the parsed value is returned.
     * @param validator the validation to apply to the parsed value.
     * @return a parser that uses this object to do the parsing and then applies
     * the given validator to the parsed result.
     */
    default ObjectParser<T> withValidation(Validator<String, T> validator) {
        return new ValidatingObjectParser<>(this, validator);
    }
    
}
/* NOTE. A poor man's interface for parsing.
 * Moderately useful for validation, but seriously hampered by the lack of 
 * composability. Use it if it fits your needs but be aware that there are 
 * way better options out there, most notably parser combinator libraries.
 */  
