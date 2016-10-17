package ome.smuggler.core.io.crypto;

import static org.hamcrest.Matchers.*;
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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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

    @Theory
    public void canImportKeyExportedToString(CryptoAlgoSpec algo) {
        byte[] exported = exportNewKey(algo).getBytes(StandardCharsets.UTF_8);
        Key key = importKey(new ByteArrayInputStream(exported));

        CipherFactory crypto = new CipherFactory(algo, key);
        CryptoSinkWriter<byte[]> encryptionFilter =
                new CryptoSinkWriter<>(crypto, OutputStream::write);

        byte[] input = new byte[] { 10 };
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        encryptionFilter.uncheckedWrite(sink, input);
        byte[] encrypted = sink.toByteArray();

        assertThat(encrypted.length, greaterThan(0));
        assertThat(encrypted[0], is(not(input[0])));
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

    @Test (expected = NullPointerException.class)
    public void exportNewKeyThrowsIfNullAlgo() {
        exportNewKey(null);
    }

}
