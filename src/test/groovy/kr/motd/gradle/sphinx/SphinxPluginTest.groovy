package kr.motd.gradle.sphinx

import kr.motd.gradle.sphinx.gradle.SphinxTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class SphinxPluginTest {
    Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
    }

    @After
    void tearDown() {
        project.ant.delete(dir: project.projectDir, deleteonexit: true)
    }

    @Test
    void testTaskRegistration() {
        project.pluginManager.apply 'kr.motd.sphinx'
        assert project.tasks.sphinx instanceof SphinxTask
        assert project.tasks.site.dependsOn.contains(project.tasks.sphinx)
    }

    @Test
    void testTaskRegistrationWithExistingSiteTask() {
        project.task('site')
        project.pluginManager.apply 'kr.motd.sphinx'
        assert project.tasks.sphinx instanceof SphinxTask
        assert !project.tasks.site.dependsOn.contains(project.tasks.sphinx)
    }
}
