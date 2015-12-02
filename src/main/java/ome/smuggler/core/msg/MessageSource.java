package ome.smuggler.core.msg;

import static util.error.Exceptions.unchecked;

/**
 * Encapsulates the sending of an asynchronous message along a channel that
 * supports metadata.
 * This channel source imposes a minimal structure to the data being sent over 
 * the channel, namely that of a message containing data meant to be consumed 
 * by the receiving end and optionally metadata, typically used to configure 
 * the sending of the message.
 * @see ChannelMessage
 */
public interface MessageSource<M, D> 
    extends ChannelSource<ChannelMessage<M, D>> {
    
    /**
     * Inputs data into the channel and returns immediately.
     * This is an asynchronous operation, so that the sender never waits for
     * the receiver to get the data.
     * @param data the item to send.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the data could not be input into the channel.
     */
    default void sendData(D data) throws Exception {
        send(new ChannelMessage<>(data));
    }
    
    /**
     * Same as {@link #sendData(Object) sendData} but masks any exception as a 
     * runtime (unchecked) exception and throws it as such without any wrapping.
     */
    default void uncheckedSendData(D data) {
        unchecked(this::sendData).accept(data);
    }

    /**
     * Inputs data into the channel and returns immediately.
     * This is an asynchronous operation, so that the sender never waits for
     * the receiver to get the data.
     * @param metadata specifies how to configure the send operation.
     * @param data the message to send.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if the data could not be input into the channel.
     */
    default void send(M metadata, D data) throws Exception {
        send(new ChannelMessage<>(metadata, data));
    }
    
    /**
     * Same as {@link #send(Object, Object) send} but masks any exception as a 
     * runtime (unchecked) exception and throws it as such without any wrapping.
     */
    default void uncheckedSend(M metadata, D data) {
        unchecked(() -> send(metadata, data));
    }
    
}
