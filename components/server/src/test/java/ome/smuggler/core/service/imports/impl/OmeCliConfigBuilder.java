package ome.smuggler.core.service.imports.impl;

import ome.smuggler.config.data.DefaultOmeCliConfig;
import ome.smuggler.config.items.OmeCliConfig;

public class OmeCliConfigBuilder {

    public static OmeCliConfig config() {
        OmeCliConfig cfg = new DefaultOmeCliConfig()
                .defaultReadConfig()
                .findFirst()
                .get();
        cfg.setOmeLibDirPath("../../gradle");  // (*)

        return cfg;
    }
    /* (*) Avoid Cmd Builders bomb out on empty classpath.
     * We need to specify a directory with at least one jar file in it or
     * in its subdirectories, which jar doesn't matter for our tests here.
     * Gradle always cd's in the project directory so the path above points
     * to the 'gradle' directory in our root.
     */
}
