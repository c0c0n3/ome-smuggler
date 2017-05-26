package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RunWith(Theories.class)
public class RemoteMountTest {

    @DataPoints
    public static final URI[] remoteBaseSupply = array(
        URI.create("file://h"), URI.create("file://h/"),
        URI.create("file://h1/d"), URI.create("file://h1/d/")
    );

    @DataPoints
    public static final Path[] localBaseSupply = array(
        Paths.get("/"), Paths.get("/mnt"),
        Paths.get("/mnt/d"), Paths.get("/mnt/d/")
    );

    @DataPoints
    public static final String[] pathSupply = array(
        "", "/", "/x", "/x/y", "/x/y/");

    private static RemoteMount newTarget(String remotePrefix,
                                         String localPrefix) {
        return new RemoteMount(URI.create(remotePrefix),
                               Paths.get(localPrefix));
    }

    @Theory
    public void nullNeverTranslatesToLocalPath(URI remote, Path local) {
        RemoteMount target = new RemoteMount(remote, local);

        Optional<Path> actual = target.toLocalPath(null);
        assertFalse(actual.isPresent());
    }

    @Theory
    public void localPathNeverTranslatesToLocalPath(
            URI remote, Path local, String path) {
        RemoteMount target = new RemoteMount(remote, local);

        Optional<Path> actual = target.toLocalPath(URI.create(path));
        assertFalse(actual.isPresent());
    }

    @Theory
    public void translateRemotePathIfSameRemotePrefix(
            URI remote, Path local, String partialPath)
            throws URISyntaxException {
        RemoteMount target = new RemoteMount(remote, local);
        String fullPath = Paths.get(remote.getPath(), partialPath).toString();
        URI remotePath = new URI("file", remote.getHost(), fullPath, null);

        Optional<Path> actual = target.toLocalPath(remotePath);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(local.resolve(partialPath)));
    }

    @Theory
    public void neverTranslateRemotePathIfNotSameRemotePrefix(
            URI remote, Path local, String partialPath)
            throws URISyntaxException {
        RemoteMount target = new RemoteMount(remote, local);
        String fullPath = Paths.get(remote.getPath(), partialPath).toString();
        URI remotePath = new URI(
                "file", remote.getHost() + "x", fullPath, null);

        Optional<Path> actual = target.toLocalPath(remotePath);
        assertFalse(actual.isPresent());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullRemoteBase() {
        new RemoteMount(null, Paths.get("/"));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullLocalBase() {
        new RemoteMount(URI.create("/"), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNotRemotePath() {
        new RemoteMount(URI.create("/"), Paths.get("/"));
    }

}
