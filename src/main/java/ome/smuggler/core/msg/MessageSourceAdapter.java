package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;

/**
 * Makes any {@link MessageSource} work like a plain {@link ChannelSource} by
 * simply discarding metadata.
 */
public class MessageSourceAdapter<T> implements ChannelSource<T> {

    private final MessageSource<?, T> target;
    
    /**
     * Creates a new instance.
     * @param target the target channel over which data will be sent.
     */
    public MessageSourceAdapter(MessageSource<?, T> target) {
        requireNonNull(target, "target");
        this.target = target;
    }
    
    @Override
    public void send(T data) throws Exception {
        target.sendData(data);
    }

}
