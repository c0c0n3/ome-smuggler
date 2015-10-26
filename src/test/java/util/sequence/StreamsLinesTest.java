package util.sequence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.lines;

import org.junit.Test;

public class StreamsLinesTest {
    
    private static String[] breakLines(String text) {
        return lines(text).toArray(String[]::new);
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullText() {
        lines(null);
    }
    
    @Test
    public void emptyStreamIfEmptyText() {
        String[] actual = breakLines("");
        assertThat(actual.length, is(0));
    }
    
    @Test
    public void sameStringIfTextHasNoNewLine() {
        String[] actual = lines("abc").toArray(String[]::new);
        String[] expected = array("abc");
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void twoLinesWhenOnlyOneNewLineInBetweenWords() {
        String[] actual = lines("w1 \n w2").toArray(String[]::new);
        String[] expected = array("w1 ", " w2");
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void multipleLines() {
        String[] actual = lines("w1 \n w2\n").toArray(String[]::new);
        String[] expected = array("w1 ", " w2");
        
        assertArrayEquals(expected, actual);
    }
    
}
