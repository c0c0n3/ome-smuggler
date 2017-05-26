package ome.smuggler.core.io;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static util.string.Strings.isNullOrEmpty;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;


/**
 *
 */
public class MountPointResolver {

    private static Map<URI, Path> filterNull(Map<URI, Path> m) {
        return m.entrySet()
                .stream()
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private static boolean isMatch(Entry<URI, Path> e, URI remotePath) {
        return remotePath.toString().startsWith(e.getKey().toString());
    }

    private static Optional<String> convert(Entry<URI, Path> e, URI remotePath) {
        String k = e.getKey().toString();
        String p = remotePath.toString();
        int ix = p.indexOf(k);

        if (ix == -1) {
            return Optional.empty();
        }

        String r = p.substring(ix, p.length());
        Path local = e.getValue().resolve(r);

        return null;
    }



    private final Map<URI, Path> remoteToLocalMap;

    public MountPointResolver(Map<URI, Path> remoteToLocalMap) {
        requireNonNull(remoteToLocalMap, "remoteToLocalMap");
        this.remoteToLocalMap = filterNull(remoteToLocalMap);
    }

    private Optional<Entry<URI, Path>> findMatch(URI remotePath) {
        return null;
    }

    /**
     * Is the given URI that of a file path on a remote host?
     *
     * @param x the URI to test.
     * @return {@code true} just in case the UR has a "file" scheme and a
     * host part too.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public boolean isRemoteFilePath(URI x) {
        requireNonNull(x, "x");

        return "file".equals(x.getScheme())
            && !isNullOrEmpty(x.getHost());
    }

    /**
     *
     * @param remoteFilePath
     * @return
     * @throws NullPointerException if the argument is {@code null}.
     */
    public Optional<Path> toLocal(URI remoteFilePath) {
        if (!isRemoteFilePath(remoteFilePath)) {
            return Optional.empty();
        }

        return null;
    }
}
