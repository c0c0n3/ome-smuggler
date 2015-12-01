package ome.smuggler.core.msg;

import static util.error.Exceptions.unchecked;

public interface MessageSource<M, D> 
    extends ChannelSource<ChannelMessage<M, D>> {
    
    default void sendData(D data) throws Exception {
        send(new ChannelMessage<>(data));
    }
    
    default void uncheckedSendData(D data) {
        unchecked(this::sendData).accept(data);
    }
    
    default void send(M metadata, D data) throws Exception {
        send(new ChannelMessage<>(metadata, data));
    }
    
    default void uncheckedSend(M metadata, D data) {
        unchecked(() -> send(metadata, data));
    }
    
}
