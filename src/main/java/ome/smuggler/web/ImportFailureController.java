package ome.smuggler.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static util.spring.http.ResponseEntities._204;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import ome.smuggler.core.service.ImportTracker;
import ome.smuggler.core.types.ImportId;
import util.servlet.http.Caches;

@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(ImportFailureController.FailedImportUrl)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportFailureController {

    public static final String FailedImportUrl = "/ome/failed/import";
    private static final String ImportIdPathVar = "failedImportId";
    
    @Autowired
    private ImportTracker service;
    
    private String toUrlString(ImportId task) {
        return UriComponentsBuilder.newInstance()
                                   .path(FailedImportUrl)
                                   .path("/")
                                   .path(task.id())
                                   .toUriString();
    }
    
    @RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE) 
    public String[] getFailedImportList() {
        return service.listFailedImports()
                      .map(this::toUrlString)
                      .toArray(String[]::new);
    }
    
    @RequestMapping(method = GET, value = "{" + ImportIdPathVar + "}") 
    public ResponseEntity<String> getFailedImportLog(
            @PathVariable(value=ImportIdPathVar) String failedImportId, 
            HttpServletResponse response) throws IOException {
        ImportId taskId = new ImportId(failedImportId);
        Path importLog = service.failedImportLogPathFor(taskId); 
        FileStreamer streamer = new FileStreamer(importLog, 
                                                 MediaType.TEXT_PLAIN, 
                                                 Caches::cacheForAsLongAsPossible);
        return streamer.streamOr404(response);
    }
    
    @RequestMapping(method = DELETE, value = "{" + ImportIdPathVar + "}") 
    public ResponseEntity<?> deleteFailedImportLog(
            @PathVariable(value=ImportIdPathVar) String failedImportId) {
        ImportId taskId = new ImportId(failedImportId);
        service.stopTrackingFailedImport(taskId); 
        return _204();
    }
    
}
