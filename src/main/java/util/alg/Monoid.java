package util.alg;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Classes implementing this interface state that their instances are the 
 * objects of a <a href="https://en.wikipedia.org/wiki/Monoid">monoid</a>.
 * Methods in this interface are named with multiplicative notation in mind:
 * the binary operation is the "product" and the identity is the "unit".
 */
public interface Monoid<T> extends Supplier<T> {

    /**
     * This monoid's identity; ideally each implementing class would have a 
     * parameter-less constructor which would create the identity.
     * Any two calls to this method may not return the same object, but in any
     * case the returned objects {@code u} and {@code u'} have to be equal
     * according to the {@link Object#equals(Object) equals} method. 
     * @return this monoid's identity.
     */
    Monoid<T> unit();
    
    /**
     * The monoid's product, i.e. its associative and unit-bearing binary 
     * operation.
     * @param t the object to "multiply" with this object.
     * @return the object resulting from multiplying {@code t} with this object.
     * @throws NullPointerException if the argument is {@code null}.
     */
    Monoid<T> _x_(Monoid<T> t);
    
    /**
     * "Multiplies" the given sequence of objects.
     * This is the canonical reduction operation from the free monoid to this 
     * monoid, see e.g. <a href="https://en.wikipedia.org/wiki/Monoid#Monoids_in_computer_science">
     * monoids in computer science</a>.
     * @param ts
     * @return
     * @throws NullPointerException if the argument is {@code null} or any of
     * the contained objects is {@code null}.
     */
    default Monoid<T> fold(Stream<Monoid<T>> ts) {
        requireNonNull(ts, "ts");
        return ts.reduce(unit(), (v, w) -> v._x_(w));
    }
    
}
