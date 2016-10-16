package ome.smuggler.core.io.crypto;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static ome.smuggler.core.io.crypto.CryptoKeyFactory.*;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Key;


@RunWith(Theories.class)
public class CryptoKeyFactoryTest {

    @DataPoints
    public static CryptoAlgoSpec[] algos = CryptoAlgoSpec.values();

    private static Key serializeThenDeserialize(Key input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exportKey(input, out);

        ByteArrayInputStream serialized =
                new ByteArrayInputStream(out.toByteArray());
        return importKey(serialized);
    }

    @Theory
    public void serializeThenDeserializeIsIdentity(CryptoAlgoSpec algo) {
        Key initialKey = generateKey(algo);
        Key deserializedKey = serializeThenDeserialize(initialKey);

        assertArrayEquals(initialKey.getEncoded(),
                          deserializedKey.getEncoded());
    }

    @Test (expected = NullPointerException.class)
    public void generateKeyThrowsIfNullAlgo() {
        generateKey(null);
    }

    @Test (expected = NullPointerException.class)
    public void exportKeyThrowsIfNullKey() {
        exportKey(null, new ByteArrayOutputStream());
    }

    @Test (expected = NullPointerException.class)
    public void exportKeyThrowsIfNullStream() {
        exportKey(mock(Key.class), null);
    }

    @Test (expected = NullPointerException.class)
    public void importKeyThrowsIfNullStream() {
        importKey(null);
    }

}
