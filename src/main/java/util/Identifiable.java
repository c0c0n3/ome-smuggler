package util;

/**
 * A way to identify an object in a given context.
 */
public interface Identifiable {

    /**
     * Produces the unique id that identifies this object in some given 
     * context.
     * This method is defaulted to return the string {@code fqn@ref}, where
     * {@code fqn} is the fully qualified class name of this object and 
     * {@code ref} is the HEX representation of this object's native hash code
     * (i.e. what is returned by {@link System#identityHashCode(Object) 
     * identityHashCode}) which is usually the memory address of the object.
     * This is the same of what {@code toString} returns if it has not been
     * overwritten and {@code hashCode} has not been overwritten either.
     * @return this object's id.
     */
    default String id() {
        return String.format("%s@%h", 
                             this.getClass().getName(),
                             System.identityHashCode(this));
    }
    
}
