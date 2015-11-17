package integration.config;

import ome.smuggler.config.data.ImportLogYmlFile;
import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.config.providers.ImportLogConfigProvider;
import ome.smuggler.config.providers.PriorityConfigProvider;
import ome.smuggler.run.ImportLogYmlGen;
import ome.smuggler.run.RunnableApp;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import util.config.ConfigProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ImportLogConfigProvider.class)
public class ImportLogConfigFileTest extends ConfigFileTest<ImportLogConfig> {

    @Autowired
    private ImportLogConfigProvider configProvider;
    
    @Override
    protected PriorityConfigProvider<ImportLogConfig> getConfigProvider() {
        return configProvider;
    }

    @Override
    protected RunnableApp getFileGenerator() {
        return new ImportLogYmlGen();
    }

    @Override
    protected ConfigProvider<ImportLogConfig> getFileContents() {
        return new ImportLogYmlFile();
    }

}
