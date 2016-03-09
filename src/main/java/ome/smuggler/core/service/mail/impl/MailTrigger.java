package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.types.MailId;
import ome.smuggler.core.types.PlainTextMail;

public class MailTrigger implements MailRequestor {

    private final MailEnv env;
    
    public MailTrigger(MailEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    @Override
    public MailId enqueue(PlainTextMail request) {
        env.queue().uncheckedSend(request);
        return null;
    }
    // TODO mail id

}
