package ome.smuggler.core.service.imports.impl;

import java.net.InetAddress;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;

public class ImportMailFormatter {

    private static final String SuccessSubject = "OMERO import succeeded";
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
            String contentTemplate, QueuedImport task, Email recipient) {
        String subject = String.format(subjectTemplate, task.getTaskId());
        String content = String.format(contentTemplate,
                                       task.getRequest().getTarget(),
                                       hostname(),
                                       task.getRequest().getOmeroHost().getHost());

        return new PlainTextMail(recipient, subject, content);
    }
    
    private static PlainTextMail format(String subjectTemplate, 
            String contentTemplate, QueuedImport task) {
        return format(subjectTemplate, contentTemplate, task, 
                      task.getRequest().getExperimenterEmail());
    }
    
    public static PlainTextMail successMessage(QueuedImport task) {
        return format(SuccessSubject, SuccessMessage, task);
    }
    
    public static PlainTextMail failureMessage(QueuedImport task) {
        return format(FailureSubject, FailureMessage, task);
    }
    
    public static PlainTextMail sysAdminFailureMessage(QueuedImport task, 
            Email sysAdminAddress) {
        return format(FailureSubject, FailureMessage, task, sysAdminAddress);
    }
    
}
