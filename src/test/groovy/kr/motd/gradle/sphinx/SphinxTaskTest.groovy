package kr.motd.gradle.sphinx

import kr.motd.gradle.sphinx.gradle.SphinxTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class SphinxTaskTest {
    Project project
    SphinxTask task

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        task = project.task('sphinx', type: SphinxTask) as SphinxTask
    }

    @After
    void tearDown() {
        project.ant.delete(dir: project.projectDir, deleteonexit: true)
    }

    @Test
    void testDefaultProperties() {
        assert !"${task.sourceDirectory}".contains('${')
        assert !"${task.outputDirectory}".contains('${')
        assert task.builder == 'html'
        assert task.tags.isEmpty()
        assert task.verbose
        assert !task.warningsAsErrors
    }

    @Test
    void testOverriddenProperties() {
        task.sourceDirectory "${project.projectDir}/src/my/sphinx"
        task.outputDirectory "${project.buildDir}/my/sphinx"
        task.builder 'mo'
        task.tags 'foo', 'bar'
        task.verbose false
        task.warningsAsErrors true

        assert "${task.getSourceDirectory()}" ==
                "${project.projectDir}${File.separator}src${File.separator}my${File.separator}sphinx"
        assert "${task.getOutputDirectory()}" ==
                "${project.buildDir}${File.separator}my${File.separator}sphinx"

        assert task.builder == 'mo'
        assert task.tags == [ "foo", "bar" ]
        assert !task.verbose
        assert task.warningsAsErrors
    }

    @Test
    void testGeneration() {
        def sourceDir = ['../../../..', '../../..'].stream()
                .map({ "${getClass().protectionDomain.codeSource.location.path}$it/src/site/sphinx" })
                .filter({ new File(it).isDirectory() })
                .findFirst().get()

        project.ant.mkdir(dir: "${task.sourceDirectory}")
        project.ant.copy(todir: "${task.sourceDirectory}") {
            fileset(dir: sourceDir)
        }

        task.binaryCacheDir = new File(".gradle/sphinx-binary")
        task.tags({ ["tagFoo", "tagBar"] })
        task.run()

        assert new File("${task.outputDirectory}/index.html").exists()
        assert new File("${task.outputDirectory}/.doctrees").exists()
        assert new File("${task.outputDirectory}/.buildinfo").exists()
    }
}
