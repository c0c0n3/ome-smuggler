package ome.smuggler.core.service.mail.impl;

import static ome.smuggler.core.msg.RepeatAction.Repeat;

import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.types.PlainTextMail;

public class Mailer implements MailProcessor {

    @Override
    public RepeatAction consume(PlainTextMail data) {
        return Repeat;
    }
    // TODO replace with actual logic.
}
