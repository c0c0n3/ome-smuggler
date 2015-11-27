package ome.smuggler.core.msg;

/**
 * Encapsulates the consumption of an asynchronous message received from a 
 * channel along with associated metadata.
 * @see ChannelSink 
 */
public interface ChannelAwareSink<M, D> {

    /**
     * Consumes a message.
     * @param metadata any parameter associated to the reception of the
     * message from the channel.
     * @param data the message data output from the channel.
     */
    void consume(M metadata, D data);
    
}
