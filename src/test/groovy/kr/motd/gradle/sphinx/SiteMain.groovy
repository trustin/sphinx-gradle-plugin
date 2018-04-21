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
        task.tags = { ["tagFoo"] }
        task.tags "tagBar"
        task.environments = { ['ENV_FOO':'1'] }
        task.env('ENV_BAR', '2')
        task.run()
    }
}
