package util.runtime;

import java.util.stream.Stream;

/**
 * Convenience {@link CommandBuilder} that produces an empty sequence of tokens.
 * <p><em>Note</em>. Command builders form a monoid under the "join token lists"
 * operation; this class represents the monoid's identity.
 * </p>
 */
public class EmptyCommandBuilder implements CommandBuilder {

    /**
     * @return the empty command builder.
     */
    public static CommandBuilder emptyCommandBuilder() {
        return new EmptyCommandBuilder();
    }
    
    @Override
    public Stream<String> tokens() {
        return Stream.empty();
    }

}
