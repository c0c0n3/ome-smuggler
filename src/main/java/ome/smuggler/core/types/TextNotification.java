package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.util.Objects;

/**
 * A text message to notify somebody of something.
 */
public class TextNotification {

    private final Email recipient;
    private final String title;
    private final String content;
    
    /**
     * Creates a new instance.
     * @param recipient the email address of this notification's recipient.
     * @param title the title of this notification message.
     * @param content the content of this notification message.
     * @throws NullPointerException if the {@code recipient} is {@code null}.
     * @throws IllegalArgumentException if any of the string arguments is
     * {@code null} or empty.
     */
    public TextNotification(Email recipient, String title, String content) {
        requireNonNull(recipient, "recipient");
        requireString(title, "title");
        requireString(content, "content");
        
        this.recipient = recipient;
        this.title = title;
        this.content = content;
    }

    /**
     * @return the email address of this notification's recipient.
     */
    public Email getRecipient() {
        return recipient;
    }

    /**
     * @return the title of this notification message.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the content of this notification message.
     */
    public String getContent() {
        return content;
    }
    
    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof TextNotification) {
            TextNotification other = (TextNotification) x;
            return Objects.equals(recipient, other.recipient)
                && Objects.equals(title, other.title)
                && Objects.equals(content, other.content);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(recipient, title, content);
    }
    
}
