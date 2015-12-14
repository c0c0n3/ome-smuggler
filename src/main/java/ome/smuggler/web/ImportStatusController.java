package ome.smuggler.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import util.servlet.http.Caches;


@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(ImportController.ImportUrl)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportStatusController {

    private static final String ImportIdPathVar = "importId";
    
    @RequestMapping(method = GET, value = "{" + ImportIdPathVar + "}") 
    public ResponseEntity<String> getStatusUpdate(
            @PathVariable(value=ImportIdPathVar) String importId, 
            HttpServletResponse response) throws IOException {
        Path importLog = Paths.get("import/log/" + importId);
        FileStreamer streamer = new FileStreamer(importLog, 
                                                 MediaType.TEXT_PLAIN, 
                                                 Caches::doNotCache);
        return streamer.streamOr404(response);
    }
    
}
