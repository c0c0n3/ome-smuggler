package packager

import static util.Filez.*


class Staging {

    File buildDir, baseDir, binDir, configDir, dataDir, libDir, logDir

    Staging(File buildDir) {
        this.buildDir = buildDir
        baseDir = toFile(buildDir, 'staging')
        binDir = toFile(baseDir, 'bin')
        configDir = toFile(baseDir, 'config')
        dataDir = toFile(baseDir, 'data')
        libDir = toFile(baseDir, 'lib')
        logDir = toFile(baseDir, 'log')
    }

    def subdirs() {
        [binDir, configDir, dataDir, libDir, logDir]
    }

}
