package ome.smuggler.core.io.crypto;


import ome.smuggler.core.convert.SourceReader;

import javax.crypto.CipherInputStream;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;

/**
 * An encryption filter that wraps an underlying {@link SourceReader} {@code r}
 * to decrypt whatever {@code r} reads from the source {@link InputStream}.
 */
public class CryptoSourceReader<T> implements SourceReader<InputStream, T> {

    private final CipherFactory crypto;
    private final SourceReader<InputStream, T> reader;

    /**
     * Creates a new instance.
     * @param crypto factory to create encryption ciphers.
     * @param reader the underlying reader that actually pulls the
     *               {@code T-}value out of the source {@link InputStream}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CryptoSourceReader(CipherFactory crypto,
                              SourceReader<InputStream, T> reader) {
        requireNonNull(crypto, "crypto");
        requireNonNull(reader, "reader");

        this.crypto = crypto;
        this.reader = reader;
    }

    @Override
    public T read(InputStream source) throws Exception {
        requireNonNull(source, "source");

        try (CipherInputStream cis = new CipherInputStream(
                source, crypto.decryptionChipher())) {
            return reader.read(cis);
        }
    }

}
