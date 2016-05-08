package packager

import org.gradle.api.tasks.bundling.AbstractArchiveTask

/**
 * Configures archive tasks to collect files from the packager's staging area,
 * depending on release type, e.g. generic release, windows service, etc.
 */
class ReleaseBundle {

    BuildDir dirs
    ReleaseInfo info

    ReleaseBundle(BuildDir dirs, ReleaseInfo info) {
        this.dirs = dirs
        this.info = info
    }

    def configureBase(AbstractArchiveTask task) {
        task.from dirs.staging.baseDir
        task.into info.baseName
        task.includeEmptyDirs = true
        task.destinationDir = dirs.distDir
        task.baseName = info.baseName
        task.version = info.version
        task.classifier = info.classifier
    }

    def configureGeneric(AbstractArchiveTask task) {
        configureBase(task)
        task.include '**/*.md', '**/*.sh', '**/*.bat', '**/*.jar'
    }

    def configureWinService(AbstractArchiveTask task) {
        configureBase(task)
        task.baseName = info.baseName + '-winsvc'
        task.include '**/*.md', '**/*.exe', '**/*.xml', '**/*.config', '**/*.jar'
    }

    def configureLinuxDaemon(AbstractArchiveTask task) {
        configureBase(task)
        task.baseName = info.baseName + '-linux-daemon'
        task.include '**/*.md', dirs.staging.linuxDaemonDir.name + '/*'
    }

}
