package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static util.sequence.Arrayz.asList;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Arrayz.hasNulls;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.ProgramArgument;
import util.sequence.StreamMonoid;

/**
 * Factory methods to build a command to launch a JVM.
 */
public class JvmCmdBuilder implements CommandBuilder {

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

    public static Stream<String> java(JarJvmArg appToRun, 
                                      Stream<SysPropJvmArg> props,
                                      ProgramArgument<?>...appArgs) {
        return java(thisJre(), appToRun, props, appArgs);
    }
    
    public static ProgramArgument<Path> thisJre() {
        return JvmLocator.findCurrentJvmExecutable()
                         .map(BaseProgramArgument<Path>::new)
                         .get();
    }


    private List<SysPropJvmArg> sysProps;
    private List<ProgramArgument<?>> appArgs;
    
    private JvmCmdBuilder() {
        sysProps = new ArrayList<>();
        appArgs = new ArrayList<>();
    }
    
    public JvmCmdBuilder addProp(SysPropJvmArg...ps) {
        if (ps == null || hasNulls(ps)) {  // hasNull is false if ps is null or zero len
            throw new NullPointerException("null elements");
        }
        sysProps.addAll(asList(ps));
        return this;
    }
    
    public <K, V> JvmCmdBuilder addProps(Map<K, V> props) {
        Stream<SysPropJvmArg> ps = SysPropJvmArg.toJvmArguments(props);
        return addProp(ps.toArray(SysPropJvmArg[]::new));
    }
    
    public JvmCmdBuilder addCurrentSysProps() {
        return addProps(System.getProperties());
    }
    
    public JvmCmdBuilder addApplicationArgument(ProgramArgument<?>...ps) {
        if (ps == null || hasNulls(ps)) {  // hasNull is false if ps is null or zero len
            throw new NullPointerException("null elements");
        }
        appArgs.addAll(asList(ps));
        return this;
    }
    
    @Override
    public Stream<String> tokens() {
        StreamMonoid<CommandBuilder> builderMonoid = new StreamMonoid<>();
        return null;
    }
    
}
