package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.stream.Stream;


/**
 * Represents a JVM class path.
 */
public class ClassPath {
    
    /**
     * Parses the given string as a JVM class path.
     * @param cp a string of paths separated by a ':'. It may be empty.
     * @return the parsed class path entries.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ClassPath fromString(String cp) {
        requireNonNull(cp, "cp");
        
        String[] cpEntries = cp.split(":");
        Stream<Path> ps = Stream.of(cpEntries).map(Paths::get);
        return new ClassPath().add(ps);
    }
    
    
    private LinkedHashSet<Path> entries;
    
    /**
     * Creates a new {@link #isEmpty() empty} class path.
     */
    public ClassPath() {
        entries = new LinkedHashSet<>();
    }
    
    /**
     * Does this class path have any path entries?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    /**
     * Appends the given entries to this class path.
     * This method will preserve the order in which entries are given as well
     * as the order in which they were added; e.g. {@code add(x).add(y, z)} 
     * will yield a class path of {@code x:y:z}.
     * If an entry was already in the class path, this method will not add it
     * again. This is consistent with the way the JVM searches the class path, 
     * as a class path of {@code x:y:x} is equivalent to {@code x:y}.
     * Also, each entry will be normalized before appending; if it turns out
     * to be the empty path, then it will not be appended.
     * @param xs the entries to add; they will be added in the given order from
     * left to right.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if the argument is {@code null} or any of 
     * its elements are {@code null}.
     * @see #toStream()
     * @see #toString()
     */
    public ClassPath add(Path...cpEntries) {
        return add(Stream.of(cpEntries));
    }
    
    /**
     * Convenience method, same as {@link #add(Path...)}.
     * @param cpEntries the entries to add; the stream must be ordered.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if the argument is {@code null} or any of 
     * its elements are {@code null}.
     */
    public ClassPath add(Stream<Path> cpEntries) {
        requireNonNull(cpEntries, "cpEntries");
        
        cpEntries.map(p -> requireNonNull(p))
                 .map(Path::normalize)
                 .filter(p -> p.toString() != "")
                 .forEachOrdered(entries::add);
        
        return this;
    }
    
    /**
     * Appends all the entries in the given class path to this class path.
     * This method will preserve the order in which entries are stored in the
     * given class path.
     * If an entry was already in the class path, this method will not add it
     * again. This is consistent with the way the JVM searches the class path, 
     * as a class path of {@code x:y:x} is equivalent to {@code x:y}.
     * @param cp the entries to add.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if the argument is {@code null}.
     * @see #toStream()
     * @see #toString()
     */
    public ClassPath add(ClassPath cp) {
        requireNonNull(cp, "cp");
        entries.addAll(cp.entries);
        return this;
    }
    
    /**
     * @return the entries in this class path, in the same order in which they
     * were {@link #add(Path...) added}.
     */
    public Stream<Path> toStream() {
        return entries.stream();
    }
    
    /**
     * Turns this class path into its JVM string representation where path 
     * entries are separated by a ':'. 
     * Entries will be output in the same order in which they were {@link 
     * #add(Path...) added}.
     * If this class path only has one entry, that path is returned as a string 
     * without any added ':'. If this class path is {@link #isEmpty() empty}, 
     * then the empty string is returned.
     */
    @Override
    public String toString() {
        return toStream().map(Path::toString).collect(joining(":"));
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ClassPath) {
            return toString().equals(other.toString());
        }
        return false;
    }
    
}
