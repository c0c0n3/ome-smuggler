package ome.smuggler.web;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import ome.smuggler.core.io.FileOps;
import ome.smuggler.core.types.Nat;

/**
 * Streams a file to the HTTP response body or sends a 404 if the file does not
 * exist.
 */
public class FileStreamer {

    private final Path content;
    private final MediaType contentType;
    private final Consumer<HttpServletResponse> cacheStrategy;
    
    public FileStreamer(Path content, MediaType contentType, 
                        Consumer<HttpServletResponse> cacheStrategy) {
        requireNonNull(content, "content");
        requireNonNull(contentType, "contentType");
        requireNonNull(cacheStrategy, "cacheStrategy");
        
        this.content = content;
        this.contentType = contentType;
        this.cacheStrategy = cacheStrategy;
    }
    
    private void stream(HttpServletResponse response) throws IOException {
        Nat contentSize = FileOps.byteLength(content);  
        
        if (contentSize.get() <= Integer.MAX_VALUE) {
            response.setContentLength(contentSize.get().intValue());
        }  // (1)
        
        response.setContentType(contentType.toString());
        cacheStrategy.accept(response);
        
        ServletOutputStream out = response.getOutputStream();
        FileOps.transfer(content, contentSize, out);  // (2)
    }
    /* NOTES.
     * 1. Else can't set; will result in chunked transfer encoding.
     * To see this, use a 200M file without setting the content-length; then the 
     * servlet container splits the content into 16KiB chunks (HEX size = 4000).
     * 2. The file is possibly being written to, so we can't just use Files.copy
     * as it may write more bytes than we declared for the content-length.
     */
    
    /**
     * Streams the file to the client or sends back a 404 if the file does not
     * exist.
     * @param response the response to send to the client.
     * @throws IOException if an I/O error occurs.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void streamOr404(HttpServletResponse response) throws IOException {
        requireNonNull(response, "response");
        
        if(Files.exists(content)) {
            stream(response);
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), 
                               HttpStatus.NOT_FOUND.getReasonPhrase());
        }
    }
    
}
