package ome.smuggler.core.msg;

import static util.error.Exceptions.unchecked;

/**
 * Encapsulates the sending of an asynchronous message along a channel that
 * supports configuring the sending of the message. 
 */
public interface ConfigurableChannelSource<M, D> extends ChannelSource<D> {

    /**
     * Inputs data into the channel and returns immediately.
     * This is an asynchronous operation, so that the sender never waits for
     * the receiver to get the data.
     * @param metadata specifies how to configure the send operation.
     * @param data the message to send.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if the data could not be input into the channel.
     */
    void send(M metadata, D data) throws Exception;
    
    /**
     * Same as {@link #send(Object, Object) send} but masks any exception as a 
     * runtime (unchecked) exception and throws it as such without any wrapping.
     */
    default void uncheckedSend(M metadata, D data) {
        unchecked(() -> send(metadata, data));
    }
    
}
