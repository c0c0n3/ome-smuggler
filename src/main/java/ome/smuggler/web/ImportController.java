package ome.smuggler.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;

import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.types.ImportId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Enqueues a message to request an OMERO import.
 */
@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping("/ome/import")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportController {

    @Autowired
    private ImportRequestor service;
    
    
    private ImportResponse responseBody(HttpServletRequest req, ImportId task) {
        HttpRequest request = new ServletServerHttpRequest(req);
        ImportResponse responseBody = new ImportResponse();
        responseBody.statusUri = UriComponentsBuilder
                                .fromHttpRequest(request)
                                .path("/")
                                .path(task.id())
                                .toUriString();
        return responseBody;
    }
    
    /**
     * Adds a request to the import queue.
     * The requested import will be queued and will be processed as soon as 
     * resources are available; this method returns immediately so that the 
     * client doesn't have to wait for the import to complete. 
     * An import outcome notification will be sent to the email address 
     * specified in the request. 
     * The request must contain a valid OMERO session key that the client has
     * acquired before hand; the corresponding OMERO session will be used to
     * import the data. However, the client can close its session as soon as
     * this web method returns.
     * @param request details what image data to import.
     */
    @RequestMapping(method = POST, 
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ImportResponse enqueue(HttpServletRequest request,
                                  @RequestBody ImportRequest data) { 
        System.out.println(">>> host: " + data.omeroHost);
        System.out.println(">>>  key: " + data.sessionKey);
        
        ImportId task = service.enqueue(null);
        return responseBody(request, task);
    }
    // curl -H 'Content-Type: application/json'  -X POST -d '' http://localhost:8080/ome/import
    // curl -H 'Content-Type: application/json'  -X POST -d '{}' http://localhost:8080/ome/import
    // curl -H 'Content-Type: application/json'  -X POST -d '{"omeroHost":"gauss"}' http://localhost:8080/ome/import
    // curl -H 'Content-Type: application/json'  -X POST -d '{"omeroHost":"gauss", "sessionKey":""}' http://localhost:8080/ome/import
    // NB first request above is silently discarded by spring booty so enqueue
    // is not called!
    
}
