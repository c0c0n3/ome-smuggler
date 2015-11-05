package ome.smuggler.core.data;

import static util.validation.ParserFactory.pairParser;
import static util.validation.ParserFactory.positiveLongParser;
import static util.validation.ParserFactory.stringParser;

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
              .parse(xs)
              .map(TextAnnotation::new);
    }

}
