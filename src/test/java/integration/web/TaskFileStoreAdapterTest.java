package integration.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class TaskFileStoreAdapterTest extends BaseWebTest {

    private static String taskIdUrl(String taskId) {
        return TaskFileStoreController.RootPath + "/" + taskId;
    }
    
    @Test
    public void fetchFile() throws Exception {
        String[] taskIds = doGetAndReadJson(TaskFileStoreController.RootPath, 
                                            String[].class);
        
        assertNotNull(taskIds);
        assertThat(taskIds.length, greaterThan(0));
    }
    
}
