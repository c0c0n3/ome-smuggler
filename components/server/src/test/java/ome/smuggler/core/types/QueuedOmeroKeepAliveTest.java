package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.net.URI;

public class QueuedOmeroKeepAliveTest {

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullOmero() {
        new QueuedOmeroKeepAlive(null, "sesh");
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullSessionKey() {
        new QueuedOmeroKeepAlive(URI.create("h:1"), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptySessionKey() {
        new QueuedOmeroKeepAlive(URI.create("h:1"), "");
    }

    @Test
    public void enforceValueEquality() {
        URI omero = URI.create("h:1");
        String sessionKey = "sk";
        QueuedOmeroKeepAlive value =
                new QueuedOmeroKeepAlive(omero, sessionKey);
        QueuedOmeroKeepAlive valueCopy =
                new QueuedOmeroKeepAlive(omero, sessionKey);

        assertThat(value.getOmero(), is(omero));
        assertThat(value.getSessionKey(), is(sessionKey));

        assertThat(value, is(valueCopy));
        assertThat(value.hashCode(), is(valueCopy.hashCode()));
    }

}
