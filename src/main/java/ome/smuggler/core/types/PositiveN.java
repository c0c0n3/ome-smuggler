package ome.smuggler.core.types;

import util.object.Wrapper;

/**
 * A positive natural number, i.e. an integer greater than zero.
 * An instance of this object can only be obtained through a parser so that it
 * is impossible to construct an invalid value.
 */
public class PositiveN extends Wrapper<Long> {

    protected PositiveN(Long wrappedValue) {
        super(wrappedValue);
    }

}
