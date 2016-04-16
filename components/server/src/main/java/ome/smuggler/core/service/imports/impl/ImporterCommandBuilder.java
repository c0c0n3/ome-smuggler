package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.OmeCliConfigSource;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;
import util.runtime.ProgramArgument;
import util.runtime.jvm.JvmCmdBuilder;

/**
 * Build the command line to call the OMERO importer.
 */
public class ImporterCommandBuilder extends OmeCliCommandBuilder {

    public static final String SessionKeySwitch = "-k";
    public static final String MaskedSessionKey = "***";
    
    
    private final ImportInput importArgs;
    
    /**
     * Creates a new instance to build a command line from the given data.
     * @param config configuration for the OMERO CLI commands.
     * @param importArgs details what to import.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImporterCommandBuilder(OmeCliConfigSource config,
                                  ImportInput importArgs) {
        super(config);
        requireNonNull(importArgs, "importArgs");
        
        this.importArgs = importArgs;
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
        String absPath = Paths.get(importArgs.getTarget()).toString();  // (*) 
        return new BaseProgramArgument<>(absPath);
    }
    /* (*) URI resolution. We're assuming the file is local but going forward
     * we might replace this with a more sophisticated URI to file resolution
     * so that files may at least come from a network share visible to both
     * client and smuggler. We could stretch it even further and cater for
     * FTP and HTTP but the OMERO import library will have to be modified to
     * read files from FTP or HTTP... 
     */

    @Override
    protected JvmCmdBuilder assembleArguments(JvmCmdBuilder java) {
        return java
               .addApplicationArgument(arg("Import"))
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