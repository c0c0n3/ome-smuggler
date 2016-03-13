package ome.smuggler.core.service.mail;

import javax.mail.internet.MimeMessage;

/**
 * Defines the functionality our services need to send mail.
 * An external service provider supplies the actual implementation.
 */
public interface MailClient {

    /**
     * @return a new empty MIME mail message.
     */
    MimeMessage createMimeMessage();
    
    /**
     * Relays a mail message to a server.
     * @param data the message to send.
     */
    void send(MimeMessage data);
    
}
