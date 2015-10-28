package util.runtime.jvm;

import java.util.stream.Stream;

/**
 * Class path argument to pass to a JVM, e.g. {@code -cp my.jar:yr.jar}.
 */
public class ClassPathJvmArg extends BaseJvmArg<ClassPath> {

    @Override
    protected Stream<String> tokenize(ClassPath arg) {
        return Stream.of("-cp", arg.toString());
    }
    
}
