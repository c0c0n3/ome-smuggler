package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;
import static util.runtime.jvm.ClassPathFactory.fromLibDir;
import static util.runtime.jvm.JvmCmdFactory.java;
import static util.sequence.Arrayz.asList;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.core.types.ImportInput;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.EmptyProgramArgument;
import util.runtime.ListProgramArgument;
import util.runtime.ProgramArgument;
import util.runtime.jvm.ClassPath;
import util.runtime.jvm.ClassPathJvmArg;
import util.runtime.jvm.JvmCmdBuilder;


public class ImporterCommandBuilder implements CommandBuilder {

    private static ListProgramArgument<String> arg(String...tokens) {
        return new ListProgramArgument<String>(asList(tokens));
    }
    
    private static <T> ListProgramArgument<String> optionalArg(
            String argName, Optional<T> argValue) {
        ListProgramArgument<String> empty = new ListProgramArgument<>(asList()); 
        return argValue.map(Object::toString)
                       .map(n -> arg("-n", n))
                       .orElse(empty);
    }
    
    private final ImportInput importArgs;
    
    public ImporterCommandBuilder(ImportInput importArgs) {
        requireNonNull(importArgs, "importArgs");        
        this.importArgs = importArgs;
    }
    
    private ProgramArgument<String> mainClass() {  
        String fqn = "ome.formats.importer.cli.CommandLineImporter";  // TODO move to config
        return new BaseProgramArgument<>(fqn);  
    }
    
    private ClassPathJvmArg classPath() {  
        Path libDir = Paths.get("ome-lib");  // TODO move to config
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
    
    /*
     * -d DATASET_ID                            OMERO dataset ID to import image into
  -r SCREEN_ID                          OMERO screen ID to import plate into
     */
    
    private CommandBuilder assembleCommand() {
        JvmCmdBuilder cmd = java(classPath(), mainClass());
        
        return null;
    }
    
    @Override
    public Stream<String> tokens() {
        return assembleCommand().tokens();
    }

}
