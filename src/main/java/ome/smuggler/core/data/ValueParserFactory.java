package ome.smuggler.core.data;

import static util.validation.ParserFactory.positiveIntParser;
import static util.validation.ParserFactory.stringParser;

import java.util.function.Function;

import util.object.Either;

public class ValueParserFactory {

    private static <T extends PositiveInt>
    Either<String, T> parsePosInt(String value, Function<Integer, T> mapper) {
        return positiveIntParser().parse(value).map(mapper);
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
    
    public static Either<String, PositiveInt> positiveInt(String value) {
        return parsePosInt(value, PositiveInt::new);
    }

}
