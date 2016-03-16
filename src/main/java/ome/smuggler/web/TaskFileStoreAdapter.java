package ome.smuggler.web;

import static java.util.Objects.requireNonNull;
import static util.spring.http.ResponseEntities._204;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import ome.smuggler.core.service.file.TaskFileStore;
import util.object.Identifiable;
import util.servlet.http.Caches;

/**
 * Provides the means to make the functionality of a {@link TaskFileStore} 
 * available to web clients.
 */
public class TaskFileStoreAdapter<T extends Identifiable> {
    
    private final TaskFileStore<T> service;
    private final MediaType filesContentType;
    private final URI rootPath;
    private final Function<String, T> newTaskId;
    
    /**
     * Creates a new instance.
     * @param service the underlying task file store.
     * @param filesContentType the content type of the files in the store.
     * @param rootPath the base web path from which the files will be served.
     * @param newTaskId creates a task ID from its string representation.
     */
    public TaskFileStoreAdapter(TaskFileStore<T> service, 
                                MediaType filesContentType, URI rootPath, 
                                Function<String, T> newTaskId) {
        requireNonNull(service, "service");
        requireNonNull(filesContentType, "filesContentType");
        requireNonNull(rootPath, "rootPath");
        requireNonNull(newTaskId, "newTaskId");
        
        this.service = service;
        this.filesContentType = filesContentType;
        this.rootPath = rootPath;
        this.newTaskId = newTaskId;
    }
    
    private String toUrlString(T taskId) {
        return UriComponentsBuilder.newInstance()
                                   .path(rootPath.getPath())
                                   .path("/")
                                   .path(taskId.id())
                                   .toUriString();
    }
    
    private T taskIdFromString(String taskIdRepresentation) {
        return newTaskId.apply(taskIdRepresentation);
    }
    
    /**
     * @return the ID's of all the files currently in the store. 
     */
    public String[] listTaskIds() {
        return service.listTaskIds()
                      .map(this::toUrlString)
                      .toArray(String[]::new);
    }
    
    /**
     * Looks up the file associated to a task using the task ID and streams that
     * file to the client if found; otherwise outputs a 404 to the client.
     * @param taskId the ID of the task associated to the file.
     * @param response the response to use to stream the file.
     * @return dummy entity to make this work with Spring MVC.
     * @throws IOException If an error occurs while reading and streaming the
     * file.
     */
    public ResponseEntity<String> streamFileOr404(String taskId, 
            HttpServletResponse response) throws IOException {
        Path file = service.pathFor(taskIdFromString(taskId)); 
        FileStreamer streamer = new FileStreamer(file, filesContentType, 
                                                 Caches::cacheForAsLongAsPossible);
        return streamer.streamOr404(response);
    }
    
    /**
     * Removes the file associated to a task if it is in the store; does nothing
     * otherwise.
     * @param taskId the ID of the task associated to the file.
     * @return a 204 response to the client.
     */
    public ResponseEntity<?> delete(String taskId) {
        service.remove(taskIdFromString(taskId)); 
        return _204();
    }
    
}
