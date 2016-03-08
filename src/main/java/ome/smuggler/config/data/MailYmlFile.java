package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.MailConfig;
import util.config.ConfigProvider;

/**
 * The data that goes into 'config/mail.yml'.
 */
public class MailYmlFile implements ConfigProvider<MailConfig> {

    @Override
    public Stream<MailConfig> readConfig() {
        MailConfig cfg = new MailConfig();
        cfg.setHost("localhost");
        cfg.setPort(25);
        cfg.setFromAddress("omero-noreply@mri.cnrs.fr");
        
        return Stream.of(cfg);
    }

}
