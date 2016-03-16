package integration.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.BaseStringId;
import ome.smuggler.web.TaskFileStoreAdapter;


@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(TaskFileStoreController.RootPath)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class TaskFileStoreController {

    public static final String RootPath = "/ome/test/task";
    private static final String TaskIdPathVar = "taskId";
    
    @Autowired
    private TaskFileStore<BaseStringId> service;
    
    private TaskFileStoreAdapter<BaseStringId> newAdapter() {
        return new TaskFileStoreAdapter<>(service, MediaType.TEXT_PLAIN, 
                URI.create(RootPath), BaseStringId::new);
    }
    
    @RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE) 
    public String[] listTaskIds() {
        return newAdapter().listTaskIds();
    }
    
    @RequestMapping(method = GET, value = "{" + TaskIdPathVar + "}") 
    public ResponseEntity<String> streamFileOr404(
            @PathVariable(value=TaskIdPathVar) String taskId, 
            HttpServletResponse response) throws IOException {
        return newAdapter().streamFileOr404(taskId, response);
    }
    
    @RequestMapping(method = DELETE, value = "{" + TaskIdPathVar + "}") 
    public ResponseEntity<?> deleteFile(
            @PathVariable(value=TaskIdPathVar) String taskId) {
        return newAdapter().delete(taskId);
    }
    
}
