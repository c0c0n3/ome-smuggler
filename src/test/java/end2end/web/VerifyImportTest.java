package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.unchecked;
import static util.error.Exceptions.runUnchecked;
import static end2end.web.Asserts.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.core.io.FileOps;
import ome.smuggler.web.ImportController;
import ome.smuggler.web.ImportFailureController;
import ome.smuggler.web.ImportRequest;
import ome.smuggler.web.ImportResponse;
import util.config.YamlConverter;

public class VerifyImportTest extends BaseWebTest {

    private static ImportConfig readImportConfigFromPwd() throws IOException {
        YamlConverter<ImportConfig> reader = new YamlConverter<>();
        FileInputStream importYml = new FileInputStream("import.yml");
        return reader.fromYaml(importYml, ImportConfig.class);
    }
    
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
    
    private URI canGetFailedImportLog(URI statusUri) {
        String importId = Paths.get(statusUri.getPath())
                               .getFileName().toString();
        String[] logs = getFailedLogUrls();
        Optional<URI> failedLogUri = Stream.of(logs)
                                           .filter(x -> x.endsWith(importId))
                                           .map(x -> url(x))
                                           .findFirst();
        assertTrue(failedLogUri.isPresent());
        
        return failedLogUri.get();
    }
    
    private void noFailedLogsAvailable() {
        String[] logs = getFailedLogUrls();
        assertThat(logs.length, is(0));
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
    
    private ImportConfig config;
    
    @Override
    protected void additionalSetup() {
        config = unchecked(() -> readImportConfigFromPwd()).get();
        Path failedImportLogDir = Paths.get(config.getFailedImportLogDir());
        FileOps.listChildFiles(failedImportLogDir)
               .forEach(log -> FileOps.delete(log));
    }
    
    @Test
    public void failedImportWorkflow() {
        ImportRequest doomedImportRequest = buildValidRequestThatWillFail();
        URI statusUri = requestImport(doomedImportRequest);
        canGetStatusUpdate(statusUri, doomedImportRequest);
        
        runUnchecked(() -> Thread.sleep(80 * 1000));  // (!)
        
        noMoreImportStatusUpdatesAfterLogRetentionPeriod(statusUri);
        URI failedLog = canGetFailedImportLog(statusUri);
        canDownloadFailedLog(failedLog, doomedImportRequest);
        canStopTrackingFailedLog(failedLog);
        cannotDownloadFailedLog(failedLog);
        noFailedLogsAvailable();  // (*)
    }
    /* (!) For what follows to work, there must be an import.yml in the pwd with
     * > logRetentionMinutes: 1
     * > retryIntervals: []
     * (*) this test will fail if there were failed log files before this method
     * ran which is why we delete any of them in the setup phase.
     * 
     * TODO: come up with a decent way of running this test!
     */
}
