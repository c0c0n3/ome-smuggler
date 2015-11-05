package util.object;

import static java.util.function.Function.identity;
import static java.util.Objects.requireNonNull;
import static util.object.Either.right;
import static util.object.Either.left;
import static util.string.Strings.isNullOrEmpty;

import java.util.function.Function;
import java.util.stream.Stream;

import util.lambda.FunctionE;

/**
 * Makes a function {@code String → T} works as an {@link ObjectParser}.  
 */
public class SingleTokenParserAdapter<T> implements ObjectParser<T> {

    /**
     * @return a parser that accepts a string of length at least one and rejects
     * {@code null} and empty strings.
     */
    public static SingleTokenParserAdapter<String> string() {
        return new SingleTokenParserAdapter<>(identity());
    }
    
    
    private final Function<String, T> parser;
    
    /**
     * Creates a new instance.
     * @param parser turns strings into {@code T}-values.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public SingleTokenParserAdapter(FunctionE<String, T> parser) {
        requireNonNull(parser, "parser");
        this.parser = parser;
    }
    
    /**
     * Creates a new instance.
     * @param parser turns strings into {@code T}-values.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public SingleTokenParserAdapter(Function<String, T> parser) {
        requireNonNull(parser, "parser");
        this.parser = parser;
    }
    
    @Override
    public Either<String, T> parse(Stream<String> tokens) {
        requireNonNull(tokens, "tokens");
        String value = tokens.findFirst().orElse(null);
        return parse(value);
    }

    /**
     * Parses a textual representation of a {@code T}-value. 
     * @param value the input to parse, may be {@code null} or empty.
     * @return either the parsed value (right) or a parse error message (left).
     */
    public Either<String, T> parse(String value) {
        if (isNullOrEmpty(value)) {
            return left("no input");
        }
        try {
            T parsed = parser.apply(value);
            return right(parsed);
        } catch (Exception e) {
            return left(e.getMessage());
        }
    }
    
}
