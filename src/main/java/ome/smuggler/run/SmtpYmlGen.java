package ome.smuggler.run;

import java.util.List;

import ome.smuggler.config.data.SmtpYmlFile;
import ome.smuggler.config.items.SmtpConfig;
import util.config.YamlConverter;

/**
 * Run this class redirecting {@code stdout} to {@code config/smtp.yml} to 
 * generate the file. This way we can keep all config data in Java and avoid 
 * any deserialization issue.
 * <pre>
 * java -jar build/libs/ome-smuggler-0.1.0.jar ome.smuggler.run.SmtpYmlGen > src/main/resources/config/smtp.yml
 *</pre>
 */
public class SmtpYmlGen implements RunnableApp {

    /**
     * Dumps YAML config to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        SmtpConfig fileContents = new SmtpYmlFile()
                                     .readConfig()
                                     .findFirst()
                                     .get();
        String yaml = new YamlConverter<SmtpConfig>().toYaml(fileContents);
        System.out.print(yaml); 
    }

}
