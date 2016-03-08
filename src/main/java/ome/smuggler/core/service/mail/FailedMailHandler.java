package ome.smuggler.core.service.mail;

import java.util.function.Consumer;

import ome.smuggler.core.types.PlainTextMail;

/**
 * Handles mailing requests that were run by the {@link MailProcessor} but did 
 * not complete successfully.
 */
public interface FailedMailHandler extends Consumer<PlainTextMail> {

}
