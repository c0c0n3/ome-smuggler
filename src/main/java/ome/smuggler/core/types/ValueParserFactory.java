package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.validation.ParserFactory.pairParser;
import static util.validation.ParserFactory.positiveIntParser;
import static util.validation.ParserFactory.positiveLongParser;
import static util.validation.ParserFactory.stringParser;
import static util.validation.ParserFactory.uriParser;

import java.net.URI;
import java.util.function.Function;

import util.object.Either;

public class ValueParserFactory {

    private static <T extends PositiveN>
    Either<String, T> parsePosInt(String value, Function<Long, T> mapper) {
        return positiveLongParser().parse(value).map(mapper);
    }
    
    public static Either<String, DatasetId> datasetId(String value) {
        return parsePosInt(value, DatasetId::new);
    }
    
    public static Either<String, ScreenId> screenId(String value) {
        return parsePosInt(value, ScreenId::new);
    }
    
    public static Either<String, Email> email(String value) {
        return stringParser()
              .withValidation(Email.validator())
              .parse(value)
              .map(Email::new);
    }
    
    public static Either<String, PositiveN> positiveInt(String value) {
        return parsePosInt(value, PositiveN::new);
    }
    
    public static Either<String, TextAnnotation> textAnnotation(String...xs) {
        return pairParser(stringParser(), stringParser())
              .parse(xs == null ? new String[0] : xs)
              .map(TextAnnotation::new);
    }
    
    public static Either<String, URI> uri(String value) {
        return uriParser().parse(value);
    }
    
    public static Either<String, URI> uri(String host, String port) {
        return pairParser(stringParser(), positiveIntParser())
              .parse(host, port)
              .map(p -> String.format("%s:%s", p.fst(), p.snd()))
              .bind(s -> uri(s));
    }
    
    public static Either<String, String> string(String value) {
        return stringParser().parse(value);
    }
    
    public static <T> Either<String, T> label(String errorMsgPrefix, 
                                              Either<String, T> value) {
        requireNonNull(errorMsgPrefix, "errorMsgPrefix");
        requireNonNull(value, "value");
        
        return value.mapLeft(e -> String.format("%s: %s", errorMsgPrefix, e));
    }

}
