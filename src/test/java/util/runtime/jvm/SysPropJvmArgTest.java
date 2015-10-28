package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

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
        String key = "a key ", value = " a value ";
        char qq = '"';
        String expected = new StringBuffer().append("-D")
                         .append(qq).append(key).append(qq)
                         .append('=')
                         .append(qq).append(value).append(qq)
                         .toString();
                
        String[] actual = new SysPropJvmArg(key, value)
                         .tokens()
                         .toArray(String[]::new);

        assertThat(actual.length, is(1));
        assertThat(actual[0], is(expected));
    }
    
    private static String[] tokensArray(Stream<JvmArgument<?>> args) {
        return args.map(JvmArgument::tokens)
                   .flatMap(x -> x)
                   .toArray(String[]::new);
    }
    
    private static String[] tokensArray(JvmArgument<?>...args) {
        return tokensArray(Stream.of(args));
    }
    
    @Test
    public void toJvmArgumentsFiltersOutBadProps() {
        String validKey1 = " ", v1 = "";  // NB valid prop according to how JVM parses it
        String validKey2 = "k", v2 = "v";
        Map<String, String> props = new LinkedHashMap<>();  // keeps insertion order
        props.put(null, null);
        props.put("", null); 
        props.put(validKey1, v1);    
        props.put("x", null);  
        props.put(validKey2, v2);
        
        String[] expected = tokensArray(new SysPropJvmArg(validKey1, v1), 
                                        new SysPropJvmArg(validKey2, v2));
                                  
        String[] actual = tokensArray(SysPropJvmArg.toJvmArguments(props));

        assertArrayEquals(expected, actual);
    }
    
}
