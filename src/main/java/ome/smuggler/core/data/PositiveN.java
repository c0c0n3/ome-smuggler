package ome.smuggler.core.data;

import util.object.Wrapper;

/**
 * A positive natural number, i.e. an integer greater than zero.
 */
public class PositiveN extends Wrapper<Long> {

    protected PositiveN(Long wrappedValue) {
        super(wrappedValue);
    }

}
