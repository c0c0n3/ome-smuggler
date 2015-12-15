package end2end.web;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import ome.smuggler.config.data.DefaultHornetQPersistenceConfig;
import ome.smuggler.config.items.HornetQPersistenceConfig;
import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.config.providers.HornetQPersistenceConfigProvider;
import ome.smuggler.config.providers.ImportConfigProvider;
import util.config.YamlConverter;

public class Config {

    private static <T> void writeConfig(String fileName, T fileContents) 
            throws IOException {
        String yaml = new YamlConverter<>().toYaml(fileContents);
        Path outputFile = Paths.get(fileName);
        List<String> content = Arrays.asList(yaml);
        Files.write(outputFile, content, CREATE, WRITE, TRUNCATE_EXISTING);
    }
    
    private static ImportConfig buildImportConfig(Path baseDataDir) {
        ImportConfig cfg = new ImportConfig();
        cfg.setImportLogDir(baseDataDir.resolve("import/log").toString());
        cfg.setFailedImportLogDir(baseDataDir.resolve("import/failed-log").toString());
        cfg.setLogRetentionMinutes(1L);
        cfg.setRetryIntervals(new Long[0]);
        
        return cfg;
    }
    
    public final Path baseDataDir;
    public final ImportConfig importConfig;
    public final HornetQPersistenceConfig hornetQPersistenceConfig;
    
    public Config() throws IOException {
        baseDataDir = Files.createTempDirectory("smuggler-tests");
        importConfig = buildImportConfig(baseDataDir);
        hornetQPersistenceConfig = 
                DefaultHornetQPersistenceConfig.build(baseDataDir); 
    }
    
    public void writeConfigFilesInPwd() throws IOException {
        writeConfig(HornetQPersistenceConfigProvider.FileName,
                    hornetQPersistenceConfig);
        writeConfig(ImportConfigProvider.FileName, importConfig);
    }
    
}
