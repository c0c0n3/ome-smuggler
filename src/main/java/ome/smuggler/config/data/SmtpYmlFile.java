package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.SmtpConfig;
import util.config.ConfigProvider;

/**
 * The data that goes into 'config/smtp.yml'.
 */
public class SmtpYmlFile implements ConfigProvider<SmtpConfig> {

    @Override
    public Stream<SmtpConfig> readConfig() {
        SmtpConfig cfg = new SmtpConfig();
        cfg.setHost("localhost");
        cfg.setPort(25);
        cfg.setFromAddress("omero-noreply@mri.cnrs.fr");
        
        return Stream.of(cfg);
    }

}
