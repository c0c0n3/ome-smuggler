package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import ome.smuggler.web.ImportController;
import ome.smuggler.web.ImportRequest;
import ome.smuggler.web.ImportResponse;

public class VerifyImportTest extends BaseWebTest {

    private static ImportRequest buildValidRequestThatWillFail() {
        ImportRequest req = new ImportRequest();
        req.experimenterEmail = "x@y";
        req.targetUri = "my/file";
        req.omeroHost = "h";
        req.omeroPort = "1";
        req.sessionKey = "k";
        
        return req;
    }
    
    private URI checkAndGetStatusUri(ResponseEntity<ImportResponse> response) {
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        
        String statusUri = response.getBody().statusUri;
        return url(statusUri);
    }
    
    private void checkCanGetStatusUpdates(URI statusUri) {
        ResponseEntity<String> response = 
                httpClient.getForEntity(statusUri, String.class);
        
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType(), 
                   is(MediaType.TEXT_PLAIN));
    }
    
    @Test
    public void postImportThatWillFail() {
        ResponseEntity<ImportResponse> response = 
                post(url(ImportController.ImportUrl), 
                     buildValidRequestThatWillFail(), 
                     ImportResponse.class);
        
        URI statusUri = checkAndGetStatusUri(response);
        checkCanGetStatusUpdates(statusUri);
    }
    
}
