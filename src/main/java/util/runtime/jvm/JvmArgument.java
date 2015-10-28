package util.runtime.jvm;

import java.util.stream.Stream;

/**
 * A typed JVM argument.
 */
public interface JvmArgument<T> {
    
    /**
     * Sets this argument's payload.
     * @param arg the value to set.
     * @return itself for use in a fluent API style.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the argument is not suitable.
     */
    JvmArgument<T> set(T arg);
    
    /**
     * Builds the sequence of tokens that make up the string representation of 
     * this JVM argument, e.g. (pseudo code) {@code [-cp, my.jar:your.jar]}.
     * @return the list of tokens that make up this argument string.
     * @throws IllegalStateException if the argument hasn't been set yet.
     */
    Stream<String> tokens();  // see note at bottom of file
    
    /**
     * Utility function to quote a string that is part of a token.
     * This is useful for e.g. system properties as each of them would be a
     * single token but key and value parts need to be quoted if spaces are
     * present.
     * @param x the string to quote.
     * @return the quoted string.
     */
    default String quote(String x) {  // see note at bottom of file
        return String.format("\"%s\"", x);
        // or String.format("\\\"%s\\\"", x); ???
    }
    
}
/* NOTE. Quoting & escaping.
 * When and how should token be quoted and what characters should be escaped?
 * Getting this right in general across operating systems is hairy to say the
 * least, see e.g.
 * - http://stackoverflow.com/questions/6306386/how-can-i-escape-an-arbitrary-string-for-use-as-a-command-line-argument-in-bash
 * - http://stackoverflow.com/questions/6427732/how-can-i-escape-an-arbitrary-string-for-use-as-a-command-line-argument-in-windo
 * 
 * For now we feed the tokens into ProcessBuilder.command(...) which seems to
 * quote them so that each token (even if it has spaces in it) becomes an entry
 * in the program's arguments vector---i.e. what is passed to the main method.
 * See e.g. 
 * - http://stackoverflow.com/questions/12124935/processbuilder-adds-extra-quotes-to-command-line
 * - http://stackoverflow.com/questions/18099499/how-to-start-a-process-from-java-with-arguments-which-contain-double-quotes
 * 
 * However, if you dig into the code you see that ProcessBuilder just forwards
 * the tokens to a native fork & exec method which should hopefully handle the
 * escaping as well? Didn't have time to test this but it seems to work for 
 * simple cases which is all we need for now...
 * 
 * In the case of a system property containing spaces, we need to do some extra
 * quoting ourselves though. In fact, consider the token "-Da key = a value ";
 * even though ProcessBuilder will pass it as a single argument to the JVM, the
 * Java lib is not going to parse it the way we intended---i.e. key = "a key ",
 * value = " a value ". This is our fault as both key and value needed to be 
 * quoted as in the example JVM invocation below:
 * 
 *      java -D"a key "=" a value " Main
 *  
 * If Main contains this code:
 * 
 *      String value = System.getProperty("a key ");
 *      System.out.println(">>>" + value + "<<<");
 * 
 * The output would be: 
 * 
 *      >>> a value <<<
 *      
 * Whereas quoting the *whole* token as in
 * 
 *      java "-Da key = a value " Main
 *      
 * would output:
 * 
 *      >>>null<<<
 *      
 * which is correct as there would be no property with a key of "a key ".
 */