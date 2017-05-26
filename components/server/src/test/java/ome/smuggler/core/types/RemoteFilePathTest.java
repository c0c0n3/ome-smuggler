package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.object.Pair.pair;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import util.object.Pair;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(Theories.class)
public class RemoteFilePathTest {

    @DataPoints
    public static final Pair<String, String>[] schemeSupply = array(
        pair("", ""), pair("xxx", "://"), pair("ftp", "://"),
        pair("file", "://")
    );

    @DataPoints
    public static final String[] hostSupply = array("", "host");

    @DataPoints
    public static final Path[] pathSupply = array(
        Paths.get(""), Paths.get("/"), Paths.get("/x/y")
    );

    private static URI buildURI(Pair<String, String> scheme,
                                String host, String path) {
        StringBuilder buf = new StringBuilder();

        buf.append(scheme.fst());
        buf.append(scheme.snd());
        if (host.isEmpty() && path.isEmpty()) {
            buf.append("/");
        }
        buf.append(host);
        buf.append(path);

        return URI.create(buf.toString());
    }


    @Theory
    public void isRemoteJustInCaseIsFileSchemeAndHasHost(
            Pair<String, String> scheme, String host, Path path) {
        URI x = buildURI(scheme, host, path.toString());

        boolean expected = "file".equals(scheme.fst()) && !host.isEmpty();
        boolean actual = RemoteMount.isRemoteFilePath(x);

        assertThat(actual, is(expected));
    }


    @Test
    public void nullNotRemotePath() {
        assertFalse(RemoteMount.isRemoteFilePath(null));
    }

    @Test
    public void emptyNotRemotePath() {
        URI x = URI.create("");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void absLocalPathNotRemotePath() {
        URI x = URI.create("/some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void relLocalPathNotRemotePath() {
        URI x = URI.create("some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void urlNotRemotePath() {
        URI x = URI.create("http://host/some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void fileUriWithoutHostNotRemotePath() {
        URI x = URI.create("file:///some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void fileUriWithHostIsRemotePath() {
        URI x = URI.create("file://host");
        assertTrue(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void fileUriWithHostAndPathIsRemotePath() {
        URI x = URI.create("file://host/some/file");
        assertTrue(RemoteMount.isRemoteFilePath(x));
    }

}
