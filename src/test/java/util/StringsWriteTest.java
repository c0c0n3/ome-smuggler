package util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.Arrayz.array;
import static util.Strings.write;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class StringsWriteTest {

    @DataPoints
    public static String[] contents = array("", "a", "ab");
    
    @Theory
    public void consumerControlsWhatWritten(String s, String t) {
        String actual = write(out -> {
            out.print(s);
            out.print(t);
        });
        
        assertThat(actual, is(s + t));
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullWriter() {
        write(null);
    }
    
}
