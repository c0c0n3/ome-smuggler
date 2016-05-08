package packager

import static util.Filez.*

/**
 * Defines the packager's staging directory layout.
 * We use this directory to collect the various bits and pieces that will then
 * be used to create the release bundles.
 */
class Staging {

    File buildDir, baseDir, binDir, configDir, dataDir, libDir, logDir,
            linuxDaemonDir

    Staging(File buildDir) {
        this.buildDir = buildDir
        baseDir = toFile(buildDir, 'staging')
        binDir = toFile(baseDir, 'bin')
        configDir = toFile(baseDir, 'config')
        dataDir = toFile(baseDir, 'data')
        libDir = toFile(baseDir, 'lib')
        logDir = toFile(baseDir, 'log')
        linuxDaemonDir = toFile(baseDir, 'linux-daemon')
    }

    def subdirs() {
        [binDir, configDir, dataDir, libDir, logDir, linuxDaemonDir]
    }

}
