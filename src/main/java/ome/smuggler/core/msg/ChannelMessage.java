package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

public class ChannelMessage<M, D> {

    private final Optional<M> metadata;
    private final D data;
    
    public ChannelMessage(M metadata, D data) {
        requireNonNull(metadata, "metadata");
        requireNonNull(data, "data");
        
        this.metadata = Optional.of(metadata);
        this.data = data;
    }
    
    public ChannelMessage(D data) {
        requireNonNull(data, "data");
        
        this.metadata = Optional.empty();
        this.data = data;
    }
    
    public Optional<M> metadata() {
        return metadata;
    }
    
    public D data() {
        return data;
    }
    
    @Override
    public boolean equals(Object x) {
        if (x == this) {  // avoid unnecessary work
            return true;
        }
        if (x instanceof ChannelMessage) {  // false if x == null or type differs
            ChannelMessage<?, ?> other = (ChannelMessage<?, ?>) x;  // best we can do, courtesy of type erasure
            return Objects.equals(this.metadata, other.metadata)    // crossing fingers their equals methods check
                && Objects.equals(this.data, other.data);           // the type is the same
        }
        return false;
    }
    /* NOTE. See also:
     * - http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ501
     */
    
    @Override
    public int hashCode() {
        return Objects.hash(metadata, data);
    }
    
}
