package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Arrayz.hasNulls;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Factory methods to build a command to launch a JVM.
 */
public class JvmCmdBuilder {

    private static Stream<String> build(Stream<Stream<Object>> xs) {
        return xs.flatMap(identity())
                 .map(x -> (JvmArgument<?>) x)
                 .map(JvmArgument::tokens)
                 .flatMap(identity());
    }
    
    public static Stream<String> java(JvmArgument<Path> jrePath,
                                      JarJvmArg appToRun, 
                                      Stream<SysPropJvmArg> props,
                                      JvmArgument<?>...appArgs) {
        requireNonNull(jrePath, "jrePath");
        requireNonNull(appToRun, "appToRun");
        requireNonNull(props, "props");
        if (hasNulls(appArgs)) {  // false if appArgs is null or zero len
            throw new NullPointerException("appArgs has null elements");
        }
        
        Stream<Stream<Object>> xs = Stream.of(Stream.of(jrePath), 
                Stream.of(appToRun), Stream.of(props), asStream(appArgs));
        return build(xs);
    }
    
    public static Stream<String> java(JvmArgument<Path> jrePath,
                                      JarJvmArg appToRun, 
                                      JvmArgument<?>...appArgs) {
        return java(jrePath, appToRun, Stream.empty(), appArgs);
    }
  
}
