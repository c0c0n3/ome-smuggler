package util.runtime;

import java.util.stream.Stream;

/**
 * Convenience {@link CommandBuilder} that produces an empty sequence of tokens.
 */
public class EmptyCommandBuilder implements CommandBuilder {

    @Override
    public Stream<String> tokens() {
        return Stream.empty();
    }

}
