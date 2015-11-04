package ome.smuggler.core.data;

import static util.object.Either.left;
import static util.object.Either.right;
import util.object.Either;
import util.object.ObjectParser;

/**
 * A parser for positive integers.
 */
public class PositiveIntParser implements ObjectParser<Integer> {

    @Override
    public Either<String, Integer> parseNonEmpty(String value) {
        try {
            Integer parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                return left("not a positive integer: " + value);
            }
            return right(parsed);
        } catch (NumberFormatException e) {
            return left(e.toString());
        }
    }

}
