package ome.smuggler.core.service.imports.impl;

import static ome.smuggler.core.types.ImportInputTest.makeNew;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import ome.smuggler.config.data.DefaultOmeCliConfig;
import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.core.types.ImportInput;

public class KeepAliveCommandBuilderTest {

    private static OmeCliConfig config() {
        OmeCliConfig cfg = new DefaultOmeCliConfig()
                               .defaultReadConfig()
                               .findFirst()
                               .get();
        cfg.setOmeLibDirPath("gradle");
        
        return cfg;
    }
    
    private static String[] tokenPgmArgs(ImportInput args) {
        String[] whole = new KeepAliveCommandBuilder(config(), args)
                        .tokens()
                        .toArray(String[]::new);
        return Arrays.copyOfRange(whole, 4, whole.length);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfFirstArgNull() {
        new KeepAliveCommandBuilder(null, makeNew());
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfSecondArgNull() {
        new KeepAliveCommandBuilder(config(), null);
    }
    
    @Test
    public void minimalCommandLine() {
        ImportInput args = makeNew();
        String[] xs = tokenPgmArgs(args);
        
        assertThat(xs.length, is(3));
        assertThat(xs[0], is(args.getOmeroHost().getHost()));
        assertThat(xs[1], is("" + args.getOmeroHost().getPort()));
        assertThat(xs[2], is(args.getSessionKey()));
    }

}
