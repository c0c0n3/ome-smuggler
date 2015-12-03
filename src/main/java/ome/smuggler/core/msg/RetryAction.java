package ome.smuggler.core.msg;

/**
 * Indicates weather a message should be re-delivered after being consumed.
 * @see DeliveryRetry
 */
public enum RetryAction {

    /**
     * A message consumer uses this flag to cause re-delivery of the message.
     */
    Retry, 
    
    /**
     * A message consumer uses this flag to indicate the message has been
     * consumed fully and must not be re-delivered.
     */
    Stop;
    
}
