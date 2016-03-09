package ome.smuggler.core.service.imports.impl;

import java.net.InetAddress;

import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;

public class ImporMailFormatter {

    private static final String SuccessSubject = "OMERO import succeeded [ref. %s]";
    private static final String SuccessMessage = 
            "Your image data in %s on %s was successfully imported into the OMERO server on %s.";
    
    private static final String FailureSubject = "Failed OMERO import [ref. %s]";
    private static final String FailureMessage = 
            "Your image data in %s on %s failed to import. Please contact your OMERO administrator.";
    
    
    private static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "[unknown host]";
        }
    }
    
    private static PlainTextMail format(String subjectTemplate, 
            String contentTemplate, QueuedImport task) {
        String subject = String.format(subjectTemplate, task.getTaskId());
        String content = String.format(contentTemplate,
                                       task.getRequest().getTarget(),
                                       hostname(),
                                       task.getRequest().getOmeroHost().getHost());

        return new PlainTextMail(task.getRequest().getExperimenterEmail(), 
                                 subject, content);
    }
    
    public static PlainTextMail successMessage(QueuedImport task) {
        return format(SuccessSubject, SuccessMessage, task);
    }
    
    public static PlainTextMail failureMessage(QueuedImport task) {
        return format(FailureSubject, FailureMessage, task);
    }
    
}
