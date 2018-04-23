package kr.motd.gradle.sphinx.gradle

import kr.motd.maven.sphinx.SphinxException
import kr.motd.maven.sphinx.SphinxRunner
import kr.motd.maven.sphinx.SphinxUtil
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.*

import java.util.concurrent.Callable

class SphinxTask extends DefaultTask {

    def binaryBaseUrl = { SphinxRunner.DEFAULT_BINARY_BASE_URL }
    def binaryVersion = { SphinxRunner.DEFAULT_BINARY_VERSION }
    def binaryCacheDir = { "${project.gradle.gradleUserHomeDir}/caches/sphinx-binary" }
    def environments = { Collections.emptyMap() }
    private final def additionalEnvironments = new HashMap<String, String>()
    def dotBinary = null
    def sourceDirectory = {
        "${project.projectDir}${File.separator}src${File.separator}site${File.separator}sphinx"
    }
    def outputDirectory = { "${project.buildDir}${File.separator}site" }
    def builder = { "html" }
    def tags = { Collections.emptyList() }
    private final def additionalTags = new ArrayList<String>()
    def verbose = { true }
    def force = { false }
    def warningsAsErrors = { false }
    def skip = { false }

    private static def unwrap(Object o) {
        if (o instanceof Callable) {
            return o.call()
        } else {
            return o
        }
    }

    @Input
    String getBinaryBaseUrl() {
        return String.valueOf(unwrap(binaryBaseUrl))
    }

    @Input
    String getBinaryVersion() {
        return String.valueOf(unwrap(binaryVersion))
    }

    @Input
    File getBinaryCacheDir() {
        return project.file(binaryCacheDir).getCanonicalFile()
    }

    @Input
    Map<String, String> getEnvironments() {
        def unwrapped = unwrap(environments)
        if (!(unwrapped instanceof Map)) {
            throw new InvalidUserDataException(
                    "environments must yield a Map<String, String>: ${unwrapped.getClass().name}")
        }

        Map<String, String> env = new HashMap<>()
        env.putAll(unwrapped)
        env.putAll(additionalEnvironments)
        return env
    }

    void env(String name, String value) {
        additionalEnvironments.put(name, value)
    }

    @Input
    @Optional
    String getDotBinary() {
        def unwrapped = unwrap(dotBinary)
        if (unwrapped == null) {
            return null
        } else {
            return String.valueOf(unwrapped)
        }
    }

    @SkipWhenEmpty
    @InputDirectory
    File getSourceDirectory() {
        project.file(sourceDirectory).getCanonicalFile()
    }

    @OutputDirectory
    File getOutputDirectory() {
        project.file(outputDirectory).getCanonicalFile()
    }

    @Input
    String getBuilder() {
        return String.valueOf(unwrap(builder))
    }

    @Input
    List<String> getTags() {
        def unwrapped = unwrap(tags)
        def result = new ArrayList<String>()
        if (unwrapped instanceof Iterable) {
            result.addAll(unwrapped)
        } else {
            result.add(String.valueOf(unwrapped))
        }
        result.addAll(additionalTags)
        return result
    }

    void tags(String... tags) {
        additionalTags.addAll(tags)
    }

    void tags(Iterable<String> tags) {
        tags.forEach { additionalTags.add(it) }
    }

    @Input
    boolean isVerbose() {
        (verbose instanceof Boolean ? verbose : verbose()).asBoolean()
    }

    @Input
    boolean isForce() {
        (force instanceof Boolean ? force : force()).asBoolean()
    }

    @Input
    boolean isWarningsAsErrors() {
        (warningsAsErrors instanceof Boolean ? warningsAsErrors : warningsAsErrors()).asBoolean()
    }

    @Input
    boolean isSkip() {
        (skip instanceof Boolean ? skip : skip()).asBoolean()
    }

    @TaskAction
    def run() {
        if (isSkip()) {
            logger.info("Skipping Sphinx execution.")
            return
        }

        SphinxRunner runner = new SphinxRunner(
                getBinaryBaseUrl(), getBinaryVersion(), getBinaryCacheDir(),
                getEnvironments(), getDotBinary(), { logger.lifecycle(it) })

        int result = runner.run(getSourceDirectory(), getSphinxRunnerCmdLine())
        if (result != 0) {
            throw new SphinxException("Sphinx exited with non-zero code: ${result}")
        }
        SphinxUtil.convertLineSeparators(getOutputDirectory())
    }

    /**
     * Build the Sphinx Command line options.
     */
    private List<String> getSphinxRunnerCmdLine() {
        List<String> args = []

        if (isVerbose()) {
            args.add('-v')
        } else {
            args.add('-Q')
        }

        if (isWarningsAsErrors()) {
            args.add('-W')
        }

        if (isForce()) {
            args.add('-a')
            args.add('-E')
        }

        for (String tag : getTags()) {
            args.add("-t")
            args.add(tag)
        }

        args.add('-n')

        args.add('-b')
        args.add(getBuilder())

        args.add(getSourceDirectory().getPath())
        args.add(getOutputDirectory().getPath())

        return args
    }
}
