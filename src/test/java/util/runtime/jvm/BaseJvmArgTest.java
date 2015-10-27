package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class BaseJvmArgTest {

    @DataPoints
    public static String[] values = array("", "x", " x y");
    
    @Theory
    public void stringSetIsReturnedAsIsByBuild(String arg) {
        target.set(arg);
        assertThat(target.build(), is(arg));
    }
    
    private BaseJvmArg<String> target;
    
    @Before
    public void setup() {
        target = new BaseJvmArg<>();
    }
    
    @Test (expected = NullPointerException.class)
    public void setThrowsIfNullArg() {
        target.set(null);
    }
    
    @Test (expected = IllegalStateException.class)
    public void buildThrowsIfArgNotSetYet() {
        target.build();
    }
    
}
