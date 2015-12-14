package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.joining;
import static util.error.Exceptions.runUnchecked;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
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
    
    private static void assertStatusOk(ResponseEntity<?> response) {
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
    
    private static void assert404(ResponseEntity<?> response) {
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
    
    private static void assertNoCaching(ResponseEntity<?> response) {
        HttpHeaders hs = response.getHeaders();
        String cache = hs.get(HttpHeaders.CACHE_CONTROL)
                         .stream()
                         .collect(joining());
        assertThat(cache, containsString("no-cache"));
        assertThat(cache, containsString("no-store"));
        assertThat(hs.getPragma(), containsString("no-cache"));
        assertThat(hs.getFirst("Expires"), is("0"));
    }
    // http://stackoverflow.com/questions/49547/making-sure-a-web-page-is-not-cached-across-all-browsers
    // https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en
    
    private static void assertPlainText(ResponseEntity<?> response) {
        HttpHeaders hs = response.getHeaders();
        MediaType expected = new MediaType("text", "plain", StandardCharsets.UTF_8);
        assertThat(hs.getContentType(), is(expected));
    }
    
    private static void assertExpected(ResponseEntity<String> response, 
                                       ImportRequest expected) {
        assertStatusOk(response);
        assertNoCaching(response);
        assertPlainText(response);
        
        String importLog = response.getBody();
        assertThat(importLog, containsString(expected.targetUri));
        assertThat(importLog, containsString(expected.experimenterEmail));
    }
    
    @Test
    public void postImportThatWillFail() {
        ImportRequest requestData = buildValidRequestThatWillFail(); 
        ResponseEntity<ImportResponse> postImportResponse = 
                post(url(ImportController.ImportUrl), requestData, 
                        ImportResponse.class);

        assertStatusOk(postImportResponse);
        
        URI statusUri = url(postImportResponse.getBody().statusUri);
        ResponseEntity<String> statusUpdateResponse = 
                httpClient.getForEntity(statusUri, String.class); 
        
        assertExpected(statusUpdateResponse, requestData);
        
        runUnchecked(() -> Thread.sleep(80 * 1000));  // (!)
        statusUpdateResponse = httpClient.getForEntity(statusUri, String.class);
        
        assert404(statusUpdateResponse);  // ==> log was garbage collected
    }
    /* (!) For what follows to work, there must be an import.yml in the pwd with
     * > logRetentionMinutes: 1
     * > retryIntervals: []
     * TODO: come up with a decent way of running this test!
     */
}
