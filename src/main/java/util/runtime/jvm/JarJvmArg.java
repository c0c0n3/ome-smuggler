package util.runtime.jvm;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Jar argument to pass to a JVM, e.g. {@code -jar app.jar}.
 */
public class JarJvmArg extends BaseJvmArg<Path> {

    @Override
    protected Stream<String> tokenize(Path arg) {
        return Stream.of("-jar", arg.toString());
    }
    
}
