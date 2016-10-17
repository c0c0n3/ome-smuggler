package ome.smuggler.core.io.crypto;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.runUnchecked;
import static util.error.Exceptions.unchecked;

import javax.crypto.KeyGenerator;
import java.io.*;
import java.security.Key;
import java.util.Base64;

/**
 * Factory methods for security {@link Key}s.
 */
public class CryptoKeyFactory {

    /**
     * Generates a security {@link Key} for the given algorithm.
     * @param algo the algorithm to use.
     * @return the key.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Key generateKey(CryptoAlgoSpec algo) {
        requireNonNull(algo, "algo");

        return unchecked(() -> KeyGenerator.getInstance(algo.canonicalName())
                                           .generateKey()).get();
    }

    /**
     * Serialises a key into a given stream using Base64 encoding.
     * @param key the key to serialise.
     * @param out the destination stream to write the data to.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IOException if an error occurs while writing to the stream;
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping.
     * @see #importKey(InputStream)
     */
    public static void exportKey(Key key, OutputStream out) {
        requireNonNull(key, "key");
        requireNonNull(out, "out");

        runUnchecked(() -> {
            try (OutputStream b64 = Base64.getEncoder().wrap(out);
                 ObjectOutputStream s = new ObjectOutputStream(b64)) {
                s.writeObject(key);
                s.flush();
            }
        });
    }

    /**
     * De-serialises a key from a given Base64-encoded stream.
     * @param in contains the key object as serialised by the {@link
     * #exportKey(Key, OutputStream) export} method.
     * @return the key object read from the stream.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an error occurs while reading from the stream;
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping.
     * @throws ClassNotFoundException if an error occurs while instantiating
     * the key object; the exception is masked as a runtime exception and thrown
     * as is without wrapping.
     * @see #exportKey(Key, OutputStream)
     */
    public static Key importKey(InputStream in) {
        requireNonNull(in, "in");

        return unchecked(() -> {
            try (InputStream b64 = Base64.getDecoder().wrap(in);
                 ObjectInputStream s = new ObjectInputStream(b64)) {
                return (Key) s.readObject();
            }
        }).get();
    }

}
