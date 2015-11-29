package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PositiveNTest {

    @Test(expected = IllegalArgumentException.class)
    public void failIfNegativeValue() {
        PositiveN.of(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void failIfZero() {
        PositiveN.of(0);
    }
    
    @Test
    public void buildIfPositive() {
        long expected = 1;
        PositiveN actual = PositiveN.of(expected);
        
        assertThat(actual, is(expected));
    }
    
}
