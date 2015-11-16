package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.CliImporterConfig;
import util.config.ConfigProvider;


/**
 * Hard-coded configuration for launching the OMERO CLI importer.
 */
public class DefaultCliImporterConfig 
    implements ConfigProvider<CliImporterConfig> {

    @Override
    public Stream<CliImporterConfig> readConfig() throws Exception {
        CliImporterConfig cfg = new CliImporterConfig();
        cfg.setMainClassFqn("ome.formats.importer.cli.CommandLineImporter");
        cfg.setOmeLibDirPath("ome-lib");
        
        return Stream.of(cfg);
    }

}
