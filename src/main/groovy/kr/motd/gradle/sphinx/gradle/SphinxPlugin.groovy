package kr.motd.gradle.sphinx.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task

class SphinxPlugin implements Plugin<Project> {
    void apply(Project target) {
        def sphinxTask = target.task('sphinx', type: SphinxTask)
        def siteTask = target.tasks.find { v -> v.name == 'site' }
        if (siteTask == null) {
            target.task('site').dependsOn(sphinxTask)
        }
    }
}
