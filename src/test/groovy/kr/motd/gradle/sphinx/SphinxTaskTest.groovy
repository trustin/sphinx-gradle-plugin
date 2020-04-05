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
        assert task.traceback
        assert !task.force
        assert !task.skip
        assert !task.warningsAsErrors
        assert !task.useDoctreeCache
        assert task.doctreeCacheDirectory == null
        assert !task.useMakeMode
    }

    @Test
    void testOverriddenProperties() {
        task.sourceDirectory "${project.projectDir}/src/my/sphinx"
        task.outputDirectory "${project.buildDir}/my/sphinx"
        task.binaryCacheDir "${project.buildDir}/my/cache"
        task.builder 'mo'
        task.tags 'foo', 'bar'
        task.verbose false
        task.traceback false
        task.force true
        task.skip true
        task.warningsAsErrors true
        task.useDoctreeCache true
        task.useMakeMode true

        assert "${task.sourceDirectory}" ==
                "${project.projectDir}${File.separator}src${File.separator}my${File.separator}sphinx"
        assert "${task.outputDirectory}" ==
                "${project.buildDir}${File.separator}my${File.separator}sphinx"
        assert "${task.binaryCacheDir}" ==
                "${project.buildDir}${File.separator}my${File.separator}cache"
        assert task.binaryCacheDir.isDirectory()

        assert task.builder == 'mo'
        assert task.tags == [ "foo", "bar" ]
        assert !task.verbose
        assert !task.traceback
        assert task.force
        assert task.skip
        assert task.warningsAsErrors
        assert task.useDoctreeCache
        assert "${task.doctreeCacheDirectory}" ==
                "${project.buildDir}${File.separator}site${File.separator}.doctrees"
        assert task.useMakeMode

        task.doctreeCacheDirectory "${project.buildDir}/my/doctree-cache"
        assert task.doctreeCacheDirectory.isDirectory()
        assert "${task.doctreeCacheDirectory}" ==
                "${project.buildDir}${File.separator}my${File.separator}doctree-cache"
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

        task.binaryCacheDir = new File(System.getProperty("user.home") +
                                       "/.gradle/caches/sphinx-binary")
        task.tags = { ["tagFoo"] }
        task.tags "tagBar"
        task.environments = { ['ENV_FOO': '1'] }
        task.env 'ENV_BAR', '2'
        task.run()

        assert new File("${task.outputDirectory}/index.html").exists()
        assert new File("${task.outputDirectory}/.doctrees").exists()
        assert new File("${task.outputDirectory}/.buildinfo").exists()
    }

    @Test
    void testDoctreeRedirect() {
        def sourceDir = ['../../../..', '../../..'].stream()
                .map({ "${getClass().protectionDomain.codeSource.location.path}$it/src/site/sphinx" })
                .filter({ new File(it).isDirectory() })
                .findFirst().get()

        project.ant.mkdir(dir: "${task.sourceDirectory}")
        project.ant.copy(todir: "${task.sourceDirectory}") {
            fileset(dir: sourceDir)
        }

        task.binaryCacheDir = new File(System.getProperty("user.home") +
                                       "/.gradle/caches/sphinx-binary")
        task.tags = { ["tagFoo"] }
        task.tags "tagBar"
        task.environments = { ['ENV_FOO': '1'] }
        task.env 'ENV_BAR', '2'
        task.doctreeCacheDirectory = new File("${task.outputDirectory}/redirectedDoctrees/")
        task.useDoctreeCache = true
        task.run()

        assert new File("${task.outputDirectory}/redirectedDoctrees").exists()
    }
}
