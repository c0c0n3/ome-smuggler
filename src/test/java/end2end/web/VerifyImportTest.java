package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.runUnchecked;
import static end2end.web.Asserts.*;

import java.net.URI;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import ome.smuggler.web.ImportController;
import ome.smuggler.web.ImportFailureController;
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
    
    private static void assertImportLogResponse(
            ResponseEntity<String> response, ImportRequest expected) {
        assertStatusOk(response);
        assertPlainText(response);
        
        String importLog = response.getBody();
        assertThat(importLog, containsString(expected.targetUri));
        assertThat(importLog, containsString(expected.experimenterEmail));
    }
    
    private static void assertImportResponse(
            ResponseEntity<String> response, ImportRequest expected) {
        assertImportLogResponse(response, expected);
        assertNoCaching(response);
    }
    
    private static void assertFailedLogDownload(
            ResponseEntity<String> response, ImportRequest expected) {
        assertImportLogResponse(response, expected);
        assertCacheForAsLongAsPossible(response);
    }
    
    private URI requestImport(ImportRequest req) {
        ResponseEntity<ImportResponse> postImportResponse = 
                post(url(ImportController.ImportUrl), req, ImportResponse.class);
        assertStatusOk(postImportResponse);
        return url(postImportResponse.getBody().statusUri);
    }
    
    private void canGetStatusUpdate(URI statusUri, ImportRequest requested) {
        ResponseEntity<String> response = 
                httpClient.getForEntity(statusUri, String.class);
        assertImportResponse(response, requested);
    }
    
    private void noMoreImportStatusUpdatesAfterLogRetentionPeriod(URI statusUri) {
        ResponseEntity<String> statusUpdateResponse = 
                httpClient.getForEntity(statusUri, String.class);
        assert404(statusUpdateResponse);  // ==> log was garbage collected
    }
    
    private URI canGetFirstFailedImportLogUrl() {
        ResponseEntity<String[]> response = 
                httpClient.getForEntity(
                        url(ImportFailureController.FailedImportUrl), 
                        String[].class);
        assertStatusOk(response);
        
        String[] logs = response.getBody();
        assertNotNull(logs);
        assertThat(logs.length, greaterThan(0));
        
        return url(logs[0]);
    }
    
    private void canDownloadFailedLog(URI logUri, ImportRequest requested) {
        ResponseEntity<String> failedLogResponse = 
                httpClient.getForEntity(logUri, String.class);
        assertFailedLogDownload(failedLogResponse, requested);
    }
    
    @Test
    public void failedImportWorkflow() {
        ImportRequest doomedImportRequest = buildValidRequestThatWillFail();
        URI statusUri = requestImport(doomedImportRequest);
        canGetStatusUpdate(statusUri, doomedImportRequest);
        
        runUnchecked(() -> Thread.sleep(80 * 1000));  // (!)
        
        noMoreImportStatusUpdatesAfterLogRetentionPeriod(statusUri);
        URI failedLog = canGetFirstFailedImportLogUrl();
        canDownloadFailedLog(failedLog, doomedImportRequest);
    }
    /* (!) For what follows to work, there must be an import.yml in the pwd with
     * > logRetentionMinutes: 1
     * > retryIntervals: []
     * and the import/failed-log dir must be empty.
     * TODO: come up with a decent way of running this test!
     */
}
