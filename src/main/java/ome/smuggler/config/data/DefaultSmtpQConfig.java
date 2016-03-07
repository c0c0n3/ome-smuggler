package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.SmtpQConfig;

import util.config.ConfigProvider;

/**
 * Hard-coded import queue configuration.
 */
public class DefaultSmtpQConfig implements ConfigProvider<SmtpQConfig> {

    @Override
    public Stream<SmtpQConfig> readConfig() throws Exception {
        String address = "ome/smtp";
        String name = address;
        String filter = "";
        boolean durable = true;
        
        return Stream.of(new SmtpQConfig(address, name, filter, durable));
    }

}
