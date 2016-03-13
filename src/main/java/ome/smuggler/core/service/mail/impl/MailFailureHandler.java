package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.io.FileOps.writeNew;

import java.nio.file.Path;

import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.types.QueuedMail;


public class MailFailureHandler implements FailedMailHandler {

    private final MailEnv env;
    
    public MailFailureHandler(MailEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    @Override
    public void accept(QueuedMail data) {
        env.log().failedMail(data);
        
        Path failedMessageFile = env.failedMailPathFor(data.getTaskId());
        writeNew(failedMessageFile, 
                 outputStream -> env.mailClient()
                                    .stream(data.getRequest(), outputStream));
    }

}
