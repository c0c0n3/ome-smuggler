package util.validation;

/**
 * Enumerates success and failure, with an error message attached to failure.
 */
public class ValidationOutcome {

    /**
     * @return the success value.
     */
    public static ValidationOutcome success() {
        return new ValidationOutcome();
    }
    
    /**
     * @return the failure value with the attached explanatory message. 
     */
    public static ValidationOutcome error(String message) {
        return new ValidationOutcome(message);
    }
    
    private final boolean error;
    private final String message;
    
    private ValidationOutcome() {
        error = false;
        message = "";
    }
    
    private ValidationOutcome(String message) {
        this.error = true;
        this.message = message;
    }
    
    /**
     * Is this the failure outcome value?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean failed() {
        return error;
    }
    
    /**
     * Is this the success outcome value?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean succeeded() {
        return !failed();
    }
    
    /**
     * Returns the explanatory message associated to this instance.
     */
    @Override
    public String toString() {
        return String.valueOf(message);
    }
    
}
