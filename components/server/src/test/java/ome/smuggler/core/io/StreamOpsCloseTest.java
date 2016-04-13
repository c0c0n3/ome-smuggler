package ome.smuggler.core.io;

import org.junit.Test;

import static ome.smuggler.core.io.StreamOps.close;

import java.io.Closeable;
import java.io.IOException;

public class StreamOpsCloseTest {

    @Test (expected = NullPointerException.class)
    public void closeThrowsIfNullArg() {
        close(null);
    }

    @Test
    public void closeSwallowsIoException() {
        close(new Closeable() {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        });
    }

    @Test (expected = RuntimeException.class)
    public void closeLetsRuntimeExceptionBubbleUp() {
        close(new Closeable() {
            @Override
            public void close() throws IOException {
                throw new RuntimeException();
            }
        });
    }

}
