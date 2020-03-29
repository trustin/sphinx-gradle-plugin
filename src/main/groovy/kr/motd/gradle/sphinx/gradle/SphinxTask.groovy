package kr.motd.gradle.sphinx.gradle

import kr.motd.maven.sphinx.SphinxException
import kr.motd.maven.sphinx.SphinxRunner
import kr.motd.maven.sphinx.SphinxUtil
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.*

import java.util.concurrent.Callable

class SphinxTask extends DefaultTask {

    private def binaryUrl = { SphinxRunner.DEFAULT_BINARY_URL }
    private def binaryCacheDir = { "${project.gradle.gradleUserHomeDir}/caches/sphinx-binary" }
    private def environments = { Collections.emptyMap() }
    private final def additionalEnvironments = new HashMap<String, String>()
    private def dotBinary = null
    private def sourceDirectory = {
        "${project.projectDir}${File.separator}src${File.separator}site${File.separator}sphinx"
    }
    private def outputDirectory = { "${project.buildDir}${File.separator}site" }
    private def doctreeCacheDirectory = { "${project.buildDir}${File.separator}site${File.separator}.doctrees" }
    private def builder = { "html" }
    private def tags = { Collections.emptyList() }
    private final def additionalTags = new ArrayList<String>()
    private def verbose = { true }
    private def traceback = { true }
    private def force = { false }
    private def warningsAsErrors = { false }
    private def skip = { false }
    private def useDoctreeCache = { false }

    private static def unwrap(Object o) {
        if (o instanceof Callable) {
            return o.call()
        } else {
            return o
        }
    }

    @Input
    String getBinaryUrl() {
        return String.valueOf(unwrap(binaryUrl))
    }

    void setBinaryUrl(Object binaryUrl) {
        this.binaryUrl = binaryUrl
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File getBinaryCacheDir() {
        return project.file(binaryCacheDir).getCanonicalFile()
    }

    void setBinaryCacheDir(Object binaryCacheDir) {
        this.binaryCacheDir = binaryCacheDir;
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

    void setEnvironments(Object environments) {
        this.environments = environments
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

    void setDotBinary(Object dotBinary) {
        this.dotBinary = dotBinary
    }

    @SkipWhenEmpty
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File getSourceDirectory() {
        project.file(sourceDirectory).getCanonicalFile()
    }

    void setSourceDirectory(Object sourceDirectory) {
        this.sourceDirectory = sourceDirectory
    }

    @OutputDirectory
    File getOutputDirectory() {
        project.file(outputDirectory).getCanonicalFile()
    }

    void setOutputDirectory(Object outputDirectory) {
        this.outputDirectory = outputDirectory
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File getDoctreeCacheDirectory() {
        project.file(doctreeCacheDirectory).getCanonicalFile()
    }

    void setDoctreeCacheDirectory(Object doctreeCacheDirectory) {
        this.doctreeCacheDirectory = doctreeCacheDirectory
    }

    @Input
    String getBuilder() {
        return String.valueOf(unwrap(builder))
    }

    void setBuilder(Object builder) {
        this.builder = builder
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

    void setTags(Object tags) {
        this.tags = tags
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

    void setVerbose(Object verbose) {
        this.verbose = verbose
    }

    @Input
    boolean isTraceback() {
        (traceback instanceof Boolean ? traceback : verbose()).asBoolean()
    }

    void setTraceback(Object traceback) {
        this.traceback = traceback
    }

    @Input
    boolean isForce() {
        (force instanceof Boolean ? force : force()).asBoolean()
    }

    void setForce(Object force) {
        this.force = force
    }

    @Input
    boolean isWarningsAsErrors() {
        (warningsAsErrors instanceof Boolean ? warningsAsErrors : warningsAsErrors()).asBoolean()
    }

    void setWarningsAsErrors(Object warningsAsErrors) {
        this.warningsAsErrors = warningsAsErrors
    }

    @Input
    boolean isUseDoctreeCache() {
        (useDoctreeCache instanceof Boolean ? useDoctreeCache : useDoctreeCache()).asBoolean()
    }

    void setUseDoctreeCache(Object useDoctreeCache) {
        this.useDoctreeCache = useDoctreeCache
    }

    @Input
    boolean isSkip() {
        (skip instanceof Boolean ? skip : skip()).asBoolean()
    }

    void setSkip(Object skip) {
        this.skip = skip
    }

    @TaskAction
    def run() {
        if (isSkip()) {
            logger.info("Skipping Sphinx execution.")
            return
        }

        SphinxRunner runner = new SphinxRunner(
                getBinaryUrl(), getBinaryCacheDir(),
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

        if (isTraceback()) {
            args.add('-T')
        }

        if (isWarningsAsErrors()) {
            args.add('-W')
        }

        if (isForce()) {
            args.add('-a')
            args.add('-E')
        }

        if (isUseDoctreeCache()) {
            args.add('-d')
            args.add(getDoctreeCacheDirectory().getPath())
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
