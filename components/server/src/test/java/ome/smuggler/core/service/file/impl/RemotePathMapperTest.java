package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Arrayz.asList;

import ome.smuggler.core.service.file.RemotePathResolver;
import ome.smuggler.core.types.RemoteMount;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@RunWith(Theories.class)
public class RemotePathMapperTest {

    private static Path path(String absPath) {
        URI fileUri = URI.create("file://" + absPath);
        return Paths.get(fileUri);
    }
    // NB converting to URI to avoid being platform dependent with '/' in path.

    @DataPoints
    public static final String[] localPathSupply = array(
        "/", "rel/path", "/abs/path", "file:///", "file:///still/local"
    );

    static final RemoteMount m1 = new RemoteMount(
            URI.create("file://h1/d1"), path("/mnt/d1"));
    static final RemoteMount m2 = new RemoteMount(
            URI.create("file://h2/d2"), path("/mnt/d2"));

    @DataPoints
    public static final RemoteMount[][] mountSupply = array(
        array(), array(m1), array(m1, m2)
    );

    static RemotePathResolver resolver(RemoteMount[] remoteToLocalMap) {
        return new RemotePathMapper(asList(remoteToLocalMap));
    }

    @Theory
    public void neverMapLocalPath(String localPath,
                                  RemoteMount[] remoteToLocalMap) {
        RemotePathResolver target = resolver(remoteToLocalMap);

        URI input = URI.create(localPath);
        Optional<Path> actual = target.toLocalPath(input);

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

    @Test
    public void mapRemoteToLocal() {
        RemotePathResolver target = resolver(mountSupply[2]);
        URI remotePath = URI.create("file://h2/d2/x/my-file");
        Optional<Path> actual = target.toLocalPath(remotePath);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get().toString(), containsString("my-file"));
    }

    @Test (expected = NullPointerException.class)
    public void toLocalPathThrowsIfNullArg() {
        resolver(mountSupply[2]).toLocalPath(null);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new RemotePathMapper(null);
    }

}
