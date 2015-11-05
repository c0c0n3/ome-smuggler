package util.validation;

import static java.util.Objects.requireNonNull;
import static util.object.Either.left;
import static util.object.Either.right;

import java.util.stream.Stream;

import util.object.Either;

/**
 * Combines parsing and validation of {@code T}-values.
 */
public class ValidatingObjectParser<T> implements ObjectParser<T> {

    private final ObjectParser<T> parser;
    private final Validator<T> validator;
    
    /**
     * Creates a new instance.
     * @param parser the parser to turn tokens into a {@code T}-value.
     * @param validator decides if the parsed {@code T}-value is legit.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ValidatingObjectParser(ObjectParser<T> parser, 
                                  Validator<T> validator) {
        requireNonNull(parser, "parser");
        requireNonNull(validator, "validator");
        
        this.parser = parser;
        this.validator = validator;
    }

    private Either<String, T> doValidation(T parsed) {
        ValidationOutcome outcome = validator.validate(parsed);
        if (outcome == null || outcome.succeeded()) {
            return right(parsed);
        }
        return left(outcome.toString());
    }
    
    /**
     * First applies the given parser to turn tokens into a {@code T}-value,
     * then the given validator to decide if the parsed value is legit. 
     * If the parser fails, then the process stops there and the parse error
     * is returned; otherwise the parsed value is given to the validator.
     * If validation fails, then the validation error is returned; otherwise
     * the parsed value is returned.
     */
    @Override
    public Either<String, T> parse(Stream<String> tokens) {
        return parser.parse(tokens).bind(this::doValidation);
    }
    
}
/* NOTE. Did I mention this approach to parsing is lame because parsers don't 
 * compose?
 * Well, now we need to make a special case for what is not really in principle
 * a different concept: 
 * 
 *  + parsing = transforming some input into a result value or an error
 *  + validation = transforming an input into itself or an error
 *  
 * Surely these guys must be siblings? (In fact they belong to the same family
 * of functions = you can come up with a polymorphic function to represent the
 * whole family.)  
 * I'm kicking myself. Nuff said. 
 */
