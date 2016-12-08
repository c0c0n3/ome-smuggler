package ome.smuggler.core.types;

import java.time.Duration;

/**
 * Default values for OMERO configuration items.
 */
public class OmeroDefault {

    /**
     * Default OMERO server port: 4064.
     */
    public static final PositiveN Port = PositiveN.of(4064);

    /**
     * Default OMERO session timeout: 10 minutes.
     */
    public static final Duration SessionTimeout = Duration.ofMinutes(10);
}
