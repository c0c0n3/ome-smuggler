package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class SysPropJvmArgTest {

    @Test (expected = IllegalArgumentException.class)
    public void throwIfNullKey() {
        new SysPropJvmArg(null, "");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void throwIfEmptyKey() {
        new SysPropJvmArg("", "");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void throwIfNullValue() {
        new SysPropJvmArg("", null);
    }
    
    @Test
    public void checkFormat() {
        String actual = new SysPropJvmArg("k", "v ").build();
        assertThat(actual, is("-Dk=v "));
    }
    
}
