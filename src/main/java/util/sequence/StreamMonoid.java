package util.sequence;

import java.util.stream.Stream;

import util.alg.Monoid;
import util.object.Wrapper;

public class StreamMonoid<T> 
    extends Wrapper<Stream<T>> implements Monoid<Stream<T>> {

    public StreamMonoid() {
        super(Stream.empty());
    }
    
    public StreamMonoid(Stream<T> wrappedValue) {
        super(wrappedValue);
    }

    @Override
    public Monoid<Stream<T>> unit() {
        return new StreamMonoid<>();
    }

    @Override
    public Monoid<Stream<T>> _x_(Monoid<Stream<T>> t) {
        Stream<T> joined = Stream.concat(get(), t.get());
        return new StreamMonoid<>(joined);
    }

}
