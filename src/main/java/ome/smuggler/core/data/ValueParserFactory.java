package ome.smuggler.core.data;

import static util.validation.SingleTokenParserAdapter.string;
import static util.validation.ValidationOutcome.error;
import static util.validation.ValidationOutcome.success;

import java.util.function.Function;
import java.util.stream.Stream;

import util.object.Either;
import util.validation.SingleTokenParserAdapter;

public class ValueParserFactory {

    private static <T extends PositiveInt>
    Either<String, T> parseId(String value, Function<Integer, T> mapper) {
        return parsePositiveInt(value).map(mapper);
    }
    
    public static Either<String, DatasetId> datasetId(String value) {
        return parseId(value, DatasetId::new);
    }
    
    public static Either<String, ScreenId> screenId(String value) {
        return parseId(value, ScreenId::new);
    }
    
    public static Either<String, Email> email(String value) {
        return string()
              .withValidation(Email.validator())
              .parse(Stream.of(value))
              .map(Email::new);
    }
    
    public static Either<String, Integer> parsePositiveInt(String value) {
        return new SingleTokenParserAdapter<>(Integer::parseInt)
                    .withValidation(parsed -> 
                        parsed > 0 ? success() 
                                   : error("not a positive integer: " + parsed)
                        )
                    .parse(Stream.of(value));
    }
    
    public static Either<String, PositiveInt> positiveInt(String value) {
        return parsePositiveInt(value).map(PositiveInt::new);
    }
        
}
