package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.MailConfig;
import util.config.ConfigProvider;

/**
 * The data that goes into 'config/mail.yml'.
 */
public class MailYmlFile implements ConfigProvider<MailConfig> {

    public static String DeadMailDirRelPath = "mail/failed";
    
    @Override
    public Stream<MailConfig> readConfig() {
        MailConfig cfg = new MailConfig();
        cfg.setMailServerHost("localhost");
        cfg.setMailServerPort(25);
        cfg.setFromAddress("omero-noreply@mri.cnrs.fr");
        cfg.setRetryIntervals(new Long[] { 10L, 10L, 120L, 1440L, 1440L, 1440L });
        cfg.setDeadMailDir(DeadMailDirRelPath);
        
        return Stream.of(cfg);
    }

}
