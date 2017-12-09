package kr.motd.gradle.sphinx

import kr.motd.gradle.sphinx.gradle.SphinxTask
import org.gradle.testfixtures.ProjectBuilder

final class SiteMain {
    static void main(String[] args) {
        def project = ProjectBuilder.builder()
                                    .withName('sphinx-gradle-plugin-site')
                                    .withProjectDir(new File(args[0]))
                                    .withGradleUserHomeDir(new File(args[1])).build()

        def task = project.task('sphinx', type: SphinxTask) as SphinxTask
        task.tags({ ["tagFoo", "tagBar"] })
        task.run()
    }
}
