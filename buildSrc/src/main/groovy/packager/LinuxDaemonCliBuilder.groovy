package packager

import static ServerGenericCliBuilder.sysPropArg
import static ServerGenericCliBuilder.configDirArg
import static ServerGenericCliBuilder.dataDirArg

/**
 * Builds the arguments to pass to the java command for starting the Spring Boot
 * Linux daemon.
 * Use for the scripts in the "linux-daemon" distribution.
 */
class LinuxDaemonCliBuilder {

    static final String logFilePathKey = 'logging.file'

    static String logFileArg(String path) {
        sysPropArg(logFilePathKey, path)
    }

    ReleaseInfo info

    LinuxDaemonCliBuilder(ReleaseInfo info) {
        this.info = info
    }

    String config() {
        configDirArg('/etc/opt/' + info.baseName + '.d')
    }

    String data() {
        dataDirArg('/var/opt/' + info.baseName)
    }

    String log() {
        logFileArg('/var/log/' + info.baseName + '.log')
    }

    String build() {
        [config(), data(), log()].join(' ')
    }

}
