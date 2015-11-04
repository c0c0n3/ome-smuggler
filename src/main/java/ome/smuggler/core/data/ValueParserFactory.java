package ome.smuggler.core.data;

import java.util.function.Function;

import util.object.Either;

public class ValueParserFactory {

    private static <T extends PositiveInt>
    Either<String, T> parseId(String value, Function<Integer, T> mapper) {
        return new PositiveIntParser().parse(value).map(mapper);
    }
    
    public static Either<String, DatasetId> datasetId(String value) {
        return parseId(value, DatasetId::new);
    }
    
    public static Either<String, ScreenId> screenId(String value) {
        return parseId(value, ScreenId::new);
    }
    
    public static Either<String, Email> email(String value) {
        return new EmailParser().parse(value).map(Email::new);
    }
    
}
