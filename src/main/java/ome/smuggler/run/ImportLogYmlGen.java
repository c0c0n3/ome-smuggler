package ome.smuggler.run;

import java.util.List;

import ome.smuggler.config.data.ImportLogYmlFile;
import ome.smuggler.config.items.ImportLogConfig;
import util.config.YamlConverter;

/**
 * Run this class redirecting {@code stdout} to {@code config/import-log.yml} to 
 * generate the file. This way we can keep all config data in Java and avoid 
 * any deserialization issue.
 * <pre>
 * java -jar build/libs/ome-smuggler-0.1.0.jar ome.smuggler.run.ImportLogYmlGen > src/main/resources/config/import-log.yml
 *</pre>
 */
public class ImportLogYmlGen implements RunnableApp {

    /**
     * Dumps YAML config to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        ImportLogConfig fileContents = new ImportLogYmlFile()
                                     .readConfig()
                                     .findFirst()
                                     .get();
        String yaml = new YamlConverter<ImportLogConfig>().toYaml(fileContents);
        System.out.print(yaml); 
    }

}
