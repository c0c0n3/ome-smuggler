package ome.smuggler.core.service.omero.impl;


import static java.util.Objects.requireNonNull;

import java.net.URI;

import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.OmeCliConfigSource;
import util.runtime.ListProgramArgument;
import util.runtime.jvm.JvmCmdBuilder;

/**
 * Builds the command line to call the OMERO session keep-alive command.
 */
public class KeepAliveCommandBuilder extends OmeCliCommandBuilder {

    private final ImportInput importArgs;
    
    /**
     * Creates a new instance to build a command line from the given data.
     * @param config configuration for the OMERO CLI commands.
     * @param importArgs details what to import.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public KeepAliveCommandBuilder(OmeCliConfigSource config, ImportInput importArgs) {
        super(config);
        requireNonNull(importArgs, "importArgs");
        
        this.importArgs = importArgs;
    }

    private ListProgramArgument<String> serverAndKey() {
        URI omero = importArgs.getOmeroHost();
        return arg(omero.getHost(), 
                   String.valueOf(omero.getPort()), 
                   importArgs.getSessionKey());
    }

    @Override
    protected String commandName() {
        return "SessionKeepAlive";
    }

    @Override
    protected JvmCmdBuilder assembleArguments(JvmCmdBuilder java) {
        return java.addApplicationArgument(serverAndKey());
    }

}
