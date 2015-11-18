package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import ome.smuggler.web.Error;
import ome.smuggler.web.ImportController;
import ome.smuggler.web.ImportRequest;
import ome.smuggler.web.ImportResponse;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


public class EnqueueImportTest extends BaseWebTest {

    private static ImportRequest buildMinValidRequest() {
        ImportRequest req = new ImportRequest();
        req.experimenterEmail = "x@y";
        req.targetUri = "my/file";
        req.omeroHost = "h";
        req.omeroPort = "1";
        req.sessionKey = "k";
        
        return req;
    }
    
    /* curl -v -H 'Accept: application/json' 
     *         -H 'Content-Type: application/json'  
     *         -X POST -d '{"experimenterEmail": "x@y", 
     *                      "targetUri":"my/file", 
     *                      "omeroHost":"h", 
     *                      "omeroPort":"1", 
     *                      "sessionKey":"k"}' 
     *         http://localhost:8080/ome/import
     */
    @Test
    public void postValidRequest() {
        ResponseEntity<ImportResponse> response = 
                post(url(ImportController.ImportUrl), 
                     buildMinValidRequest(), 
                     ImportResponse.class);
        
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        
        String statusUri = response.getBody().statusUri;
        assertThat(statusUri, is(not(isEmptyOrNullString())));
        assertTrue(URI.create(statusUri).isAbsolute());
    }
    
    /* curl -v -H 'Accept: application/json' 
     *         -H 'Content-Type: application/json'  
     *         -X POST -d '{}' 
     *         http://localhost:8080/ome/import
     */
    @Test
    public void postRequestWithoutRequiredFields() {
        ResponseEntity<Error> response = 
                post(url(ImportController.ImportUrl), 
                     "{}", 
                     Error.class);
        
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().reason, is(not(isEmptyOrNullString())));
    }
    
    /* curl -v -H 'Accept: application/json' 
     *         -H 'Content-Type: application/json' 
     *         -X POST -d '' 
     *         http://localhost:8080/ome/import
     */
    @Test
    public void postRequestWithEmptyBody() {
        ResponseEntity<String> response = 
                post(url(ImportController.ImportUrl), 
                     "", 
                     String.class);
        
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), 
                   containsString("HttpMessageNotReadableException"));
    }
    /* NB Our controller is not even hit, Spring MVC handles this internally so
     * we can't return an instance of our Error class!
     */
    
    /* curl -v -H 'Accept: * / *' 
     *         -H 'Content-Type: application/json'  
     *         -X POST -d '{"experimenterEmail": "x@y", 
     *                      "targetUri":"/", 
     *                      "omeroHost":"h", 
     *                      "omeroPort":"1", 
     *                      "sessionKey":"k"}' 
     *         http://localhost:8080/ome/import
     */
    @Test
    public void postValidRequestWithAcceptAll() {
        ResponseEntity<String> response = 
                post(url(ImportController.ImportUrl), 
                     buildMinValidRequest(), 
                     String.class,
                     hs -> hs.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE));
        
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
    }
    /* NB this is weird, perhaps a Spring MVC bug! Our controller is not even 
     * called, I'd have expected the same outcome as in postValidRequest().
     */
}
