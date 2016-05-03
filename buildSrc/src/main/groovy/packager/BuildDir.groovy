package packager

import static util.Filez.*


class BuildDir {

    File rootDir, distDir
    Staging staging

    BuildDir(File buildDir) {
        rootDir = buildDir
        distDir = toFile(buildDir, 'distributions')
        staging = new Staging(buildDir)
    }

    def subdirs() {
        [distDir] + staging.subdirs()
    }

}
