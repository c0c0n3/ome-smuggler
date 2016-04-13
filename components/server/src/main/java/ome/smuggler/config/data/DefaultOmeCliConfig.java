package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.OmeCliConfig;
import util.config.ConfigProvider;


/**
 * Hard-coded configuration for launching the OMERO CLI commands.
 */
public class DefaultOmeCliConfig 
    implements ConfigProvider<OmeCliConfig> {

    @Override
    public Stream<OmeCliConfig> readConfig() throws Exception {
        OmeCliConfig cfg = new OmeCliConfig();
        cfg.setOmeLibDirPath("ome-lib");
        cfg.setImporterMainClassFqn("ome.formats.importer.cli.CommandLineImporter");
        cfg.setKeepAliveMainClassFqn("ome.cli.omero.session.KeepAlive");
        
        return Stream.of(cfg);
    }

}
