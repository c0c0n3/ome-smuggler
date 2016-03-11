package ome.smuggler.core.service.mail.impl;

import static java.util.stream.Collectors.joining;
import static util.object.Pair.pair;

import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ome.smuggler.core.service.Loggers;
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedMail;

/**
 * Utility logging methods for the mail service.
 */
public class MailLoggers extends Loggers {

    private static String mailDataSummary(QueuedMail data) {
        PlainTextMail req = data.getRequest();
        return Stream.of(
                pair("To", req.getRecipient().get()),
                pair("Subject", req.getSubject()))
            .map(p -> String.format(">>> %s: %s%n", p.fst(), p.snd()))
            .collect(joining());
    }
    
    private static Consumer<PrintWriter> messageWriter(
            QueuedMail data, String event) {
        return buf -> {
            buf.println(event);
            buf.println(mailDataSummary(data));
        };
    }
    
    public static void logMailSent(QueuedMail data) {
        logInfo(data, messageWriter(data, "Email sent."));
    }
    
    public static void logFailedMail(QueuedMail data) {
        logError(data, messageWriter(data, "Failed to send email."));
    }
    
}
