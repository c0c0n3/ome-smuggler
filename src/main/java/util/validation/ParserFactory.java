package util.validation;

import static java.util.function.Function.identity;
import static util.object.Either.left;
import static util.object.Either.right;

import java.util.stream.Stream;

import util.object.Either;

/**
 * Common parsers.
 */
public class ParserFactory {

    /**
     * Builds a parser that returns the input stream as is, provided it is not
     * {@code null}, in which case a {@link NullPointerException} is thrown.
     * @return a no-op parser.
     */
    static ObjectParser<Stream<String>> identityParser() { 
        return Either::right;
    }

    /**
     * Builds a parser that accepts a string of length at least one and rejects
     * {@code null} and empty strings.
     * @return the parser.
     */
    public static ObjectParser<String> stringParser() {
        return new SingleTokenParserAdapter<>(identity());
    }
    
    /**
     * Builds a parser that accepts integers.
     * @return the parser.
     */
    public static ObjectParser<Integer> intParser() {
        return new SingleTokenParserAdapter<>(Integer::parseInt);
    }
    
    /**
     * Builds a parser that only accepts positive integers.
     * @return the parser.
     */
    public static ObjectParser<Integer> positiveIntParser() {
        return intParser()
              .withValidation(parsed -> 
                  parsed > 0 ? right(parsed) 
                             : left("not a positive integer: " + parsed)
              );
    }
    
}
