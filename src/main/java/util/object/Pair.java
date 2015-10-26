package util.object;

import java.util.Objects;

/**
 * A pair {@code (x,y)} in {@code A×B}.
 */
public class Pair<A, B> {

    private final A fst;
    private final B snd;

    /**
     * Creates a new pair.
     * @param x the first element.
     * @param y the second element.
     */
    public Pair(A x, B y) {
        fst = x;
        snd = y;
    }

    /**
     * @return the first element of the pair.
     */
    public A fst() {
        return fst;
    }

    /**
     * @return the second element of the pair.
     */
    public B snd() {
        return snd;
    }

    @Override 
    public boolean equals(Object x) {
        if (x instanceof Pair) {
            Pair<?, ?> other = (Pair<?, ?>) x;
            return Objects.equals(fst, other.fst) 
                && Objects.equals(snd, other.snd);
         }
         return false;
    }
    
    @Override 
    public int hashCode() {
        return Objects.hash(fst, snd);
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %s)", fst, snd);
    }
    
}
