package integration.config;

import static org.junit.Assert.*;
import static util.spring.io.ResourceLocation.classpath;
import static util.spring.io.ResourceLocation.filepathFromCwd;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.test.OutputCapture;

import util.config.ConfigProvider;
import util.spring.io.ResourceLocation;
import ome.smuggler.config.providers.PriorityConfigProvider;
import ome.smuggler.run.RunnableApp;


public abstract class ConfigFileTest<T> {
    
    @Rule
    public final OutputCapture generatedConfig = new OutputCapture();
    
    @Rule
    public final TemporaryFolder configDirUnderPwd = new TemporaryFolder(new File("./"));
    
    protected abstract PriorityConfigProvider<T> getConfigProvider();
    
    protected abstract RunnableApp getFileGenerator();
    
    protected abstract ConfigProvider<T> getFileContents();
    
    protected String generateFile() {
        getFileGenerator().run(null);
        
        String fileContents = generatedConfig.toString();
        return fileContents;
    }
    
    protected ResourceLocation writeFile() throws IOException {
        String fileContents = generateFile();
        
        String fileName = getConfigProvider().getConfigFileName();
        File configFile = configDirUnderPwd.newFile(fileName);
        PrintWriter out = new PrintWriter(configFile);
        out.print(fileContents);
        out.close();
        
        String configDirName = configDirUnderPwd.getRoot().getName();
        return filepathFromCwd(configDirName, fileName);
    }
    
    @Test
    public void readConfigFileFromConfigDirUnderPwd() throws Exception {
        ResourceLocation pathRelativeToPwd = writeFile();
        
        Object[] actual = getConfigProvider()
                         .readConfig(pathRelativeToPwd)
                         .toArray();
        Object[] expected = getFileContents().readConfig().toArray();
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void defaultToHardCodedConfigIfNoOtherAvailable() throws Exception {
        
        Object[] actual = getConfigProvider()
                         .readConfig(classpath("some"), classpath("nonsense"))
                         .toArray();
        Object[] expected = getConfigProvider().getFallback().toArray();
        
        assertArrayEquals(expected, actual);
    }
    
}
