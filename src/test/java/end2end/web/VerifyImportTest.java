package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.runUnchecked;
import static end2end.web.Asserts.*;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import ome.smuggler.web.imports.ImportController;
import ome.smuggler.web.imports.ImportFailureController;
import ome.smuggler.web.imports.ImportRequest;
import ome.smuggler.web.imports.ImportResponse;


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
        assert200(response);
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
        assert200(postImportResponse);
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
    
    private String[] getFailedLogUrls() {
        ResponseEntity<String[]> response = 
                httpClient.getForEntity(
                        url(ImportFailureController.FailedImportUrl), 
                        String[].class);
        assert200(response);
        
        String[] logs = response.getBody();
        assertNotNull(logs);
        
        return logs;
    }
    
    private Optional<URI> findFailedLog(URI logUri) {
        String importId = Paths.get(logUri.getPath())
                               .getFileName()
                               .toString();
        String[] logs = getFailedLogUrls();
        return Stream.of(logs)
                     .filter(x -> x.endsWith(importId))
                     .map(x -> url(x))
                     .findFirst();
    }
    
    private URI canGetFailedImportLog(URI statusUri) {
        Optional<URI> failedLogUri = findFailedLog(statusUri);
        assertTrue(failedLogUri.isPresent());
        return failedLogUri.get();
    }
    
    private void canDownloadFailedLog(URI logUri, ImportRequest requested) {
        ResponseEntity<String> response = 
                httpClient.getForEntity(logUri, String.class);
        assertFailedLogDownload(response, requested);
    }
    
    private void cannotDownloadFailedLog(URI logUri) {
        ResponseEntity<String> response = 
                httpClient.getForEntity(logUri, String.class);
        assert404(response);
    }
    
    private void canStopTrackingFailedLog(URI logUri) {
        RequestEntity<Object> request = 
                new RequestEntity<>(HttpMethod.DELETE, logUri);
        ResponseEntity<Object> response = 
                httpClient.exchange(request, Object.class);
        assert204(response);
    }
    
    private void failedLogNoLongerTracked(URI logUri) {
        Optional<URI> failedLogUri = findFailedLog(logUri);
        assertFalse(failedLogUri.isPresent());
    }
    
    private void waitUntilPastLogRetentionPeriod() {
        long millis = config.importConfig.logRetentionPeriod()
                     .plusSeconds(60).toMillis();
        runUnchecked(() -> Thread.sleep(millis));
    }
    
    @Test
    public void failedImportWorkflow() {
        ImportRequest doomedImportRequest = buildValidRequestThatWillFail();
        URI statusUri = requestImport(doomedImportRequest);
        canGetStatusUpdate(statusUri, doomedImportRequest);
        
        waitUntilPastLogRetentionPeriod();  // (1)
        noMoreImportStatusUpdatesAfterLogRetentionPeriod(statusUri);
        
        URI failedLog = canGetFailedImportLog(statusUri);  // (2)
        canDownloadFailedLog(failedLog, doomedImportRequest);
        canStopTrackingFailedLog(failedLog);
        cannotDownloadFailedLog(failedLog);
        failedLogNoLongerTracked(failedLog);  // (3)
    }
    /* NOTES.
     * 1. Assumes the duration in config.importConfig is the same as that used 
     * by Spring; Config class takes care of that. 
     * 2. Assumes the import config used by Spring has no retry intervals so 
     * that the failed log is immediately available after the first failure;
     * this is why the BaseWebTest sets the Dev profile.
     * 3. Note that there may be more than one failed log. In fact, tests are
     * run concurrently and so EnqueueImportTest::postValidRequest may result
     * in a failed log being produced just before we ask Smuggler to list all
     * failed logs. So we can't just assume and check the returned list is [].
     */
}
