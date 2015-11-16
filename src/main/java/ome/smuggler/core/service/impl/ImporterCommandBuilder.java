package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;
import static util.runtime.jvm.ClassPathFactory.fromLibDir;
import static util.runtime.jvm.JvmCmdFactory.java;
import static util.sequence.Arrayz.asList;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.core.types.ImportInput;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;
import util.runtime.ProgramArgument;
import util.runtime.jvm.ClassPath;
import util.runtime.jvm.ClassPathJvmArg;


public class ImporterCommandBuilder implements CommandBuilder {

    private static ListProgramArgument<String> arg(String...tokens) {
        return new ListProgramArgument<String>(asList(tokens));
    }
    
    private static <T> ListProgramArgument<String> optionalArg(
            String argName, Optional<T> argValue) {
        return argValue.map(Object::toString)
                       .map(n -> arg(argName, n))
                       .orElse(arg());
    }
    
    private final ImportInput importArgs;
    private final CliImporterConfig config;
    
    public ImporterCommandBuilder(CliImporterConfig config,
                                  ImportInput importArgs) {
        requireNonNull(config, "config");
        requireNonNull(importArgs, "importArgs");
        
        this.config = config;
        this.importArgs = importArgs;
    }
    
    private ProgramArgument<String> mainClass() {  
        return new BaseProgramArgument<>(config.getMainClassFqn());  
    }
    
    private ClassPathJvmArg classPath() {  
        Path libDir = Paths.get(config.getOmeLibDirPath());
        ClassPath cp = unchecked(() -> fromLibDir(libDir)).get();
        return new ClassPathJvmArg(cp);
    }
    
    private ListProgramArgument<String> server() {
        URI omero = importArgs.getOmeroHost();
        return arg("-s", omero.getHost(), 
                   "-p", String.valueOf(omero.getPort()), 
                   "-k", importArgs.getSessionKey());
    }
    
    private ListProgramArgument<String> name() {
        return optionalArg("-n", importArgs.getName());
                         
    }
    
    private ListProgramArgument<String> description() {
        return optionalArg("-x", importArgs.getDescription());                     
    }
    
    private ListProgramArgument<String> datasetId() {
        return importArgs.hasDatasetId() ?
                optionalArg("-d", importArgs.getDatasetOrScreenId()) :
                arg();                     
    }
    
    private ListProgramArgument<String> screenId() {
        return importArgs.hasScreenId() ?
                optionalArg("-r", importArgs.getDatasetOrScreenId()) :
                arg();                     
    }
    
    private CommandBuilder[] textAnnotations() {
        return importArgs.getTextAnnotations()
                         .map(a -> arg("--annotation-ns", a.namespace(), 
                                       "--annotation-text", a.text()))
                         .toArray(CommandBuilder[]::new);
    }
    
    private CommandBuilder[] annotationIds() {
        return importArgs.getAnnotationIds()
                         .map(id -> arg("--annotation-link", id.toString()))
                         .toArray(CommandBuilder[]::new);
    }
    
    private CommandBuilder assembleCommand() {
        return java(classPath(), mainClass())
               .addApplicationArgument(server())
               .addApplicationArgument(name())
               .addApplicationArgument(description())
               .addApplicationArgument(datasetId())
               .addApplicationArgument(screenId())
               .addApplicationArgument(textAnnotations())
               .addApplicationArgument(annotationIds());
    }
    
    @Override
    public Stream<String> tokens() {
        return assembleCommand().tokens();
    }

}
