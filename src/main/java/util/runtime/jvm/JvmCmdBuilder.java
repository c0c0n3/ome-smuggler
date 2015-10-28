package util.runtime.jvm;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

/**
 * Factory methods to build a command to launch a JVM.
 */
public class JvmCmdBuilder {

    public static Stream<String> java(JarJvmArg appToRun, 
                                      Stream<SysPropJvmArg> props,
                                      JvmArgument<?>...appArgs) {
        requireNonNull(appToRun, "appToRun");
        requireNonNull(props, "props");
        
        return null;
    }
    
    public static Stream<String> java(JarJvmArg appToRun, 
                                      JvmArgument<?>...appArgs) {
        return java(appToRun, Stream.empty(), appArgs);
    }
  
}
