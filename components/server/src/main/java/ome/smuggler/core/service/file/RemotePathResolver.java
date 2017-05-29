package ome.smuggler.core.service.file;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Remote-to-local path translation.
 */
public interface RemotePathResolver {

    /**
     * Resolves a remote path into a local one.
     * The input remote file path is mapped to a local file as documented in
     * {@link ome.smuggler.core.types.RemoteMount}. If no remote-to-local
     * mappings have been configured or none of them can be used to resolve
     * the remote path, then empty is returned.
     *
     * @param remotePath a file URI pointing to a file on a remote device
     *                   connected to this machine through a suitable mount
     *                   point.
     * @return the mapped path.
     * @throws NullPointerException if the argument is {@code null}.
     */
    Optional<Path> toLocalPath(URI remotePath);

}
