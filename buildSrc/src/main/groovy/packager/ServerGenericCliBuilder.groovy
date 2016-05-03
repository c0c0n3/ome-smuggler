package packager

import java.nio.file.Path

/**
 * Builds the arguments to pass to the java command for starting the server.
 * Use for the scripts in the "generic" distribution.
 */
class ServerGenericCliBuilder {

    static final String configDirKey = 'ome.smuggler.ConfigDir'
    static final String dataDirKey = 'ome.smuggler.DataDir'
    static final String loggingPathKey = 'logging.path'

    static String sysPropArg(String name, String value) {
        String.format('-D%s="%s"', name, value)
    }

    static String jarArg(String path) {
        String.format('-jar "%s"', path)
    }

    static String configDirArg(String path) {
        sysPropArg(configDirKey, path)
    }

    static String datagDirArg(String path) {
        sysPropArg(dataDirKey, path)
    }

    static String logDirArg(String path) {
        sysPropArg(loggingPathKey, path)
    }

    static String buildWindowsArgs(BuildDir dirs, String serverJarName) {
        new ServerGenericCliBuilder('%APP_HOME%', '\\', dirs, serverJarName)
                .build()
    }

    static String buildUnixArgs(BuildDir dirs, String serverJarName) {
        new ServerGenericCliBuilder('$APP_HOME', '/', dirs, serverJarName)
                .build()
    }

    String appHomeVar
    String separator
    BuildDir dirs
    String serverJarName

    ServerGenericCliBuilder(String appHomeVar, String separator, BuildDir dirs,
                            String serverJarName) {
        this.appHomeVar = appHomeVar
        this.separator = separator
        this.dirs = dirs
        this.serverJarName = serverJarName
    }

    String path(p) {
        String.format('%s%s%s', appHomeVar, separator, p)
    }

    String jar() {
        jarArg(path(dirs.staging.libDir.name) + separator + serverJarName)
    }

    String config() {
        configDirArg(path(dirs.staging.configDir.name))
    }

    String data() {
        datagDirArg(path(dirs.staging.dataDir.name))
    }

    String log() {
        logDirArg(path(dirs.staging.logDir.name))
    }

    String build() {
        [config(), data(), log(), jar()].join(' ')
    }

    /*
    [ '@lib.dir.name@' : dirs.staging.libDir.name
    , '@jar.file.name@' : serverJarFile().name
    , '@config.dir.name@' : dirs.staging.configDir.name
    , '@data.dir.name@' : dirs.staging.dataDir.name
    , '@log.dir.name@' : dirs.staging.logDir.name
    */
    /*
JAR="$APP_HOME/@lib.dir.name@/@jar.file.name@"
CONFIG_DIR_OPT=-Dome.smuggler.ConfigDir="$APP_HOME/@config.dir.name@"
DATA_DIR_OPT=-Dome.smuggler.DataDir="$APP_HOME/@data.dir.name@"
LOG_DIR_OPT=-Dlogging.path="$APP_HOME/@log.dir.name@"

JAVA_OPTS=$CONFIG_DIR_OPT $DATA_DIR_OPT $LOG_DIR_OPT
exec "$JAVACMD" $JAVA_OPTS -jar $JAR "$@"
     */
}
