package ome.smuggler.core.service.omero.impl;

import static ome.smuggler.core.types.ValueParserFactory.omeroUri;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Arrays;

import org.junit.Test;


public class KeepAliveCommandBuilderTest {

    private static URI omeroServer() {
        return omeroUri("somehost", "1234").getRight();
    }

    private static String sessionKey() {
        return "sessionKey";
    }

    private static String[] tokenPgmArgs(URI omero, String sessionKey) {
        String[] whole = new KeepAliveCommandBuilder(
                                    OmeCliConfigBuilder.config(),
                                    omero, sessionKey)
                        .tokens()
                        .toArray(String[]::new);
        return Arrays.copyOfRange(whole, 4, whole.length);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfFirstArgNull() {
        new KeepAliveCommandBuilder(null, omeroServer(), sessionKey());
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfSecondArgNull() {
        new KeepAliveCommandBuilder(OmeCliConfigBuilder.config(), null,
                                    sessionKey());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfThirdArgNull() {
        new KeepAliveCommandBuilder(OmeCliConfigBuilder.config(), omeroServer(),
                                    null);
    }

    @Test
    public void minimalCommandLine() {
        URI omero = omeroServer();
        String sessionKey = sessionKey();
        String[] xs = tokenPgmArgs(omero, sessionKey);
        
        assertThat(xs.length, is(3));
        assertThat(xs[0], is(omero.getHost()));
        assertThat(xs[1], is("" + omero.getPort()));
        assertThat(xs[2], is(sessionKey));
    }

}
