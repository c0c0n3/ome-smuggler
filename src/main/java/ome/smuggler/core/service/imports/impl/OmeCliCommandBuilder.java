package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static util.error.Exceptions.unchecked;
import static util.runtime.jvm.ClassPathFactory.fromLibDir;
import static util.runtime.jvm.JvmCmdFactory.java;
import static util.sequence.Arrayz.asList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.config.items.OmeCliConfig;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;
import util.runtime.ProgramArgument;
import util.runtime.jvm.ClassPath;
import util.runtime.jvm.ClassPathJvmArg;
import util.runtime.jvm.JvmCmdBuilder;

/**
 * Template class for OMERO CLI command builders.
 */
public abstract class OmeCliCommandBuilder implements CommandBuilder {

    protected static ListProgramArgument<String> arg(String...tokens) {
        return new ListProgramArgument<String>(asList(tokens));
    }
    
    protected static <T> ListProgramArgument<String> optionalArg(
            String argName, Optional<T> argValue) {
        return argValue.map(Object::toString)
                       .map(n -> arg(argName, n))
                       .orElse(arg());
    }
    
    
    protected final OmeCliConfig config;
    
    protected OmeCliCommandBuilder(OmeCliConfig config) {
        requireNonNull(config, "config");
        
        this.config = config;
    }
    
    protected abstract String getMainClassFqn();
    
    protected abstract JvmCmdBuilder assembleArguments(JvmCmdBuilder bareCommand);
    
    protected ProgramArgument<String> mainClass() {  
        return new BaseProgramArgument<>(getMainClassFqn());  
    }
    
    protected ClassPathJvmArg classPath() {  
        Path libDir = Paths.get(config.getOmeLibDirPath());
        ClassPath cp = unchecked(() -> fromLibDir(libDir)).get();
        return new ClassPathJvmArg(cp);
    }
    
    protected CommandBuilder assembleCommand() {
        JvmCmdBuilder bareCommand = java(classPath(), mainClass());
        return assembleArguments(bareCommand);
    }
    
    @Override
    public Stream<String> tokens() { 
        return assembleCommand().tokens();
    }
    
    @Override
    public String toString() {
        return tokens().collect(joining(" "));
    }

}
