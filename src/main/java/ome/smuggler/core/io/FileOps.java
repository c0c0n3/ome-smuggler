package ome.smuggler.core.io;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.runUnchecked;
import static util.error.Exceptions.unchecked;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import ome.smuggler.core.types.Nat;

/**
 * Utility methods for file operations.
 */
public class FileOps {

    /**
     * Deletes the specified file if it exists; does nothing otherwise.
     * @param file path to the file to delete.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * unchecked.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static void delete(Path file) {
        requireNonNull(file, "file");
        runUnchecked(() -> Files.deleteIfExists(file));
    }
    
    /**
     * Copies the source file to the destination path, overriding it if it 
     * exists already. If the source file does not exist, this method does
     * nothing.
     * @param source path to the file to copy.
     * @param destination path to the copied file.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * unchecked.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static void copy(Path source, Path destination) {
        requireNonNull(source, "source");
        requireNonNull(destination, "destination");
        
        if (Files.exists(source)) {
            runUnchecked(() -> Files.copy(source, destination, 
                                          StandardCopyOption.REPLACE_EXISTING));
        }
    }
    
    /**
     * Efficiently copies an initial segment of a file into a target stream.
     * @param from path to the file from which to get the data.
     * @param howManyBytes number of bytes to take, starting at the beginning
     * of the file.
     * @param to where to copy the data.
     * @return the number of bytes actually copied.
     * @throws IOException if an I/O error occurs.
     */
    public static long transfer(Path from, Nat howManyBytes, OutputStream to) 
            throws IOException {
        requireNonNull(from, "from");
        requireNonNull(howManyBytes, "howManyBytes");
        requireNonNull(to, "to");
        
        try (FileChannel source = FileChannel.open(from);
             WritableByteChannel target = Channels.newChannel(to)) {
            long limit = howManyBytes.get();
            long bytesRead = 0;
            while (bytesRead < limit && bytesRead < source.size()) {  // (1)
                long count = limit - bytesRead;
                bytesRead += source.transferTo(bytesRead, count, target); // (2)
            }
            return bytesRead;
        }
    }
    /* NOTES
     * 1. Don't assume the file won't change as we read it. It may be written to
     * or being truncated; the latter justifies querying the size each time.
     * 2. If the JavaDoc doesn't lie, then transferTo "is potentially much more 
     * efficient than a simple loop that reads from this channel and writes to
     * the target channel. Many operating systems can transfer bytes directly 
     * from the filesystem cache to the target channel without actually copying
     * them."
     */
        
    /**
     * Queries the size of the given file.
     * @param file the file to query.
     * @return the number of bytes in the specified file.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * unchecked.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Nat byteLength(Path file) {
        requireNonNull(file, "file");
        return unchecked(Files::size)
               .andThen(Nat::from)
               .andThen(x -> x.orElse(Nat.of(0)))  // (*)
               .apply(file);
    }
    /* (*) returned file size should always be non-negative, I'm just being
     * paranoid here I guess...
     */
    
    /**
     * Collects all the regular files directly under the specified directory.
     * Sub-directories are not recursed.
     * @param dir the target directory.
     * @return a list of the files found in the directory.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * unchecked.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Stream<Path> listChildFiles(Path dir) {
        requireNonNull(dir, "dir");
        return unchecked(() -> Files.walk(dir, 1).filter(Files::isRegularFile))
                .get();
    }
    
}
