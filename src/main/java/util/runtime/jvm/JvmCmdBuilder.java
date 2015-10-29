package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Arrayz.hasNulls;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import util.runtime.ProgramArgument;

/**
 * Factory methods to build a command to launch a JVM.
 */
public class JvmCmdBuilder {

    private static Stream<String> build(Stream<Stream<Object>> xs) {
        return xs.flatMap(identity())
                 .map(x -> (ProgramArgument<?>) x)
                 .map(ProgramArgument::tokens)
                 .flatMap(identity());
    }
    
    public static Stream<String> java(ProgramArgument<Path> jrePath,
                                      JarJvmArg appToRun, 
                                      Stream<SysPropJvmArg> props,
                                      ProgramArgument<?>...appArgs) {
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
    
    public static Stream<String> java(ProgramArgument<Path> jrePath,
                                      JarJvmArg appToRun, 
                                      ProgramArgument<?>...appArgs) {
        return java(jrePath, appToRun, Stream.empty(), appArgs);
    }

    public static Stream<String> thisJava(JarJvmArg appToRun, 
                                          Stream<SysPropJvmArg> props,
                                          ProgramArgument<?>...appArgs) {
        Optional<JarJvmArg> thisJre = JvmLocator
                                     .findCurrentJvmExecutable()
                                     .map(path -> { 
                                         JarJvmArg arg = new JarJvmArg();
                                         arg.set(path);
                                         return arg;
                                     });
        return java(thisJre.get(), appToRun, props, appArgs);
    }
    
}
