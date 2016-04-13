package ome.smuggler.web.imports;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static util.spring.http.ResponseEntities.okOr400;

import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * Enqueues a message to request an OMERO import.
 */
@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(ImportController.ImportUrl)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportController {

    public static final String ImportUrl = "/ome/import";
    
    @Autowired
    private ImportRequestor service;
    
    private ImportInput buildInput(ImportRequest requestInput, 
                                   ImportRequestValidator validator) {
        return new ImportInput(
                validator.getEmail(), 
                validator.getTarget(), 
                validator.getOmero(), 
                validator.getSession())
            .setName(requestInput.name)
            .setDescription(requestInput.description)
            .setDatasetId(validator.getDatasetId())
            .setScreenId(validator.getScreenId())
            .addTextAnnotations(validator.getTextAnnotations().stream())
            .addAnnotationIds(validator.getAnnotationIds().stream());
    }
    
    private ImportResponse responseBody(ImportId task) {
        ImportResponse responseBody = new ImportResponse();
        responseBody.statusUri = UriComponentsBuilder.newInstance()
                                .path(ImportUrl)
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
    public ResponseEntity<Object> enqueue(@RequestBody ImportRequest data) { 
        ImportRequestValidator validator = new ImportRequestValidator();
        return okOr400(validator
                .validate(data)
                .map(x -> buildInput(data, validator))
                .map(service::enqueue)
                .map(this::responseBody)
                );
    }
    
}
