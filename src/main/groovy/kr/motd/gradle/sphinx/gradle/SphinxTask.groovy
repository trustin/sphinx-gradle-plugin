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

    def sourceDirectory = { "${project.projectDir}${File.separator}src${File.separator}sphinx" }
    def outputDirectory = { "${project.buildDir}${File.separator}site" }
    def sphinxSourceDirectory = { "${project.buildDir}${File.separator}sphinx" }
    def builder = { "html" }
    def tags = { Collections.emptyList() }
    def verbose = { true }
    def force = { false }
    def warningsAsErrors = { false }

    @SkipWhenEmpty
    @InputDirectory
    File getSourceDirectory() {
        project.file(sourceDirectory)
    }

    @OutputDirectory
    File getOutputDirectory() {
        project.file(outputDirectory)
    }

    @OutputDirectory
    File getSphinxSourceDirectory() {
        project.file(sphinxSourceDirectory)
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

    void tags(Object tags) {
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

    @TaskAction
    def run() {
        SphinxRunner runner = new SphinxRunner(getSphinxSourceDirectory()) {
            @Override
            protected void log(String s) {
                logger.debug(s)
            }
        }

        try {
            int result = runner.runSphinx(getSphinxRunnerCmdLine());
            if (result != 0) {
                throw new SphinxException("Sphinx exited with non-zero code: ${result}")
            }
            SphinxUtil.convertLineSeparators(getOutputDirectory())
        } finally {
            runner.destroy()
        }
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

        args.add(getSourceDirectory().getPath());
        args.add(getOutputDirectory().getPath());

        return args;
    }
}
