package kr.motd.gradle.sphinx.gradle

import kr.motd.maven.sphinx.SphinxException
import kr.motd.maven.sphinx.SphinxRunner
import kr.motd.maven.sphinx.SphinxUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

class SphinxTask extends DefaultTask {

    def binaryBaseUrl = { SphinxRunner.DEFAULT_BINARY_BASE_URL }
    def binaryVersion = { SphinxRunner.DEFAULT_BINARY_VERSION }
    def binaryCacheDir = { "${project.gradle.gradleUserHomeDir}/caches/sphinx-binary" }
    def sourceDirectory = {
        "${project.projectDir}${File.separator}src${File.separator}site${File.separator}sphinx"
    }
    def outputDirectory = { "${project.buildDir}${File.separator}site" }
    def builder = { "html" }
    def tags = { Collections.emptyList() }
    def verbose = { true }
    def force = { false }
    def warningsAsErrors = { false }
    def skip = { false }

    @Input
    String getBinaryBaseUrl() {
        (binaryBaseUrl instanceof CharSequence ? binaryBaseUrl : binaryBaseUrl()).toString()
    }

    @Input
    String getBinaryVersion() {
        (binaryVersion instanceof CharSequence ? binaryVersion : binaryVersion()).toString()
    }

    @Input
    File getBinaryCacheDir() {
        if (binaryCacheDir instanceof File) {
            return binaryCacheDir.getCanonicalFile()
        }
        if (binaryCacheDir instanceof CharSequence) {
            return project.file(binaryCacheDir).getCanonicalFile()
        }

        return project.file(binaryCacheDir()).getCanonicalFile()
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
        (builder instanceof CharSequence ? builder : builder()).toString()
    }

    @Input
    List<String> getTags() {
        (tags instanceof Iterable ? tags : tags()).toList()
    }

    void tags(String... tags) {
        this.tags = Arrays.asList(tags)
    }

    void tags(Iterable<String> tags) {
        this.tags = tags
    }

    void tags(Closure tags) {
        this.tags = tags
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
        SphinxRunner runner = new SphinxRunner(getBinaryBaseUrl(), getBinaryVersion(), getBinaryCacheDir(),
                                               { logger.lifecycle(it) });
        int result = runner.run(getSourceDirectory().getCanonicalFile(),
                                getSphinxRunnerCmdLine())
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
            args.add('-v');
        } else {
            args.add('-Q');
        }

        if (isWarningsAsErrors()) {
            args.add('-W');
        }

        if (isForce()) {
            args.add('-a');
            args.add('-E');
        }

        for (String tag : getTags()) {
            args.add("-t");
            args.add(tag);
        }

        args.add('-n');

        args.add('-b');
        args.add(getBuilder());

        args.add(getSourceDirectory().getCanonicalPath());
        args.add(getOutputDirectory().getCanonicalPath());

        return args;
    }
}
