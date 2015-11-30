package ome.smuggler.core.msg;

import java.util.Optional;

/**
 * Encapsulates the consumption of an asynchronous message received from a 
 * channel along with associated metadata.
 * @see ChannelSink 
 */
public interface ChannelAwareSink<M, D> {

    /**
     * Consumes a message.
     * @param metadata any parameter associated with the reception of the
     * message from the channel or an empty value if no metadata is available.
     * @param data the message data output from the channel.
     */
    void consume(Optional<M> metadata, D data);
    
}
