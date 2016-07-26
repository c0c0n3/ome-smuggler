package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.BaseStringId;
import util.lambda.ConsumerE;

public class TaskIdPathStoreTest {

    @Rule
    public final TemporaryFolder storeDir = new TemporaryFolder();

    private TaskFileStore<BaseStringId> target;
    
    private BaseStringId addNewTaskIdFileToStore() throws IOException {
        BaseStringId taskId = new BaseStringId();
        target.add(taskId, taskId.toString());
        
        return taskId;
    }
    
    @Before
    public void setup() {
        Path p = Paths.get(storeDir.getRoot().getPath());
        target = new TaskIdPathStore<>(p, BaseStringId::new);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfFirstArgNull() {
        new TaskIdPathStore<>(null, BaseStringId::new);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfSecondArgNull() {
        new TaskIdPathStore<>(Paths.get(""), null);
    }
    
    @Test(expected = NullPointerException.class)
    public void pathForThrowsIfNullArg() {
        target.pathFor(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void removeThrowsIfNullArg() {
        target.remove(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfFirstArgNull() {
        target.add(null, out -> {});
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfSecondArgNull() {
        target.add(new BaseStringId(), (ConsumerE<OutputStream>)null);
    }
    
    @Test
    public void storeInNewDirWillBeInitiallyEmpty() {
        assertThat(target.listTaskIds().count(), is(0L));
    }

    @Test
    public void requestingPathDoesntCreateFile() {
        Path p = target.pathFor(new BaseStringId());
        assertFalse(Files.exists(p));
        assertThat(target.listTaskIds().count(), is(0L));
    }
    
    @Test
    public void taskIdListedAfterCreatingFile() throws IOException {
        BaseStringId taskId = addNewTaskIdFileToStore();
        Path taskIdPath = target.pathFor(taskId);
        
        assertTrue(Files.exists(taskIdPath));
        assertThat(target.listTaskIds().count(), is(1L));
        assertThat(target.listTaskIds().findFirst().get(), is(taskId));
    }
    
    @Test
    public void nonTaskIdFilesWouldBeListedToo() throws IOException {
        addNewTaskIdFileToStore();
        storeDir.newFile();
        
        assertThat(target.listTaskIds().count(), is(2L));
    }
    
    @Test
    public void removeDoesNothingIfFileDoesntExist() throws IOException {
        BaseStringId existingTaskId = addNewTaskIdFileToStore();
        BaseStringId nonExistentTaskId = new BaseStringId();
        
        target.remove(nonExistentTaskId);
        
        assertThat(target.listTaskIds().count(), is(1L));
        assertThat(target.listTaskIds().findFirst().get(), is(existingTaskId));
    }
    
    @Test
    public void removeTaskIdFile() throws IOException {
        BaseStringId taskId1 = addNewTaskIdFileToStore();
        BaseStringId taskId2 = addNewTaskIdFileToStore();
        
        assertThat(target.listTaskIds().count(), is(2L));
        
        target.remove(taskId1);
        
        assertThat(target.listTaskIds().count(), is(1L));
        assertThat(target.listTaskIds().findFirst().get(), is(taskId2));
    }

    @Test
    public void replaceFileContent() throws IOException {
        BaseStringId taskId = addNewTaskIdFileToStore();
        String replacement = "new";
        target.replace(taskId, currentValue -> {
            assertThat(currentValue, is(taskId.get()));
            return replacement;
        });
        target.replace(taskId, currentValue -> {
            assertThat(currentValue, is(replacement));
            return "";
        });
    }

}
