package ome.smuggler.core.msg;

/**
 * Encapsulates the consumption of an asynchronous message received from a 
 * channel.
 */
public interface ChannelSink<T> {

    /**
     * Consumes a message.
     * @param data the message data output from the channel.
     */
    void consume(T data);
    
}
