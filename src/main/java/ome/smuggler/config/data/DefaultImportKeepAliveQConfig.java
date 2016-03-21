package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.ImportKeepAliveQConfig;
import util.config.ConfigProvider;

/**
 * Hard-coded import keep-alive queue configuration.
 */
public class DefaultImportKeepAliveQConfig 
    implements ConfigProvider<ImportKeepAliveQConfig> {

    @Override
    public Stream<ImportKeepAliveQConfig> readConfig() throws Exception {
        String address = "ome/import/keep-alive";
        String name = address;
        String filter = "";
        boolean durable = true;
        
        return Stream.of(new ImportKeepAliveQConfig(
                                            address, name, filter, durable));
    }

}