package ome.smuggler.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import ome.smuggler.core.data.ImportRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

/**
 * Enqueues a message to request an OMERO import.
 */
@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping("/ome/import")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportController {

    @RequestMapping(method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void enqueue(@RequestBody ImportRequest request) { 
        System.out.println(">>> host: " + request.omeroHost);
        System.out.println(">>>  key: " + request.sessionKey);
    }
    // curl -H 'Content-Type: application/json'  -X POST -d '' http://localhost:8080/ome/import
    // curl -H 'Content-Type: application/json'  -X POST -d '{}' http://localhost:8080/ome/import
    // curl -H 'Content-Type: application/json'  -X POST -d '{"omeroHost":"gauss"}' http://localhost:8080/ome/import
    // curl -H 'Content-Type: application/json'  -X POST -d '{"omeroHost":"gauss", "sessionKey":""}' http://localhost:8080/ome/import
    // NB first request above is silently discarded by spring booty so enqueue
    // is not called!
}
