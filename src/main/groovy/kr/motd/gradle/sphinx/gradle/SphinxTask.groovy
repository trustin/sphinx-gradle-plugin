package kr.motd.gradle.sphinx.gradle

import kr.motd.maven.sphinx.SphinxException
import kr.motd.maven.sphinx.SphinxRunner
import kr.motd.maven.sphinx.SphinxUtil
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.*

import java.util.concurrent.Callable

class SphinxTask extends DefaultTask {

    def binaryUrl = { SphinxRunner.DEFAULT_BINARY_URL }
    def binaryCacheDir = { "${project.gradle.gradleUserHomeDir}/caches/sphinx-binary" }
    def environments = { Collections.emptyMap() }
    private final def additionalEnvironments = new HashMap<String, String>()
    def dotBinary = null
    def sourceDirectory = {
        "${project.projectDir}${File.separator}src${File.separator}site${File.separator}sphinx"
    }
    def outputDirectory = { "${project.buildDir}${File.separator}site" }
    def doctreeCacheDirectory = { "${project.buildDir}${File.separator}site${File.separator}.doctrees" }
    def builder = { "html" }
    def tags = { Collections.emptyList() }
    private final def additionalTags = new ArrayList<String>()
    def verbose = { true }
    def traceback = { true }
    def force = { false }
    def warningsAsErrors = { false }
    def skip = { false }
    def useDoctreeCache = { false }
    def useMakeMode = { false }

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

    void binaryUrl(Object binaryUrl) {
        setBinaryUrl(binaryUrl);
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File getBinaryCacheDir() {
        def binaryCacheDir = project.file(this.binaryCacheDir)
        binaryCacheDir.mkdirs()
        return binaryCacheDir.getCanonicalFile()
    }

    void setBinaryCacheDir(Object binaryCacheDir) {
        this.binaryCacheDir = binaryCacheDir
    }

    void binaryCacheDir(Object binaryCacheDir) {
        setBinaryCacheDir(binaryCacheDir);
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

    void dotBinary(Object dotBinary) {
        setDotBinary(dotBinary);
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

    void sourceDirectory(Object sourceDirectory) {
        setSourceDirectory(sourceDirectory);
    }

    @OutputDirectory
    File getOutputDirectory() {
        project.file(outputDirectory).getCanonicalFile()
    }

    void setOutputDirectory(Object outputDirectory) {
        this.outputDirectory = outputDirectory
    }

    void outputDirectory(Object outputDirectory) {
        setOutputDirectory(outputDirectory);
    }

    @Optional
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File getDoctreeCacheDirectory() {
        if (getUseDoctreeCache()) {
            def doctreeCacheDirectory = project.file(this.doctreeCacheDirectory)
            doctreeCacheDirectory.mkdirs()
            return doctreeCacheDirectory.getCanonicalFile()
        } else {
            return null
        }
    }

    void setDoctreeCacheDirectory(Object doctreeCacheDirectory) {
        this.doctreeCacheDirectory = doctreeCacheDirectory
    }

    void doctreeCacheDirectory(Object doctreeCacheDirectory) {
        setDoctreeCacheDirectory(doctreeCacheDirectory);
    }

    @Input
    String getBuilder() {
        return String.valueOf(unwrap(builder))
    }

    void setBuilder(Object builder) {
        this.builder = builder
    }

    void builder(Object builder) {
        setBuilder(builder);
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
    boolean getVerbose() {
        (verbose instanceof Boolean ? verbose : verbose()).asBoolean()
    }

    void setVerbose(Object verbose) {
        this.verbose = verbose
    }

    void verbose(Object verbose) {
        this.verbose = verbose;
    }

    @Input
    boolean getTraceback() {
        (traceback instanceof Boolean ? traceback : verbose()).asBoolean()
    }

    void setTraceback(Object traceback) {
        this.traceback = traceback
    }

    void traceback(Object traceback) {
        setTraceback(traceback);
    }

    @Input
    boolean getForce() {
        (force instanceof Boolean ? force : force()).asBoolean()
    }

    void setForce(Object force) {
        this.force = force
    }

    void force(Object force) {
        setForce(force);
    }

    @Input
    boolean getWarningsAsErrors() {
        (warningsAsErrors instanceof Boolean ? warningsAsErrors : warningsAsErrors()).asBoolean()
    }

    void setWarningsAsErrors(Object warningsAsErrors) {
        this.warningsAsErrors = warningsAsErrors
    }

    void warningsAsErrors(Object warningsAsErrors) {
        setWarningsAsErrors(warningsAsErrors);
    }

    @Input
    boolean getUseDoctreeCache() {
        (useDoctreeCache instanceof Boolean ? useDoctreeCache : useDoctreeCache()).asBoolean()
    }

    void setUseDoctreeCache(Object useDoctreeCache) {
        this.useDoctreeCache = useDoctreeCache
    }

    void useDoctreeCache(Object useDoctreeCache) {
        setUseDoctreeCache(useDoctreeCache)
    }

    @Input
    boolean getUseMakeMode() {
        (useMakeMode instanceof Boolean ? useMakeMode : useMakeMode()).asBoolean()
    }

    void setUseMakeMode(Object useMakeMode) {
        this.useMakeMode = useMakeMode
    }

    void useMakeMode(Object useMakeMode) {
        setUseMakeMode(useMakeMode)
    }

    @Input
    boolean getSkip() {
        (skip instanceof Boolean ? skip : skip()).asBoolean()
    }

    void setSkip(Object skip) {
        this.skip = skip
    }

    void skip(Object skip) {
        setSkip(skip)
    }

    @TaskAction
    def run() {
        if (getSkip()) {
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

        args.add(getUseMakeMode() ? '-M' : '-b')
        args.add(getBuilder())

        if (getVerbose()) {
            args.add('-v')
        } else {
            args.add('-Q')
        }

        if (getTraceback()) {
            args.add('-T')
        }

        if (getWarningsAsErrors()) {
            args.add('-W')
        }

        if (getForce()) {
            args.add('-a')
            args.add('-E')
        }

        def doctreeCacheDirectory = getDoctreeCacheDirectory();
        if (doctreeCacheDirectory != null) {
            args.add('-d')
            args.add(doctreeCacheDirectory.getPath())
        }

        for (String tag : getTags()) {
            args.add("-t")
            args.add(tag)
        }

        args.add('-n')

        args.add(getSourceDirectory().getPath())
        args.add(getOutputDirectory().getPath())

        return args
    }
}
