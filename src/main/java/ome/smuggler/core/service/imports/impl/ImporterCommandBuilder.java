package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
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

import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.core.types.ImportInput;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;
import util.runtime.ProgramArgument;
import util.runtime.jvm.ClassPath;
import util.runtime.jvm.ClassPathJvmArg;

/**
 * Build the command line to call the OMERO importer.
 */
public class ImporterCommandBuilder implements CommandBuilder {

    public static final String SessionKeySwitch = "-k";
    public static final String MaskedSessionKey = "***";
    
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
    private final OmeCliConfig config;
    
    /**
     * Creates a new instance to build a command line from the given data.
     * @param config configuration applicable to all import runs.
     * @param importArgs details what to import.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImporterCommandBuilder(OmeCliConfig config,
                                  ImportInput importArgs) {
        requireNonNull(config, "config");
        requireNonNull(importArgs, "importArgs");
        
        this.config = config;
        this.importArgs = importArgs;
    }
    
    private ProgramArgument<String> mainClass() {  
        return new BaseProgramArgument<>(config.getImporterMainClassFqn());  
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
                   SessionKeySwitch, importArgs.getSessionKey());
    }
    
    private ListProgramArgument<String> name() {
        return optionalArg("-n", importArgs.getName());
                         
    }
    
    private ListProgramArgument<String> description() {
        return optionalArg("-x", importArgs.getDescription());                     
    }
    
    private ListProgramArgument<String> datasetId() {
        return optionalArg("-d", importArgs.getDatasetId());                     
    }
    
    private ListProgramArgument<String> screenId() {
        return optionalArg("-r", importArgs.getScreenId());
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
    
    private ProgramArgument<String> importTarget() {
        return new BaseProgramArgument<>(importArgs.getTarget().toString());
    }
    
    private CommandBuilder assembleCommand() {
        return java(classPath(), mainClass())
               .addApplicationArgument(server())
               .addApplicationArgument(name())
               .addApplicationArgument(description())
               .addApplicationArgument(datasetId())
               .addApplicationArgument(screenId())
               .addApplicationArgument(textAnnotations())
               .addApplicationArgument(annotationIds())
               .addApplicationArgument(importTarget());
    }
    
    @Override
    public Stream<String> tokens() {
        return assembleCommand().tokens();
    }
    
    @Override
    public String toString() {
        List<String> ts = tokens().collect(toList());
        for (int k = 0; k < ts.size(); ++k) {
            if (SessionKeySwitch.equals(ts.get(k)) && k + 1 < ts.size()) {
                ts.set(k + 1, MaskedSessionKey);
                break;
            }
        }
        return ts.stream().collect(joining(" "));
    }

}
