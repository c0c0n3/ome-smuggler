package ome.smuggler.core.service.imports.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.types.ImportInputTest.*;

import java.util.Arrays;

import ome.smuggler.config.data.DefaultOmeCliConfig;
import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.core.service.imports.impl.ImporterCommandBuilder;
import ome.smuggler.core.types.ImportInput;

import org.junit.Test;

public class ImporterCommandBuilderTest {

    private static OmeCliConfig config() {
        OmeCliConfig cfg = new DefaultOmeCliConfig()
                               .defaultReadConfig()
                               .findFirst()
                               .get();
        cfg.setOmeLibDirPath("gradle");
        
        return cfg;
    }
    
    private static String[] tokenPgmArgs(ImportInput args) {
        String[] whole = new ImporterCommandBuilder(config(), args)
                        .tokens()
                        .toArray(String[]::new);
        return Arrays.copyOfRange(whole, 4, whole.length);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfFirstArgNull() {
        new ImporterCommandBuilder(null, makeNew());
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfSecondArgNull() {
        new ImporterCommandBuilder(config(), null);
    }
    
    @Test
    public void minimalCommandLine() {
        String[] xs = tokenPgmArgs(makeNew());
        
        assertThat(xs.length, is(7));
        assertThat(xs[0], is("-s"));
        assertThat(xs[2], is("-p"));
        assertThat(xs[4], is("-k"));
        assertThat(xs[6], is(makeNew().getTarget().toString()));
    }
    
    @Test
    public void commandLineWithName() {
        ImportInput args = makeNew().setName("name");
        String[] xs = tokenPgmArgs(args);
        
        assertThat(xs.length, is(9));
        assertThat(xs[0], is("-s"));
        assertThat(xs[2], is("-p"));
        assertThat(xs[4], is("-k"));
        assertThat(xs[6], is("-n"));
    }
    
    @Test
    public void commandLineWithDescription() {
        ImportInput args = makeNew().setName("name").setDescription("desc");
        String[] xs = tokenPgmArgs(args);
        
        assertThat(xs.length, is(11));
        assertThat(xs[0], is("-s"));
        assertThat(xs[2], is("-p"));
        assertThat(xs[4], is("-k"));
        assertThat(xs[6], is("-n"));
        assertThat(xs[8], is("-x"));
    }
    
    @Test
    public void commandLineWithDatasetId() {
        ImportInput args = makeNew().setName("name").setDescription("desc")
                          .setDatasetId(posN("2"));
        String[] xs = tokenPgmArgs(args);
        
        assertThat(xs.length, is(13));
        assertThat(xs[0], is("-s"));
        assertThat(xs[2], is("-p"));
        assertThat(xs[4], is("-k"));
        assertThat(xs[6], is("-n"));
        assertThat(xs[8], is("-x"));
        assertThat(xs[10], is("-d"));
    }
    
    @Test
    public void commandLineWithScreenId() {
        ImportInput args = makeNew().setName("name").setDescription("desc")
                          .setScreenId(posN("2"));
        String[] xs = tokenPgmArgs(args);
        
        assertThat(xs.length, is(13));
        assertThat(xs[0], is("-s"));
        assertThat(xs[2], is("-p"));
        assertThat(xs[4], is("-k"));
        assertThat(xs[6], is("-n"));
        assertThat(xs[8], is("-x"));
        assertThat(xs[10], is("-r"));
    }
    
    @Test
    public void commandLineWithTextAnnotations() {
        ImportInput args = makeNew().setName("name").setDescription("desc")
                          .setScreenId(posN("2"))
                          .addTextAnnotation(anno("n1", "a1"), anno("n2", "a2"));
        String[] xs = tokenPgmArgs(args);
        
        assertThat(xs.length, is(21));
        assertThat(xs[0], is("-s"));
        assertThat(xs[2], is("-p"));
        assertThat(xs[4], is("-k"));
        assertThat(xs[6], is("-n"));
        assertThat(xs[7], is("name"));
        assertThat(xs[8], is("-x"));
        assertThat(xs[9], is("desc"));
        assertThat(xs[10], is("-r"));
        assertThat(xs[11], is("2"));
        assertThat(xs[12], is("--annotation-ns"));
        assertThat(xs[13], is("n1"));
        assertThat(xs[14], is("--annotation-text"));
        assertThat(xs[15], is("a1"));
        assertThat(xs[16], is("--annotation-ns"));
        assertThat(xs[17], is("n2"));
        assertThat(xs[18], is("--annotation-text"));
        assertThat(xs[19], is("a2"));
    }
    
    @Test
    public void commandLineWithAnnotationIds() {
        ImportInput args = makeNew().setName("name").setDescription("desc")
                          .setScreenId(posN("2"))
                          .addTextAnnotation(anno("n1", "a1"), anno("n2", "a2"))
                          .addAnnotationId(posN("3"));
        String[] xs = tokenPgmArgs(args);
        
        assertThat(xs.length, is(23));
        assertThat(xs[0], is("-s"));
        assertThat(xs[2], is("-p"));
        assertThat(xs[4], is("-k"));
        assertThat(xs[6], is("-n"));
        assertThat(xs[7], is("name"));
        assertThat(xs[8], is("-x"));
        assertThat(xs[9], is("desc"));
        assertThat(xs[10], is("-r"));
        assertThat(xs[11], is("2"));
        assertThat(xs[12], is("--annotation-ns"));
        assertThat(xs[13], is("n1"));
        assertThat(xs[14], is("--annotation-text"));
        assertThat(xs[15], is("a1"));
        assertThat(xs[16], is("--annotation-ns"));
        assertThat(xs[17], is("n2"));
        assertThat(xs[18], is("--annotation-text"));
        assertThat(xs[19], is("a2"));
        assertThat(xs[20], is("--annotation-link"));
        assertThat(xs[21], is("3"));
    }
    
    @Test
    public void maskSessionKey() {
        ImportInput args = makeNew();
        String sessionKey = args.getSessionKey();
        String cmd = new ImporterCommandBuilder(config(), args).toString();
        
        assertThat(cmd, not(containsString(sessionKey)));
    }
    
}
